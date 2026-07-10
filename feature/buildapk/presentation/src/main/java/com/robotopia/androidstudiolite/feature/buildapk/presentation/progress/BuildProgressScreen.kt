package com.robotopia.androidstudiolite.feature.buildapk.presentation.progress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BuildProgressScreen(
    jobId: String,
    buildService: BuildService,
    onReadyToInstall: (apkLocalPath: String) -> Unit,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)?,
    viewModel: BuildProgressViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(jobId, buildService) {
        buildService.observeBuild(jobId).collect { progress ->
            viewModel.applyProgress(progress)
        }
    }

    BuildProgressContent(
        state = state,
        onDismiss = onDismiss,
        onCancel = {
            scope.launch {
                buildService.cancelBuild(jobId)
            }
        },
        onInstall = { apkPath ->
            onReadyToInstall(apkPath)
        },
        onRetry = onRetry,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun BuildProgressScreenPreview() {
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
