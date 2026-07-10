package com.robotopia.androidstudiolite.feature.editor.presentation.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.feature.editor.api.DocumentStore
import com.robotopia.androidstudiolite.feature.editor.api.EditorSession
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.editor.model.OpenDocument
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal data class EditorPreviewCase(
    private val label: String,
    val state: EditorUiState,
) {
    override fun toString(): String = label
}

internal class EditorPreviewProvider : PreviewParameterProvider<EditorPreviewCase> {
    override fun getDisplayName(index: Int): String = values.toList()[index].toString()

    override val values = sequenceOf(
        EditorPreviewCase(
            "loading",
            previewBaseState().copy(isLoading = true),
        ),
        EditorPreviewCase(
            "ready",
            previewBaseState().copy(
                isLoading = false,
                content = "fun main() {\n    println(\"Hello\")\n}\n",
            ),
        ),
        EditorPreviewCase(
            "dirty",
            previewBaseState().copy(
                isLoading = false,
                content = "fun main() {}\n",
                isDirty = true,
            ),
        ),
        EditorPreviewCase(
            "menu open",
            previewBaseState().copy(
                isLoading = false,
                content = "package demo\n",
                menuOpen = true,
                autoSave = false,
            ),
        ),
        EditorPreviewCase(
            "unsaved leave",
            previewBaseState().copy(
                isLoading = false,
                content = "changed",
                isDirty = true,
                dialog = EditorDialog.UnsavedLeave,
            ),
        ),
        EditorPreviewCase(
            "load error",
            previewBaseState().copy(
                isLoading = false,
                loadError = "File not found",
            ),
        ),
        EditorPreviewCase(
            "saved toast",
            previewBaseState().copy(
                isLoading = false,
                content = "ok",
                toastMessage = "File saved",
            ),
        ),
    )
}

@Composable
internal fun EditorPreviewHost(state: EditorUiState) {
    val scope = rememberCoroutineScope()
    val context = remember(scope) {
        EditorScreenContext(
            updateState = {},
            editorSession = PreviewEditorSession,
            documentStore = PreviewDocumentStore,
            root = state.root,
            onNavigateBack = {},
            onRun = null,
            scope = scope,
        )
    }
    context.EditorScreen(state)
}

private fun previewBaseState() = EditorUiState(
    documentId = DocumentId(ProjectId("preview"), "app/src/main/java/MainActivity.kt"),
    root = ProjectRoot("/preview"),
    fileName = "MainActivity.kt",
)

private object PreviewEditorSession : EditorSession {
    private val _document = MutableStateFlow<OpenDocument?>(null)
    override val document: StateFlow<OpenDocument?> = _document.asStateFlow()
    override fun open(id: DocumentId, initialContent: String) = Unit
    override fun updateContent(content: String) = Unit
    override fun markSaved(content: String) = Unit
    override fun close() = Unit
}

private object PreviewDocumentStore : DocumentStore {
    override suspend fun load(root: ProjectRoot, relativePath: String): String = ""
    override suspend fun save(root: ProjectRoot, relativePath: String, content: String) = Unit
}
