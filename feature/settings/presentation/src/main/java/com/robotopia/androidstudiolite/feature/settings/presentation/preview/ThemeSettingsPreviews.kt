package com.robotopia.androidstudiolite.feature.settings.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.color.DarkColorScheme
import com.robotopia.androidstudiolite.designsystem.color.DraculaColorScheme
import com.robotopia.androidstudiolite.designsystem.color.LightColorScheme
import com.robotopia.androidstudiolite.feature.settings.api.AppTheme
import com.robotopia.androidstudiolite.feature.settings.presentation.theme.ThemeSettingsContent

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640, name = "Theme · Dark selected")
@Composable
private fun ThemeSettingsDarkPreview() {
    Theme(colors = DarkColorScheme) {
        ThemeSettingsContent(
            selected = AppTheme.Dark,
            onBackClick = {},
            onThemeClick = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEBECF0, widthDp = 360, heightDp = 640, name = "Theme · Light selected")
@Composable
private fun ThemeSettingsLightPreview() {
    Theme(colors = LightColorScheme) {
        ThemeSettingsContent(
            selected = AppTheme.Light,
            onBackClick = {},
            onThemeClick = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF21222C, widthDp = 360, heightDp = 640, name = "Theme · Dracula selected")
@Composable
private fun ThemeSettingsDraculaPreview() {
    Theme(colors = DraculaColorScheme) {
        ThemeSettingsContent(
            selected = AppTheme.Dracula,
            onBackClick = {},
            onThemeClick = {},
        )
    }
}
