package com.robotopia.androidstudiolite.designsystem.preview.settings

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
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.InfoCard
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.SettingsRow
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.preview.ExamplePreviewBackground
import com.robotopia.androidstudiolite.designsystem.typography.Typography

enum class SettingsExampleCase {
    HomeLoggedOut,
    HomeConnected,
    BuildAccountLoggedOut,
    BuildAccountConnected,
}

private class SettingsExampleCaseProvider : PreviewParameterProvider<SettingsExampleCase> {
    override val values = SettingsExampleCase.entries.asSequence()
    override fun getDisplayName(index: Int): String = values.elementAt(index).name
}

@Preview(
    name = "Settings",
    showBackground = true,
    backgroundColor = ExamplePreviewBackground,
    widthDp = 360,
    heightDp = 640,
)
@Composable
fun SettingsExamplePreview(
    @PreviewParameter(SettingsExampleCaseProvider::class) case: SettingsExampleCase,
) {
    when (case) {
        SettingsExampleCase.HomeLoggedOut -> SettingsHomeScreen(connected = false)
        SettingsExampleCase.HomeConnected -> SettingsHomeScreen(connected = true)
        SettingsExampleCase.BuildAccountLoggedOut -> BuildAccountScreen(connected = false)
        SettingsExampleCase.BuildAccountConnected -> BuildAccountScreen(connected = true)
    }
}

@Composable
private fun SettingsHomeScreen(connected: Boolean) {
    IslandScaffold(
        topBar = { TopBarBackTitle(title = "Settings") },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SettingsRow(
                title = "Build account",
                subtitle = if (connected) "GitHub · @alex-dev" else "Not connected",
            )
            SettingsRow(
                title = "About",
                subtitle = "Android Studio Lite",
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun BuildAccountScreen(connected: Boolean) {
    IslandScaffold(
        topBar = { TopBarBackTitle(title = "Build account") },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                if (connected) {
                    Button(
                        label = "Log out",
                        onClick = {},
                        variant = ButtonVariant.DangerText,
                    )
                } else {
                    Button(
                        label = "Connect GitHub",
                        onClick = {},
                        variant = ButtonVariant.Primary,
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
                text = "GitHub",
                style = Typography.Headline.copy(color = Colors.Text),
            )
            if (connected) {
                InfoCard(
                    label = "GitHub",
                    title = "@alex-dev",
                )
            } else {
                InfoCard(title = "Not connected")
                BasicText(
                    text = "Connect to run cloud builds from this device.",
                    style = Typography.Body.copy(color = Colors.Muted),
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
