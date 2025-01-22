package com.core.services.impl

import com.core.models.AgentConfig
import com.core.models.AgentTool
import com.core.models.Preference
import com.core.repositories.AgentToolRepository
import com.core.repositories.ToolRepository
import com.core.repository.AgentConfigRepository
import com.core.services.AgentConfigServiceInterface
import jakarta.inject.Singleton
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Singleton
class AgentConfigServiceImpl(
    private val agentConfigRepository: AgentConfigRepository,
    private val toolRepository: ToolRepository,
    private val agentToolRepository: AgentToolRepository
) : AgentConfigServiceInterface {

    override fun upsertAgentConfig(agentId: String, preferences: Preference?): AgentConfig {
        val existingConfig = this.agentConfigRepository.findByAgentId(agentId)

        return if (existingConfig != null) {
            // Actualizar preferencias y fecha de actualización
            existingConfig.preferences = preferences
            existingConfig.updatedAt = LocalDateTime.now()
            this.agentConfigRepository.update(existingConfig)
        } else {
            // Crear nueva configuración
            val newConfig = AgentConfig().apply {
                this.agentId = agentId
                this.preferences = preferences
            }
            this.agentConfigRepository.save(newConfig)
        }
    }

    override fun addToolToAgent(agentId: String, toolId: Long): AgentTool {
        val agentConfig = this.agentConfigRepository.findByAgentId(agentId)
  ?: throw IllegalArgumentException("Agent not found")
        val agentToolExists = this.agentToolRepository.findByAgentConfigIdAndToolId(agentConfig.id!!, toolId)
        if (agentToolExists != null) throw IllegalArgumentException("tool agent already exists")
        print(agentToolExists)
        val agentTool = AgentTool().apply {
            this.agentConfigId = agentConfig.id!!;
            this.toolId = toolId
        }

        return this.agentToolRepository.save(agentTool)
    }

    override  fun removeToolFromAgent(agentId: String, toolId: Long) {
        val agentConfig = this.agentConfigRepository.findByAgentId(agentId) ?: throw IllegalArgumentException("Agent not found")
        this.agentToolRepository.deleteByAgentConfigIdAndToolId(agentConfig.id!!, toolId)
    }
}