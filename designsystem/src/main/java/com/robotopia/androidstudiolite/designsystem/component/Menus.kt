package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.icon.IconAdd
import com.robotopia.androidstudiolite.designsystem.icon.IconCopy
import com.robotopia.androidstudiolite.designsystem.icon.IconFile
import com.robotopia.androidstudiolite.designsystem.icon.IconFolder
import com.robotopia.androidstudiolite.designsystem.icon.IconRun
import com.robotopia.androidstudiolite.designsystem.icon.IconSave
import com.robotopia.androidstudiolite.designsystem.icon.IconSettings
import com.robotopia.androidstudiolite.designsystem.icon.IconSuccess
import com.robotopia.androidstudiolite.designsystem.icon.IconWrapText
import com.robotopia.androidstudiolite.designsystem.modifier.insetClickable
import com.robotopia.androidstudiolite.designsystem.modifier.overlayEnter
import com.robotopia.androidstudiolite.designsystem.typography.Typography

private val MenuCorner = 8.dp
private val MenuIconGutter = 32.dp
private val MenuIconSize = 16.dp
private val MenuItemHeight = 28.dp
private val MenuVerticalPad = 4.dp
private val MenuHorizontalInset = 8.dp

@Composable
fun ContextMenu(
    onRename: () -> Unit = {},
    onMove: () -> Unit = {},
    onCopy: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    DropdownMenu(modifier = modifier.width(200.dp)) {
        MenuItem(label = "Rename", onClick = onRename)
        MenuItem(label = "Move", onClick = onMove)
        MenuItem(
            label = "Copy",
            onClick = onCopy,
            icon = { tint, size -> IconCopy(tint = tint, size = size) },
        )
        MenuDivider()
        MenuItem(label = "Delete", onClick = onDelete, danger = true)
    }
}

@Composable
fun ProjectMenu(
    onOpen: () -> Unit = {},
    onRun: () -> Unit = {},
    onExport: () -> Unit = {},
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    DropdownMenu(modifier = modifier.width(200.dp)) {
        MenuItem(
            label = "Open",
            onClick = onOpen,
            icon = { tint, size -> IconFolder(tint = tint, size = size) },
        )
        MenuItem(
            label = "Run",
            onClick = onRun,
            icon = { tint, size -> IconRun(tint = tint, size = size) },
        )
        MenuItem(
            label = "Export…",
            onClick = onExport,
            icon = { tint, size -> IconSave(tint = tint, size = size) },
        )
        MenuDivider()
        MenuItem(label = "Delete", onClick = onDelete, danger = true)
    }
}

/** Top-bar + menu on the Projects list: create new or import a zip. */
@Composable
fun ProjectsHubMenu(
    onNewProject: () -> Unit = {},
    onImportProject: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    DropdownMenu(modifier = modifier.width(200.dp)) {
        MenuItem(
            label = "New project",
            onClick = onNewProject,
            icon = { tint, size -> IconAdd(tint = tint, size = size) },
        )
        MenuItem(
            label = "Import project",
            onClick = onImportProject,
            icon = { tint, size -> IconFolder(tint = tint, size = size) },
        )
    }
}

@Composable
fun CreateMenu(
    onNewFile: () -> Unit = {},
    onNewFolder: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    DropdownMenu(modifier = modifier.width(200.dp)) {
        MenuItem(
            label = "New file",
            onClick = onNewFile,
            icon = { tint, size -> IconFile(tint = tint, size = size) },
        )
        MenuItem(
            label = "New folder",
            onClick = onNewFolder,
            icon = { tint, size -> IconFolder(tint = tint, size = size) },
        )
    }
}

@Composable
fun EditorMenu(
    autoSave: Boolean = false,
    wrapText: Boolean = false,
    onAutoSaveToggle: () -> Unit = {},
    onWrapTextToggle: () -> Unit = {},
    onSave: () -> Unit = {},
    onEditorSettings: () -> Unit = {},
    onRename: () -> Unit = {},
    showEditorSettings: Boolean = true,
    showRename: Boolean = true,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(modifier = modifier.width(220.dp)) {
        MenuItem(
            label = "Save",
            onClick = onSave,
            enabled = !autoSave,
            muted = autoSave,
            icon = { tint, size -> IconSave(tint = tint, size = size) },
        )
        MenuItem(
            label = "Auto save",
            onClick = onAutoSaveToggle,
            trailing = if (autoSave) {
                { IconSuccess(tint = Colors.Primary, size = MenuIconSize) }
            } else {
                null
            },
        )
        MenuDivider()
        MenuItem(
            label = "Wrap text",
            onClick = onWrapTextToggle,
            icon = { tint, size -> IconWrapText(tint = tint, size = size) },
            trailing = if (wrapText) {
                { IconSuccess(tint = Colors.Primary, size = MenuIconSize) }
            } else {
                null
            },
        )
        if (showEditorSettings || showRename) {
            MenuDivider()
        }
        if (showEditorSettings) {
            MenuItem(
                label = "Editor settings",
                onClick = onEditorSettings,
                muted = true,
                icon = { tint, size -> IconSettings(tint = tint, size = size) },
            )
        }
        if (showRename) {
            MenuItem(label = "Rename", onClick = onRename)
        }
    }
}

@Composable
private fun DropdownMenu(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(MenuCorner)
    Column(
        modifier = modifier
            .overlayEnter(transformOrigin = TransformOrigin(pivotFractionX = 1f, pivotFractionY = 0f))
            .shadow(8.dp, shape)
            .clip(shape)
            .background(Colors.Menu)
            .border(1.dp, Colors.MenuBorder, shape)
            .padding(vertical = MenuVerticalPad),
        content = content,
    )
}

@Composable
private fun MenuItem(
    label: String,
    onClick: () -> Unit,
    danger: Boolean = false,
    enabled: Boolean = true,
    muted: Boolean = false,
    icon: (@Composable (tint: Color, size: Dp) -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val color = when {
        danger -> Colors.Danger
        muted || !enabled -> Colors.Muted
        else -> Colors.Text
    }
    val iconTint = when {
        danger -> Colors.Danger
        muted || !enabled -> Colors.Muted
        else -> Colors.Muted
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .insetClickable(
                onClick = onClick,
                enabled = enabled,
                horizontalInset = 4.dp,
                verticalInset = 1.dp,
                corner = 6.dp,
            )
            .height(MenuItemHeight)
            .padding(horizontal = MenuHorizontalInset),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(MenuIconGutter),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (icon != null) {
                icon(iconTint, MenuIconSize)
            }
        }
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
private fun MenuDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = MenuHorizontalInset)
            .height(1.dp)
            .background(Colors.MenuDivider),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "ProjectMenu")
@Composable
private fun ProjectMenuPreview() {
    ProjectMenu(modifier = Modifier.padding(16.dp))
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "ProjectsHubMenu")
@Composable
private fun ProjectsHubMenuPreview() {
    ProjectsHubMenu(modifier = Modifier.padding(16.dp))
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "ContextMenu")
@Composable
private fun ContextMenuPreview() {
    ContextMenu(modifier = Modifier.padding(16.dp))
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "CreateMenu")
@Composable
private fun CreateMenuPreview() {
    CreateMenu(modifier = Modifier.padding(16.dp))
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "EditorMenu")
@Composable
private fun EditorMenuPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EditorMenu(autoSave = false, wrapText = false)
        EditorMenu(autoSave = true, wrapText = true)
    }
}
