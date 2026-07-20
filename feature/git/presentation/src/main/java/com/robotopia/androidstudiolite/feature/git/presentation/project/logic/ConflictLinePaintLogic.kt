package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
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

fun conflictHighlightTransformation(
    paints: List<ConflictLinePaint>,
    colors: ColorScheme,
): VisualTransformation = VisualTransformation { text ->
    val body = text.text
    val lines = body.split('\n')
    val annotated = buildAnnotatedString {
        lines.forEachIndexed { index, line ->
            if (index > 0) append('\n')
            val paint = paints.getOrElse(index) { ConflictLinePaint.None }
            val (fg, bg) = conflictPaintColors(paint, colors)
            withStyle(SpanStyle(color = fg, background = bg)) {
                append(line)
            }
        }
    }
    TransformedText(annotated, OffsetMapping.Identity)
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
