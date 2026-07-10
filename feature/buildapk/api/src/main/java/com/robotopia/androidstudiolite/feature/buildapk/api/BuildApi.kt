package com.robotopia.androidstudiolite.feature.buildapk.api

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildProgress
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
import kotlinx.coroutines.flow.Flow

interface BuildService {
    fun observeBuild(jobId: String): Flow<BuildProgress>
    suspend fun startBuild(request: BuildRequest): String
    suspend fun cancelBuild(jobId: String)
}

interface ApkInstaller {
    fun requestInstall(apkLocalPath: String)
}

interface BuildScreens {
    /** Feature-owned entry; integration calls this rather than individual screens. */
    @Composable
    fun NavHost(
        jobId: String,
        onReadyToInstall: (apkLocalPath: String) -> Unit,
        onDismiss: () -> Unit,
        onRetry: (() -> Unit)?,
    )

    @Composable
    fun BuildProgress(
        jobId: String,
        onReadyToInstall: (apkLocalPath: String) -> Unit,
        onDismiss: () -> Unit,
        onRetry: (() -> Unit)?,
    )
}
