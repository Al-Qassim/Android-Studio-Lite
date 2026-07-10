package com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorScreenContext
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorUiState
import kotlinx.coroutines.launch

private const val TAG = "Editor"
private const val GENERIC_ERROR_MESSAGE = "Something went wrong"

internal fun EditorScreenContext.onContentChange(content: String) {
    editorSession.updateContent(content)
    val dirty = editorSession.document.value?.isDirty == true
    updateState {
        copy(content = content, isDirty = dirty, toastMessage = null, actionError = null)
    }
}

internal fun EditorScreenContext.saveDocument(
    state: EditorUiState,
    showToast: Boolean = true,
    onSuccess: (() -> Unit)? = null,
) {
    scope.launch {
        val saved = persist(state)
        if (!saved) return@launch
        updateState {
            copy(
                menuOpen = false,
                toastMessage = if (showToast) "File saved" else toastMessage,
            )
        }
        onSuccess?.invoke()
    }
}

internal fun EditorScreenContext.toggleAutoSave(state: EditorUiState) {
    updateState { copy(autoSave = !state.autoSave, menuOpen = false) }
}

internal suspend fun EditorScreenContext.persist(state: EditorUiState): Boolean {
    return runCatching {
        documentStore.save(state.root, state.documentId.relativePath, state.content)
        editorSession.markSaved(state.content)
        updateState {
            copy(isDirty = false, actionError = null)
        }
    }.onFailure { error ->
        updateState {
            copy(actionError = error.userMessageOrNull(TAG) ?: GENERIC_ERROR_MESSAGE)
        }
    }.isSuccess
}
