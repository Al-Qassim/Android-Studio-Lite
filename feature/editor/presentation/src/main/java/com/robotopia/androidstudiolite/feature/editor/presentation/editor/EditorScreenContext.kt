package com.robotopia.androidstudiolite.feature.editor.presentation.editor

import com.robotopia.androidstudiolite.feature.editor.api.DocumentStore
import com.robotopia.androidstudiolite.feature.editor.api.EditorSession
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.CoroutineScope

class EditorScreenContext(
    val updateState: (EditorUiState.() -> EditorUiState) -> Unit,
    val editorSession: EditorSession,
    val documentStore: DocumentStore,
    val root: ProjectRoot,
    val onNavigateBack: () -> Unit,
    val onRun: (() -> Unit)?,
    val scope: CoroutineScope,
)
