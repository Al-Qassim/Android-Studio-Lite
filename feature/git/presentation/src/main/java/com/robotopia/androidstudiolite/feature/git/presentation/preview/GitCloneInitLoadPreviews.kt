package com.robotopia.androidstudiolite.feature.git.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.feature.git.presentation.clone.CloneProjectUiState
import com.robotopia.androidstudiolite.feature.git.presentation.clone.ui.CloneProjectBody
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Clone · idle")
@Composable
private fun CloneIdlePreview() {
    CloneProjectBody(
        state = CloneProjectUiState(url = "owner/repo"),
        onCancel = {},
        onUrlChange = {},
        onCloneClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Clone · error")
@Composable
private fun CloneErrorPreview() {
    CloneProjectBody(
        state = CloneProjectUiState(
            url = "bad",
            urlError = "Use https://github.com/owner/repo or owner/repo.",
        ),
        onCancel = {},
        onUrlChange = {},
        onCloneClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Init · empty project")
@Composable
private fun InitRepositoryPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            needsInit = true,
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Init · busy")
@Composable
private fun InitRepositoryBusyPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            needsInit = true,
            isBusy = true,
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Load · error")
@Composable
private fun ProjectGitLoadErrorPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(
            isLoading = false,
            loadError = "Couldn't open Git for this project.",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = GIT_PREVIEW_BG, widthDp = 360, heightDp = 640, name = "Load · spinner")
@Composable
private fun ProjectGitLoadingPreview() {
    PreviewProjectGit(
        state = ProjectGitUiState(isLoading = true),
    )
}
