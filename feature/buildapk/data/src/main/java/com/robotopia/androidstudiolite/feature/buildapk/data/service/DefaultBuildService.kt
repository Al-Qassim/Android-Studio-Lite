package com.robotopia.androidstudiolite.feature.buildapk.data.service

import android.content.Context
import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryEventHooks
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryEventsListener
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.data.github.GitHubCloudBuildGatewayAdapter
import com.robotopia.androidstudiolite.feature.buildapk.data.job.BuildJobLogic
import com.robotopia.androidstudiolite.feature.buildapk.data.room.BuildJobDao
import com.robotopia.androidstudiolite.feature.buildapk.data.room.RoomBuildJobRepositoryAdapter
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildProgress
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
import com.robotopia.androidstudiolite.feature.github.api.GitHubClient
import com.robotopia.androidstudiolite.feature.projects.api.ProjectEventHooks
import com.robotopia.androidstudiolite.feature.projects.api.ProjectEventsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Wires [BuildJobLogic] to Room + GitHub adapters, auth, and event hooks.
 */
class DefaultBuildService(
    context: Context,
    private val authSession: AuthSession,
    gitHubClient: GitHubClient,
    buildJobDao: BuildJobDao,
    historyEventHooks: BuildHistoryEventHooks,
    projectEventHooks: ProjectEventHooks,
) : BuildService {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val logic = BuildJobLogic(
        jobs = RoomBuildJobRepositoryAdapter(buildJobDao),
        cloud = GitHubCloudBuildGatewayAdapter(context, gitHubClient),
        scope = scope,
    )

    init {
        historyEventHooks.addListener(
            BuildHistoryEventsListener { jobId ->
                logic.cancelBuild(jobId, authSession.accessToken())
            },
        )
        projectEventHooks.addListener(
            ProjectEventsListener { projectId ->
                logic.cancelBuildsForProject(projectId.value, authSession.accessToken())
            },
        )
        val tokenProvider: suspend () -> String? = { authSession.accessToken() }
        val providerName: () -> String = { authSession.providerDisplayName }
        logic.startEagerResume(tokenProvider, providerName)
        scope.launch {
            authSession.observeAccount().collect { account ->
                if (account != null) {
                    logic.onSignedIn(tokenProvider, providerName)
                }
            }
        }
    }

    override fun observeBuild(jobId: String): Flow<BuildProgress> =
        logic.observeBuild(jobId)

    override suspend fun startBuild(request: BuildRequest): String {
        val token = authSession.accessToken()
            ?: throw AppException("Connect your build account before starting a build.")
        return logic.startBuild(
            request = request,
            token = token,
            providerName = authSession.providerDisplayName,
        )
    }

    override suspend fun cancelBuild(jobId: String) {
        logic.cancelBuild(jobId, authSession.accessToken())
    }

    override suspend fun cancelBuildsForProject(projectId: String) {
        logic.cancelBuildsForProject(projectId, authSession.accessToken())
    }
}
