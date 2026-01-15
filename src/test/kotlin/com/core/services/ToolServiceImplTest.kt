package com.core.services

import com.core.models.Tool
import com.core.models.ToolConfig
import com.core.repositories.ToolRepository
import com.core.services.impl.ToolServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.time.LocalDateTime

class ToolServiceImplTest {

    private lateinit var toolRepository: ToolRepository
    private lateinit var toolService: ToolServiceImpl

    @BeforeEach
    fun setUp() {
        toolRepository = mock()
        toolService = ToolServiceImpl(toolRepository)
    }

    @Test
    fun `findToolIdByName should return tool when exists`() {
        val expectedTool = Tool().apply {
            id = 1L
            toolName = "search_products"
            description = "Search products in catalog"
        }
        whenever(toolRepository.findByToolName("search_products")).thenReturn(expectedTool)

        val result = toolService.findToolIdByName("search_products")

        assertEquals(expectedTool, result)
        assertEquals("search_products", result.toolName)
        verify(toolRepository).findByToolName("search_products")
    }

    @Test
    fun `findToolIdByName should throw exception when tool not found`() {
        whenever(toolRepository.findByToolName("non_existent")).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            toolService.findToolIdByName("non_existent")
        }

        assertEquals("Tool not found", exception.message)
        verify(toolRepository).findByToolName("non_existent")
    }

    @Test
    fun `upsertTool should create new tool when not exists`() {
        val toolName = "new_tool"
        val description = "A new tool"
        val config = ToolConfig().apply {
            name = toolName
            api = "https://api.example.com"
            method = "GET"
        }

        whenever(toolRepository.findByToolName(toolName)).thenReturn(null)
        whenever(toolRepository.save(any<Tool>())).thenAnswer { invocation ->
            (invocation.getArgument(0) as Tool).apply { id = 1L }
        }

        val result = toolService.upsertTool(toolName, description, config)

        assertEquals(toolName, result.toolName)
        assertEquals(description, result.description)
        assertEquals(config, result.config)
        verify(toolRepository).findByToolName(toolName)
        verify(toolRepository).save(any())
        verify(toolRepository, never()).update(any())
    }

    @Test
    fun `upsertTool should update existing tool when exists`() {
        val toolName = "existing_tool"
        val newDescription = "Updated description"
        val newConfig = ToolConfig().apply {
            name = toolName
            api = "https://api.new.com"
            method = "POST"
        }

        val existingTool = Tool().apply {
            id = 1L
            this.toolName = toolName
            description = "Old description"
            config = ToolConfig().apply {
                name = toolName
                api = "https://api.old.com"
                method = "GET"
            }
            createdAt = LocalDateTime.now().minusDays(1)
        }

        whenever(toolRepository.findByToolName(toolName)).thenReturn(existingTool)
        whenever(toolRepository.update(any<Tool>())).thenAnswer { invocation ->
            invocation.getArgument(0) as Tool
        }

        val result = toolService.upsertTool(toolName, newDescription, newConfig)

        assertEquals(1L, result.id)
        assertEquals(newDescription, result.description)
        assertEquals(newConfig, result.config)
        verify(toolRepository).findByToolName(toolName)
        verify(toolRepository).update(any())
        verify(toolRepository, never()).save(any<Tool>())
    }

    @Test
    fun `upsertTool should handle null config`() {
        val toolName = "tool_without_config"
        val description = "Tool without config"

        whenever(toolRepository.findByToolName(toolName)).thenReturn(null)
        whenever(toolRepository.save(any<Tool>())).thenAnswer { invocation ->
            (invocation.getArgument(0) as Tool).apply { id = 1L }
        }

        val result = toolService.upsertTool(toolName, description, null)

        assertEquals(toolName, result.toolName)
        assertEquals(description, result.description)
        assertNull(result.config)
        verify(toolRepository).save(any())
    }

    @Test
    fun `findAll should return all tools`() {
        val tools = listOf(
            Tool().apply {
                id = 1L
                toolName = "tool1"
                description = "First tool"
            },
            Tool().apply {
                id = 2L
                toolName = "tool2"
                description = "Second tool"
            }
        )

        whenever(toolRepository.findAll()).thenReturn(tools)

        val result = toolService.findAll()

        assertEquals(2, result.size)
        assertEquals("tool1", result[0].toolName)
        assertEquals("tool2", result[1].toolName)
        verify(toolRepository).findAll()
    }

    @Test
    fun `findAll should return empty list when no tools exist`() {
        whenever(toolRepository.findAll()).thenReturn(emptyList())

        val result = toolService.findAll()

        assertTrue(result.isEmpty())
        verify(toolRepository).findAll()
    }
}
