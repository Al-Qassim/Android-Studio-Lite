package com.robotopia.androidstudiolite.feature.editor.api

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.editor.model.OpenDocument
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.flow.StateFlow

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
    /** Feature-owned entry; integration calls this rather than individual screens. */
    @Composable
    fun NavHost(
        documentId: DocumentId,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)?,
    )

    @Composable
    fun Editor(
        documentId: DocumentId,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)?,
    )
}
