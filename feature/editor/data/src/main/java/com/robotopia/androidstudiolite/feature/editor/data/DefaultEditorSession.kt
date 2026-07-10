package com.robotopia.androidstudiolite.feature.editor.data

import com.robotopia.androidstudiolite.feature.editor.api.EditorSession
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.editor.model.OpenDocument
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultEditorSession : EditorSession {
    private val _document = MutableStateFlow<OpenDocument?>(null)
    override val document: StateFlow<OpenDocument?> = _document.asStateFlow()

    override fun open(id: DocumentId, initialContent: String) {
        _document.value = OpenDocument(id, initialContent, isDirty = false)
    }

    override fun updateContent(content: String) {
        val current = _document.value ?: return
        _document.value = current.copy(content = content, isDirty = true)
    }

    override fun markSaved(content: String) {
        val current = _document.value ?: return
        _document.value = current.copy(content = content, isDirty = false)
    }

    override fun close() {
        _document.value = null
    }
}
