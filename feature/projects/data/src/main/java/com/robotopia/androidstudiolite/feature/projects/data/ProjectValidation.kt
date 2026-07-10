package com.robotopia.androidstudiolite.feature.projects.data

import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.projects.model.CreateProjectFieldErrors
import com.robotopia.androidstudiolite.feature.projects.model.CreateProjectRequest

internal object ProjectValidation {
    private val packageNameRegex =
        Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$")

    fun fieldErrors(
        name: String,
        packageName: String,
        minSdk: Int?,
    ): CreateProjectFieldErrors {
        val trimmedName = name.trim()
        val nameError = when {
            trimmedName.isEmpty() -> "App name is required"
            trimmedName.length > 64 -> "Project name is too long"
            trimmedName.contains('/') || trimmedName.contains('\\') ->
                "Project name cannot contain path separators"
            else -> null
        }

        val trimmedPackage = packageName.trim()
        val packageError = when {
            trimmedPackage.isEmpty() -> "Package name is required"
            !packageNameRegex.matches(trimmedPackage) ->
                "Use a valid Java package (e.g. com.example.app)"
            else -> null
        }

        val minSdkError = when {
            minSdk == null -> "Min SDK is required"
            minSdk !in 21..35 -> "Min SDK must be between 21 and 35"
            else -> null
        }

        return CreateProjectFieldErrors(
            name = nameError,
            packageName = packageError,
            minSdk = minSdkError,
        )
    }

    fun validate(request: CreateProjectRequest) {
        val errors = fieldErrors(
            name = request.name,
            packageName = request.packageName,
            minSdk = request.minSdk,
        )
        errors.name?.let { throw AppException(it) }
        errors.packageName?.let { throw AppException(it) }
        errors.minSdk?.let { throw AppException(it) }
    }
}
