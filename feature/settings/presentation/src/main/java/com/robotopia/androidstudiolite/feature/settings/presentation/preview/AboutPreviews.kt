package com.robotopia.androidstudiolite.feature.settings.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.feature.settings.presentation.about.AboutContent

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun AboutPreview() {
    AboutContent(
        versionLabel = "Version 1.0.1",
        onBackClick = {},
    )
}
