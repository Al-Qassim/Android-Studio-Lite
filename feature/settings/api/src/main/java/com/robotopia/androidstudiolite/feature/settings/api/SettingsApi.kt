package com.robotopia.androidstudiolite.feature.settings.api

import androidx.compose.runtime.Composable

interface SettingsScreens {
    /**
     * Settings · Build account — connect / log out for the cloud build provider.
     * Owns in-feature navigation into auth Connect when the user chooses Connect account.
     */
    @Composable
    fun BuildAccount(
        onDismiss: () -> Unit,
    )
}
