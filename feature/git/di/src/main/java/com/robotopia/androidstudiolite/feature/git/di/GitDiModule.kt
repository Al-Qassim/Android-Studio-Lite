package com.robotopia.androidstudiolite.feature.git.di

import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.git.data.JGitGitServiceAdapter
import org.koin.dsl.module

val gitDiModule = module {
    single<GitService> { JGitGitServiceAdapter() }
}
