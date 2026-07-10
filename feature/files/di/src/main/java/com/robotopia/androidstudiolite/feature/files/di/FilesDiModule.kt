package com.robotopia.androidstudiolite.feature.files.di

import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.api.FilesScreens
import com.robotopia.androidstudiolite.feature.files.data.StubFileExplorerService
import com.robotopia.androidstudiolite.feature.files.presentation.StubFilesScreens
import org.koin.dsl.module

val filesDiModule = module {
    single<FileExplorerService> { StubFileExplorerService() }
    single<FilesScreens> { StubFilesScreens() }
}
