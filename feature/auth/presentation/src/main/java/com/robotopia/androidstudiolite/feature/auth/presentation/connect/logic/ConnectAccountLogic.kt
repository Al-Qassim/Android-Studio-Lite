package com.robotopia.androidstudiolite.feature.auth.presentation.connect.logic

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import com.robotopia.androidstudiolite.feature.auth.api.AuthService
import com.robotopia.androidstudiolite.feature.auth.model.ConnectProgress
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ConnectUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch

internal suspend fun collectConnectProgress(
    authService: AuthService,
    uiState: MutableStateFlow<ConnectUiState>,
) {
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
                    uiState.value = ConnectUiState.ShowCode(
                        userCode = progress.userCode,
                        verificationUri = progress.verificationUri,
                        providerName = progress.providerName,
                    )
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
    openUri: (String) -> Unit,
) {
    runCatching { openUri(uri) }
}

internal suspend fun copyUserCode(
    code: String,
    clipboard: Clipboard,
) {
    clipboard.setClipEntry(ClipEntry(ClipData.newPlainText(CLIP_LABEL, code)))
}

private const val CLIP_LABEL = "device code"
