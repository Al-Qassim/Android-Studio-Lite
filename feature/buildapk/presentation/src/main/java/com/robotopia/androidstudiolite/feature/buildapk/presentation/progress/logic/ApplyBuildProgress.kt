package com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.logic

import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildProgress
import com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.BuildProgressUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal fun MutableStateFlow<BuildProgressUiState>.applyBuildProgress(
    progress: BuildProgress,
) {
    update { previous ->
        val lastActivePhase =
            if (progress.phase in setOf(BuildPhase.Failed, BuildPhase.Cancelled)) {
                previous.lastActivePhase
            } else {
                progress.phase
            }
        val isFailed = progress.phase == BuildPhase.Failed || progress.error != null
        val sameJob = previous.jobId.isEmpty() || previous.jobId == progress.jobId
        BuildProgressUiState(
            jobId = progress.jobId,
            phase = progress.phase,
            message = progress.message,
            apkLocalPath = progress.apkLocalPath,
            error = progress.error,
            failedAtPhase = if (isFailed) lastActivePhase else null,
            lastActivePhase = lastActivePhase,
            providerName = progress.providerName,
            logUrl = progress.logUrl,
            isInstalling = if (sameJob) previous.isInstalling else false,
            installError = if (sameJob) previous.installError else null,
        )
    }
}
