package com.robotopia.androidstudiolite.feature.projects.di

import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.data.DefaultProjectService
import com.robotopia.androidstudiolite.feature.projects.presentation.DefaultProjectsScreens
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val projectsDiModule = module {
    single<ProjectService> {
        DefaultProjectService(
            context = androidContext(),
            projectDao = get(),
        )
    }
    single<ProjectsScreens> { DefaultProjectsScreens(projectService = get()) }
}
