package com.core.requests

import com.core.models.ToolConfig
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class CreateToolRequest(
    val name: String,
    val description: String,
    val config: ToolConfig?
)