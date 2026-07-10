package com.robotopia.androidstudiolite.feature.buildapk.presentation.progress

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640, name = "Build · queued")
@Composable
private fun BuildProgressQueuedPreview() {
    BuildProgressContent(
        state = BuildProgressUiState(
            phase = BuildPhase.Queued,
            message = "Simulating queue…",
            progressFraction = 0.05f,
        ),
        onDismiss = {},
        onCancel = {},
        onInstall = {},
        onRetry = null,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640, name = "Build · building")
@Composable
private fun BuildProgressBuildingPreview() {
    BuildProgressContent(
        state = BuildProgressUiState(
            phase = BuildPhase.Building,
            message = "Simulating remote build…",
            progressFraction = 0.55f,
        ),
        onDismiss = {},
        onCancel = {},
        onInstall = {},
        onRetry = null,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640, name = "Build · ready")
@Composable
private fun BuildProgressReadyPreview() {
    BuildProgressContent(
        state = BuildProgressUiState(
            phase = BuildPhase.ReadyToInstall,
            message = "Demo APK ready to install",
            progressFraction = 1f,
            apkLocalPath = "/cache/demo.apk",
        ),
        onDismiss = {},
        onCancel = {},
        onInstall = {},
        onRetry = null,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640, name = "Build · failed")
@Composable
private fun BuildProgressFailedPreview() {
    BuildProgressContent(
        state = BuildProgressUiState(
            phase = BuildPhase.Failed,
            error = "Could not prepare demo APK",
        ),
        onDismiss = {},
        onCancel = {},
        onInstall = {},
        onRetry = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640, name = "Build · cancelled")
@Composable
private fun BuildProgressCancelledPreview() {
    BuildProgressContent(
        state = BuildProgressUiState(phase = BuildPhase.Cancelled),
        onDismiss = {},
        onCancel = {},
        onInstall = {},
        onRetry = null,
    )
}
