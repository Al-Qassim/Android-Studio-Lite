package com.robotopia.androidstudiolite.feature.onboarding.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.robotopia.androidstudiolite.feature.auth.api.AuthScreens
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.onboarding.api.OnboardingScreens
import com.robotopia.androidstudiolite.feature.onboarding.api.OnboardingStore
import com.robotopia.androidstudiolite.feature.onboarding.presentation.intro.OnboardingIntroContent

private enum class OnboardingRoute {
    Intro,
    Connect,
}

class DefaultOnboardingScreens(
    private val authSession: AuthSession,
    private val authScreens: AuthScreens,
    private val onboardingStore: OnboardingStore,
) : OnboardingScreens {

    @Composable
    override fun Onboarding(onFinished: () -> Unit) {
        var route by rememberSaveable { mutableStateOf(OnboardingRoute.Intro) }

        fun complete() {
            onboardingStore.markCompleted()
            onFinished()
        }

        when (route) {
            OnboardingRoute.Intro -> {
                BackHandler(onBack = ::complete)
                OnboardingIntroContent(
                    providerDisplayName = authSession.providerDisplayName,
                    onConnectClick = { route = OnboardingRoute.Connect },
                    onSkipClick = ::complete,
                )
            }

            OnboardingRoute.Connect -> {
                authScreens.ConnectAccount(
                    onFinished = ::complete,
                    onCancel = { route = OnboardingRoute.Intro },
                )
            }
        }
    }
}
