package com.robotopia.androidstudiolite.feature.editor.presentation.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
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
    LaunchedEffect(state.documentId, state.root) {
        loadDocument(state)
    }

    LaunchedEffect(state.toastMessage) {
        if (state.toastMessage != null) {
            delay(2_000)
            dismissToast()
        }
    }

    BackHandler { requestLeave(state) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Editor),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBarEditorMore(
                fileName = state.fileName,
                isDirty = state.isDirty,
                onBackClick = { requestLeave(state) },
                onMoreClick = { openMenu() },
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                EditorBody(state)
            }
        }

        state.toastMessage?.let { message ->
            Toast(
                message = message,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
            )
        }
    }

    EditorOverflowMenu(state)
    EditorDialogs(state)
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0F14, widthDp = 360, heightDp = 640)
@Composable
private fun EditorPreview(
    @PreviewParameter(EditorPreviewProvider::class) case: EditorPreviewCase,
) {
    EditorPreviewHost(case.state)
}
