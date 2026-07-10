package com.robotopia.androidstudiolite.feature.buildapk.di

import com.robotopia.androidstudiolite.feature.buildapk.api.BuildScreens
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.data.FakeBuildService
import com.robotopia.androidstudiolite.feature.buildapk.presentation.DefaultBuildScreens
import com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.BuildProgressViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val buildApkDiModule = module {
    single<BuildService> { FakeBuildService(context = androidContext()) }
    single<BuildScreens> { DefaultBuildScreens(buildService = get()) }
    viewModel { BuildProgressViewModel() }
}
