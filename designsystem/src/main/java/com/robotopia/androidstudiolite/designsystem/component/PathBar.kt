package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.typography.Typography

private const val PathSeparator = " / "
private const val PathEllipsis = "…"

/**
 * Collapses leading path segments to [PathEllipsis] so the trail fits [availableWidthPx].
 *
 * Priority: show the current file/directory name (last segment) in full whenever possible.
 * Only spend leftover width on trailing parents; never keep parents that would force the
 * current name to ellipsize. Ellipsize the current name only when it alone overflows.
 */
internal fun collapsePathSegments(
    segments: List<String>,
    availableWidthPx: Int,
    measureWidth: (String) -> Int,
    separator: String = PathSeparator,
): List<String> {
    if (segments.isEmpty() || segments.size == 1 || availableWidthPx <= 0) return segments

    // Keep a leading root "/" marker; collapse only the path under it.
    if (segments.first() == "/") {
        val rest = segments.drop(1)
        if (rest.isEmpty()) return segments
        val rootWidth = measureWidth("/") + measureWidth(" ")
        val collapsedRest = collapsePathSegments(
            segments = rest,
            availableWidthPx = (availableWidthPx - rootWidth).coerceAtLeast(0),
            measureWidth = measureWidth,
            separator = separator,
        )
        return listOf("/") + collapsedRest
    }

    val sepWidth = measureWidth(separator)
    val current = segments.last()
    val parents = segments.dropLast(1)
    val currentWidth = measureWidth(current)

    fun trailWidth(parts: List<String>): Int {
        if (parts.isEmpty()) return 0
        return parts.sumOf(measureWidth) + sepWidth * (parts.lastIndex)
    }

    if (trailWidth(segments) <= availableWidthPx) return segments

    // Current name alone needs the whole bar — drop every parent.
    if (currentWidth + sepWidth >= availableWidthPx) {
        return listOf(current)
    }

    // Reserve full current name; fill leftover with as many trailing parents as fit.
    for (keep in (parents.size - 1) downTo 0) {
        val kept = parents.takeLast(keep)
        val leading = if (keep == 0) {
            listOf(PathEllipsis)
        } else {
            listOf(PathEllipsis) + kept
        }
        if (trailWidth(leading) + sepWidth + currentWidth <= availableWidthPx) {
            return leading + current
        }
    }

    return listOf(current)
}

@Composable
fun PathBar(
    segments: List<String>,
    modifier: Modifier = Modifier,
) {
    PathTrail(
        segments = segments,
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(Colors.Editor)
            .padding(horizontal = 12.dp),
        parentStyle = Typography.Code.copy(color = Colors.Muted),
        currentStyle = Typography.Code.copy(color = Colors.Text),
        separatorStyle = Typography.Code.copy(color = Colors.Muted2),
    )
}

/**
 * Shared path trail used by [PathBar] and top bars.
 * Prioritizes the current segment; collapses parents to …; ellipsizes the current name at the end only if needed.
 */
@Composable
internal fun PathTrail(
    segments: List<String>,
    modifier: Modifier = Modifier,
    parentStyle: TextStyle,
    currentStyle: TextStyle,
    separatorStyle: TextStyle = parentStyle,
) {
    if (segments.isEmpty()) return

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        val density = LocalDensity.current
        val textMeasurer = rememberTextMeasurer()
        val availableWidthPx = with(density) { maxWidth.roundToPx() }
        val currentName = segments.last()

        val displaySegments = remember(segments, availableWidthPx, parentStyle, currentStyle, separatorStyle) {
            collapsePathSegments(
                segments = segments,
                availableWidthPx = availableWidthPx,
                measureWidth = { text ->
                    val style = when (text) {
                        currentName -> currentStyle
                        PathSeparator -> separatorStyle
                        else -> parentStyle
                    }
                    textMeasurer.measure(text = text, style = style).size.width
                },
            )
        }

        val parents = displaySegments.dropLast(1)
        val current = displaySegments.last()

        // Non-weighted current is measured first so it keeps its full width;
        // parents only consume leftover space.
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (parents.isNotEmpty()) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    parents.forEach { segment ->
                        BasicText(
                            text = segment,
                            style = if (segment == PathEllipsis) separatorStyle else parentStyle,
                            maxLines = 1,
                        )
                        BasicText(
                            // Leading "/" is the root marker; use a space before the next segment
                            // so the trail reads "/ app / src" instead of "/ / app / src".
                            text = if (segment == "/") " " else PathSeparator,
                            style = separatorStyle,
                            maxLines = 1,
                        )
                    }
                }
            }
            BasicText(
                text = current,
                style = currentStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (parents.isEmpty()) Modifier.fillMaxWidth() else Modifier,
            )
        }
    }
}

@Composable
fun CodeSample(
    gutter: String,
    code: String,
    modifier: Modifier = Modifier,
    stringHighlight: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Colors.Editor)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        BasicText(
            text = gutter,
            style = Typography.CodeGutter.copy(color = Colors.Gutter),
            modifier = Modifier.padding(end = 12.dp),
        )
        BasicText(
            text = code,
            style = Typography.Code.copy(
                color = if (stringHighlight) Colors.CodeString else Colors.Text,
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360, name = "PathBar · root")
@Composable
private fun PathBarRootPreview() {
    PathBar(segments = listOf("/"))
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360, name = "PathBar · short")
@Composable
private fun PathBarShortPreview() {
    PathBar(segments = listOf("MyApp", "app"))
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360, name = "PathBar · directory")
@Composable
private fun PathBarDirectoryPreview() {
    PathBar(segments = listOf("app", "src", "main", "java"))
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360, name = "PathBar · file")
@Composable
private fun PathBarFilePreview() {
    PathBar(segments = listOf("app", "src", "main", "java", "MainActivity.kt"))
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 280, name = "PathBar · deep (auto collapse)")
@Composable
private fun PathBarDeepPreview() {
    PathBar(
        segments = listOf(
            "MyApp",
            "app",
            "src",
            "main",
            "java",
            "com",
            "robotopia",
            "androidstudiolite",
            "ui",
            "screens",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 280, name = "PathBar · long name (auto ellipsis)")
@Composable
private fun PathBarLongNamePreview() {
    PathBar(
        segments = listOf(
            "app",
            "src",
            "main",
            "java",
            "VeryLongActivityNameThatShouldEllipsizeAtTheEnd.kt",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 200, name = "PathBar · narrow")
@Composable
private fun PathBarNarrowPreview() {
    PathBar(segments = listOf("app", "src", "main", "java", "MainActivity.kt"))
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22)
@Composable
private fun CodeSamplePreview() {
    CodeSample(gutter = "12", code = "val name = \"ASL\"", stringHighlight = true)
}
