package com.robotopia.androidstudiolite.core.model

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
)

data class ProjectRoot(val absolutePath: String)
