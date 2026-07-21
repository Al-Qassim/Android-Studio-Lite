package com.robotopia.androidstudiolite.feature.git.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Changes · clean")
@Composable
private fun ChangesCleanPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            changeFiles = emptyList(),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Changes · uncommitted")
@Composable
private fun ChangesUncommittedPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "feature/login",
            changeFiles = sampleChangeFiles(),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Changes · message drafted")
@Composable
private fun ChangesCommitMessagePreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "feature/login",
            changeFiles = sampleChangeFiles(),
            commitMessage = "Add login screen and wire auth session.",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Changes · commit empty message")
@Composable
private fun ChangesCommitEmptyMessageErrorPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            changeFiles = sampleChangeFiles(),
            commitError = "Enter a commit message.",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Changes · nothing staged")
@Composable
private fun ChangesNothingStagedErrorPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            changeFiles = sampleChangeFiles().map { it.copy(staged = false) },
            commitMessage = "WIP",
            commitError = "Select at least one file to commit.",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Changes · partial stage")
@Composable
private fun ChangesPartialStagePreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            changeFiles = listOf(
                GitChangeFile("app/src/main/java/MainActivity.kt", GitChangeKind.Modified, staged = true),
                GitChangeFile("README.md", GitChangeKind.Modified, staged = false),
                GitChangeFile("notes.txt", GitChangeKind.Untracked, staged = false),
            ),
            commitMessage = "Update MainActivity entry point.",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Flow · toast after commit")
@Composable
private fun FlowToastAfterCommitPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "feature/login",
            changeFiles = listOf(
                GitChangeFile("README.md", GitChangeKind.Modified, staged = false),
            ),
            toastMessage = "Committed 2 file(s).",
        ),
    )
}
