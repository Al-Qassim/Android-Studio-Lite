package com.robotopia.androidstudiolite.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val packageName: String,
    val rootPath: String,
    val lastOpenedAt: Long? = null,
)
