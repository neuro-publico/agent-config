package com.core.controllers

import com.core.models.Tool
import com.core.models.ToolConfig
import com.core.requests.CreateToolRequest
import com.core.services.ToolServiceInterface
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@Singleton
@Replaces(ToolServiceInterface::class)
@Requires(env = ["mock"])
class MockToolService : ToolServiceInterface {
    
    private val tools = mutableMapOf<String, Tool>()
    
    override fun findToolIdByName(toolName: String): Tool {
        return tools[toolName] ?: throw IllegalArgumentException("Tool not found")
    }

    override fun upsertTool(name: String, description: String, config: ToolConfig?): Tool {
        val tool = Tool().apply {
            id = (tools.size + 1).toLong()
            toolName = name
            this.description = description
            this.config = config
            createdAt = LocalDateTime.now()
            updatedAt = LocalDateTime.now()
        }
        tools[name] = tool
        return tool
    }

    override fun findAll(): List<Tool> {
        return tools.values.toList()
    }
}

@MicronautTest(environments = ["mock"])
class ToolControllerTest {

    @Inject
    @field:Client("/api/ms/agent")
    lateinit var client: HttpClient

    @Test
    fun `GET tool should return list of tools`() {
        val response = client.toBlocking().exchange("/tool", String::class.java)
        
        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
    }

    @Test
    fun `POST tool should create new tool`() {
        val request = CreateToolRequest(
            name = "test_tool_${System.currentTimeMillis()}",
            description = "A test tool",
            config = ToolConfig().apply {
                name = "test_tool"
                api = "https://api.test.com"
                method = "GET"
            }
        )
        
        val httpRequest = HttpRequest.POST("/tool", request)
        val response = client.toBlocking().exchange(httpRequest, Tool::class.java)
        
        assertEquals(HttpStatus.CREATED, response.status)
        
        val body = response.body()
        assertNotNull(body)
        assertEquals(request.name, body?.toolName)
        assertEquals(request.description, body?.description)
    }

    @Test
    fun `POST tool should create tool without config`() {
        val request = CreateToolRequest(
            name = "tool_no_config_${System.currentTimeMillis()}",
            description = "Tool without config",
            config = null
        )
        
        val httpRequest = HttpRequest.POST("/tool", request)
        val response = client.toBlocking().exchange(httpRequest, Tool::class.java)
        
        assertEquals(HttpStatus.CREATED, response.status)
        
        val body = response.body()
        assertNotNull(body)
        assertNull(body?.config)
    }
}
