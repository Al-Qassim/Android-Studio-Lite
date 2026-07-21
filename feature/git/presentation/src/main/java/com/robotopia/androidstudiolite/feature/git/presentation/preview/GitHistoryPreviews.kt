package com.robotopia.androidstudiolite.feature.git.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.previewCommitFilesFor
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.previewDiffLinesFor
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.previewHistoryCommits

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "History · current branch",
)
@Composable
private fun HistoryCurrentBranchPreview() {
    val commits = previewHistoryCommits()
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.History,
            currentBranch = "main",
            historyCommits = commits,
            changeFiles = sampleChangeFiles(),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "History · empty",
)
@Composable
private fun HistoryEmptyPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.History,
            currentBranch = "main",
            historyCommits = emptyList(),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "History · commit detail files",
)
@Composable
private fun HistoryCommitDetailPreview() {
    val commit = previewHistoryCommits().first()
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.History,
            currentBranch = "main",
            historyCommits = previewHistoryCommits(),
            selectedCommit = commit,
            selectedCommitFiles = previewCommitFilesFor(commit.id),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "History · single-file commit",
)
@Composable
private fun HistorySingleFileCommitPreview() {
    val commit = previewHistoryCommits()[1]
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.History,
            currentBranch = "main",
            historyCommits = previewHistoryCommits(),
            selectedCommit = commit,
            selectedCommitFiles = previewCommitFilesFor(commit.id),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "History · file diff from commit",
)
@Composable
private fun HistoryCommitFileDiffPreview() {
    val commit = previewHistoryCommits().first()
    val path = "app/src/main/java/LoginScreen.kt"
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.History,
            currentBranch = "main",
            historyCommits = previewHistoryCommits(),
            selectedCommit = commit,
            selectedCommitFiles = previewCommitFilesFor(commit.id),
            selectedDiffPath = path,
            diffTitle = "LoginScreen.kt",
            diffLines = previewDiffLinesFor(path),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "History · on feature branch",
)
@Composable
private fun HistoryFeatureBranchPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.History,
            currentBranch = "feature/login",
            historyCommits = previewHistoryCommits().take(2),
        ),
    )
}
