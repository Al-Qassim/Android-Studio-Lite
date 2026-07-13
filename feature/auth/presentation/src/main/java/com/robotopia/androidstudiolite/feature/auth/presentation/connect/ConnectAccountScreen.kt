package com.robotopia.androidstudiolite.feature.auth.presentation.connect

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import com.robotopia.androidstudiolite.feature.auth.api.AuthService
import com.robotopia.androidstudiolite.feature.auth.model.ConnectProgress
import kotlinx.coroutines.flow.catch

@Composable
internal fun ConnectAccountScreen(
    authService: AuthService,
    onFinished: () -> Unit,
    onCancel: () -> Unit,
) {
    var attempt by remember { mutableIntStateOf(0) }
    var state by remember { mutableStateOf<ConnectUiState>(ConnectUiState.Loading) }
    val uriHandler = LocalUriHandler.current

    BackHandler(onBack = onCancel)

    LaunchedEffect(attempt) {
        state = ConnectUiState.Loading
        authService.connect()
            .catch {
                state = ConnectUiState.Failed(
                    message = "Couldn't connect to GitHub. Try again.",
                )
            }
            .collect { progress ->
                state = progress.toUiState()
            }
    }

    ConnectAccountContent(
        state = state,
        onBackClick = onCancel,
        onOpenGitHub = { uri ->
            runCatching { uriHandler.openUri(uri) }
            val current = state
            if (current is ConnectUiState.ShowCode) {
                state = ConnectUiState.Waiting(
                    userCode = current.userCode,
                    verificationUri = current.verificationUri,
                )
            }
        },
        onCancel = onCancel,
        onContinue = onFinished,
        onTryAgain = { attempt += 1 },
    )
}

private fun ConnectProgress.toUiState(): ConnectUiState = when (this) {
    is ConnectProgress.ShowCode -> ConnectUiState.ShowCode(
        userCode = userCode,
        verificationUri = verificationUri,
    )
    is ConnectProgress.Waiting -> ConnectUiState.Waiting(
        userCode = userCode,
        verificationUri = verificationUri,
    )
    is ConnectProgress.Connected -> ConnectUiState.Connected(account = account)
    is ConnectProgress.Failed -> ConnectUiState.Failed(message = message)
}
