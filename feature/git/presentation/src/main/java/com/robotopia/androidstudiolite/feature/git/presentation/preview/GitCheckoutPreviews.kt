package com.robotopia.androidstudiolite.feature.git.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import com.robotopia.androidstudiolite.feature.git.api.GitBranchKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.CheckoutOverwritePrompt
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Checkout · dirty branches (safe carry-over ok)",
)
@Composable
private fun CheckoutDirtyBranchesPreview() {
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    val feature = GitBranch("feature/login", GitBranchKind.Local)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "main",
            recentBranches = listOf(main, feature),
            localBranches = listOf(main, feature),
            changeFiles = listOf(
                GitChangeFile("README.md", GitChangeKind.Modified),
                GitChangeFile("scratch.tmp", GitChangeKind.Untracked, staged = false),
            ),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Checkout · overwrite blocked dialog",
)
@Composable
private fun CheckoutOverwriteBlockedPreview() {
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    val feature = GitBranch("feature/login", GitBranchKind.Local)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "main",
            recentBranches = listOf(main, feature),
            localBranches = listOf(main, feature),
            changeFiles = listOf(
                GitChangeFile("app/src/main/java/MainActivity.kt", GitChangeKind.Modified),
                GitChangeFile("app/src/main/java/LoginScreen.kt", GitChangeKind.Added),
            ),
            checkoutOverwrite = CheckoutOverwritePrompt(
                targetBranch = "feature/login",
                conflictingPaths = listOf(
                    "app/src/main/java/MainActivity.kt",
                    "app/src/main/java/LoginScreen.kt",
                ),
            ),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Checkout · overwrite many files",
)
@Composable
private fun CheckoutOverwriteManyFilesPreview() {
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    val develop = GitBranch("develop", GitBranchKind.Local)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "main",
            recentBranches = listOf(main, develop),
            localBranches = listOf(main, develop),
            changeFiles = sampleChangeFiles(),
            checkoutOverwrite = CheckoutOverwritePrompt(
                targetBranch = "develop",
                conflictingPaths = listOf(
                    "app/src/main/java/MainActivity.kt",
                    "app/src/main/java/LoginScreen.kt",
                    "README.md",
                    "scratch.tmp",
                    "old/LegacyScreen.kt",
                    "app/build.gradle.kts",
                ),
            ),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Checkout · commit first → Changes",
)
@Composable
private fun CheckoutCommitFirstChangesPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            changeFiles = listOf(
                GitChangeFile("app/src/main/java/MainActivity.kt", GitChangeKind.Modified),
                GitChangeFile("app/src/main/java/LoginScreen.kt", GitChangeKind.Added),
            ),
            commitMessage = "",
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = GIT_PREVIEW_BG,
    widthDp = 360,
    heightDp = 640,
    name = "Checkout · after discard & switch",
)
@Composable
private fun CheckoutAfterDiscardSwitchPreview() {
    val main = GitBranch("main", GitBranchKind.Local)
    val feature = GitBranch("feature/login", GitBranchKind.Local, isCurrent = true)
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Branches,
            currentBranch = "feature/login",
            recentBranches = listOf(feature, main),
            localBranches = listOf(main, feature),
            changeFiles = emptyList(),
            toastMessage = "Discarded local changes and checked out feature/login.",
        ),
    )
}
