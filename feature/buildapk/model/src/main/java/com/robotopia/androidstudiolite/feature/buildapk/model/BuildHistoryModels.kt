package com.robotopia.androidstudiolite.feature.buildapk.model

data class BuildHistoryItem(
    val jobId: String,
    val projectId: String,
    val projectName: String,
    val phase: BuildPhase,
    val startedAtEpochMs: Long,
    val finishedAtEpochMs: Long? = null,
    val message: String? = null,
    val error: String? = null,
    val apkLocalPath: String? = null,
    val logUrl: String? = null,
    val providerName: String? = null,
    val lastActivePhase: BuildPhase? = null,
)
