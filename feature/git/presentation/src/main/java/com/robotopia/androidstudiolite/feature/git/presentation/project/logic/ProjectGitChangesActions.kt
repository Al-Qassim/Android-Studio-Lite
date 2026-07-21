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

fun ProjectGitScreenContext.selectTab(tab: ProjectGitTab) {
    updateState { copy(tab = tab, actionError = null, commitError = null) }
}

fun ProjectGitScreenContext.setCommitMessage(value: String) {
    updateState { copy(commitMessage = value, commitError = null) }
}

fun ProjectGitScreenContext.toggleChangeStaged(path: String) {
    scope.launch {
        try {
            var shouldStage = false
            updateState {
                val file = changeFiles.firstOrNull { it.path == path } ?: return@updateState this
                shouldStage = !file.staged
                copy(
                    changeFiles = changeFiles.map {
                        if (it.path == path) it.copy(staged = shouldStage) else it
                    },
                )
            }
            if (shouldStage) {
                gitService.stagePaths(projectRoot, listOf(path))
            } else {
                gitService.unstagePaths(projectRoot, listOf(path))
            }
            refreshProjectGit(showLoading = false)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            refreshProjectGit(showLoading = false)
            val message = e.userMessageOrNull(TAG) ?: "Couldn't update staging."
            updateState {
                copy(actionError = message, toastMessage = message)
            }
        }
    }
}

/** Stage every change file, or unstage all when every file is already selected. */
fun ProjectGitScreenContext.toggleSelectAllChanges() {
    scope.launch {
        try {
            var selectAll = false
            var paths = emptyList<String>()
            updateState {
                if (changeFiles.isEmpty()) return@updateState this
                paths = changeFiles.map { it.path }
                selectAll = !changeFiles.all { it.staged }
                copy(
                    changeFiles = changeFiles.map { it.copy(staged = selectAll) },
                    actionError = null,
                )
            }
            if (paths.isEmpty()) return@launch
            if (selectAll) {
                gitService.stagePaths(projectRoot, paths)
            } else {
                gitService.unstagePaths(projectRoot, paths)
            }
            refreshProjectGit(showLoading = false)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            refreshProjectGit(showLoading = false)
            val message = e.userMessageOrNull(TAG) ?: "Couldn't update staging."
            updateState {
                copy(actionError = message, toastMessage = message)
            }
        }
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
    scope.launch {
        updateState {
            copy(
                selectedDiffPath = file.path,
                diffTitle = file.path.substringAfterLast('/'),
                isDiffLoading = true,
                isConflictEditor = false,
                conflictText = "",
                conflictLinePaint = emptyList(),
                diffLines = emptyList(),
                actionError = null,
            )
        }
        try {
            val lines = gitService.diffWorkingTree(projectRoot, file.path).map { it.toUiLine() }
            updateState {
                copy(
                    isDiffLoading = false,
                    diffLines = lines.ifEmpty {
                        listOf(
                            GitDiffLine(
                                GitDiffLineKind.Context,
                                "(No textual diff for this file.)",
                            ),
                        )
                    },
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val message = e.userMessageOrNull(TAG) ?: "Couldn't open that diff."
            updateState {
                copy(
                    isDiffLoading = false,
                    actionError = message,
                    toastMessage = message,
                    selectedDiffPath = null,
                )
            }
        }
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

/** Leave Project Git and open [path] in the editor. */
fun ProjectGitScreenContext.requestOpenWorkingFile(path: String) {
    updateState { copy(changeFileMenuPath = null) }
    onOpenFile(path)
}

fun ProjectGitScreenContext.requestInitRepository() {
    scope.launch {
        updateState { copy(isBusy = true, actionError = null) }
        try {
            gitService.init(projectRoot)
            refreshProjectGit(showLoading = false)
            updateState {
                copy(
                    isBusy = false,
                    toastMessage = "Repository initialized.",
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            updateState {
                copy(
                    isBusy = false,
                    actionError = e.userMessageOrNull(TAG) ?: "Couldn't initialize Git.",
                )
            }
        }
    }
}

fun ProjectGitScreenContext.requestCommit(state: ProjectGitUiState) {
    val message = state.commitMessage.trim()
    if (message.isEmpty()) {
        updateState { copy(commitError = "Enter a commit message.") }
        return
    }
    val staged = state.changeFiles.filter { it.staged }
    if (staged.isEmpty()) {
        updateState { copy(commitError = "Select at least one file to commit.") }
        return
    }
    val finishingMerge = state.mergeSourceBranch != null
    scope.launch {
        updateState { copy(isBusy = true, commitError = null, actionError = null) }
        try {
            val toStage = staged.map { it.path }
            val toUnstage = state.changeFiles.filterNot { it.staged }.map { it.path }
            gitService.unstagePaths(projectRoot, toUnstage)
            gitService.stagePaths(projectRoot, toStage)
            val (name, email) = commitIdentity()
            gitService.commit(projectRoot, message, name, email)
            refreshProjectGit(showLoading = false)
            updateState {
                copy(
                    isBusy = false,
                    commitMessage = "",
                    selectedDiffPath = null,
                    isConflictEditor = false,
                    conflictText = "",
                    conflictLinePaint = emptyList(),
                    toastMessage = if (finishingMerge) {
                        "Merge committed."
                    } else {
                        "Committed ${staged.size} file(s)."
                    },
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            updateState {
                copy(
                    isBusy = false,
                    commitError = e.userMessageOrNull(TAG) ?: "Couldn't create that commit.",
                )
            }
        }
    }
}

suspend fun ProjectGitScreenContext.commitIdentity(): Pair<String, String> {
    val login = authSession.currentAccount()?.identity
        ?.removePrefix("@")
        ?.trim()
        ?.ifBlank { null }
    return if (login != null) {
        login to "$login@users.noreply.github.com"
    } else {
        "Android Studio Lite" to "noreply@androidstudiolite.local"
    }
}

/**
 * Placeholder diff content for Compose previews.
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

private const val TAG = "ProjectGit"
