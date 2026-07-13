package com.robotopia.androidstudiolite.feature.github.di

import com.robotopia.androidstudiolite.feature.github.api.GitHubClient
import com.robotopia.androidstudiolite.feature.github.data.HttpGitHubClient
import org.koin.dsl.module

val gitHubDiModule = module {
    single<GitHubClient> { HttpGitHubClient() }
}
