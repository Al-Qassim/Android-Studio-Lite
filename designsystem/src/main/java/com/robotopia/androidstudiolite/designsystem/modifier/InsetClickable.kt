package com.robotopia.androidstudiolite.designsystem.modifier

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.LocalColorScheme

private val DefaultInsetHorizontal = 4.dp
private val DefaultInsetVertical = 2.dp
private val DefaultCorner = 8.dp

/**
 * Pads the row slightly, then clips click/ripple to a rounded rect so the
 * indication is not edge-to-edge across the parent.
 */
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.insetClickable(
    onClick: () -> Unit,
    selected: Boolean = false,
    enabled: Boolean = true,
    onLongClick: (() -> Unit)? = null,
    horizontalInset: Dp = DefaultInsetHorizontal,
    verticalInset: Dp = DefaultInsetVertical,
    corner: Dp = DefaultCorner,
): Modifier = composed {
    val selection = LocalColorScheme.current.Selection
    val shape = RoundedCornerShape(corner)
    this
        .padding(horizontal = horizontalInset, vertical = verticalInset)
        .clip(shape)
        .background(if (selected) selection else Color.Transparent)
        .then(
            if (onLongClick != null) {
                Modifier.combinedClickable(
                    enabled = enabled,
                    onClick = onClick,
                    onLongClick = onLongClick,
                )
            } else {
                Modifier.clickable(enabled = enabled, onClick = onClick)
            },
        )
}
