package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLine
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLineKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

fun ProjectGitScreenContext.openAbortMergeConfirm() {
    updateState { copy(showAbortMergeConfirm = true, actionError = null) }
}

fun ProjectGitScreenContext.dismissAbortMergeConfirm() {
    updateState { copy(showAbortMergeConfirm = false) }
}

fun ProjectGitScreenContext.requestAbortMerge() {
    scope.launch {
        updateState { copy(isBusy = true, showAbortMergeConfirm = false, actionError = null) }
        try {
            gitService.abortMerge(projectRoot)
            refreshProjectGit(showLoading = false)
            updateState {
                copy(
                    isBusy = false,
                    selectedDiffPath = null,
                    isConflictEditor = false,
                    conflictText = "",
                    conflictLinePaint = emptyList(),
                    diffLines = emptyList(),
                    commitMessage = "",
                    toastMessage = "Merge aborted.",
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            updateState {
                copy(
                    isBusy = false,
                    actionError = e.userMessageOrNull(TAG) ?: "Couldn't abort the merge.",
                )
            }
        }
    }
}

fun ProjectGitScreenContext.setConflictText(value: String) {
    updateState {
        copy(
            conflictText = value,
            conflictLinePaint = updateConflictLinePaintAfterEdit(
                previousText = conflictText,
                previousPaint = conflictLinePaint,
                newText = value,
            ),
        )
    }
}

fun ProjectGitScreenContext.acceptConflictOurs(state: ProjectGitUiState) {
    if (!state.hasOpenConflictHunks) return
    val resolved = resolveConflictTextKeeping(state.conflictText, keepOurs = true)
    updateState {
        copy(
            conflictText = resolved,
            conflictLinePaint = classifyConflictLinePaint(resolved),
            toastMessage = "Kept current changes.",
        )
    }
}

fun ProjectGitScreenContext.acceptConflictTheirs(state: ProjectGitUiState) {
    if (!state.hasOpenConflictHunks) return
    val resolved = resolveConflictTextKeeping(state.conflictText, keepOurs = false)
    updateState {
        copy(
            conflictText = resolved,
            conflictLinePaint = classifyConflictLinePaint(resolved),
            toastMessage = "Kept incoming changes.",
        )
    }
}

fun ProjectGitScreenContext.markConflictResolvedManually(state: ProjectGitUiState) {
    val path = state.selectedDiffPath ?: return
    if (state.hasOpenConflictHunks) return
    scope.launch {
        updateState { copy(isBusy = true, actionError = null) }
        try {
            gitService.writeWorkingFile(projectRoot, path, state.conflictText)
            gitService.stagePaths(projectRoot, listOf(path))
            val resolvedLines = state.conflictText.split('\n').mapIndexed { index, text ->
                val n = index + 1
                GitDiffLine(GitDiffLineKind.Context, text, oldLine = n, newLine = n)
            }
            refreshProjectGit(showLoading = false)
            updateState {
                copy(
                    isBusy = false,
                    isConflictEditor = false,
                    conflictText = "",
                    conflictLinePaint = emptyList(),
                    diffLines = resolvedLines,
                    toastMessage = "Marked as resolved.",
                )
            }
            prepareMergeCommitMessageAfterRefresh()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            updateState {
                copy(
                    isBusy = false,
                    actionError = e.userMessageOrNull(TAG) ?: "Couldn't mark that file resolved.",
                )
            }
        }
    }
}

fun ProjectGitScreenContext.openConflictFile(
    file: GitChangeFile,
    currentBranch: String,
    mergeSource: String,
) {
    scope.launch {
        updateState {
            copy(
                selectedDiffPath = file.path,
                diffTitle = file.path.substringAfterLast('/'),
                isDiffLoading = true,
                isConflictEditor = true,
                diffLines = emptyList(),
                conflictText = "",
                conflictLinePaint = emptyList(),
            )
        }
        try {
            val text = gitService.readWorkingFile(projectRoot, file.path)
            updateState {
                copy(
                    isDiffLoading = false,
                    conflictText = text,
                    conflictLinePaint = classifyConflictLinePaint(text),
                    // Keep branch labels available for header via existing state fields.
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            updateState {
                copy(
                    isDiffLoading = false,
                    selectedDiffPath = null,
                    isConflictEditor = false,
                    actionError = e.userMessageOrNull(TAG) ?: "Couldn't open that conflict file.",
                )
            }
        }
    }
}

fun conflictTextFromDiffLines(lines: List<GitDiffLine>): String =
    lines.joinToString("\n") { it.text }

/**
 * Drops conflict markers and keeps either the current (ours) or incoming (theirs) side
 * for every `<<<<<<<` / `=======` / `>>>>>>>` hunk in [text].
 */
fun resolveConflictTextKeeping(text: String, keepOurs: Boolean): String {
    val out = ArrayList<String>()
    var inOurs = false
    var inTheirs = false
    for (line in text.split('\n')) {
        when {
            line.startsWith("<<<<<<<") -> {
                inOurs = true
                inTheirs = false
            }
            line.startsWith("=======") && (inOurs || inTheirs) -> {
                inOurs = false
                inTheirs = true
            }
            line.startsWith(">>>>>>>") -> {
                inOurs = false
                inTheirs = false
            }
            inOurs -> if (keepOurs) out.add(line)
            inTheirs -> if (!keepOurs) out.add(line)
            else -> out.add(line)
        }
    }
    return out.joinToString("\n")
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

private fun ProjectGitScreenContext.prepareMergeCommitMessageAfterRefresh() {
    updateState {
        if (mergeSourceBranch == null || unresolvedConflictCount > 0) {
            this
        } else {
            copy(
                tab = ProjectGitTab.Changes,
                commitMessage = commitMessage.ifBlank {
                    "Merge branch '$mergeSourceBranch' into $currentBranch"
                },
                commitError = null,
            )
        }
    }
}

private const val TAG = "ProjectGit"
