package com.robotopia.androidstudiolite.feature.settings.presentation

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.auth.api.AuthScreens
import com.robotopia.androidstudiolite.feature.auth.api.AuthService
import com.robotopia.androidstudiolite.feature.settings.api.SettingsScreens
import com.robotopia.androidstudiolite.feature.settings.presentation.account.BuildAccountScreen

class DefaultSettingsScreens(
    private val authService: AuthService,
    private val authScreens: AuthScreens,
) : SettingsScreens {

    @Composable
    override fun BuildAccount(
        onDismiss: () -> Unit,
    ) {
        BuildAccountScreen(
            authService = authService,
            authScreens = authScreens,
            onDismiss = onDismiss,
        )
    }
}
