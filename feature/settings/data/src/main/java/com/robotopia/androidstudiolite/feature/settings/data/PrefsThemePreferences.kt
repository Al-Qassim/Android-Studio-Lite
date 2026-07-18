package com.robotopia.androidstudiolite.feature.settings.data

import android.content.Context
import android.content.SharedPreferences
import com.robotopia.androidstudiolite.feature.settings.api.AppTheme
import com.robotopia.androidstudiolite.feature.settings.api.ThemePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PrefsThemePreferences(
    context: Context,
) : ThemePreferences {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(readTheme())
    override val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    override fun setTheme(theme: AppTheme) {
        prefs.edit().putString(KEY_THEME, theme.name).apply()
        _theme.value = theme
    }

    private fun readTheme(): AppTheme {
        val raw = prefs.getString(KEY_THEME, DEFAULT_THEME.name) ?: DEFAULT_THEME.name
        return runCatching { AppTheme.valueOf(raw) }.getOrDefault(DEFAULT_THEME)
    }

    private companion object {
        const val PREFS_NAME = "theme_preferences"
        const val KEY_THEME = "theme"
        val DEFAULT_THEME = AppTheme.Dark
    }
}
