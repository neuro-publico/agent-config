package com.core.controllers

import com.core.externals.vectordb.clients.VectordbClient
import com.core.externals.vectordb.requests.QueryRequest
import com.core.externals.vectordb.requests.UpsertDataRequestVectorDB
import com.core.externals.vectordb.responses.SearchResponse
import com.core.models.*
import com.core.requests.CreateAgentConfigRequest
import com.core.requests.SearchAgentRequest
import com.core.services.AgentConfigServiceInterface
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Singleton
@Replaces(VectordbClient::class)
@Requires(env = ["mock"])
class MockVectordbClient : VectordbClient {
    override fun search(providerDB: String, indexName: String, queryRequest: QueryRequest): Mono<List<SearchResponse>> {
        return Mono.just(emptyList())
    }

    override fun upsertData(providerDB: String, indexName: String, upsertDataRequest: UpsertDataRequestVectorDB): Mono<Map<String, String>> {
        return Mono.just(mapOf("status" to "ok"))
    }
}

@Singleton
@Replaces(AgentConfigServiceInterface::class)
@Requires(env = ["mock"])
class MockAgentConfigService : AgentConfigServiceInterface {
    
    private val agents = mutableMapOf<String, AgentConfig>()
    private val agentTools = mutableMapOf<Long, MutableList<Long>>()
    private val promptVersions = mutableListOf<PromptVersion>()
    private var agentIdCounter = 1L
    private var promptVersionIdCounter = 1L
    
    override fun upsertAgentConfig(request: CreateAgentConfigRequest): AgentConfig {
        val existing = agents[request.agentId]
        
        return if (existing != null) {
            if (existing.prompt != request.prompt) {
                promptVersions.add(PromptVersion(
                    id = promptVersionIdCounter++,
                    agentId = existing.agentId,
                    previousPrompt = existing.prompt
                ))
            }
            existing.apply {
                preferences = request.preferences
                prompt = request.prompt
                description = request.description
                providerAI = request.providerAI
                modelAI = request.modelAI
                updatedAt = LocalDateTime.now()
            }
        } else {
            AgentConfig().apply {
                id = agentIdCounter++
                agentId = request.agentId
                preferences = request.preferences
                prompt = request.prompt
                description = request.description
                providerAI = request.providerAI
                modelAI = request.modelAI
                createdAt = LocalDateTime.now()
                updatedAt = LocalDateTime.now()
            }.also { agents[request.agentId] = it }
        }
    }

    override fun addToolToAgent(agentId: String, toolId: Long): AgentTool {
        val agent = agents[agentId] ?: throw IllegalArgumentException("Agent not found")
        val toolList = agentTools.getOrPut(agent.id!!) { mutableListOf() }
        
        if (toolList.contains(toolId)) {
            throw IllegalArgumentException("tool agent already exists")
        }
        
        toolList.add(toolId)
        return AgentTool().apply {
            id = toolList.size.toLong()
            agentConfigId = agent.id!!
            this.toolId = toolId
        }
    }

    override fun removeToolFromAgent(agentId: String, toolId: Long) {
        val agent = agents[agentId] ?: throw IllegalArgumentException("Agent not found")
        agentTools[agent.id]?.remove(toolId)
    }

    override fun getAgent(request: SearchAgentRequest): Mono<AgentConfig> {
        return when {
            request.agentId != null -> {
                val agent = agents[request.agentId] 
                    ?: return Mono.error(IllegalArgumentException("GET Agent not found"))
                Mono.just(agent)
            }
            else -> Mono.error(IllegalArgumentException("GET Agent not found"))
        }
    }

    override fun findAll(): List<AgentConfig> = agents.values.toList()

    override fun getPromptHistory(agentId: String): List<PromptVersion> {
        return promptVersions.filter { it.agentId == agentId }
            .sortedByDescending { it.createdAt }
    }

    override fun revertPromptVersion(agentId: String, promptVersionId: Long): AgentConfig? {
        val version = promptVersions.find { it.id == promptVersionId }
            ?: throw IllegalArgumentException("La versión del prompt no existe.")
            
        if (version.agentId != agentId) {
            throw IllegalArgumentException("La versión del prompt no pertenece al agente especificado.")
        }
        
        val agent = agents[agentId] ?: return null
        agent.prompt = version.previousPrompt ?: ""
        return agent
    }
}

@MicronautTest(environments = ["mock"])
class AgentConfigControllerTest {

    @Inject
    @field:Client("/api/ms/agent")
    lateinit var client: HttpClient

    @Test
    fun `GET config should return list of agents`() {
        val response = client.toBlocking().exchange("/config", String::class.java)
        
        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        // Verify it's a valid JSON array
        assertTrue(response.body()!!.startsWith("["))
    }

