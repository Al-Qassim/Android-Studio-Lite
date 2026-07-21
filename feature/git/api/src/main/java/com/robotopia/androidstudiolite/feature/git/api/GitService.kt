package com.robotopia.androidstudiolite.feature.git.api

import java.io.File

/**
 * Checkout refused because local changes would be overwritten.
 * Paths are relative to the project root. No stash — caller offers commit or discard.
 */
class GitCheckoutConflictException(
    val conflictingPaths: List<String>,
    cause: Throwable? = null,
) : Exception("Local changes would be overwritten.", cause)

/**
 * Local Git surface (JGit). Remotes use HTTPS; pass [GitCredentials] per network op —
 * never embed tokens in remote URLs.
 */
interface GitService {
    /** True when [projectRoot] contains a `.git` directory. */
    suspend fun isRepository(projectRoot: File): Boolean

    suspend fun init(projectRoot: File)

    suspend fun status(projectRoot: File): GitStatusSnapshot

    suspend fun repositoryInfo(projectRoot: File): GitRepositoryInfo

    suspend fun stageAll(projectRoot: File)

    suspend fun stagePaths(projectRoot: File, paths: List<String>)

    suspend fun unstagePaths(projectRoot: File, paths: List<String>)

    /** Discard working-tree changes for [paths] (tracked restore + delete untracked). */
    suspend fun discardPaths(projectRoot: File, paths: List<String>)

    /** Hard-reset to HEAD and remove untracked files. */
    suspend fun discardAll(projectRoot: File)

    /** Soft-reset tip commit (`HEAD~1`); keeps file contents as local changes. */
    suspend fun undoLastCommit(projectRoot: File)

    /** Returns the new commit SHA. */
    suspend fun commit(
        projectRoot: File,
        message: String,
        authorName: String,
        authorEmail: String,
    ): String

    suspend fun currentBranch(projectRoot: File): String

    /**
     * Creates a local branch named [name].
     * When [startPoint] is set (local or `origin/branch`), the new branch starts there.
     */
    suspend fun createBranch(
        projectRoot: File,
        name: String,
        startPoint: String? = null,
    )

    /** Deletes a local branch. Fails if [name] is the current branch. */
    suspend fun deleteBranch(projectRoot: File, name: String)

    /**
     * Checks out [name]. Local names are bare (`main`); remotes use `origin/branch`.
     * Checking out a remote creates/updates a matching local branch when needed.
     *
     * Uncommitted changes that don't conflict are carried over. When checkout would
     * overwrite local work, throws [GitCheckoutConflictException] unless [force] is true
     * (discards conflicting local changes — no stash).
     */
    suspend fun checkout(
        projectRoot: File,
        name: String,
        force: Boolean = false,
    )

    /** Local, remote-tracking, and recent (max 3, current first) branches. */
    suspend fun listBranches(projectRoot: File): GitBranchesSnapshot

    /**
     * Clones [httpsUrl] into [destDir] (must be empty or missing).
     * [credentials] required for private repos; optional for public.
     */
    suspend fun clone(
        httpsUrl: String,
        destDir: File,
        credentials: GitCredentials? = null,
    )

    suspend fun fetch(
        projectRoot: File,
        credentials: GitCredentials? = null,
    )

    suspend fun pull(
        projectRoot: File,
        credentials: GitCredentials? = null,
    )

    suspend fun push(
        projectRoot: File,
        credentials: GitCredentials? = null,
    )

    /**
     * Push [branch] to [remote] and set upstream tracking.
     */
    suspend fun pushSetUpstream(
        projectRoot: File,
        remote: String,
        branch: String,
        credentials: GitCredentials? = null,
    )

    suspend fun addRemote(
        projectRoot: File,
        name: String,
        httpsUrl: String,
    )

    /**
     * Merge [branchName] into the current branch.
     * On conflicts, leaves the merge in progress and returns [GitMergeResult.conflicts] true.
     */
    suspend fun merge(projectRoot: File, branchName: String): GitMergeResult

    suspend fun abortMerge(projectRoot: File)

    /**
     * Renames a branch. Local names are bare (`main`); remotes use `origin/branch`
     * and are renamed on the remote (push new + delete old). [credentials] required for remotes.
     */
    suspend fun renameBranch(
        projectRoot: File,
        oldName: String,
        newName: String,
        credentials: GitCredentials? = null,
    )

    suspend fun readWorkingFile(projectRoot: File, relativePath: String): String

    suspend fun writeWorkingFile(projectRoot: File, relativePath: String, content: String)

    /** Append [pattern] to `.gitignore` (creates the file when missing). */
    suspend fun appendGitignore(projectRoot: File, pattern: String)

    /** Working-tree vs HEAD (or index) unified diff for one path. */
    suspend fun diffWorkingTree(projectRoot: File, relativePath: String): List<GitDiffLineInfo>

    /** Diff of [commitId] against its first parent (or empty tree). */
    suspend fun diffCommit(
        projectRoot: File,
        commitId: String,
        relativePath: String,
    ): List<GitDiffLineInfo>

    suspend fun log(projectRoot: File, maxCount: Int = 50): List<GitCommitInfo>

    suspend fun commitFiles(projectRoot: File, commitId: String): List<GitCommitFileInfo>

    /** Normalizes a clone URL or `owner/repo` slug to an HTTPS GitHub URL. */
    fun validateCloneUrl(input: String): CloneUrlValidation
}

data class CloneUrlValidation(
    val normalizedHttpsUrl: String? = null,
    val errorMessage: String? = null,
) {
    val isValid: Boolean get() = normalizedHttpsUrl != null && errorMessage == null
}

data class GitCredentials(
    val username: String,
    val passwordOrToken: String,
)

enum class GitBranchKind {
    Local,
    Remote,
}

data class GitBranch(
    /** Local: `main`. Remote-tracking: `origin/main`. */
    val name: String,
    val kind: GitBranchKind,
    val isCurrent: Boolean = false,
)

data class GitBranchesSnapshot(
    val currentBranch: String,
    /** Up to 3 recently used local branches; [currentBranch] is first when present. */
    val recent: List<GitBranch>,
    val local: List<GitBranch>,
    val remote: List<GitBranch>,
)

data class GitStatusSnapshot(
    val branch: String,
    val isClean: Boolean,
    val added: Set<String>,
    val changed: Set<String>,
    val removed: Set<String>,
    val modified: Set<String>,
    val untracked: Set<String>,
    val conflicting: Set<String>,
    val missing: Set<String>,
)
