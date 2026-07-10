package com.robotopia.androidstudiolite.feature.editor.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.robotopia.androidstudiolite.feature.editor.api.DocumentStore
import com.robotopia.androidstudiolite.feature.editor.api.EditorScreens
import com.robotopia.androidstudiolite.feature.editor.api.EditorSession
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorScreen
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorScreenContext
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorViewModel
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.flow.update
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class DefaultEditorScreens(
    private val editorSession: EditorSession,
    private val documentStore: DocumentStore,
) : EditorScreens {

    @Composable
    override fun NavHost(
        documentId: DocumentId,
        root: ProjectRoot,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)?,
    ) {
        Editor(
            documentId = documentId,
            root = root,
            onNavigateBack = onNavigateBack,
            onRun = onRun,
        )
    }

    @Composable
    override fun Editor(
        documentId: DocumentId,
        root: ProjectRoot,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)?,
    ) {
        val viewModel: EditorViewModel = koinViewModel {
            parametersOf(documentId, root)
        }
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        val screenContext = EditorScreenContext(
            updateState = { updater -> viewModel.uiState.update { updater(it) } },
            editorSession = editorSession,
            documentStore = documentStore,
            root = root,
            onNavigateBack = onNavigateBack,
            onRun = onRun,
            scope = viewModel.viewModelScope,
        )
        screenContext.EditorScreen(state)
    }
}
