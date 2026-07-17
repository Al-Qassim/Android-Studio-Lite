package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.typography.Typography

private const val SpokeCount = 8

/**
 * Classic 8-spoke activity indicator (pill bars, fading opacity).
 * Matches Android Studio / iOS-style throbbers rather than a Material arc.
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    label: String? = null,
    size: Dp = 32.dp,
) {
    val transition = rememberInfiniteTransition(label = "loading")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotation",
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size.minDimension
            val spokeWidth = canvasSize * 0.12f
            val spokeLength = canvasSize * 0.28f
            val spokeRadius = spokeWidth / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val spokeCenterRadius = (canvasSize / 2f) - (spokeLength / 2f) - spokeWidth * 0.15f
            val spokeColor = Colors.Text

            rotate(degrees = rotation, pivot = center) {
                repeat(SpokeCount) { index ->
                    val alpha = 1f - (index.toFloat() / SpokeCount)
                    val angleDegrees = index * (360f / SpokeCount)
                    rotate(degrees = angleDegrees, pivot = center) {
                        val topLeft = Offset(
                            x = center.x - spokeWidth / 2f,
                            y = center.y - spokeCenterRadius - spokeLength / 2f,
                        )
                        drawRoundRect(
                            color = spokeColor.copy(alpha = alpha.coerceIn(0.15f, 1f)),
                            topLeft = topLeft,
                            size = Size(spokeWidth, spokeLength),
                            cornerRadius = CornerRadius(spokeRadius, spokeRadius),
                        )
                    }
                }
            }
        }
        if (!label.isNullOrBlank()) {
            BasicText(
                text = label,
                style = Typography.Body.copy(color = Colors.Muted),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22)
@Composable
private fun LoadingIndicatorPreview() {
    LoadingIndicator(
        label = "Opening file…",
        modifier = Modifier.padding(24.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "LoadingIndicator · bare")
@Composable
private fun LoadingIndicatorBarePreview() {
    LoadingIndicator(modifier = Modifier.padding(24.dp))
}
