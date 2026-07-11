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
            label = "queued",
            state = BuildProgressUiState(
                phase = BuildPhase.Queued,
                message = "Waiting in queue…",
                progressFraction = 0.05f,
            ),
            onRetry = null,
        ),
        BuildProgressPreviewCase(
            label = "uploading",
            state = BuildProgressUiState(
                phase = BuildPhase.Uploading,
                message = "Uploading project sources…",
                progressFraction = 0.25f,
            ),
            onRetry = null,
        ),
        BuildProgressPreviewCase(
            label = "building",
            state = BuildProgressUiState(
                phase = BuildPhase.Building,
                message = "Building APK remotely…",
                progressFraction = 0.55f,
            ),
            onRetry = null,
        ),
        BuildProgressPreviewCase(
            label = "downloading",
            state = BuildProgressUiState(
                phase = BuildPhase.Downloading,
                message = "Downloading APK…",
                progressFraction = 0.85f,
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
            ),
            onRetry = null,
        ),
        BuildProgressPreviewCase(
            label = "ready · install disabled",
            state = BuildProgressUiState(
                phase = BuildPhase.ReadyToInstall,
                message = "APK ready to install",
                progressFraction = 1f,
                apkLocalPath = null,
            ),
            onRetry = null,
        ),
        BuildProgressPreviewCase(
            label = "failed · building",
            state = BuildProgressUiState(
                phase = BuildPhase.Failed,
                error = "Building failed: Task :app:compileDebugKotlin failed with 2 errors.",
                failedAtPhase = BuildPhase.Building,
            ),
            onRetry = {},
        ),
        BuildProgressPreviewCase(
            label = "failed · downloading",
            state = BuildProgressUiState(
                phase = BuildPhase.Failed,
                error = "Could not prepare demo APK",
                failedAtPhase = BuildPhase.Downloading,
            ),
            onRetry = {},
        ),
        BuildProgressPreviewCase(
            label = "failed · no retry",
            state = BuildProgressUiState(
                phase = BuildPhase.Failed,
                error = "Build not found",
                failedAtPhase = BuildPhase.Queued,
            ),
            onRetry = null,
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
    )
}
