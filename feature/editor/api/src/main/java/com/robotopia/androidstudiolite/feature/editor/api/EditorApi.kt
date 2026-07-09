package com.robotopia.androidstudiolite.feature.editor.api

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.core.model.ProjectId
import com.robotopia.androidstudiolite.core.model.ProjectRoot
import kotlinx.coroutines.flow.StateFlow

data class DocumentId(
    val projectId: ProjectId,
    val relativePath: String,
)

data class OpenDocument(
    val id: DocumentId,
    val content: String,
    val isDirty: Boolean,
)

interface EditorSession {
    val document: StateFlow<OpenDocument?>
    fun open(id: DocumentId, initialContent: String)
    fun updateContent(content: String)
    fun markSaved(content: String)
    fun close()
}

interface DocumentStore {
    suspend fun load(root: ProjectRoot, relativePath: String): String
    suspend fun save(root: ProjectRoot, relativePath: String, content: String)
}

interface EditorScreens {
    @Composable
    fun Editor(
        documentId: DocumentId,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)? = null,
    )
}
