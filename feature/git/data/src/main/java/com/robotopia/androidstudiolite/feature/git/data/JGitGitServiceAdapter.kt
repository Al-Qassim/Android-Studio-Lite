package com.robotopia.androidstudiolite.feature.git.data

import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.git.api.CloneUrlValidation
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import com.robotopia.androidstudiolite.feature.git.api.GitBranchKind
import com.robotopia.androidstudiolite.feature.git.api.GitBranchesSnapshot
import com.robotopia.androidstudiolite.feature.git.api.GitCheckoutConflictException
import com.robotopia.androidstudiolite.feature.git.api.GitCredentials
import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.git.api.GitStatusSnapshot
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.MergeResult
import org.eclipse.jgit.api.errors.CheckoutConflictException
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

/**
 * JGit-backed [GitService] proof of concept.
 * Serializes ops per project root; uses HTTPS credentials in-process only.
 */
class JGitGitServiceAdapter : GitService {
    private val locks = mutableMapOf<String, Mutex>()
    private val locksGuard = Mutex()

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
                modified = status.modified,
                untracked = status.untracked,
                conflicting = status.conflicting,
                missing = status.missing,
            )
        }
    }

    override suspend fun stageAll(projectRoot: File) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            git.add().addFilepattern(".").call()
            // Include deletions in the index for a complete "stage all".
            git.add().setUpdate(true).addFilepattern(".").call()
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
                    git.checkout().setName(name).setForce(force).call()
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
                val command = git.fetch()
                    .setCheckFetchedObjects(true)
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
            val command = git.pull()
            credentials?.let { command.setCredentialsProvider(it.toProvider()) }
            command.call()
        }
        Unit
    }

    override suspend fun push(
        projectRoot: File,
        credentials: GitCredentials?,
    ) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            val command = git.push()
            credentials?.let { command.setCredentialsProvider(it.toProvider()) }
            command.call()
        }
        Unit
    }

    override suspend fun merge(projectRoot: File, branchName: String) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            val ref = git.repository.findRef(branchName)
                ?: git.repository.findRef("refs/heads/$branchName")
                ?: git.repository.findRef("refs/remotes/$branchName")
                ?: throw AppException("Couldn't find that branch.")
            val result = git.merge().include(ref).call()
            when (result.mergeStatus) {
                MergeResult.MergeStatus.CONFLICTING ->
                    throw AppException("Merge has conflicts. Resolve them in the files, then commit.")
                MergeResult.MergeStatus.FAILED,
                MergeResult.MergeStatus.ABORTED,
                MergeResult.MergeStatus.NOT_SUPPORTED,
                -> throw AppException("Couldn't merge that branch.")
                else -> Unit
            }
        }
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
                // Remote UI may pass `origin/foo` or bare `foo`; remote heads are short names.
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

    override fun validateCloneUrl(input: String): CloneUrlValidation =
        CloneUrlParser.validate(input)

    /**
     * Renames [oldRemoteName] (`origin/feature`) to [newShortName] on the remote via push,
     * then fetches with prune so local remote-tracking refs match.
     */
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

    /**
     * @return local branch name that is now checked out
     */
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
                .setForce(force)
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

    private companion object {
        const val RECENT_MAX = 3
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
}
