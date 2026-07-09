package com.robotopia.androidstudiolite.designsystem.component

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.typography.Typography

@Composable
fun DialogForm(
    title: String,
    locationLabel: String,
    fieldValue: String,
    onFieldChange: (String) -> Unit,
    primaryActionLabel: String,
    onCancel: () -> Unit = {},
    onPrimaryAction: () -> Unit = {},
    modifier: Modifier = Modifier,
    fieldPlaceholder: String = "",
    isError: Boolean = false,
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .width(320.dp)
            .shadow(12.dp, shape)
            .clip(shape)
            .background(Colors.Surface)
            .padding(20.dp),
    ) {
        BasicText(
            text = title,
            style = Typography.Headline.copy(color = Colors.Text),
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicText(
            text = locationLabel,
            style = Typography.Caption.copy(color = Colors.Muted),
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = fieldValue,
            onValueChange = onFieldChange,
            placeholder = fieldPlaceholder,
            variant = TextFieldVariant.Dialog,
            isError = isError,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                label = "Cancel",
                onClick = onCancel,
                variant = ButtonVariant.TextAction,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                label = primaryActionLabel,
                onClick = onPrimaryAction,
                variant = ButtonVariant.Primary,
            )
        }
    }
}

@Composable
fun DialogMessageAction(
    title: String,
    message: String,
    actionLabel: String,
    onCancel: () -> Unit = {},
    onAction: () -> Unit = {},
    modifier: Modifier = Modifier,
    dangerAction: Boolean = false,
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .width(320.dp)
            .shadow(12.dp, shape)
            .clip(shape)
            .background(Colors.Surface)
            .padding(20.dp),
    ) {
        BasicText(
            text = title,
            style = Typography.Headline.copy(color = Colors.Text),
        )
        Spacer(modifier = Modifier.height(12.dp))
        BasicText(
            text = message,
            style = Typography.Body.copy(color = Colors.Muted),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                label = "Cancel",
                onClick = onCancel,
                variant = ButtonVariant.TextAction,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                label = actionLabel,
                onClick = onAction,
                variant = if (dangerAction) {
                    ButtonVariant.DangerText
                } else {
                    ButtonVariant.Primary
                },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun DialogFormPreview() {
    var value by remember { mutableStateOf("") }
    DialogForm(
        title = "New file",
        locationLabel = "Location: app/src/main/java",
        fieldValue = value,
        onFieldChange = { value = it },
        primaryActionLabel = "Create",
        fieldPlaceholder = "File name",
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun DialogMessageActionPreview() {
    DialogMessageAction(
        title = "Delete file?",
        message = "MainActivity.kt will be permanently deleted. This cannot be undone.",
        actionLabel = "Delete",
        dangerAction = true,
        modifier = Modifier.padding(16.dp),
    )
}
