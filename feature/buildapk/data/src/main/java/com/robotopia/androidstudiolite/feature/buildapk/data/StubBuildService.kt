package com.robotopia.androidstudiolite.feature.buildapk.data

import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildProgress
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class StubBuildService : BuildService {
    override fun observeBuild(jobId: String): Flow<BuildProgress> = emptyFlow()
    override suspend fun startBuild(request: BuildRequest): String = "stub"
    override suspend fun cancelBuild(jobId: String) = Unit
}
