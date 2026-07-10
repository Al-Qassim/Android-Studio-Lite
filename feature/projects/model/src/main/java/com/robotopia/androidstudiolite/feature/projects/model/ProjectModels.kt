package com.robotopia.androidstudiolite.feature.projects.model

data class ProjectId(val value: String)

data class Project(
    val id: ProjectId,
    val name: String,
    val packageName: String,
    val rootPath: String,
    val lastOpenedAt: Long?,
)

data class CreateProjectRequest(
    val name: String,
    val packageName: String,
    val minSdk: Int = 26,
)

/** Per-field errors for the create-project form. Null means the field is valid. */
data class CreateProjectFieldErrors(
    val name: String? = null,
    val packageName: String? = null,
    val minSdk: String? = null,
) {
    val hasErrors: Boolean
        get() = name != null || packageName != null || minSdk != null
}
