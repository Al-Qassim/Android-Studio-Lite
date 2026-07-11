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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.typography.Typography

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
            val stroke = Stroke(width = size.toPx() / 10f, cap = StrokeCap.Round)
            rotate(rotation) {
                drawArc(
                    color = Colors.Primary,
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = stroke,
                )
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

@Preview(showBackground = true, backgroundColor = 0xFF0D0F14)
@Composable
private fun LoadingIndicatorPreview() {
    LoadingIndicator(
        label = "Opening file…",
        modifier = Modifier.padding(24.dp),
    )
}
