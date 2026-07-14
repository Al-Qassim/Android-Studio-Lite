package com.robotopia.androidstudiolite.feature.buildapk.presentation.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.icon.IconSuccess
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase

private val progressPhases = listOf(
    BuildPhase.Preparing,
    BuildPhase.Uploading,
    BuildPhase.Queued,
    BuildPhase.Building,
    BuildPhase.Downloading,
    BuildPhase.ReadyToInstall,
)

@Composable
internal fun BuildProgressContent(
    state: BuildProgressUiState,
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    onInstall: (apkLocalPath: String) -> Unit,
    onRetry: (() -> Unit)?,
    onViewLog: ((logUrl: String) -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        TopBarBackTitle(
            title = "Build",
            onBackClick = onDismiss,
        )
        BuildProgressBody(
            state = state,
            onDismiss = onDismiss,
            onCancel = onCancel,
            onInstall = onInstall,
            onRetry = onRetry,
            onViewLog = onViewLog,
        )
    }
}

@Composable
private fun BuildProgressBody(
    state: BuildProgressUiState,
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    onInstall: (apkLocalPath: String) -> Unit,
    onRetry: (() -> Unit)?,
    onViewLog: ((logUrl: String) -> Unit)?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        when {
            state.phase == BuildPhase.Cancelled -> BuildCancelledState(
                message = state.message,
            )
            state.error != null || state.phase == BuildPhase.Failed -> BuildFailedState(
                state = state,
                onRetry = onRetry,
                onDismiss = onDismiss,
                onViewLog = onViewLog,
            )
            else -> BuildActiveState(
                state = state,
                onCancel = onCancel,
                onInstall = onInstall,
            )
        }
    }
}

@Composable
private fun BuildActiveState(
    state: BuildProgressUiState,
    onCancel: () -> Unit,
    onInstall: (apkLocalPath: String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BuildProgressHeader(
                title = phaseLabel(state.phase),
                titleColor = Colors.Text,
                message = state.message,
                providerName = state.providerName,
            )
            BuildProgressBar(fraction = state.progressFraction)
            BuildPhaseList(
                currentPhase = state.phase,
                failedAtPhase = null,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (state.phase == BuildPhase.ReadyToInstall) {
            val apkPath = state.apkLocalPath
            Button(
                label = "Install app",
                onClick = { apkPath?.let(onInstall) },
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Primary,
                enabled = apkPath != null,
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    label = "Cancel",
                    onClick = onCancel,
                    variant = ButtonVariant.Secondary,
                )
            }
        }
    }
}

@Composable
private fun BuildFailedState(
    state: BuildProgressUiState,
    onRetry: (() -> Unit)?,
    onDismiss: () -> Unit,
    onViewLog: ((logUrl: String) -> Unit)?,
) {
    val logUrl = state.logUrl
    Column(modifier = Modifier.fillMaxSize()) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            BuildProgressHeader(
                title = "Build failed",
                titleColor = Colors.Danger,
                message = state.error ?: state.message
                    ?: "Build failed. Open the build log.",
                providerName = state.providerName,
            )
            BuildPhaseList(
                currentPhase = state.phase,
                failedAtPhase = state.failedAtPhase ?: BuildPhase.Building,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!logUrl.isNullOrBlank() && onViewLog != null) {
                Button(
                    label = "View build log",
                    onClick = { onViewLog(logUrl) },
                    modifier = Modifier.fillMaxWidth(),
                    variant = ButtonVariant.Secondary,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    label = "Close",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    variant = ButtonVariant.Secondary,
                )
                if (onRetry != null) {
                    Button(
                        label = "Retry",
                        onClick = onRetry,
                        modifier = Modifier.weight(1f),
                        variant = ButtonVariant.Primary,
                    )
                }
            }
        }
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
            style = Typography.Headline.copy(color = Colors.Text),
        )
        BasicText(
            text = message?.takeIf { it.isNotBlank() }
                ?: "No APK was produced. You can start a new build when you're ready.",
            style = Typography.Body.copy(color = Colors.Muted),
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
                style = Typography.Body.copy(color = Colors.Muted),
            )
        }
        if (!providerName.isNullOrBlank()) {
            BasicText(
                text = "via $providerName",
                style = Typography.Caption.copy(color = Colors.Muted),
            )
        }
    }
}

