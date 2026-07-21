package com.robotopia.androidstudiolite.feature.github.data

import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.github.api.GitHubClient
import com.robotopia.androidstudiolite.feature.github.api.GitHubDeviceCode
import com.robotopia.androidstudiolite.feature.github.api.GitHubDeviceTokenResult
import com.robotopia.androidstudiolite.feature.github.api.GitHubReleaseRef
import com.robotopia.androidstudiolite.feature.github.api.GitHubRepoRef
import com.robotopia.androidstudiolite.feature.github.api.GitHubUser
import com.robotopia.androidstudiolite.feature.github.api.GitHubWorkflowRun
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.delay

/**
 * Local stub for offline UI review of device flow. Build helpers are not implemented.
 */
class StubGitHubClient : GitHubClient {

    private val pollCounts = ConcurrentHashMap<String, AtomicInteger>()

    override suspend fun requestDeviceCode(clientId: String): GitHubDeviceCode {
        val deviceCode = "stub-device-${System.currentTimeMillis()}"
        pollCounts[deviceCode] = AtomicInteger(0)
        return GitHubDeviceCode(
            deviceCode = deviceCode,
            userCode = "WDJB-MJHT",
            verificationUri = VERIFICATION_URI,
            expiresInSeconds = 900,
            intervalSeconds = 1,
        )
    }

    override suspend fun pollDeviceToken(
        clientId: String,
        deviceCode: String,
    ): GitHubDeviceTokenResult {
        delay(400)
        val count = pollCounts[deviceCode]?.incrementAndGet() ?: return GitHubDeviceTokenResult.Expired
        return when {
            count < POLLS_BEFORE_SUCCESS -> GitHubDeviceTokenResult.Pending
            else -> GitHubDeviceTokenResult.Success(accessToken = "stub-token-$deviceCode")
        }
    }

    override suspend fun fetchAuthenticatedUser(accessToken: String): GitHubUser {
        delay(200)
        return GitHubUser(login = "alex-dev")
    }

    override suspend fun ensureSandboxRepo(accessToken: String): GitHubRepoRef =
        notImplemented()

    override suspend fun createUserRepo(
        accessToken: String,
        name: String,
        private: Boolean,
    ): GitHubRepoRef {
        delay(200)
        return GitHubRepoRef(owner = "alex-dev", name = name)
    }

    override suspend fun ensureWorkflowFile(accessToken: String, repo: GitHubRepoRef) =
        notImplemented()

    override suspend fun createRelease(
        accessToken: String,
        repo: GitHubRepoRef,
        tag: String,
    ): GitHubReleaseRef = notImplemented()

    override suspend fun uploadReleaseAsset(
        accessToken: String,
        release: GitHubReleaseRef,
        file: File,
        assetName: String,
    ) = notImplemented()

    override suspend fun dispatchWorkflow(
        accessToken: String,
        repo: GitHubRepoRef,
        releaseTag: String,
    ) = notImplemented()

    override suspend fun findLatestWorkflowRun(
        accessToken: String,
        repo: GitHubRepoRef,
        notBeforeEpochMs: Long,
    ): GitHubWorkflowRun? = notImplemented()

    override suspend fun getWorkflowRun(
        accessToken: String,
        repo: GitHubRepoRef,
        runId: Long,
    ): GitHubWorkflowRun = notImplemented()

    override suspend fun cancelWorkflowRun(
        accessToken: String,
        repo: GitHubRepoRef,
        runId: Long,
    ) = notImplemented()

    override suspend fun findReleaseApkAssetUrl(
        accessToken: String,
        repo: GitHubRepoRef,
        tag: String,
    ): String = notImplemented()

    override suspend fun downloadAssetToFile(
        accessToken: String,
        assetApiUrl: String,
        destination: File,
    ) = notImplemented()

    override suspend fun deleteRelease(
        accessToken: String,
        repo: GitHubRepoRef,
        releaseId: Long,
        tag: String,
    ) = notImplemented()

    private fun notImplemented(): Nothing =
        throw AppException("Stub GitHub client cannot run cloud builds.")

    private companion object {
        const val VERIFICATION_URI = "https://github.com/login/device"
        const val POLLS_BEFORE_SUCCESS = 4
    }
}
