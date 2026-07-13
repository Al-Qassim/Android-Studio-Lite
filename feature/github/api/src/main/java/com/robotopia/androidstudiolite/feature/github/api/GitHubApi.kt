package com.robotopia.androidstudiolite.feature.github.api

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
}
