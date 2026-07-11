package com.robotopia.androidstudiolite.feature.editor.presentation.editor

import com.robotopia.androidstudiolite.designsystem.component.ToastVariant
import com.robotopia.androidstudiolite.feature.editor.api.DocumentStore
import com.robotopia.androidstudiolite.feature.editor.api.EditorSession
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class EditorScreenContext(
    val updateState: (EditorUiState.() -> EditorUiState) -> Unit,
    val editorSession: EditorSession,
    val documentStore: DocumentStore,
    val onNavigateBack: () -> Unit,
    val onRun: (() -> Unit)?,
    val scope: CoroutineScope,
) {
    var autoSaveJob: Job? = null
}

sealed interface EditorDialog {
    data object UnsavedLeave : EditorDialog
}

data class EditorToast(
    val message: String,
    val variant: ToastVariant = ToastVariant.Success,
)

data class EditorUiState(
    val documentId: DocumentId,
    val root: ProjectRoot,
    val fileName: String = "",
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val autoSave: Boolean = true,
    val menuOpen: Boolean = false,
    val dialog: EditorDialog? = null,
    val toast: EditorToast? = null,
)
