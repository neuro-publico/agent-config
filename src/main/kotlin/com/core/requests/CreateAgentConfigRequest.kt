package com.core.requests

import com.core.models.Preference
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class CreateAgentConfigRequest(
    @field:JsonProperty("agent_id")
    val agentId: String,
    val preferences: Preference,
    @field:JsonProperty("description")
    var description: String,
    @field:JsonProperty("index_name")
    var indexName: String = "startup",

    @field:JsonProperty("namespace")
    var namespace: String = "agents",

    @field:JsonProperty("metadata")
    var metadata: Map<String, Any>? = null,

    @field:JsonProperty("provider_vector_db")
    var providerVectorDB: String = "pinecone",

    @field:JsonProperty("prompt")
    var prompt: String = "",

    @field:JsonProperty("provider_ai")
    var providerAI: String = "",

    @field:JsonProperty("model_ai")
    var modelAI: String = ""
)