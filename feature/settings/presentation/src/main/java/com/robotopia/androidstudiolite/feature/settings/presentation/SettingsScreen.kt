package com.robotopia.androidstudiolite.feature.settings.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.designsystem.animation.navFade
import com.robotopia.androidstudiolite.feature.auth.api.AuthScreens
import com.robotopia.androidstudiolite.feature.auth.api.AuthService
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildScreens
import com.robotopia.androidstudiolite.feature.settings.api.ThemePreferences
import com.robotopia.androidstudiolite.feature.settings.presentation.account.BuildAccountContent
import com.robotopia.androidstudiolite.feature.settings.presentation.home.SettingsHomeContent
import com.robotopia.androidstudiolite.feature.settings.presentation.theme.ThemeSettingsContent
import kotlinx.coroutines.launch

private enum class SettingsRoute {
    Home,
    Theme,
    BuildAccount,
    Connect,
    BuildHistory,
}

@Composable
internal fun SettingsScreen(
    authService: AuthService,
    authScreens: AuthScreens,
    buildScreens: BuildScreens,
    themePreferences: ThemePreferences,
    onDismiss: () -> Unit,
) {
    var route by rememberSaveable { mutableStateOf(SettingsRoute.Home) }
    val account by authService.observeAccount().collectAsState(initial = null)
    val theme by themePreferences.theme.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    AnimatedContent(
        targetState = route,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = { navFade() },
        label = "settingsNav",
    ) { current ->
        when (current) {
            SettingsRoute.Home -> {
                BackHandler(onBack = onDismiss)
                SettingsHomeContent(
                    buildAccountSubtitle = when {
                        account == null -> "Not connected"
                        else -> "${account!!.providerName} · ${account!!.identity}"
                    },
                    themeSubtitle = theme.displayName,
                    onBackClick = onDismiss,
                    onThemeClick = { route = SettingsRoute.Theme },
                    onBuildAccountClick = { route = SettingsRoute.BuildAccount },
                    onBuildHistoryClick = { route = SettingsRoute.BuildHistory },
                )
            }

            SettingsRoute.Theme -> {
                BackHandler(onBack = { route = SettingsRoute.Home })
                ThemeSettingsContent(
                    selected = theme,
                    onBackClick = { route = SettingsRoute.Home },
                    onThemeClick = { themePreferences.setTheme(it) },
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

            SettingsRoute.BuildHistory -> {
                buildScreens.History(
                    projectIdFilter = null,
                    onDismiss = { route = SettingsRoute.Home },
                )
            }
        }
    }
}
