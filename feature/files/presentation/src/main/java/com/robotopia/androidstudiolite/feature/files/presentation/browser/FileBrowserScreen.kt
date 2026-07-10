package com.robotopia.androidstudiolite.feature.files.presentation.browser

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.MoveBar
import com.robotopia.androidstudiolite.designsystem.component.PathBar
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitleAdd
import com.robotopia.androidstudiolite.designsystem.component.TransferBarMode
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.clearClipboard
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.collectListing
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.navigateUp
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.openAddMenu
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.pasteClipboard
import com.robotopia.androidstudiolite.feature.files.presentation.browser.ui.FileBrowserAddMenu
import com.robotopia.androidstudiolite.feature.files.presentation.browser.ui.FileBrowserBody
import com.robotopia.androidstudiolite.feature.files.presentation.browser.ui.FileBrowserDialogs

@Composable
internal fun FileBrowserScreenContext.FileBrowserScreen(state: FileBrowserUiState) {
    LaunchedEffect(state.root, state.currentRelativePath) {
        collectListing(
            root = state.root,
            relativePath = state.currentRelativePath,
        )
    }

    BackHandler { navigateUp(state) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        TopBarBackTitleAdd(
            title = state.projectName,
            onBackClick = { navigateUp(state) },
            onAddClick = { openAddMenu() },
        )
        val pathSegments = relativePathSegments(state.currentRelativePath)
        if (pathSegments.isNotEmpty()) {
            PathBar(segments = pathSegments)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            FileBrowserBody(state)
        }
        state.clipboard?.let { clipboard ->
            MoveBar(
                name = clipboard.node.name,
                mode = when (clipboard.mode) {
                    ClipboardMode.Cut -> TransferBarMode.Move
                    ClipboardMode.Copy -> TransferBarMode.Copy
                },
                onCancel = { clearClipboard() },
                onMoveHere = { pasteClipboard(state) },
            )
        }
    }

    FileBrowserAddMenu(state)
    FileBrowserDialogs(state)
}

private fun relativePathSegments(relativePath: String): List<String> =
    relativePath.split('/').filter { it.isNotEmpty() }

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun FileBrowserPreview(
    @PreviewParameter(FileBrowserPreviewProvider::class) case: FileBrowserPreviewCase,
) {
    FileBrowserPreviewHost(case.state)
}
