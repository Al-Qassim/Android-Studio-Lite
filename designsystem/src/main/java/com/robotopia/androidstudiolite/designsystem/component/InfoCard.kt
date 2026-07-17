package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.typography.Typography

/** Compact surface card for project/account summaries. */
@Composable
fun InfoCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    label: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Colors.Surface2)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (!label.isNullOrBlank()) {
            BasicText(
                text = label,
                style = Typography.Label.copy(color = Colors.Muted),
            )
        }
        BasicText(
            text = title,
            style = Typography.Subtitle.copy(color = Colors.Text),
        )
        if (!subtitle.isNullOrBlank()) {
            BasicText(
                text = subtitle,
                style = Typography.Body.copy(color = Colors.Muted),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360)
@Composable
private fun InfoCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        InfoCard(
            title = "HelloCompose",
            subtitle = "com.example.hellocompose",
        )
        InfoCard(
            label = "GitHub",
            title = "@alex-dev",
        )
    }
}
