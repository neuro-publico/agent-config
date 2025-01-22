package com.core.models

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
class ToolConfig {
    @field:JsonProperty("parameters")
   var parameters: Map<String, String>? = null
}