package com.core.models

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class AgentConfigTest {

    @Test
    fun `AgentConfig should have default values`() {
        val config = AgentConfig()
        
        assertNull(config.id)
        assertEquals("", config.agentId)
        assertEquals("", config.description)
        assertEquals("startup", config.indexName)
        assertEquals("agents", config.namespace)
        assertEquals("default", config.project)
        assertEquals("pinecone", config.providerVectorDB)
        assertEquals("", config.prompt)
        assertEquals("", config.providerAI)
        assertEquals("", config.modelAI)
        assertNotNull(config.preferences)
        assertNull(config.metadata)
        assertNull(config.mcpConfig)
        assertNull(config.tools)
    }

    @Test
    fun `AgentConfig should allow full configuration`() {
        val now = LocalDateTime.now()
        val config = AgentConfig().apply {
            id = 1L
            agentId = "test_agent"
            description = "Test agent description"
            indexName = "custom_index"
            namespace = "custom_namespace"
            project = "my_project"
            providerVectorDB = "pinecone"
            prompt = "You are a helpful assistant"
            providerAI = "openai"
            modelAI = "gpt-4"
            preferences = Preference().apply {
                temperature = 0.7
                maxTokens = 500
            }
            metadata = mapOf("key" to "value")
            mcpConfig = mapOf("server" to "localhost")
            createdAt = now
            updatedAt = now
            tools = listOf(Tool().apply { toolName = "search" })
        }
        
        assertEquals(1L, config.id)
        assertEquals("test_agent", config.agentId)
        assertEquals("Test agent description", config.description)
        assertEquals("custom_index", config.indexName)
        assertEquals("custom_namespace", config.namespace)
        assertEquals("my_project", config.project)
        assertEquals("pinecone", config.providerVectorDB)
        assertEquals("You are a helpful assistant", config.prompt)
        assertEquals("openai", config.providerAI)
        assertEquals("gpt-4", config.modelAI)
        assertEquals(0.7, config.preferences.temperature)
        assertEquals(500L, config.preferences.maxTokens)
        assertEquals("value", config.metadata?.get("key"))
        assertEquals("localhost", config.mcpConfig?.get("server"))
        assertEquals(1, config.tools?.size)
        assertEquals("search", config.tools?.get(0)?.toolName)
    }
}
