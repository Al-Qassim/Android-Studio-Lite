package com.robotopia.androidstudiolite.designsystem.preview.buildapk

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.component.PhaseItem
import com.robotopia.androidstudiolite.designsystem.component.PhaseList
import com.robotopia.androidstudiolite.designsystem.component.PhaseStatus
import com.robotopia.androidstudiolite.designsystem.component.ProgressBar
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.preview.ExamplePreviewBackground
import com.robotopia.androidstudiolite.designsystem.typography.Typography

enum class BuildExampleCase {
    StartSignedIn,
    StartNeedsAuth,
    Preparing,
    Building,
    ReadyToInstall,
    Failed,
}

private class BuildExampleCaseProvider : PreviewParameterProvider<BuildExampleCase> {
    override val values = BuildExampleCase.entries.asSequence()
    override fun getDisplayName(index: Int): String = values.elementAt(index).name
}

@Preview(
    name = "Build",
    showBackground = true,
    backgroundColor = ExamplePreviewBackground,
    widthDp = 360,
    heightDp = 640,
)
@Composable
fun BuildExamplePreview(
    @PreviewParameter(BuildExampleCaseProvider::class) case: BuildExampleCase,
) {
    when (case) {
        BuildExampleCase.StartSignedIn -> BuildStartScreen(signedIn = true)
        BuildExampleCase.StartNeedsAuth -> BuildStartScreen(signedIn = false)
        BuildExampleCase.Preparing -> BuildPreparingScreen()
        BuildExampleCase.Building -> BuildProgressScreen()
        BuildExampleCase.ReadyToInstall -> BuildReadyScreen()
        BuildExampleCase.Failed -> BuildFailedScreen()
    }
}

@Composable
private fun BuildStartScreen(signedIn: Boolean) {
    IslandScaffold(
        topBar = { TopBarBackTitle(title = "Build") },
        footer = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.End,
            ) {
                if (signedIn) {
                    Button(
                        label = "Start build",
                        onClick = {},
                        variant = ButtonVariant.Primary,
                    )
                } else {
                    Button(
                        label = "Start build",
                        onClick = {},
                        variant = ButtonVariant.Disabled,
                        enabled = false,
                    )
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            InfoCard(
                title = "HelloCompose",
                subtitle = "com.example.hellocompose",
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

@Composable
private fun BuildPreparingScreen() {
    IslandScaffold(
        topBar = { TopBarBackTitle(title = "Build") },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    label = "Cancel",
                    onClick = {},
                    variant = ButtonVariant.Secondary,
                )
            }
        },
    ) {
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            LoadingIndicator(label = "Preparing workspace…")
        }
    }
}

@Composable
private fun BuildProgressScreen() {
    IslandScaffold(
        topBar = { TopBarBackTitle(title = "Build") },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    label = "Cancel",
                    onClick = {},
                    variant = ButtonVariant.Secondary,
                )
            }
        },
    ) {
        BuildProgressBody(
            title = "Building",
            message = "Compiling app sources…",
            fraction = 0.55f,
            phases = listOf(
                PhaseItem("Preparing", PhaseStatus.Complete),
                PhaseItem("Uploading", PhaseStatus.Complete),
                PhaseItem("Queued", PhaseStatus.Complete),
                PhaseItem("Building", PhaseStatus.Current),
                PhaseItem("Downloading", PhaseStatus.Upcoming),
                PhaseItem("Ready to install", PhaseStatus.Upcoming),
            ),
        )
    }
}

@Composable
private fun BuildReadyScreen() {
    IslandScaffold(
        topBar = { TopBarBackTitle(title = "Build") },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    label = "Install app",
                    onClick = {},
                    variant = ButtonVariant.Primary,
                )
            }
        },
    ) {
        BuildProgressBody(
            title = "Ready to install",
            message = "APK downloaded to this device.",
            fraction = 1f,
            phases = listOf(
                PhaseItem("Preparing", PhaseStatus.Complete),
                PhaseItem("Uploading", PhaseStatus.Complete),
                PhaseItem("Queued", PhaseStatus.Complete),
                PhaseItem("Building", PhaseStatus.Complete),
                PhaseItem("Downloading", PhaseStatus.Complete),
                PhaseItem("Ready to install", PhaseStatus.Complete),
            ),
        )
    }
}

@Composable
private fun BuildFailedScreen() {
    IslandScaffold(
        topBar = { TopBarBackTitle(title = "Build") },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                Button(
                    label = "Close",
                    onClick = {},
                    variant = ButtonVariant.Secondary,
                )
                Button(
                    label = "Retry",
                    onClick = {},
                    variant = ButtonVariant.Primary,
                )
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
                text = "Build failed",
                style = Typography.Headline.copy(color = Colors.Danger),
            )
            BasicText(
                text = "Compilation error in MainActivity.kt. Open the build log.",
                style = Typography.Body.copy(color = Colors.Muted),
            )
            BasicText(
                text = "via GitHub",
                style = Typography.Caption.copy(color = Colors.Muted),
            )
            PhaseList(
                phases = listOf(
                    PhaseItem("Preparing", PhaseStatus.Complete),
                    PhaseItem("Uploading", PhaseStatus.Complete),
                    PhaseItem("Queued", PhaseStatus.Complete),
                    PhaseItem("Building", PhaseStatus.Failed),
                    PhaseItem("Downloading", PhaseStatus.Upcoming),
                    PhaseItem("Ready to install", PhaseStatus.Upcoming),
                ),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ColumnScope.BuildProgressBody(
    title: String,
    message: String,
    fraction: Float,
    phases: List<PhaseItem>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BasicText(
            text = title,
            style = Typography.Headline.copy(color = Colors.Text),
        )
        BasicText(
            text = message,
            style = Typography.Body.copy(color = Colors.Muted),
        )
        BasicText(
            text = "via GitHub",
            style = Typography.Caption.copy(color = Colors.Muted),
        )
        ProgressBar(fraction = fraction)
        PhaseList(phases = phases)
    }
    Spacer(modifier = Modifier.weight(1f))
}
