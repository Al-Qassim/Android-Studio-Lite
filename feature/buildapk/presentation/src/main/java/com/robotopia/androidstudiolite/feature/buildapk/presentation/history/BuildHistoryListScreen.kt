package com.robotopia.androidstudiolite.feature.buildapk.presentation.history

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryStore
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic.cancelDeleteHistoryJob
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic.collectBuildHistory
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic.confirmDeleteHistoryJob
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic.dismissHistoryMenu
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic.openHistoryMenu
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic.requestDeleteHistoryJob
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun BuildHistoryListScreen(
    projectIdFilter: String?,
    buildHistoryStore: BuildHistoryStore,
    onDismiss: () -> Unit,
    onOpenJob: (BuildHistoryRowUi) -> Unit,
    viewModel: BuildHistoryViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var loadGeneration by rememberSaveable { mutableIntStateOf(0) }

    BackHandler(onBack = onDismiss)

    LaunchedEffect(projectIdFilter, buildHistoryStore, loadGeneration) {
        collectBuildHistory(
            projectIdFilter = projectIdFilter,
            buildHistoryStore = buildHistoryStore,
            uiState = viewModel.uiState,
        )
    }

    BuildHistoryContent(
        state = state,
        onBackClick = onDismiss,
        onJobClick = onOpenJob,
        onMenuOpen = { row -> openHistoryMenu(row, viewModel.uiState) },
        onMenuDismiss = { dismissHistoryMenu(viewModel.uiState) },
        onDeleteMenuClick = { row -> requestDeleteHistoryJob(row, viewModel.uiState) },
        onDeleteCancel = { cancelDeleteHistoryJob(viewModel.uiState) },
        onDeleteConfirm = {
            confirmDeleteHistoryJob(
                uiState = viewModel.uiState,
                buildHistoryStore = buildHistoryStore,
                scope = scope,
            )
        },
        onRetryLoad = { loadGeneration += 1 },
    )
}
