package com.robotopia.androidstudiolite.feature.git.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLine
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLineKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.previewConflictLinesFor

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Merge · resolve editor")
@Composable
private fun MergeResolveEditorPreview() {
    val path = "app/src/main/java/Nav.kt"
    PreviewProjectGit(
        state = mergeConflictsState().copy(
            selectedDiffPath = path,
            diffTitle = "Nav.kt",
            isConflictEditor = true,
            diffLines = previewConflictLinesFor(
                path = path,
                currentBranch = "main",
                mergeSource = "feature/login",
            ),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Merge · ready to mark resolved")
@Composable
private fun MergeReadyToMarkResolvedPreview() {
    val path = "app/src/main/java/Nav.kt"
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            mergeSourceBranch = "feature/login",
            changeFiles = listOf(
                GitChangeFile(path, GitChangeKind.Conflict, staged = false),
                GitChangeFile("app/src/main/java/Theme.kt", GitChangeKind.Conflict, staged = false),
            ),
            selectedDiffPath = path,
            diffTitle = "Nav.kt",
            isConflictEditor = true,
            diffLines = listOf(
                GitDiffLine(GitDiffLineKind.Context, "package com.example.app", oldLine = 1, newLine = 1),
                GitDiffLine(GitDiffLineKind.Context, "", oldLine = 2, newLine = 2),
                GitDiffLine(GitDiffLineKind.Context, "@Composable", oldLine = 3, newLine = 3),
                GitDiffLine(GitDiffLineKind.Context, "fun Nav.kt() {", oldLine = 4, newLine = 4),
                GitDiffLine(GitDiffLineKind.Context, "    Text(\"Hello from main\")", oldLine = 5, newLine = 5),
                GitDiffLine(GitDiffLineKind.Context, "    Button(onClick = { /* current */ })", oldLine = 6, newLine = 6),
                GitDiffLine(GitDiffLineKind.Context, "}", oldLine = 7, newLine = 7),
            ),
            toastMessage = "Kept current changes.",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Merge · after mark resolved")
@Composable
private fun MergeAfterMarkResolvedPreview() {
    val path = "app/src/main/java/Nav.kt"
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            tab = ProjectGitTab.Changes,
            currentBranch = "main",
            mergeSourceBranch = "feature/login",
            changeFiles = listOf(
                GitChangeFile(path, GitChangeKind.Modified, staged = true),
                GitChangeFile("app/src/main/java/Theme.kt", GitChangeKind.Conflict, staged = false),
            ),
            selectedDiffPath = path,
            diffTitle = "Nav.kt",
            isConflictEditor = false,
            diffLines = listOf(
                GitDiffLine(GitDiffLineKind.Context, "package com.example.app", oldLine = 1, newLine = 1),
                GitDiffLine(GitDiffLineKind.Context, "", oldLine = 2, newLine = 2),
                GitDiffLine(GitDiffLineKind.Context, "@Composable", oldLine = 3, newLine = 3),
                GitDiffLine(GitDiffLineKind.Context, "fun Nav.kt() {", oldLine = 4, newLine = 4),
                GitDiffLine(GitDiffLineKind.Context, "    Text(\"Hello from main\")", oldLine = 5, newLine = 5),
                GitDiffLine(GitDiffLineKind.Context, "    Button(onClick = { /* current */ })", oldLine = 6, newLine = 6),
                GitDiffLine(GitDiffLineKind.Context, "}", oldLine = 7, newLine = 7),
            ),
            toastMessage = "Marked as resolved.",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Merge · both sides multi-hunk")
@Composable
private fun MergeMultiHunkConflictPreview() {
    val path = "app/src/main/java/AppNav.kt"
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            currentBranch = "main",
            mergeSourceBranch = "origin/develop",
            selectedDiffPath = path,
            diffTitle = "AppNav.kt",
            isConflictEditor = true,
            diffLines = listOf(
                GitDiffLine(GitDiffLineKind.Context, "sealed interface AppRoute {", oldLine = 1, newLine = 1),
                GitDiffLine(GitDiffLineKind.ConflictMarker, "<<<<<<< HEAD (main)"),
                GitDiffLine(GitDiffLineKind.ConflictOurs, "    data object Home : AppRoute", oldLine = 2, newLine = 2),
                GitDiffLine(GitDiffLineKind.ConflictMarker, "======="),
                GitDiffLine(GitDiffLineKind.ConflictTheirs, "    data object Hub : AppRoute", oldLine = null, newLine = 2),
                GitDiffLine(GitDiffLineKind.ConflictMarker, ">>>>>>> origin/develop"),
                GitDiffLine(GitDiffLineKind.Context, "    data object Settings : AppRoute", oldLine = 3, newLine = 3),
                GitDiffLine(GitDiffLineKind.ConflictMarker, "<<<<<<< HEAD (main)"),
                GitDiffLine(GitDiffLineKind.ConflictOurs, "    data object Build : AppRoute", oldLine = 4, newLine = 4),
                GitDiffLine(GitDiffLineKind.ConflictMarker, "======="),
                GitDiffLine(GitDiffLineKind.ConflictTheirs, "    data object CloudBuild : AppRoute", oldLine = null, newLine = 4),
                GitDiffLine(GitDiffLineKind.ConflictTheirs, "    data object History : AppRoute", oldLine = null, newLine = 5),
                GitDiffLine(GitDiffLineKind.ConflictMarker, ">>>>>>> origin/develop"),
                GitDiffLine(GitDiffLineKind.Context, "}", oldLine = 5, newLine = 6),
            ),
        ),
    )
}
