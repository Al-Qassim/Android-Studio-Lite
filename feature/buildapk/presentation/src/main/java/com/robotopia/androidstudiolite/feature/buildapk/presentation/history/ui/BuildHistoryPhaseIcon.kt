package com.robotopia.androidstudiolite.feature.buildapk.presentation.history.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.icon.IconSuccess
import com.robotopia.androidstudiolite.designsystem.icon.IconWarning
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase

@Composable
internal fun BuildHistoryPhaseIcon(
    phase: BuildPhase,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
) {
    when (phase) {
        BuildPhase.ReadyToInstall -> IconSuccess(
            modifier = modifier,
            tint = Colors.Run,
            size = size,
        )
        BuildPhase.Failed -> IconWarning(
            modifier = modifier,
            tint = Colors.Danger,
            size = size,
        )
        BuildPhase.Cancelled -> IconWarning(
            modifier = modifier,
            tint = Colors.Muted,
            size = size,
        )
        BuildPhase.Preparing,
        BuildPhase.Uploading,
        BuildPhase.Queued,
        BuildPhase.Building,
        BuildPhase.Downloading,
        -> LoadingIndicator(
            modifier = modifier,
            size = size,
        )
    }
}
