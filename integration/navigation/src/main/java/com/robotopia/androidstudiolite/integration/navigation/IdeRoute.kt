package com.robotopia.androidstudiolite.integration.navigation

import androidx.compose.runtime.saveable.Saver
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Cross-feature route. Deep links carry the project fields destinations need
 * so the host does not fetch [Project] to render.
 *
 * Route payloads stay serializable primitives (e.g. project id as [String]);
 * wrap into domain types only at feature API boundaries.
 */
@Serializable
internal sealed interface IdeRoute {
    @Serializable
    @SerialName("onboarding")
    data object Onboarding : IdeRoute

    @Serializable
    @SerialName("projects")
    data object Projects : IdeRoute

    @Serializable
    @SerialName("settings")
    data object Settings : IdeRoute

    @Serializable
    @SerialName("files")
    data class Files(
        val projectId: String,
        val projectName: String,
        val rootPath: String,
        val packageName: String,
    ) : IdeRoute

    @Serializable
    @SerialName("editor")
    data class Editor(
        val projectId: String,
        val relativePath: String,
        val projectName: String,
        val rootPath: String,
        val packageName: String,
    ) : IdeRoute

    @Serializable
    @SerialName("build")
    data class Build(
        val projectId: String,
        val projectName: String,
        val rootPath: String,
        val packageName: String,
        val returnTo: IdeRoute,
    ) : IdeRoute
}

internal fun IdeRoute.projectIdOrNull(): String? = when (this) {
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
        projectId = id.value,
        projectName = name,
        rootPath = rootPath,
        packageName = packageName,
    )

internal fun Project.toBuildRoute(returnTo: IdeRoute): IdeRoute.Build =
    IdeRoute.Build(
        projectId = id.value,
        projectName = name,
        rootPath = rootPath,
        packageName = packageName,
        returnTo = returnTo,
    )

internal fun IdeRoute.Editor.documentId(): DocumentId =
    DocumentId(ProjectId(projectId), relativePath)

private val IdeRouteJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

internal fun IdeRoute.encodeToString(): String =
    IdeRouteJson.encodeToString(IdeRoute.serializer(), this)

internal fun String.decodeIdeRouteOrNull(): IdeRoute? =
    runCatching { IdeRouteJson.decodeFromString(IdeRoute.serializer(), this) }
        .getOrNull()

/**
 * Persists [IdeRoute] for [androidx.compose.runtime.saveable.rememberSaveable]
 * as a kotlinx.serialization JSON string.
 */
internal val IdeRouteSaver: Saver<IdeRoute, String> = Saver(
    save = { route -> route.encodeToString() },
    restore = { saved -> saved.decodeIdeRouteOrNull() },
)
