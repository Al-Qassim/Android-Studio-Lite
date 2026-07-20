package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLine
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLineKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState

fun ProjectGitScreenContext.openAbortMergeConfirm() {
    updateState { copy(showAbortMergeConfirm = true, actionError = null) }
}

fun ProjectGitScreenContext.dismissAbortMergeConfirm() {
    updateState { copy(showAbortMergeConfirm = false) }
}

/** UI shell — clears merge conflict state. */
fun ProjectGitScreenContext.requestAbortMerge() {
    updateState {
        copy(
            showAbortMergeConfirm = false,
            mergeSourceBranch = null,
            changeFiles = changeFiles.filterNot { it.kind == GitChangeKind.Conflict },
            selectedDiffPath = null,
            isConflictEditor = false,
            diffLines = emptyList(),
            commitMessage = "",
            toastMessage = "Merge aborted.",
        )
    }
}

/** UI shell — keep current (ours) side in the buffer; user still marks resolved. */
fun ProjectGitScreenContext.acceptConflictOurs(state: ProjectGitUiState) {
    if (!state.hasOpenConflictHunks) return
    updateState {
        copy(
            diffLines = state.diffLines
                .filter {
                    it.kind == GitDiffLineKind.Context || it.kind == GitDiffLineKind.ConflictOurs
                }
                .map { it.copy(kind = GitDiffLineKind.Context) },
            toastMessage = "Kept current changes.",
        )
    }
}

/** UI shell — keep incoming (theirs) side in the buffer; user still marks resolved. */
fun ProjectGitScreenContext.acceptConflictTheirs(state: ProjectGitUiState) {
    if (!state.hasOpenConflictHunks) return
    updateState {
        copy(
            diffLines = state.diffLines
                .filter {
                    it.kind == GitDiffLineKind.Context || it.kind == GitDiffLineKind.ConflictTheirs
                }
                .map { it.copy(kind = GitDiffLineKind.Context) },
            toastMessage = "Kept incoming changes.",
        )
    }
}

/**
 * UI shell — stage the open conflict file after hunks are cleared
 * (Accept current/incoming, or a future manual edit).
 */
fun ProjectGitScreenContext.markConflictResolvedManually(state: ProjectGitUiState) {
    val path = state.selectedDiffPath ?: return
    if (state.hasOpenConflictHunks) return
    updateState {
        copy(
            changeFiles = changeFiles.map { file ->
                if (file.path == path && file.kind == GitChangeKind.Conflict) {
                    file.copy(staged = true, kind = GitChangeKind.Modified)
                } else {
                    file
                }
            },
            isConflictEditor = false,
            toastMessage = "Marked as resolved.",
        )
    }
}

fun ProjectGitScreenContext.openConflictFile(file: GitChangeFile, currentBranch: String, mergeSource: String) {
    updateState {
        copy(
            selectedDiffPath = file.path,
            diffTitle = file.path.substringAfterLast('/'),
            isDiffLoading = false,
            isConflictEditor = true,
            diffLines = previewConflictLinesFor(
                path = file.path,
                currentBranch = currentBranch,
                mergeSource = mergeSource,
            ),
        )
    }
}

fun previewConflictLinesFor(
    path: String,
    currentBranch: String,
    mergeSource: String,
): List<GitDiffLine> {
    val name = path.substringAfterLast('/')
    return listOf(
        GitDiffLine(GitDiffLineKind.Context, "package com.example.app", oldLine = 1, newLine = 1),
        GitDiffLine(GitDiffLineKind.Context, "", oldLine = 2, newLine = 2),
        GitDiffLine(GitDiffLineKind.Context, "@Composable", oldLine = 3, newLine = 3),
        GitDiffLine(GitDiffLineKind.Context, "fun $name() {", oldLine = 4, newLine = 4),
        GitDiffLine(GitDiffLineKind.ConflictMarker, "<<<<<<< HEAD ($currentBranch)"),
        GitDiffLine(GitDiffLineKind.ConflictOurs, "    Text(\"Hello from $currentBranch\")", oldLine = 5, newLine = 5),
        GitDiffLine(GitDiffLineKind.ConflictOurs, "    Button(onClick = { /* current */ })", oldLine = 6, newLine = 6),
        GitDiffLine(GitDiffLineKind.ConflictMarker, "======="),
        GitDiffLine(GitDiffLineKind.ConflictTheirs, "    Text(\"Hello from $mergeSource\")", oldLine = null, newLine = 5),
        GitDiffLine(GitDiffLineKind.ConflictTheirs, "    OutlinedButton(onClick = { /* incoming */ })", oldLine = null, newLine = 6),
        GitDiffLine(GitDiffLineKind.ConflictMarker, ">>>>>>> $mergeSource"),
        GitDiffLine(GitDiffLineKind.Context, "}", oldLine = 7, newLine = 7),
    )
}

/** After all conflicts are resolved, seed a merge commit message (UI shell). */
fun ProjectGitScreenContext.prepareMergeCommitMessage(state: ProjectGitUiState) {
    if (state.mergeSourceBranch == null) return
    if (state.unresolvedConflictCount > 0) return
    updateState {
        copy(
            tab = ProjectGitTab.Changes,
            commitMessage = "Merge branch '${state.mergeSourceBranch}' into ${state.currentBranch}",
            commitError = null,
        )
    }
}
