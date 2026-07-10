package com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic

import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorDialog
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorScreenContext
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorUiState
import kotlinx.coroutines.launch

internal fun EditorScreenContext.requestLeave(state: EditorUiState) {
    when {
        state.actionError != null ->
            updateState { copy(actionError = null) }
        state.dialog != null ->
            updateState { copy(dialog = null) }
        state.menuOpen ->
            updateState { copy(menuOpen = false) }
        state.isDirty ->
            updateState { copy(dialog = EditorDialog.UnsavedLeave, menuOpen = false) }
        else ->
            leaveClean()
    }
}

internal fun EditorScreenContext.dismissDialog() {
    updateState { copy(dialog = null) }
}

internal fun EditorScreenContext.discardAndLeave() {
    updateState { copy(dialog = null) }
    leaveClean()
}

internal fun EditorScreenContext.saveAndLeave(state: EditorUiState) {
    scope.launch {
        val saved = persist(state)
        if (!saved) {
            updateState { copy(dialog = null) }
            return@launch
        }
        updateState { copy(dialog = null) }
        leaveClean()
    }
}

internal fun EditorScreenContext.leaveClean() {
    editorSession.close()
    onNavigateBack()
}

internal fun EditorScreenContext.openMenu() {
    updateState { copy(menuOpen = true) }
}

internal fun EditorScreenContext.closeMenu() {
    updateState { copy(menuOpen = false) }
}

internal fun EditorScreenContext.dismissActionError() {
    updateState { copy(actionError = null) }
}

internal fun EditorScreenContext.dismissToast() {
    updateState { copy(toastMessage = null) }
}
