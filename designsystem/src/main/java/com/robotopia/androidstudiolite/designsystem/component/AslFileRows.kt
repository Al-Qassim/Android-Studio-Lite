package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.robotopia.androidstudiolite.designsystem.color.AslColors
import com.robotopia.androidstudiolite.designsystem.icon.AslIcons
import com.robotopia.androidstudiolite.designsystem.typography.AslTypography

@Composable
fun AslFileRow(
    name: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    showChevron: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(if (selected) AslColors.Selection else AslColors.Bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = AslIcons.File,
            style = AslTypography.Body,
        )
        Spacer(modifier = Modifier.width(10.dp))
        BasicText(
            text = name,
            style = AslTypography.Body.copy(color = AslColors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (showChevron) {
            BasicText(
                text = AslIcons.Chevron,
                style = AslTypography.Body.copy(color = AslColors.Muted),
            )
        }
    }
}

@Composable
fun AslFolderRow(
    name: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(if (selected) AslColors.Selection else AslColors.Bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = AslIcons.Folder,
            style = AslTypography.Body,
        )
        Spacer(modifier = Modifier.width(10.dp))
        BasicText(
            text = name,
            style = AslTypography.Body.copy(color = AslColors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        // No chevron on folder rows per DS
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun AslFileFolderRowPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        AslFolderRow(name = "src")
        AslFolderRow(name = "java", selected = true)
        AslFileRow(name = "MainActivity.kt")
        AslFileRow(name = "Theme.kt", selected = true)
    }
}
