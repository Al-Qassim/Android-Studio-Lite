package com.robotopia.androidstudiolite.integration.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.koin.dsl.module

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS build_jobs (
                jobId TEXT NOT NULL PRIMARY KEY,
                projectId TEXT NOT NULL,
                projectName TEXT NOT NULL,
                packageName TEXT NOT NULL,
                projectRootPath TEXT NOT NULL,
                phase TEXT NOT NULL,
                message TEXT,
                error TEXT,
                apkLocalPath TEXT,
                logUrl TEXT,
                providerName TEXT,
                providerId TEXT NOT NULL,
                resumeJson TEXT,
                lastActivePhase TEXT,
                startedAtEpochMs INTEGER NOT NULL,
                finishedAtEpochMs INTEGER
            )
            """.trimIndent(),
        )
    }
}

fun createAslDatabase(context: Context): AslDatabase =
    Room.databaseBuilder(
        context.applicationContext,
        AslDatabase::class.java,
        "asl.db",
    )
        .addMigrations(MIGRATION_1_2)
        .build()

val databaseDiModule = module {
    single { createAslDatabase(get()) }
    single { get<AslDatabase>().projectDao() }
    single { get<AslDatabase>().buildJobDao() }
}
