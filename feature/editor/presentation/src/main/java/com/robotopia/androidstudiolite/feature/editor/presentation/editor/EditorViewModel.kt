package com.robotopia.androidstudiolite.feature.editor.presentation.editor

import androidx.lifecycle.ViewModel
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.flow.MutableStateFlow

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
