package com.robotopia.androidstudiolite.feature.buildapk.presentation.progress

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase

internal data class BuildProgressPreviewCase(
    private val label: String,
    val state: BuildProgressUiState,
    val onRetry: (() -> Unit)?,
) {
    override fun toString(): String = label
}

internal class BuildProgressPreviewProvider : PreviewParameterProvider<BuildProgressPreviewCase> {
    override fun getDisplayName(index: Int): String = values.toList()[index].toString()

    override val values = sequenceOf(
        BuildProgressPreviewCase(
            label = "preparing",
            state = BuildProgressUiState(
                phase = BuildPhase.Preparing,
                message = "Preparing workspace…",
                progressFraction = 0.05f,
                providerName = "GitHub",
            ),
            onRetry = null,
        ),
        BuildProgressPreviewCase(
            label = "queued",
            state = BuildProgressUiState(
                phase = BuildPhase.Queued,
                message = "Waiting in queue…",
                progressFraction = 0.35f,
                providerName = "GitHub",
            ),
            onRetry = null,
        ),
        BuildProgressPreviewCase(
            label = "uploading",
            state = BuildProgressUiState(
                phase = BuildPhase.Uploading,
                message = "Uploading project sources…",
                progressFraction = 0.2f,
                providerName = "GitHub",
            ),
            onRetry = null,
        ),
        BuildProgressPreviewCase(
            label = "building",
            state = BuildProgressUiState(
                phase = BuildPhase.Building,
                message = "Building APK remotely…",
                progressFraction = 0.55f,
                providerName = "GitHub",
            ),
            onRetry = null,
        ),
        BuildProgressPreviewCase(
            label = "downloading",
            state = BuildProgressUiState(
                phase = BuildPhase.Downloading,
                message = "Downloading APK…",
                progressFraction = 0.85f,
                providerName = "GitHub",
            ),
            onRetry = null,
        ),
        BuildProgressPreviewCase(
            label = "ready",
            state = BuildProgressUiState(
                phase = BuildPhase.ReadyToInstall,
                message = "APK ready to install",
                progressFraction = 1f,
                apkLocalPath = "/cache/demo.apk",
                providerName = "GitHub",
            ),
            onRetry = null,
        ),
        BuildProgressPreviewCase(
            label = "failed · with log",
            state = BuildProgressUiState(
                phase = BuildPhase.Failed,
                error = "Build failed. Open the GitHub Actions log.",
                failedAtPhase = BuildPhase.Building,
                providerName = "GitHub",
                logUrl = "https://github.com/",
            ),
            onRetry = {},
        ),
        BuildProgressPreviewCase(
            label = "failed · no log",
            state = BuildProgressUiState(
                phase = BuildPhase.Failed,
                error = "Build failed. Open the GitHub Actions log.",
                failedAtPhase = BuildPhase.Building,
                providerName = "GitHub",
            ),
            onRetry = {},
        ),
        BuildProgressPreviewCase(
            label = "cancelled",
            state = BuildProgressUiState(
                phase = BuildPhase.Cancelled,
                message = "No APK was produced. You can start a new build when you're ready.",
            ),
            onRetry = null,
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun BuildProgressPreview(
    @PreviewParameter(BuildProgressPreviewProvider::class) case: BuildProgressPreviewCase,
) {
    BuildProgressContent(
        state = case.state,
        onDismiss = {},
        onCancel = {},
        onInstall = {},
        onRetry = case.onRetry,
        onViewLog = if (case.state.logUrl != null) ({}) else null,
    )
}
