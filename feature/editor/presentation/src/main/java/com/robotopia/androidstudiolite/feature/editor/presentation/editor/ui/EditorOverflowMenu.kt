package com.robotopia.androidstudiolite.feature.editor.presentation.editor.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.robotopia.androidstudiolite.designsystem.component.EditorMenu
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorScreenContext
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorUiState
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.closeMenu
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.saveDocument
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.toggleAutoSave

@Composable
internal fun EditorScreenContext.EditorOverflowMenu(state: EditorUiState, autoSave: Boolean) {
    if (!state.menuOpen) return
    Popup(
        alignment = Alignment.TopEnd,
        onDismissRequest = { closeMenu() },
        properties = PopupProperties(focusable = true),
    ) {
        EditorMenu(
            autoSave = autoSave,
            onAutoSaveToggle = { toggleAutoSave(state) },
            onSave = { saveDocument(state) },
            showEditorSettings = false,
            showRename = false,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 48.dp, end = 12.dp),
        )
    }
}
