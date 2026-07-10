package com.robotopia.androidstudiolite.feature.files.presentation.browser

import androidx.lifecycle.ViewModel
import com.robotopia.androidstudiolite.feature.files.model.FsNode
import kotlinx.coroutines.flow.MutableStateFlow

enum class ClipboardMode {
    Cut,
    Copy,
}

data class ClipboardState(
    val mode: ClipboardMode,
    val relativePath: String,
    val node: FsNode,
)

sealed interface FileBrowserDialog {
    data class CreateFile(
        val name: String = "",
        val nameError: String? = null,
    ) : FileBrowserDialog

    data class CreateFolder(
        val name: String = "",
        val nameError: String? = null,
    ) : FileBrowserDialog

    data class Rename(
        val item: FsNode,
        val name: String,
        val nameError: String? = null,
    ) : FileBrowserDialog

    data class DeleteConfirm(
        val item: FsNode,
    ) : FileBrowserDialog
}

data class FileBrowserUiState(
    val projectName: String = "",
    val currentRelativePath: String = "",
    val entries: List<FsNode> = emptyList(),
    val menuItem: FsNode? = null,
    val addMenuOpen: Boolean = false,
    val clipboard: ClipboardState? = null,
    val dialog: FileBrowserDialog? = null,
    val actionError: String? = null,
)

/** Holds file-browser UI state across configuration changes. No business/UI logic. */
class FileBrowserViewModel : ViewModel() {
    val uiState = MutableStateFlow(FileBrowserUiState())
}
