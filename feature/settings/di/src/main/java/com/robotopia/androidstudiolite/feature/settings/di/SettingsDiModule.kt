package com.robotopia.androidstudiolite.feature.settings.di

import com.robotopia.androidstudiolite.feature.settings.api.SettingsScreens
import com.robotopia.androidstudiolite.feature.settings.presentation.DefaultSettingsScreens
import org.koin.dsl.module

val settingsDiModule = module {
    single<SettingsScreens> {
        DefaultSettingsScreens(
            authService = get(),
            authScreens = get(),
            buildScreens = get(),
        )
    }
}
