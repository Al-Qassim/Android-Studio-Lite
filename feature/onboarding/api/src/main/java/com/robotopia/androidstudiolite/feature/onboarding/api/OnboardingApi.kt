package com.robotopia.androidstudiolite.feature.onboarding.api

import androidx.compose.runtime.Composable

interface OnboardingScreens {
    /**
     * First-launch Connect prompt. Owns intro → shared auth Connect; calls [onFinished]
     * after Connect success, Cancel, or Skip.
     */
    @Composable
    fun Onboarding(
        onFinished: () -> Unit,
    )
}
