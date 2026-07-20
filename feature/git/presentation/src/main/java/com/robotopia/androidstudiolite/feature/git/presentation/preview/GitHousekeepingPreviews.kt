package com.robotopia.androidstudiolite.feature.git.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import com.robotopia.androidstudiolite.feature.git.api.GitBranchKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.previewCommitFilesFor
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.previewHistoryCommits

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Housekeeping · discard file confirm",
)
@Composable
private fun DiscardFileConfirmPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            changeFiles = sampleChangeFiles(),
            discardConfirmPath = "app/src/main/java/MainActivity.kt",
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Housekeeping · discard all confirm",
)
@Composable
private fun DiscardAllConfirmPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            changeFiles = sampleChangeFiles(),
            showDiscardAllConfirm = true,
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Housekeeping · change file menu",
)
@Composable
private fun ChangeFileMenuPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            changeFiles = sampleChangeFiles(),
            changeFileMenuPath = "scratch.tmp",
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Housekeeping · after ignore",
)
@Composable
private fun AfterIgnorePreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            changeFiles = sampleChangeFiles().filterNot { it.path == "scratch.tmp" },
            toastMessage = "Added to .gitignore.",
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Housekeeping · ahead behind",
)
@Composable
private fun AheadBehindPreview() {
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "main",
            recentBranches = listOf(main),
            localBranches = listOf(main),
            remoteBranches = listOf(GitBranch("origin/main", GitBranchKind.Remote)),
            hasRemote = true,
            aheadCount = 3,
            behindCount = 1,
            remoteHtmlUrl = "https://github.com/alex/MyApp",
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Housekeeping · undo commit confirm",
)
@Composable
private fun UndoCommitConfirmPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.History,
            currentBranch = "main",
            historyCommits = previewHistoryCommits(),
            showUndoCommitConfirm = true,
            hasRemote = true,
            aheadCount = 0,
            remoteHtmlUrl = "https://github.com/alex/MyApp",
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Housekeeping · after undo commit",
)
@Composable
private fun AfterUndoCommitPreview() {
    val tip = previewHistoryCommits().first()
    val restored = previewCommitFilesFor(tip.id).map {
        GitChangeFile(it.path, it.kind, staged = true)
    }
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            historyCommits = previewHistoryCommits().drop(1),
            changeFiles = restored,
            toastMessage = "Undid commit ${tip.shortId}.",
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Housekeeping · open commit on GitHub",
)
@Composable
private fun OpenCommitOnGitHubPreview() {
    val commit = previewHistoryCommits().first()
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.History,
            currentBranch = "main",
            historyCommits = previewHistoryCommits(),
            selectedCommit = commit,
            selectedCommitFiles = previewCommitFilesFor(commit.id),
            hasRemote = true,
            remoteHtmlUrl = "https://github.com/alex/MyApp",
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Housekeeping · open branch on GitHub",
)
@Composable
private fun OpenBranchOnGitHubPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.History,
            currentBranch = "feature/login",
            historyCommits = previewHistoryCommits().take(2),
            hasRemote = true,
            remoteHtmlUrl = "https://github.com/alex/MyApp",
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Housekeeping · discarded toast",
)
@Composable
private fun DiscardedToastPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            changeFiles = listOf(
                GitChangeFile("README.md", GitChangeKind.Modified),
            ),
            toastMessage = "Discarded changes in MainActivity.kt.",
        ),
    )
}
