package com.robotopia.androidstudiolite.feature.buildapk.presentation

import com.robotopia.androidstudiolite.designsystem.component.PhaseItem
import com.robotopia.androidstudiolite.designsystem.component.PhaseStatus
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase

internal val BuildProgressPhases = listOf(
    BuildPhase.Preparing,
    BuildPhase.Uploading,
    BuildPhase.Queued,
    BuildPhase.Building,
    BuildPhase.Downloading,
    BuildPhase.ReadyToInstall,
)

internal fun buildPhaseItems(
    currentPhase: BuildPhase,
    failedAtPhase: BuildPhase? = null,
    cancelledAtPhase: BuildPhase? = null,
): List<PhaseItem> =
    BuildProgressPhases.map { phase ->
        PhaseItem(
            label = buildPhaseLabel(phase),
            status = buildPhaseStatus(
                phase = phase,
                current = currentPhase,
                failedAtPhase = failedAtPhase,
                cancelledAtPhase = cancelledAtPhase,
            ),
        )
    }

internal fun buildPhaseLabel(phase: BuildPhase): String = when (phase) {
    BuildPhase.Preparing -> "Preparing"
    BuildPhase.Uploading -> "Uploading"
    BuildPhase.Queued -> "Queued"
    BuildPhase.Building -> "Building"
    BuildPhase.Downloading -> "Downloading"
    BuildPhase.ReadyToInstall -> "Ready to install"
    BuildPhase.Failed -> "Failed"
    BuildPhase.Cancelled -> "Cancelled"
}

private fun buildPhaseStatus(
    phase: BuildPhase,
    current: BuildPhase,
    failedAtPhase: BuildPhase?,
    cancelledAtPhase: BuildPhase?,
): PhaseStatus {
    val phaseIndex = BuildProgressPhases.indexOf(phase)
    if (failedAtPhase != null) {
        val failedIndex = BuildProgressPhases.indexOf(failedAtPhase).coerceAtLeast(0)
        return when {
            phaseIndex < failedIndex -> PhaseStatus.Complete
            phaseIndex == failedIndex -> PhaseStatus.Failed
            else -> PhaseStatus.Upcoming
        }
    }
    if (cancelledAtPhase != null) {
        val cancelledIndex = BuildProgressPhases.indexOf(cancelledAtPhase).coerceAtLeast(0)
        return when {
            phaseIndex <= cancelledIndex -> PhaseStatus.Complete
            else -> PhaseStatus.Upcoming
        }
    }
    val currentIndex = BuildProgressPhases.indexOf(current).coerceAtLeast(0)
    return when {
        current == BuildPhase.ReadyToInstall && phaseIndex <= currentIndex -> PhaseStatus.Complete
        phaseIndex < currentIndex -> PhaseStatus.Complete
        phaseIndex == currentIndex -> PhaseStatus.Current
        else -> PhaseStatus.Upcoming
    }
}
