package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

fun ProjectGitScreenContext.requestRetryLoad() {
    scope.launch { refreshProjectGit() }
}

/** Full reload of branches, status, history, remotes, and auth publish flags. */
suspend fun ProjectGitScreenContext.refreshBranches(showLoading: Boolean = true) {
    refreshProjectGit(showLoading)
}

suspend fun ProjectGitScreenContext.refreshProjectGit(showLoading: Boolean = true) {
    if (showLoading) {
        updateState { copy(isLoading = true, loadError = null, actionError = null) }
    }
    try {
        val account = authSession.currentAccount()
        val providerName = authSession.providerDisplayName
        if (!gitService.isRepository(projectRoot)) {
            updateState {
                copy(
                    isLoading = false,
                    needsInit = true,
                    loadError = null,
                    currentBranch = "",
                    recentBranches = emptyList(),
                    localBranches = emptyList(),
                    remoteBranches = emptyList(),
                    changeFiles = emptyList(),
                    historyCommits = emptyList(),
                    hasRemote = false,
                    remoteHtmlUrl = null,
                    aheadCount = 0,
                    behindCount = 0,
                    mergeSourceBranch = null,
                    publishAccountConnected = account != null,
                    publishProviderName = providerName,
                    publishNeedsCommit = true,
                )
            }
            return
        }

        val branches = gitService.listBranches(projectRoot)
        val status = gitService.status(projectRoot)
        val info = gitService.repositoryInfo(projectRoot)
        val commits = gitService.log(projectRoot).map { it.toSummary() }
        val changeFiles = status.toChangeFiles()
        updateState {
            copy(
                isLoading = false,
                needsInit = false,
                loadError = null,
                currentBranch = branches.currentBranch,
                recentBranches = branches.recent,
                localBranches = branches.local,
                remoteBranches = branches.remote,
                changeFiles = changeFiles,
                historyCommits = commits,
                hasRemote = info.hasRemote,
                remoteHtmlUrl = info.remoteHtmlUrl,
                aheadCount = info.aheadCount,
                behindCount = info.behindCount,
                mergeSourceBranch = when {
                    info.isMerging -> mergeSourceBranch ?: "incoming"
                    else -> null
                },
                publishAccountConnected = account != null,
                publishProviderName = providerName,
                publishNeedsCommit = commits.isEmpty(),
                publishRepoName = publishRepoName.ifBlank { projectRoot.name },
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
