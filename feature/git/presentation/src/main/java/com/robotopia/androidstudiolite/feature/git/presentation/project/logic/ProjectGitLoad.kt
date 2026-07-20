package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

fun ProjectGitScreenContext.requestRetryLoad() {
    scope.launch { refreshBranches() }
}

suspend fun ProjectGitScreenContext.refreshBranches(showLoading: Boolean = true) {
    if (showLoading) {
        updateState { copy(isLoading = true, loadError = null, actionError = null) }
    }
    try {
        val snapshot = gitService.listBranches(projectRoot)
        updateState {
            copy(
                isLoading = false,
                currentBranch = snapshot.currentBranch,
                recentBranches = snapshot.recent,
                localBranches = snapshot.local,
                remoteBranches = snapshot.remote,
                loadError = null,
            )
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        updateState {
            copy(
                isLoading = false,
                loadError = e.userMessageOrNull(TAG) ?: "Couldn't load Git for this project.",
            )
        }
    }
}

private const val TAG = "ProjectGit"
