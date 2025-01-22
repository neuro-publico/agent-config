package com.core.requests

import com.core.models.Preference
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class CreateAgentConfigRequest(
    @field:JsonProperty("agent_id")
    val agentId: String,
    val preferences: Preference?
)