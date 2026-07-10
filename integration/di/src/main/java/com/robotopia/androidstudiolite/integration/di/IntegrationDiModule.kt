package com.robotopia.androidstudiolite.integration.di

import com.robotopia.androidstudiolite.feature.buildapk.di.buildApkDiModule
import com.robotopia.androidstudiolite.feature.editor.di.editorDiModule
import com.robotopia.androidstudiolite.feature.files.di.filesDiModule
import com.robotopia.androidstudiolite.feature.projects.di.projectsDiModule
import com.robotopia.androidstudiolite.integration.database.databaseDiModule
import org.koin.dsl.module

/** Aggregates feature + database Koin modules. `:app` starts Koin with this only. */
val integrationDiModule = module {
    includes(
        databaseDiModule,
        projectsDiModule,
        filesDiModule,
        editorDiModule,
        buildApkDiModule,
    )
}
