package com.core.ai

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GeminiModelPolicyTest {
    @Test
    fun `routes every general Flash family to the approved stable model`() {
        assertEquals("gemini-3.7-flash", GeminiModelPolicy.resolve("gemini"))
        assertEquals("gemini-3.7-flash", GeminiModelPolicy.resolve("gemini-flash-latest"))
        assertEquals("gemini-3.7-flash", GeminiModelPolicy.resolve("gemini-3.6-flash"))
        assertEquals(
            "gemini-3.5-flash-lite",
            GeminiModelPolicy.resolve("gemini-3.1-flash-lite-preview")
        )
    }

    @Test
    fun `does not route specialized Gemini models`() {
        listOf(
            "gemini-3-pro-preview",
            "gemini-3.1-flash-image-preview",
            "gemini-2.5-flash-preview-tts",
            "gemini-live-2.5-flash-preview",
            "gemini-embedding-001"
        ).forEach { model -> assertEquals(model, GeminiModelPolicy.resolve(model)) }
    }
}
