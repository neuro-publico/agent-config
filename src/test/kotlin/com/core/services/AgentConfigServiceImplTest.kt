package com.core.services

import com.core.externals.vectordb.clients.VectordbClient
import com.core.externals.vectordb.requests.UpsertDataRequestVectorDB
import com.core.models.*
import com.core.repositories.AgentConfigRepository
import com.core.repositories.AgentToolRepository
import com.core.repositories.PromptVersionRepository
import com.core.repositories.ToolRepository
import com.core.requests.CreateAgentConfigRequest
import com.core.requests.SearchAgentRequest
import com.core.services.impl.AgentConfigServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import reactor.core.publisher.Mono
import java.util.*

class AgentConfigServiceImplTest {

    private lateinit var agentConfigRepository: AgentConfigRepository
    private lateinit var agentToolRepository: AgentToolRepository
    private lateinit var vectordbClient: VectordbClient
    private lateinit var toolRepository: ToolRepository
    private lateinit var promptVersionRepository: PromptVersionRepository
    private lateinit var agentConfigService: AgentConfigServiceImpl

    @BeforeEach
    fun setUp() {
        agentConfigRepository = mock()
        agentToolRepository = mock()
        vectordbClient = mock()
        toolRepository = mock()
        promptVersionRepository = mock()
        
        agentConfigService = AgentConfigServiceImpl(
            agentConfigRepository,
            agentToolRepository,
            vectordbClient,
            toolRepository,
            promptVersionRepository
        )

        // Default mock for vectordbClient.upsertData
        whenever(vectordbClient.upsertData(any(), any(), any<UpsertDataRequestVectorDB>())).thenReturn(
            Mono.just(mapOf("status" to "ok"))
        )
    }

    private fun createDefaultRequest(
        agentId: String = "test_agent",
        prompt: String = "Test prompt"
    ): CreateAgentConfigRequest {
        return CreateAgentConfigRequest(
            agentId = agentId,
            preferences = Preference().apply {
                temperature = 0.8
                maxTokens = 200
            },
            description = "Test description",
            prompt = prompt,
            providerAI = "openai",
            modelAI = "gpt-4"
        )
    }

    @Test
    fun `upsertAgentConfig should create new agent when not exists`() {
        val request = createDefaultRequest()
        
        whenever(agentConfigRepository.findByAgentId("test_agent")).thenReturn(null)
        whenever(agentConfigRepository.save(any<AgentConfig>())).thenAnswer { invocation ->
            (invocation.getArgument(0) as AgentConfig).apply { id = 1L }
        }

        val result = agentConfigService.upsertAgentConfig(request)

        assertEquals("test_agent", result.agentId)
        assertEquals("Test description", result.description)
        assertEquals("Test prompt", result.prompt)
        assertEquals("openai", result.providerAI)
        assertEquals("gpt-4", result.modelAI)
        
        verify(agentConfigRepository).findByAgentId("test_agent")
        verify(agentConfigRepository).save(any<AgentConfig>())
        verify(agentConfigRepository, never()).update(any())
    }

    @Test
    fun `upsertAgentConfig should update existing agent when exists`() {
        val request = createDefaultRequest(prompt = "Updated prompt")
        
        val existingAgent = AgentConfig().apply {
            id = 1L
            agentId = "test_agent"
            description = "Old description"
            prompt = "Old prompt"
            providerAI = "bedrock"
            modelAI = "claude-2"
        }

        whenever(agentConfigRepository.findByAgentId("test_agent")).thenReturn(existingAgent)
        whenever(agentConfigRepository.update(any<AgentConfig>())).thenAnswer { invocation ->
            invocation.getArgument(0) as AgentConfig
        }

        val result = agentConfigService.upsertAgentConfig(request)

        assertEquals(1L, result.id)
        assertEquals("Test description", result.description)
        assertEquals("Updated prompt", result.prompt)
        
        verify(agentConfigRepository).findByAgentId("test_agent")
        verify(agentConfigRepository).update(any())
        verify(agentConfigRepository, never()).save(any<AgentConfig>())
    }

    @Test
    fun `upsertAgentConfig should save prompt version when prompt changes`() {
        val request = createDefaultRequest(prompt = "New prompt")
        
        val existingAgent = AgentConfig().apply {
            id = 1L
            agentId = "test_agent"
            prompt = "Old prompt"
        }

        whenever(agentConfigRepository.findByAgentId("test_agent")).thenReturn(existingAgent)
        whenever(agentConfigRepository.update(any<AgentConfig>())).thenAnswer { invocation ->
            invocation.getArgument(0) as AgentConfig
        }

        agentConfigService.upsertAgentConfig(request)

        verify(promptVersionRepository).save(any())
    }

