package com.robotopia.androidstudiolite.feature.projects.di

import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.data.StubProjectService
import com.robotopia.androidstudiolite.feature.projects.presentation.StubProjectsScreens
import org.koin.dsl.module

val projectsDiModule = module {
    single<ProjectService> { StubProjectService(get()) }
    single<ProjectsScreens> { StubProjectsScreens() }
}
