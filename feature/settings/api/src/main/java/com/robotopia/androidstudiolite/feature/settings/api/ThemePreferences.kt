package com.robotopia.androidstudiolite.feature.settings.api

import kotlinx.coroutines.flow.StateFlow

/** Built-in IDE appearance options. */
enum class AppTheme {
    Dark,
    Light,
    Dracula,
    ;

    val displayName: String
        get() = when (this) {
            Dark -> "Dark"
            Light -> "Light"
            Dracula -> "Dracula"
        }

    val subtitle: String
        get() = when (this) {
            Dark -> "Islands Dark"
            Light -> "Islands Light"
            Dracula -> "Dracula"
        }
}

interface ThemePreferences {
    val theme: StateFlow<AppTheme>
    fun setTheme(theme: AppTheme)
}
