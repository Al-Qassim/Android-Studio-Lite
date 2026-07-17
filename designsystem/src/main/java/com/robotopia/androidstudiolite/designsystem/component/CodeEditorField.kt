package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.editor.CodeHighlightTransformation
import com.robotopia.androidstudiolite.designsystem.typography.Typography

/**
 * Multi-line code field with a scrolling line-number gutter (Islands / New UI).
 */
@Composable
fun CodeEditorField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val lineCount = remember(value) { value.count { it == '\n' } + 1 }
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
            .verticalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 12.dp),
    ) {
        BasicText(
            text = gutterText,
            style = Typography.CodeGutter.copy(
                color = Colors.Gutter,
                textAlign = TextAlign.End,
            ),
            modifier = Modifier
                .widthIn(min = 28.dp)
                .padding(end = 12.dp),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            textStyle = Typography.Code.copy(color = Colors.Text),
            cursorBrush = SolidColor(Colors.Primary),
            visualTransformation = CodeHighlightTransformation,
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
    )
}
