package com.robotopia.androidstudiolite.feature.settings.di

import com.robotopia.androidstudiolite.feature.settings.api.SettingsScreens
import com.robotopia.androidstudiolite.feature.settings.api.ThemePreferences
import com.robotopia.androidstudiolite.feature.settings.data.PrefsThemePreferences
import com.robotopia.androidstudiolite.feature.settings.presentation.DefaultSettingsScreens
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val settingsDiModule = module {
    single<ThemePreferences> {
        PrefsThemePreferences(context = androidContext())
    }
    single<SettingsScreens> {
        DefaultSettingsScreens(
            authService = get(),
            authScreens = get(),
            buildScreens = get(),
            themePreferences = get(),
        )
    }
}
