package com.robotopia.androidstudiolite.designsystem.typography

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robotopia.androidstudiolite.designsystem.R
import com.robotopia.androidstudiolite.designsystem.color.Colors

/**
 * Typography tokens using bundled Inter font files.
 */
object Typography {
    private val Family = FontFamily(
        Font(R.font.inter_regular, FontWeight.Normal),
        Font(R.font.inter_medium, FontWeight.Medium),
        Font(R.font.inter_bold, FontWeight.Bold),
    )

    val Display = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    )

    val TitleNav = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    )

    val TitleEditor = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    )

    val Headline = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    )

    val Subtitle = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    )

    val BodyStrong = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
    )

    val Body = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    )

    val BodyMedium = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
    )

    val Label = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
    )

    val Caption = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
    )

    val Menu = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )

    val Button = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    )

    val ButtonCompact = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )

    val Code = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
    )

    val CodeGutter = TextStyle(
        fontFamily = Family,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 16.sp,
    )
}

private data class TypographyToken(
    val name: String,
    val style: TextStyle,
    val meta: String,
    val sample: String,
)

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360)
@Composable
private fun TypographyPreview() {
    val tokens = listOf(
        TypographyToken("Display", Typography.Display, "18 / Bold · 24 LH", "Display"),
        TypographyToken("TitleNav", Typography.TitleNav, "16 / Bold · 22 LH", "Projects"),
        TypographyToken("TitleEditor", Typography.TitleEditor, "15 / Bold · 20 LH", "MainActivity.kt"),
        TypographyToken("Headline", Typography.Headline, "18 / Bold · 24 LH", "New file"),
        TypographyToken("Subtitle", Typography.Subtitle, "16 / Bold · 22 LH", "Android Studio Lite"),
        TypographyToken("BodyStrong", Typography.BodyStrong, "15 / Bold · 20 LH", "java"),
        TypographyToken("Body", Typography.Body, "13 / Regular · 18 LH", "Browse project files"),
        TypographyToken("BodyMedium", Typography.BodyMedium, "13 / Medium · 18 LH", "File saved"),
        TypographyToken("Label", Typography.Label, "11 / Medium · 14 LH", "SECTION"),
        TypographyToken("Caption", Typography.Caption, "10 / Regular · 14 LH", "Last opened · Today"),
        TypographyToken("Menu", Typography.Menu, "12 / Regular · 16 LH", "Rename"),
        TypographyToken("Button", Typography.Button, "14 / Bold · 18 LH", "Create"),
        TypographyToken("ButtonCompact", Typography.ButtonCompact, "12 / Bold · 16 LH", "+ New"),
        TypographyToken("Code", Typography.Code, "11 / Regular · 16 LH", "val name = \"ASL\""),
        TypographyToken("CodeGutter", Typography.CodeGutter, "10 / Regular · 16 LH", "12"),
    )

    Column(
        modifier = Modifier
            .background(Colors.Bg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BasicText(
            text = "Typography",
            style = Typography.Display.copy(color = Colors.Text),
        )
        tokens.forEach { token ->
            TypographyPreviewRow(token)
        }
    }
}

@Composable
private fun TypographyPreviewRow(token: TypographyToken) {
    Column(modifier = Modifier.fillMaxWidth()) {
        BasicText(
            text = token.name,
            style = Typography.Label.copy(color = Colors.Muted),
        )
        Spacer(modifier = Modifier.height(2.dp))
        BasicText(
            text = token.sample,
            style = token.style.copy(color = Colors.Text),
        )
        Spacer(modifier = Modifier.height(2.dp))
        BasicText(
            text = token.meta,
            style = Typography.Caption.copy(color = Colors.Muted2),
        )
    }
}
