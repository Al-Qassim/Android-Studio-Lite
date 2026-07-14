package com.robotopia.androidstudiolite.feature.onboarding.data

import android.content.Context
import com.robotopia.androidstudiolite.feature.onboarding.api.OnboardingStore

class PrefsOnboardingStore(
    context: Context,
) : OnboardingStore {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    override fun isCompleted(): Boolean = prefs.getBoolean(KEY_COMPLETED, false)

    override fun markCompleted() {
        prefs.edit().putBoolean(KEY_COMPLETED, true).apply()
    }

    private companion object {
        const val PREFS = "asl_onboarding"
        const val KEY_COMPLETED = "completed"
    }
}
