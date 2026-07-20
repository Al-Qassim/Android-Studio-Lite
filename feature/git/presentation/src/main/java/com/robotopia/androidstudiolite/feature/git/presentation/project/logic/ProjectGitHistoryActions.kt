package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitCommitFileChange
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitCommitSummary
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLine
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLineKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

fun ProjectGitScreenContext.openCommit(commit: GitCommitSummary) {
    scope.launch {
        updateState {
            copy(
                selectedCommit = commit,
                selectedCommitFiles = emptyList(),
                selectedDiffPath = null,
                diffTitle = "",
                diffLines = emptyList(),
                isConflictEditor = false,
                conflictText = "",
                conflictLinePaint = emptyList(),
                isBusy = true,
                actionError = null,
            )
        }
        try {
            val files = gitService.commitFiles(projectRoot, commit.id).map { it.toUiFile() }
            updateState {
                copy(
                    isBusy = false,
                    selectedCommitFiles = files,
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            updateState {
                copy(
                    isBusy = false,
                    selectedCommit = null,
                    actionError = e.userMessageOrNull(TAG) ?: "Couldn't open that commit.",
                )
            }
        }
    }
}

fun ProjectGitScreenContext.closeCommitDetail() {
    updateState {
        copy(
            selectedCommit = null,
            selectedCommitFiles = emptyList(),
            selectedDiffPath = null,
            diffTitle = "",
            diffLines = emptyList(),
            isConflictEditor = false,
            conflictText = "",
            conflictLinePaint = emptyList(),
        )
    }
}

fun ProjectGitScreenContext.openCommitFileDiff(file: GitCommitFileChange) {
    scope.launch {
        val commitId = run {
            var id: String? = null
            updateState {
                id = selectedCommit?.id
                copy(
                    selectedDiffPath = file.path,
                    diffTitle = file.path.substringAfterLast('/'),
                    isDiffLoading = true,
                    isConflictEditor = false,
                    conflictText = "",
                    conflictLinePaint = emptyList(),
                    diffLines = emptyList(),
                )
            }
            id
        } ?: return@launch
        try {
            val lines = gitService.diffCommit(projectRoot, commitId, file.path).map { it.toUiLine() }
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
            updateState {
                copy(
                    isDiffLoading = false,
                    selectedDiffPath = null,
                    actionError = e.userMessageOrNull(TAG) ?: "Couldn't open that diff.",
                )
            }
        }
    }
}

/** Placeholder history for Compose previews. */
fun previewHistoryCommits(): List<GitCommitSummary> = listOf(
    GitCommitSummary(
        id = "a1b2c3d4e5f6789012345678abcdef0123456789",
        shortId = "a1b2c3d",
        subject = "Add login screen and wire navigation",
        authorName = "Alex Chen",
        authoredRelative = "2 hours ago",
    ),
    GitCommitSummary(
        id = "b2c3d4e5f6789012345678abcdef0123456789a1",
        shortId = "b2c3d4e",
        subject = "Fix crash when theme preference is missing",
        authorName = "Alex Chen",
        authoredRelative = "Yesterday",
    ),
    GitCommitSummary(
        id = "c3d4e5f6789012345678abcdef0123456789a1b2",
        shortId = "c3d4e5f",
        subject = "Merge branch 'feature/login' into main",
        authorName = "Alex Chen",
        authoredRelative = "2 days ago",
    ),
    GitCommitSummary(
        id = "d4e5f6789012345678abcdef0123456789a1b2c3",
        shortId = "d4e5f67",
        subject = "Initial project import",
        authorName = "Alex Chen",
        authoredRelative = "Last week",
    ),
)

fun previewCommitFilesFor(commitId: String): List<GitCommitFileChange> = when {
    commitId.startsWith("a1b2c3d") -> listOf(
        GitCommitFileChange("app/src/main/java/LoginScreen.kt", GitChangeKind.Added),
        GitCommitFileChange("app/src/main/java/MainActivity.kt", GitChangeKind.Modified),
        GitCommitFileChange("app/src/main/java/Nav.kt", GitChangeKind.Modified),
    )
    commitId.startsWith("b2c3d4e") -> listOf(
        GitCommitFileChange("app/src/main/java/Theme.kt", GitChangeKind.Modified),
    )
    commitId.startsWith("c3d4e5f") -> listOf(
        GitCommitFileChange("app/src/main/java/Nav.kt", GitChangeKind.Modified),
        GitCommitFileChange("app/src/main/java/Theme.kt", GitChangeKind.Modified),
        GitCommitFileChange("README.md", GitChangeKind.Modified),
    )
    else -> listOf(
        GitCommitFileChange("README.md", GitChangeKind.Added),
        GitCommitFileChange("app/build.gradle.kts", GitChangeKind.Added),
        GitCommitFileChange("settings.gradle.kts", GitChangeKind.Added),
    )
}

private const val TAG = "ProjectGit"
