package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.AslColors
import com.robotopia.androidstudiolite.designsystem.typography.AslTypography

@Composable
fun AslPathBar(
    segments: List<String>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(AslColors.Editor)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        segments.forEachIndexed { index, segment ->
            val isLast = index == segments.lastIndex
            BasicText(
                text = segment,
                style = if (isLast) {
                    AslTypography.Code.copy(color = AslColors.Text)
                } else {
                    AslTypography.Code.copy(color = AslColors.Muted)
                },
            )
            if (!isLast) {
                BasicText(
                    text = " / ",
                    style = AslTypography.Code.copy(color = AslColors.Muted2),
                )
            }
        }
    }
}

@Composable
fun AslCodeSample(
    gutter: String,
    code: String,
    modifier: Modifier = Modifier,
    stringHighlight: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AslColors.Editor)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        BasicText(
            text = gutter,
            style = AslTypography.CodeGutter.copy(color = AslColors.Gutter),
            modifier = Modifier.padding(end = 12.dp),
        )
        BasicText(
            text = code,
            style = AslTypography.Code.copy(
                color = if (stringHighlight) AslColors.CodeString else AslColors.Text,
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0F14)
@Composable
private fun AslPathBarPreview() {
    AslPathBar(segments = listOf("app", "src", "main", "java", "MainActivity.kt"))
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0F14)
@Composable
private fun AslCodeSamplePreview() {
    AslCodeSample(gutter = "12", code = "val name = \"ASL\"", stringHighlight = true)
}
