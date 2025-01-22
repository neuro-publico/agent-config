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
import org.hibernate.annotations.Type
import java.time.LocalDateTime

@Serdeable
@Entity(name = "tools")
class Tool {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tool_seq_generator")
    @SequenceGenerator(name = "tool_seq_generator", sequenceName = "tools_id_seq", allocationSize = 1)
    var id: Long? = null

    @Column(name = "tool_name")
    @field:JsonProperty("tool_name")
    var toolName: String = ""

    @Column(name = "description")
    var description: String? = null

    @Column(name = "type")
    var type: String = ""

    @Type(value = JsonBinaryType::class)
    @Column(name = "config", columnDefinition = "jsonb")
    var config: ToolConfig? = null

    @field:JsonProperty("created_at")
    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()

    @field:JsonProperty("updated_at")
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
}