package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.AslColors
import com.robotopia.androidstudiolite.designsystem.icon.AslIcons
import com.robotopia.androidstudiolite.designsystem.typography.AslTypography

enum class AslButtonVariant {
    Primary,
    Secondary,
    Disabled,
    DangerText,
    TextAction,
    Run,
}

@Composable
fun AslButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AslButtonVariant = AslButtonVariant.Primary,
    enabled: Boolean = variant != AslButtonVariant.Disabled,
) {
    val shape = RoundedCornerShape(8.dp)
    val isEnabled = enabled && variant != AslButtonVariant.Disabled
    val background = when (variant) {
        AslButtonVariant.Primary -> AslColors.Primary
        AslButtonVariant.Secondary -> Color.Transparent
        AslButtonVariant.Disabled -> AslColors.Disabled
        AslButtonVariant.DangerText, AslButtonVariant.TextAction -> Color.Transparent
        AslButtonVariant.Run -> AslColors.Primary
    }
    val contentColor = when (variant) {
        AslButtonVariant.Primary, AslButtonVariant.Run -> AslColors.OnPrimary
        AslButtonVariant.Secondary -> AslColors.Text
        AslButtonVariant.Disabled -> AslColors.Muted
        AslButtonVariant.DangerText -> AslColors.Danger
        AslButtonVariant.TextAction -> AslColors.Primary
    }
    val borderColor = when (variant) {
        AslButtonVariant.Secondary -> AslColors.Border
        else -> Color.Transparent
    }
    val displayLabel = if (variant == AslButtonVariant.Run) {
        "${AslIcons.Run} $label"
    } else {
        label
    }

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(shape)
            .background(background)
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(1.dp, borderColor, shape)
                } else {
                    Modifier
                },
            )
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = displayLabel,
            style = AslTypography.Button.copy(color = contentColor),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun AslButtonPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AslButton(label = "Create", onClick = {}, variant = AslButtonVariant.Primary)
        AslButton(label = "Cancel", onClick = {}, variant = AslButtonVariant.Secondary)
        AslButton(label = "Disabled", onClick = {}, variant = AslButtonVariant.Disabled)
        AslButton(label = "Delete", onClick = {}, variant = AslButtonVariant.DangerText)
        AslButton(label = "+ New", onClick = {}, variant = AslButtonVariant.TextAction)
        AslButton(label = "Run", onClick = {}, variant = AslButtonVariant.Run)
    }
}
