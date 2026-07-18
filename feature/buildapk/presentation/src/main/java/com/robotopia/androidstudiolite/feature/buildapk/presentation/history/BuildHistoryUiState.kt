package com.robotopia.androidstudiolite.feature.buildapk.presentation.history

import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase

data class BuildHistoryRowUi(
    val jobId: String,
    val projectName: String,
    val phase: BuildPhase,
    /** Relative or absolute time already formatted for display. */
    val timeLabel: String,
)

data class BuildHistoryUiState(
    val jobs: List<BuildHistoryRowUi> = emptyList(),
    val menuJobId: String? = null,
    val pendingDelete: BuildHistoryRowUi? = null,
    val isLoading: Boolean = false,
    val loadError: String? = null,
)

data class BuildHistoryDetailUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val projectName: String = "",
    val phase: BuildPhase = BuildPhase.ReadyToInstall,
    /** Last non-terminal phase reached — used for failed/cancelled phase list. */
    val lastActivePhase: BuildPhase? = null,
    val providerName: String? = null,
    val startedLabel: String = "",
    val finishedLabel: String? = null,
    val message: String? = null,
    val error: String? = null,
    val logUrl: String? = null,
    val apkLocalPath: String? = null,
    val canInstall: Boolean = false,
    val isInstalling: Boolean = false,
    val installError: String? = null,
)

internal fun BuildPhase.toHistoryLabel(): String = when (this) {
    BuildPhase.Preparing -> "Preparing"
    BuildPhase.Uploading -> "Uploading"
    BuildPhase.Queued -> "Queued"
    BuildPhase.Building -> "Building"
    BuildPhase.Downloading -> "Downloading"
    BuildPhase.ReadyToInstall -> "Ready to install"
    BuildPhase.Failed -> "Failed"
    BuildPhase.Cancelled -> "Cancelled"
}

internal fun BuildPhase.isActiveHistoryPhase(): Boolean = when (this) {
    BuildPhase.ReadyToInstall,
    BuildPhase.Failed,
    BuildPhase.Cancelled,
    -> false
    else -> true
}
