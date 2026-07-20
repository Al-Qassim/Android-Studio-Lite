package com.robotopia.androidstudiolite.feature.files.di

import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.api.FilesScreens
import com.robotopia.androidstudiolite.feature.files.data.DefaultFileExplorerService
import com.robotopia.androidstudiolite.feature.files.presentation.DefaultFilesScreens
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val filesDiModule = module {
    single<FileExplorerService> { DefaultFileExplorerService() }
    single<FilesScreens> {
        DefaultFilesScreens(
            fileExplorerService = get(),
            gitScreens = get(),
        )
    }
    viewModelOf(::FileBrowserViewModel)
}
