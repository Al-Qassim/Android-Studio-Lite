package com.robotopia.androidstudiolite.feature.editor.presentation.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorScreenContext
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorUiState
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.onContentChange
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.retryLoad

@Composable
internal fun EditorScreenContext.EditorBody(state: EditorUiState) {
    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Colors.Primary)
            }
        }

        state.loadError != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    EmptyState(
                        title = "Couldn't open file",
                        hint = state.loadError,
                    )
                    Button(
                        label = "Retry",
                        onClick = { retryLoad(state) },
                        variant = ButtonVariant.Primary,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }
            }
        }

        else -> {
            BasicTextField(
                value = state.content,
                onValueChange = { onContentChange(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .background(Colors.Editor)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .fillMaxWidth(),
                textStyle = Typography.Code.copy(color = Colors.Text),
                cursorBrush = SolidColor(Colors.Primary),
            )
        }
    }
}
