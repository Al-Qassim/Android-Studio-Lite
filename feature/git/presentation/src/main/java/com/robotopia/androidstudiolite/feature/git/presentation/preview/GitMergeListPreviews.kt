package com.robotopia.androidstudiolite.feature.git.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import com.robotopia.androidstudiolite.feature.git.api.GitBranchKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Merge · confirm dialog")
@Composable
private fun MergeConfirmDialogPreview() {
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    val feature = GitBranch("feature/login", GitBranchKind.Local)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "main",
            recentBranches = listOf(main, feature),
            localBranches = listOf(main, feature),
            mergeConfirmBranch = "feature/login",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Merge · conflicts list")
@Composable
private fun MergeConflictsListPreview() {
    PreviewProjectGit(
        state = mergeConflictsState(),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Merge · one conflict left")
@Composable
private fun MergeOneConflictLeftPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            mergeSourceBranch = "feature/login",
            changeFiles = listOf(
                GitChangeFile("app/src/main/java/Nav.kt", GitChangeKind.Modified, staged = true),
                GitChangeFile("app/src/main/java/Theme.kt", GitChangeKind.Conflict, staged = false),
                GitChangeFile("README.md", GitChangeKind.Modified, staged = true),
            ),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Merge · ready to commit")
@Composable
private fun MergeReadyToCommitPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            mergeSourceBranch = "feature/login",
            changeFiles = listOf(
                GitChangeFile("app/src/main/java/Nav.kt", GitChangeKind.Modified, staged = true),
                GitChangeFile("app/src/main/java/Theme.kt", GitChangeKind.Modified, staged = true),
                GitChangeFile("README.md", GitChangeKind.Modified, staged = true),
            ),
            commitMessage = "Merge branch 'feature/login' into main",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Merge · abort confirm")
@Composable
private fun MergeAbortConfirmPreview() {
    PreviewProjectGit(
        state = mergeConflictsState().copy(showAbortMergeConfirm = true),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Merge · busy overlay")
@Composable
private fun MergeBusyOverlayPreview() {
    PreviewProjectGit(
        state = mergeConflictsState().copy(isBusy = true),
    )
}
