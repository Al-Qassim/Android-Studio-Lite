package com.robotopia.androidstudiolite.feature.auth.presentation.account

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.robotopia.androidstudiolite.feature.auth.api.AuthService
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ConnectAccountScreen
import kotlinx.coroutines.launch

private sealed interface BuildAccountRoute {
    data object Settings : BuildAccountRoute
    data object Connect : BuildAccountRoute
}

@Composable
internal fun BuildAccountScreen(
    authService: AuthService,
    onDismiss: () -> Unit,
) {
    var route by remember { mutableStateOf<BuildAccountRoute>(BuildAccountRoute.Settings) }
    val account by authService.observeAccount().collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    when (route) {
        BuildAccountRoute.Settings -> {
            BackHandler(onBack = onDismiss)
            BuildAccountContent(
                account = account,
                onBackClick = onDismiss,
                onConnectClick = { route = BuildAccountRoute.Connect },
                onLogOutClick = {
                    scope.launch { authService.clearAccount() }
                },
            )
        }

        BuildAccountRoute.Connect -> {
            ConnectAccountScreen(
                authService = authService,
                onFinished = { route = BuildAccountRoute.Settings },
                onCancel = { route = BuildAccountRoute.Settings },
            )
        }
    }
}
