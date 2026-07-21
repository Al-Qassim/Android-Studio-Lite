package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import com.robotopia.androidstudiolite.designsystem.color.ColorScheme
import com.robotopia.androidstudiolite.feature.git.presentation.project.ConflictLinePaint

fun classifyConflictLinePaint(text: String): List<ConflictLinePaint> {
    var region = "normal"
    return text.split('\n').map { line ->
        when {
            line.startsWith("<<<<<<<") -> {
                region = "ours"
                ConflictLinePaint.Marker
            }
            line.startsWith("=======") && region == "ours" -> {
                region = "theirs"
                ConflictLinePaint.Marker
            }
            line.startsWith(">>>>>>>") -> {
                region = "normal"
                ConflictLinePaint.Marker
            }
            region == "ours" -> ConflictLinePaint.Ours
            region == "theirs" -> ConflictLinePaint.Theirs
            else -> ConflictLinePaint.None
        }
    }
}

/**
 * After an edit, drop paint on any line whose text changed.
 * Line inserts/deletes reclassify, then clear paint on shifted/changed rows.
 */
fun updateConflictLinePaintAfterEdit(
    previousText: String,
    previousPaint: List<ConflictLinePaint>,
    newText: String,
): List<ConflictLinePaint> {
    val prevLines = previousText.split('\n')
    val newLines = newText.split('\n')
    if (prevLines.size == newLines.size && previousPaint.size == prevLines.size) {
        return newLines.mapIndexed { index, line ->
            if (line != prevLines[index]) {
                ConflictLinePaint.None
            } else {
                previousPaint[index]
            }
        }
    }
    val classified = classifyConflictLinePaint(newText)
    return classified.mapIndexed { index, paint ->
        val prevLine = prevLines.getOrNull(index)
        val prevPaint = previousPaint.getOrNull(index) ?: ConflictLinePaint.None
        when {
            prevLine != null && newLines[index] != prevLine -> ConflictLinePaint.None
            prevLine != null && prevPaint == ConflictLinePaint.None -> ConflictLinePaint.None
            else -> paint
        }
    }
}

fun conflictHighlightOutputTransformation(
    paints: List<ConflictLinePaint>,
    colors: ColorScheme,
): OutputTransformation = OutputTransformation {
    val body = toString()
    val lines = body.split('\n')
    var offset = 0
    lines.forEachIndexed { index, line ->
        if (index > 0) offset++ // newline between lines
        val paint = paints.getOrElse(index) { ConflictLinePaint.None }
        val (fg, bg) = conflictPaintColors(paint, colors)
        val end = offset + line.length
        if (offset < end) {
            addStyle(SpanStyle(color = fg, background = bg), offset, end)
        }
        offset = end
    }
}

private fun conflictPaintColors(
    paint: ConflictLinePaint,
    colors: ColorScheme,
): Pair<Color, Color> = when (paint) {
    ConflictLinePaint.Marker -> colors.Danger to colors.Danger.copy(alpha = 0.22f)
    ConflictLinePaint.Ours -> colors.Primary to colors.Primary.copy(alpha = 0.16f)
    ConflictLinePaint.Theirs -> colors.Run to colors.Run.copy(alpha = 0.16f)
    ConflictLinePaint.None -> colors.Text to Color.Transparent
}
