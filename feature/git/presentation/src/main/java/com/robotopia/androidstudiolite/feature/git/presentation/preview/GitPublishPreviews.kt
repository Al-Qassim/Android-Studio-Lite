package com.robotopia.androidstudiolite.feature.git.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import com.robotopia.androidstudiolite.feature.git.api.GitBranchKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.previewHistoryCommits

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Publish · Branches no remote",
)
@Composable
private fun PublishBranchesNoRemotePreview() {
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "main",
            recentBranches = listOf(main),
            localBranches = listOf(main),
            hasRemote = false,
            historyCommits = previewHistoryCommits(),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Publish · connect account",
)
@Composable
private fun PublishConnectAccountPreview() {
    PreviewProjectGit(
        state = publishBaseState().copy(
            publishAccountConnected = false,
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Publish · form private",
)
@Composable
private fun PublishFormPrivatePreview() {
    PreviewProjectGit(
        state = publishBaseState().copy(
            publishRepoName = "MyApp",
            publishPrivate = true,
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Publish · form public",
)
@Composable
private fun PublishFormPublicPreview() {
    PreviewProjectGit(
        state = publishBaseState().copy(
            publishRepoName = "MyApp",
            publishPrivate = false,
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Publish · name error",
)
@Composable
private fun PublishNameErrorPreview() {
    PreviewProjectGit(
        state = publishBaseState().copy(
            publishRepoName = "my app",
            publishNameError = "Use a single name without spaces or slashes.",
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Publish · needs commit first",
)
@Composable
private fun PublishNeedsCommitPreview() {
    PreviewProjectGit(
        state = publishBaseState().copy(
            publishRepoName = "MyApp",
            publishNeedsCommit = true,
            historyCommits = emptyList(),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Publish · busy",
)
@Composable
private fun PublishBusyPreview() {
    PreviewProjectGit(
        state = publishBaseState().copy(
            publishRepoName = "MyApp",
            isBusy = true,
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Publish · API error",
)
@Composable
private fun PublishApiErrorPreview() {
    PreviewProjectGit(
        state = publishBaseState().copy(
            publishRepoName = "MyApp",
            publishError = "Couldn't create that repository. Try another name.",
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Publish · success",
)
@Composable
private fun PublishSuccessPreview() {
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
            historyCommits = previewHistoryCommits(),
            toastMessage = "Published to GitHub.",
        ),
    )
}

private fun publishBaseState() = ProjectGitUiState(
    isLoading = false,
    tab = ProjectGitTab.Branches,
    currentBranch = "main",
    showPublish = true,
    publishAccountConnected = true,
    publishProviderName = "GitHub",
    publishRepoName = "",
    publishPrivate = true,
    hasRemote = false,
    historyCommits = previewHistoryCommits(),
)
