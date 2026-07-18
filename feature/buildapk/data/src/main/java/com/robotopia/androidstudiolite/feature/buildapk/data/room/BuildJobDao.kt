package com.robotopia.androidstudiolite.feature.buildapk.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildJobDao {
    @Query(
        """
        SELECT * FROM build_jobs
        ORDER BY startedAtEpochMs DESC
        """,
    )
    fun observeAll(): Flow<List<BuildJobEntity>>

    @Query(
        """
        SELECT * FROM build_jobs
        WHERE projectId = :projectId
        ORDER BY startedAtEpochMs DESC
        """,
    )
    fun observeByProject(projectId: String): Flow<List<BuildJobEntity>>

    @Query("SELECT * FROM build_jobs WHERE jobId = :jobId")
    fun observeById(jobId: String): Flow<BuildJobEntity?>

    @Query("SELECT * FROM build_jobs WHERE jobId = :jobId")
    suspend fun getById(jobId: String): BuildJobEntity?

    @Query(
        """
        SELECT * FROM build_jobs
        WHERE phase NOT IN ('ReadyToInstall', 'Failed', 'Cancelled')
        """,
    )
    suspend fun getNonTerminal(): List<BuildJobEntity>

    @Query(
        """
        SELECT * FROM build_jobs
        WHERE projectId = :projectId
          AND phase NOT IN ('ReadyToInstall', 'Failed', 'Cancelled')
        """,
    )
    suspend fun getNonTerminalForProject(projectId: String): List<BuildJobEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: BuildJobEntity)

    @Query("DELETE FROM build_jobs WHERE jobId = :jobId")
    suspend fun deleteById(jobId: String)
}
