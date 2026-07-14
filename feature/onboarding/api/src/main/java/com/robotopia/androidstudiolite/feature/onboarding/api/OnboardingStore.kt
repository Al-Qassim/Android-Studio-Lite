package com.robotopia.androidstudiolite.feature.onboarding.api

interface OnboardingStore {
    fun isCompleted(): Boolean

    fun markCompleted()
}
