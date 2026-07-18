package com.robotopia.androidstudiolite.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.robotopia.androidstudiolite.designsystem.color.ColorScheme
import com.robotopia.androidstudiolite.designsystem.color.DarkColorScheme
import com.robotopia.androidstudiolite.designsystem.color.DraculaColorScheme
import com.robotopia.androidstudiolite.designsystem.color.LightColorScheme
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.feature.settings.api.AppTheme

fun AppTheme.toColorScheme(): ColorScheme = when (this) {
    AppTheme.Dark -> DarkColorScheme
    AppTheme.Light -> LightColorScheme
    AppTheme.Dracula -> DraculaColorScheme
}

val AppTheme.usesLightSystemBars: Boolean
    get() = this == AppTheme.Light

@Composable
fun AndroidStudioLiteTheme(
    appTheme: AppTheme = AppTheme.Dark,
    content: @Composable () -> Unit,
) {
    val colors = remember(appTheme) { appTheme.toColorScheme() }
    val materialScheme = remember(colors, appTheme) {
        if (appTheme == AppTheme.Light) {
            lightColorScheme(
                primary = colors.Primary,
                onPrimary = colors.OnPrimary,
                secondary = colors.Muted,
                onSecondary = colors.Text,
                background = colors.Canvas,
                onBackground = colors.Text,
                surface = colors.Surface,
                onSurface = colors.Text,
                error = colors.Danger,
                onError = colors.OnPrimary,
            )
        } else {
            darkColorScheme(
                primary = colors.Primary,
                onPrimary = colors.OnPrimary,
                secondary = colors.Muted,
                onSecondary = colors.Text,
                background = colors.Canvas,
                onBackground = colors.Text,
                surface = colors.Surface,
                onSurface = colors.Text,
                error = colors.Danger,
                onError = colors.Text,
            )
        }
    }
    MaterialTheme(
        colorScheme = materialScheme,
        typography = Typography,
    ) {
        Theme(colors = colors, content = content)
    }
}
