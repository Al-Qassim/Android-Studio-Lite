package com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryStore
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "BuildHistory"
private const val GENERIC_LOAD_ERROR = "Couldn't load build history. Try again."

internal suspend fun collectBuildHistory(
    projectIdFilter: String?,
    buildHistoryStore: BuildHistoryStore,
    uiState: MutableStateFlow<BuildHistoryUiState>,
) {
    uiState.update { it.copy(isLoading = true, loadError = null) }
    try {
        buildHistoryStore.observeHistory(projectIdFilter).collect { items ->
            uiState.update { state ->
                state.copy(
                    jobs = items.map { it.toRowUi() },
                    isLoading = false,
                    loadError = null,
                    menuJobId = state.menuJobId?.takeIf { id -> items.any { it.jobId == id } },
                    pendingDelete = state.pendingDelete?.takeIf { pending ->
                        items.any { it.jobId == pending.jobId }
                    },
                )
            }
        }
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        uiState.update {
            it.copy(
                isLoading = false,
                loadError = error.userMessageOrNull(TAG) ?: GENERIC_LOAD_ERROR,
            )
        }
    }
}
