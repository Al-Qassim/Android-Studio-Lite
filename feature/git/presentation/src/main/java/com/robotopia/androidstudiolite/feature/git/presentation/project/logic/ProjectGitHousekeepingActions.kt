package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.git.presentation.logic.credentialsOrNull
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
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

/** UI shell — drop one file’s local changes. */
fun ProjectGitScreenContext.requestDiscardFile(path: String) {
    updateState {
        copy(
            discardConfirmPath = null,
            changeFiles = changeFiles.filterNot { it.path == path },
            toastMessage = "Discarded changes in ${path.substringAfterLast('/')}.",
        )
    }
}

/** UI shell — drop all local changes. */
fun ProjectGitScreenContext.requestDiscardAll() {
    updateState {
        copy(
            showDiscardAllConfirm = false,
            changeFiles = emptyList(),
            toastMessage = "Discarded all local changes.",
        )
    }
}

/** UI shell — add path to .gitignore and remove from Changes. */
fun ProjectGitScreenContext.requestIgnorePath(path: String) {
    updateState {
        copy(
            changeFileMenuPath = null,
            changeFiles = changeFiles.filterNot { it.path == path },
            toastMessage = "Added to .gitignore.",
        )
    }
}

/**
 * Fetches when a remote exists so ahead/behind is fresh, then shows undo confirm.
 * Local-only repos skip fetch.
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
                    actionError = e.userMessageOrNull("ProjectGit")
                        ?: "Could not fetch. Undo still applies locally only.",
                )
            }
        }
    }
}

fun ProjectGitScreenContext.dismissUndoCommitConfirm() {
    updateState { copy(showUndoCommitConfirm = false) }
}

/**
 * UI shell — soft undo of the tip commit (keeps file contents as local changes).
 */
fun ProjectGitScreenContext.requestUndoCommit(state: ProjectGitUiState) {
    val tip = state.historyCommits.firstOrNull() ?: return
    val restored = previewCommitFilesFor(tip.id).map { file ->
        GitChangeFile(
            path = file.path,
            kind = file.kind,
            staged = file.kind != GitChangeKind.Untracked,
        )
    }
    updateState {
        copy(
            showUndoCommitConfirm = false,
            historyCommits = historyCommits.drop(1),
            changeFiles = (restored + changeFiles).distinctBy { it.path },
            selectedCommit = null,
            selectedCommitFiles = emptyList(),
            toastMessage = "Undid commit ${tip.shortId}.",
        )
    }
}

/**
 * UI shell — open commit/branch on GitHub (host wires browser later).
 */
fun ProjectGitScreenContext.requestOpenOnGitHub(url: String) {
    updateState {
        copy(
            toastMessage = "Open $url",
            changeFileMenuPath = null,
        )
    }
}

fun githubCommitUrl(remoteHtmlUrl: String, commitId: String): String =
    remoteHtmlUrl.trimEnd('/') + "/commit/" + commitId

fun githubTreeUrl(remoteHtmlUrl: String, branch: String): String =
    remoteHtmlUrl.trimEnd('/') + "/tree/" + branch
