package com.robotopia.androidstudiolite.feature.buildapk.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Menu
import com.robotopia.androidstudiolite.designsystem.component.MenuItem
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.DialogMessageAction
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.component.ProjectRow
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.popup.topEndPopupOffset
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.ui.BuildHistoryPhaseIcon

private val HistoryMenuTopOffset = 62.dp
private val HistoryMenuEndOffset = 8.dp

@Composable
internal fun BuildHistoryContent(
    state: BuildHistoryUiState,
    onBackClick: () -> Unit,
    onJobClick: (BuildHistoryRowUi) -> Unit,
    onMenuOpen: (BuildHistoryRowUi) -> Unit,
    onMenuDismiss: () -> Unit,
    onDeleteMenuClick: (BuildHistoryRowUi) -> Unit,
    onDeleteCancel: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onRetryLoad: () -> Unit,
) {
    IslandScaffold(
        topBar = {
            TopBarBackTitle(
                title = "Build history",
                onBackClick = onBackClick,
            )
        },
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            BuildHistoryBody(
                state = state,
                onJobClick = onJobClick,
                onMenuOpen = onMenuOpen,
                onMenuDismiss = onMenuDismiss,
                onDeleteMenuClick = onDeleteMenuClick,
                onRetryLoad = onRetryLoad,
            )
        }
    }

    state.pendingDelete?.let { job ->
        DeleteHistoryJobDialog(
            projectName = job.projectName,
            phase = job.phase,
            onCancel = onDeleteCancel,
            onConfirm = onDeleteConfirm,
        )
    }
}

@Composable
private fun BuildHistoryBody(
    state: BuildHistoryUiState,
    onJobClick: (BuildHistoryRowUi) -> Unit,
    onMenuOpen: (BuildHistoryRowUi) -> Unit,
    onMenuDismiss: () -> Unit,
    onDeleteMenuClick: (BuildHistoryRowUi) -> Unit,
    onRetryLoad: () -> Unit,
) {
    val loadError = state.loadError
    when {
        state.isLoading -> BuildHistoryLoading()
        loadError != null -> BuildHistoryLoadError(
            message = loadError,
            onRetryLoad = onRetryLoad,
        )
        state.jobs.isEmpty() -> BuildHistoryEmpty()
        else -> BuildHistoryList(
            jobs = state.jobs,
            menuJobId = state.menuJobId,
            onJobClick = onJobClick,
            onMenuOpen = onMenuOpen,
            onMenuDismiss = onMenuDismiss,
            onDeleteMenuClick = onDeleteMenuClick,
        )
    }
}

@Composable
private fun BuildHistoryLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LoadingIndicator(label = "Loading builds…")
    }
}

@Composable
private fun BuildHistoryLoadError(
    message: String,
    onRetryLoad: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        EmptyState(
            title = "Couldn't load history",
            hint = message,
        )
        Button(
            label = "Try again",
            onClick = onRetryLoad,
            variant = ButtonVariant.Secondary,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun BuildHistoryEmpty() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        EmptyState(
            title = "No builds yet",
            hint = "Start a cloud build from a project to see it here.",
        )
    }
}

@Composable
private fun BuildHistoryList(
    jobs: List<BuildHistoryRowUi>,
    menuJobId: String?,
    onJobClick: (BuildHistoryRowUi) -> Unit,
    onMenuOpen: (BuildHistoryRowUi) -> Unit,
    onMenuDismiss: () -> Unit,
    onDeleteMenuClick: (BuildHistoryRowUi) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        items(jobs, key = { it.jobId }) { job ->
            BuildHistoryListItem(
                job = job,
                menuOpen = menuJobId == job.jobId,
                onJobClick = onJobClick,
                onMenuOpen = onMenuOpen,
                onMenuDismiss = onMenuDismiss,
                onDeleteMenuClick = onDeleteMenuClick,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 16.dp)
                    .height(1.dp)
                    .background(Colors.MenuDivider),
            )
        }
    }
}

@Composable
private fun BuildHistoryListItem(
    job: BuildHistoryRowUi,
    menuOpen: Boolean,
    onJobClick: (BuildHistoryRowUi) -> Unit,
    onMenuOpen: (BuildHistoryRowUi) -> Unit,
    onMenuDismiss: () -> Unit,
    onDeleteMenuClick: (BuildHistoryRowUi) -> Unit,
) {
    Box {
        ProjectRow(
            name = job.projectName,
            packageName = job.phase.toHistoryLabel(),
            meta = job.timeLabel,
            onClick = { onJobClick(job) },
            onLongClick = { onMenuOpen(job) },
            onMenuClick = { onMenuOpen(job) },
            leading = { BuildHistoryPhaseIcon(phase = job.phase) },
        )
        if (menuOpen) {
            BuildHistoryItemOverflowMenu(
                onDelete = {
                    onMenuDismiss()
                    onDeleteMenuClick(job)
                },
                onDismiss = onMenuDismiss,
            )
        }
    }
}

@Composable
private fun BuildHistoryItemOverflowMenu(
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    Popup(
        alignment = Alignment.TopEnd,
        offset = topEndPopupOffset(
            top = HistoryMenuTopOffset,
            end = HistoryMenuEndOffset,
        ),
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        Menu(
            items = listOf(
                MenuItem.Button(label = "Delete", onClick = onDelete, danger = true),
            ),
        )
    }
}

@Composable
private fun DeleteHistoryJobDialog(
    projectName: String,
    phase: BuildPhase,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    val message = if (phase.isActiveHistoryPhase()) {
        "This will cancel the running build for $projectName and remove it from history."
    } else {
        "Remove this build for $projectName from history? The APK file is kept if it still exists."
    }
    Dialog(onDismissRequest = onCancel) {
        DialogMessageAction(
            title = "Delete build?",
            message = message,
            actionLabel = "Delete",
            dangerAction = true,
            onCancel = onCancel,
            onAction = onConfirm,
        )
    }
}
