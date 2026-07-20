package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.modifier.overlayEnter
import com.robotopia.androidstudiolite.designsystem.typography.Typography

private val DialogCorner = 12.dp
private val DialogWidth = 320.dp
private val DialogPadding = 16.dp

@Composable
fun DialogForm(
    title: String,
    fieldValue: String,
    onFieldChange: (String) -> Unit,
    primaryActionLabel: String,
    onCancel: () -> Unit = {},
    onPrimaryAction: () -> Unit = {},
    modifier: Modifier = Modifier,
    locationLabel: String? = null,
    fieldPlaceholder: String = "",
    errorMessage: String? = null,
) {
    DialogSurface(modifier = modifier) {
        DialogTitle(title)
        if (!locationLabel.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            BasicText(
                text = locationLabel,
                style = Typography.Caption.copy(color = Theme.colors.Muted),
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = fieldValue,
            onValueChange = onFieldChange,
            placeholder = fieldPlaceholder,
            variant = TextFieldVariant.Dialog,
            isError = !errorMessage.isNullOrBlank(),
            errorMessage = errorMessage,
        )
        Spacer(modifier = Modifier.height(16.dp))
        DialogActions(
            cancelLabel = "Cancel",
            onCancel = onCancel,
            actionLabel = primaryActionLabel,
            onAction = onPrimaryAction,
            actionVariant = ButtonVariant.Primary,
        )
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
    cancelLabel: String = "Cancel",
    dangerAction: Boolean = false,
) {
    DialogSurface(modifier = modifier) {
        DialogTitle(title)
        Spacer(modifier = Modifier.height(12.dp))
        BasicText(
            text = message,
            style = Typography.Body.copy(color = Theme.colors.Muted),
        )
        Spacer(modifier = Modifier.height(16.dp))
        DialogActions(
            cancelLabel = cancelLabel,
            onCancel = onCancel,
            actionLabel = actionLabel,
            onAction = onAction,
            actionVariant = if (dangerAction) {
                ButtonVariant.Danger
            } else {
                ButtonVariant.Primary
            },
        )
    }
}

/**
 * Message dialog with stacked full-width actions (phone-friendly multi-choice).
 * Primary first, optional danger, then cancel.
 */
@Composable
fun DialogMessageStackedActions(
    title: String,
    message: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    dangerLabel: String? = null,
    onDanger: (() -> Unit)? = null,
    cancelLabel: String = "Cancel",
) {
    DialogSurface(modifier = modifier) {
        DialogTitle(title)
        Spacer(modifier = Modifier.height(12.dp))
        BasicText(
            text = message,
            style = Typography.Body.copy(color = Theme.colors.Muted),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                label = primaryLabel,
                onClick = onPrimary,
                variant = ButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
            )
            if (dangerLabel != null && onDanger != null) {
                Button(
                    label = dangerLabel,
                    onClick = onDanger,
                    variant = ButtonVariant.Danger,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Button(
                label = cancelLabel,
                onClick = onCancel,
                variant = ButtonVariant.Secondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun DialogSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(DialogCorner)
    Column(
        modifier = modifier
            .overlayEnter(transformOrigin = TransformOrigin.Center)
            .width(DialogWidth)
            .shadow(12.dp, shape)
            .clip(shape)
            .background(Theme.colors.Menu)
            .border(1.dp, Theme.colors.MenuBorder, shape)
            .padding(DialogPadding),
        content = content,
    )
}

@Composable
private fun DialogTitle(title: String) {
    BasicText(
        text = title,
        style = Typography.BodyStrong.copy(color = Theme.colors.Text),
    )
}

@Composable
private fun DialogActions(
    cancelLabel: String,
    onCancel: () -> Unit,
    actionLabel: String,
    onAction: () -> Unit,
    actionVariant: ButtonVariant,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            label = cancelLabel,
            onClick = onCancel,
            variant = ButtonVariant.Secondary,
        )
        Button(
            label = actionLabel,
            onClick = onAction,
            variant = actionVariant,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "DialogForm · empty + location")
@Composable
private fun DialogFormEmptyWithLocationPreview() {
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

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "DialogForm · no location")
@Composable
private fun DialogFormNoLocationPreview() {
    var value by remember { mutableStateOf("") }
    DialogForm(
        title = "Rename",
        fieldValue = value,
        onFieldChange = { value = it },
        primaryActionLabel = "Rename",
        fieldPlaceholder = "New name",
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "DialogForm · filled")
@Composable
private fun DialogFormFilledPreview() {
    var value by remember { mutableStateOf("MainActivity.kt") }
    DialogForm(
        title = "Rename",
        locationLabel = "Location: app/src/main/java",
        fieldValue = value,
        onFieldChange = { value = it },
        primaryActionLabel = "Rename",
        fieldPlaceholder = "File name",
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "DialogForm · error")
@Composable
private fun DialogFormErrorPreview() {
    var value by remember { mutableStateOf("Main Activity.kt") }
    DialogForm(
        title = "New file",
        locationLabel = "Location: app/src/main/java",
        fieldValue = value,
        onFieldChange = { value = it },
        primaryActionLabel = "Create",
        fieldPlaceholder = "File name",
        errorMessage = "Name cannot contain spaces",
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "DialogForm · error, no location")
@Composable
private fun DialogFormErrorNoLocationPreview() {
    var value by remember { mutableStateOf("") }
    DialogForm(
        title = "New folder",
        fieldValue = value,
        onFieldChange = { value = it },
        primaryActionLabel = "Create",
        fieldPlaceholder = "Folder name",
        errorMessage = "Name is required",
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22)
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

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "DialogMessageStacked · overwrite")
@Composable
private fun DialogMessageStackedActionsPreview() {
    DialogMessageStackedActions(
        title = "Local changes would be overwritten",
        message = "Checking out “feature/login” would overwrite:\n• MainActivity.kt\n• LoginScreen.kt\n\nCommit your changes first, or discard them to switch.",
        primaryLabel = "Commit first",
        onPrimary = {},
        dangerLabel = "Discard & switch",
        onDanger = {},
        onCancel = {},
        modifier = Modifier.padding(16.dp),
    )
}
