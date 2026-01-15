package com.core.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PreferenceTest {

    @Test
    fun `Preference should have default values`() {
        val preference = Preference()
        
        assertEquals(0.8, preference.temperature)
        assertEquals(200L, preference.maxTokens)
        assertEquals(1.0, preference.topP)
        assertNull(preference.extraParameters)
    }

    @Test
    fun `Preference should allow custom values`() {
        val preference = Preference().apply {
            temperature = 0.5
            maxTokens = 1000
            topP = 0.9
            extraParameters = mapOf("custom" to "value")
        }
        
        assertEquals(0.5, preference.temperature)
        assertEquals(1000L, preference.maxTokens)
        assertEquals(0.9, preference.topP)
        assertEquals("value", preference.extraParameters?.get("custom"))
    }
}
