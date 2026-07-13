package com.robotopia.androidstudiolite.feature.auth.presentation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount

@Composable
internal fun BuildAccountContent(
    account: AuthAccount?,
    onBackClick: () -> Unit,
    onConnectClick: () -> Unit,
    onLogOutClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        TopBarBackTitle(
            title = "Settings",
            onBackClick = onBackClick,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BasicText(
                text = "GitHub",
                style = Typography.Headline.copy(color = Colors.Text),
            )
            AccountStatusCard(account = account)
            Spacer(modifier = Modifier.height(8.dp))
            if (account == null) {
                Button(
                    label = "Connect account",
                    onClick = onConnectClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.Primary,
                )
            } else {
                Button(
                    label = "Log out",
                    onClick = onLogOutClick,
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.DangerText,
                )
            }
        }
    }
}

@Composable
private fun AccountStatusCard(account: AuthAccount?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Colors.Surface2)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (account == null) {
            BasicText(
                text = "Not connected",
                style = Typography.Subtitle.copy(color = Colors.Text),
            )
        } else {
            BasicText(
                text = account.providerName,
                style = Typography.Subtitle.copy(color = Colors.Text),
            )
            BasicText(
                text = account.identity,
                style = Typography.Body.copy(color = Colors.Muted),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun BuildAccountLoggedOutPreview() {
    BuildAccountContent(
        account = null,
        onBackClick = {},
        onConnectClick = {},
        onLogOutClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun BuildAccountConnectedPreview() {
    BuildAccountContent(
        account = AuthAccount(providerName = "GitHub", identity = "@alex-dev"),
        onBackClick = {},
        onConnectClick = {},
        onLogOutClick = {},
    )
}
