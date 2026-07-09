package com.robotopia.androidstudiolite.feature.build.impl

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.build.api.BuildProgress
import com.robotopia.androidstudiolite.feature.build.api.BuildRequest
import com.robotopia.androidstudiolite.feature.build.api.BuildScreens
import com.robotopia.androidstudiolite.feature.build.api.BuildService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.koin.dsl.module

internal class StubBuildService : BuildService {
    override fun observeBuild(jobId: String): Flow<BuildProgress> = emptyFlow()
    override suspend fun startBuild(request: BuildRequest): String = "stub"
    override suspend fun cancelBuild(jobId: String) = Unit
}

internal class StubBuildScreens : BuildScreens {
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

val buildModule = module {
    single<BuildService> { StubBuildService() }
    single<BuildScreens> { StubBuildScreens() }
}
