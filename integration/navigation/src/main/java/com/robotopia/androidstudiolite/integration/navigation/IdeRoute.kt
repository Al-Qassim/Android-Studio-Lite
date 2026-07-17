package com.robotopia.androidstudiolite.integration.navigation

import androidx.compose.runtime.saveable.Saver
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId

/**
 * Cross-feature route — IDs only so [rememberSaveable] stays small and stable.
 */
internal sealed interface IdeRoute {
    data object Onboarding : IdeRoute
    data object Projects : IdeRoute
    data object Settings : IdeRoute
    data class Files(val projectId: ProjectId) : IdeRoute
    data class Editor(val documentId: DocumentId) : IdeRoute
    data class Build(
        val projectId: ProjectId,
        val returnTo: IdeRoute,
    ) : IdeRoute
}

internal fun IdeRoute.projectIdOrNull(): ProjectId? = when (this) {
    is IdeRoute.Files -> projectId
    is IdeRoute.Editor -> documentId.projectId
    is IdeRoute.Build -> projectId
    IdeRoute.Onboarding, IdeRoute.Projects, IdeRoute.Settings -> null
}

internal val IdeRouteSaver: Saver<IdeRoute, ArrayList<String>> = Saver(
    save = { ArrayList(it.toSaveList()) },
    restore = { it.toIdeRoute() },
)

internal fun IdeRoute.toSaveList(): List<String> = when (this) {
    IdeRoute.Onboarding -> listOf("onboarding")
    IdeRoute.Projects -> listOf("projects")
    IdeRoute.Settings -> listOf("settings")
    is IdeRoute.Files -> listOf("files", projectId.value)
    is IdeRoute.Editor -> listOf(
        "editor",
        documentId.projectId.value,
        documentId.relativePath,
    )
    is IdeRoute.Build -> buildList {
        add("build")
        add(projectId.value)
        addAll(returnTo.toSaveList())
    }
}

internal fun List<String>.toIdeRoute(): IdeRoute? {
    if (isEmpty()) return null
    return when (this[0]) {
        "onboarding" -> IdeRoute.Onboarding
        "projects" -> IdeRoute.Projects
        "settings" -> IdeRoute.Settings
        "files" -> getOrNull(1)?.let { IdeRoute.Files(ProjectId(it)) }
        "editor" -> {
            val projectId = getOrNull(1) ?: return null
            val path = getOrNull(2) ?: return null
            IdeRoute.Editor(DocumentId(ProjectId(projectId), path))
        }
        "build" -> {
            val projectId = getOrNull(1) ?: return null
            val returnTokens = drop(2)
            val returnTo = returnTokens.toIdeRoute() ?: IdeRoute.Projects
            IdeRoute.Build(ProjectId(projectId), returnTo)
        }
        else -> null
    }
}
