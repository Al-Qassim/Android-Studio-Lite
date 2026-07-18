package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.icon.IconAdd
import com.robotopia.androidstudiolite.designsystem.icon.IconBack
import com.robotopia.androidstudiolite.designsystem.icon.IconMore
import com.robotopia.androidstudiolite.designsystem.icon.IconRun
import com.robotopia.androidstudiolite.designsystem.typography.Typography

enum class ButtonVariant {
    Primary,
    Secondary,
    Neutral,
    Disabled,
    Danger,
    DangerText,
    TextAction,
}

enum class IconButtonVariant {
    Primary,
    Secondary,
    Ghost,
    Danger,
}

@Composable
fun Button(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = variant != ButtonVariant.Disabled,
    leadingIcon: (@Composable (tint: Color, size: Dp) -> Unit)? = null,
    trailingIcon: (@Composable (tint: Color, size: Dp) -> Unit)? = null,
) {
    val shape = RoundedCornerShape(8.dp)
    val isEnabled = enabled && variant != ButtonVariant.Disabled
    val background = when (variant) {
        ButtonVariant.Primary -> Theme.colors.Primary
        ButtonVariant.Secondary -> Color.Transparent
        ButtonVariant.Neutral -> Theme.colors.Bg
        ButtonVariant.Disabled -> Theme.colors.Disabled
        ButtonVariant.Danger -> Theme.colors.Danger
        ButtonVariant.DangerText, ButtonVariant.TextAction -> Color.Transparent
    }
    val contentColor = when (variant) {
        ButtonVariant.Primary -> Theme.colors.OnPrimary
        ButtonVariant.Secondary, ButtonVariant.Neutral -> Theme.colors.Text
        ButtonVariant.Disabled -> Theme.colors.Muted
        ButtonVariant.Danger -> Theme.colors.Text
        ButtonVariant.DangerText -> Theme.colors.Danger
        ButtonVariant.TextAction -> Theme.colors.Primary
    }
    val borderColor = when (variant) {
        ButtonVariant.Secondary -> Theme.colors.Border
        else -> Color.Transparent
    }
    val iconSize = 16.dp

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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            leadingIcon?.invoke(contentColor, iconSize)
            BasicText(
                text = label,
                style = Typography.Button.copy(color = contentColor),
            )
            trailingIcon?.invoke(contentColor, iconSize)
        }
    }
}

@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: IconButtonVariant = IconButtonVariant.Ghost,
    enabled: Boolean = true,
    size: Dp = 36.dp,
    iconSize: Dp = 20.dp,
    icon: @Composable (tint: Color, size: Dp) -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    val background = when (variant) {
        IconButtonVariant.Primary -> Theme.colors.Primary
        IconButtonVariant.Secondary -> Color.Transparent
        IconButtonVariant.Ghost, IconButtonVariant.Danger -> Color.Transparent
    }
    val contentColor = when (variant) {
        IconButtonVariant.Primary -> Theme.colors.OnPrimary
        IconButtonVariant.Secondary -> Theme.colors.Text
        IconButtonVariant.Ghost -> Theme.colors.Text
        IconButtonVariant.Danger -> Theme.colors.Danger
    }
    val borderColor = when (variant) {
        IconButtonVariant.Secondary -> Theme.colors.Border
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(background)
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(1.dp, borderColor, shape)
                } else {
                    Modifier
                },
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        icon(contentColor, iconSize)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "Button · variants")
@Composable
private fun ButtonPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(label = "Create", onClick = {}, variant = ButtonVariant.Primary)
        Button(label = "Cancel", onClick = {}, variant = ButtonVariant.Secondary)
        Button(label = "Neutral", onClick = {}, variant = ButtonVariant.Neutral)
        Button(label = "Disabled", onClick = {}, variant = ButtonVariant.Disabled)
        Button(label = "Delete", onClick = {}, variant = ButtonVariant.Danger)
        Button(label = "Delete text", onClick = {}, variant = ButtonVariant.DangerText)
        Button(label = "New", onClick = {}, variant = ButtonVariant.TextAction)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "Button · with icons")
@Composable
private fun ButtonWithIconsPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            label = "Run",
            onClick = {},
            variant = ButtonVariant.Primary,
            leadingIcon = { tint, size -> IconRun(tint = tint, size = size) },
        )
        Button(
            label = "New",
            onClick = {},
            variant = ButtonVariant.Secondary,
            leadingIcon = { tint, size -> IconAdd(tint = tint, size = size) },
        )
        Button(
            label = "More",
            onClick = {},
            variant = ButtonVariant.TextAction,
            trailingIcon = { tint, size -> IconMore(tint = tint, size = size) },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "IconButton · variants")
@Composable
private fun IconButtonPreview() {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = {},
            variant = IconButtonVariant.Primary,
            icon = { tint, size -> IconRun(tint = tint, size = size) },
        )
        IconButton(
            onClick = {},
            variant = IconButtonVariant.Secondary,
            icon = { tint, size -> IconAdd(tint = tint, size = size) },
        )
        IconButton(
            onClick = {},
            variant = IconButtonVariant.Ghost,
            icon = { tint, size -> IconBack(tint = tint, size = size) },
        )
        IconButton(
            onClick = {},
            variant = IconButtonVariant.Ghost,
            icon = { tint, size -> IconMore(tint = tint, size = size) },
        )
        IconButton(
            onClick = {},
            variant = IconButtonVariant.Danger,
            icon = { tint, size -> IconMore(tint = tint, size = size) },
        )
    }
}
