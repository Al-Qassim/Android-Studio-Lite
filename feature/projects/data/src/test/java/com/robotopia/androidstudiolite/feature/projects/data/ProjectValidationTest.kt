package com.robotopia.androidstudiolite.feature.projects.data

import com.robotopia.androidstudiolite.feature.projects.model.CreateProjectRequest
import org.junit.Assert.assertThrows
import org.junit.Test

class ProjectValidationTest {

    @Test
    fun acceptsValidRequest() {
        ProjectValidation.validate(
            CreateProjectRequest(name = "MyApp", packageName = "com.example.myapp"),
        )
    }

    @Test
    fun rejectsBlankName() {
        assertThrows(IllegalArgumentException::class.java) {
            ProjectValidation.validate(
                CreateProjectRequest(name = "  ", packageName = "com.example.app"),
            )
        }
    }

    @Test
    fun rejectsPathSeparatorsInName() {
        assertThrows(IllegalArgumentException::class.java) {
            ProjectValidation.validate(
                CreateProjectRequest(name = "bad/name", packageName = "com.example.app"),
            )
        }
    }

    @Test
    fun rejectsInvalidPackage() {
        assertThrows(IllegalArgumentException::class.java) {
            ProjectValidation.validate(
                CreateProjectRequest(name = "MyApp", packageName = "Com.Example"),
            )
        }
    }

    @Test
    fun rejectsSingleSegmentPackage() {
        assertThrows(IllegalArgumentException::class.java) {
            ProjectValidation.validate(
                CreateProjectRequest(name = "MyApp", packageName = "example"),
            )
        }
    }
}
