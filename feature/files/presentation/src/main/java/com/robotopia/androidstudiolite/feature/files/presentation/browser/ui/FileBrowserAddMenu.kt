package com.robotopia.androidstudiolite.feature.files.presentation.browser.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.robotopia.androidstudiolite.designsystem.component.CreateMenu
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreenContext
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserUiState
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.dismissAddMenu
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.openCreateFileDialog
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.openCreateFolderDialog

@Composable
internal fun FileBrowserScreenContext.FileBrowserAddMenu(state: FileBrowserUiState) {
    if (!state.addMenuOpen) return
    Popup(
        alignment = Alignment.TopEnd,
        onDismissRequest = { dismissAddMenu() },
        properties = PopupProperties(focusable = true),
    ) {
        CreateMenu(
            onNewFile = { openCreateFileDialog() },
            onNewFolder = { openCreateFolderDialog() },
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 48.dp, end = 12.dp),
        )
    }
}
