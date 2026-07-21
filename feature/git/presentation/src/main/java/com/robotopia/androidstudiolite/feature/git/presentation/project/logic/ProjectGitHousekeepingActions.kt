package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.git.presentation.logic.credentialsOrNull
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

fun ProjectGitScreenContext.openChangeFileMenu(path: String) {
    updateState { copy(changeFileMenuPath = path, actionError = null) }
}

fun ProjectGitScreenContext.dismissChangeFileMenu() {
    updateState { copy(changeFileMenuPath = null) }
}

fun ProjectGitScreenContext.openDiscardFileConfirm(path: String) {
    updateState {
        copy(
            changeFileMenuPath = null,
            discardConfirmPath = path,
            showDiscardAllConfirm = false,
        )
    }
}

fun ProjectGitScreenContext.openDiscardAllConfirm() {
    updateState {
        copy(
            showDiscardAllConfirm = true,
            discardConfirmPath = null,
            changeFileMenuPath = null,
        )
    }
}

fun ProjectGitScreenContext.dismissDiscardConfirm() {
    updateState {
        copy(discardConfirmPath = null, showDiscardAllConfirm = false)
    }
}

fun ProjectGitScreenContext.requestDiscardFile(path: String) {
    scope.launch {
        updateState { copy(isBusy = true, discardConfirmPath = null, actionError = null) }
        try {
            gitService.discardPaths(projectRoot, listOf(path))
            refreshProjectGit(showLoading = false)
            updateState {
                copy(
                    isBusy = false,
                    selectedDiffPath = null,
                    toastMessage = "Discarded changes in ${path.substringAfterLast('/')}.",
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            updateState {
                copy(
                    isBusy = false,
                    actionError = e.userMessageOrNull(TAG) ?: "Couldn't discard those changes.",
                )
            }
        }
    }
}

fun ProjectGitScreenContext.requestDiscardAll() {
    scope.launch {
        updateState { copy(isBusy = true, showDiscardAllConfirm = false, actionError = null) }
        try {
            gitService.discardAll(projectRoot)
            refreshProjectGit(showLoading = false)
            updateState {
                copy(
                    isBusy = false,
                    selectedDiffPath = null,
                    toastMessage = "Discarded all local changes.",
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            updateState {
                copy(
                    isBusy = false,
                    actionError = e.userMessageOrNull(TAG) ?: "Couldn't discard local changes.",
                )
            }
        }
    }
}

fun ProjectGitScreenContext.requestIgnorePath(path: String) {
    scope.launch {
        updateState { copy(isBusy = true, changeFileMenuPath = null, actionError = null) }
        try {
            gitService.appendGitignore(projectRoot, path)
            val untracked = path in gitService.status(projectRoot).untracked
            if (untracked) {
                gitService.discardPaths(projectRoot, listOf(path))
            }
            refreshProjectGit(showLoading = false)
            updateState {
                copy(
                    isBusy = false,
                    toastMessage = "Added to .gitignore.",
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            updateState {
                copy(
                    isBusy = false,
                    actionError = e.userMessageOrNull(TAG) ?: "Couldn't update .gitignore.",
                )
            }
        }
    }
}

/**
 * Fetches when a remote exists so ahead/behind is fresh, then shows undo confirm.
 */
fun ProjectGitScreenContext.openUndoCommitConfirm(state: ProjectGitUiState) {
    if (!state.hasRemote) {
        updateState { copy(showUndoCommitConfirm = true) }
        return
    }
    scope.launch {
        updateState { copy(isBusy = true, actionError = null) }
        try {
            gitService.fetch(projectRoot, credentialsOrNull(authSession))
            refreshProjectGit(showLoading = false)
            updateState {
                copy(
                    isBusy = false,
                    showUndoCommitConfirm = true,
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            updateState {
                copy(
                    isBusy = false,
                    showUndoCommitConfirm = true,
                    actionError = e.userMessageOrNull(TAG)
                        ?: "Could not fetch. Undo still applies locally only.",
                )
            }
        }
    }
}

fun ProjectGitScreenContext.dismissUndoCommitConfirm() {
    updateState { copy(showUndoCommitConfirm = false) }
}

fun ProjectGitScreenContext.requestUndoCommit(state: ProjectGitUiState) {
    if (state.historyCommits.isEmpty()) return
    scope.launch {
        updateState { copy(isBusy = true, showUndoCommitConfirm = false, actionError = null) }
        try {
            gitService.undoLastCommit(projectRoot)
            refreshProjectGit(showLoading = false)
            updateState {
                copy(
                    isBusy = false,
                    selectedCommit = null,
                    selectedCommitFiles = emptyList(),
                    toastMessage = "Undid last commit.",
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            updateState {
                copy(
                    isBusy = false,
                    actionError = e.userMessageOrNull(TAG) ?: "Couldn't undo the last commit.",
                )
            }
        }
    }
}

fun ProjectGitScreenContext.requestOpenOnGitHub(url: String) {
    openUrl(url)
    updateState {
        copy(
            toastMessage = "Opening in browser…",
            changeFileMenuPath = null,
        )
    }
}

fun githubCommitUrl(remoteHtmlUrl: String, commitId: String): String =
    remoteHtmlUrl.trimEnd('/') + "/commit/" + commitId

fun githubTreeUrl(remoteHtmlUrl: String, branch: String): String =
    remoteHtmlUrl.trimEnd('/') + "/tree/" + branch

private const val TAG = "ProjectGit"
