package com.robotopia.androidstudiolite.feature.projects.presentation.list

import java.util.concurrent.TimeUnit
import kotlin.math.max

internal fun formatOpenedMeta(lastOpenedAt: Long?, now: Long = System.currentTimeMillis()): String {
    if (lastOpenedAt == null) return "Never opened"
    val elapsedMs = max(0L, now - lastOpenedAt)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMs)
    val hours = TimeUnit.MILLISECONDS.toHours(elapsedMs)
    val days = TimeUnit.MILLISECONDS.toDays(elapsedMs)
    return when {
        minutes < 1 -> "Opened just now"
        minutes < 60 -> "Opened ${minutes}m ago"
        hours < 24 -> "Opened ${hours}h ago"
        days == 1L -> "Opened yesterday"
        else -> "Opened $days days ago"
    }
}
