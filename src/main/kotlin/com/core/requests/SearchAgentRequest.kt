package com.core.requests

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class SearchAgentRequest(
    @field:JsonProperty("agent_id")
    val agentId: String? = null,
    val query: String? = null,
    @field:JsonProperty("metadata_filter")
    val metadataFilter: List<Map<String, Any>>? = null,
    @field:JsonProperty("parameter_prompt")
    val parameterPrompt: Map<String, Any>? = null
)