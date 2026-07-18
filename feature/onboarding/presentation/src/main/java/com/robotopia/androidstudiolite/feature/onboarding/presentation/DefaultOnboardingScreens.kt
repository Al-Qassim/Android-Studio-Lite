package com.robotopia.androidstudiolite.feature.onboarding.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.robotopia.androidstudiolite.designsystem.animation.navFade
import com.robotopia.androidstudiolite.feature.auth.api.AuthScreens
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.onboarding.api.OnboardingScreens
import com.robotopia.androidstudiolite.feature.onboarding.api.OnboardingStore
import com.robotopia.androidstudiolite.feature.onboarding.presentation.intro.OnboardingIntroContent
import com.robotopia.androidstudiolite.feature.onboarding.presentation.welcome.OnboardingWelcomeContent

private enum class OnboardingRoute {
    Welcome,
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
        var route by rememberSaveable { mutableStateOf(OnboardingRoute.Welcome) }

        AnimatedContent(
            targetState = route,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = { navFade() },
            label = "onboardingNav",
        ) { current ->
            when (current) {
                OnboardingRoute.Welcome -> {
                    BackHandler(onBack = { finishOnboarding(onboardingStore, onFinished) })
                    OnboardingWelcomeContent(
                        onContinueClick = { route = OnboardingRoute.Intro },
                    )
                }

                OnboardingRoute.Intro -> {
                    BackHandler(onBack = { route = OnboardingRoute.Welcome })
                    OnboardingIntroContent(
                        providerDisplayName = authSession.providerDisplayName,
                        onConnectClick = { route = OnboardingRoute.Connect },
                        onSkipClick = { finishOnboarding(onboardingStore, onFinished) },
                    )
                }

                OnboardingRoute.Connect -> {
                    authScreens.ConnectAccount(
                        onFinished = { finishOnboarding(onboardingStore, onFinished) },
                        onCancel = { route = OnboardingRoute.Intro },
                    )
                }
            }
        }
    }
}

private fun finishOnboarding(
    onboardingStore: OnboardingStore,
    onFinished: () -> Unit,
) {
    onboardingStore.markCompleted()
    onFinished()
}
