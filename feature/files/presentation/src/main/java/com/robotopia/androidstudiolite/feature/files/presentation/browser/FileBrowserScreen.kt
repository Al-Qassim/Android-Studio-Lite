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

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · empty",
)
@Composable
private fun FileBrowserEmptyPreview() {
    FileBrowserPreviewHost(fileBrowserEmptyPreviewState())
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · filled",
)
@Composable
private fun FileBrowserFilledPreview() {
    FileBrowserPreviewHost(fileBrowserFilledPreviewState())
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · nested path",
)
@Composable
private fun FileBrowserNestedPathPreview() {
    FileBrowserPreviewHost(fileBrowserNestedPathPreviewState())
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · create file",
)
@Composable
private fun FileBrowserCreateFilePreview() {
    FileBrowserPreviewHost(fileBrowserCreateFilePreviewState())
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · create file field error",
)
@Composable
private fun FileBrowserCreateFileFieldErrorPreview() {
    FileBrowserPreviewHost(fileBrowserCreateFileFieldErrorPreviewState())
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · rename field error",
)
@Composable
private fun FileBrowserRenameFieldErrorPreview() {
    FileBrowserPreviewHost(fileBrowserRenameFieldErrorPreviewState())
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · menu open",
)
@Composable
private fun FileBrowserMenuPreview() {
    FileBrowserPreviewHost(fileBrowserMenuPreviewState())
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · delete confirm",
)
@Composable
private fun FileBrowserDeletePreview() {
    FileBrowserPreviewHost(fileBrowserDeletePreviewState())
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · move bar",
)
@Composable
private fun FileBrowserMoveBarPreview() {
    FileBrowserPreviewHost(fileBrowserMoveBarPreviewState())
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · copy bar",
)
@Composable
private fun FileBrowserCopyBarPreview() {
    FileBrowserPreviewHost(fileBrowserCopyBarPreviewState())
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · action error",
)
@Composable
private fun FileBrowserActionErrorPreview() {
    FileBrowserPreviewHost(fileBrowserActionErrorPreviewState())
}
