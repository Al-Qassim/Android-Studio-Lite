package com.robotopia.androidstudiolite.feature.buildapk.presentation

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildScreens
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.BuildProgressScreen

class DefaultBuildScreens(
    private val buildService: BuildService,
) : BuildScreens {

    @Composable
    override fun NavHost(
        jobId: String,
        onReadyToInstall: (apkLocalPath: String) -> Unit,
        onDismiss: () -> Unit,
        onRetry: (() -> Unit)?,
    ) {
        BuildProgress(
            jobId = jobId,
            onReadyToInstall = onReadyToInstall,
            onDismiss = onDismiss,
            onRetry = onRetry,
        )
    }

    @Composable
    override fun BuildProgress(
        jobId: String,
        onReadyToInstall: (apkLocalPath: String) -> Unit,
        onDismiss: () -> Unit,
        onRetry: (() -> Unit)?,
    ) {
        BuildProgressScreen(
            jobId = jobId,
            buildService = buildService,
            onReadyToInstall = onReadyToInstall,
            onDismiss = onDismiss,
            onRetry = onRetry,
        )
    }
}
