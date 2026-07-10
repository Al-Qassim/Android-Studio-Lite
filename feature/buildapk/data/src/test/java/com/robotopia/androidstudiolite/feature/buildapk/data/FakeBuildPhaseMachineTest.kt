package com.robotopia.androidstudiolite.feature.buildapk.data

import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeBuildPhaseMachineTest {

    @Test
    fun timedPhases_totalDuration_isAboutSevenSeconds() {
        assertEquals(7_000L, FakeBuildPhaseMachine.totalDurationMs)
        assertEquals(4, FakeBuildPhaseMachine.timedPhases.size)
    }

    @Test
    fun timedPhases_walkQueuedThroughDownloading() {
        val phases = FakeBuildPhaseMachine.timedPhases.map { it.phase }
        assertEquals(
            listOf(
                BuildPhase.Queued,
                BuildPhase.Uploading,
                BuildPhase.Building,
                BuildPhase.Downloading,
            ),
            phases,
        )
    }

    @Test
    fun messageForPhase_returnsHonestDemoCopy() {
        FakeBuildPhaseMachine.timedPhases.forEach { timed ->
            assertEquals(timed.message, FakeBuildPhaseMachine.messageForPhase(timed.phase))
        }
        assertEquals(
            "Demo APK ready to install",
            FakeBuildPhaseMachine.messageForPhase(BuildPhase.ReadyToInstall),
        )
        assertEquals("Simulating upload…", FakeBuildPhaseMachine.messageForPhase(BuildPhase.Uploading))
        assertEquals(
            "Preparing bundled demo APK…",
            FakeBuildPhaseMachine.messageForPhase(BuildPhase.Downloading),
        )
    }
}
