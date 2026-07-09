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
import com.robotopia.androidstudiolite.designsystem.color.AslColors
import com.robotopia.androidstudiolite.designsystem.icon.AslIcons
import com.robotopia.androidstudiolite.designsystem.typography.AslTypography

@Composable
fun AslTopBarTitleAction(
    title: String,
    actionLabel: String,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(AslColors.Bg)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = title,
            style = AslTypography.TitleNav.copy(color = AslColors.Text),
        )
        BasicText(
            text = actionLabel,
            style = AslTypography.ButtonCompact.copy(color = AslColors.Primary),
            modifier = Modifier.clickable(onClick = onActionClick),
        )
    }
}

@Composable
fun AslTopBarBackTitle(
    title: String,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(AslColors.Bg)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopBarIconButton(glyph = AslIcons.Back, onClick = onBackClick)
        Spacer(modifier = Modifier.width(8.dp))
        BasicText(
            text = title,
            style = AslTypography.TitleNav.copy(color = AslColors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * Path bar: back, truncated path (priority on current dir name), add.
 */
@Composable
fun AslTopBarPathAdd(
    pathSegments: List<String>,
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(AslColors.Bg)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopBarIconButton(glyph = AslIcons.Back, onClick = onBackClick)
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            PathText(segments = pathSegments)
        }
        TopBarIconButton(glyph = AslIcons.Add, onClick = onAddClick, tintPrimary = true)
    }
}

@Composable
fun AslTopBarEditorMore(
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
            .background(AslColors.Editor)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopBarIconButton(glyph = AslIcons.Back, onClick = onBackClick)
        Spacer(modifier = Modifier.width(8.dp))
        BasicText(
            text = if (isDirty) "$fileName •" else fileName,
            style = AslTypography.TitleEditor.copy(color = AslColors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        TopBarIconButton(glyph = AslIcons.More, onClick = onMoreClick)
    }
}

@Composable
private fun TopBarIconButton(
    glyph: String,
    onClick: () -> Unit,
    tintPrimary: Boolean = false,
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = glyph,
            style = AslTypography.TitleNav.copy(
                color = if (tintPrimary) AslColors.Primary else AslColors.Text,
            ),
        )
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
                style = AslTypography.Body.copy(color = AslColors.Muted),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
        BasicText(
            text = current,
            style = AslTypography.BodyStrong.copy(color = AslColors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun AslTopBarTitleActionPreview() {
    AslTopBarTitleAction(title = "Projects", actionLabel = "+ New")
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun AslTopBarBackTitlePreview() {
    AslTopBarBackTitle(title = "MyApp")
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun AslTopBarPathAddPreview() {
    AslTopBarPathAdd(pathSegments = listOf("app", "src", "main", "java"))
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0F14)
@Composable
private fun AslTopBarEditorMorePreview() {
    AslTopBarEditorMore(fileName = "MainActivity.kt", isDirty = true)
}
