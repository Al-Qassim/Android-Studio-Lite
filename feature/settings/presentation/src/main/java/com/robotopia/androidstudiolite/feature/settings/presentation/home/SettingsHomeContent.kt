package com.robotopia.androidstudiolite.feature.settings.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.SettingsRow
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.feature.settings.presentation.preview.SettingsHomePreviewCase
import com.robotopia.androidstudiolite.feature.settings.presentation.preview.SettingsHomePreviewProvider

@Composable
internal fun SettingsHomeContent(
    buildAccountSubtitle: String,
    onBackClick: () -> Unit,
    onBuildAccountClick: () -> Unit,
) {
    IslandScaffold(
        topBar = {
            TopBarBackTitle(
                title = "Settings",
                onBackClick = onBackClick,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SettingsRow(
                title = "Build account",
                subtitle = buildAccountSubtitle,
                onClick = onBuildAccountClick,
            )
            SettingsRow(
                title = "About",
                subtitle = "Android Studio Lite",
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun SettingsHomeContentPreview(
    @PreviewParameter(SettingsHomePreviewProvider::class) preview: SettingsHomePreviewCase,
) {
    SettingsHomeContent(
        buildAccountSubtitle = preview.buildAccountSubtitle,
        onBackClick = {},
        onBuildAccountClick = {},
    )
}
