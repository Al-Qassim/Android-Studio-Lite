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
import com.robotopia.androidstudiolite.designsystem.color.AslColors
import com.robotopia.androidstudiolite.designsystem.typography.AslTypography

@Composable
fun AslDialogForm(
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
            .background(AslColors.Surface)
            .padding(20.dp),
    ) {
        BasicText(
            text = title,
            style = AslTypography.Headline.copy(color = AslColors.Text),
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicText(
            text = locationLabel,
            style = AslTypography.Caption.copy(color = AslColors.Muted),
        )
        Spacer(modifier = Modifier.height(12.dp))
        AslTextField(
            value = fieldValue,
            onValueChange = onFieldChange,
            placeholder = fieldPlaceholder,
            variant = AslTextFieldVariant.Dialog,
            isError = isError,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AslButton(
                label = "Cancel",
                onClick = onCancel,
                variant = AslButtonVariant.TextAction,
            )
            Spacer(modifier = Modifier.width(8.dp))
            AslButton(
                label = primaryActionLabel,
                onClick = onPrimaryAction,
                variant = AslButtonVariant.Primary,
            )
        }
    }
}

@Composable
fun AslDialogMessageAction(
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
            .background(AslColors.Surface)
            .padding(20.dp),
    ) {
        BasicText(
            text = title,
            style = AslTypography.Headline.copy(color = AslColors.Text),
        )
        Spacer(modifier = Modifier.height(12.dp))
        BasicText(
            text = message,
            style = AslTypography.Body.copy(color = AslColors.Muted),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AslButton(
                label = "Cancel",
                onClick = onCancel,
                variant = AslButtonVariant.TextAction,
            )
            Spacer(modifier = Modifier.width(8.dp))
            AslButton(
                label = actionLabel,
                onClick = onAction,
                variant = if (dangerAction) {
                    AslButtonVariant.DangerText
                } else {
                    AslButtonVariant.Primary
                },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun AslDialogFormPreview() {
    var value by remember { mutableStateOf("") }
    AslDialogForm(
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
private fun AslDialogMessageActionPreview() {
    AslDialogMessageAction(
        title = "Delete file?",
        message = "MainActivity.kt will be permanently deleted. This cannot be undone.",
        actionLabel = "Delete",
        dangerAction = true,
        modifier = Modifier.padding(16.dp),
    )
}
