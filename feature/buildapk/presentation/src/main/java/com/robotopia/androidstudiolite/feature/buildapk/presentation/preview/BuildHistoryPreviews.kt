package com.robotopia.androidstudiolite.feature.buildapk.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryContent
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryDetailContent
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryDetailUiState
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryRowUi
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryUiState
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic.HISTORY_DETAIL_NOT_FOUND

private val sampleJobs = listOf(
    BuildHistoryRowUi(
        jobId = "job-building",
        projectName = "HelloCompose",
        phase = BuildPhase.Building,
        timeLabel = "Started 2 min ago",
    ),
    BuildHistoryRowUi(
        jobId = "job-uploading",
        projectName = "NotesApp",
        phase = BuildPhase.Uploading,
        timeLabel = "Started 5 min ago",
    ),
    BuildHistoryRowUi(
        jobId = "job-ready",
        projectName = "TodoApp",
        phase = BuildPhase.ReadyToInstall,
        timeLabel = "Finished 1 hour ago",
    ),
    BuildHistoryRowUi(
        jobId = "job-failed",
        projectName = "WeatherDemo",
        phase = BuildPhase.Failed,
        timeLabel = "Finished yesterday",
    ),
    BuildHistoryRowUi(
        jobId = "job-cancelled",
        projectName = "HelloCompose",
        phase = BuildPhase.Cancelled,
        timeLabel = "Finished 3 days ago",
    ),
)

internal data class BuildHistoryPreviewCase(
    private val label: String,
    val state: BuildHistoryUiState,
) {
    override fun toString(): String = label
}

internal class BuildHistoryPreviewProvider : PreviewParameterProvider<BuildHistoryPreviewCase> {
    override fun getDisplayName(index: Int): String = values.toList()[index].toString()

    override val values = sequenceOf(
        BuildHistoryPreviewCase(
            label = "empty",
            state = BuildHistoryUiState(),
        ),
        BuildHistoryPreviewCase(
            label = "loading",
            state = BuildHistoryUiState(isLoading = true),
        ),
        BuildHistoryPreviewCase(
            label = "load failed",
            state = BuildHistoryUiState(
                loadError = "Couldn't load build history. Try again.",
            ),
        ),
        BuildHistoryPreviewCase(
            label = "list",
            state = BuildHistoryUiState(jobs = sampleJobs),
        ),
        BuildHistoryPreviewCase(
            label = "menu open",
            state = BuildHistoryUiState(
                jobs = sampleJobs,
                menuJobId = "job-ready",
            ),
        ),
        BuildHistoryPreviewCase(
            label = "delete · active",
            state = BuildHistoryUiState(
                jobs = sampleJobs,
                pendingDelete = sampleJobs.first { it.jobId == "job-building" },
            ),
        ),
        BuildHistoryPreviewCase(
            label = "delete · terminal",
            state = BuildHistoryUiState(
                jobs = sampleJobs,
                pendingDelete = sampleJobs.first { it.jobId == "job-ready" },
            ),
        ),
    )
}

internal data class BuildHistoryDetailPreviewCase(
    private val label: String,
    val state: BuildHistoryDetailUiState,
) {
    override fun toString(): String = label
}

