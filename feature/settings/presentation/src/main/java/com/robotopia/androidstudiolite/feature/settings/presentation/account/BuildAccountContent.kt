package com.robotopia.androidstudiolite.feature.settings.presentation.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.InfoCard
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount
import com.robotopia.androidstudiolite.feature.settings.presentation.preview.BuildAccountPreviewCase
import com.robotopia.androidstudiolite.feature.settings.presentation.preview.BuildAccountPreviewProvider

@Composable
internal fun BuildAccountContent(
    account: AuthAccount?,
    providerDisplayName: String,
    onBackClick: () -> Unit,
    onConnectClick: () -> Unit,
    onLogOutClick: () -> Unit,
) {
    IslandScaffold(
        topBar = {
            TopBarBackTitle(
                title = "Build account",
                onBackClick = onBackClick,
            )
        },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                if (account == null) {
                    Button(
                        label = "Connect $providerDisplayName",
                        onClick = onConnectClick,
                        variant = ButtonVariant.Primary,
                    )
                } else {
                    Button(
                        label = "Log out",
                        onClick = onLogOutClick,
                        variant = ButtonVariant.DangerText,
                    )
                }
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BasicText(
                text = account?.providerName ?: providerDisplayName,
                style = Typography.Headline.copy(color = Colors.Text),
            )
            if (account == null) {
                InfoCard(title = "Not connected")
            } else {
                InfoCard(
                    label = account.providerName,
                    title = account.identity,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun BuildAccountContentPreview(
    @PreviewParameter(BuildAccountPreviewProvider::class) preview: BuildAccountPreviewCase,
) {
    BuildAccountContent(
        account = preview.account,
        providerDisplayName = preview.providerDisplayName,
        onBackClick = {},
        onConnectClick = {},
        onLogOutClick = {},
    )
}
