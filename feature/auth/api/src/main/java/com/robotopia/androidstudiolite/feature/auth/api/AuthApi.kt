package com.robotopia.androidstudiolite.feature.auth.api

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount
import com.robotopia.androidstudiolite.feature.auth.model.ConnectProgress
import kotlinx.coroutines.flow.Flow

interface AuthSession {
    fun observeAccount(): Flow<AuthAccount?>
    suspend fun currentAccount(): AuthAccount?
    suspend fun clearAccount()
}

/**
 * Owns Connect device-flow orchestration. Emits [ConnectProgress] until a terminal
 * Connected/Failed, or until the collector is cancelled (Cancel / back — no half session).
 */
interface AuthService : AuthSession {
    fun connect(): Flow<ConnectProgress>
}

interface AuthScreens {
    /**
     * Settings · Build account — connect / log out.
     * Owns in-feature navigation to [ConnectAccount].
     */
    @Composable
    fun BuildAccount(
        onDismiss: () -> Unit,
    )

    @Composable
    fun ConnectAccount(
        onFinished: () -> Unit,
        onCancel: () -> Unit,
    )
}
