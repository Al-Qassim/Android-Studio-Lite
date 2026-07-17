package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.icon.IconChevron
import com.robotopia.androidstudiolite.designsystem.typography.Typography

/** Settings destination row — title, subtitle, trailing chevron. */
@Composable
fun SettingsRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Colors.Surface2)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            BasicText(
                text = title,
                style = Typography.Subtitle.copy(color = Colors.Text),
            )
            BasicText(
                text = subtitle,
                style = Typography.Body.copy(color = Colors.Muted),
            )
        }
        IconChevron(
            tint = Colors.Muted,
            size = 18.dp,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360)
@Composable
private fun SettingsRowPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SettingsRow(
            title = "Build account",
            subtitle = "Not connected",
        )
        SettingsRow(
            title = "Build account",
            subtitle = "GitHub · @alex-dev",
        )
    }
}
