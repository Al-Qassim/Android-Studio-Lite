package com.robotopia.androidstudiolite.feature.auth.presentation.connect

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.IconButton
import com.robotopia.androidstudiolite.designsystem.component.IconButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.icon.IconCopy
import com.robotopia.androidstudiolite.designsystem.icon.IconSuccess
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount

internal sealed interface ConnectUiState {
    data class ShowCode(
        val userCode: String,
        val verificationUri: String,
    ) : ConnectUiState

    data class Waiting(
        val userCode: String,
    ) : ConnectUiState

    data class Connected(
        val account: AuthAccount,
    ) : ConnectUiState

    data class Failed(
        val message: String,
    ) : ConnectUiState

    data object Loading : ConnectUiState
}

@Composable
internal fun ConnectAccountContent(
    state: ConnectUiState,
    onBackClick: () -> Unit,
    onOpenGitHub: (uri: String) -> Unit,
    onCopyCode: (code: String) -> Unit,
    onCancel: () -> Unit,
    onContinue: () -> Unit,
    onTryAgain: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        TopBarBackTitle(
            title = "Connect",
            onBackClick = onBackClick,
        )
        when (state) {
            ConnectUiState.Loading -> {
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

            is ConnectUiState.ShowCode -> ShowCodeBody(
                state = state,
                onOpenGitHub = onOpenGitHub,
                onCopyCode = onCopyCode,
            )

            is ConnectUiState.Waiting -> WaitingBody(
                state = state,
                onCancel = onCancel,
            )

            is ConnectUiState.Connected -> ConnectedBody(
                state = state,
                onContinue = onContinue,
            )

            is ConnectUiState.Failed -> FailedBody(
                state = state,
                onCancel = onCancel,
                onTryAgain = onTryAgain,
            )
        }
    }
}

@Composable
private fun ShowCodeBody(
    state: ConnectUiState.ShowCode,
    onOpenGitHub: (uri: String) -> Unit,
    onCopyCode: (code: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        BasicText(
            text = "Copy the code below",
            style = Typography.Body.copy(color = Colors.Muted),
        )
        Spacer(modifier = Modifier.height(16.dp))
        UserCodeCopyRow(
            userCode = state.userCode,
            onCopyCode = onCopyCode,
        )
        Spacer(modifier = Modifier.height(12.dp))
        BasicText(
            text = "Paste it at github.com/login/device",
            style = Typography.Body.copy(color = Colors.Muted),
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            label = "Open GitHub",
            onClick = { onOpenGitHub(state.verificationUri) },
            modifier = Modifier.fillMaxWidth(),
            variant = ButtonVariant.Primary,
        )
    }
}

@Composable
private fun WaitingBody(
    state: ConnectUiState.Waiting,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        BasicText(
            text = "Waiting for approval…",
            style = Typography.Headline.copy(color = Colors.Text),
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicText(
            text = "Finish signing in on GitHub on the other device. This screen updates automatically.",
            style = Typography.Body.copy(color = Colors.Muted),
        )
        Spacer(modifier = Modifier.height(20.dp))
        UserCodeCard(userCode = state.userCode)
        Spacer(modifier = Modifier.height(16.dp))
        WaitingDotsRow(label = "Checking authorization…")
        Spacer(modifier = Modifier.weight(1f))
        Button(
            label = "Cancel",
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            variant = ButtonVariant.Secondary,
        )
    }
}

@Composable
private fun ConnectedBody(
    state: ConnectUiState.Connected,
    onContinue: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        IconSuccess(
            tint = Colors.Primary,
            size = 48.dp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        BasicText(
            text = "Account connected",
            style = Typography.Headline.copy(color = Colors.Text),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicText(
            text = "Builds will use your GitHub Actions minutes. You can disconnect anytime in Settings.",
            style = Typography.Body.copy(color = Colors.Muted),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(20.dp))
        AccountCard(account = state.account)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            label = "Continue",
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            variant = ButtonVariant.Primary,
        )
    }
}

@Composable
private fun FailedBody(
    state: ConnectUiState.Failed,
    onCancel: () -> Unit,
    onTryAgain: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        BasicText(
            text = "Couldn't connect to GitHub",
            style = Typography.Headline.copy(color = Colors.Danger),
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicText(
            text = state.message,
            style = Typography.Body.copy(color = Colors.Muted),
        )
        Spacer(modifier = Modifier.weight(1f))
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
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = "Your code",
            style = Typography.Label.copy(color = Colors.Muted),
        )
        BasicText(
            text = userCode,
            style = Typography.Headline.copy(
                color = Colors.Text,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                letterSpacing = 2.sp,
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
private fun WaitingDotsRow(label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Colors.Primary),
                )
            }
        }
        BasicText(
            text = label,
            style = Typography.Body.copy(color = Colors.Muted),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun ConnectShowCodePreview() {
    ConnectAccountContent(
        state = ConnectUiState.ShowCode(
            userCode = "WDJB-MJHT",
            verificationUri = "https://github.com/login/device",
        ),
        onBackClick = {},
        onOpenGitHub = {},
        onCopyCode = {},
        onCancel = {},
        onContinue = {},
        onTryAgain = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun ConnectWaitingPreview() {
    ConnectAccountContent(
        state = ConnectUiState.Waiting(
            userCode = "WDJB-MJHT",
        ),
        onBackClick = {},
        onOpenGitHub = {},
        onCopyCode = {},
        onCancel = {},
        onContinue = {},
        onTryAgain = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun ConnectConnectedPreview() {
    ConnectAccountContent(
        state = ConnectUiState.Connected(
            account = AuthAccount(providerName = "GitHub", identity = "@alex-dev"),
        ),
        onBackClick = {},
        onOpenGitHub = {},
        onCopyCode = {},
        onCancel = {},
        onContinue = {},
        onTryAgain = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun ConnectFailedPreview() {
    ConnectAccountContent(
        state = ConnectUiState.Failed(
            message = "Authorization expired or was denied. Generate a new code to try again.",
        ),
        onBackClick = {},
        onOpenGitHub = {},
        onCopyCode = {},
        onCancel = {},
        onContinue = {},
        onTryAgain = {},
    )
}
