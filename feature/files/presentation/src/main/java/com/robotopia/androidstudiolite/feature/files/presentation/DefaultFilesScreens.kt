package com.robotopia.androidstudiolite.feature.files.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.robotopia.androidstudiolite.designsystem.animation.navFade
import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.api.FilesScreens
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreen
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreenContext
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserViewModel
import com.robotopia.androidstudiolite.feature.git.api.GitScreens
import java.io.File
import kotlinx.coroutines.flow.update
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private enum class FilesRoute {
    Browser,
    Git,
}

class DefaultFilesScreens(
    private val fileExplorerService: FileExplorerService,
    private val gitScreens: GitScreens,
) : FilesScreens {

    @Composable
    override fun NavHost(
        root: ProjectRoot,
        projectName: String,
        initialRelativePath: String,
        onOpenFile: (relativePath: String) -> Unit,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)?,
    ) {
        var route by rememberSaveable { mutableStateOf(FilesRoute.Browser) }

        AnimatedContent(
            targetState = route,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = { navFade() },
            label = "filesNav",
        ) { current ->
            when (current) {
                FilesRoute.Browser -> {
                    FileBrowser(
                        root = root,
                        projectName = projectName,
                        initialRelativePath = initialRelativePath,
                        onOpenFile = onOpenFile,
                        onNavigateBack = onNavigateBack,
                        onRun = onRun,
                        onOpenGit = { route = FilesRoute.Git },
                    )
                }

                FilesRoute.Git -> {
                    gitScreens.ProjectGit(
                        projectRoot = File(root.absolutePath),
                        projectName = projectName,
                        onBack = { route = FilesRoute.Browser },
                    )
                }
            }
        }
    }

    @Composable
    override fun FileBrowser(
        root: ProjectRoot,
        projectName: String,
        initialRelativePath: String,
        onOpenFile: (relativePath: String) -> Unit,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)?,
    ) {
        FileBrowser(
            root = root,
            projectName = projectName,
            initialRelativePath = initialRelativePath,
            onOpenFile = onOpenFile,
            onNavigateBack = onNavigateBack,
            onRun = onRun,
            onOpenGit = null,
        )
    }

    @Composable
    private fun FileBrowser(
        root: ProjectRoot,
        projectName: String,
        initialRelativePath: String,
        onOpenFile: (relativePath: String) -> Unit,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)?,
        onOpenGit: (() -> Unit)?,
    ) {
        val viewModel: FileBrowserViewModel = koinViewModel(
            key = "${root.absolutePath}/${projectName}/${initialRelativePath}",
        ) {
            parametersOf(root, projectName, initialRelativePath)
        }
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        val screenContext = remember(
            viewModel,
            fileExplorerService,
            onOpenFile,
            onNavigateBack,
            onRun,
            onOpenGit,
        ) {
            FileBrowserScreenContext(
                updateState = { updater -> viewModel.uiState.update { updater(it) } },
                fileExplorerService = fileExplorerService,
                onOpenFile = onOpenFile,
                onNavigateBack = onNavigateBack,
                onRun = onRun,
                onOpenGit = onOpenGit,
                scope = viewModel.viewModelScope,
            )
        }
        screenContext.FileBrowserScreen(state)
    }
}
