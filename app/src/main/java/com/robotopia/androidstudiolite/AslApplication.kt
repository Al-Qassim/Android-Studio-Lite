package com.robotopia.androidstudiolite

import android.app.Application
import com.robotopia.androidstudiolite.integration.di.integrationDiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AslApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AslApplication)
            modules(integrationDiModule)
        }
    }
}
