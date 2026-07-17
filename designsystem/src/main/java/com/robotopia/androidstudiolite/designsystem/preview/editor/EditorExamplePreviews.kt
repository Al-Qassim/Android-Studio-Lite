package com.robotopia.androidstudiolite.designsystem.preview.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.component.CodeSample
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.Toast
import com.robotopia.androidstudiolite.designsystem.component.ToastVariant
import com.robotopia.androidstudiolite.designsystem.component.TopBarEditorMore
import com.robotopia.androidstudiolite.designsystem.preview.ExamplePreviewBackground

enum class EditorExampleCase {
    Clean,
    DirtyToast,
}

private class EditorExampleCaseProvider : PreviewParameterProvider<EditorExampleCase> {
    override val values = EditorExampleCase.entries.asSequence()
    override fun getDisplayName(index: Int): String = values.elementAt(index).name
}

private val EditorPreviewLines = listOf(
    "1" to "package com.example.demo",
    "2" to "",
    "3" to "import android.os.Bundle",
    "4" to "",
    "5" to "// Entry activity for the sample app",
    "6" to "@Composable",
    "7" to "fun MainScreen(count: Int = 0) {",
    "8" to "    val title = \"Android Studio Lite\"",
    "9" to "    /* greet once on open */",
    "10" to "    if (count > 0) {",
    "11" to "        println(title)",
    "12" to "        showToast(title, count)",
    "13" to "    }",
    "14" to "}",
    "15" to "",
    "16" to "private fun showToast(message: String, times: Int) {",
    "17" to "    repeat(times) {",
    "18" to "        Log.d(\"Demo\", message)",
    "19" to "    }",
    "20" to "}",
)

@Preview(
    name = "Editor",
    showBackground = true,
    backgroundColor = ExamplePreviewBackground,
    widthDp = 360,
    heightDp = 640,
)
@Composable
fun EditorExamplePreview(
    @PreviewParameter(EditorExampleCaseProvider::class) case: EditorExampleCase,
) {
    when (case) {
        EditorExampleCase.Clean -> EditorCleanScreen()
        EditorExampleCase.DirtyToast -> EditorDirtyToastScreen()
    }
}

@Composable
private fun ColumnScope.EditorPreviewCode() {
    EditorPreviewLines.forEach { (gutter, code) ->
        CodeSample(gutter = gutter, code = code)
    }
    Spacer(modifier = Modifier.weight(1f))
}

@Composable
private fun EditorCleanScreen() {
    IslandScaffold(
        topBar = { TopBarEditorMore(fileName = "MainActivity.kt", onRunClick = {}) },
    ) {
        EditorPreviewCode()
    }
}

@Composable
private fun EditorDirtyToastScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        IslandScaffold(
            topBar = {
                TopBarEditorMore(
                    fileName = "MainActivity.kt",
                    isDirty = true,
                    onRunClick = {},
                )
            },
        ) {
            EditorPreviewCode()
        }
        Toast(
            message = "File saved",
            variant = ToastVariant.Success,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
        )
    }
}
