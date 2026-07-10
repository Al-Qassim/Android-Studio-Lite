package com.robotopia.androidstudiolite.feature.editor.data

import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultEditorSessionTest {
    private val session = DefaultEditorSession()
    private val documentId = DocumentId(ProjectId("p1"), "src/Main.kt")

    @Test
    fun open_setsCleanDocument() {
        session.open(documentId, "hello")

        val doc = session.document.value!!
        assertEquals(documentId, doc.id)
        assertEquals("hello", doc.content)
        assertFalse(doc.isDirty)
    }

    @Test
    fun updateContent_marksDirty() {
        session.open(documentId, "hello")
        session.updateContent("hello world")

        val doc = session.document.value!!
        assertEquals("hello world", doc.content)
        assertTrue(doc.isDirty)
    }

    @Test
    fun markSaved_clearsDirty() {
        session.open(documentId, "hello")
        session.updateContent("edited")
        session.markSaved("edited")

        val doc = session.document.value!!
        assertEquals("edited", doc.content)
        assertFalse(doc.isDirty)
    }

    @Test
    fun close_clearsDocument() {
        session.open(documentId, "hello")
        session.close()

        assertNull(session.document.value)
    }

    @Test
    fun updateContent_ignoredWhenClosed() {
        session.updateContent("orphan")
        assertNull(session.document.value)
    }
}