    @Test
    fun `POST config should create new agent configuration`() {
        val request = CreateAgentConfigRequest(
            agentId = "new_agent_${System.currentTimeMillis()}",
            preferences = Preference().apply {
                temperature = 0.7
                maxTokens = 500
            },
            description = "Test agent",
            prompt = "You are a helpful assistant",
            providerAI = "openai",
            modelAI = "gpt-4"
        )
        
        val httpRequest = HttpRequest.POST("/config", request)
        val response = client.toBlocking().exchange(httpRequest, AgentConfig::class.java)
        
        assertEquals(HttpStatus.OK, response.status)
        
        val body = response.body()
        assertNotNull(body)
        assertEquals(request.agentId, body?.agentId)
        assertEquals(request.description, body?.description)
        assertEquals(request.prompt, body?.prompt)
    }

    @Test
    fun `POST config should update existing agent configuration`() {
        val agentId = "update_agent_${System.currentTimeMillis()}"
        
        // Create first
        val createRequest = CreateAgentConfigRequest(
            agentId = agentId,
            preferences = Preference(),
            description = "Original description",
            prompt = "Original prompt",
            providerAI = "openai",
            modelAI = "gpt-3.5"
        )
        client.toBlocking().exchange(HttpRequest.POST("/config", createRequest), AgentConfig::class.java)
        
        // Update
        val updateRequest = CreateAgentConfigRequest(
            agentId = agentId,
            preferences = Preference().apply { temperature = 0.9 },
            description = "Updated description",
            prompt = "Updated prompt",
            providerAI = "openai",
            modelAI = "gpt-4"
        )
        
        val response = client.toBlocking().exchange(
            HttpRequest.POST("/config", updateRequest), 
            AgentConfig::class.java
        )
        
        assertEquals(HttpStatus.OK, response.status)
        assertEquals("Updated description", response.body()?.description)
        assertEquals("Updated prompt", response.body()?.prompt)
    }

    @Test
    fun `POST search-agent should return agent by id`() {
        val agentId = "search_agent_${System.currentTimeMillis()}"
        
        // Create agent first
        val createRequest = CreateAgentConfigRequest(
            agentId = agentId,
            preferences = Preference(),
            description = "Searchable agent",
            prompt = "Test prompt"
        )
        client.toBlocking().exchange(HttpRequest.POST("/config", createRequest), AgentConfig::class.java)
        
        // Search by ID
        val searchRequest = SearchAgentRequest(agentId = agentId)
        val response = client.toBlocking().exchange(
            HttpRequest.POST("/config/search-agent", searchRequest),
            AgentConfig::class.java
        )
        
        assertEquals(HttpStatus.OK, response.status)
        assertEquals(agentId, response.body()?.agentId)
    }

    @Test
    fun `POST search-agent should return error for non-existent agent`() {
        val searchRequest = SearchAgentRequest(agentId = "non_existent_agent_xyz_${System.currentTimeMillis()}")
        
        val exception = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(
                HttpRequest.POST("/config/search-agent", searchRequest),
                AgentConfig::class.java
            )
        }
        
        assertTrue(exception.status == HttpStatus.BAD_REQUEST || exception.status == HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `GET prompts history should return list for agent`() {
        val agentId = "history_agent_${System.currentTimeMillis()}"
        
        // Create agent
        val createRequest = CreateAgentConfigRequest(
            agentId = agentId,
            preferences = Preference(),
            description = "Agent for history test",
            prompt = "Initial prompt"
        )
        client.toBlocking().exchange(HttpRequest.POST("/config", createRequest), AgentConfig::class.java)
        
        // Get history
        val response = client.toBlocking().exchange(
            "/config/$agentId/prompts/history",
            String::class.java
        )
        
        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        // Verify it's a valid JSON array
        assertTrue(response.body()!!.startsWith("["))
    }

    @Test
    fun `GET prompts history should return versions after prompt updates`() {
        val agentId = "versioned_agent_${System.currentTimeMillis()}"
        
        // Create agent
        val createRequest = CreateAgentConfigRequest(
            agentId = agentId,
            preferences = Preference(),
            description = "Versioned agent",
            prompt = "Version 1"
        )
        client.toBlocking().exchange(HttpRequest.POST("/config", createRequest), AgentConfig::class.java)
        
        // Update prompt
        val updateRequest = CreateAgentConfigRequest(
            agentId = agentId,
            preferences = Preference(),
            description = "Versioned agent",
            prompt = "Version 2"
        )
        client.toBlocking().exchange(HttpRequest.POST("/config", updateRequest), AgentConfig::class.java)
        
        // Get history
        val response = client.toBlocking().exchange(
            "/config/$agentId/prompts/history",
            String::class.java
        )
        
        assertEquals(HttpStatus.OK, response.status)
        // Verify it's a non-empty JSON array (not just "[]")
        assertTrue(response.body()!!.startsWith("["))
        assertTrue(response.body()!!.length > 2) // More than just "[]"
    }
}
