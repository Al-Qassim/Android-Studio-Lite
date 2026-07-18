package com.robotopia.androidstudiolite.feature.buildapk.presentation.history

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstaller
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryStore
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic.collectHistoryDetail
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic.openHistoryLogUrl
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic.requestHistoryDetailInstall
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BuildHistoryDetailScreen(
    jobId: String,
    buildHistoryStore: BuildHistoryStore,
    apkInstaller: ApkInstaller,
    onDismiss: () -> Unit,
    viewModel: BuildHistoryDetailViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var loadGeneration by rememberSaveable { mutableIntStateOf(0) }

    BackHandler(onBack = onDismiss)

    LaunchedEffect(jobId, buildHistoryStore, loadGeneration) {
        collectHistoryDetail(
            jobId = jobId,
            buildHistoryStore = buildHistoryStore,
            uiState = viewModel.uiState,
        )
    }

    BuildHistoryDetailContent(
        state = state,
        onBackClick = onDismiss,
        onInstall = {
            requestHistoryDetailInstall(
                apkInstaller = apkInstaller,
                uiState = viewModel.uiState,
            )
        },
        onViewLog = { url -> openHistoryLogUrl(context, url) },
        onRetryLoad = { loadGeneration += 1 },
    )
}
