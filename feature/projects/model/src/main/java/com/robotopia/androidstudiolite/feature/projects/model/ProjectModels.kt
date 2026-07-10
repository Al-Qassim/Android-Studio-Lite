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
