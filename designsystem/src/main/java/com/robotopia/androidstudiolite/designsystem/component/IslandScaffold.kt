package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.color.LocalColorScheme

private val IslandCorner = 12.dp
private val IslandGap = 6.dp
private val IslandEdge = 8.dp

private const val CanvasGlowCenterXFraction = 0.2f
private const val CanvasGlowAlpha = 0.5f
private const val CanvasGlowRadiusFraction = 0.55f

/**
 * Islands-style screen chrome (Android Studio New UI).
 *
 * - **Canvas:** radial primary glow on a dark sea, edge-to-edge under system bars.
 * - **Top bar:** on the canvas, below the status-bar inset.
 * - **Body:** darker rounded island (files list, editor, …).
 * - **Footer:** optional strip inside the same island, under an [InsetDivider]
 *   (e.g. [MoveBar], Cancel).
 * - **Insets:** status and navigation bar padding for chrome content.
 */
@Composable
fun IslandScaffold(
    topBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    footer: (@Composable () -> Unit)? = null,
    edgePadding: Dp = IslandEdge,
    islandGap: Dp = IslandGap,
    body: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .islandCanvasBackground(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
        ) {
            topBar()
        }
        IslandPanel(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = edgePadding)
                .padding(top = islandGap)
                .navigationBarsPadding()
                .padding(bottom = edgePadding),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                content = body,
            )
            if (footer != null) {
                InsetDivider()
                footer()
            }
        }
    }
}

/** Darker rounded panel on the canvas sea. */
@Composable
fun IslandPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(IslandCorner)
    Column(
        modifier = modifier
            .clip(shape)
            .background(Theme.colors.Bg),
        content = content,
    )
}

private fun Modifier.islandCanvasBackground(): Modifier = composed {
    val canvasBottom = LocalColorScheme.current.CanvasBottom
    val primary = LocalColorScheme.current.Primary
    this.drawBehind {
        drawRect(color = canvasBottom)
        val center = Offset(
            x = size.width * CanvasGlowCenterXFraction,
            y = 0f,
        )
        val radius = maxOf(size.width, size.height) * CanvasGlowRadiusFraction
        drawRect(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0f to primary.copy(alpha = CanvasGlowAlpha),
                    0.55f to primary.copy(alpha = CanvasGlowAlpha * 0.35f),
                    1f to primary.copy(alpha = 0f),
                ),
                center = center,
                radius = radius,
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun IslandScaffoldPreview() {
    IslandScaffold(
        topBar = {
            TopBarBackTitleAdd(title = "MyApp", onRunClick = {})
        },
        footer = {
            MoveBar(name = "build.gradle.kts")
        },
    ) {
        PathBar(segments = listOf("/", "app", "src"))
        InsetDivider()
        FolderRow(name = "java")
        FolderRow(name = "res")
        FileRow(name = "AndroidManifest.xml", showChevron = false)
        Box(modifier = Modifier.weight(1f))
    }
}
