package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.AslColors
import com.robotopia.androidstudiolite.designsystem.typography.AslTypography

@Composable
fun AslEmptyState(
    title: String,
    hint: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = title,
            style = AslTypography.Subtitle.copy(
                color = AslColors.Text,
                textAlign = TextAlign.Center,
            ),
        )
        BasicText(
            text = hint,
            style = AslTypography.Body.copy(
                color = AslColors.Muted,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun AslEmptyStatePreview() {
    AslEmptyState(
        title = "No projects yet",
        hint = "Tap + New to create your first project.",
    )
}
