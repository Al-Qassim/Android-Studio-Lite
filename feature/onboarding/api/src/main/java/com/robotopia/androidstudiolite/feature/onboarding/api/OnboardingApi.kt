package com.robotopia.androidstudiolite.feature.onboarding.api

import androidx.compose.runtime.Composable

interface OnboardingScreens {
    /**
     * First-launch flow: welcome → connect prompt → shared auth Connect.
     * Calls [onFinished] after Connect success, Skip, or system Back from welcome.
     */
    @Composable
    fun Onboarding(
        onFinished: () -> Unit,
    )
}
