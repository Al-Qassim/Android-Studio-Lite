package com.robotopia.androidstudiolite.feature.git.presentation.project.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.robotopia.androidstudiolite.designsystem.component.Menu
import com.robotopia.androidstudiolite.designsystem.component.MenuItem
import com.robotopia.androidstudiolite.designsystem.popup.topEndPopupOffset
import com.robotopia.androidstudiolite.feature.git.api.GitBranchKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissBranchMenu
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openDeleteConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openMergeConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openRename
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestCheckout

@Composable
internal fun ProjectGitScreenContext.ProjectGitBranchMenu(state: ProjectGitUiState) {
    val branch = state.menuBranch ?: return
    val isRemote = branch.kind == GitBranchKind.Remote
    val isCurrent = branch.isCurrent

    Popup(
        alignment = Alignment.TopEnd,
        offset = topEndPopupOffset(top = 96.dp, end = 16.dp, includeStatusBars = true),
        onDismissRequest = { dismissBranchMenu() },
        properties = PopupProperties(focusable = true),
    ) {
        val items = buildList {
            if (!isCurrent) {
                add(
                    MenuItem.Button(
                        label = "Checkout",
                        onClick = { requestCheckout(branch) },
                    ),
                )
                add(
                    MenuItem.Button(
                        label = "Merge into current",
                        onClick = { openMergeConfirm(branch) },
                    ),
                )
            }
            if (!isRemote) {
                add(
                    MenuItem.Button(
                        label = "Rename",
                        onClick = { openRename(branch) },
                    ),
                )
                if (!isCurrent) {
                    add(
                        MenuItem.Button(
                            label = "Delete",
                            onClick = { openDeleteConfirm(branch) },
                        ),
                    )
                }
            }
        }
        Menu(items = items)
    }
}
