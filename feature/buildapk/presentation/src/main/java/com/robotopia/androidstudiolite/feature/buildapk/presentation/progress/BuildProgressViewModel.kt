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
    val phase: BuildPhase = BuildPhase.Preparing,
    val message: String? = null,
    val progressFraction: Float = 0f,
    val apkLocalPath: String? = null,
    val error: String? = null,
    /** Which checklist step failed; only used when [phase] is Failed / [error] is set. */
    val failedAtPhase: BuildPhase? = null,
    val providerName: String? = null,
    val logUrl: String? = null,
)

/** Holds build progress UI state across configuration changes. No service calls. */
class BuildProgressViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BuildProgressUiState())
    val uiState: StateFlow<BuildProgressUiState> = _uiState.asStateFlow()

    private var lastActivePhase: BuildPhase = BuildPhase.Preparing

    fun applyProgress(progress: BuildProgress) {
        if (progress.phase !in setOf(BuildPhase.Failed, BuildPhase.Cancelled)) {
            lastActivePhase = progress.phase
        }
        val isFailed = progress.phase == BuildPhase.Failed || progress.error != null
        _uiState.update {
            BuildProgressUiState(
                jobId = progress.jobId,
                phase = progress.phase,
                message = progress.message,
                progressFraction = progress.displayProgressFraction(),
                apkLocalPath = progress.apkLocalPath,
                error = progress.error,
                failedAtPhase = if (isFailed) lastActivePhase else null,
                providerName = progress.providerName,
                logUrl = progress.logUrl,
            )
        }
    }
}

internal fun BuildProgress.displayProgressFraction(): Float = when (phase) {
    BuildPhase.Preparing -> 0.05f
    BuildPhase.Uploading -> 0.2f
    BuildPhase.Queued -> 0.35f
    BuildPhase.Building -> 0.55f
    BuildPhase.Downloading -> 0.85f
    BuildPhase.ReadyToInstall -> 1f
    BuildPhase.Failed, BuildPhase.Cancelled -> 0f
}
