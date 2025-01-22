package com.core.services

import com.core.models.Tool
import com.core.models.ToolConfig

interface ToolServiceInterface {
    fun findToolIdByName(toolName: String): Tool
    fun upsertTool(name: String, description: String?, type: String, config: ToolConfig?): Tool
}