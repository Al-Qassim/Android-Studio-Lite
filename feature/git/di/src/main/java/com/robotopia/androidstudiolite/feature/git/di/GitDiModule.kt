package com.robotopia.androidstudiolite.feature.git.di

import com.robotopia.androidstudiolite.feature.git.api.GitScreens
import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.git.data.JGitGitServiceAdapter
import com.robotopia.androidstudiolite.feature.git.presentation.DefaultGitScreens
import com.robotopia.androidstudiolite.feature.git.presentation.clone.CloneProjectViewModel
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val gitDiModule = module {
    single<GitService> { JGitGitServiceAdapter() }
    single<GitScreens> {
        DefaultGitScreens(
            gitService = get(),
            gitHubClient = get(),
            projectService = get(),
            authSession = get(),
            authScreens = get(),
        )
    }
    viewModel { CloneProjectViewModel() }
    viewModel { ProjectGitViewModel() }
}
