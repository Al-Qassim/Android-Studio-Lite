package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.LocalColorScheme
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.editor.codeHighlightOutputTransformation
import com.robotopia.androidstudiolite.designsystem.typography.Typography

/**
 * Multi-line code field with a scrolling line-number gutter.
 *
 * Scrolls inside a viewport-sized text field (not an outer scroll of the full document). That keeps
 * focus/caret bring-into-view on the writing cursor instead of jumping to the top of the file.
 *
 * @param wrapText when true, lines soft-wrap to the viewport width; when false, the editor
 * scrolls horizontally for long lines.
 * @param outputTransformation defaults to Kotlin syntax highlighting; override for conflict paints.
 */
@Composable
fun CodeEditorField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    wrapText: Boolean = false,
    outputTransformation: OutputTransformation? = null,
) {
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()
    val colors = LocalColorScheme.current
    val highlight = outputTransformation
        ?: remember(colors) { codeHighlightOutputTransformation(colors) }
    val textFieldState = rememberTextFieldState(initialText = value)

    LaunchedEffect(value) {
        if (value != textFieldState.text.toString()) {
            textFieldState.setTextAndPlaceCursorAtEnd(value)
        }
    }
    LaunchedEffect(textFieldState) {
        var lastEmitted = textFieldState.text.toString()
        snapshotFlow { textFieldState.text.toString() }
            .collect { text ->
                if (text != lastEmitted) {
                    lastEmitted = text
                    onValueChange(text)
                }
            }
    }

    val lineCount = textFieldState.text.count { it == '\n' } + 1
    val gutterText = remember(lineCount) {
        buildString(lineCount * 3) {
            for (line in 1..lineCount) {
                if (line > 1) append('\n')
                append(line)
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (wrapText) {
                    Modifier
                } else {
                    Modifier.horizontalScroll(horizontalScroll)
                },
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(min = 28.dp)
                .padding(end = 12.dp)
                .clipToBounds(),
        ) {
            BasicText(
                text = gutterText,
                style = Typography.CodeGutter.copy(
                    color = Theme.colors.Gutter,
                    textAlign = TextAlign.End,
                ),
                // Parent Box is viewport-tall; without unbounded height only the first screen of
                // numbers is measured, so scrolling past that leaves a blank gutter.
                modifier = Modifier
                    .wrapContentHeight(unbounded = true, align = Alignment.Top)
                    .offset { IntOffset(0, -verticalScroll.value) },
            )
        }
        BasicTextField(
            state = textFieldState,
            modifier = if (wrapText) {
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
            } else {
                Modifier.fillMaxHeight()
            },
            textStyle = Typography.Code.copy(color = Theme.colors.Text),
            cursorBrush = SolidColor(Theme.colors.Primary),
            lineLimits = TextFieldLineLimits.MultiLine(),
            scrollState = verticalScroll,
            outputTransformation = highlight,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360, heightDp = 240)
@Composable
private fun CodeEditorFieldPreview() {
    CodeEditorField(
        value = """
            package com.example

            fun main() {
                println("hi")
            }
        """.trimIndent(),
        onValueChange = {},
        wrapText = true,
        modifier = Modifier.fillMaxHeight(),
    )
}
