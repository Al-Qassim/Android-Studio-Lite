package com.robotopia.androidstudiolite.feature.git.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import com.robotopia.androidstudiolite.feature.git.api.GitBranchKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Branches · populated")
@Composable
private fun BranchesPopulatedPreview() {
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    val feature = GitBranch("feature/login", GitBranchKind.Local)
    val fix = GitBranch("fix/crash", GitBranchKind.Local)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "main",
            recentBranches = listOf(main, feature),
            localBranches = listOf(main, feature, fix),
            remoteBranches = listOf(
                GitBranch("origin/main", GitBranchKind.Remote),
                GitBranch("origin/develop", GitBranchKind.Remote),
            ),
            changeFiles = sampleChangeFiles(),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Branches · after fetch empty remote")
@Composable
private fun BranchesEmptyRemotePreview() {
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "main",
            recentBranches = listOf(main),
            localBranches = listOf(main),
            remoteBranches = emptyList(),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Branches · sync busy")
@Composable
private fun BranchesSyncBusyPreview() {
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "main",
            recentBranches = listOf(main),
            localBranches = listOf(main),
            isBusy = true,
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Branches · push failed")
@Composable
private fun BranchesPushFailedPreview() {
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "main",
            recentBranches = listOf(main),
            localBranches = listOf(main),
            actionError = "Push rejected. Pull and retry.",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Flow · create branch dialog")
@Composable
private fun FlowCreateBranchDialogPreview() {
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "main",
            recentBranches = listOf(main),
            localBranches = listOf(main),
            showCreateBranch = true,
            createBranchFrom = "main",
            createBranchValue = "feature/settings",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Flow · rename remote")
@Composable
private fun FlowRenameRemotePreview() {
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "main",
            recentBranches = listOf(main),
            localBranches = listOf(main),
            remoteBranches = listOf(GitBranch("origin/develop", GitBranchKind.Remote)),
            renameBranch = "origin/develop",
            renameValue = "develop",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Flow · branch menu")
@Composable
private fun FlowBranchMenuPreview() {
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    val feature = GitBranch("feature/login", GitBranchKind.Local)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "main",
            recentBranches = listOf(main, feature),
            localBranches = listOf(main, feature),
            menuBranch = feature,
            menuBranchKey = "local-${feature.name}",
        ),
    )
}
