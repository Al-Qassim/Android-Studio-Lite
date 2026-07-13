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
    var lastUserCode by remember { mutableStateOf("") }
    val uriHandler = LocalUriHandler.current

    BackHandler(onBack = onCancel)

    LaunchedEffect(attempt) {
        state = ConnectUiState.Loading
        lastUserCode = ""
        authService.connect()
            .catch {
                state = ConnectUiState.Failed(
                    message = "Couldn't connect to GitHub. Try again.",
                )
            }
            .collect { progress ->
                when (progress) {
                    is ConnectProgress.ShowCode -> {
                        lastUserCode = progress.userCode
                        state = ConnectUiState.ShowCode(
                            userCode = progress.userCode,
                            verificationUri = progress.verificationUri,
                        )
                    }
                    ConnectProgress.Waiting -> {
                        state = ConnectUiState.Waiting(userCode = lastUserCode)
                    }
                    is ConnectProgress.Connected -> {
                        state = ConnectUiState.Connected(account = progress.account)
                    }
                    is ConnectProgress.Failed -> {
                        state = ConnectUiState.Failed(message = progress.message)
                    }
                }
            }
    }

    ConnectAccountContent(
        state = state,
        onBackClick = onCancel,
        onOpenGitHub = { uri ->
            runCatching { uriHandler.openUri(uri) }
            val current = state
            if (current is ConnectUiState.ShowCode) {
                state = ConnectUiState.Waiting(userCode = current.userCode)
            }
        },
        onCancel = onCancel,
        onContinue = onFinished,
        onTryAgain = { attempt += 1 },
    )
}
