package com.robotopia.androidstudiolite.feature.editor.presentation.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.Toast
import com.robotopia.androidstudiolite.designsystem.component.TopBarEditorMore
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.dismissToast
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.loadDocument
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.openMenu
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.requestLeave
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.ui.EditorBody
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.ui.EditorDialogs
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.ui.EditorOverflowMenu
import kotlinx.coroutines.delay

@Composable
internal fun EditorScreenContext.EditorScreen(state: EditorUiState) {
    val document by editorSession.document.collectAsStateWithLifecycle()
    val openDocument = document?.takeIf { it.id == state.documentId }
    val autoSave by editorPreferences.autoSave.collectAsStateWithLifecycle()

    LaunchedEffect(state.documentId, state.root) {
        loadDocument(state)
    }

    LaunchedEffect(state.toast) {
        if (state.toast != null) {
            delay(2_000)
            dismissToast()
        }
    }

    BackHandler { requestLeave(state) }

    Box(modifier = Modifier.fillMaxSize()) {
        IslandScaffold(
            topBar = {
                TopBarEditorMore(
                    fileName = state.fileName,
                    isDirty = openDocument?.isDirty == true,
                    onBackClick = { requestLeave(state) },
                    onRunClick = onRun,
                    onMoreClick = { openMenu() },
                )
            },
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                EditorBody(state = state, content = openDocument?.content.orEmpty())
            }
        }

        state.toast?.let { toast ->
            Toast(
                message = toast.message,
                variant = toast.variant,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
            )
        }
    }

    EditorOverflowMenu(state = state, autoSave = autoSave)
    EditorDialogs(state)
}
