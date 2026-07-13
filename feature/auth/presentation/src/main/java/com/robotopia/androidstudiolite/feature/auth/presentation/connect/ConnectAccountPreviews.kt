package com.robotopia.androidstudiolite.feature.auth.presentation.connect

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount

internal data class ConnectAccountPreviewCase(
    private val label: String,
    val state: ConnectUiState,
) {
    override fun toString(): String = label
}

internal class ConnectAccountPreviewProvider : PreviewParameterProvider<ConnectAccountPreviewCase> {
    override fun getDisplayName(index: Int): String = values.toList()[index].toString()

    override val values = sequenceOf(
        ConnectAccountPreviewCase("loading", ConnectUiState.Loading),
        ConnectAccountPreviewCase(
            "show code",
            ConnectUiState.ShowCode(
                userCode = "WDJB-MJHT",
                verificationUri = "https://github.com/login/device",
                providerName = "GitHub",
            ),
        ),
        ConnectAccountPreviewCase(
            "waiting",
            ConnectUiState.Waiting(userCode = "WDJB-MJHT"),
        ),
        ConnectAccountPreviewCase(
            "connected",
            ConnectUiState.Connected(
                account = AuthAccount(providerName = "GitHub", identity = "@alex-dev"),
            ),
        ),
        ConnectAccountPreviewCase(
            "failed",
            ConnectUiState.Failed(message = "Expired or denied. Try again."),
        ),
    )
}
