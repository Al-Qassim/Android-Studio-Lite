package com.robotopia.androidstudiolite.feature.files.presentation

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.api.FilesScreens
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreen

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
        FileBrowserScreen(
            root = root,
            projectName = projectName,
            initialRelativePath = initialRelativePath,
            fileExplorerService = fileExplorerService,
            onOpenFile = onOpenFile,
            onNavigateBack = onNavigateBack,
        )
    }
}
