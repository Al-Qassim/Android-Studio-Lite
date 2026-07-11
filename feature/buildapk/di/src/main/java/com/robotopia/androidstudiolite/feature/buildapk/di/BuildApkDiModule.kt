package com.robotopia.androidstudiolite.feature.buildapk.di

import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstaller
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildScreens
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.api.GitHubTokenAuth
import com.robotopia.androidstudiolite.feature.buildapk.data.DefaultApkInstaller
import com.robotopia.androidstudiolite.feature.buildapk.data.GitHubBuildService
import com.robotopia.androidstudiolite.feature.buildapk.data.GitHubTokenStore
import com.robotopia.androidstudiolite.feature.buildapk.presentation.DefaultBuildScreens
import com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.BuildProgressViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val buildApkDiModule = module {
    single { GitHubTokenStore(context = androidContext()) }
    single<GitHubTokenAuth> { get<GitHubTokenStore>() }
    single<BuildService> {
        GitHubBuildService(
            context = androidContext(),
            tokenStore = get(),
        )
    }
    single<ApkInstaller> { DefaultApkInstaller(context = androidContext()) }
    single<BuildScreens> {
        DefaultBuildScreens(
            buildService = get(),
            tokenAuth = get(),
        )
    }
    viewModel { BuildProgressViewModel() }
}
