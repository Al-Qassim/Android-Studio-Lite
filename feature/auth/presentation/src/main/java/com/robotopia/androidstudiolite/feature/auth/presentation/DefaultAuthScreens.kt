package com.robotopia.androidstudiolite.feature.auth.presentation

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.auth.api.AuthScreens
import com.robotopia.androidstudiolite.feature.auth.api.AuthService
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ConnectAccountScreen

class DefaultAuthScreens(
    private val authService: AuthService,
) : AuthScreens {

    @Composable
    override fun ConnectAccount(
        onFinished: () -> Unit,
        onCancel: () -> Unit,
    ) {
        ConnectAccountScreen(
            authService = authService,
            onFinished = onFinished,
            onCancel = onCancel,
        )
    }
}
