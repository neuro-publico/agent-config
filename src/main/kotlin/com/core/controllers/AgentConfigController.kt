package com.core.controllers

import com.core.models.AgentConfig
import com.core.models.AgentTool
import com.core.requests.CreateAgentConfigRequest
import com.core.services.AgentConfigServiceInterface
import com.core.services.ToolServiceInterface
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import com.core.externals.vectordb.clients.VectordbClient
import com.core.requests.SearchAgentRequest
import io.micronaut.http.annotation.QueryValue
import reactor.core.publisher.Mono

@Controller("/config")
@ExecuteOn(TaskExecutors.IO)
class AgentConfigController(
    private val agentConfigService: AgentConfigServiceInterface,
    private val toolServiceInterface: ToolServiceInterface,
    private val vectordbClient: VectordbClient
) {
    @Get
    fun findAll(): List<AgentConfig> {
        return this.agentConfigService.findAll()
    }

    @Post
    fun upsertAgentConfig(@Body request: CreateAgentConfigRequest): HttpResponse<AgentConfig> {
        val agentConfig = this.agentConfigService.upsertAgentConfig(request)
        return HttpResponse.ok(agentConfig)
    }

    @Post("/{agentId}/tools/{toolName}")
    fun addToolToAgent(
        @PathVariable agentId: String,
        @PathVariable toolName: String
    ): HttpResponse<AgentTool> {
        val tool = this.toolServiceInterface.findToolIdByName(toolName)
        val agentTool = this.agentConfigService.addToolToAgent(agentId, tool.id!!)
        return HttpResponse.created(agentTool)
    }

    @Delete("/{agentId}/tools/{toolName}")
    fun removeToolFromAgent(@PathVariable agentId: String, @PathVariable toolName: String): HttpResponse<Unit> {
        val tool = this.toolServiceInterface.findToolIdByName(toolName)
        this.agentConfigService.removeToolFromAgent(agentId, tool.id!!)

        return HttpResponse.ok()
    }

    @Post("/search-agent")
    fun searchAgent(@Body request: SearchAgentRequest): Mono<AgentConfig> {
        return this.agentConfigService.getAgent(request)
    }
}

