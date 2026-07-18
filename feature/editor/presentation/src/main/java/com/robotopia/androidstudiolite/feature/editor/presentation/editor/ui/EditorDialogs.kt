package com.robotopia.androidstudiolite.feature.editor.presentation.editor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorDialog
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorScreenContext
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorUiState
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.discardAndLeave
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.dismissDialog
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.logic.saveAndLeave

@Composable
internal fun EditorScreenContext.EditorDialogs(state: EditorUiState) {
    when (state.dialog) {
        EditorDialog.UnsavedLeave -> {
            Dialog(onDismissRequest = { dismissDialog() }) {
                UnsavedLeaveDialog(
                    fileName = state.fileName,
                    onDiscard = { discardAndLeave() },
                    onSave = { saveAndLeave(state) },
                )
            }
        }

        null -> Unit
    }
}

@Composable
private fun UnsavedLeaveDialog(
    fileName: String,
    onDiscard: () -> Unit,
    onSave: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .width(320.dp)
            .shadow(12.dp, shape)
            .clip(shape)
            .background(Theme.colors.Surface)
            .padding(20.dp),
    ) {
        BasicText(
            text = "Unsaved changes",
            style = Typography.Headline.copy(color = Theme.colors.Text),
        )
        Spacer(modifier = Modifier.height(12.dp))
        BasicText(
            text = "$fileName has unsaved edits. Save before leaving?",
            style = Typography.Body.copy(color = Theme.colors.Muted),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                label = "Discard",
                onClick = onDiscard,
                variant = ButtonVariant.Neutral,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                label = "Save",
                onClick = onSave,
                variant = ButtonVariant.Primary,
            )
        }
    }
}
