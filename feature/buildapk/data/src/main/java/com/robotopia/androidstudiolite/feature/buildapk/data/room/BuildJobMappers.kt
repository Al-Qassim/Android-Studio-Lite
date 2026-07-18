package com.robotopia.androidstudiolite.feature.buildapk.data.room

import com.robotopia.androidstudiolite.feature.buildapk.model.BuildHistoryItem
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildProgress

internal fun BuildJobEntity.toHistoryItem(): BuildHistoryItem =
    BuildHistoryItem(
        jobId = jobId,
        projectId = projectId,
        projectName = projectName,
        phase = phase.toBuildPhase(),
        startedAtEpochMs = startedAtEpochMs,
        finishedAtEpochMs = finishedAtEpochMs,
        message = message,
        error = error,
        apkLocalPath = apkLocalPath,
        logUrl = logUrl,
        providerName = providerName,
        lastActivePhase = lastActivePhase?.toBuildPhase(),
    )

internal fun BuildJobEntity.toProgress(): BuildProgress =
    BuildProgress(
        jobId = jobId,
        phase = phase.toBuildPhase(),
        message = message,
        apkLocalPath = apkLocalPath,
        error = error,
        providerName = providerName,
        logUrl = logUrl,
    )

internal fun BuildPhase.toStorageName(): String = name

internal fun String.toBuildPhase(): BuildPhase =
    runCatching { BuildPhase.valueOf(this) }.getOrDefault(BuildPhase.Failed)
