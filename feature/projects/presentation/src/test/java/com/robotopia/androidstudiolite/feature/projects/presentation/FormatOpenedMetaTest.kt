package com.robotopia.androidstudiolite.feature.projects.presentation

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class FormatOpenedMetaTest {

    @Test
    fun neverOpened() {
        assertEquals("Never opened", formatOpenedMeta(null))
    }

    @Test
    fun justNow() {
        val now = 1_000_000L
        assertEquals("Opened just now", formatOpenedMeta(now - 10_000, now))
    }

    @Test
    fun hoursAgo() {
        val now = 1_000_000L
        assertEquals(
            "Opened 2h ago",
            formatOpenedMeta(now - TimeUnit.HOURS.toMillis(2), now),
        )
    }

    @Test
    fun yesterday() {
        val now = 1_000_000L
        assertEquals(
            "Opened yesterday",
            formatOpenedMeta(now - TimeUnit.DAYS.toMillis(1), now),
        )
    }
}
