package com.robotopia.androidstudiolite.feature.buildapk.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.robotopia.androidstudiolite.feature.auth.api.AuthScreens
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildScreens
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
import com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.BuildProgressScreen
import com.robotopia.androidstudiolite.feature.buildapk.presentation.start.BuildStartScreen
import kotlinx.coroutines.launch

private enum class BuildStep {
    Start,
    Connect,
    Progress,
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
        var step by rememberSaveable(request.projectId.value) {
            mutableStateOf(BuildStep.Start)
        }
        var jobId by rememberSaveable(request.projectId.value) {
            mutableStateOf("")
        }
        val scope = rememberCoroutineScope()
        val account by authSession.observeAccount().collectAsState(initial = null)

        when (step) {
            BuildStep.Start -> {
                BuildStartScreen(
                    projectName = request.projectName,
                    packageName = request.packageName,
                    signedIn = account != null,
                    providerDisplayName = authSession.providerDisplayName,
                    onBackClick = onDismiss,
                    onStartBuild = {
                        jobId = buildService.startBuild(request)
                        step = BuildStep.Progress
                    },
                    onConnectAccountClick = {
                        step = BuildStep.Connect
                    },
                )
            }

            BuildStep.Connect -> {
                authScreens.ConnectAccount(
                    onFinished = { step = BuildStep.Start },
                    onCancel = { step = BuildStep.Start },
                )
            }

            BuildStep.Progress -> {
                BuildProgress(
                    jobId = jobId,
                    onReadyToInstall = onReadyToInstall,
                    onDismiss = onDismiss,
                    onRetry = {
                        scope.launch {
                            jobId = buildService.startBuild(request)
                            step = BuildStep.Progress
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
