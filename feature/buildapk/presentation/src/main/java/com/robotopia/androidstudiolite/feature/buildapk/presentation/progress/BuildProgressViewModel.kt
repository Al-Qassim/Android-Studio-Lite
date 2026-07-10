package com.robotopia.androidstudiolite.feature.buildapk.presentation.progress

import androidx.lifecycle.ViewModel
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildProgress
import kotlinx.coroutines.flow.MutableStateFlow

data class BuildProgressUiState(
    val jobId: String = "",
    val phase: BuildPhase = BuildPhase.Queued,
    val message: String? = null,
    val progressFraction: Float = 0f,
    val apkLocalPath: String? = null,
    val error: String? = null,
    val isDemoApkNoticeVisible: Boolean = true,
)

/** Holds build progress UI state across configuration changes. No service calls. */
class BuildProgressViewModel : ViewModel() {
    val uiState = MutableStateFlow(BuildProgressUiState())

    fun applyProgress(progress: BuildProgress, progressFraction: Float) {
        uiState.value = BuildProgressUiState(
            jobId = progress.jobId,
            phase = progress.phase,
            message = progress.message,
            progressFraction = progressFraction,
            apkLocalPath = progress.apkLocalPath,
            error = progress.error,
            isDemoApkNoticeVisible = true,
        )
    }
}
