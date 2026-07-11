package com.robotopia.androidstudiolite.feature.buildapk.data

import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase

/**
 * Pure timed phase sequence for the v0.1 fake build (~7s total).
 * Real cloud builds will replace this; keep logic testable without Android.
 */
internal object FakeBuildPhaseMachine {

    data class TimedPhase(
        val phase: BuildPhase,
        val durationMs: Long,
        val message: String,
    )

    val timedPhases: List<TimedPhase> = listOf(
        TimedPhase(
            phase = BuildPhase.Queued,
            durationMs = 1_000L,
            message = "Waiting in queue…",
        ),
        TimedPhase(
            phase = BuildPhase.Uploading,
            durationMs = 1_500L,
            message = "Uploading project sources…",
        ),
        TimedPhase(
            phase = BuildPhase.Building,
            durationMs = 3_000L,
            message = "Building APK remotely…",
        ),
        TimedPhase(
            phase = BuildPhase.Downloading,
            durationMs = 1_500L,
            message = "Downloading APK…",
        ),
    )

    val totalDurationMs: Long = timedPhases.sumOf { it.durationMs }

    fun messageForPhase(phase: BuildPhase): String? = when (phase) {
        BuildPhase.ReadyToInstall -> "APK ready to install"
        BuildPhase.Failed -> null
        BuildPhase.Cancelled -> null
        else -> timedPhases.firstOrNull { it.phase == phase }?.message
    }
}
