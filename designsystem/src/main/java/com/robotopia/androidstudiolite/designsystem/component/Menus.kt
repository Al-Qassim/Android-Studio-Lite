package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.robotopia.androidstudiolite.designsystem.icon.IconCloud
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
private val MenuDefaultWidth = 200.dp

/** Entry in a [Menu]: an action button or a divider. */
sealed interface MenuItem {
    data class Button(
        val label: String,
        val onClick: () -> Unit,
        val danger: Boolean = false,
        val enabled: Boolean = true,
        val muted: Boolean = false,
        val icon: (@Composable (tint: Color, size: Dp) -> Unit)? = null,
        val trailing: (@Composable () -> Unit)? = null,
    ) : MenuItem

    data object Divider : MenuItem
}

/** Popup menu surface. Screens pass the [items] they need. */
@Composable
fun Menu(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    width: Dp = MenuDefaultWidth,
) {
    val shape = RoundedCornerShape(MenuCorner)
    Column(
        modifier = modifier
            .width(width)
            .overlayEnter(transformOrigin = TransformOrigin(pivotFractionX = 1f, pivotFractionY = 0f))
            .shadow(8.dp, shape)
            .clip(shape)
            .background(Colors.Menu)
            .border(1.dp, Colors.MenuBorder, shape)
            .padding(vertical = MenuVerticalPad),
    ) {
        items.forEach { item ->
            when (item) {
                MenuItem.Divider -> MenuDividerRow()
                is MenuItem.Button -> MenuButtonRow(item)
            }
        }
    }
}

@Composable
private fun MenuButtonRow(item: MenuItem.Button) {
    val color = when {
        item.danger -> Colors.Danger
        item.muted || !item.enabled -> Colors.Muted
        else -> Colors.Text
    }
    val iconTint = when {
        item.danger -> Colors.Danger
        item.muted || !item.enabled -> Colors.Muted
        else -> Colors.Muted
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .insetClickable(
                onClick = item.onClick,
                enabled = item.enabled,
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
            item.icon?.invoke(iconTint, MenuIconSize)
        }
        BasicText(
            text = item.label,
            style = Typography.Menu.copy(color = color),
            modifier = Modifier.weight(1f),
        )
        if (item.trailing != null) {
            Spacer(modifier = Modifier.width(8.dp))
            item.trailing.invoke()
        }
    }
}

@Composable
private fun MenuDividerRow() {
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
    Menu(
        items = listOf(
            MenuItem.Button(
                label = "Open",
                onClick = {},
                icon = { tint, size -> IconFolder(tint = tint, size = size) },
            ),
            MenuItem.Divider,
            MenuItem.Button(
                label = "Run",
                onClick = {},
                icon = { tint, size -> IconRun(tint = tint, size = size) },
            ),
            MenuItem.Button(
                label = "Build history",
                onClick = {},
                icon = { tint, size -> IconCloud(tint = tint, size = size) },
            ),
            MenuItem.Divider,
            MenuItem.Button(
                label = "Export…",
                onClick = {},
                icon = { tint, size -> IconSave(tint = tint, size = size) },
            ),
            MenuItem.Button(label = "Delete", onClick = {}, danger = true),
        ),
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "ProjectsHubMenu")
@Composable
private fun ProjectsHubMenuPreview() {
    Menu(
        items = listOf(
            MenuItem.Button(
                label = "New project",
                onClick = {},
                icon = { tint, size -> IconAdd(tint = tint, size = size) },
            ),
            MenuItem.Button(
                label = "Import project",
                onClick = {},
                icon = { tint, size -> IconFolder(tint = tint, size = size) },
            ),
        ),
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "BuildHistoryMenu")
@Composable
private fun BuildHistoryMenuPreview() {
    Menu(
        items = listOf(
            MenuItem.Button(label = "Delete", onClick = {}, danger = true),
        ),
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "ContextMenu")
@Composable
private fun ContextMenuPreview() {
    Menu(
        items = listOf(
            MenuItem.Button(label = "Rename", onClick = {}),
            MenuItem.Button(label = "Move", onClick = {}),
            MenuItem.Button(
                label = "Copy",
                onClick = {},
                icon = { tint, size -> IconCopy(tint = tint, size = size) },
            ),
            MenuItem.Divider,
            MenuItem.Button(label = "Delete", onClick = {}, danger = true),
        ),
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "CreateMenu")
@Composable
private fun CreateMenuPreview() {
    Menu(
        items = listOf(
            MenuItem.Button(
                label = "New file",
                onClick = {},
                icon = { tint, size -> IconFile(tint = tint, size = size) },
            ),
            MenuItem.Button(
                label = "New folder",
                onClick = {},
                icon = { tint, size -> IconFolder(tint = tint, size = size) },
            ),
        ),
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "EditorMenu")
@Composable
private fun EditorMenuPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Menu(
            width = 220.dp,
            items = editorMenuPreviewItems(autoSave = false, wrapText = false),
        )
        Menu(
            width = 220.dp,
            items = editorMenuPreviewItems(autoSave = true, wrapText = true),
        )
    }
}

private fun editorMenuPreviewItems(
    autoSave: Boolean,
    wrapText: Boolean,
): List<MenuItem> = listOf(
    MenuItem.Button(
        label = "Save",
        onClick = {},
        enabled = !autoSave,
        muted = autoSave,
        icon = { tint, size -> IconSave(tint = tint, size = size) },
    ),
    MenuItem.Button(
        label = "Auto save",
        onClick = {},
        trailing = if (autoSave) {
            { IconSuccess(tint = Colors.Primary, size = MenuIconSize) }
        } else {
            null
        },
    ),
    MenuItem.Divider,
    MenuItem.Button(
        label = "Wrap text",
        onClick = {},
        icon = { tint, size -> IconWrapText(tint = tint, size = size) },
        trailing = if (wrapText) {
            { IconSuccess(tint = Colors.Primary, size = MenuIconSize) }
        } else {
            null
        },
    ),
    MenuItem.Divider,
    MenuItem.Button(
        label = "Editor settings",
        onClick = {},
        muted = true,
        icon = { tint, size -> IconSettings(tint = tint, size = size) },
    ),
    MenuItem.Button(label = "Rename", onClick = {}),
)
