package com.robotopia.androidstudiolite.feature.settings.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.icon.IconChevron
import com.robotopia.androidstudiolite.designsystem.typography.Typography

@Composable
internal fun SettingsHomeContent(
    buildAccountSubtitle: String,
    onBackClick: () -> Unit,
    onBuildAccountClick: () -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SettingsSectionRow(
                title = "Build account",
                subtitle = buildAccountSubtitle,
                onClick = onBuildAccountClick,
            )
        }
    }
}

@Composable
private fun SettingsSectionRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Colors.Surface2)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            BasicText(
                text = title,
                style = Typography.Subtitle.copy(color = Colors.Text),
            )
            BasicText(
                text = subtitle,
                style = Typography.Body.copy(color = Colors.Muted),
            )
        }
        IconChevron(
            tint = Colors.Muted,
            size = 18.dp,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun SettingsHomeLoggedOutPreview() {
    SettingsHomeContent(
        buildAccountSubtitle = "Not connected",
        onBackClick = {},
        onBuildAccountClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun SettingsHomeConnectedPreview() {
    SettingsHomeContent(
        buildAccountSubtitle = "GitHub · @alex-dev",
        onBackClick = {},
        onBuildAccountClick = {},
    )
}
