package com.robotopia.androidstudiolite.feature.buildapk.data.github

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class GitHubResumePayloadTest {
    @Test
    fun encodeDecode_roundTripsFields() {
        val payload = GitHubResumePayload(
            owner = "alice",
            repo = "asl-builds",
            runId = 42L,
            releaseId = 99L,
            releaseTag = "asl-build-job1",
            uploadUrlTemplate = "https://uploads.example/{?name}",
        )
        val decoded = GitHubResumePayload.decode(payload.encode())
        assertNotNull(decoded)
        assertEquals(payload, decoded)
    }

    @Test
    fun decode_blank_returnsNull() {
        assertNull(GitHubResumePayload.decode(null))
        assertNull(GitHubResumePayload.decode(""))
        assertNull(GitHubResumePayload.decode("not-json"))
    }
}
