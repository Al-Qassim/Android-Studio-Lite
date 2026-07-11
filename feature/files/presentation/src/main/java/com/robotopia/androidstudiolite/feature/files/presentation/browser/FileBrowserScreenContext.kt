package com.robotopia.androidstudiolite.feature.files.presentation.browser

import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import kotlinx.coroutines.CoroutineScope

class FileBrowserScreenContext(
    val updateState: (FileBrowserUiState.() -> FileBrowserUiState) -> Unit,
    val fileExplorerService: FileExplorerService,
    val onOpenFile: (relativePath: String) -> Unit,
    val onNavigateBack: () -> Unit,
    val onRun: (() -> Unit)?,
    val scope: CoroutineScope,
)
