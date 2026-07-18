package com.robotopia.androidstudiolite.feature.settings.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.SettingsRow
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle

@Composable
internal fun SettingsHomeContent(
    buildAccountSubtitle: String,
    themeSubtitle: String,
    onBackClick: () -> Unit,
    onThemeClick: () -> Unit,
    onBuildAccountClick: () -> Unit,
    onBuildHistoryClick: () -> Unit,
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
                title = "Theme",
                subtitle = themeSubtitle,
                onClick = onThemeClick,
            )
            SettingsRow(
                title = "Build account",
                subtitle = buildAccountSubtitle,
                onClick = onBuildAccountClick,
            )
            SettingsRow(
                title = "Build history",
                subtitle = "Past and running builds",
                onClick = onBuildHistoryClick,
            )
            SettingsRow(
                title = "About",
                subtitle = "Android Studio Lite",
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
