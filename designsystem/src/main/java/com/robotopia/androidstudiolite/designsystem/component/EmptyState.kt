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
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.typography.Typography

@Composable
fun EmptyState(
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
            style = Typography.Subtitle.copy(
                color = Colors.Text,
                textAlign = TextAlign.Center,
            ),
        )
        BasicText(
            text = hint,
            style = Typography.Body.copy(
                color = Colors.Muted,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22)
@Composable
private fun EmptyStatePreview() {
    EmptyState(
        title = "No projects yet",
        hint = "Tap + to create your first project.",
    )
}
