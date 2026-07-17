package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

/**
 * Islands-style screen chrome (Android Studio New UI).
 *
 * - **Canvas:** radial primary glow on a dark sea (no section dividers).
 * - **Top bar:** sits on the canvas (no rounded plate).
 * - **Body:** darker rounded island (files list, editor, …).
 * - **Footer:** optional strip inside the same island, under an [InsetDivider]
 *   (e.g. [MoveBar], Cancel) — not a second island.
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
        topBar()
        IslandPanel(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = edgePadding)
                .padding(top = islandGap, bottom = edgePadding),
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
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Colors.Primary.copy(alpha = CanvasGlowAlpha),
                Colors.CanvasBottom,
            ),
            center = Offset(
                x = size.width * CanvasGlowCenterXFraction,
                y = 0f,
            ),
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
