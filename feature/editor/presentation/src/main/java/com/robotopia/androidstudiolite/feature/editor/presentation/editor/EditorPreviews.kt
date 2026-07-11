package com.robotopia.androidstudiolite.feature.editor.presentation.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.designsystem.component.ToastVariant
import com.robotopia.androidstudiolite.feature.editor.api.DocumentStore
import com.robotopia.androidstudiolite.feature.editor.api.EditorPreferences
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
    val document: OpenDocument? = null,
    val autoSave: Boolean = true,
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
            previewBaseState().copy(isLoading = false),
            document = previewDocument(
                content = "fun main() {\n    println(\"Hello\")\n}\n",
            ),
        ),
        EditorPreviewCase(
            "dirty",
            previewBaseState().copy(isLoading = false),
            document = previewDocument(content = "fun main() {}\n", isDirty = true),
            autoSave = false,
        ),
        EditorPreviewCase(
            "menu open",
            previewBaseState().copy(isLoading = false, menuOpen = true),
            document = previewDocument(content = "package demo\n"),
            autoSave = true,
        ),
        EditorPreviewCase(
            "unsaved leave",
            previewBaseState().copy(
                isLoading = false,
                dialog = EditorDialog.UnsavedLeave,
            ),
            document = previewDocument(content = "changed", isDirty = true),
            autoSave = false,
        ),
        EditorPreviewCase(
            "load error",
            previewBaseState().copy(
                isLoading = false,
                loadError = "File may have been moved or deleted.",
            ),
        ),
        EditorPreviewCase(
            "saved toast",
            previewBaseState().copy(
                isLoading = false,
                toast = EditorToast("File saved", ToastVariant.Success),
            ),
            document = previewDocument(content = "ok"),
        ),
        EditorPreviewCase(
            "save error toast",
            previewBaseState().copy(
                isLoading = false,
                toast = EditorToast("Couldn't save file", ToastVariant.Error),
            ),
            document = previewDocument(content = "ok", isDirty = true),
            autoSave = false,
        ),
    )
}

@Composable
internal fun EditorPreviewHost(
    state: EditorUiState,
    document: OpenDocument? = null,
    autoSave: Boolean = true,
) {
    val scope = rememberCoroutineScope()
    val session = remember(document) { PreviewEditorSession(document) }
    val preferences = remember(autoSave) { PreviewEditorPreferences(autoSave) }
    val context = remember(scope, session, preferences) {
        EditorScreenContext(
            updateState = {},
            editorSession = session,
            documentStore = PreviewDocumentStore,
            editorPreferences = preferences,
            onNavigateBack = {},
            onRun = null,
            scope = scope,
        )
    }
    context.EditorScreen(state)
}

private fun previewDocumentId() =
    DocumentId(ProjectId("preview"), "app/src/main/java/MainActivity.kt")

private fun previewDocument(content: String, isDirty: Boolean = false) = OpenDocument(
    id = previewDocumentId(),
    content = content,
    isDirty = isDirty,
)

private fun previewBaseState() = EditorUiState(
    documentId = previewDocumentId(),
    root = ProjectRoot("/preview"),
    fileName = "MainActivity.kt",
)

private class PreviewEditorSession(
    initial: OpenDocument?,
) : EditorSession {
    private val _document = MutableStateFlow(initial)
    override val document: StateFlow<OpenDocument?> = _document.asStateFlow()
    override fun open(id: DocumentId, initialContent: String) = Unit
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

private class PreviewEditorPreferences(
    initialAutoSave: Boolean,
) : EditorPreferences {
    private val _autoSave = MutableStateFlow(initialAutoSave)
    override val autoSave: StateFlow<Boolean> = _autoSave.asStateFlow()
    override fun setAutoSave(enabled: Boolean) {
        _autoSave.value = enabled
    }
}

private object PreviewDocumentStore : DocumentStore {
    override suspend fun load(root: ProjectRoot, relativePath: String): String = ""
    override suspend fun save(root: ProjectRoot, relativePath: String, content: String) = Unit
}
