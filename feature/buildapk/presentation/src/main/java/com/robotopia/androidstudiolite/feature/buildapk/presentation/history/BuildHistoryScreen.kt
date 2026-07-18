package com.robotopia.androidstudiolite.feature.buildapk.presentation.history

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.robotopia.androidstudiolite.designsystem.animation.navFade
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstaller
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryStore
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.BuildProgressScreen

private enum class HistoryRoute {
    List,
    Progress,
    Detail,
}

@Composable
internal fun BuildHistoryScreen(
    projectIdFilter: String?,
    buildHistoryStore: BuildHistoryStore,
    buildService: BuildService,
    apkInstaller: ApkInstaller,
    onDismiss: () -> Unit,
) {
    var route by rememberSaveable { mutableStateOf(HistoryRoute.List) }
    var selectedJobId by rememberSaveable { mutableStateOf("") }

    AnimatedContent(
        targetState = route,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = { navFade() },
        label = "buildHistoryNav",
    ) { current ->
        when (current) {
            HistoryRoute.List -> {
                BuildHistoryListScreen(
                    projectIdFilter = projectIdFilter,
                    buildHistoryStore = buildHistoryStore,
                    onDismiss = onDismiss,
                    onOpenJob = { row ->
                        selectedJobId = row.jobId
                        route = if (row.phase.isActiveHistoryPhase()) {
                            HistoryRoute.Progress
                        } else {
                            HistoryRoute.Detail
                        }
                    },
                )
            }

            HistoryRoute.Progress -> {
                BuildProgressScreen(
                    jobId = selectedJobId,
                    buildService = buildService,
                    apkInstaller = apkInstaller,
                    onDismiss = { route = HistoryRoute.List },
                    onRetry = null,
                )
            }

            HistoryRoute.Detail -> {
                BuildHistoryDetailScreen(
                    jobId = selectedJobId,
                    buildHistoryStore = buildHistoryStore,
                    apkInstaller = apkInstaller,
                    onDismiss = { route = HistoryRoute.List },
                )
            }
        }
    }
}
