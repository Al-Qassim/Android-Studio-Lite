package com.robotopia.androidstudiolite.feature.settings.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.feature.settings.presentation.home.SettingsHomeContent

internal data class SettingsHomePreviewCase(
    private val label: String,
    val buildAccountSubtitle: String,
) {
    override fun toString(): String = label
}

internal class SettingsHomePreviewProvider : PreviewParameterProvider<SettingsHomePreviewCase> {
    override fun getDisplayName(index: Int): String = values.toList()[index].toString()

    override val values = sequenceOf(
        SettingsHomePreviewCase("logged out", "Not connected"),
        SettingsHomePreviewCase("connected", "GitHub · @alex-dev"),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun SettingsHomePreview(
    @PreviewParameter(SettingsHomePreviewProvider::class) preview: SettingsHomePreviewCase,
) {
    SettingsHomeContent(
        buildAccountSubtitle = preview.buildAccountSubtitle,
        onBackClick = {},
        onBuildAccountClick = {},
    )
}
