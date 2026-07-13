package com.robotopia.androidstudiolite.feature.auth.model

/** Connected build account shown in UI (provider display name + identity). */
data class AuthAccount(
    val providerName: String,
    val identity: String,
)

/** Live device-authorization progress for Connect account. */
sealed interface ConnectProgress {
    /** Challenge ready: caller may show [userCode] and open [verificationUri]. */
    data class ShowCode(
        val userCode: String,
        val verificationUri: String,
    ) : ConnectProgress

    /** Polling for authorization. */
    data object Waiting : ConnectProgress

    data class Connected(
        val account: AuthAccount,
    ) : ConnectProgress

    data class Failed(
        val message: String,
    ) : ConnectProgress
}
