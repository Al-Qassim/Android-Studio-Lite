package com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryStore
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "BuildHistoryDetail"
private const val GENERIC_LOAD_ERROR = "Couldn't load this build. Try again."
internal const val HISTORY_DETAIL_NOT_FOUND =
    "This build was removed from history."

internal suspend fun collectHistoryDetail(
    jobId: String,
    buildHistoryStore: BuildHistoryStore,
    uiState: MutableStateFlow<BuildHistoryDetailUiState>,
) {
    uiState.update { it.copy(isLoading = true, loadError = null) }
    try {
        buildHistoryStore.observeJob(jobId).collect { item ->
            if (item == null) {
                uiState.update {
                    it.copy(isLoading = false, loadError = HISTORY_DETAIL_NOT_FOUND)
                }
                return@collect
            }
            uiState.update { item.toDetailUi(it) }
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
