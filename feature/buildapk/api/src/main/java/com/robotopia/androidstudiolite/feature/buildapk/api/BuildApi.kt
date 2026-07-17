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

/** Result of asking the system to install a local APK. */
enum class ApkInstallOutcome {
    /** Package installer activity was started. */
    InstallerOpened,

    /**
     * Install-unknown-apps settings were opened because the app is not allowed
     * to install packages yet. Caller should ask the user to allow, then retry.
     */
    UnknownSourcesSettingsOpened,
}

interface ApkInstaller {
    fun requestInstall(apkLocalPath: String): ApkInstallOutcome
}

interface BuildScreens {
    /** Feature-owned entry; integration calls this rather than individual screens. */
    @Composable
    fun NavHost(
        request: BuildRequest,
        onDismiss: () -> Unit,
    )

    @Composable
    fun BuildProgress(
        jobId: String,
        onDismiss: () -> Unit,
        onRetry: (() -> Unit)?,
    )
}
