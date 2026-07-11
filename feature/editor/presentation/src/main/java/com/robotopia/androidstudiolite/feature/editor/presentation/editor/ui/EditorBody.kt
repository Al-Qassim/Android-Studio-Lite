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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.icon.IconWarning
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorScreenContext
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorUiState
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.leaveClean
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
                LoadingIndicator(label = "Opening file…")
            }
        }

        state.loadError != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconWarning(tint = Colors.Danger, size = 32.dp)
                    EmptyState(
                        title = "Couldn't open file",
                        hint = state.loadError,
                    )
                    Button(
                        label = "Retry",
                        onClick = { retryLoad(state) },
                        variant = ButtonVariant.Primary,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Button(
                        label = "Back to files",
                        onClick = { leaveClean() },
                        variant = ButtonVariant.TextAction,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }

        else -> {
            BasicTextField(
                value = state.content,
                onValueChange = { onContentChange(state, it) },
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
