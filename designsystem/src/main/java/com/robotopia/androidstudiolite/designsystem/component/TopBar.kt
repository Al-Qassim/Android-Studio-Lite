package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.icon.IconAdd
import com.robotopia.androidstudiolite.designsystem.icon.IconBack
import com.robotopia.androidstudiolite.designsystem.icon.IconMore
import com.robotopia.androidstudiolite.designsystem.typography.Typography

@Composable
fun TopBarTitleAction(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Colors.Bg)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = title,
            style = Typography.TitleNav.copy(color = Colors.Text),
        )
        BasicText(
            text = actionLabel,
            style = Typography.ButtonCompact.copy(color = Colors.Primary),
            modifier = Modifier.clickable(onClick = onActionClick),
        )
    }
}

@Composable
fun TopBarBackTitle(
    title: String,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Colors.Bg)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopBarIconButton(onClick = onBackClick) {
            IconBack(tint = Colors.Text, size = 20.dp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        BasicText(
            text = title,
            style = Typography.TitleNav.copy(color = Colors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Path bar: back, truncated path (priority on current dir name), add.
 */
@Composable
fun TopBarPathAdd(
    pathSegments: List<String>,
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Colors.Bg)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopBarIconButton(onClick = onBackClick) {
            IconBack(tint = Colors.Text, size = 20.dp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            PathText(segments = pathSegments)
        }
        TopBarIconButton(onClick = onAddClick) {
            IconAdd(tint = Colors.Primary, size = 20.dp)
        }
    }
}

@Composable
fun TopBarEditorMore(
    fileName: String,
    isDirty: Boolean = false,
    onBackClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Colors.Editor)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopBarIconButton(onClick = onBackClick) {
            IconBack(tint = Colors.Text, size = 20.dp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        BasicText(
            text = if (isDirty) "$fileName •" else fileName,
            style = Typography.TitleEditor.copy(color = Colors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        TopBarIconButton(onClick = onMoreClick) {
            IconMore(tint = Colors.Text, size = 20.dp)
        }
    }
}

@Composable
private fun TopBarIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun PathText(segments: List<String>) {
    if (segments.isEmpty()) return
    val current = segments.last()
    val prefix = if (segments.size > 1) {
        segments.dropLast(1).joinToString("/") + "/"
    } else {
        ""
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (prefix.isNotEmpty()) {
            BasicText(
                text = prefix,
                style = Typography.Body.copy(color = Colors.Muted),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
        BasicText(
            text = current,
            style = Typography.BodyStrong.copy(color = Colors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun TopBarTitleActionPreview() {
    TopBarTitleAction(title = "Projects", actionLabel = "+ New")
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun TopBarBackTitlePreview() {
    TopBarBackTitle(title = "MyApp")
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun TopBarPathAddPreview() {
    TopBarPathAdd(pathSegments = listOf("app", "src", "main", "java"))
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0F14)
@Composable
private fun TopBarEditorMorePreview() {
    TopBarEditorMore(fileName = "MainActivity.kt", isDirty = true)
}
