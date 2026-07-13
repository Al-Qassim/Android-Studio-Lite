package com.robotopia.androidstudiolite.feature.buildapk.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.robotopia.androidstudiolite.feature.auth.api.AuthScreens
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildScreens
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
import com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.BuildProgressScreen
import com.robotopia.androidstudiolite.feature.buildapk.presentation.start.BuildStartScreen
import kotlinx.coroutines.launch

private sealed interface BuildRoute {
    data object Start : BuildRoute
    data object Connect : BuildRoute
    data class Progress(val jobId: String) : BuildRoute
}

class DefaultBuildScreens(
    private val buildService: BuildService,
    private val authSession: AuthSession,
    private val authScreens: AuthScreens,
) : BuildScreens {

    @Composable
    override fun NavHost(
        request: BuildRequest,
        onReadyToInstall: (apkLocalPath: String) -> Unit,
        onDismiss: () -> Unit,
    ) {
        var route by remember(request.projectId) { mutableStateOf<BuildRoute>(BuildRoute.Start) }
        val scope = rememberCoroutineScope()
        val account by authSession.observeAccount().collectAsState(initial = null)

        when (val current = route) {
            BuildRoute.Start -> {
                BuildStartScreen(
                    projectName = request.projectName,
                    packageName = request.packageName,
                    signedIn = account != null,
                    onBackClick = onDismiss,
                    onStartBuild = {
                        val jobId = buildService.startBuild(request)
                        route = BuildRoute.Progress(jobId)
                    },
                    onConnectGitHubClick = {
                        route = BuildRoute.Connect
                    },
                )
            }

            BuildRoute.Connect -> {
                authScreens.ConnectAccount(
                    onFinished = { route = BuildRoute.Start },
                    onCancel = { route = BuildRoute.Start },
                )
            }

            is BuildRoute.Progress -> {
                BuildProgress(
                    jobId = current.jobId,
                    onReadyToInstall = onReadyToInstall,
                    onDismiss = onDismiss,
                    onRetry = {
                        scope.launch {
                            val jobId = buildService.startBuild(request)
                            route = BuildRoute.Progress(jobId)
                        }
                    },
                )
            }
        }
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
