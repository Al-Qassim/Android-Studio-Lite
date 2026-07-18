package com.robotopia.androidstudiolite.feature.buildapk.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "build_jobs")
data class BuildJobEntity(
    @PrimaryKey val jobId: String,
    val projectId: String,
    val projectName: String,
    val packageName: String,
    val projectRootPath: String,
    val phase: String,
    val message: String? = null,
    val error: String? = null,
    val apkLocalPath: String? = null,
    val logUrl: String? = null,
    val providerName: String? = null,
    val providerId: String = "github",
    val resumeJson: String? = null,
    val lastActivePhase: String? = null,
    val startedAtEpochMs: Long,
    val finishedAtEpochMs: Long? = null,
)
