package com.robotopia.androidstudiolite.feature.buildapk.data

import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeBuildPhaseMachineTest {

    @Test
    fun timedPhases_totalDuration_isAboutSevenSeconds() {
        assertEquals(7_000L, FakeBuildPhaseMachine.totalDurationMs)
        assertEquals(4, FakeBuildPhaseMachine.timedPhases.size)
    }

    @Test
    fun phaseAtElapsed_walksQueuedThroughDownloading() {
        assertEquals(BuildPhase.Queued, FakeBuildPhaseMachine.phaseAtElapsed(0))
        assertEquals(BuildPhase.Queued, FakeBuildPhaseMachine.phaseAtElapsed(999))
        assertEquals(BuildPhase.Uploading, FakeBuildPhaseMachine.phaseAtElapsed(1_000))
        assertEquals(BuildPhase.Uploading, FakeBuildPhaseMachine.phaseAtElapsed(2_499))
        assertEquals(BuildPhase.Building, FakeBuildPhaseMachine.phaseAtElapsed(2_500))
        assertEquals(BuildPhase.Building, FakeBuildPhaseMachine.phaseAtElapsed(5_499))
        assertEquals(BuildPhase.Downloading, FakeBuildPhaseMachine.phaseAtElapsed(5_500))
        assertEquals(BuildPhase.Downloading, FakeBuildPhaseMachine.phaseAtElapsed(6_999))
        assertEquals(BuildPhase.ReadyToInstall, FakeBuildPhaseMachine.phaseAtElapsed(7_000))
        assertEquals(BuildPhase.ReadyToInstall, FakeBuildPhaseMachine.phaseAtElapsed(10_000))
    }

    @Test
    fun progressFraction_increasesMonotonicallyUntilComplete() {
        val checkpoints = listOf(0L, 500L, 1_000L, 2_500L, 5_500L, 7_000L, 8_000L)
        var previous = -1f
        checkpoints.forEach { elapsed ->
            val fraction = FakeBuildPhaseMachine.progressFraction(elapsed)
            assertTrue(fraction >= previous)
            previous = fraction
        }
        assertEquals(0f, FakeBuildPhaseMachine.progressFraction(0))
        assertEquals(1f, FakeBuildPhaseMachine.progressFraction(7_000))
        assertEquals(1f, FakeBuildPhaseMachine.progressFraction(9_000))
    }

    @Test
    fun messageForPhase_returnsCopyForActivePhases() {
        FakeBuildPhaseMachine.timedPhases.forEach { timed ->
            assertEquals(timed.message, FakeBuildPhaseMachine.messageForPhase(timed.phase))
        }
        assertEquals(
            "Demo APK ready to install",
            FakeBuildPhaseMachine.messageForPhase(BuildPhase.ReadyToInstall),
        )
    }
}
