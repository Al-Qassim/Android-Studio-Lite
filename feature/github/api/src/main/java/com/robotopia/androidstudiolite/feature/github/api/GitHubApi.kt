package com.robotopia.androidstudiolite.feature.github.api

import java.io.File

data class GitHubDeviceCode(
    val deviceCode: String,
    val userCode: String,
    val verificationUri: String,
    val expiresInSeconds: Int,
    val intervalSeconds: Int,
)

data class GitHubUser(
    val login: String,
)

sealed interface GitHubDeviceTokenResult {
    data class Success(val accessToken: String) : GitHubDeviceTokenResult
    data object Pending : GitHubDeviceTokenResult
    data object Denied : GitHubDeviceTokenResult
    data object Expired : GitHubDeviceTokenResult
}

data class GitHubRepoRef(
    val owner: String,
    val name: String,
    val htmlUrl: String = "https://github.com/$owner/$name",
    val cloneUrl: String = "https://github.com/$owner/$name.git",
) {
    val fullName: String get() = "$owner/$name"
}

data class GitHubReleaseRef(
    val id: Long,
    val tag: String,
    val uploadUrlTemplate: String,
)

data class GitHubWorkflowRun(
    val id: Long,
    val status: String,
    val conclusion: String?,
    val htmlUrl: String?,
)

/**
 * Stateless GitHub helpers. Caller supplies client id / tokens; no ASL session storage.
 */
interface GitHubClient {
    suspend fun requestDeviceCode(clientId: String): GitHubDeviceCode

    suspend fun pollDeviceToken(
        clientId: String,
        deviceCode: String,
    ): GitHubDeviceTokenResult

    suspend fun fetchAuthenticatedUser(accessToken: String): GitHubUser

    /** Ensure the ASL public build sandbox exists (or is a known ASL repo). */
    suspend fun ensureSandboxRepo(accessToken: String): GitHubRepoRef

    /** Create a user-owned repository (Publish to GitHub). */
    suspend fun createUserRepo(
        accessToken: String,
        name: String,
        private: Boolean,
    ): GitHubRepoRef

    suspend fun ensureWorkflowFile(accessToken: String, repo: GitHubRepoRef)

    suspend fun createRelease(
        accessToken: String,
        repo: GitHubRepoRef,
        tag: String,
    ): GitHubReleaseRef

    suspend fun uploadReleaseAsset(
        accessToken: String,
        release: GitHubReleaseRef,
        file: File,
        assetName: String,
    )

    suspend fun dispatchWorkflow(
        accessToken: String,
        repo: GitHubRepoRef,
        releaseTag: String,
    )

    suspend fun findLatestWorkflowRun(
        accessToken: String,
        repo: GitHubRepoRef,
        notBeforeEpochMs: Long,
    ): GitHubWorkflowRun?

    suspend fun getWorkflowRun(
        accessToken: String,
        repo: GitHubRepoRef,
        runId: Long,
    ): GitHubWorkflowRun

    suspend fun cancelWorkflowRun(
        accessToken: String,
        repo: GitHubRepoRef,
        runId: Long,
    )

    suspend fun findReleaseApkAssetUrl(
        accessToken: String,
        repo: GitHubRepoRef,
        tag: String,
    ): String

    suspend fun downloadAssetToFile(
        accessToken: String,
        assetApiUrl: String,
        destination: File,
    )

    suspend fun deleteRelease(
        accessToken: String,
        repo: GitHubRepoRef,
        releaseId: Long,
        tag: String,
    )
}
