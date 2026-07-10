package com.robotopia.androidstudiolite.feature.buildapk.presentation.progress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
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
        collectBuildProgress(
            jobId = jobId,
            buildService = buildService,
            uiState = viewModel.uiState,
        )
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

private suspend fun collectBuildProgress(
    jobId: String,
    buildService: BuildService,
    uiState: MutableStateFlow<BuildProgressUiState>,
) {
    buildService.observeBuild(jobId).collect { progress ->
        uiState.update {
            BuildProgressUiState(
                jobId = progress.jobId,
                phase = progress.phase,
                message = progress.message,
                progressFraction = progress.displayProgressFraction(),
                apkLocalPath = progress.apkLocalPath,
                error = progress.error,
                isDemoApkNoticeVisible = true,
            )
        }
    }
}

private fun BuildProgress.displayProgressFraction(): Float = when (phase) {
    BuildPhase.Queued -> 0.05f
    BuildPhase.Uploading -> 0.25f
    BuildPhase.Building -> 0.55f
    BuildPhase.Downloading -> 0.85f
    BuildPhase.ReadyToInstall -> 1f
    BuildPhase.Failed, BuildPhase.Cancelled -> 0f
}
