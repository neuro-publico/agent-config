package com.core.models

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import java.time.LocalDateTime

@Serdeable
@Entity(name = "agent_tool")
class AgentTool{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agent_tool_generator")
    @SequenceGenerator(name = "agent_tool_generator", sequenceName = "agent_tool_id_seq", allocationSize = 1)
    var id: Long? = null

    @Column(name = "agent_config_id")
    @field:JsonProperty("agent_config_id")
    var agentConfigId: Long = 0

    @Column(name = "tool_id")
    @field:JsonProperty("tool_id")
    var toolId: Long = 0

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()
}