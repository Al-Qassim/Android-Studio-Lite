package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.AslColors
import com.robotopia.androidstudiolite.designsystem.icon.AslIcons
import com.robotopia.androidstudiolite.designsystem.typography.AslTypography

/**
 * Success toast — pill shape, surface fill, no border.
 */
@Composable
fun AslToast(
    message: String,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(999.dp)
    Row(
        modifier = modifier
            .shadow(6.dp, shape)
            .clip(shape)
            .background(AslColors.Surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = AslIcons.Success,
            style = AslTypography.BodyMedium.copy(color = AslColors.Primary),
        )
        BasicText(
            text = message,
            style = AslTypography.BodyMedium.copy(color = AslColors.Text),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun AslToastPreview() {
    AslToast(
        message = "File saved",
        modifier = Modifier.padding(16.dp),
    )
}
