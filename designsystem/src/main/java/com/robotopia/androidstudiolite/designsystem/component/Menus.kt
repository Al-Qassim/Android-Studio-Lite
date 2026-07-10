package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.icon.IconSuccess
import com.robotopia.androidstudiolite.designsystem.typography.Typography

@Composable
fun ContextMenu(
    onRename: () -> Unit = {},
    onMove: () -> Unit = {},
    onCopy: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier
            .width(180.dp)
            .shadow(8.dp, shape)
            .clip(shape)
            .background(Colors.Menu),
    ) {
        MenuItem(label = "Rename", onClick = onRename)
        MenuItem(label = "Move", onClick = onMove)
        MenuItem(label = "Copy", onClick = onCopy)
        MenuItem(label = "Delete", onClick = onDelete, danger = true)
    }
}

@Composable
fun ProjectMenu(
    onOpen: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier
            .width(180.dp)
            .shadow(8.dp, shape)
            .clip(shape)
            .background(Colors.Menu),
    ) {
        MenuItem(label = "Open", onClick = onOpen)
        MenuItem(label = "Delete", onClick = onDelete, danger = true)
    }
}

@Composable
fun CreateMenu(
    onNewFile: () -> Unit = {},
    onNewFolder: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier
            .width(180.dp)
            .shadow(8.dp, shape)
            .clip(shape)
            .background(Colors.Menu)
            .padding(4.dp),
    ) {
        InsetMenuItem(label = "New file", onClick = onNewFile)
        InsetMenuItem(label = "New folder", onClick = onNewFolder)
    }
}

@Composable
fun EditorMenu(
    autoSave: Boolean = false,
    onAutoSaveToggle: () -> Unit = {},
    onSave: () -> Unit = {},
    onEditorSettings: () -> Unit = {},
    onRename: () -> Unit = {},
    showEditorSettings: Boolean = true,
    showRename: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier
            .width(200.dp)
            .shadow(8.dp, shape)
            .clip(shape)
            .background(Colors.Menu),
    ) {
        MenuItem(
            label = "Auto save",
            onClick = onAutoSaveToggle,
            trailing = if (autoSave) {
                { IconSuccess(tint = Colors.Primary, size = 16.dp) }
            } else {
                null
            },
        )
        MenuItem(
            label = "Save",
            onClick = onSave,
            enabled = !autoSave,
            muted = autoSave,
        )
        if (showEditorSettings) {
            MenuItem(
                label = "Editor settings",
                onClick = onEditorSettings,
                muted = true,
            )
        }
        if (showRename) {
            MenuItem(label = "Rename", onClick = onRename)
        }
    }
}

@Composable
private fun MenuItem(
    label: String,
    onClick: () -> Unit,
    danger: Boolean = false,
    enabled: Boolean = true,
    muted: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    val color = when {
        danger -> Colors.Danger
        muted || !enabled -> Colors.Muted
        else -> Colors.Text
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = label,
            style = Typography.Menu.copy(color = color),
            modifier = Modifier.weight(1f),
        )
        if (trailing != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailing()
        }
    }
}

@Composable
private fun InsetMenuItem(
    label: String,
    onClick: () -> Unit,
) {
    val itemShape = RoundedCornerShape(6.dp)
    BasicText(
        text = label,
        style = Typography.Menu.copy(color = Colors.Text),
        modifier = Modifier
            .fillMaxWidth()
            .clip(itemShape)
            .clickable(onClick = onClick)
            .background(Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun ProjectMenuPreview() {
    ProjectMenu(modifier = Modifier.padding(16.dp))
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun ContextMenuPreview() {
    ContextMenu(modifier = Modifier.padding(16.dp))
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun CreateMenuPreview() {
    CreateMenu(modifier = Modifier.padding(16.dp))
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun EditorMenuPreview() {
    EditorMenu(modifier = Modifier.padding(16.dp))
}
