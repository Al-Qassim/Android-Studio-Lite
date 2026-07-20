package com.robotopia.androidstudiolite.feature.files.presentation.browser

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.robotopia.androidstudiolite.designsystem.component.InsetDivider
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
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

    IslandScaffold(
        topBar = {
            TopBarBackTitleAdd(
                title = state.projectName,
                onBackClick = { navigateUp(state) },
                onRunClick = onRun,
                onGitClick = onOpenGit,
                onAddClick = { openAddMenu() },
            )
        },
        footer = state.clipboard?.let { clipboard ->
            {
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
        },
    ) {
        val pathSegments = relativePathSegments(state.currentRelativePath)
        PathBar(segments = pathSegments)
        InsetDivider()
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            FileBrowserBody(state)
        }
    }

    FileBrowserAddMenu(state)
    FileBrowserDialogs(state)
}

private fun relativePathSegments(relativePath: String): List<String> {
    val parts = relativePath.split('/').filter { it.isNotEmpty() }
    return listOf("/") + parts
}
