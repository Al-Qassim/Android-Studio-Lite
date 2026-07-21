package com.robotopia.androidstudiolite.designsystem.editor

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import com.robotopia.androidstudiolite.designsystem.color.ColorScheme
import com.robotopia.androidstudiolite.designsystem.color.DarkColorScheme

private val KotlinKeywords = setOf(
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun",
    "if", "in", "interface", "is", "null", "object", "package", "return",
    "super", "this", "throw", "true", "try", "typealias", "typeof", "val",
    "var", "when", "while",
    "by", "catch", "constructor", "delegate", "dynamic", "field", "file",
    "finally", "get", "import", "init", "param", "property", "receiver",
    "set", "setparam", "where",
    "actual", "abstract", "annotation", "companion", "const", "crossinline",
    "data", "enum", "expect", "external", "final", "infix", "inline",
    "inner", "internal", "lateinit", "noinline", "open", "operator", "out",
    "override", "private", "protected", "public", "reified", "sealed",
    "suspend", "tailrec", "value", "vararg",
)

/**
 * Lightweight Kotlin-oriented syntax coloring for the code editor.
 *
 * Colors keywords, strings, comments, annotations, numbers, and function
 * names (identifier followed by `(`). Not a full parser — good enough for
 * readable editing in Android Studio Lite.
 */
fun highlightCode(
    code: String,
    colors: ColorScheme = DarkColorScheme,
): AnnotatedString = buildAnnotatedString {
    var i = 0
    while (i < code.length) {
        when {
            code.startsWith("//", i) -> {
                val end = code.indexOf('\n', i).let { if (it < 0) code.length else it }
                appendStyled(code, i, end, colors.CodeComment)
                i = end
            }
            code.startsWith("/*", i) -> {
                val end = code.indexOf("*/", i + 2).let { if (it < 0) code.length else it + 2 }
                appendStyled(code, i, end, colors.CodeComment)
                i = end
            }
            code[i] == '"' -> {
                val end = endOfString(code, i)
                appendStyled(code, i, end, colors.CodeString)
                i = end
            }
            code[i] == '\'' -> {
                val end = endOfCharLiteral(code, i)
                appendStyled(code, i, end, colors.CodeString)
                i = end
            }
            code[i] == '@' -> {
                val end = endOfIdentifier(code, i + 1)
                appendStyled(code, i, end, colors.CodeAnnotation)
                i = end
            }
            code[i].isDigit() -> {
                val end = endOfNumber(code, i)
                appendStyled(code, i, end, colors.CodeNumber)
                i = end
            }
            code[i].isKotlinIdentStart() -> {
                val end = endOfIdentifier(code, i)
                val word = code.substring(i, end)
                val color = when {
                    word in KotlinKeywords -> colors.CodeKeyword
                    isFollowedByParen(code, end) -> colors.CodeFunction
                    else -> colors.Text
                }
                appendStyled(code, i, end, color)
                i = end
            }
            else -> {
                withStyle(SpanStyle(color = colors.Text)) {
                    append(code[i])
                }
                i++
            }
        }
    }
}

/** [VisualTransformation] that applies [highlightCode] for [colors]. */
fun codeHighlightTransformation(
    colors: ColorScheme = DarkColorScheme,
): VisualTransformation = VisualTransformation { text ->
    TransformedText(highlightCode(text.text, colors), OffsetMapping.Identity)
}

/**
 * [OutputTransformation] that applies [highlightCode] styles for [BasicTextField] with
 * [androidx.compose.foundation.text.input.TextFieldState].
 */
fun codeHighlightOutputTransformation(
    colors: ColorScheme = DarkColorScheme,
): OutputTransformation = OutputTransformation {
    val annotated = highlightCode(toString(), colors)
    annotated.spanStyles.forEach { range ->
        addStyle(range.item, range.start, range.end)
    }
}

/** Dark-scheme highlight transform (default product theme). */
val CodeHighlightTransformation = codeHighlightTransformation(DarkColorScheme)

private fun AnnotatedString.Builder.appendStyled(
    code: String,
    start: Int,
    end: Int,
    color: Color,
) {
    withStyle(SpanStyle(color = color)) {
        append(code, start, end)
    }
}

private fun endOfString(code: String, start: Int): Int {
    var i = start + 1
    while (i < code.length) {
        when (code[i]) {
            '\\' -> i += 2
            '"' -> return i + 1
            '\n' -> return i
            else -> i++
        }
    }
    return code.length
}

private fun endOfCharLiteral(code: String, start: Int): Int {
    var i = start + 1
    while (i < code.length) {
        when (code[i]) {
            '\\' -> i += 2
            '\'' -> return i + 1
            '\n' -> return i
            else -> i++
        }
    }
    return code.length
}

private fun endOfIdentifier(code: String, start: Int): Int {
    var i = start
    while (i < code.length && code[i].isKotlinIdentPart()) i++
    return i
}

/** `name(` or `name (` — used as a simple function-name heuristic. */
private fun isFollowedByParen(code: String, afterIdent: Int): Boolean {
    var i = afterIdent
    while (i < code.length && code[i].isWhitespace()) i++
    return i < code.length && code[i] == '('
}

private fun endOfNumber(code: String, start: Int): Int {
    var i = start
    while (i < code.length && (code[i].isDigit() || code[i] == '.' || code[i] == '_')) i++
    return i
}

private fun Char.isKotlinIdentStart(): Boolean =
    isLetter() || this == '_'

private fun Char.isKotlinIdentPart(): Boolean =
    isLetterOrDigit() || this == '_'
