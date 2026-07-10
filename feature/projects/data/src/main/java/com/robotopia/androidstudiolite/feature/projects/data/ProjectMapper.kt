package com.robotopia.androidstudiolite.feature.projects.data

import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId

fun ProjectEntity.toDomain(): Project = Project(
    id = ProjectId(id),
    name = name,
    packageName = packageName,
    rootPath = rootPath,
    lastOpenedAt = lastOpenedAt,
)

fun Project.toEntity(): ProjectEntity = ProjectEntity(
    id = id.value,
    name = name,
    packageName = packageName,
    rootPath = rootPath,
    lastOpenedAt = lastOpenedAt,
)
