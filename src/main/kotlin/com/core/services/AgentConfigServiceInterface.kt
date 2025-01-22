package com.core.services

import com.core.models.AgentConfig
import com.core.models.AgentTool
import com.core.models.Preference

interface AgentConfigServiceInterface {
    fun upsertAgentConfig(agentId: String, preferences: Preference?): AgentConfig
    fun addToolToAgent(agentId: String, toolId: Long): AgentTool
    fun removeToolFromAgent(agentId: String, toolId: Long)
}