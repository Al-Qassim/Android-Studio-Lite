package com.robotopia.androidstudiolite.feature.editor.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.editor.api.EditorScreens
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId

class StubEditorScreens : EditorScreens {
    @Composable
    override fun NavHost(
        documentId: DocumentId,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)?,
    ) {
        Editor(
            documentId = documentId,
            onNavigateBack = onNavigateBack,
            onRun = onRun,
        )
    }

    @Composable
    override fun Editor(
        documentId: DocumentId,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)?,
    ) {
        Text("Editor (stub)")
    }
}
