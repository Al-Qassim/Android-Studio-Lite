package com.robotopia.androidstudiolite.feature.buildapk.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.InfoCard
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.component.PhaseList
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.presentation.buildPhaseItems
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic.HISTORY_DETAIL_NOT_FOUND
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.ui.BuildHistoryPhaseIcon

@Composable
internal fun BuildHistoryDetailContent(
    state: BuildHistoryDetailUiState,
    onBackClick: () -> Unit,
    onInstall: () -> Unit,
    onViewLog: (logUrl: String) -> Unit,
    onRetryLoad: () -> Unit,
) {
    val loadError = state.loadError
    when {
        state.isLoading -> {
            HistoryDetailScaffold(onBackClick = onBackClick) {
                HistoryDetailLoading()
            }
        }

        loadError != null -> {
            HistoryDetailScaffold(onBackClick = onBackClick) {
                HistoryDetailLoadError(
                    message = loadError,
                    onRetryLoad = onRetryLoad,
                )
            }
        }

        else -> {
            HistoryDetailReadyBody(
                state = state,
                onBackClick = onBackClick,
                onInstall = onInstall,
                onViewLog = onViewLog,
            )
        }
    }
}

@Composable
private fun HistoryDetailScaffold(
    onBackClick: () -> Unit,
    body: @Composable ColumnScope.() -> Unit,
) {
    IslandScaffold(
        topBar = {
            TopBarBackTitle(
                title = "Build",
                onBackClick = onBackClick,
            )
        },
        body = body,
    )
}

@Composable
private fun HistoryDetailLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LoadingIndicator(label = "Loading build…")
    }
}

@Composable
private fun HistoryDetailLoadError(
    message: String,
    onRetryLoad: () -> Unit,
) {
    val title = if (message == HISTORY_DETAIL_NOT_FOUND) {
        "Build not found"
    } else {
        "Couldn't load build"
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EmptyState(
            title = title,
            hint = message,
        )
        if (message != HISTORY_DETAIL_NOT_FOUND) {
            Button(
                label = "Try again",
                onClick = onRetryLoad,
                variant = ButtonVariant.Secondary,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun HistoryDetailReadyBody(
    state: BuildHistoryDetailUiState,
    onBackClick: () -> Unit,
    onInstall: () -> Unit,
    onViewLog: (logUrl: String) -> Unit,
) {
    val showInstall = state.phase == BuildPhase.ReadyToInstall
    IslandScaffold(
        topBar = {
            TopBarBackTitle(
                title = "Build",
                onBackClick = onBackClick,
            )
        },
        footer = if (showInstall) {
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    Button(
                        label = if (state.isInstalling) "Opening installer…" else "Install app",
                        onClick = onInstall,
                        variant = ButtonVariant.Primary,
                        enabled = state.canInstall && !state.isInstalling,
                    )
                }
            }
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HistoryDetailStatusHeader(
                phase = state.phase,
                providerName = state.providerName,
            )
            InfoCard(title = state.projectName)
            PhaseList(phases = historyDetailPhaseItems(state))
            HistoryDetailLine(label = "Started", value = state.startedLabel)
            state.finishedLabel?.let { finished ->
                HistoryDetailLine(label = "Finished", value = finished)
            }
            state.message?.takeIf { it.isNotBlank() }?.let { message ->
                BasicText(
                    text = message,
                    style = Typography.Body.copy(color = Theme.colors.Muted),
                )
            }
            state.error?.takeIf { it.isNotBlank() }?.let { error ->
                BasicText(
                    text = error,
                    style = Typography.Body.copy(color = Theme.colors.Danger),
                )
            }
            if (showInstall && !state.canInstall && !state.isInstalling) {
                BasicText(
                    text = "APK is no longer available on this device.",
                    style = Typography.Body.copy(color = Theme.colors.Muted),
                )
            }
            state.installError?.takeIf { it.isNotBlank() }?.let { installError ->
                BasicText(
                    text = installError,
                    style = Typography.Body.copy(color = Theme.colors.Danger),
                )
            }
            val logUrl = state.logUrl
            if (!logUrl.isNullOrBlank()) {
                Button(
                    label = "View build log",
                    onClick = { onViewLog(logUrl) },
                    variant = ButtonVariant.Secondary,
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun HistoryDetailStatusHeader(
    phase: BuildPhase,
    providerName: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BuildHistoryPhaseIcon(phase = phase, size = 24.dp)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            BasicText(
                text = phase.toHistoryLabel(),
                style = Typography.Headline.copy(color = historyPhaseTitleColor(phase)),
            )
            if (!providerName.isNullOrBlank()) {
                BasicText(
                    text = "via $providerName",
                    style = Typography.Caption.copy(color = Theme.colors.Muted),
                )
            }
        }
    }
}

@Composable
private fun HistoryDetailLine(
    label: String,
    value: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        BasicText(
            text = label,
            style = Typography.Caption.copy(color = Theme.colors.Muted2),
        )
        BasicText(
            text = value,
            style = Typography.Body.copy(color = Theme.colors.Text),
        )
    }
}

@Composable
@ReadOnlyComposable
private fun historyPhaseTitleColor(phase: BuildPhase) = when (phase) {
    BuildPhase.Failed -> Theme.colors.Danger
    BuildPhase.ReadyToInstall -> Theme.colors.Run
    else -> Theme.colors.Text
}

private fun historyDetailPhaseItems(state: BuildHistoryDetailUiState) = when (state.phase) {
    BuildPhase.Failed -> buildPhaseItems(
        currentPhase = state.phase,
        failedAtPhase = state.lastActivePhase ?: BuildPhase.Building,
    )
    BuildPhase.Cancelled -> buildPhaseItems(
        currentPhase = state.phase,
        cancelledAtPhase = state.lastActivePhase ?: BuildPhase.Preparing,
    )
    else -> buildPhaseItems(currentPhase = state.phase)
}
