package com.robotopia.androidstudiolite.feature.git.api

import java.io.File

/**
 * POC local Git surface (JGit). Not a full IDE SCM product yet.
 * Remotes use HTTPS; pass [GitCredentials] per network op — never embed tokens in remote URLs.
 */
interface GitService {
    suspend fun init(projectRoot: File)

    suspend fun status(projectRoot: File): GitStatusSnapshot

    suspend fun stageAll(projectRoot: File)

    /** Returns the new commit SHA (abbreviated or full). */
    suspend fun commit(
        projectRoot: File,
        message: String,
        authorName: String,
        authorEmail: String,
    ): String

    suspend fun currentBranch(projectRoot: File): String

    suspend fun createBranch(projectRoot: File, name: String)

    suspend fun checkout(projectRoot: File, name: String)

    suspend fun listBranches(projectRoot: File): List<String>

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
}

data class GitCredentials(
    val username: String,
    val passwordOrToken: String,
)

data class GitStatusSnapshot(
    val branch: String,
    val isClean: Boolean,
    val added: Set<String>,
    val changed: Set<String>,
    val modified: Set<String>,
    val untracked: Set<String>,
    val conflicting: Set<String>,
    val missing: Set<String>,
)
