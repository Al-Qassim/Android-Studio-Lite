package com.robotopia.androidstudiolite.integration.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.robotopia.androidstudiolite.feature.projects.data.ProjectDao
import com.robotopia.androidstudiolite.feature.projects.data.ProjectEntity

/**
 * Wires feature-owned entities/DAOs into one Room database.
 * Features declare schema in `:feature:*:data`; this module only assembles them.
 */
@Database(
    entities = [ProjectEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AslDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
}
