package com.robotopia.androidstudiolite.feature.auth.di

import com.robotopia.androidstudiolite.feature.auth.api.AuthScreens
import com.robotopia.androidstudiolite.feature.auth.api.AuthService
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.auth.data.BuildConfig
import com.robotopia.androidstudiolite.feature.auth.data.DefaultAuthService
import com.robotopia.androidstudiolite.feature.auth.data.PrefsAuthSessionStore
import com.robotopia.androidstudiolite.feature.auth.presentation.DefaultAuthScreens
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ConnectAccountViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authDiModule = module {
    single { PrefsAuthSessionStore(context = androidContext()) }
    single<AuthService> {
        DefaultAuthService(
            store = get(),
            gitHubClient = get(),
            clientId = BuildConfig.GITHUB_OAUTH_CLIENT_ID,
        )
    }
    single<AuthSession> { get<AuthService>() }
    single<AuthScreens> { DefaultAuthScreens(authService = get()) }
    viewModel { ConnectAccountViewModel() }
}
