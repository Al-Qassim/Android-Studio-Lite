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
            message = "Waiting for a build slot…",
        ),
        TimedPhase(
            phase = BuildPhase.Uploading,
            durationMs = 1_500L,
            message = "Uploading project files…",
        ),
        TimedPhase(
            phase = BuildPhase.Building,
            durationMs = 3_000L,
            message = "Building on remote workers…",
        ),
        TimedPhase(
            phase = BuildPhase.Downloading,
            durationMs = 1_500L,
            message = "Downloading APK…",
        ),
    )

    val totalDurationMs: Long = timedPhases.sumOf { it.durationMs }

    fun phaseAtElapsed(elapsedMs: Long): BuildPhase {
        if (elapsedMs < 0) return BuildPhase.Queued
        var remaining = elapsedMs
        for (timed in timedPhases) {
            if (remaining < timed.durationMs) return timed.phase
            remaining -= timed.durationMs
        }
        return BuildPhase.ReadyToInstall
    }

    fun messageForPhase(phase: BuildPhase): String? = when (phase) {
        BuildPhase.ReadyToInstall -> "Demo APK ready to install"
        BuildPhase.Failed -> null
        BuildPhase.Cancelled -> null
        else -> timedPhases.firstOrNull { it.phase == phase }?.message
    }

    fun progressFraction(elapsedMs: Long): Float {
        if (elapsedMs <= 0) return 0f
        if (elapsedMs >= totalDurationMs) return 1f
        return (elapsedMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
    }
}
