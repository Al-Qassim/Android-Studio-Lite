package com.robotopia.androidstudiolite.feature.git.presentation.project.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.robotopia.androidstudiolite.designsystem.component.Menu
import com.robotopia.androidstudiolite.designsystem.component.MenuItem
import com.robotopia.androidstudiolite.designsystem.popup.rememberEndAlignedMenuPopupPositionProvider
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import com.robotopia.androidstudiolite.feature.git.api.GitBranchKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissBranchMenu
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openCreateBranch
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openDeleteConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openMergeConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openRename
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestCheckout

@Composable
internal fun ProjectGitScreenContext.BranchOverflowMenu(branch: GitBranch) {
    val isRemote = branch.kind == GitBranchKind.Remote
    val isCurrent = branch.isCurrent
    val positionProvider = rememberEndAlignedMenuPopupPositionProvider()

    Popup(
        popupPositionProvider = positionProvider,
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
            add(
                MenuItem.Button(
                    label = "New branch from here",
                    onClick = { openCreateBranch(branch) },
                ),
            )
            add(
                MenuItem.Button(
                    label = "Rename",
                    onClick = { openRename(branch) },
                ),
            )
            if (!isRemote && !isCurrent) {
                add(MenuItem.Divider)
                add(
                    MenuItem.Button(
                        label = "Delete",
                        onClick = { openDeleteConfirm(branch) },
                        danger = true,
                    ),
                )
            }
        }
        Menu(items = items)
    }
}
