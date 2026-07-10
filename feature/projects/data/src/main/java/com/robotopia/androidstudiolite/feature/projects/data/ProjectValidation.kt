package com.robotopia.androidstudiolite.feature.projects.data

import com.robotopia.androidstudiolite.feature.projects.model.CreateProjectRequest

internal object ProjectValidation {
    private val packageNameRegex =
        Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$")

    fun validate(request: CreateProjectRequest) {
        val name = request.name.trim()
        require(name.isNotEmpty()) { "Project name is required" }
        require(name.length <= 64) { "Project name is too long" }
        require(!name.contains('/') && !name.contains('\\')) {
            "Project name cannot contain path separators"
        }

        val packageName = request.packageName.trim()
        require(packageName.isNotEmpty()) { "Package name is required" }
        require(packageNameRegex.matches(packageName)) {
            "Package name must be a valid Java package (e.g. com.example.app)"
        }

        require(request.minSdk in 21..35) {
            "Min SDK must be between 21 and 35"
        }
    }
}
