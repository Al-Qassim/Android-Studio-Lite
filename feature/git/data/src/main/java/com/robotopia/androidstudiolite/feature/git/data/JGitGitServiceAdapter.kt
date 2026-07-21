package com.robotopia.androidstudiolite.feature.git.data

import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.git.api.CloneUrlValidation
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import com.robotopia.androidstudiolite.feature.git.api.GitBranchKind
import com.robotopia.androidstudiolite.feature.git.api.GitBranchesSnapshot
import com.robotopia.androidstudiolite.feature.git.api.GitCheckoutConflictException
import com.robotopia.androidstudiolite.feature.git.api.GitCommitFileInfo
import com.robotopia.androidstudiolite.feature.git.api.GitCommitInfo
import com.robotopia.androidstudiolite.feature.git.api.GitCredentials
import com.robotopia.androidstudiolite.feature.git.api.GitDiffLineInfo
import com.robotopia.androidstudiolite.feature.git.api.GitDiffLineType
import com.robotopia.androidstudiolite.feature.git.api.GitFileChangeKind
import com.robotopia.androidstudiolite.feature.git.api.GitMergeResult
import com.robotopia.androidstudiolite.feature.git.api.GitRepositoryInfo
import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.git.api.GitStatusSnapshot
import java.io.ByteArrayOutputStream
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.MergeResult
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.api.errors.CheckoutConflictException
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.BranchTrackingStatus
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryState
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.EmptyTreeIterator
import org.eclipse.jgit.treewalk.FileTreeIterator
import org.eclipse.jgit.treewalk.filter.PathFilter

/**
 * JGit-backed [GitService]. Serializes ops per project root; HTTPS credentials stay in-process.
 */
class JGitGitServiceAdapter : GitService {
    private val locks = mutableMapOf<String, Mutex>()
    private val locksGuard = Mutex()

    override suspend fun isRepository(projectRoot: File): Boolean = withContext(Dispatchers.IO) {
        File(projectRoot, ".git").exists()
    }

    override suspend fun init(projectRoot: File) = withRepoLock(projectRoot) {
        projectRoot.mkdirs()
        Git.init().setDirectory(projectRoot).call().use { }
    }

