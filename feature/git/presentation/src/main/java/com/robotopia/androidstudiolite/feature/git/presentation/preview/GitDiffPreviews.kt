package com.robotopia.androidstudiolite.feature.git.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLine
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLineKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.previewDiffLinesFor

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Diff · open file from Changes")
@Composable
private fun DiffOpenFromChangesPreview() {
    val path = "app/src/main/java/MainActivity.kt"
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "feature/login",
            changeFiles = sampleChangeFiles(),
            selectedDiffPath = path,
            diffTitle = "MainActivity.kt",
            diffLines = previewDiffLinesFor(path),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Diff · deleted file")
@Composable
private fun DiffDeletedFilePreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            selectedDiffPath = "old/LegacyScreen.kt",
            diffTitle = "LegacyScreen.kt",
            diffLines = listOf(
                GitDiffLine(GitDiffLineKind.Remove, "package com.example.legacy", oldLine = 1),
                GitDiffLine(GitDiffLineKind.Remove, "", oldLine = 2),
                GitDiffLine(GitDiffLineKind.Remove, "class LegacyScreen", oldLine = 3),
                GitDiffLine(GitDiffLineKind.Remove, "fun render() = Unit", oldLine = 4),
            ),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Diff · new file")
@Composable
private fun DiffNewFilePreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            selectedDiffPath = "app/src/main/java/LoginScreen.kt",
            diffTitle = "LoginScreen.kt",
            diffLines = listOf(
                GitDiffLine(GitDiffLineKind.Add, "package com.example.app", newLine = 1),
                GitDiffLine(GitDiffLineKind.Add, "", newLine = 2),
                GitDiffLine(GitDiffLineKind.Add, "@Composable", newLine = 3),
                GitDiffLine(GitDiffLineKind.Add, "fun LoginScreen() {", newLine = 4),
                GitDiffLine(GitDiffLineKind.Add, "  Text(\"Sign in\")", newLine = 5),
                GitDiffLine(GitDiffLineKind.Add, "}", newLine = 6),
            ),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Diff · loading")
@Composable
private fun DiffLoadingPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            selectedDiffPath = "app/src/main/java/MainActivity.kt",
            diffTitle = "MainActivity.kt",
            isDiffLoading = true,
        ),
    )
}
