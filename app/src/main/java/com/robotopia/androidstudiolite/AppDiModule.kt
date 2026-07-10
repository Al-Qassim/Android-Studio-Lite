package com.robotopia.androidstudiolite

import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstaller
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appDiModule = module {
    single<ApkInstaller> { DefaultApkInstaller(context = androidContext()) }
}
