package com.robotopia.androidstudiolite.feature.files.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.api.FilesScreens
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreen
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreenContext
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserViewModel
import kotlinx.coroutines.flow.update
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class DefaultFilesScreens(
    private val fileExplorerService: FileExplorerService,
) : FilesScreens {

    @Composable
    override fun NavHost(
        root: ProjectRoot,
        projectName: String,
        initialRelativePath: String,
        onOpenFile: (relativePath: String) -> Unit,
        onNavigateBack: () -> Unit,
    ) {
        FileBrowser(
            root = root,
            projectName = projectName,
            initialRelativePath = initialRelativePath,
            onOpenFile = onOpenFile,
            onNavigateBack = onNavigateBack,
        )
    }

    @Composable
    override fun FileBrowser(
        root: ProjectRoot,
        projectName: String,
        initialRelativePath: String,
        onOpenFile: (relativePath: String) -> Unit,
        onNavigateBack: () -> Unit,
    ) {
        val viewModel: FileBrowserViewModel = koinViewModel {
            parametersOf(root, projectName, initialRelativePath)
        }
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        val screenContext = FileBrowserScreenContext(
            updateState = { updater -> viewModel.uiState.update { updater(it) } },
            fileExplorerService = fileExplorerService,
            onOpenFile = onOpenFile,
            onNavigateBack = onNavigateBack,
            scope = viewModel.viewModelScope,
        )
        screenContext.FileBrowserScreen(state)
    }
}
