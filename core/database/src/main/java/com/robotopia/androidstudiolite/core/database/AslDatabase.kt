package com.robotopia.androidstudiolite.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProjectEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AslDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
}
