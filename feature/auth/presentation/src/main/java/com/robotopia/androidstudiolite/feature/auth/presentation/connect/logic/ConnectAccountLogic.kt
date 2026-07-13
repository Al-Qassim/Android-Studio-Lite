package com.robotopia.androidstudiolite.feature.auth.presentation.connect.logic

import com.robotopia.androidstudiolite.feature.auth.api.AuthService
import com.robotopia.androidstudiolite.feature.auth.model.ConnectProgress
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ConnectUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch

internal suspend fun collectConnectProgress(
    authService: AuthService,
    uiState: MutableStateFlow<ConnectUiState>,
) {
    var lastUserCode = ""
    uiState.value = ConnectUiState.Loading
    authService.connect()
        .catch {
            uiState.value = ConnectUiState.Failed(
                message = "Couldn't connect. Try again.",
            )
        }
        .collect { progress ->
            when (progress) {
                is ConnectProgress.ShowCode -> {
                    lastUserCode = progress.userCode
                    uiState.value = ConnectUiState.ShowCode(
                        userCode = progress.userCode,
                        verificationUri = progress.verificationUri,
                        providerName = progress.providerName,
                    )
                }

                ConnectProgress.Waiting -> {
                    uiState.value = ConnectUiState.Waiting(userCode = lastUserCode)
                }

                is ConnectProgress.Connected -> {
                    uiState.value = ConnectUiState.Connected(account = progress.account)
                }

                is ConnectProgress.Failed -> {
                    uiState.value = ConnectUiState.Failed(message = progress.message)
                }
            }
        }
}

internal fun openVerificationUri(
    uri: String,
    uiState: MutableStateFlow<ConnectUiState>,
    openUri: (String) -> Unit,
) {
    runCatching { openUri(uri) }
    val current = uiState.value
    if (current is ConnectUiState.ShowCode) {
        uiState.value = ConnectUiState.Waiting(userCode = current.userCode)
    }
}

internal fun copyUserCode(
    code: String,
    setClipboardText: (String) -> Unit,
) {
    setClipboardText(code)
}
