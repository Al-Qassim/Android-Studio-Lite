package com.robotopia.androidstudiolite.feature.editor.presentation.editor

import org.junit.Assert.assertEquals
import org.junit.Test

class FileNameFromPathTest {
    @Test
    fun fileNameFromPath_returnsLastSegment() {
        assertEquals("MainActivity.kt", fileNameFromPath("app/src/main/java/MainActivity.kt"))
        assertEquals("notes.txt", fileNameFromPath("notes.txt"))
        assertEquals("", fileNameFromPath(""))
    }
}
