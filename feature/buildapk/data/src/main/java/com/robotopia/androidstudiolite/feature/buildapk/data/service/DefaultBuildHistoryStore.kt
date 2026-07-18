package com.robotopia.androidstudiolite.feature.buildapk.data.service

import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryStore
import com.robotopia.androidstudiolite.feature.buildapk.data.room.BuildJobDao
import com.robotopia.androidstudiolite.feature.buildapk.data.room.toHistoryItem
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildHistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-backed build history. Delete notifies [DefaultBuildHistoryEventHooks] first
 * (e.g. [DefaultBuildService] cancels active jobs); APK files on disk are kept.
 */
class DefaultBuildHistoryStore(
    private val buildJobDao: BuildJobDao,
    private val historyEventHooks: DefaultBuildHistoryEventHooks,
) : BuildHistoryStore {
    override fun observeHistory(projectId: String?): Flow<List<BuildHistoryItem>> =
        if (projectId.isNullOrBlank()) {
            buildJobDao.observeAll().map { list -> list.map { it.toHistoryItem() } }
        } else {
            buildJobDao.observeByProject(projectId).map { list -> list.map { it.toHistoryItem() } }
        }

    override fun observeJob(jobId: String): Flow<BuildHistoryItem?> =
        buildJobDao.observeById(jobId).map { it?.toHistoryItem() }

    override suspend fun getJob(jobId: String): BuildHistoryItem? =
        buildJobDao.getById(jobId)?.toHistoryItem()

    override suspend fun delete(jobId: String) {
        if (buildJobDao.getById(jobId) == null) return
        historyEventHooks.notifyHistoryJobDeleting(jobId)
        buildJobDao.deleteById(jobId)
    }
}
