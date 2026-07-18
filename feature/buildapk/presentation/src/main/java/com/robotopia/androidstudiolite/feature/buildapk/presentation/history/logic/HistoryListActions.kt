package com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic

import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryStore
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryRowUi
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal fun openHistoryMenu(
    row: BuildHistoryRowUi,
    uiState: MutableStateFlow<BuildHistoryUiState>,
) {
    uiState.update { it.copy(menuJobId = row.jobId) }
}

internal fun dismissHistoryMenu(uiState: MutableStateFlow<BuildHistoryUiState>) {
    uiState.update { it.copy(menuJobId = null) }
}

internal fun requestDeleteHistoryJob(
    row: BuildHistoryRowUi,
    uiState: MutableStateFlow<BuildHistoryUiState>,
) {
    uiState.update { it.copy(menuJobId = null, pendingDelete = row) }
}

internal fun cancelDeleteHistoryJob(uiState: MutableStateFlow<BuildHistoryUiState>) {
    uiState.update { it.copy(pendingDelete = null) }
}

internal fun confirmDeleteHistoryJob(
    uiState: MutableStateFlow<BuildHistoryUiState>,
    buildHistoryStore: BuildHistoryStore,
    scope: CoroutineScope,
) {
    val jobId = uiState.value.pendingDelete?.jobId
    uiState.update { it.copy(pendingDelete = null) }
    if (jobId != null) {
        scope.launch { buildHistoryStore.delete(jobId) }
    }
}
