package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.icon.IconAdd
import com.robotopia.androidstudiolite.designsystem.icon.IconBack
import com.robotopia.androidstudiolite.designsystem.icon.IconMore
import com.robotopia.androidstudiolite.designsystem.icon.IconRun
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
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = title,
            style = Typography.TitleNav.copy(color = Colors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        BasicText(
            text = actionLabel,
            style = Typography.ButtonCompact.copy(color = Colors.Primary),
            maxLines = 1,
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
        IconButton(
            onClick = onBackClick,
            icon = { tint, size -> IconBack(tint = tint, size = size) },
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicText(
            text = title,
            style = Typography.TitleNav.copy(color = Colors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

/** Back + title + run + add — file browser chrome (path lives in [PathBar] below). */
@Composable
fun TopBarBackTitleAdd(
    title: String,
    onBackClick: () -> Unit = {},
    onRunClick: (() -> Unit)? = null,
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
        IconButton(
            onClick = onBackClick,
            icon = { tint, size -> IconBack(tint = tint, size = size) },
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicText(
            text = title,
            style = Typography.TitleNav.copy(color = Colors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (onRunClick != null) {
            IconButton(
                onClick = onRunClick,
                icon = { _, size -> IconRun(tint = Colors.Primary, size = size) },
            )
        }
        IconButton(
            onClick = onAddClick,
            icon = { _, size -> IconAdd(tint = Colors.Primary, size = size) },
        )
    }
}

/**
 * Path bar: back, truncated path (priority on current dir name), add.
 * Deep paths collapse leading parents to …; long current names ellipsize at the end.
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
        IconButton(
            onClick = onBackClick,
            icon = { tint, size -> IconBack(tint = tint, size = size) },
        )
        Spacer(modifier = Modifier.width(8.dp))
        PathTrail(
            segments = pathSegments,
            modifier = Modifier.weight(1f),
            parentStyle = Typography.Body.copy(color = Colors.Muted),
            currentStyle = Typography.BodyStrong.copy(color = Colors.Text),
            separatorStyle = Typography.Body.copy(color = Colors.Muted2),
        )
        IconButton(
            onClick = onAddClick,
            icon = { _, size -> IconAdd(tint = Colors.Primary, size = size) },
        )
    }
}

@Composable
fun TopBarEditorMore(
    fileName: String,
    isDirty: Boolean = false,
    onBackClick: () -> Unit = {},
    onRunClick: (() -> Unit)? = null,
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
        IconButton(
            onClick = onBackClick,
            icon = { tint, size -> IconBack(tint = tint, size = size) },
        )
        Spacer(modifier = Modifier.width(8.dp))
        BasicText(
            text = if (isDirty) "$fileName •" else fileName,
            style = Typography.TitleEditor.copy(color = Colors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (onRunClick != null) {
            IconButton(
                onClick = onRunClick,
                icon = { _, size -> IconRun(tint = Colors.Primary, size = size) },
            )
        }
        IconButton(
            onClick = onMoreClick,
            icon = { tint, size -> IconMore(tint = tint, size = size) },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, name = "TitleAction · normal")
@Composable
private fun TopBarTitleActionPreview() {
    TopBarTitleAction(title = "Projects", actionLabel = "+ New")
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 280, name = "TitleAction · long title")
@Composable
private fun TopBarTitleActionLongPreview() {
    TopBarTitleAction(
        title = "Very Long Project List Title That Should Ellipsize",
        actionLabel = "+ New",
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, name = "BackTitle · normal")
@Composable
private fun TopBarBackTitlePreview() {
    TopBarBackTitle(title = "MyApp")
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 280, name = "BackTitle · long title")
@Composable
private fun TopBarBackTitleLongPreview() {
    TopBarBackTitle(title = "VeryLongAndroidStudioLiteProjectNameThatNeedsEllipsis")
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, name = "BackTitleAdd · normal")
@Composable
private fun TopBarBackTitleAddPreview() {
    TopBarBackTitleAdd(
        title = "MyApp",
        onRunClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, name = "PathAdd · normal")
@Composable
private fun TopBarPathAddPreview() {
    TopBarPathAdd(pathSegments = listOf("app", "src", "main", "java"))
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 280, name = "PathAdd · deep (auto collapse)")
@Composable
private fun TopBarPathAddDeepPreview() {
    TopBarPathAdd(
        pathSegments = listOf(
            "MyApp",
            "app",
            "src",
            "main",
            "java",
            "com",
            "robotopia",
            "androidstudiolite",
            "ui",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 280, name = "PathAdd · long name (auto ellipsis)")
@Composable
private fun TopBarPathAddLongNamePreview() {
    TopBarPathAdd(
        pathSegments = listOf(
            "app",
            "src",
            "main",
            "VeryLongDirectoryNameThatNeedsEllipsis",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 200, name = "PathAdd · narrow")
@Composable
private fun TopBarPathAddNarrowPreview() {
    TopBarPathAdd(pathSegments = listOf("app", "src", "main", "java", "com", "robotopia"))
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0F14, widthDp = 360, name = "EditorMore · normal")
@Composable
private fun TopBarEditorMorePreview() {
    TopBarEditorMore(fileName = "MainActivity.kt", isDirty = true)
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0F14, widthDp = 280, name = "EditorMore · long name")
@Composable
private fun TopBarEditorMoreLongPreview() {
    TopBarEditorMore(
        fileName = "VeryLongActivityNameThatShouldEllipsizeAtTheEnd.kt",
        isDirty = true,
    )
}
