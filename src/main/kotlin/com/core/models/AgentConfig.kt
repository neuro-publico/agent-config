package com.core.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import java.time.LocalDateTime

@Serdeable
@Entity(name = "agent_configs")
class AgentConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agent_config_seq_generator")
    @SequenceGenerator(name = "agent_config_seq_generator", sequenceName = "agent_configs_id_seq", allocationSize = 1)
    var id: Long? = null

    @Column(name = "agent_id")
    @field:JsonProperty("agent_id")
    var agentId: String = ""

    @Column(name = "description")
    @field:JsonProperty("description")
    var description: String = ""

    @Column(name = "index_name")
    @field:JsonProperty("index_name")
    var indexName: String = "startup"

    @Column(name = "namespace")
    @field:JsonProperty("namespace")
    var namespace: String = "agents"

    @Column(name = "project")
    @field:JsonProperty("project")
    var project: String? = "default"

    @Column(name = "provider_vector_db")
    @field:JsonProperty("provider_vector_db")
    var providerVectorDB: String = "pinecone"

    @Column(name = "prompt")
    @field:JsonProperty("prompt")
    var prompt: String = ""

    @Column(name = "provider_ai")
    @field:JsonProperty("provider_ai")
    var providerAI: String = ""

    @Column(name = "model_ai")
    @field:JsonProperty("model_ai")
    var modelAI: String = ""

    @Type(value = JsonBinaryType::class)
    @Column(name = "preferences", columnDefinition = "jsonb")
    var preferences: Preference = Preference()

    @Column(name = "created_at")
    @field:JsonProperty("created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at")
    @field:JsonProperty("updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @Type(value = JsonBinaryType::class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    var metadata: Map<String, Any>? = null

    @Type(value = JsonBinaryType::class)
    @field:JsonProperty("mcp_config")
    @Column(name = "mcp_config", columnDefinition = "jsonb")
    var mcpConfig: Map<String, Any>? = null

    @field:JsonProperty("tools")
    @Transient
    var tools: List<Tool>? = null
}