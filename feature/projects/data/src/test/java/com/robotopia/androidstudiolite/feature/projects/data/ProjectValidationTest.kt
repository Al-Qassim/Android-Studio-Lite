package com.robotopia.androidstudiolite.feature.projects.data

import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.projects.model.CreateProjectRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ProjectValidationTest {

    @Test
    fun acceptsValidRequest() {
        ProjectValidation.validate(
            CreateProjectRequest(name = "MyApp", packageName = "com.example.myapp", minSdk = 26),
        )
    }

    @Test
    fun rejectsBlankName() {
        val error = assertThrows(AppException::class.java) {
            ProjectValidation.validate(
                CreateProjectRequest(name = "  ", packageName = "com.example.app"),
            )
        }
        assertEquals("App name is required", error.uiMessage)
    }

    @Test
    fun fieldErrorsForBlankMinSdk() {
        val errors = ProjectValidation.fieldErrors(
            name = "MyApp",
            packageName = "com.example.app",
            minSdk = null,
        )
        assertEquals("Min SDK is required", errors.minSdk)
    }

    @Test
    fun rejectsPathSeparatorsInName() {
        val error = assertThrows(AppException::class.java) {
            ProjectValidation.validate(
                CreateProjectRequest(name = "bad/name", packageName = "com.example.app"),
            )
        }
        assertEquals("Project name cannot contain path separators", error.uiMessage)
    }

    @Test
    fun rejectsInvalidPackage() {
        val error = assertThrows(AppException::class.java) {
            ProjectValidation.validate(
                CreateProjectRequest(name = "MyApp", packageName = "Com.Example"),
            )
        }
        assertEquals(
            "Use a valid Java package (e.g. com.example.app)",
            error.uiMessage,
        )
    }

    @Test
    fun rejectsSingleSegmentPackage() {
        val error = assertThrows(AppException::class.java) {
            ProjectValidation.validate(
                CreateProjectRequest(name = "MyApp", packageName = "example"),
            )
        }
        assertEquals(
            "Use a valid Java package (e.g. com.example.app)",
            error.uiMessage,
        )
    }

    @Test
    fun rejectsOutOfRangeMinSdk() {
        val error = assertThrows(AppException::class.java) {
            ProjectValidation.validate(
                CreateProjectRequest(name = "MyApp", packageName = "com.example.app", minSdk = 14),
            )
        }
        assertEquals("Min SDK must be between 21 and 35", error.uiMessage)
    }
}
