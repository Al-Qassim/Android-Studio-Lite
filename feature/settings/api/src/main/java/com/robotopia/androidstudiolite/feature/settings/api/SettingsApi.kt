package com.robotopia.androidstudiolite.feature.settings.api

import androidx.compose.runtime.Composable

interface SettingsScreens {
    /**
     * Settings hub. Owns in-feature navigation to sections (Build account today)
     * and into auth Connect from Build account.
     */
    @Composable
    fun Settings(
        onDismiss: () -> Unit,
    )
}
