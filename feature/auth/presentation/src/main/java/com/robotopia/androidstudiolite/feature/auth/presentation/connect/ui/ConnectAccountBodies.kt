package com.robotopia.androidstudiolite.feature.auth.presentation.connect.ui

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.InfoCard
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.component.UserCodeRow
import com.robotopia.androidstudiolite.designsystem.icon.IconSuccess
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ConnectUiState

@Composable
internal fun ConnectLoadingBody() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LoadingIndicator(label = "Preparing…")
    }
}

@Composable
internal fun ConnectShowCodeBody(
    state: ConnectUiState.ShowCode,
    onOpenVerificationUri: (uri: String) -> Unit,
    onCopyCode: (code: String) -> Unit,
) {
    val verificationHost = Uri.parse(state.verificationUri).host
        ?.takeIf { it.isNotBlank() }
        ?: state.verificationUri
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BasicText(
                text = "Copy the code below",
                style = Typography.Body.copy(color = Colors.Muted),
            )
            UserCodeRow(
                code = state.userCode,
                onCopy = { onCopyCode(state.userCode) },
            )
            BasicText(
                text = "Paste it at $verificationHost",
                style = Typography.Body.copy(color = Colors.Muted),
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Opening the provider also copies the user code so the user can paste
            // it immediately after the browser opens.
            Button(
                label = "Open ${state.providerName}",
                onClick = {
                    onCopyCode(state.userCode)
                    onOpenVerificationUri(state.verificationUri)
                },
                variant = ButtonVariant.Primary,
            )
        }
    }
}

@Composable
internal fun ConnectConnectedBody(
    state: ConnectUiState.Connected,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            IconSuccess(
                tint = Colors.Primary,
                size = 48.dp,
            )
            BasicText(
                text = "Connected",
                style = Typography.Headline.copy(color = Colors.Text),
                modifier = Modifier.fillMaxWidth(),
            )
            InfoCard(
                label = state.account.providerName,
                title = state.account.identity,
            )
        }
    }
}

@Composable
internal fun ConnectFailedBody(
    state: ConnectUiState.Failed,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BasicText(
                text = "Couldn't connect",
                style = Typography.Headline.copy(color = Colors.Danger),
            )
            BasicText(
                text = state.message,
                style = Typography.Body.copy(
                    color = Colors.Muted,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
