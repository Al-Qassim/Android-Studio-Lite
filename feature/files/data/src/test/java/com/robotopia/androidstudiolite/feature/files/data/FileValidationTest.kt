package com.robotopia.androidstudiolite.feature.files.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FileValidationTest {
    @Test
    fun fieldErrors_requiresName() {
        assertEquals("Name is required", FileValidation.fieldErrors("").name)
        assertEquals("Name is required", FileValidation.fieldErrors("   ").name)
    }

    @Test
    fun fieldErrors_rejectsReservedNames() {
        assertEquals("Name cannot be . or ..", FileValidation.fieldErrors(".").name)
        assertEquals("Name cannot be . or ..", FileValidation.fieldErrors("..").name)
    }

    @Test
    fun fieldErrors_rejectsPathSeparatorsAndWildcards() {
        assertEquals("Name contains invalid characters", FileValidation.fieldErrors("a/b").name)
        assertEquals("Name contains invalid characters", FileValidation.fieldErrors("a\\b").name)
        assertEquals("Name contains invalid characters", FileValidation.fieldErrors("a*b").name)
    }

    @Test
    fun fieldErrors_acceptsValidNames() {
        val errors = FileValidation.fieldErrors("MainActivity.kt")
        assertNull(errors.name)
        assertFalse(errors.hasErrors)
    }

    @Test
    fun fieldErrors_rejectsVeryLongNames() {
        val longName = "a".repeat(256)
        assertEquals("Name is too long", FileValidation.fieldErrors(longName).name)
        assertTrue(FileValidation.fieldErrors("a".repeat(255)).hasErrors.not())
    }
}
