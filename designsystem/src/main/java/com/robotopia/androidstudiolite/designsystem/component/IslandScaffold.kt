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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors

private val IslandCorner = 12.dp
private val IslandGap = 6.dp
private val IslandEdge = 8.dp

/** Radial glow sits at 20% of canvas width, pinned to the top edge. */
private const val CanvasGlowCenterXFraction = 0.2f
private const val CanvasGlowAlpha = 0.5f
/** Glow radius as a fraction of the longer side — keeps the falloff inside bounds. */
private const val CanvasGlowRadiusFraction = 0.95f

/**
 * Islands-style screen chrome (Android Studio New UI).
 *
 * - **Canvas:** radial primary glow on a dark sea (draws edge-to-edge under system bars).
 * - **Top bar:** sits on the canvas below the status bar inset.
 * - **Body:** darker rounded island (files list, editor, …).
 * - **Footer:** optional strip inside the same island, under an [InsetDivider]
 *   (e.g. [MoveBar], Cancel) — not a second island.
 * - **Insets:** status/navigation bar padding is applied here — not in the Activity.
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
            .background(Colors.Bg),
        content = content,
    )
}

private fun Modifier.islandCanvasBackground(): Modifier = drawBehind {
    // Solid sea first so edges never show a hard-clipped glow against a different color.
    drawRect(color = Colors.CanvasBottom)
    val center = Offset(
        x = size.width * CanvasGlowCenterXFraction,
        y = 0f,
    )
    val radius = maxOf(size.width, size.height) * CanvasGlowRadiusFraction
    drawRect(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to Colors.Primary.copy(alpha = CanvasGlowAlpha),
                0.55f to Colors.Primary.copy(alpha = CanvasGlowAlpha * 0.35f),
                1f to Colors.Primary.copy(alpha = 0f),
            ),
            center = center,
            radius = radius,
        ),
    )
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
