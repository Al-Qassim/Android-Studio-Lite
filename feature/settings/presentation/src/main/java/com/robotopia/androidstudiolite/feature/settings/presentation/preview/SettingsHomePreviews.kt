package com.robotopia.androidstudiolite.feature.settings.presentation.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

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
