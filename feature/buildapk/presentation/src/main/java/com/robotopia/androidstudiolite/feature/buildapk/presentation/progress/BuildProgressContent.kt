package com.robotopia.androidstudiolite.feature.buildapk.presentation.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.PhaseList
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.presentation.buildPhaseItems
import com.robotopia.androidstudiolite.feature.buildapk.presentation.buildPhaseLabel

@Composable
internal fun BuildProgressContent(
    state: BuildProgressUiState,
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    onInstall: () -> Unit,
    onRetry: (() -> Unit)?,
    onViewLog: ((logUrl: String) -> Unit)? = null,
) {
    val isFailed = state.error != null || state.phase == BuildPhase.Failed
    val isCancelled = state.phase == BuildPhase.Cancelled
    val isReady = state.phase == BuildPhase.ReadyToInstall
    val canInstall = !state.apkLocalPath.isNullOrBlank() && !state.isInstalling

    IslandScaffold(
        topBar = {
            TopBarBackTitle(
                title = "Build",
                onBackClick = onDismiss,
            )
        },
        footer = when {
            isCancelled -> null
            isFailed -> {
                {
                    BuildProgressFooter {
                        Button(
                            label = "Close",
                            onClick = onDismiss,
                            variant = ButtonVariant.Secondary,
                        )
                        if (onRetry != null) {
                            Button(
                                label = "Retry",
                                onClick = onRetry,
                                variant = ButtonVariant.Primary,
                            )
                        }
                    }
                }
            }
            isReady -> {
                {
                    BuildProgressFooter {
                        Button(
                            label = if (state.isInstalling) "Opening installer…" else "Install app",
                            onClick = onInstall,
                            variant = ButtonVariant.Primary,
                            enabled = canInstall,
                        )
                    }
                }
            }
            else -> {
                {
                    BuildProgressFooter {
                        Button(
                            label = "Cancel",
                            onClick = onCancel,
                            variant = ButtonVariant.Secondary,
                            enabled = !state.isInstalling,
                        )
                    }
                }
            }
        },
    ) {
        BuildProgressBody(
            state = state,
            onViewLog = onViewLog,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun BuildProgressFooter(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
    ) {
        content()
    }
}

@Composable
private fun BuildProgressBody(
    state: BuildProgressUiState,
    onViewLog: ((logUrl: String) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        when {
            state.phase == BuildPhase.Cancelled -> BuildCancelledState(
                message = state.message,
            )
            state.error != null || state.phase == BuildPhase.Failed -> BuildFailedState(
                state = state,
                onViewLog = onViewLog,
            )
            else -> BuildActiveState(state = state)
        }
    }
}

@Composable
private fun BuildActiveState(state: BuildProgressUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BuildProgressHeader(
            title = buildPhaseLabel(state.phase),
            titleColor = Theme.colors.Text,
            message = state.message,
            providerName = state.providerName,
        )
        PhaseList(
            phases = buildPhaseItems(currentPhase = state.phase),
        )
        if (state.phase == BuildPhase.ReadyToInstall && state.isInstalling) {
            BasicText(
                text = "Opening the package installer…",
                style = Typography.Body.copy(color = Theme.colors.Muted),
            )
        }
        if (state.phase == BuildPhase.ReadyToInstall && !state.installError.isNullOrBlank()) {
            BasicText(
                text = state.installError,
                style = Typography.Body.copy(color = Theme.colors.Danger),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun BuildFailedState(
    state: BuildProgressUiState,
    onViewLog: ((logUrl: String) -> Unit)?,
) {
    val logUrl = state.logUrl
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BuildProgressHeader(
            title = "Build failed",
            titleColor = Theme.colors.Danger,
            message = state.error ?: state.message
                ?: "Build failed. Open the build log.",
            providerName = state.providerName,
        )
        PhaseList(
            phases = buildPhaseItems(
                currentPhase = state.phase,
                failedAtPhase = state.failedAtPhase ?: BuildPhase.Building,
            ),
        )
        if (!logUrl.isNullOrBlank() && onViewLog != null) {
            Button(
                label = "View build log",
                onClick = { onViewLog(logUrl) },
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Secondary,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun BuildCancelledState(message: String?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = "Build cancelled",
            style = Typography.Headline.copy(color = Theme.colors.Text),
        )
        BasicText(
            text = message?.takeIf { it.isNotBlank() }
                ?: "No APK was produced. You can start a new build when you're ready.",
            style = Typography.Body.copy(color = Theme.colors.Muted),
        )
    }
}

@Composable
private fun BuildProgressHeader(
    title: String,
    titleColor: Color,
    message: String?,
    providerName: String?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        BasicText(
            text = title,
            style = Typography.Headline.copy(color = titleColor),
        )
        if (!message.isNullOrBlank()) {
            BasicText(
                text = message,
                style = Typography.Body.copy(color = Theme.colors.Muted),
            )
        }
        if (!providerName.isNullOrBlank()) {
            BasicText(
                text = "via $providerName",
                style = Typography.Caption.copy(color = Theme.colors.Muted),
            )
        }
    }
}
