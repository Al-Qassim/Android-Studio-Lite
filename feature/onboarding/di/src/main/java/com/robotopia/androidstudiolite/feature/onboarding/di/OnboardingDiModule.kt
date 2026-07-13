package com.robotopia.androidstudiolite.feature.onboarding.di

import com.robotopia.androidstudiolite.feature.onboarding.api.OnboardingScreens
import com.robotopia.androidstudiolite.feature.onboarding.api.OnboardingStore
import com.robotopia.androidstudiolite.feature.onboarding.presentation.DefaultOnboardingScreens
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val onboardingDiModule = module {
    single { OnboardingStore(context = androidContext()) }
    single<OnboardingScreens> {
        DefaultOnboardingScreens(
            authSession = get(),
            authScreens = get(),
            onboardingStore = get(),
        )
    }
}
