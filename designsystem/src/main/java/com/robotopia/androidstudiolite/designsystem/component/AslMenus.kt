package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.AslColors
import com.robotopia.androidstudiolite.designsystem.icon.AslIcons
import com.robotopia.androidstudiolite.designsystem.typography.AslTypography

@Composable
fun AslContextMenu(
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
            .background(AslColors.Menu),
    ) {
        MenuItem(label = "Rename", onClick = onRename)
        MenuItem(label = "Move", onClick = onMove)
        MenuItem(label = "Copy", onClick = onCopy)
        MenuItem(label = "Delete", onClick = onDelete, danger = true)
    }
}

@Composable
fun AslCreateMenu(
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
            .background(AslColors.Menu)
            .padding(4.dp),
    ) {
        InsetMenuItem(label = "New file", onClick = onNewFile)
        InsetMenuItem(label = "New folder", onClick = onNewFolder)
    }
}

@Composable
fun AslEditorMenu(
    autoSave: Boolean = true,
    onAutoSaveToggle: () -> Unit = {},
    onSave: () -> Unit = {},
    onEditorSettings: () -> Unit = {},
    onRename: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier
            .width(200.dp)
            .shadow(8.dp, shape)
            .clip(shape)
            .background(AslColors.Menu),
    ) {
        MenuItem(
            label = if (autoSave) "Auto save  ${AslIcons.Success}" else "Auto save",
            onClick = onAutoSaveToggle,
        )
        MenuItem(
            label = "Save",
            onClick = onSave,
            enabled = !autoSave,
            muted = autoSave,
        )
        MenuItem(
            label = "Editor settings",
            onClick = onEditorSettings,
            muted = true,
        )
        MenuItem(label = "Rename", onClick = onRename)
    }
}

@Composable
private fun MenuItem(
    label: String,
    onClick: () -> Unit,
    danger: Boolean = false,
    enabled: Boolean = true,
    muted: Boolean = false,
) {
    val color = when {
        danger -> AslColors.Danger
        muted || !enabled -> AslColors.Muted
        else -> AslColors.Text
    }
    BasicText(
        text = label,
        style = AslTypography.Menu.copy(color = color),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    )
}

@Composable
private fun InsetMenuItem(
    label: String,
    onClick: () -> Unit,
) {
    val itemShape = RoundedCornerShape(6.dp)
    BasicText(
        text = label,
        style = AslTypography.Menu.copy(color = AslColors.Text),
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
private fun AslContextMenuPreview() {
    AslContextMenu(modifier = Modifier.padding(16.dp))
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun AslCreateMenuPreview() {
    AslCreateMenu(modifier = Modifier.padding(16.dp))
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun AslEditorMenuPreview() {
    AslEditorMenu(modifier = Modifier.padding(16.dp))
}
