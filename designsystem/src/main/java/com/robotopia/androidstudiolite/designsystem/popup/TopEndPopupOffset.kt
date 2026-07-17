package com.robotopia.androidstudiolite.designsystem.popup

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

/**
 * [androidx.compose.ui.window.Popup] offset for [androidx.compose.ui.Alignment.TopEnd].
 *
 * Prefer this over padding the popup content. Negative [end] insets from the trailing edge.
 *
 * @param includeStatusBars when the popup is window-scoped (not anchored inside a padded row).
 */
@Composable
fun topEndPopupOffset(
    top: Dp,
    end: Dp = 0.dp,
    includeStatusBars: Boolean = false,
): IntOffset {
    val density = LocalDensity.current
    val statusBarTop =
        if (includeStatusBars) WindowInsets.statusBars.getTop(density) else 0
    return with(density) {
        IntOffset(
            x = -end.roundToPx(),
            y = statusBarTop + top.roundToPx(),
        )
    }
}
