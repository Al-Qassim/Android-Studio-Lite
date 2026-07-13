package com.robotopia.androidstudiolite.feature.buildapk.data

import android.content.Context
import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildProgress
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class FakeBuildService(
    context: Context,
) : BuildService {

    private val demoApkCache = DemoApkCache(context.applicationContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val jobs = ConcurrentHashMap<String, BuildJob>()

    override fun observeBuild(jobId: String): Flow<BuildProgress> {
        val job = jobs[jobId]
            ?: return MutableStateFlow(
                BuildProgress(
                    jobId = jobId,
                    phase = BuildPhase.Failed,
                    error = "Build not found",
                ),
            ).asStateFlow()
        return job.progress.asStateFlow()
    }

    override suspend fun startBuild(request: BuildRequest): String {
        val jobId = UUID.randomUUID().toString()
        val progress = MutableStateFlow(
            BuildProgress(
                jobId = jobId,
                phase = BuildPhase.Preparing,
                message = FakeBuildPhaseMachine.messageForPhase(BuildPhase.Preparing),
                providerName = FakeBuildPhaseMachine.PROVIDER_NAME,
            ),
        )
        val runner = scope.launch {
            runTimedBuild(jobId, progress)
        }
        jobs[jobId] = BuildJob(progress = progress, runner = runner)
        return jobId
    }

    override suspend fun cancelBuild(jobId: String) {
        val job = jobs.remove(jobId)
            ?: throw AppException("Build not found")
        job.runner.cancel()
        job.progress.update {
            it.copy(
                phase = BuildPhase.Cancelled,
                message = "No APK was produced. You can start a new build when you're ready.",
            )
        }
    }

    private suspend fun runTimedBuild(
        jobId: String,
        progress: MutableStateFlow<BuildProgress>,
    ) {
        for (timed in FakeBuildPhaseMachine.timedPhases) {
            progress.update {
                it.copy(
                    phase = timed.phase,
                    message = timed.message,
                    providerName = FakeBuildPhaseMachine.PROVIDER_NAME,
                    logUrl = null,
                )
            }
            var waited = 0L
            while (waited < timed.durationMs) {
                if (!coroutineContext.isActive) return
                val step = minOf(TICK_MS, timed.durationMs - waited)
                delay(step)
                waited += step
            }
        }

        val apkPath = runCatching { demoApkCache.copyBundledDemoApk() }
            .getOrElse { error ->
                progress.update {
                    it.copy(
                        phase = BuildPhase.Failed,
                        message = null,
                        error = "Build failed. Open the GitHub Actions log.",
                        providerName = FakeBuildPhaseMachine.PROVIDER_NAME,
                        logUrl = "https://github.com/",
                    )
                }
                jobs.remove(jobId)
                return
            }

        progress.update {
            it.copy(
                phase = BuildPhase.ReadyToInstall,
                message = FakeBuildPhaseMachine.messageForPhase(BuildPhase.ReadyToInstall),
                apkLocalPath = apkPath,
                providerName = FakeBuildPhaseMachine.PROVIDER_NAME,
            )
        }
    }

    private data class BuildJob(
        val progress: MutableStateFlow<BuildProgress>,
        val runner: Job,
    )

    private companion object {
        const val TICK_MS = 100L
    }
}
