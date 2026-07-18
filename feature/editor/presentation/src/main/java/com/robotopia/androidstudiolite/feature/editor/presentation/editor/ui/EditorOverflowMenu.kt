package com.robotopia.androidstudiolite.feature.editor.presentation.editor.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Menu
import com.robotopia.androidstudiolite.designsystem.component.MenuItem
import com.robotopia.androidstudiolite.designsystem.icon.IconSave
import com.robotopia.androidstudiolite.designsystem.icon.IconSuccess
import com.robotopia.androidstudiolite.designsystem.icon.IconWrapText
import com.robotopia.androidstudiolite.designsystem.popup.topEndPopupOffset
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorScreenContext
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorUiState
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.closeMenu
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.saveDocument
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.toggleAutoSave
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.toggleWrapText

/** Below the editor top bar ⋮ control. */
private val EditorMenuTopOffset = 48.dp
private val EditorMenuEndOffset = 12.dp
private val EditorMenuWidth = 220.dp
private val EditorMenuCheckSize = 16.dp

@Composable
internal fun EditorScreenContext.EditorOverflowMenu(
    state: EditorUiState,
    autoSave: Boolean,
    wrapText: Boolean,
) {
    if (!state.menuOpen) return
    Popup(
        alignment = Alignment.TopEnd,
        offset = topEndPopupOffset(
            top = EditorMenuTopOffset,
            end = EditorMenuEndOffset,
            includeStatusBars = true,
        ),
        onDismissRequest = { closeMenu() },
        properties = PopupProperties(focusable = true),
    ) {
        Menu(
            width = EditorMenuWidth,
            items = listOf(
                MenuItem.Button(
                    label = "Save",
                    onClick = { saveDocument(state) },
                    enabled = !autoSave,
                    muted = autoSave,
                    icon = { tint, size -> IconSave(tint = tint, size = size) },
                ),
                MenuItem.Button(
                    label = "Auto save",
                    onClick = { toggleAutoSave(state) },
                    trailing = if (autoSave) {
                        { IconSuccess(tint = Colors.Primary, size = EditorMenuCheckSize) }
                    } else {
                        null
                    },
                ),
                MenuItem.Divider,
                MenuItem.Button(
                    label = "Wrap text",
                    onClick = { toggleWrapText() },
                    icon = { tint, size -> IconWrapText(tint = tint, size = size) },
                    trailing = if (wrapText) {
                        { IconSuccess(tint = Colors.Primary, size = EditorMenuCheckSize) }
                    } else {
                        null
                    },
                ),
            ),
        )
    }
}
