package com.robotopia.androidstudiolite.designsystem.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

val LocalColorScheme = staticCompositionLocalOf { DarkColorScheme }

/**
 * Access the active [ColorScheme] from composition.
 *
 * ```
 * val colors = Theme.colors
 * Text(color = colors.Text)
 * ```
 */
object Theme {
    val colors: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalColorScheme.current
}

/** Provides [colors] to [Theme.colors] under [content]. */
@Composable
fun Theme(
    colors: ColorScheme = DarkColorScheme,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalColorScheme provides colors, content = content)
}
