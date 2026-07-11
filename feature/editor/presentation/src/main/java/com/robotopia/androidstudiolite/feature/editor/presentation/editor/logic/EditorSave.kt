package com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.designsystem.component.ToastVariant
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorScreenContext
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorToast
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "Editor"
private const val GENERIC_ERROR_MESSAGE = "Couldn't save file"
private const val AUTO_SAVE_DEBOUNCE_MS = 400L

internal fun EditorScreenContext.onContentChange(state: EditorUiState, content: String) {
    editorSession.updateContent(content)
    updateState { copy(toast = null) }
    if (!editorPreferences.autoSave.value) {
        cancelAutoSave()
        return
    }
    scheduleAutoSave(state, content)
}

internal fun EditorScreenContext.saveDocument(
    state: EditorUiState,
    showToast: Boolean = true,
    onSuccess: (() -> Unit)? = null,
) {
    val content = editorSession.document.value?.content ?: return
    cancelAutoSave()
    scope.launch {
        val saved = persist(state = state, content = content, showToast = showToast)
        if (!saved) return@launch
        updateState { copy(menuOpen = false) }
        onSuccess?.invoke()
    }
}

internal fun EditorScreenContext.toggleAutoSave(state: EditorUiState) {
    val enabling = !editorPreferences.autoSave.value
    editorPreferences.setAutoSave(enabling)
    updateState { copy(menuOpen = false) }
    val document = editorSession.document.value
    if (enabling && document?.isDirty == true) {
        scheduleAutoSave(state, document.content)
    } else {
        cancelAutoSave()
    }
}

internal suspend fun EditorScreenContext.persist(
    state: EditorUiState,
    content: String,
    showToast: Boolean,
): Boolean {
    return runCatching {
        documentStore.save(state.root, state.documentId.relativePath, content)
        editorSession.markSaved(content)
        updateState {
            copy(
                toast = if (showToast) {
                    EditorToast("File saved", ToastVariant.Success)
                } else {
                    toast
                },
            )
        }
    }.onFailure { error ->
        updateState {
            copy(
                toast = EditorToast(
                    message = error.userMessageOrNull(TAG) ?: GENERIC_ERROR_MESSAGE,
                    variant = ToastVariant.Error,
                ),
            )
        }
    }.isSuccess
}

private fun EditorScreenContext.scheduleAutoSave(state: EditorUiState, content: String) {
    cancelAutoSave()
    autoSaveJob = scope.launch {
        delay(AUTO_SAVE_DEBOUNCE_MS)
        persist(state = state, content = content, showToast = false)
    }
}

private fun EditorScreenContext.cancelAutoSave() {
    autoSaveJob?.cancel()
    autoSaveJob = null
}
