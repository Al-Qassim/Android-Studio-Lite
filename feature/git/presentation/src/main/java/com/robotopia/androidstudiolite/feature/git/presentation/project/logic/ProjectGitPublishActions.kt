package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.git.presentation.logic.credentialsOrNull
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

fun ProjectGitScreenContext.openPublish(defaultRepoName: String = "") {
    updateState {
        copy(
            showPublish = true,
            publishRepoName = publishRepoName.ifBlank {
                defaultRepoName.ifBlank { projectRoot.name }
            },
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

fun ProjectGitScreenContext.requestConnectPublishAccount(state: ProjectGitUiState) {
    onConnectAccount()
}

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
        try {
            val token = authSession.accessToken()
                ?: throw AppException("Connect ${state.publishProviderName} to publish.")
            val repo = gitHubClient.createUserRepo(
                accessToken = token,
                name = name,
                private = state.publishPrivate,
            )
            gitService.addRemote(projectRoot, "origin", repo.cloneUrl)
            val branch = state.currentBranch.ifBlank { "main" }
            val credentials = credentialsOrNull(authSession)
                ?: throw AppException("Connect ${state.publishProviderName} to publish.")
            gitService.pushSetUpstream(projectRoot, "origin", branch, credentials)
            refreshProjectGit(showLoading = false)
            updateState {
                copy(
                    isBusy = false,
                    showPublish = false,
                    tab = ProjectGitTab.Branches,
                    remoteHtmlUrl = repo.htmlUrl,
                    toastMessage = "Published to ${state.publishProviderName}.",
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            updateState {
                copy(
                    isBusy = false,
                    publishError = e.userMessageOrNull(TAG)
                        ?: "Couldn't publish that repository.",
                )
            }
        }
    }
}

private const val TAG = "ProjectGit"
