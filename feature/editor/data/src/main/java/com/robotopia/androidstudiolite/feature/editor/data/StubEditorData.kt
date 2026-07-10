package com.robotopia.androidstudiolite.feature.editor.data

import com.robotopia.androidstudiolite.feature.editor.api.DocumentStore
import com.robotopia.androidstudiolite.feature.editor.api.EditorSession
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.editor.model.OpenDocument
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StubEditorSession : EditorSession {
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

class StubDocumentStore : DocumentStore {
    override suspend fun load(root: ProjectRoot, relativePath: String): String = ""
    override suspend fun save(root: ProjectRoot, relativePath: String, content: String) = Unit
}
