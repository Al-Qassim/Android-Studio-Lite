package com.robotopia.androidstudiolite.feature.projects.di

import com.robotopia.androidstudiolite.feature.projects.api.ProjectEventHooks
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.data.DefaultProjectEventHooks
import com.robotopia.androidstudiolite.feature.projects.data.DefaultProjectService
import com.robotopia.androidstudiolite.feature.projects.presentation.DefaultProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.presentation.create.CreateProjectViewModel
import com.robotopia.androidstudiolite.feature.projects.presentation.list.ProjectsListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val projectsDiModule = module {
    single { DefaultProjectEventHooks() }
    single<ProjectEventHooks> { get<DefaultProjectEventHooks>() }
    single<ProjectService> {
        DefaultProjectService(
            context = androidContext(),
            projectDao = get(),
            projectEventHooks = get(),
        )
    }
    single<ProjectsScreens> {
        DefaultProjectsScreens(
            projectService = get(),
            buildScreens = get(),
        )
    }
    viewModel { ProjectsListViewModel() }
    viewModel { CreateProjectViewModel() }
}
