package com.robotopia.androidstudiolite.feature.git.data

import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.git.api.GitCredentials
import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.git.api.GitStatusSnapshot
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
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

    override suspend fun createBranch(projectRoot: File, name: String) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            git.branchCreate().setName(name).call()
        }
        Unit
    }

    override suspend fun checkout(projectRoot: File, name: String) = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            git.checkout().setName(name).call()
        }
        Unit
    }

    override suspend fun listBranches(projectRoot: File): List<String> = withRepoLock(projectRoot) {
        open(projectRoot).use { git ->
            git.branchList()
                .setListMode(ListBranchCommand.ListMode.ALL)
                .call()
                .map { it.name.removePrefix("refs/heads/").removePrefix("refs/remotes/") }
                .distinct()
                .sorted()
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
            val command = git.fetch()
            credentials?.let { command.setCredentialsProvider(it.toProvider()) }
            command.call()
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
}
