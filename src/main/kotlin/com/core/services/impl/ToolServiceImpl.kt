package com.core.services.impl

import com.core.models.Tool
import com.core.models.ToolConfig
import com.core.repositories.ToolRepository
import com.core.services.ToolServiceInterface
import jakarta.inject.Singleton
import java.time.LocalDateTime

@Singleton
class ToolServiceImpl(
    private val toolRepository: ToolRepository
) : ToolServiceInterface {

    override fun findToolIdByName(toolName: String): Tool {
        return this.toolRepository.findByToolName(toolName) ?: throw IllegalArgumentException("Tool not found")
    }

    override fun upsertTool(name: String, description: String, config: ToolConfig?): Tool {
        val existingTool = toolRepository.findByToolName(name)

        return if (existingTool != null) {
            existingTool.apply {
                this.description = description
                this.config = config
                this.updatedAt = LocalDateTime.now()
            }
            this.toolRepository.update(existingTool)
        } else {
            this.toolRepository.save(Tool().apply {
                this.toolName = name
                this.description = description
                this.config = config
            })
        }
    }
}