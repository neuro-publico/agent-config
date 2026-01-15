package com.core.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ToolConfigTest {

    @Test
    fun `ToolConfig should have default values`() {
        val config = ToolConfig()
        
        assertEquals("", config.name)
        assertEquals("", config.description)
        assertEquals("", config.api)
        assertEquals("", config.method)
        assertTrue(config.properties.isEmpty())
        assertTrue(config.body.isEmpty())
        assertTrue(config.headers.isEmpty())
        assertTrue(config.queryParams.isEmpty())
        assertNull(config.fakeResponse)
    }

    @Test
    fun `ToolConfig should allow full configuration`() {
        val config = ToolConfig().apply {
            name = "search_api"
            description = "Search products API"
            api = "https://api.example.com/search"
            method = "POST"
            properties = listOf(
                Property().apply { 
                    name = "query"
                    description = "Search query"
                }
            )
            headers = listOf(mapOf("Authorization" to "Bearer token"))
            queryParams = mapOf("limit" to "10")
            body = mapOf("filter" to "active")
            fakeResponse = mapOf("result" to "[]")
        }
        
        assertEquals("search_api", config.name)
        assertEquals("Search products API", config.description)
        assertEquals("https://api.example.com/search", config.api)
        assertEquals("POST", config.method)
        assertEquals(1, config.properties.size)
        assertEquals("query", config.properties[0].name)
        assertEquals(1, config.headers.size)
        assertEquals("10", config.queryParams["limit"])
        assertEquals("active", config.body["filter"])
        assertEquals("[]", config.fakeResponse?.get("result"))
    }

    @Test
    fun `Property should have default values`() {
        val property = Property()
        
        assertEquals("", property.name)
        assertEquals("", property.description)
    }

    @Test
    fun `Property should allow custom values`() {
        val property = Property().apply {
            name = "user_id"
            description = "The user identifier"
        }
        
        assertEquals("user_id", property.name)
        assertEquals("The user identifier", property.description)
    }
}
