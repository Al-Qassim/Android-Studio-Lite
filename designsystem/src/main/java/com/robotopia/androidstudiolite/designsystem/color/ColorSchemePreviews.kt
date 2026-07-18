package com.robotopia.androidstudiolite.designsystem.color

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.typography.Typography

private data class ColorSwatch(val name: String, val color: Color)

private fun Color.toHexLabel(): String =
    "#%06X".format(0xFFFFFF and toArgb())

private fun ColorScheme.swatches(): List<ColorSwatch> = listOf(
    ColorSwatch("island / bg", Bg),
    ColorSwatch("canvas", Canvas),
    ColorSwatch("canvas-top", CanvasTop),
    ColorSwatch("canvas-bottom", CanvasBottom),
    ColorSwatch("surface", Surface),
    ColorSwatch("surface-2", Surface2),
    ColorSwatch("menu", Menu),
    ColorSwatch("editor", Editor),
    ColorSwatch("border", Border),
    ColorSwatch("text", Text),
    ColorSwatch("muted", Muted),
    ColorSwatch("muted-2", Muted2),
    ColorSwatch("primary", Primary),
    ColorSwatch("on-primary", OnPrimary),
    ColorSwatch("run", Run),
    ColorSwatch("danger", Danger),
    ColorSwatch("warning", Warning),
    ColorSwatch("selection", Selection),
    ColorSwatch("code-string", CodeString),
    ColorSwatch("code-annotation", CodeAnnotation),
    ColorSwatch("code-keyword", CodeKeyword),
    ColorSwatch("code-function", CodeFunction),
    ColorSwatch("code-comment", CodeComment),
    ColorSwatch("code-number", CodeNumber),
    ColorSwatch("disabled", Disabled),
)

@Composable
private fun ColorSchemePreview(
    title: String,
    scheme: ColorScheme,
) {
    Column(
        modifier = Modifier
            .background(scheme.Bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = title,
            style = Typography.Display.copy(color = scheme.Text),
        )
        Spacer(modifier = Modifier.height(8.dp))
        scheme.swatches().chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { swatch ->
                    ColorSwatchCell(
                        swatch = swatch,
                        labelColor = scheme.Muted,
                        hexColor = scheme.Muted2,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ColorSwatchCell(
    swatch: ColorSwatch,
    labelColor: Color,
    hexColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(swatch.color),
        )
        Spacer(modifier = Modifier.height(4.dp))
        BasicText(
            text = swatch.name,
            style = Typography.Caption.copy(color = labelColor),
        )
        BasicText(
            text = swatch.color.toHexLabel(),
            style = Typography.Caption.copy(color = hexColor),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360, name = "Colors · Dark")
@Composable
private fun ColorsDarkPreview() {
    ColorSchemePreview(title = "Colors · Islands Dark", scheme = DarkColorScheme)
}

@Preview(showBackground = true, backgroundColor = 0xFFEBECF0, widthDp = 360, name = "Colors · Light")
@Composable
private fun ColorsLightPreview() {
    ColorSchemePreview(title = "Colors · Islands Light", scheme = LightColorScheme)
}

@Preview(showBackground = true, backgroundColor = 0xFF21222C, widthDp = 360, name = "Colors · Dracula")
@Composable
private fun ColorsDraculaPreview() {
    ColorSchemePreview(title = "Colors · Dracula", scheme = DraculaColorScheme)
}
