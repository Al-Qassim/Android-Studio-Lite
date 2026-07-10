package com.robotopia.androidstudiolite.feature.buildapk.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildScreens

class StubBuildScreens : BuildScreens {
    @Composable
    override fun BuildProgress(
        jobId: String,
        onReadyToInstall: (apkLocalPath: String) -> Unit,
        onDismiss: () -> Unit,
        onRetry: (() -> Unit)?,
    ) {
        Text("Build (stub)")
    }
}
