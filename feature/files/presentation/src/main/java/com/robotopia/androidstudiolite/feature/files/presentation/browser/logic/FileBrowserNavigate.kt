package com.robotopia.androidstudiolite.feature.files.presentation.browser.logic

import com.robotopia.androidstudiolite.feature.files.model.parentRelativePathOrNull
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreenContext
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserUiState

internal fun FileBrowserScreenContext.navigateUp(state: FileBrowserUiState) {
    when {
        state.dialog != null ->
            updateState { copy(dialog = null) }
        state.menuItem != null ->
            updateState { copy(menuItem = null) }
        state.addMenuOpen ->
            updateState { copy(addMenuOpen = false) }
        else -> {
            val parent = parentRelativePathOrNull(state.currentRelativePath)
            if (parent == null) {
                onNavigateBack()
            } else {
                updateState {
                    copy(
                        currentRelativePath = parent,
                        menuItem = null,
                        addMenuOpen = false,
                    )
                }
            }
        }
    }
}
