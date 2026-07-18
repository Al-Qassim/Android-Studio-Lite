package com.robotopia.androidstudiolite.feature.buildapk.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.robotopia.androidstudiolite.designsystem.animation.navFade
import com.robotopia.androidstudiolite.feature.auth.api.AuthScreens
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstaller
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryStore
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildScreens
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryScreen
import com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.BuildProgressScreen
import com.robotopia.androidstudiolite.feature.buildapk.presentation.start.BuildStartScreen
import kotlinx.coroutines.launch

private enum class BuildStep {
    Start,
    Connect,
    Progress,
    History,
}

class DefaultBuildScreens(
    private val buildService: BuildService,
    private val buildHistoryStore: BuildHistoryStore,
    private val apkInstaller: ApkInstaller,
    private val authSession: AuthSession,
    private val authScreens: AuthScreens,
) : BuildScreens {

    @Composable
    override fun NavHost(
        request: BuildRequest,
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

        AnimatedContent(
            targetState = step,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = { navFade() },
            label = "buildNav",
        ) { current ->
            when (current) {
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
                        onHistoryClick = {
                            step = BuildStep.History
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
                        onDismiss = onDismiss,
                        onRetry = {
                            scope.launch {
                                jobId = buildService.startBuild(request)
                                step = BuildStep.Progress
                            }
                        },
                    )
                }

                BuildStep.History -> {
                    History(
                        projectIdFilter = request.projectId.value,
                        onDismiss = { step = BuildStep.Start },
                    )
                }
            }
        }
    }

    @Composable
    override fun BuildProgress(
        jobId: String,
        onDismiss: () -> Unit,
        onRetry: (() -> Unit)?,
    ) {
        BuildProgressScreen(
            jobId = jobId,
            buildService = buildService,
            apkInstaller = apkInstaller,
            onDismiss = onDismiss,
            onRetry = onRetry,
        )
    }

    @Composable
    override fun History(
        projectIdFilter: String?,
        onDismiss: () -> Unit,
    ) {
        BuildHistoryScreen(
            projectIdFilter = projectIdFilter,
            buildHistoryStore = buildHistoryStore,
            buildService = buildService,
            apkInstaller = apkInstaller,
            onDismiss = onDismiss,
        )
    }
}
