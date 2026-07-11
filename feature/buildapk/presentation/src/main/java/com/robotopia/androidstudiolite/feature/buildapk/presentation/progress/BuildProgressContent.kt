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
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.icon.IconSuccess
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase

private val progressPhases = listOf(
    BuildPhase.Queued,
    BuildPhase.Uploading,
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
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        when {
            state.error != null -> BuildErrorState(
                message = state.error,
                onRetry = onRetry,
                onDismiss = onDismiss,
            )
            state.phase == BuildPhase.Cancelled -> BuildCancelledState(onDismiss = onDismiss)
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
                phase = state.phase,
                message = state.message,
            )
            BuildProgressBar(fraction = state.progressFraction)
            BuildPhaseList(currentPhase = state.phase)
        }
        Spacer(modifier = Modifier.weight(1f))
        BuildActionRow(
            state = state,
            onCancel = onCancel,
            onInstall = onInstall,
        )
    }
}

@Composable
private fun BuildProgressHeader(
    phase: BuildPhase,
    message: String?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        BasicText(
            text = phaseLabel(phase),
            style = Typography.Headline.copy(color = Colors.Text),
        )
        if (!message.isNullOrBlank()) {
            BasicText(
                text = message,
                style = Typography.Body.copy(color = Colors.Muted),
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
private fun BuildPhaseList(currentPhase: BuildPhase) {
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
                status = phaseStatus(phase, currentPhase),
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
        PhaseRowStatus.Upcoming -> Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Colors.Disabled),
        )
    }
}

@Composable
private fun BuildActionRow(
    state: BuildProgressUiState,
    onCancel: () -> Unit,
    onInstall: (apkLocalPath: String) -> Unit,
) {
    Spacer(modifier = Modifier.height(12.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (state.phase != BuildPhase.ReadyToInstall) {
            Button(
                label = "Cancel",
                onClick = onCancel,
                variant = ButtonVariant.Secondary,
            )
        }
        if (state.phase == BuildPhase.ReadyToInstall) {
            val apkPath = state.apkLocalPath
            Button(
                label = "Install app",
                onClick = { apkPath?.let(onInstall) },
                variant = ButtonVariant.Primary,
                enabled = apkPath != null,
            )
        }
    }
}

@Composable
private fun BuildErrorState(
    message: String,
    onRetry: (() -> Unit)?,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BasicText(
            text = "Build failed",
            style = Typography.Headline.copy(color = Colors.Danger),
        )
        BasicText(
            text = message,
            style = Typography.Body.copy(color = Colors.Muted),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(label = "Close", onClick = onDismiss, variant = ButtonVariant.Secondary)
            if (onRetry != null) {
                Button(label = "Retry", onClick = onRetry, variant = ButtonVariant.Primary)
            }
        }
    }
}

@Composable
private fun BuildCancelledState(onDismiss: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BasicText(
            text = "Build cancelled",
            style = Typography.Headline.copy(color = Colors.Text),
            modifier = Modifier.fillMaxWidth(),
        )
        Button(label = "Close", onClick = onDismiss, variant = ButtonVariant.Secondary)
    }
}

private enum class PhaseRowStatus {
    Complete,
    Current,
    Upcoming,
}

private fun phaseLabel(phase: BuildPhase): String = when (phase) {
    BuildPhase.Queued -> "Queued"
    BuildPhase.Uploading -> "Uploading"
    BuildPhase.Building -> "Building"
    BuildPhase.Downloading -> "Downloading"
    BuildPhase.ReadyToInstall -> "Ready to install"
    BuildPhase.Failed -> "Failed"
    BuildPhase.Cancelled -> "Cancelled"
}

private fun phaseStatus(phase: BuildPhase, current: BuildPhase): PhaseRowStatus {
    val phaseIndex = progressPhases.indexOf(phase)
    val currentIndex = progressPhases.indexOf(current).coerceAtLeast(0)
    return when {
        current == BuildPhase.ReadyToInstall && phaseIndex <= currentIndex -> PhaseRowStatus.Complete
        phaseIndex < currentIndex -> PhaseRowStatus.Complete
        phaseIndex == currentIndex -> PhaseRowStatus.Current
        else -> PhaseRowStatus.Upcoming
    }
}
