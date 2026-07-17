package com.robotopia.androidstudiolite.feature.editor.presentation.editor.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.robotopia.androidstudiolite.designsystem.component.EditorMenu
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
        EditorMenu(
            autoSave = autoSave,
            wrapText = wrapText,
            onAutoSaveToggle = { toggleAutoSave(state) },
            onWrapTextToggle = { toggleWrapText() },
            onSave = { saveDocument(state) },
            showEditorSettings = false,
            showRename = false,
        )
    }
}
