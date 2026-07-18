package com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic

import com.robotopia.androidstudiolite.feature.buildapk.model.BuildHistoryItem
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryDetailUiState
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryRowUi
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.isActiveHistoryPhase
import java.text.DateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

internal fun BuildHistoryItem.toRowUi(): BuildHistoryRowUi =
    BuildHistoryRowUi(
        jobId = jobId,
        projectName = projectName,
        phase = phase,
        timeLabel = formatHistoryTime(
            startedAtEpochMs = startedAtEpochMs,
            finishedAtEpochMs = finishedAtEpochMs,
            phase = phase,
        ),
    )

internal fun BuildHistoryItem.toDetailUi(current: BuildHistoryDetailUiState): BuildHistoryDetailUiState =
    current.copy(
        isLoading = false,
        loadError = null,
        projectName = projectName,
        phase = phase,
        providerName = providerName,
        lastActivePhase = lastActivePhase,
        startedLabel = formatAbsoluteTime(startedAtEpochMs),
        finishedLabel = finishedAtEpochMs?.let { formatAbsoluteTime(it) },
        message = message,
        error = error,
        logUrl = logUrl,
        apkLocalPath = apkLocalPath,
        canInstall = phase == BuildPhase.ReadyToInstall && !apkLocalPath.isNullOrBlank(),
    )

private fun formatHistoryTime(
    startedAtEpochMs: Long,
    finishedAtEpochMs: Long?,
    phase: BuildPhase,
): String {
    val epoch = if (phase.isActiveHistoryPhase()) {
        startedAtEpochMs
    } else {
        finishedAtEpochMs ?: startedAtEpochMs
    }
    val prefix = if (phase.isActiveHistoryPhase()) "Started" else "Finished"
    return "$prefix ${relativeTimeLabel(epoch)}"
}

private fun formatAbsoluteTime(epochMs: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(epochMs))

private fun relativeTimeLabel(epochMs: Long): String {
    val deltaMs = (System.currentTimeMillis() - epochMs).coerceAtLeast(0L)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(deltaMs)
    val hours = TimeUnit.MILLISECONDS.toHours(deltaMs)
    val days = TimeUnit.MILLISECONDS.toDays(deltaMs)
    return when {
        minutes < 1L -> "just now"
        minutes < 60L -> "$minutes min ago"
        hours < 24L -> if (hours == 1L) "1 hour ago" else "$hours hours ago"
        days == 1L -> "yesterday"
        else -> "$days days ago"
    }
}
