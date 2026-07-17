package com.robotopia.androidstudiolite.feature.auth.presentation.connect

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.feature.auth.api.AuthService
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.logic.collectConnectProgress
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.logic.copyUserCode
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.logic.openVerificationUri
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ui.ConnectConnectedBody
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ui.ConnectFailedBody
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ui.ConnectLoadingBody
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ui.ConnectShowCodeBody
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@Composable
internal fun ConnectAccountScreen(
    authService: AuthService,
    onFinished: () -> Unit,
    onCancel: () -> Unit,
    viewModel: ConnectAccountViewModel = koinViewModel(
        key = rememberSaveable { UUID.randomUUID().toString() },
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val connectAttempt by viewModel.connectAttempt.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    BackHandler(onBack = onCancel)

    LaunchedEffect(connectAttempt) {
        collectConnectProgress(
            authService = authService,
            uiState = viewModel.uiState,
        )
    }

    ConnectAccountScreen(
        state = state,
        providerDisplayName = authService.providerDisplayName,
        onBackClick = onCancel,
        onOpenVerificationUri = { uri ->
            openVerificationUri(
                uri = uri,
                openUri = uriHandler::openUri,
            )
        },
        onCopyCode = { code ->
            scope.launch {
                copyUserCode(code = code, clipboard = clipboard)
            }
        },
        onCancel = onCancel,
        onContinue = onFinished,
        onTryAgain = viewModel::retryConnect,
    )
}

@Composable
internal fun ConnectAccountScreen(
    state: ConnectUiState,
    providerDisplayName: String,
    onBackClick: () -> Unit,
    onOpenVerificationUri: (uri: String) -> Unit,
    onCopyCode: (code: String) -> Unit,
    onCancel: () -> Unit,
    onContinue: () -> Unit,
    onTryAgain: () -> Unit,
) {
    IslandScaffold(
        topBar = {
            TopBarBackTitle(
                title = "Connect $providerDisplayName",
                onBackClick = onBackClick,
            )
        },
        footer = when (state) {
            is ConnectUiState.Connected -> {
                {
                    ConnectFooterEnd {
                        Button(
                            label = "Continue",
                            onClick = onContinue,
                            variant = ButtonVariant.Primary,
                        )
                    }
                }
            }
            is ConnectUiState.Failed -> {
                {
                    ConnectFooterEnd {
                        Button(
                            label = "Cancel",
                            onClick = onCancel,
                            variant = ButtonVariant.Secondary,
                        )
                        Button(
                            label = "Try again",
                            onClick = onTryAgain,
                            variant = ButtonVariant.Primary,
                        )
                    }
                }
            }
            else -> null
        },
    ) {
        when (state) {
            ConnectUiState.Loading -> ConnectLoadingBody()

            is ConnectUiState.ShowCode -> ConnectShowCodeBody(
                state = state,
                onOpenVerificationUri = onOpenVerificationUri,
                onCopyCode = onCopyCode,
            )

            is ConnectUiState.Connected -> ConnectConnectedBody(state = state)

            is ConnectUiState.Failed -> ConnectFailedBody(state = state)
        }
    }
}

@Composable
private fun ConnectFooterEnd(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
    ) {
        content()
    }
}
