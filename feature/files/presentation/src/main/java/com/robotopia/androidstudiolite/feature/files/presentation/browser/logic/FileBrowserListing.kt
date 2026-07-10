package com.robotopia.androidstudiolite.feature.files.presentation.browser.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreenContext
import kotlinx.coroutines.flow.catch

private const val TAG = "FileBrowser"
private const val GENERIC_ERROR_MESSAGE = "Something went wrong"

internal suspend fun FileBrowserScreenContext.collectListing(
    root: ProjectRoot,
    relativePath: String,
) {
    fileExplorerService.observeListing(root, relativePath)
        .catch { error ->
            updateState {
                copy(
                    entries = emptyList(),
                    menuItem = null,
                    actionError = error.userMessageOrNull(TAG) ?: GENERIC_ERROR_MESSAGE,
                )
            }
        }
        .collect { listing ->
            updateState {
                copy(
                    currentRelativePath = listing.currentRelativePath,
                    entries = listing.entries,
                    menuItem = menuItem?.takeIf { menu ->
                        listing.entries.any { it.relativePath == menu.relativePath }
                    },
                )
            }
        }
}
