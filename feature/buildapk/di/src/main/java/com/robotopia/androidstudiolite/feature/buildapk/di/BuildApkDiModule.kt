package com.robotopia.androidstudiolite.feature.buildapk.di

import com.robotopia.androidstudiolite.feature.buildapk.api.BuildScreens
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.data.StubBuildService
import com.robotopia.androidstudiolite.feature.buildapk.presentation.StubBuildScreens
import org.koin.dsl.module

val buildApkDiModule = module {
    single<BuildService> { StubBuildService() }
    single<BuildScreens> { StubBuildScreens() }
}