@Composable
private fun BuildProgressBar(fraction: Float) {
    val shape = RoundedCornerShape(999.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(shape)
            .background(Colors.Surface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(8.dp)
                .clip(shape)
                .background(Colors.Primary),
        )
    }
}

@Composable
private fun BuildPhaseList(
    currentPhase: BuildPhase,
    failedAtPhase: BuildPhase?,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Colors.Surface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        progressPhases.forEach { phase ->
            BuildPhaseRow(
                label = phaseLabel(phase),
                status = phaseStatus(
                    phase = phase,
                    current = currentPhase,
                    failedAtPhase = failedAtPhase,
                ),
            )
        }
    }
}

@Composable
private fun BuildPhaseRow(
    label: String,
    status: PhaseRowStatus,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PhaseStatusIcon(status = status)
        BasicText(
            text = label,
            style = when (status) {
                PhaseRowStatus.Current -> Typography.BodyStrong.copy(color = Colors.Text)
                PhaseRowStatus.Complete -> Typography.Body.copy(color = Colors.Muted)
                PhaseRowStatus.Upcoming -> Typography.Body.copy(color = Colors.Muted2)
                PhaseRowStatus.Failed -> Typography.BodyStrong.copy(color = Colors.Danger)
            },
        )
    }
}

@Composable
private fun PhaseStatusIcon(status: PhaseRowStatus) {
    when (status) {
        PhaseRowStatus.Complete -> IconSuccess(tint = Colors.Primary, size = 16.dp)
        PhaseRowStatus.Current -> Box(
            modifier = Modifier.size(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = "•••",
                style = Typography.Caption.copy(color = Colors.Primary),
            )
        }
        PhaseRowStatus.Failed -> Box(
            modifier = Modifier.size(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = "✕",
                style = Typography.Caption.copy(
                    color = Colors.Danger,
                    textAlign = TextAlign.Center,
                ),
            )
        }
        PhaseRowStatus.Upcoming -> Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Colors.Disabled),
        )
    }
}

private enum class PhaseRowStatus {
    Complete,
    Current,
    Upcoming,
    Failed,
}

private fun phaseLabel(phase: BuildPhase): String = when (phase) {
    BuildPhase.Preparing -> "Preparing"
    BuildPhase.Uploading -> "Uploading"
    BuildPhase.Queued -> "Queued"
    BuildPhase.Building -> "Building"
    BuildPhase.Downloading -> "Downloading"
    BuildPhase.ReadyToInstall -> "Ready to install"
    BuildPhase.Failed -> "Failed"
    BuildPhase.Cancelled -> "Cancelled"
}

private fun phaseStatus(
    phase: BuildPhase,
    current: BuildPhase,
    failedAtPhase: BuildPhase?,
): PhaseRowStatus {
    val phaseIndex = progressPhases.indexOf(phase)
    if (failedAtPhase != null) {
        val failedIndex = progressPhases.indexOf(failedAtPhase).coerceAtLeast(0)
        return when {
            phaseIndex < failedIndex -> PhaseRowStatus.Complete
            phaseIndex == failedIndex -> PhaseRowStatus.Failed
            else -> PhaseRowStatus.Upcoming
        }
    }
    val currentIndex = progressPhases.indexOf(current).coerceAtLeast(0)
    return when {
        current == BuildPhase.ReadyToInstall && phaseIndex <= currentIndex -> PhaseRowStatus.Complete
        phaseIndex < currentIndex -> PhaseRowStatus.Complete
        phaseIndex == currentIndex -> PhaseRowStatus.Current
        else -> PhaseRowStatus.Upcoming
    }
}
