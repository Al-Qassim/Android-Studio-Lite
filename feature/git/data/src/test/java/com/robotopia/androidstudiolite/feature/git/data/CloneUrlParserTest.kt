package com.robotopia.androidstudiolite.feature.git.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CloneUrlParserTest {
    @Test
    fun acceptsOwnerRepoAndHttps() {
        val slug = CloneUrlParser.validate("Al-Qassim/Android-Studio-Lite")
        assertTrue(slug.isValid)
        assertEquals(
            "https://github.com/Al-Qassim/Android-Studio-Lite.git",
            slug.normalizedHttpsUrl,
        )

        val https = CloneUrlParser.validate("https://github.com/foo/bar.git")
        assertTrue(https.isValid)
        assertEquals("https://github.com/foo/bar.git", https.normalizedHttpsUrl)
    }

    @Test
    fun rejectsNonGithub() {
        val result = CloneUrlParser.validate("https://gitlab.com/foo/bar")
        assertFalse(result.isValid)
    }

    @Test
    fun prependsHttpsWhenSchemeMissing() {
        val result = CloneUrlParser.validate("github.com/foo/bar")
        assertTrue(result.isValid)
        assertEquals("https://github.com/foo/bar.git", result.normalizedHttpsUrl)

        val www = CloneUrlParser.validate("www.github.com/foo/bar.git")
        assertTrue(www.isValid)
        assertEquals("https://github.com/foo/bar.git", www.normalizedHttpsUrl)
    }
}
