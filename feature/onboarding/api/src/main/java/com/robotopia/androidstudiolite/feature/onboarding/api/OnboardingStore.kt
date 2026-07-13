package com.robotopia.androidstudiolite.feature.onboarding.api

import android.content.Context

class OnboardingStore(
    context: Context,
) {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isCompleted(): Boolean = prefs.getBoolean(KEY_COMPLETED, false)

    fun markCompleted() {
        prefs.edit().putBoolean(KEY_COMPLETED, true).apply()
    }

    private companion object {
        const val PREFS = "asl_onboarding"
        const val KEY_COMPLETED = "completed"
    }
}
