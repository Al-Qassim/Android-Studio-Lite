package com.robotopia.androidstudiolite.feature.buildapk.presentation.progress

import androidx.lifecycle.ViewModel
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import kotlinx.coroutines.flow.MutableStateFlow

data class BuildProgressUiState(
    val jobId: String = "",
    val phase: BuildPhase = BuildPhase.Preparing,
    val message: String? = null,
    val apkLocalPath: String? = null,
    val error: String? = null,
    /** Which checklist step failed; only used when [phase] is Failed / [error] is set. */
    val failedAtPhase: BuildPhase? = null,
    /** Last non-terminal phase, used to place the failed checklist marker. */
    val lastActivePhase: BuildPhase = BuildPhase.Preparing,
    val providerName: String? = null,
    val logUrl: String? = null,
    val isInstalling: Boolean = false,
    val installError: String? = null,
)

/** Holds build progress UI state across configuration changes. */
class BuildProgressViewModel : ViewModel() {
    val uiState = MutableStateFlow(BuildProgressUiState())
}