internal class BuildHistoryDetailPreviewProvider :
    PreviewParameterProvider<BuildHistoryDetailPreviewCase> {
    override fun getDisplayName(index: Int): String = values.toList()[index].toString()

    override val values = sequenceOf(
        BuildHistoryDetailPreviewCase(
            label = "loading",
            state = BuildHistoryDetailUiState(isLoading = true),
        ),
        BuildHistoryDetailPreviewCase(
            label = "not found",
            state = BuildHistoryDetailUiState(
                isLoading = false,
                loadError = HISTORY_DETAIL_NOT_FOUND,
            ),
        ),
        BuildHistoryDetailPreviewCase(
            label = "load failed",
            state = BuildHistoryDetailUiState(
                isLoading = false,
                loadError = "Couldn't load this build. Try again.",
            ),
        ),
        BuildHistoryDetailPreviewCase(
            label = "ready · install",
            state = BuildHistoryDetailUiState(
                isLoading = false,
                projectName = "TodoApp",
                phase = BuildPhase.ReadyToInstall,
                providerName = "GitHub",
                startedLabel = "Today, 10:02",
                finishedLabel = "Today, 10:11",
                message = "APK ready to install",
                apkLocalPath = "/tmp/app.apk",
                canInstall = true,
            ),
        ),
        BuildHistoryDetailPreviewCase(
            label = "ready · installing",
            state = BuildHistoryDetailUiState(
                isLoading = false,
                projectName = "TodoApp",
                phase = BuildPhase.ReadyToInstall,
                providerName = "GitHub",
                startedLabel = "Today, 10:02",
                finishedLabel = "Today, 10:11",
                message = "APK ready to install",
                apkLocalPath = "/tmp/app.apk",
                canInstall = true,
                isInstalling = true,
            ),
        ),
        BuildHistoryDetailPreviewCase(
            label = "ready · apk missing",
            state = BuildHistoryDetailUiState(
                isLoading = false,
                projectName = "TodoApp",
                phase = BuildPhase.ReadyToInstall,
                providerName = "GitHub",
                startedLabel = "Yesterday, 18:20",
                finishedLabel = "Yesterday, 18:28",
                message = "APK ready to install",
                canInstall = false,
            ),
        ),
        BuildHistoryDetailPreviewCase(
            label = "ready · install error",
            state = BuildHistoryDetailUiState(
                isLoading = false,
                projectName = "TodoApp",
                phase = BuildPhase.ReadyToInstall,
                providerName = "GitHub",
                startedLabel = "Today, 10:02",
                finishedLabel = "Today, 10:11",
                apkLocalPath = "/tmp/app.apk",
                canInstall = true,
                installError = "Allow installs from this app, then tap Install again.",
            ),
        ),
        BuildHistoryDetailPreviewCase(
            label = "build failed · with log",
            state = BuildHistoryDetailUiState(
                isLoading = false,
                projectName = "WeatherDemo",
                phase = BuildPhase.Failed,
                providerName = "GitHub",
                lastActivePhase = BuildPhase.Building,
                startedLabel = "Yesterday, 09:00",
                finishedLabel = "Yesterday, 09:08",
                error = "Build failed. Open the build log.",
                logUrl = "https://github.com/",
            ),
        ),
        BuildHistoryDetailPreviewCase(
            label = "build failed · interrupted",
            state = BuildHistoryDetailUiState(
                isLoading = false,
                projectName = "HelloCompose",
                phase = BuildPhase.Failed,
                providerName = "GitHub",
                lastActivePhase = BuildPhase.Uploading,
                startedLabel = "Today, 08:14",
                finishedLabel = "Today, 08:14",
                error = "Build interrupted. Start a new build to try again.",
            ),
        ),
        BuildHistoryDetailPreviewCase(
            label = "cancelled",
            state = BuildHistoryDetailUiState(
                isLoading = false,
                projectName = "HelloCompose",
                phase = BuildPhase.Cancelled,
                providerName = "GitHub",
                lastActivePhase = BuildPhase.Queued,
                startedLabel = "3 days ago, 14:00",
                finishedLabel = "3 days ago, 14:02",
                message = "No APK was produced. You can start a new build when you're ready.",
            ),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun BuildHistoryPreview(
    @PreviewParameter(BuildHistoryPreviewProvider::class) preview: BuildHistoryPreviewCase,
) {
    BuildHistoryContent(
        state = preview.state,
        onBackClick = {},
        onJobClick = {},
        onMenuOpen = {},
        onMenuDismiss = {},
        onDeleteMenuClick = {},
        onDeleteCancel = {},
        onDeleteConfirm = {},
        onRetryLoad = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun BuildHistoryDetailPreview(
    @PreviewParameter(BuildHistoryDetailPreviewProvider::class) preview: BuildHistoryDetailPreviewCase,
) {
    BuildHistoryDetailContent(
        state = preview.state,
        onBackClick = {},
        onInstall = {},
        onViewLog = {},
        onRetryLoad = {},
    )
}
