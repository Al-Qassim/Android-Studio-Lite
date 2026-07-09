package com.robotopia.androidstudiolite.feature.editor.impl

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.core.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.editor.api.DocumentId
import com.robotopia.androidstudiolite.feature.editor.api.DocumentStore
import com.robotopia.androidstudiolite.feature.editor.api.EditorScreens
import com.robotopia.androidstudiolite.feature.editor.api.EditorSession
import com.robotopia.androidstudiolite.feature.editor.api.OpenDocument
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.dsl.module

internal class StubEditorSession : EditorSession {
    private val _document = MutableStateFlow<OpenDocument?>(null)
    override val document: StateFlow<OpenDocument?> = _document.asStateFlow()
    override fun open(id: DocumentId, initialContent: String) {
        _document.value = OpenDocument(id, initialContent, isDirty = false)
    }
    override fun updateContent(content: String) {
        _document.value = _document.value?.copy(content = content, isDirty = true)
    }
    override fun markSaved(content: String) {
        _document.value = _document.value?.copy(content = content, isDirty = false)
    }
    override fun close() {
        _document.value = null
    }
}

internal class StubDocumentStore : DocumentStore {
    override suspend fun load(root: ProjectRoot, relativePath: String): String = ""
    override suspend fun save(root: ProjectRoot, relativePath: String, content: String) = Unit
}

internal class StubEditorScreens : EditorScreens {
    @Composable
    override fun Editor(
        documentId: DocumentId,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)?,
    ) {
        Text("Editor (stub)")
    }
}

val editorModule = module {
    single<EditorSession> { StubEditorSession() }
    single<DocumentStore> { StubDocumentStore() }
    single<EditorScreens> { StubEditorScreens() }
}
