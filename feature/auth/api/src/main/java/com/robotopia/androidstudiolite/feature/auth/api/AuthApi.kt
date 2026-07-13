package com.robotopia.androidstudiolite.feature.auth.api

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount
import com.robotopia.androidstudiolite.feature.auth.model.ConnectProgress
import kotlinx.coroutines.flow.Flow

interface AuthSession {
    fun observeAccount(): Flow<AuthAccount?>
    suspend fun currentAccount(): AuthAccount?
    suspend fun clearAccount()

    /** Configured build-provider display name for UI (e.g. Connect CTAs). */
    val providerDisplayName: String

    /** OAuth access token for the connected build provider, or null when logged out. */
    suspend fun accessToken(): String?
}

/**
 * Owns Connect device-flow orchestration. Emits [ConnectProgress] until a terminal
 * Connected/Failed, or until the collector is cancelled (Cancel / back — no half session).
 */
interface AuthService : AuthSession {
    fun connect(): Flow<ConnectProgress>
}

interface AuthScreens {
    @Composable
    fun ConnectAccount(
        onFinished: () -> Unit,
        onCancel: () -> Unit,
    )
}
