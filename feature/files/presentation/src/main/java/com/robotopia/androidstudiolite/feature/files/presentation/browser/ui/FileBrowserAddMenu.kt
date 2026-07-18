package com.robotopia.androidstudiolite.feature.files.presentation.browser.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.robotopia.androidstudiolite.designsystem.component.Menu
import com.robotopia.androidstudiolite.designsystem.component.MenuItem
import com.robotopia.androidstudiolite.designsystem.icon.IconFile
import com.robotopia.androidstudiolite.designsystem.icon.IconFolder
import com.robotopia.androidstudiolite.designsystem.popup.topEndPopupOffset
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreenContext
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserUiState
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.dismissAddMenu
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.openCreateFileDialog
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.openCreateFolderDialog

/** Below the file-browser top bar add control. */
private val AddMenuTopOffset = 48.dp
private val AddMenuEndOffset = 12.dp

@Composable
internal fun FileBrowserScreenContext.FileBrowserAddMenu(state: FileBrowserUiState) {
    if (!state.addMenuOpen) return
    Popup(
        alignment = Alignment.TopEnd,
        offset = topEndPopupOffset(
            top = AddMenuTopOffset,
            end = AddMenuEndOffset,
            includeStatusBars = true,
        ),
        onDismissRequest = { dismissAddMenu() },
        properties = PopupProperties(focusable = true),
    ) {
        Menu(
            items = listOf(
                MenuItem.Button(
                    label = "New file",
                    onClick = { openCreateFileDialog() },
                    icon = { tint, size -> IconFile(tint = tint, size = size) },
                ),
                MenuItem.Button(
                    label = "New folder",
                    onClick = { openCreateFolderDialog() },
                    icon = { tint, size -> IconFolder(tint = tint, size = size) },
                ),
            ),
        )
    }
}
