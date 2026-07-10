package com.robotopia.androidstudiolite.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.designsystem.color.Colors

private val AslDarkColorScheme = darkColorScheme(
    primary = Colors.Primary,
    onPrimary = Colors.OnPrimary,
    secondary = Colors.Muted,
    onSecondary = Colors.Text,
    background = Colors.Bg,
    onBackground = Colors.Text,
    surface = Colors.Surface,
    onSurface = Colors.Text,
    error = Colors.Danger,
    onError = Colors.Text,
)

@Composable
fun AndroidStudioLiteTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // ASL is a dark IDE shell; ignore system/dynamic light schemes for v0.1.
    val colorScheme = AslDarkColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
