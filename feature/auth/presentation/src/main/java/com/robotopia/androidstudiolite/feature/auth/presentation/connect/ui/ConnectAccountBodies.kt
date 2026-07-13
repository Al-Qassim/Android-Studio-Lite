package com.robotopia.androidstudiolite.feature.auth.presentation.connect.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.IconButton
import com.robotopia.androidstudiolite.designsystem.component.IconButtonVariant
import com.robotopia.androidstudiolite.designsystem.icon.IconCopy
import com.robotopia.androidstudiolite.designsystem.icon.IconSuccess
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ConnectUiState

@Composable
internal fun ConnectLoadingBody() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = "Preparing…",
            style = Typography.Body.copy(color = Colors.Muted),
        )
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
        ) {
            BasicText(
                text = "Copy the code below",
                style = Typography.Body.copy(color = Colors.Muted),
            )
            UserCodeCopyRow(
                userCode = state.userCode,
                onCopyCode = onCopyCode,
            )
            BasicText(
                text = "Paste it at $verificationHost",
                style = Typography.Body.copy(color = Colors.Muted),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                label = "Open ${state.providerName}",
                onClick = { onOpenVerificationUri(state.verificationUri) },
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Primary,
            )
        }
    }
}

@Composable
internal fun ConnectWaitingBody(
    state: ConnectUiState.Waiting,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BasicText(
                text = "Waiting for approval…",
                style = Typography.Headline.copy(color = Colors.Text),
            )
            UserCodeCard(userCode = state.userCode)
            WaitingDotsRow()
            Button(
                label = "Cancel",
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Secondary,
            )
        }
    }
}

@Composable
internal fun ConnectConnectedBody(
    state: ConnectUiState.Connected,
    onContinue: () -> Unit,
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
            AccountCard(account = state.account)
            Button(
                label = "Continue",
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Primary,
            )
        }
    }
}

@Composable
internal fun ConnectFailedBody(
    state: ConnectUiState.Failed,
    onCancel: () -> Unit,
    onTryAgain: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BasicText(
                text = "Couldn't connect",
                style = Typography.Headline.copy(color = Colors.Danger),
            )
            BasicText(
                text = state.message,
                style = Typography.Body.copy(color = Colors.Muted),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                    label = "Cancel",
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    variant = ButtonVariant.Secondary,
                )
                Button(
                    label = "Try again",
                    onClick = onTryAgain,
                    modifier = Modifier.weight(1f),
                    variant = ButtonVariant.Primary,
                )
            }
        }
    }
}

@Composable
private fun UserCodeCopyRow(
    userCode: String,
    onCopyCode: (code: String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Colors.Surface2)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = userCode,
            style = Typography.Headline.copy(
                color = Colors.Text,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                letterSpacing = 1.5.sp,
            ),
            modifier = Modifier
                .weight(1f)
                .clickable { onCopyCode(userCode) }
                .padding(horizontal = 4.dp, vertical = 6.dp),
        )
        IconButton(
            onClick = { onCopyCode(userCode) },
            modifier = Modifier.semantics { contentDescription = "Copy code" },
            variant = IconButtonVariant.Ghost,
            icon = { _, size -> IconCopy(tint = Colors.Primary, size = size) },
        )
    }
}

@Composable
private fun UserCodeCard(userCode: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Colors.Surface2)
            .padding(horizontal = 14.dp, vertical = 14.dp),
    ) {
        BasicText(
            text = userCode,
            style = Typography.Headline.copy(
                color = Colors.Text,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                letterSpacing = 1.5.sp,
            ),
        )
    }
}

@Composable
private fun AccountCard(account: AuthAccount) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Colors.Surface2)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        BasicText(
            text = account.providerName,
            style = Typography.Label.copy(color = Colors.Muted),
        )
        BasicText(
            text = account.identity,
            style = Typography.Subtitle.copy(color = Colors.Text),
        )
    }
}

@Composable
private fun WaitingDotsRow() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Colors.Primary),
            )
        }
    }
}
