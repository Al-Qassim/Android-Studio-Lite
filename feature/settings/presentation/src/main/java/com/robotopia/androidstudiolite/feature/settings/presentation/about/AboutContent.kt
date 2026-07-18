package com.robotopia.androidstudiolite.feature.settings.presentation.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.InfoCard
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.SettingsRow
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.typography.Typography

internal const val GITHUB_REPO_URL = "https://github.com/Al-Qassim/Android-Studio-Lite"

@Composable
internal fun AboutContent(
    versionLabel: String,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    IslandScaffold(
        topBar = {
            TopBarBackTitle(
                title = "About",
                onBackClick = onBackClick,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            InfoCard(
                title = "Android Studio Lite",
                subtitle = versionLabel,
                label = "App",
            )
            BasicText(
                text = "A phone-first IDE for creating, editing, and building Android apps — projects, files, cloud builds, and install, all on device.",
                style = Typography.Body.copy(color = Theme.colors.Muted),
            )
            BasicText(
                text = "Android Studio Lite is open source.",
                style = Typography.Body.copy(color = Theme.colors.Text),
            )
            SettingsRow(
                title = "GitHub",
                subtitle = "github.com/Al-Qassim/Android-Studio-Lite",
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_REPO_URL)),
                    )
                },
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
