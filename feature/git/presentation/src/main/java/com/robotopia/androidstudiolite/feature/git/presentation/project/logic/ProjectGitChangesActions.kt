package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLine
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLineKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import kotlinx.coroutines.launch

fun ProjectGitScreenContext.selectTab(tab: ProjectGitTab) {
    updateState { copy(tab = tab, actionError = null, commitError = null) }
}

fun ProjectGitScreenContext.setCommitMessage(value: String) {
    updateState { copy(commitMessage = value, commitError = null) }
}

fun ProjectGitScreenContext.toggleChangeStaged(path: String) {
    updateState {
        copy(
            changeFiles = changeFiles.map { file ->
                if (file.path == path) file.copy(staged = !file.staged) else file
            },
        )
    }
}

fun ProjectGitScreenContext.openChangeDiff(file: GitChangeFile, state: ProjectGitUiState) {
    if (file.kind == GitChangeKind.Conflict) {
        openConflictFile(
            file = file,
            currentBranch = state.currentBranch,
            mergeSource = state.mergeSourceBranch.orEmpty().ifBlank { "incoming" },
        )
        return
    }
    updateState {
        copy(
            selectedDiffPath = file.path,
            diffTitle = file.path.substringAfterLast('/'),
            isDiffLoading = false,
            isConflictEditor = false,
            diffLines = previewDiffLinesFor(file.path),
        )
    }
}

fun ProjectGitScreenContext.closeChangeDiff() {
    updateState {
        copy(
            selectedDiffPath = null,
            diffTitle = "",
            diffLines = emptyList(),
            isDiffLoading = false,
            isConflictEditor = false,
            conflictText = "",
            conflictLinePaint = emptyList(),
        )
    }
}

/** UI shell only — real init wiring comes later. */
fun ProjectGitScreenContext.requestInitRepository() {
    scope.launch {
        updateState { copy(isBusy = true, actionError = null) }
        updateState {
            copy(
                isBusy = false,
                needsInit = false,
                currentBranch = "main",
                toastMessage = "Repository initialized.",
            )
        }
    }
}

/** UI shell only — real stage/commit wiring comes later. */
fun ProjectGitScreenContext.requestCommit(state: ProjectGitUiState) {
    val message = state.commitMessage.trim()
    if (message.isEmpty()) {
        updateState { copy(commitError = "Enter a commit message.") }
        return
    }
    val stagedCount = state.changeFiles.count { it.staged }
    if (stagedCount == 0) {
        updateState { copy(commitError = "Select at least one file to commit.") }
        return
    }
    val finishingMerge = state.mergeSourceBranch != null
    updateState {
        copy(
            commitMessage = "",
            commitError = null,
            changeFiles = changeFiles.filterNot { it.staged },
            mergeSourceBranch = null,
            toastMessage = if (finishingMerge) {
                "Merge committed."
            } else {
                "Committed $stagedCount file(s)."
            },
        )
    }
}

/**
 * Placeholder diff content for previews and the UI shell until a real diff API exists.
 * Line numbers follow unified-diff semantics (old / new sides).
 */
fun previewDiffLinesFor(path: String): List<GitDiffLine> {
    val name = path.substringAfterLast('/')
    return listOf(
        GitDiffLine(GitDiffLineKind.Context, "package com.example.app", oldLine = 1, newLine = 1),
        GitDiffLine(GitDiffLineKind.Context, "", oldLine = 2, newLine = 2),
        GitDiffLine(GitDiffLineKind.Remove, "fun greet() = \"Hello\"", oldLine = 3, newLine = null),
        GitDiffLine(GitDiffLineKind.Add, "fun greet() = \"Hello, $name\"", oldLine = null, newLine = 3),
        GitDiffLine(GitDiffLineKind.Context, "", oldLine = 4, newLine = 4),
        GitDiffLine(GitDiffLineKind.Add, "fun farewell() = \"Bye\"", oldLine = null, newLine = 5),
    )
}
