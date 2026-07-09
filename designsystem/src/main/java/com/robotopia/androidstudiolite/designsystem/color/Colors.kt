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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.typography.Typography

/**
 * Android Studio Lite design tokens — dark-themed palette from Figma.
 */
object Colors {
    val Bg = Color(0xFF12171C)
    val Canvas = Bg
    val Surface = Color(0xFF1F242B)
    val Surface2 = Color(0xFF1A1F26)
    val Input = Surface2
    val Menu = Color(0xFF242933)
    val Editor = Color(0xFF0D0F14)
    val Border = Color(0xFF404752)
    val Text = Color(0xFFF2F5F7)
    val Muted = Color(0xFF8C949E)
    val Muted2 = Color(0xFF73808C)
    val Gutter = Muted2
    val Primary = Color(0xFF38B873)
    val OnPrimary = Color(0xFF0D140F)
    val Danger = Color(0xFFE55959)
    val Warning = Color(0xFFE5A659)
    val Selection = Color(0xFF1F3829)
    val CodeString = Color(0xFF66CC8C)
    val CodeAnnotation = Color(0xFF73B2F2)
    val Disabled = Color(0xFF383D47)
}

private data class ColorSwatch(val name: String, val color: Color)

private fun Color.toHexLabel(): String =
    "#%06X".format(0xFFFFFF and value.toInt())

@Preview(showBackground = true, backgroundColor = 0xFF0F1216, widthDp = 360)
@Composable
private fun ColorsPreview() {
    val swatches = listOf(
        ColorSwatch("bg / canvas", Colors.Bg),
        ColorSwatch("surface", Colors.Surface),
        ColorSwatch("surface-2", Colors.Surface2),
        ColorSwatch("menu", Colors.Menu),
        ColorSwatch("editor", Colors.Editor),
        ColorSwatch("border", Colors.Border),
        ColorSwatch("text", Colors.Text),
        ColorSwatch("muted", Colors.Muted),
        ColorSwatch("muted-2", Colors.Muted2),
        ColorSwatch("primary", Colors.Primary),
        ColorSwatch("on-primary", Colors.OnPrimary),
        ColorSwatch("danger", Colors.Danger),
        ColorSwatch("warning", Colors.Warning),
        ColorSwatch("selection", Colors.Selection),
        ColorSwatch("code-string", Colors.CodeString),
        ColorSwatch("code-annotation", Colors.CodeAnnotation),
        ColorSwatch("disabled", Colors.Disabled),
    )

    Column(
        modifier = Modifier
            .background(Color(0xFF0F1216))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = "Colors",
            style = Typography.Display.copy(color = Colors.Text),
        )
        Spacer(modifier = Modifier.height(8.dp))
        swatches.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { swatch ->
                    ColorSwatchCell(
                        swatch = swatch,
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
            style = Typography.Caption.copy(color = Colors.Muted),
        )
        BasicText(
            text = swatch.color.toHexLabel(),
            style = Typography.Caption.copy(color = Colors.Muted2),
        )
    }
}
