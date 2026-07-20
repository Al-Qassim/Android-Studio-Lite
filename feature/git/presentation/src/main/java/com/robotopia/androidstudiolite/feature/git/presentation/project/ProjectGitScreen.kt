package com.robotopia.androidstudiolite.feature.git.presentation.project

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.component.ToastBottom
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.component.TopBarEditorMore
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.clearToast
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.closeChangeDiff
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.closeCommitDetail
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissPublish
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.refreshBranches
import com.robotopia.androidstudiolite.feature.git.presentation.project.ui.ProjectGitBody
import com.robotopia.androidstudiolite.feature.git.presentation.project.ui.ProjectGitDialogs
import java.io.File
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ProjectGitScreen(
    gitService: GitService,
    authSession: AuthSession,
    projectRoot: File,
    projectName: String,
    onBack: () -> Unit,
) {
    val viewModel: ProjectGitViewModel = koinViewModel(key = projectRoot.absolutePath)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val screenContext = remember(
        viewModel,
        gitService,
        authSession,
        projectRoot,
        onBack,
    ) {
        ProjectGitScreenContext(
            updateState = { updater -> viewModel.uiState.update { updater(it) } },
            gitService = gitService,
            authSession = authSession,
            projectRoot = projectRoot,
            onBack = onBack,
            scope = viewModel.viewModelScope,
        )
    }

    screenContext.ProjectGitScreen(state = state, projectName = projectName)
}

@Composable
internal fun ProjectGitScreenContext.ProjectGitScreen(
    state: ProjectGitUiState,
    projectName: String,
) {
    LaunchedEffect(projectRoot) {
        refreshBranches()
    }

    LaunchedEffect(state.toastMessage) {
        if (state.toastMessage != null) {
            delay(TOAST_MS)
            clearToast()
        }
    }

    val showingDiff = state.selectedDiffPath != null
    val showingCommit = state.selectedCommit != null
    val showingPublish = state.showPublish
    BackHandler {
        when {
            showingDiff -> closeChangeDiff()
            showingCommit -> closeCommitDetail()
            showingPublish -> dismissPublish()
            else -> onBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        IslandScaffold(
            topBar = {
                when {
                    showingDiff -> {
                        TopBarEditorMore(
                            fileName = state.selectedDiffPath
                                ?: state.diffTitle.ifBlank { "Changes" },
                            onBackClick = { closeChangeDiff() },
                            onMoreClick = null,
                        )
                    }
                    showingCommit -> {
                        TopBarBackTitle(
                            title = state.selectedCommit?.shortId ?: "Commit",
                            onBackClick = { closeCommitDetail() },
                        )
                    }
                    showingPublish -> {
                        TopBarBackTitle(
                            title = "Publish to ${state.publishProviderName}",
                            onBackClick = { dismissPublish() },
                        )
                    }
                    else -> {
                        TopBarBackTitle(
                            title = when {
                                state.needsInit -> "Git · $projectName"
                                state.mergeSourceBranch != null &&
                                    state.tab == ProjectGitTab.Changes ->
                                    "Merge · $projectName"
                                state.tab == ProjectGitTab.Changes -> "Changes · $projectName"
                                state.tab == ProjectGitTab.History -> "History · $projectName"
                                else -> "Branches · $projectName"
                            },
                            onBackClick = onBack,
                        )
                    }
                }
            },
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                ProjectGitBody(state)
                state.toastMessage?.let { message ->
                    ToastBottom(message = message)
                }
            }
        }

        // Init / Publish show busy on their primary button — no full-screen overlay there.
        if (state.isBusy && !state.needsInit && !state.showPublish) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator(label = "Working…")
            }
        }
    }

    ProjectGitDialogs(state)
}

private const val TOAST_MS = 2_500L
