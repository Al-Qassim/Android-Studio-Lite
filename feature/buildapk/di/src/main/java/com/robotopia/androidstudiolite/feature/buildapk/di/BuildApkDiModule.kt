package com.robotopia.androidstudiolite.feature.buildapk.di

import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstaller
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryEventHooks
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryStore
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildScreens
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.data.service.DefaultApkInstaller
import com.robotopia.androidstudiolite.feature.buildapk.data.service.DefaultBuildHistoryEventHooks
import com.robotopia.androidstudiolite.feature.buildapk.data.service.DefaultBuildHistoryStore
import com.robotopia.androidstudiolite.feature.buildapk.data.service.DefaultBuildService
import com.robotopia.androidstudiolite.feature.buildapk.presentation.DefaultBuildScreens
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryDetailViewModel
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryViewModel
import com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.BuildProgressViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val buildApkDiModule = module {
    single { DefaultBuildHistoryEventHooks() }
    single<BuildHistoryEventHooks> { get<DefaultBuildHistoryEventHooks>() }
    single<BuildHistoryStore> {
        DefaultBuildHistoryStore(
            buildJobDao = get(),
            historyEventHooks = get(),
        )
    }
    single<BuildService>(createdAtStart = true) {
        DefaultBuildService(
            context = androidContext(),
            authSession = get(),
            gitHubClient = get(),
            buildJobDao = get(),
            historyEventHooks = get(),
            projectEventHooks = get(),
        )
    }
    single<ApkInstaller> { DefaultApkInstaller(context = androidContext()) }
    single<BuildScreens> {
        DefaultBuildScreens(
            buildService = get(),
            buildHistoryStore = get(),
            apkInstaller = get(),
            authSession = get(),
            authScreens = get(),
        )
    }
    viewModel { BuildProgressViewModel() }
    viewModel { BuildHistoryViewModel() }
    viewModel { BuildHistoryDetailViewModel() }
}
