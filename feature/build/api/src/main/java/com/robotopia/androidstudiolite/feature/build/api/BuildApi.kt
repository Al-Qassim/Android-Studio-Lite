package com.robotopia.androidstudiolite.feature.build.api

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.core.model.ProjectId
import com.robotopia.androidstudiolite.core.model.ProjectRoot
import kotlinx.coroutines.flow.Flow

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

interface BuildService {
    fun observeBuild(jobId: String): Flow<BuildProgress>
    suspend fun startBuild(request: BuildRequest): String
    suspend fun cancelBuild(jobId: String)
}

interface ApkInstaller {
    fun requestInstall(apkLocalPath: String)
}

interface BuildScreens {
    @Composable
    fun BuildProgress(
        jobId: String,
        onReadyToInstall: (apkLocalPath: String) -> Unit,
        onDismiss: () -> Unit,
        onRetry: (() -> Unit)? = null,
    )
}
