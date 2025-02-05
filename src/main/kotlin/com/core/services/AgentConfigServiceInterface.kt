package com.core.services

import com.core.models.AgentConfig
import com.core.models.AgentTool
import com.core.requests.CreateAgentConfigRequest
import com.core.requests.SearchAgentRequest
import reactor.core.publisher.Mono

interface AgentConfigServiceInterface {
    fun upsertAgentConfig(request: CreateAgentConfigRequest): AgentConfig
    fun addToolToAgent(agentId: String, toolId: Long): AgentTool
    fun removeToolFromAgent(agentId: String, toolId: Long)
    fun getAgent(request: SearchAgentRequest): Mono<AgentConfig>
    fun findAll(): List<AgentConfig>
}