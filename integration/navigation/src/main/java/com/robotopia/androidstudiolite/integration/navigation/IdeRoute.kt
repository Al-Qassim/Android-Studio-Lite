package com.robotopia.androidstudiolite.integration.navigation

import androidx.compose.runtime.saveable.Saver
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId

/**
 * Cross-feature route. Deep links carry the project fields destinations need
 * so the host does not fetch [Project] to render.
 */
internal sealed interface IdeRoute {
    data object Onboarding : IdeRoute
    data object Projects : IdeRoute
    data object Settings : IdeRoute

    data class Files(
        val projectId: ProjectId,
        val projectName: String,
        val rootPath: String,
        val packageName: String,
    ) : IdeRoute

    data class Editor(
        val projectId: ProjectId,
        val relativePath: String,
        val projectName: String,
        val rootPath: String,
        val packageName: String,
    ) : IdeRoute

    data class Build(
        val projectId: ProjectId,
        val projectName: String,
        val rootPath: String,
        val packageName: String,
        val returnTo: IdeRoute,
    ) : IdeRoute
}

internal fun IdeRoute.projectIdOrNull(): ProjectId? = when (this) {
    is IdeRoute.Files -> projectId
    is IdeRoute.Editor -> projectId
    is IdeRoute.Build -> projectId
    IdeRoute.Onboarding, IdeRoute.Projects, IdeRoute.Settings -> null
}

internal fun IdeRoute.Files.toEditor(relativePath: String): IdeRoute.Editor =
    IdeRoute.Editor(
        projectId = projectId,
        relativePath = relativePath,
        projectName = projectName,
        rootPath = rootPath,
        packageName = packageName,
    )

internal fun IdeRoute.Editor.toFiles(): IdeRoute.Files =
    IdeRoute.Files(
        projectId = projectId,
        projectName = projectName,
        rootPath = rootPath,
        packageName = packageName,
    )

internal fun IdeRoute.Files.toBuild(returnTo: IdeRoute = this): IdeRoute.Build =
    IdeRoute.Build(
        projectId = projectId,
        projectName = projectName,
        rootPath = rootPath,
        packageName = packageName,
        returnTo = returnTo,
    )

internal fun IdeRoute.Editor.toBuild(returnTo: IdeRoute = this): IdeRoute.Build =
    IdeRoute.Build(
        projectId = projectId,
        projectName = projectName,
        rootPath = rootPath,
        packageName = packageName,
        returnTo = returnTo,
    )

internal fun Project.toFilesRoute(): IdeRoute.Files =
    IdeRoute.Files(
        projectId = id,
        projectName = name,
        rootPath = rootPath,
        packageName = packageName,
    )

internal fun Project.toBuildRoute(returnTo: IdeRoute): IdeRoute.Build =
    IdeRoute.Build(
        projectId = id,
        projectName = name,
        rootPath = rootPath,
        packageName = packageName,
        returnTo = returnTo,
    )

internal fun IdeRoute.Editor.documentId(): DocumentId =
    DocumentId(projectId, relativePath)

internal val IdeRouteSaver: Saver<IdeRoute, ArrayList<String>> = Saver(
    save = { ArrayList(it.toSaveList()) },
    restore = { it.toIdeRoute() },
)

internal fun IdeRoute.toSaveList(): List<String> = when (this) {
    IdeRoute.Onboarding -> listOf("onboarding")
    IdeRoute.Projects -> listOf("projects")
    IdeRoute.Settings -> listOf("settings")
    is IdeRoute.Files -> listOf(
        "files",
        projectId.value,
        projectName,
        rootPath,
        packageName,
    )
    is IdeRoute.Editor -> listOf(
        "editor",
        projectId.value,
        relativePath,
        projectName,
        rootPath,
        packageName,
    )
    is IdeRoute.Build -> buildList {
        add("build")
        add(projectId.value)
        add(projectName)
        add(rootPath)
        add(packageName)
        addAll(returnTo.toSaveList())
    }
}

internal fun List<String>.toIdeRoute(): IdeRoute? {
    if (isEmpty()) return null
    return when (this[0]) {
        "onboarding" -> IdeRoute.Onboarding
        "projects" -> IdeRoute.Projects
        "settings" -> IdeRoute.Settings
        "files" -> {
            val projectId = getOrNull(1) ?: return null
            val projectName = getOrNull(2) ?: return null
            val rootPath = getOrNull(3) ?: return null
            val packageName = getOrNull(4) ?: return null
            IdeRoute.Files(
                projectId = ProjectId(projectId),
                projectName = projectName,
                rootPath = rootPath,
                packageName = packageName,
            )
        }
        "editor" -> {
            val projectId = getOrNull(1) ?: return null
            val relativePath = getOrNull(2) ?: return null
            val projectName = getOrNull(3) ?: return null
            val rootPath = getOrNull(4) ?: return null
            val packageName = getOrNull(5) ?: return null
            IdeRoute.Editor(
                projectId = ProjectId(projectId),
                relativePath = relativePath,
                projectName = projectName,
                rootPath = rootPath,
                packageName = packageName,
            )
        }
        "build" -> {
            val projectId = getOrNull(1) ?: return null
            val projectName = getOrNull(2) ?: return null
            val rootPath = getOrNull(3) ?: return null
            val packageName = getOrNull(4) ?: return null
            val returnTo = drop(5).toIdeRoute() ?: IdeRoute.Projects
            IdeRoute.Build(
                projectId = ProjectId(projectId),
                projectName = projectName,
                rootPath = rootPath,
                packageName = packageName,
                returnTo = returnTo,
            )
        }
        else -> null
    }
}
