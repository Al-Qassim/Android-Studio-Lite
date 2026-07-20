package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import com.robotopia.androidstudiolite.feature.git.api.GitBranchKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import kotlinx.coroutines.launch

fun ProjectGitScreenContext.openPublish(defaultRepoName: String = "") {
    updateState {
        copy(
            showPublish = true,
            publishRepoName = publishRepoName.ifBlank { defaultRepoName },
            publishNameError = null,
            publishError = null,
            menuBranch = null,
            menuBranchKey = null,
            actionError = null,
        )
    }
}

fun ProjectGitScreenContext.dismissPublish() {
    updateState {
        copy(
            showPublish = false,
            publishNameError = null,
            publishError = null,
            isBusy = false,
        )
    }
}

fun ProjectGitScreenContext.setPublishRepoName(value: String) {
    updateState { copy(publishRepoName = value, publishNameError = null, publishError = null) }
}

fun ProjectGitScreenContext.setPublishPrivate(value: Boolean) {
    updateState { copy(publishPrivate = value) }
}

/** UI shell — host will open Settings / Connect; toast until wired. */
fun ProjectGitScreenContext.requestConnectPublishAccount(state: ProjectGitUiState) {
    updateState {
        copy(toastMessage = "Connect ${state.publishProviderName} in Settings.")
    }
}

/**
 * UI shell — create empty GitHub repo, add origin, push current branch.
 * Real GitHub POST + JGit remote/push wiring comes later.
 */
fun ProjectGitScreenContext.requestPublish(state: ProjectGitUiState) {
    if (!state.publishAccountConnected) return
    val name = state.publishRepoName.trim()
    if (name.isEmpty()) {
        updateState { copy(publishNameError = "Enter a repository name.") }
        return
    }
    if (name.contains(' ') || name.contains('/')) {
        updateState { copy(publishNameError = "Use a single name without spaces or slashes.") }
        return
    }
    if (state.publishNeedsCommit) {
        updateState {
            copy(
                showPublish = false,
                publishError = null,
                publishNameError = null,
                tab = ProjectGitTab.Changes,
                toastMessage = "Commit at least once, then publish.",
            )
        }
        return
    }
    scope.launch {
        updateState { copy(isBusy = true, publishError = null, publishNameError = null) }
        val branch = state.currentBranch.ifBlank { "main" }
        updateState {
            copy(
                isBusy = false,
                showPublish = false,
                hasRemote = true,
                tab = ProjectGitTab.Branches,
                remoteBranches = listOf(
                    GitBranch("origin/$branch", GitBranchKind.Remote),
                ),
                toastMessage = "Published to ${state.publishProviderName}.",
            )
        }
    }
}