    override suspend fun status(projectRoot: File): GitStatusSnapshot = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            val status = git.status().call()
            GitStatusSnapshot(
                branch = git.repository.branch,
                isClean = status.isClean,
                added = status.added,
                changed = status.changed,
                removed = status.removed,
                modified = status.modified,
                untracked = status.untracked,
                conflicting = status.conflicting,
                missing = status.missing,
            )
        }
    }

    override suspend fun repositoryInfo(projectRoot: File): GitRepositoryInfo =
        withRepoLock(projectRoot) {
            open(projectRoot).use { git ->
                val repo = git.repository
                val remotes = git.remoteList().call()
                val hasRemote = remotes.isNotEmpty()
                val originUrl = remotes.firstOrNull { it.name == "origin" }?.urIs?.firstOrNull()
                    ?.toPrivateASCIIString()
                    ?: remotes.firstOrNull()?.urIs?.firstOrNull()?.toPrivateASCIIString()
                val tracking = runCatching {
                    BranchTrackingStatus.of(repo, repo.branch)
                }.getOrNull()
                GitRepositoryInfo(
                    hasRemote = hasRemote,
                    remoteHtmlUrl = originUrl?.let { githubHtmlUrl(it) },
                    aheadCount = tracking?.aheadCount ?: 0,
                    behindCount = tracking?.behindCount ?: 0,
                    isMerging = repo.repositoryState.isMergeInProgress(),
                )
            }
        }

    override suspend fun stageAll(projectRoot: File) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            git.add().addFilepattern(".").call()
            git.add().setUpdate(true).addFilepattern(".").call()
        }
        Unit
    }

    override suspend fun stagePaths(projectRoot: File, paths: List<String>) =
        withRepoLock(projectRoot) {
            if (paths.isEmpty()) return@withRepoLock Unit
            open(projectRoot).use { git ->
                val add = git.add()
                val update = git.add().setUpdate(true)
                for (path in paths.map { requireRelativePath(it) }) {
                    add.addFilepattern(path)
                    update.addFilepattern(path)
                }
                add.call()
                update.call()
            }
            Unit
        }

    override suspend fun unstagePaths(projectRoot: File, paths: List<String>) =
        withRepoLock(projectRoot) {
            if (paths.isEmpty()) return@withRepoLock Unit
            open(projectRoot).use { git ->
                try {
                    // Path reset must not set MIXED/SOFT/HARD — JGit rejects that combination.
                    val reset = git.reset()
                    for (path in paths.map { requireRelativePath(it) }) {
                        reset.addPath(path)
                    }
                    reset.call()
                } catch (e: GitAPIException) {
                    throw AppException("Couldn't unstage those files.", e)
                } catch (e: Exception) {
                    throw AppException("Couldn't unstage those files.", e)
                }
            }
            Unit
        }

    override suspend fun discardPaths(projectRoot: File, paths: List<String>) =
        withRepoLock(projectRoot) {
            if (paths.isEmpty()) return@withRepoLock
            open(projectRoot).use { git ->
                val status = git.status().call()
                val untracked = status.untracked
                val trackedPaths = mutableListOf<String>()
                for (raw in paths) {
                    val path = requireRelativePath(raw)
                    val file = File(projectRoot, path)
                    if (path in untracked || (!fileExistsInIndex(git.repository, path) && file.exists())) {
                        if (file.isDirectory) file.deleteRecursively() else file.delete()
                    } else {
                        trackedPaths.add(path)
                    }
                }
                if (trackedPaths.isNotEmpty()) {
                    try {
                        val checkout = git.checkout().setStartPoint(Constants.HEAD)
                        for (path in trackedPaths) checkout.addPath(path)
                        checkout.call()
                    } catch (e: GitAPIException) {
                        throw AppException("Couldn't discard those changes.", e)
                    }
                }
            }
        }

    override suspend fun discardAll(projectRoot: File) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            try {
                git.reset().setMode(ResetCommand.ResetType.HARD).call()
                git.clean().setCleanDirectories(true).setForce(true).call()
            } catch (e: GitAPIException) {
                throw AppException("Couldn't discard local changes.", e)
            }
        }
        Unit
    }

    override suspend fun undoLastCommit(projectRoot: File) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            val head = git.repository.resolve(Constants.HEAD)
                ?: throw AppException("Nothing to undo.")
            RevWalk(git.repository).use { walk ->
                val commit = walk.parseCommit(head)
                if (commit.parentCount == 0) {
                    throw AppException("Can't undo the only commit.")
                }
            }
            try {
                git.reset()
                    .setMode(ResetCommand.ResetType.SOFT)
                    .setRef("HEAD~1")
                    .call()
            } catch (e: GitAPIException) {
                throw AppException("Couldn't undo the last commit.", e)
            }
        }
        Unit
    }

    override suspend fun commit(
        projectRoot: File,
        message: String,
        authorName: String,
        authorEmail: String,
    ): String = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            val author = PersonIdent(authorName, authorEmail)
            val rev = git.commit()
                .setMessage(message)
                .setAuthor(author)
                .setCommitter(author)
                .call()
            rev.name
        }
    }

    override suspend fun currentBranch(projectRoot: File): String = withRepoLock(projectRoot) {
        open(projectRoot).use { it.repository.branch }
    }

    override suspend fun createBranch(
        projectRoot: File,
        name: String,
        startPoint: String?,
    ) = withRepoLock(projectRoot) {
        val trimmed = requireBranchName(name)
        open(projectRoot).use { git ->
            try {
                val command = git.branchCreate().setName(trimmed)
                val start = startPoint?.trim().orEmpty()
                if (start.isNotEmpty()) {
                    command.setStartPoint(start)
                }
                command.call()
            } catch (e: GitAPIException) {
                throw AppException("Couldn't create that branch.", e)
            }
        }
        Unit
    }

    override suspend fun deleteBranch(projectRoot: File, name: String) = withRepoLock(projectRoot) {
        val trimmed = requireBranchName(name)
        open(projectRoot).use { git ->
            if (git.repository.branch == trimmed) {
                throw AppException("Switch to another branch before deleting this one.")
            }
            try {
                git.branchDelete()
                    .setBranchNames(trimmed)
                    .setForce(true)
                    .call()
            } catch (e: GitAPIException) {
                throw AppException("Couldn't delete that branch.", e)
            }
        }
        Unit
    }

    override suspend fun checkout(
        projectRoot: File,
        name: String,
        force: Boolean,
    ) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            val repo = git.repository
            val remoteTracking = repo.findRef("refs/remotes/$name")
            val localCheckedOut = if (remoteTracking != null) {
                checkoutRemoteTracking(git, name, force)
            } else {
                try {
                    // JGit: setForced == git checkout --force. setForce() only forces ref updates.
                    git.checkout().setName(name).setForced(force).call()
                } catch (e: CheckoutConflictException) {
                    throw GitCheckoutConflictException(
                        conflictingPaths = e.conflictingPaths.orEmpty().sorted(),
                        cause = e,
                    )
                } catch (e: GitAPIException) {
                    throw AppException("Couldn't check out that branch.", e)
                }
                name
            }
            RecentBranchesStore.record(repo.directory, localCheckedOut)
        }
        Unit
    }

    override suspend fun listBranches(projectRoot: File): GitBranchesSnapshot =
        withRepoLock(projectRoot) {
            open(projectRoot).use { git ->
                val repo = git.repository
                val current = repo.branch
                val localNames = git.branchList()
                    .call()
                    .map { it.name.removePrefix("refs/heads/") }
                    .distinct()
                    .sorted()
                val remoteNames = git.branchList()
                    .setListMode(ListBranchCommand.ListMode.REMOTE)
                    .call()
                    .map { it.name.removePrefix("refs/remotes/") }
                    .filter { !it.endsWith("/HEAD") && !it.endsWith("HEAD") }
                    .distinct()
                    .sorted()

                val local = localNames.map { name ->
                    GitBranch(
                        name = name,
                        kind = GitBranchKind.Local,
                        isCurrent = name == current,
                    )
                }
                val remote = remoteNames.map { name ->
                    GitBranch(name = name, kind = GitBranchKind.Remote)
                }
                val recent = buildRecent(repo.directory, current, localNames.toSet())
                GitBranchesSnapshot(
                    currentBranch = current,
                    recent = recent,
                    local = local,
                    remote = remote,
                )
            }
        }

    override suspend fun clone(
        httpsUrl: String,
        destDir: File,
        credentials: GitCredentials?,
    ) = withContext(Dispatchers.IO) {
        if (destDir.exists() && destDir.list()?.isNotEmpty() == true) {
            throw AppException("Clone destination must be empty.")
        }
        destDir.mkdirs()
        try {
            val command = Git.cloneRepository()
                .setURI(httpsUrl)
                .setDirectory(destDir)
            credentials?.let { command.setCredentialsProvider(it.toProvider()) }
            command.call().use { }
        } catch (e: AppException) {
            throw e
        } catch (e: GitAPIException) {
            destDir.deleteRecursively()
            throw AppException("Couldn't clone that repository.", e)
        } catch (e: Exception) {
            destDir.deleteRecursively()
            throw AppException("Couldn't clone that repository.", e)
        }
    }

    override suspend fun fetch(
        projectRoot: File,
        credentials: GitCredentials?,
    ) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            try {
                val command = git.fetch().setCheckFetchedObjects(true)
                credentials?.let { command.setCredentialsProvider(it.toProvider()) }
                command.call()
            } catch (e: GitAPIException) {
                throw AppException("Couldn't fetch from the remote.", e)
            } catch (e: Exception) {
                throw AppException("Couldn't fetch from the remote.", e)
            }
        }
        Unit
    }

    override suspend fun pull(
        projectRoot: File,
        credentials: GitCredentials?,
    ) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            try {
                val command = git.pull()
                credentials?.let { command.setCredentialsProvider(it.toProvider()) }
                command.call()
            } catch (e: GitAPIException) {
                throw AppException("Couldn't pull from the remote.", e)
            }
        }
        Unit
    }

    override suspend fun push(
        projectRoot: File,
        credentials: GitCredentials?,
    ) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            try {
                val command = git.push()
                credentials?.let { command.setCredentialsProvider(it.toProvider()) }
                command.call()
            } catch (e: GitAPIException) {
                throw AppException("Couldn't push to the remote.", e)
            }
        }
        Unit
    }

    override suspend fun pushSetUpstream(
        projectRoot: File,
        remote: String,
        branch: String,
        credentials: GitCredentials?,
    ) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            try {
                val command = git.push()
                    .setRemote(remote)
                    .setRefSpecs(RefSpec("refs/heads/$branch:refs/heads/$branch"))
                credentials?.let { command.setCredentialsProvider(it.toProvider()) }
                command.call()
                val config = git.repository.config
                config.setString("branch", branch, "remote", remote)
                config.setString("branch", branch, "merge", "refs/heads/$branch")
                config.save()
            } catch (e: GitAPIException) {
                throw AppException("Couldn't publish that branch.", e)
            }
        }
        Unit
    }

    override suspend fun addRemote(
        projectRoot: File,
        name: String,
        httpsUrl: String,
    ) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            try {
                git.remoteAdd()
                    .setName(name)
                    .setUri(URIish(httpsUrl))
                    .call()
            } catch (e: Exception) {
                throw AppException("Couldn't add that remote.", e)
            }
        }
        Unit
    }

    override suspend fun merge(projectRoot: File, branchName: String): GitMergeResult =
        withRepoLock(projectRoot) {
            open(projectRoot).use { git ->
                val ref = git.repository.findRef(branchName)
                    ?: git.repository.findRef("refs/heads/$branchName")
                    ?: git.repository.findRef("refs/remotes/$branchName")
                    ?: throw AppException("Couldn't find that branch.")
                val result = try {
                    git.merge().include(ref).call()
                } catch (e: GitAPIException) {
                    throw AppException("Couldn't merge that branch.", e)
                }
                when (result.mergeStatus) {
                    MergeResult.MergeStatus.CONFLICTING -> GitMergeResult(
                        conflicts = true,
                        conflictingPaths = result.conflicts?.keys.orEmpty(),
                    )
                    MergeResult.MergeStatus.FAILED,
                    MergeResult.MergeStatus.ABORTED,
                    MergeResult.MergeStatus.NOT_SUPPORTED,
                    -> throw AppException("Couldn't merge that branch.")
                    else -> GitMergeResult(conflicts = false)
                }
            }
        }

    override suspend fun abortMerge(projectRoot: File) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            if (!git.repository.repositoryState.isMergeInProgress()) {
                throw AppException("No merge in progress.")
            }
            try {
                // JGit's MERGE reset mode is unsupported on some builds; HARD clears MERGE_HEAD.
                git.reset().setMode(ResetCommand.ResetType.HARD).call()
            } catch (e: GitAPIException) {
                throw AppException("Couldn't abort the merge.", e)
            }
        }
        Unit
    }

    override suspend fun renameBranch(
        projectRoot: File,
        oldName: String,
        newName: String,
        credentials: GitCredentials?,
    ) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            val remoteTracking = git.repository.findRef("refs/remotes/$oldName")
            if (remoteTracking != null) {
                val remoteShort = requireBranchName(newName.substringAfterLast('/'))
                renameRemoteBranch(git, oldName, remoteShort, credentials)
            } else {
                val trimmedNew = requireBranchName(newName)
                try {
                    git.branchRename()
                        .setOldName(oldName)
                        .setNewName(trimmedNew)
                        .call()
                } catch (e: GitAPIException) {
                    throw AppException("Couldn't rename that branch.", e)
                }
            }
        }
        Unit
    }

    override suspend fun readWorkingFile(projectRoot: File, relativePath: String): String =
        withRepoLock(projectRoot) {
            val path = requireRelativePath(relativePath)
            val file = File(projectRoot, path)
            if (!file.isFile) {
                throw AppException("Couldn't open that file.")
            }
            file.readText()
        }

    override suspend fun writeWorkingFile(
        projectRoot: File,
        relativePath: String,
        content: String,
    ) = withRepoLock(projectRoot) {
        val path = requireRelativePath(relativePath)
        val file = File(projectRoot, path)
        file.parentFile?.mkdirs()
        file.writeText(content)
    }

    override suspend fun appendGitignore(projectRoot: File, pattern: String) =
        withRepoLock(projectRoot) {
            val trimmed = pattern.trim()
            if (trimmed.isEmpty()) {
                throw AppException("Enter a path to ignore.")
            }
            val file = File(projectRoot, ".gitignore")
            val existing = if (file.isFile) file.readText() else ""
            if (existing.lineSequence().any { it.trim() == trimmed }) {
                return@withRepoLock
            }
            val prefix = when {
                existing.isEmpty() -> ""
                existing.endsWith("\n") -> ""
                else -> "\n"
            }
            file.appendText("$prefix$trimmed\n")
        }

    override suspend fun diffWorkingTree(
        projectRoot: File,
        relativePath: String,
    ): List<GitDiffLineInfo> = withRepoLock(projectRoot) {
        val path = requireRelativePath(relativePath)
        open(projectRoot).use { git ->
            try {
                val repo = git.repository
                repo.newObjectReader().use { reader ->
                    val oldTree = when (val treeId = repo.resolve("HEAD^{tree}")) {
                        null -> EmptyTreeIterator()
                        else -> CanonicalTreeParser().also { it.reset(reader, treeId) }
                    }
                    // Scan + format must share one DiffFormatter so working-tree blobs resolve.
                    formatTreeDiff(repo, oldTree, FileTreeIterator(repo), path)
                }
            } catch (e: AppException) {
                throw e
            } catch (e: Exception) {
                throw AppException("Couldn't open that diff.", e)
            }
        }
    }

    override suspend fun diffCommit(
        projectRoot: File,
        commitId: String,
        relativePath: String,
    ): List<GitDiffLineInfo> = withRepoLock(projectRoot) {
        val path = requireRelativePath(relativePath)
        open(projectRoot).use { git ->
            try {
                val repo = git.repository
                repo.newObjectReader().use { reader ->
                    RevWalk(repo).use { walk ->
                        val commit = walk.parseCommit(
                            repo.resolve(commitId)
                                ?: throw AppException("Couldn't find that commit."),
                        )
                        val oldTree = if (commit.parentCount > 0) {
                            val parent = walk.parseCommit(commit.getParent(0))
                            CanonicalTreeParser().also { it.reset(reader, parent.tree) }
                        } else {
                            EmptyTreeIterator()
                        }
                        val newTree = CanonicalTreeParser().also {
                            it.reset(reader, commit.tree)
                        }
                        formatTreeDiff(repo, oldTree, newTree, path)
                    }
                }
            } catch (e: AppException) {
                throw e
            } catch (e: Exception) {
                throw AppException("Couldn't open that diff.", e)
            }
        }
    }

    override suspend fun log(projectRoot: File, maxCount: Int): List<GitCommitInfo> =
        withRepoLock(projectRoot) {
            open(projectRoot).use { git ->
                val head = git.repository.resolve(Constants.HEAD) ?: return@use emptyList()
                git.log().add(head).setMaxCount(maxCount.coerceAtLeast(1)).call().map { rev ->
                    GitCommitInfo(
                        id = rev.name,
                        shortId = rev.abbreviate(7).name(),
                        subject = rev.shortMessage,
                        authorName = rev.authorIdent.name,
                        authoredAtEpochMs = rev.authorIdent.`when`.time,
                    )
                }
            }
        }

    override suspend fun commitFiles(
        projectRoot: File,
        commitId: String,
    ): List<GitCommitFileInfo> = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            val repo = git.repository
            RevWalk(repo).use { walk ->
                val commit = walk.parseCommit(repo.resolve(commitId)
                    ?: throw AppException("Couldn't find that commit."))
                val parentTree = if (commit.parentCount > 0) {
                    walk.parseCommit(commit.getParent(0)).tree
                } else {
                    null
                }
                DiffFormatter(ByteArrayOutputStream()).use { formatter ->
                    formatter.setRepository(repo)
                    val oldTree = if (parentTree != null) {
                        CanonicalTreeParser().also {
                            it.reset(repo.newObjectReader(), parentTree)
                        }
                    } else {
                        EmptyTreeIterator()
                    }
                    val newTree = CanonicalTreeParser().also {
                        it.reset(repo.newObjectReader(), commit.tree)
                    }
                    formatter.scan(oldTree, newTree).mapNotNull { entry ->
                        val path = when (entry.changeType) {
                            DiffEntry.ChangeType.DELETE -> entry.oldPath
                            else -> entry.newPath
                        }
                        if (path == null || path == DiffEntry.DEV_NULL) return@mapNotNull null
                        val kind = when (entry.changeType) {
                            DiffEntry.ChangeType.ADD -> GitFileChangeKind.Added
                            DiffEntry.ChangeType.DELETE -> GitFileChangeKind.Deleted
                            else -> GitFileChangeKind.Modified
                        }
                        GitCommitFileInfo(path = path, kind = kind)
                    }
                }
            }
        }
    }

    override fun validateCloneUrl(input: String): CloneUrlValidation =
        CloneUrlParser.validate(input)

    private fun requireBranchName(name: String): String {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            throw AppException("Enter a branch name.")
        }
        if (trimmed.contains(' ') || trimmed.contains('\\')) {
            throw AppException("Branch names can't contain spaces or backslashes.")
        }
        return trimmed
    }

    private fun requireRelativePath(path: String): String {
        val trimmed = path.trim().replace('\\', '/')
        if (trimmed.isEmpty() || trimmed.startsWith("/") || trimmed.contains("..")) {
            throw AppException("Invalid file path.")
        }
        return trimmed
    }

    private fun fileExistsInIndex(repo: Repository, path: String): Boolean {
        val head = repo.resolve(Constants.HEAD) ?: return false
        return runCatching {
            RevWalk(repo).use { walk ->
                val tree = walk.parseCommit(head).tree
                repo.newObjectReader().use { reader ->
                    CanonicalTreeParser().also { it.reset(reader, tree) }
                    // Presence check via resolve of blob id
                    repo.resolve("$head:$path") != null
                }
            }
        }.getOrDefault(false)
    }

    /**
     * Formats a path-filtered tree diff. Caller must keep [oldTree]/[newTree] readers alive
     * for working-tree iterators (one shared [DiffFormatter] for scan + format).
     */
    private fun formatTreeDiff(
        repo: Repository,
        oldTree: AbstractTreeIterator,
        newTree: AbstractTreeIterator,
        path: String,
    ): List<GitDiffLineInfo> {
        val out = ByteArrayOutputStream()
        DiffFormatter(out).use { formatter ->
            formatter.setRepository(repo)
            formatter.setPathFilter(PathFilter.create(path))
            val entries = formatter.scan(oldTree, newTree)
            for (entry in entries) {
                formatter.format(entry)
            }
        }
        return parseUnifiedDiff(out.toString(Charsets.UTF_8.name()))
    }

    private fun RepositoryState.isMergeInProgress(): Boolean =
        this == RepositoryState.MERGING || this == RepositoryState.MERGING_RESOLVED

    private fun parseUnifiedDiff(raw: String): List<GitDiffLineInfo> {
        val lines = mutableListOf<GitDiffLineInfo>()
        var oldLine = 0
        var newLine = 0
        for (line in raw.lineSequence()) {
            when {
                line.startsWith("@@") -> {
                    val match = HUNK_HEADER.find(line) ?: continue
                    oldLine = match.groupValues[1].toInt()
                    newLine = match.groupValues[2].toInt()
                }
                line.startsWith("+++") || line.startsWith("---") ||
                    line.startsWith("diff ") || line.startsWith("index ") ||
                    line.startsWith("new file") || line.startsWith("deleted file") -> Unit
                line.startsWith("+") -> {
                    lines.add(
                        GitDiffLineInfo(
                            type = GitDiffLineType.Add,
                            text = line.removePrefix("+"),
                            oldLine = null,
                            newLine = newLine,
                        ),
                    )
                    newLine++
                }
                line.startsWith("-") -> {
                    lines.add(
                        GitDiffLineInfo(
                            type = GitDiffLineType.Remove,
                            text = line.removePrefix("-"),
                            oldLine = oldLine,
                            newLine = null,
                        ),
                    )
                    oldLine++
                }
                line.startsWith("\\") -> Unit
                line.startsWith(" ") || line.isEmpty() -> {
                    val text = if (line.startsWith(" ")) line.drop(1) else line
                    lines.add(
                        GitDiffLineInfo(
                            type = GitDiffLineType.Context,
                            text = text,
                            oldLine = oldLine,
                            newLine = newLine,
                        ),
                    )
                    oldLine++
                    newLine++
                }
            }
        }
        return lines
    }

    private fun renameRemoteBranch(
        git: Git,
        oldRemoteName: String,
        newShortName: String,
        credentials: GitCredentials?,
    ) {
        val slash = oldRemoteName.indexOf('/')
        if (slash <= 0 || slash == oldRemoteName.lastIndex) {
            throw AppException("Couldn't rename that remote branch.")
        }
        val remote = oldRemoteName.substring(0, slash)
        val oldShort = oldRemoteName.substring(slash + 1)
        if (oldShort == newShortName) return
        val tracking = git.repository.findRef("refs/remotes/$oldRemoteName")
            ?: throw AppException("Couldn't find that remote branch.")
        val objectId = tracking.objectId.name
        try {
            val create = git.push()
                .setRemote(remote)
                .setRefSpecs(RefSpec("$objectId:refs/heads/$newShortName"))
            credentials?.let { create.setCredentialsProvider(it.toProvider()) }
            create.call()

            val delete = git.push()
                .setRemote(remote)
                .setRefSpecs(RefSpec(":refs/heads/$oldShort"))
            credentials?.let { delete.setCredentialsProvider(it.toProvider()) }
            delete.call()

            val fetch = git.fetch()
                .setRemote(remote)
                .setRemoveDeletedRefs(true)
            credentials?.let { fetch.setCredentialsProvider(it.toProvider()) }
            fetch.call()
        } catch (e: GitAPIException) {
            throw AppException("Couldn't rename that remote branch.", e)
        }
    }

    private fun checkoutRemoteTracking(git: Git, remoteName: String, force: Boolean): String {
        val localName = remoteName.substringAfter('/')
        if (localName.isEmpty() || localName == remoteName) {
            throw AppException("Couldn't check out that remote branch.")
        }
        val repo = git.repository
        val localExists = repo.findRef("refs/heads/$localName") != null
        try {
            val command = git.checkout()
                .setName(localName)
                .setStartPoint(remoteName)
                .setForced(force)
            if (!localExists) {
                command.setCreateBranch(true)
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
            }
            command.call()
        } catch (e: CheckoutConflictException) {
            throw GitCheckoutConflictException(
                conflictingPaths = e.conflictingPaths.orEmpty().sorted(),
                cause = e,
            )
        } catch (e: GitAPIException) {
            throw AppException("Couldn't check out that remote branch.", e)
        }
        return localName
    }

    private fun buildRecent(
        gitDir: File,
        current: String,
        localNames: Set<String>,
    ): List<GitBranch> {
        val ordered = buildList {
            if (current in localNames) add(current)
            for (name in RecentBranchesStore.read(gitDir)) {
                if (name != current && name in localNames) add(name)
            }
        }.take(RECENT_MAX)
        return ordered.map { name ->
            GitBranch(
                name = name,
                kind = GitBranchKind.Local,
                isCurrent = name == current,
            )
        }
    }

    private fun open(projectRoot: File): Git {
        val gitDir = File(projectRoot, ".git")
        if (!gitDir.exists()) {
            throw AppException("Not a git repository.")
        }
        val repository = FileRepositoryBuilder()
            .setGitDir(gitDir)
            .readEnvironment()
            .findGitDir(projectRoot)
            .build()
        return Git(repository)
    }

    private suspend fun <T> withRepoLock(projectRoot: File, block: () -> T): T {
        val key = projectRoot.canonicalFile.absolutePath
        val mutex = locksGuard.withLock {
            locks.getOrPut(key) { Mutex() }
        }
        return mutex.withLock {
            withContext(Dispatchers.IO) { block() }
        }
    }

    private fun GitCredentials.toProvider() =
        UsernamePasswordCredentialsProvider(username, passwordOrToken)

    private companion object {
        const val RECENT_MAX = 3
        val HUNK_HEADER = Regex("""@@ -(\d+)(?:,\d+)? \+(\d+)(?:,\d+)? @@""")

        fun githubHtmlUrl(remoteUrl: String): String? {
            val trimmed = remoteUrl.trim().removeSuffix(".git")
            val https = Regex("""https?://github\.com/([^/]+)/([^/]+)""")
                .matchEntire(trimmed)
            if (https != null) {
                return "https://github.com/${https.groupValues[1]}/${https.groupValues[2]}"
            }
            val ssh = Regex("""git@github\.com:([^/]+)/([^/]+)""")
                .matchEntire(trimmed)
            if (ssh != null) {
                return "https://github.com/${ssh.groupValues[1]}/${ssh.groupValues[2]}"
            }
            return null
        }
    }
}
