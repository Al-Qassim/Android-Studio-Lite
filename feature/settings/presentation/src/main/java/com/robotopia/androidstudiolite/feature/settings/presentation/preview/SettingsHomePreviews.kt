package com.robotopia.androidstudiolite.feature.settings.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.feature.settings.presentation.home.SettingsHomeContent

internal data class SettingsHomePreviewCase(
    private val label: String,
    val buildAccountSubtitle: String,
    val themeSubtitle: String,
) {
    override fun toString(): String = label
}

internal class SettingsHomePreviewProvider : PreviewParameterProvider<SettingsHomePreviewCase> {
    override fun getDisplayName(index: Int): String = values.toList()[index].toString()

    override val values = sequenceOf(
        SettingsHomePreviewCase(
            label = "logged out",
            buildAccountSubtitle = "Not connected",
            themeSubtitle = "Dark",
        ),
        SettingsHomePreviewCase(
            label = "connected · light",
            buildAccountSubtitle = "GitHub · @alex-dev",
            themeSubtitle = "Light",
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun SettingsHomePreview(
    @PreviewParameter(SettingsHomePreviewProvider::class) preview: SettingsHomePreviewCase,
) {
    SettingsHomeContent(
        buildAccountSubtitle = preview.buildAccountSubtitle,
        themeSubtitle = preview.themeSubtitle,
        onBackClick = {},
        onThemeClick = {},
        onBuildAccountClick = {},
        onBuildHistoryClick = {},
        onAboutClick = {},
    )
}
