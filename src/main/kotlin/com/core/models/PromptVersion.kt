package com.core.models

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.LocalDateTime

@Serdeable
@Entity
@Table(name = "prompt_versions")
class PromptVersion(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prompt_versions_seq_generator")
    @SequenceGenerator(name = "prompt_versions_seq_generator", sequenceName = "prompt_versions_id_seq", allocationSize = 1)
    var id: Long? = null,

    @field:JsonProperty("agent_id")
    @Column(name = "agent_id", nullable = false)
    var agentId: String,

    @field:JsonProperty("previous_prompt")
    @Column(name = "previous_prompt", columnDefinition = "TEXT")
    var previousPrompt: String?,

    @field:JsonProperty("created_at")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(agentId = "", previousPrompt = null)
} 