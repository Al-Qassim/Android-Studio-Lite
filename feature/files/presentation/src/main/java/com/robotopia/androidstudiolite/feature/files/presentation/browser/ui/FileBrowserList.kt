package com.robotopia.androidstudiolite.feature.files.presentation.browser.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.ContextMenu
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.FileRow
import com.robotopia.androidstudiolite.designsystem.component.FolderRow
import com.robotopia.androidstudiolite.designsystem.typography.Typography
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
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
internal fun FileBrowserScreenContext.FileBrowserBody(state: FileBrowserUiState) {
    if (state.entries.isEmpty()) {
        FileBrowserEmpty()
    } else {
        FileBrowserList(state)
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
        item { FileBrowserFooterHint() }
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
                )
            }

            is FsNode.File -> {
                FileRow(
                    name = entry.name,
                    selected = menuOpen,
                    showChevron = false,
                    onClick = { openFile(entry) },
                    onLongClick = { openItemMenu(entry) },
                )
            }
        }
        if (menuOpen) {
            Popup(
                alignment = Alignment.TopEnd,
                onDismissRequest = { dismissItemMenu() },
                properties = PopupProperties(focusable = true),
            ) {
                ContextMenu(
                    onRename = { openRenameDialog(entry) },
                    onMove = { moveItem(entry) },
                    onCopy = { copyItem(entry) },
                    onDelete = { openDeleteDialog(entry) },
                    modifier = Modifier.padding(top = 4.dp, end = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun FileBrowserFooterHint() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(
            text = "Tap a folder to open, a file to edit",
            style = Typography.Caption.copy(
                color = Colors.Muted2,
                textAlign = TextAlign.Center,
            ),
        )
        BasicText(
            text = "Long-press for rename, move, copy, or delete",
            style = Typography.Caption.copy(
                color = Colors.Muted2,
                textAlign = TextAlign.Center,
            ),
        )
    }
}