    @Test
    fun `addToolToAgent should create agent-tool association`() {
        val agentConfig = AgentConfig().apply {
            id = 1L
            agentId = "test_agent"
        }
        
        whenever(agentConfigRepository.findByAgentId("test_agent")).thenReturn(agentConfig)
        whenever(agentToolRepository.findByAgentConfigIdAndToolId(1L, 5L)).thenReturn(null)
        whenever(agentToolRepository.save(any<AgentTool>())).thenAnswer { invocation ->
            (invocation.getArgument(0) as AgentTool).apply { id = 1L }
        }

        val result = agentConfigService.addToolToAgent("test_agent", 5L)

        assertEquals(1L, result.agentConfigId)
        assertEquals(5L, result.toolId)
        verify(agentToolRepository).save(any())
    }

    @Test
    fun `addToolToAgent should throw exception when agent not found`() {
        whenever(agentConfigRepository.findByAgentId("non_existent")).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            agentConfigService.addToolToAgent("non_existent", 1L)
        }

        assertEquals("Agent not found", exception.message)
    }

    @Test
    fun `addToolToAgent should throw exception when association already exists`() {
        val agentConfig = AgentConfig().apply {
            id = 1L
            agentId = "test_agent"
        }
        val existingAgentTool = AgentTool().apply {
            id = 1L
            agentConfigId = 1L
            toolId = 5L
        }
        
        whenever(agentConfigRepository.findByAgentId("test_agent")).thenReturn(agentConfig)
        whenever(agentToolRepository.findByAgentConfigIdAndToolId(1L, 5L)).thenReturn(existingAgentTool)

        val exception = assertThrows<IllegalArgumentException> {
            agentConfigService.addToolToAgent("test_agent", 5L)
        }

        assertEquals("tool agent already exists", exception.message)
    }

    @Test
    fun `removeToolFromAgent should delete association`() {
        val agentConfig = AgentConfig().apply {
            id = 1L
            agentId = "test_agent"
        }
        
        whenever(agentConfigRepository.findByAgentId("test_agent")).thenReturn(agentConfig)

        agentConfigService.removeToolFromAgent("test_agent", 5L)

        verify(agentToolRepository).deleteByAgentConfigIdAndToolId(1L, 5L)
    }

    @Test
    fun `removeToolFromAgent should throw exception when agent not found`() {
        whenever(agentConfigRepository.findByAgentId("non_existent")).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            agentConfigService.removeToolFromAgent("non_existent", 1L)
        }

        assertEquals("Agent not found", exception.message)
    }

    @Test
    fun `getAgent should return agent by agentId`() {
        val request = SearchAgentRequest(agentId = "test_agent")
        val agentConfig = AgentConfig().apply {
            id = 1L
            agentId = "test_agent"
            prompt = "Hello {name}"
        }
        
        whenever(agentConfigRepository.findByAgentId("test_agent")).thenReturn(agentConfig)
        whenever(agentToolRepository.findByAgentConfigId(1L)).thenReturn(emptyList())
        whenever(toolRepository.findByIdIn(emptyList())).thenReturn(emptyList())

        val result = agentConfigService.getAgent(request).block()

        assertNotNull(result)
        assertEquals("test_agent", result?.agentId)
    }

    @Test
    fun `getAgent should replace placeholders in prompt`() {
        val request = SearchAgentRequest(
            agentId = "test_agent",
            parameterPrompt = mapOf("name" to "Carlos")
        )
        val agentConfig = AgentConfig().apply {
            id = 1L
            agentId = "test_agent"
            prompt = "Hello {name}, welcome!"
        }
        
        whenever(agentConfigRepository.findByAgentId("test_agent")).thenReturn(agentConfig)
        whenever(agentToolRepository.findByAgentConfigId(1L)).thenReturn(emptyList())
        whenever(toolRepository.findByIdIn(emptyList())).thenReturn(emptyList())

        val result = agentConfigService.getAgent(request).block()

        assertEquals("Hello Carlos, welcome!", result?.prompt)
    }

    @Test
    fun `getAgent should include tools`() {
        val request = SearchAgentRequest(agentId = "test_agent")
        val agentConfig = AgentConfig().apply {
            id = 1L
            agentId = "test_agent"
            prompt = "Test prompt"
        }
        val agentTools = listOf(
            AgentTool().apply { toolId = 1L },
            AgentTool().apply { toolId = 2L }
        )
        val tools = listOf(
            Tool().apply { id = 1L; toolName = "tool1" },
            Tool().apply { id = 2L; toolName = "tool2" }
        )
        
        whenever(agentConfigRepository.findByAgentId("test_agent")).thenReturn(agentConfig)
        whenever(agentToolRepository.findByAgentConfigId(1L)).thenReturn(agentTools)
        whenever(toolRepository.findByIdIn(listOf(1L, 2L))).thenReturn(tools)

        val result = agentConfigService.getAgent(request).block()

        assertNotNull(result?.tools)
        assertEquals(2, result?.tools?.size)
    }

    @Test
    fun `getAgent should throw exception when agent not found by id`() {
        val request = SearchAgentRequest(agentId = "non_existent")
        
        whenever(agentConfigRepository.findByAgentId("non_existent")).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            agentConfigService.getAgent(request).block()
        }

        assertEquals("GET Agent not found", exception.message)
    }

    @Test
    fun `getAgent should throw exception when no search criteria provided`() {
        val request = SearchAgentRequest()

        val exception = assertThrows<IllegalArgumentException> {
            agentConfigService.getAgent(request).block()
        }

        assertEquals("GET Agent not found", exception.message)
    }

    @Test
    fun `findAll should return all agents`() {
        val agents = listOf(
            AgentConfig().apply { id = 1L; agentId = "agent1" },
            AgentConfig().apply { id = 2L; agentId = "agent2" }
        )
        
        whenever(agentConfigRepository.findAll()).thenReturn(agents)

        val result = agentConfigService.findAll()

        assertEquals(2, result.size)
        assertEquals("agent1", result[0].agentId)
        assertEquals("agent2", result[1].agentId)
    }

    @Test
    fun `getPromptHistory should return prompt versions ordered by date desc`() {
        val versions = listOf(
            PromptVersion(id = 2L, agentId = "test_agent", previousPrompt = "Version 2"),
            PromptVersion(id = 1L, agentId = "test_agent", previousPrompt = "Version 1")
        )
        
        whenever(promptVersionRepository.findByAgentIdOrderByCreatedAtDesc("test_agent")).thenReturn(versions)

        val result = agentConfigService.getPromptHistory("test_agent")

        assertEquals(2, result.size)
        assertEquals("Version 2", result[0].previousPrompt)
        assertEquals("Version 1", result[1].previousPrompt)
    }

    @Test
    fun `revertPromptVersion should revert to previous version`() {
        val promptVersion = PromptVersion(
            id = 1L,
            agentId = "test_agent",
            previousPrompt = "Old prompt to revert"
        )
        val agentConfig = AgentConfig().apply {
            id = 1L
            agentId = "test_agent"
            prompt = "Current prompt"
        }
        
        whenever(promptVersionRepository.findById(1L)).thenReturn(Optional.of(promptVersion))
        whenever(agentConfigRepository.findByAgentId("test_agent")).thenReturn(agentConfig)
        whenever(agentConfigRepository.update(any<AgentConfig>())).thenAnswer { invocation ->
            invocation.getArgument(0) as AgentConfig
        }

        val result = agentConfigService.revertPromptVersion("test_agent", 1L)

        assertNotNull(result)
        assertEquals("Old prompt to revert", result?.prompt)
        verify(promptVersionRepository).save(any()) // Should save current as new version
    }

    @Test
    fun `revertPromptVersion should throw exception when version not found`() {
        whenever(promptVersionRepository.findById(999L)).thenReturn(Optional.empty())

        val exception = assertThrows<IllegalArgumentException> {
            agentConfigService.revertPromptVersion("test_agent", 999L)
        }

        assertEquals("La versión del prompt no existe.", exception.message)
    }

    @Test
    fun `revertPromptVersion should throw exception when version belongs to different agent`() {
        val promptVersion = PromptVersion(
            id = 1L,
            agentId = "other_agent",
            previousPrompt = "Some prompt"
        )
        
        whenever(promptVersionRepository.findById(1L)).thenReturn(Optional.of(promptVersion))

        val exception = assertThrows<IllegalArgumentException> {
            agentConfigService.revertPromptVersion("test_agent", 1L)
        }

        assertEquals("La versión del prompt no pertenece al agente especificado.", exception.message)
    }
}
