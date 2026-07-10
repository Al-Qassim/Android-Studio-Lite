package com.robotopia.androidstudiolite.feature.files.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ParentRelativePathOrNullTest {
    @Test
    fun rootReturnsNull() {
        assertNull(parentRelativePathOrNull(""))
    }

    @Test
    fun topLevelReturnsEmptyRoot() {
        assertEquals("", parentRelativePathOrNull("app"))
    }

    @Test
    fun nestedReturnsParent() {
        assertEquals("app/src", parentRelativePathOrNull("app/src/main"))
    }
}
