package com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorScreenContext
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorUiState
import kotlinx.coroutines.launch

private const val TAG = "Editor"
private const val GENERIC_ERROR_MESSAGE = "Something went wrong"

internal suspend fun EditorScreenContext.loadDocument(state: EditorUiState) {
    updateState {
        copy(isLoading = true, loadError = null, toast = null)
    }
    runCatching {
        documentStore.load(state.root, state.documentId.relativePath)
    }.onSuccess { content ->
        editorSession.open(state.documentId, content)
        updateState {
            copy(
                isLoading = false,
                loadError = null,
            )
        }
    }.onFailure { error ->
        updateState {
            copy(
                isLoading = false,
                loadError = error.userMessageOrNull(TAG) ?: GENERIC_ERROR_MESSAGE,
            )
        }
    }
}

internal fun EditorScreenContext.retryLoad(state: EditorUiState) {
    scope.launch { loadDocument(state) }
}
