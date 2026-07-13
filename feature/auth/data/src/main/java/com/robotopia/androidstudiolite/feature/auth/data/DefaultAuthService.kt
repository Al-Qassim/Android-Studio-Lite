package com.robotopia.androidstudiolite.feature.auth.data

import com.robotopia.androidstudiolite.feature.auth.api.AuthService
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount
import com.robotopia.androidstudiolite.feature.auth.model.ConnectProgress
import com.robotopia.androidstudiolite.feature.github.api.GitHubClient
import com.robotopia.androidstudiolite.feature.github.api.GitHubDeviceTokenResult
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class DefaultAuthService(
    private val store: PrefsAuthSessionStore,
    private val gitHubClient: GitHubClient,
    private val clientId: String = PLACEHOLDER_CLIENT_ID,
) : AuthService {

    override fun observeAccount(): Flow<AuthAccount?> = store.account

    override suspend fun currentAccount(): AuthAccount? = store.account.value

    override suspend fun clearAccount() {
        store.clear()
    }

    override fun connect(): Flow<ConnectProgress> = flow {
        val device = gitHubClient.requestDeviceCode(clientId)
        emit(
            ConnectProgress.ShowCode(
                userCode = device.userCode,
                verificationUri = device.verificationUri,
            ),
        )

        // Brief moment on the code screen, then waiting chrome while we poll.
        delay(1_200)
        if (!currentCoroutineContext().isActive) return@flow

        emit(ConnectProgress.Waiting)

        val deadlineMs = System.currentTimeMillis() + device.expiresInSeconds * 1_000L
        val intervalMs = (device.intervalSeconds.coerceAtLeast(1)) * 1_000L

        while (currentCoroutineContext().isActive) {
            if (System.currentTimeMillis() >= deadlineMs) {
                emit(
                    ConnectProgress.Failed(
                        message = "Authorization expired or was denied. Generate a new code to try again.",
                    ),
                )
                return@flow
            }

            when (
                val result = gitHubClient.pollDeviceToken(
                    clientId = clientId,
                    deviceCode = device.deviceCode,
                )
            ) {
                GitHubDeviceTokenResult.Pending -> {
                    delay(intervalMs)
                }

                GitHubDeviceTokenResult.Denied,
                GitHubDeviceTokenResult.Expired,
                -> {
                    emit(
                        ConnectProgress.Failed(
                            message = "Authorization expired or was denied. Generate a new code to try again.",
                        ),
                    )
                    return@flow
                }

                is GitHubDeviceTokenResult.Success -> {
                    val user = gitHubClient.fetchAuthenticatedUser(result.accessToken)
                    val account = AuthAccount(
                        providerName = PROVIDER_NAME,
                        identity = "@${user.login}",
                    )
                    store.save(account = account, accessToken = result.accessToken)
                    emit(ConnectProgress.Connected(account))
                    return@flow
                }
            }
        }
    }

    private companion object {
        /** Public OAuth App client id placeholder — not a secret. Real id lands with #25. */
        const val PLACEHOLDER_CLIENT_ID = "asl-github-oauth-stub"
        const val PROVIDER_NAME = "GitHub"
    }
}
