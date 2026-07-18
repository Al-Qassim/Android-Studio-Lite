package com.robotopia.androidstudiolite.feature.files.presentation.browser.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.FileRow
import com.robotopia.androidstudiolite.designsystem.component.FolderRow
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.component.Menu
import com.robotopia.androidstudiolite.designsystem.component.MenuItem
import com.robotopia.androidstudiolite.designsystem.icon.IconCopy
import com.robotopia.androidstudiolite.designsystem.popup.topEndPopupOffset
import com.robotopia.androidstudiolite.feature.files.model.FsNode
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreenContext
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserUiState
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.copyItem
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.dismissItemMenu
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.moveItem
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.openDeleteDialog
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.openFile
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.openFolder
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.openItemMenu
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.openRenameDialog

@Composable
internal fun FileBrowserScreenContext.FileBrowserBody(state: FileBrowserUiState) {
    when {
        state.isLoading -> FileBrowserLoading()
        state.entries.isEmpty() -> FileBrowserEmpty()
        else -> FileBrowserList(state)
    }
}

@Composable
private fun FileBrowserLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LoadingIndicator(label = "Loading files…")
    }
}

@Composable
private fun FileBrowserEmpty() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        EmptyState(
            title = "Folder is empty",
            hint = "Tap + to create a file or folder.",
        )
    }
}

@Composable
private fun FileBrowserScreenContext.FileBrowserList(state: FileBrowserUiState) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.entries, key = { it.relativePath }) { entry ->
            FileBrowserListItem(
                state = state,
                entry = entry,
            )
        }
    }
}

@Composable
private fun FileBrowserScreenContext.FileBrowserListItem(
    state: FileBrowserUiState,
    entry: FsNode,
) {
    val menuOpen = state.menuItem?.relativePath == entry.relativePath
    Box {
        when (entry) {
            is FsNode.Folder -> {
                FolderRow(
                    name = entry.name,
                    selected = menuOpen,
                    onClick = { openFolder(entry) },
                    onLongClick = { openItemMenu(entry) },
                    onMenuClick = { openItemMenu(entry) },
                )
            }

            is FsNode.File -> {
                FileRow(
                    name = entry.name,
                    selected = menuOpen,
                    showChevron = false,
                    onClick = { openFile(entry) },
                    onLongClick = { openItemMenu(entry) },
                    onMenuClick = { openItemMenu(entry) },
                )
            }
        }
        if (menuOpen) {
            FileItemOverflowMenu(
                onRename = { openRenameDialog(entry) },
                onMove = { moveItem(entry) },
                onCopy = { copyItem(entry) },
                onDelete = { openDeleteDialog(entry) },
                onDismiss = { dismissItemMenu() },
            )
        }
    }
}

/** Below FileRow/FolderRow's ⋮ control: row 40.dp + small gap under the button. */
private val FileItemOverflowMenuTopOffset = 44.dp
private val FileItemOverflowMenuEndOffset = 8.dp

@Composable
private fun FileItemOverflowMenu(
    onRename: () -> Unit,
    onMove: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    Popup(
        alignment = Alignment.TopEnd,
        offset = topEndPopupOffset(
            top = FileItemOverflowMenuTopOffset,
            end = FileItemOverflowMenuEndOffset,
        ),
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        Menu(
            items = listOf(
                MenuItem.Button(label = "Rename", onClick = onRename),
                MenuItem.Button(label = "Move", onClick = onMove),
                MenuItem.Button(
                    label = "Copy",
                    onClick = onCopy,
                    icon = { tint, size -> IconCopy(tint = tint, size = size) },
                ),
                MenuItem.Divider,
                MenuItem.Button(label = "Delete", onClick = onDelete, danger = true),
            ),
        )
    }
}

