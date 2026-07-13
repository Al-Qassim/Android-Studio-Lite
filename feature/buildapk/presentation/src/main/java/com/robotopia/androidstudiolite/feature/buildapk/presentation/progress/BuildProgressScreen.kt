package com.robotopia.androidstudiolite.feature.buildapk.presentation.progress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
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
    val uriHandler = LocalUriHandler.current

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
        onViewLog = { url ->
            runCatching { uriHandler.openUri(url) }
        },
    )
}
