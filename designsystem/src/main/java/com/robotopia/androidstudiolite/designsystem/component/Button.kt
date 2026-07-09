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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.icon.IconRun
import com.robotopia.androidstudiolite.designsystem.typography.Typography

enum class ButtonVariant {
    Primary,
    Secondary,
    Disabled,
    DangerText,
    TextAction,
    Run,
}

@Composable
fun Button(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = variant != ButtonVariant.Disabled,
) {
    val shape = RoundedCornerShape(8.dp)
    val isEnabled = enabled && variant != ButtonVariant.Disabled
    val background = when (variant) {
        ButtonVariant.Primary -> Colors.Primary
        ButtonVariant.Secondary -> Color.Transparent
        ButtonVariant.Disabled -> Colors.Disabled
        ButtonVariant.DangerText, ButtonVariant.TextAction -> Color.Transparent
        ButtonVariant.Run -> Colors.Primary
    }
    val contentColor = when (variant) {
        ButtonVariant.Primary, ButtonVariant.Run -> Colors.OnPrimary
        ButtonVariant.Secondary -> Colors.Text
        ButtonVariant.Disabled -> Colors.Muted
        ButtonVariant.DangerText -> Colors.Danger
        ButtonVariant.TextAction -> Colors.Primary
    }
    val borderColor = when (variant) {
        ButtonVariant.Secondary -> Colors.Border
        else -> Color.Transparent
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
        if (variant == ButtonVariant.Run) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                IconRun(tint = contentColor, size = 16.dp)
                BasicText(
                    text = label,
                    style = Typography.Button.copy(color = contentColor),
                )
            }
        } else {
            BasicText(
                text = label,
                style = Typography.Button.copy(color = contentColor),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun ButtonPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(label = "Create", onClick = {}, variant = ButtonVariant.Primary)
        Button(label = "Cancel", onClick = {}, variant = ButtonVariant.Secondary)
        Button(label = "Disabled", onClick = {}, variant = ButtonVariant.Disabled)
        Button(label = "Delete", onClick = {}, variant = ButtonVariant.DangerText)
        Button(label = "+ New", onClick = {}, variant = ButtonVariant.TextAction)
        Button(label = "Run", onClick = {}, variant = ButtonVariant.Run)
    }
}
