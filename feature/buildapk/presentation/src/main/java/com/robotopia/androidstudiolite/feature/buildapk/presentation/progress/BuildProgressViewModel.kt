package com.robotopia.androidstudiolite.feature.buildapk.presentation.progress

import androidx.lifecycle.ViewModel
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
    private val _uiState = MutableStateFlow(BuildProgressUiState())
    val uiState: StateFlow<BuildProgressUiState> = _uiState.asStateFlow()

    fun applyProgress(progress: BuildProgress) {
        _uiState.update {
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

internal fun BuildProgress.displayProgressFraction(): Float = when (phase) {
    BuildPhase.Queued -> 0.05f
    BuildPhase.Uploading -> 0.25f
    BuildPhase.Building -> 0.55f
    BuildPhase.Downloading -> 0.85f
    BuildPhase.ReadyToInstall -> 1f
    BuildPhase.Failed, BuildPhase.Cancelled -> 0f
}
