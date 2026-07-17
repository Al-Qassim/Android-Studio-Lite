package com.robotopia.androidstudiolite.designsystem.modifier

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer

private const val OverlayEnterMs = 160
private const val OverlayEnterFromScale = 0.92f

/**
 * Short fade + scale-in for menus and dialogs when their host Popup/Dialog appears.
 */
@Composable
fun Modifier.overlayEnter(
    transformOrigin: TransformOrigin = TransformOrigin.Center,
): Modifier {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = OverlayEnterMs,
                easing = FastOutSlowInEasing,
            ),
        )
    }
    val value = progress.value
    return graphicsLayer {
        this.transformOrigin = transformOrigin
        alpha = value
        val scale = OverlayEnterFromScale + (1f - OverlayEnterFromScale) * value
        scaleX = scale
        scaleY = scale
    }
}
