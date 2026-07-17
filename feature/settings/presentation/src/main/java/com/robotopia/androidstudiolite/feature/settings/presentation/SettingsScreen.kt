package com.robotopia.androidstudiolite.feature.settings.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.robotopia.androidstudiolite.feature.auth.api.AuthScreens
import com.robotopia.androidstudiolite.feature.auth.api.AuthService
import com.robotopia.androidstudiolite.feature.settings.presentation.account.BuildAccountContent
import com.robotopia.androidstudiolite.feature.settings.presentation.home.SettingsHomeContent
import kotlinx.coroutines.launch

private enum class SettingsRoute {
    Home,
    BuildAccount,
    Connect,
}

@Composable
internal fun SettingsScreen(
    authService: AuthService,
    authScreens: AuthScreens,
    onDismiss: () -> Unit,
) {
    var route by rememberSaveable { mutableStateOf(SettingsRoute.Home) }
    val account by authService.observeAccount().collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    when (route) {
        SettingsRoute.Home -> {
            BackHandler(onBack = onDismiss)
            SettingsHomeContent(
                buildAccountSubtitle = when {
                    account == null -> "Not connected"
                    else -> "${account!!.providerName} · ${account!!.identity}"
                },
                onBackClick = onDismiss,
                onBuildAccountClick = { route = SettingsRoute.BuildAccount },
            )
        }

        SettingsRoute.BuildAccount -> {
            BackHandler(onBack = { route = SettingsRoute.Home })
            BuildAccountContent(
                account = account,
                providerDisplayName = authService.providerDisplayName,
                onBackClick = { route = SettingsRoute.Home },
                onConnectClick = { route = SettingsRoute.Connect },
                onLogOutClick = {
                    scope.launch { authService.clearAccount() }
                },
            )
        }

        SettingsRoute.Connect -> {
            authScreens.ConnectAccount(
                onFinished = { route = SettingsRoute.BuildAccount },
                onCancel = { route = SettingsRoute.BuildAccount },
            )
        }
    }
}
