package com.robotopia.androidstudiolite.feature.github.data

import com.robotopia.androidstudiolite.feature.github.api.GitHubClient
import com.robotopia.androidstudiolite.feature.github.api.GitHubDeviceCode
import com.robotopia.androidstudiolite.feature.github.api.GitHubDeviceTokenResult
import com.robotopia.androidstudiolite.feature.github.api.GitHubUser
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.delay

/**
 * Local stub for design review: returns a fixed user code and authorizes after a few polls.
 * No network and no client secret.
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

    private companion object {
        const val VERIFICATION_URI = "https://github.com/login/device"
        const val POLLS_BEFORE_SUCCESS = 4
    }
}
