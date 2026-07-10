package com.robotopia.androidstudiolite.feature.buildapk.model

import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId

data class BuildRequest(
    val projectId: ProjectId,
    val projectRoot: ProjectRoot,
    val projectName: String,
    val packageName: String,
)

enum class BuildPhase {
    Queued,
    Uploading,
    Building,
    Downloading,
    ReadyToInstall,
    Failed,
    Cancelled,
}

data class BuildProgress(
    val jobId: String,
    val phase: BuildPhase,
    val message: String? = null,
    val apkLocalPath: String? = null,
    val error: String? = null,
)
