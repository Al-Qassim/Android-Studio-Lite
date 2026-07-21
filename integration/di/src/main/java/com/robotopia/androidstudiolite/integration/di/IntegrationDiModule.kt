package com.robotopia.androidstudiolite.integration.di

import com.robotopia.androidstudiolite.feature.auth.di.authDiModule
import com.robotopia.androidstudiolite.feature.buildapk.di.buildApkDiModule
import com.robotopia.androidstudiolite.feature.editor.di.editorDiModule
import com.robotopia.androidstudiolite.feature.files.di.filesDiModule
import com.robotopia.androidstudiolite.feature.git.di.gitDiModule
import com.robotopia.androidstudiolite.feature.github.di.gitHubDiModule
import com.robotopia.androidstudiolite.feature.onboarding.di.onboardingDiModule
import com.robotopia.androidstudiolite.feature.projects.di.projectsDiModule
import com.robotopia.androidstudiolite.feature.settings.di.settingsDiModule
import com.robotopia.androidstudiolite.integration.database.databaseDiModule
import org.koin.dsl.module

/** Aggregates feature + database Koin modules. `:app` starts Koin with this only. */
val integrationDiModule = module {
    includes(
        databaseDiModule,
        gitHubDiModule,
        authDiModule,
        projectsDiModule,
        gitDiModule,
        buildApkDiModule,
        filesDiModule,
        editorDiModule,
        settingsDiModule,
        onboardingDiModule,
    )
}
