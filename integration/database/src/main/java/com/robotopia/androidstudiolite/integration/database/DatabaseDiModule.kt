package com.robotopia.androidstudiolite.integration.database

import android.content.Context
import androidx.room.Room
import org.koin.dsl.module

fun createAslDatabase(context: Context): AslDatabase =
    Room.databaseBuilder(
        context.applicationContext,
        AslDatabase::class.java,
        "asl.db",
    ).build()

val databaseDiModule = module {
    single { createAslDatabase(get()) }
    single { get<AslDatabase>().projectDao() }
}
