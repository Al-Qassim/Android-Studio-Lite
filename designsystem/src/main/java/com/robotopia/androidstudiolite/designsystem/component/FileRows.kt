package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.icon.IconChevron
import com.robotopia.androidstudiolite.designsystem.icon.IconFile
import com.robotopia.androidstudiolite.designsystem.icon.IconFolder
import com.robotopia.androidstudiolite.designsystem.icon.IconMore
import com.robotopia.androidstudiolite.designsystem.modifier.insetClickable
import com.robotopia.androidstudiolite.designsystem.typography.Typography

@Composable
fun FileRow(
    name: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    showChevron: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .insetClickable(
                onClick = onClick,
                selected = selected,
                onLongClick = onLongClick,
            )
            .height(40.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconFile(tint = Theme.colors.Muted, size = 20.dp)
        Spacer(modifier = Modifier.width(10.dp))
        BasicText(
            text = name,
            style = Typography.Body.copy(color = Theme.colors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        when {
            onMenuClick != null -> {
                IconButton(
                    onClick = onMenuClick,
                    variant = IconButtonVariant.Ghost,
                    size = 32.dp,
                    iconSize = 18.dp,
                    icon = { tint, size -> IconMore(tint = tint, size = size) },
                )
            }
            showChevron -> {
                IconChevron(tint = Theme.colors.Muted, size = 16.dp)
            }
        }
    }
}

@Composable
fun FolderRow(
    name: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .insetClickable(
                onClick = onClick,
                selected = selected,
                onLongClick = onLongClick,
            )
            .height(40.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconFolder(tint = Theme.colors.Muted, size = 20.dp)
        Spacer(modifier = Modifier.width(10.dp))
        BasicText(
            text = name,
            style = Typography.Body.copy(color = Theme.colors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (onMenuClick != null) {
            IconButton(
                onClick = onMenuClick,
                variant = IconButtonVariant.Ghost,
                size = 32.dp,
                iconSize = 18.dp,
                icon = { tint, size -> IconMore(tint = tint, size = size) },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22)
@Composable
private fun FileFolderRowPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        FolderRow(name = "src", onMenuClick = {})
        FolderRow(name = "java", selected = true, onMenuClick = {})
        FileRow(name = "MainActivity.kt", showChevron = false, onMenuClick = {})
        FileRow(name = "Theme.kt", selected = true, showChevron = false, onMenuClick = {})
    }
}
