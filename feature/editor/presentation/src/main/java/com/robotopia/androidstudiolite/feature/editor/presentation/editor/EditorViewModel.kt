package com.robotopia.androidstudiolite.feature.editor.presentation.editor

import androidx.lifecycle.ViewModel
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface EditorDialog {
    data object UnsavedLeave : EditorDialog
}

data class EditorUiState(
    val documentId: DocumentId,
    val root: ProjectRoot,
    val fileName: String = "",
    val content: String = "",
    val isDirty: Boolean = false,
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val autoSave: Boolean = false,
    val menuOpen: Boolean = false,
    val dialog: EditorDialog? = null,
    val actionError: String? = null,
    val toastMessage: String? = null,
)

class EditorViewModel(
    documentId: DocumentId,
    root: ProjectRoot,
) : ViewModel() {
    val uiState = MutableStateFlow(
        EditorUiState(
            documentId = documentId,
            root = root,
            fileName = fileNameFromPath(documentId.relativePath),
        ),
    )
}

internal fun fileNameFromPath(relativePath: String): String =
    relativePath.substringAfterLast('/').ifEmpty { relativePath }
