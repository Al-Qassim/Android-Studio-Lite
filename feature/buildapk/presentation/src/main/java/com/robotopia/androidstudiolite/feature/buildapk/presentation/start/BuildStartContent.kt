package com.robotopia.androidstudiolite.feature.buildapk.presentation.start

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
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.InfoCard
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.typography.Typography

@Composable
internal fun BuildStartContent(
    projectName: String,
    packageName: String,
    starting: Boolean,
    signedIn: Boolean,
    providerDisplayName: String,
    onBackClick: () -> Unit,
    onStartBuildClick: () -> Unit,
    onConnectAccountClick: () -> Unit,
) {
    IslandScaffold(
        topBar = {
            TopBarBackTitle(
                title = "Build",
                onBackClick = onBackClick,
            )
        },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                if (signedIn) {
                    Button(
                        label = if (starting) "Starting…" else "Start build",
                        onClick = onStartBuildClick,
                        variant = ButtonVariant.Primary,
                        enabled = !starting,
                    )
                } else {
                    Button(
                        label = "Start build",
                        onClick = {},
                        variant = ButtonVariant.Disabled,
                        enabled = false,
                    )
                    Button(
                        label = "Connect $providerDisplayName",
                        onClick = onConnectAccountClick,
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            InfoCard(
                title = projectName,
                subtitle = packageName,
            )
            if (!signedIn) {
                BasicText(
                    text = "Connect a build account before starting a cloud build.",
                    style = Typography.Body.copy(color = Colors.Muted),
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
