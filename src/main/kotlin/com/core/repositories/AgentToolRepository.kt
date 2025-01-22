package com.core.repositories

import com.core.models.AgentTool
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface AgentToolRepository : CrudRepository<AgentTool, Long> {
    fun findByAgentConfigIdAndToolId(agentConfigId: Long, toolId: Long): AgentTool?
    fun deleteByAgentConfigIdAndToolId(agentConfigId: Long, toolId: Long)
}