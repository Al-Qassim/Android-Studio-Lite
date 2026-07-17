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

/**
 * Android Studio Lite design tokens — JetBrains New UI / Islands Dark family.
 *
 * Anchored to Android Studio’s dark chrome: shell `#1E1F22`, island surfaces
 * `#2B2D30`, accent blue `#3574F0`, label gray `#DFE1E5`.
 */
object Colors {
    /** App / page shell (IDE content / editor island). */
    val Bg = Color(0xFF1E1F22)
    val Canvas = Bg

    /** Raised island (cards, dialogs, bars). */
    val Surface = Color(0xFF2B2D30)
    val Surface2 = Color(0xFF26282B)
    val Input = Surface2
    val Menu = Color(0xFF2B2D30)

    /** Code well — same family as IDE editor. */
    val Editor = Color(0xFF1E1F22)

    val Border = Color(0xFF43454A)
    val Text = Color(0xFFDFE1E5)
    val Muted = Color(0xFF6F737A)
    val Muted2 = Color(0xFF5A5D63)
    val Gutter = Muted2

    /** Kit / New UI primary blue. */
    val Primary = Color(0xFF3574F0)
    val OnPrimary = Color(0xFFFFFFFF)

    val Danger = Color(0xFFE35252)
    val Warning = Color(0xFFF2C55C)

    /** Selected row / tree (blue wash, not green). */
    val Selection = Color(0xFF2E436E)

    val CodeString = Color(0xFF6AAB73)
    val CodeAnnotation = Color(0xFF57AAF7)
    val Disabled = Color(0xFF4E5157)
}

private data class ColorSwatch(val name: String, val color: Color)

private fun Color.toHexLabel(): String =
    "#%06X".format(0xFFFFFF and toArgb())

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360)
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
            .background(Colors.Bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = "Colors · Islands Dark",
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
