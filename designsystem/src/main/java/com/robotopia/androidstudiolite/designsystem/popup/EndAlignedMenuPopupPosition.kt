package com.robotopia.androidstudiolite.designsystem.popup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider

/**
 * Positions a trailing-edge menu under its anchor when space allows,
 * otherwise above the anchor so the menu stays on screen.
 */
@Composable
fun rememberEndAlignedMenuPopupPositionProvider(
    gap: Dp = 4.dp,
    endPadding: Dp = 8.dp,
): PopupPositionProvider {
    val density = LocalDensity.current
    return remember(density, gap, endPadding) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize,
            ): IntOffset {
                val gapPx = with(density) { gap.roundToPx() }
                val endPadPx = with(density) { endPadding.roundToPx() }
                val maxX = (windowSize.width - popupContentSize.width).coerceAtLeast(0)
                val x = (anchorBounds.right - popupContentSize.width - endPadPx)
                    .coerceIn(0, maxX)
                val belowY = anchorBounds.bottom + gapPx
                val fitsBelow = belowY + popupContentSize.height <= windowSize.height
                val y = if (fitsBelow) {
                    belowY
                } else {
                    (anchorBounds.top - popupContentSize.height - gapPx).coerceAtLeast(0)
                }
                return IntOffset(x = x, y = y)
            }
        }
    }
}
