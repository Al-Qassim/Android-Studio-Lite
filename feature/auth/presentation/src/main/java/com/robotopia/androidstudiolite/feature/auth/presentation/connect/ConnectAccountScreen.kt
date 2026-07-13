package com.robotopia.androidstudiolite.feature.auth.presentation.connect

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.feature.auth.api.AuthService
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.logic.collectConnectProgress
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.logic.copyUserCode
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.logic.openVerificationUri
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ui.ConnectConnectedBody
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ui.ConnectFailedBody
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ui.ConnectLoadingBody
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ui.ConnectShowCodeBody
import com.robotopia.androidstudiolite.feature.auth.presentation.connect.ui.ConnectWaitingBody
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
        onBackClick = onCancel,
        onOpenVerificationUri = { uri ->
            openVerificationUri(
                uri = uri,
                uiState = viewModel.uiState,
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
    onBackClick: () -> Unit,
    onOpenVerificationUri: (uri: String) -> Unit,
    onCopyCode: (code: String) -> Unit,
    onCancel: () -> Unit,
    onContinue: () -> Unit,
    onTryAgain: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        TopBarBackTitle(
            title = "Connect",
            onBackClick = onBackClick,
        )
        when (state) {
            ConnectUiState.Loading -> ConnectLoadingBody()

            is ConnectUiState.ShowCode -> ConnectShowCodeBody(
                state = state,
                onOpenVerificationUri = onOpenVerificationUri,
                onCopyCode = onCopyCode,
            )

            is ConnectUiState.Waiting -> ConnectWaitingBody(
                state = state,
                onCancel = onCancel,
            )

            is ConnectUiState.Connected -> ConnectConnectedBody(
                state = state,
                onContinue = onContinue,
            )

            is ConnectUiState.Failed -> ConnectFailedBody(
                state = state,
                onCancel = onCancel,
                onTryAgain = onTryAgain,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun ConnectAccountScreenPreview(
    @PreviewParameter(ConnectAccountPreviewProvider::class) preview: ConnectAccountPreviewCase,
) {
    ConnectAccountScreen(
        state = preview.state,
        onBackClick = {},
        onOpenVerificationUri = {},
        onCopyCode = {},
        onCancel = {},
        onContinue = {},
        onTryAgain = {},
    )
}
