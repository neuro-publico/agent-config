package com.core.models

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
class Preference {
    @field:JsonProperty("extra_parameters")
    var extraParameters: Map<String, String>? = null

    @field:JsonProperty("temperature")
    var temperature: Double? = null

    @field:JsonProperty("max_tokens")
    var maxTokens: Long? = null

    @field:JsonProperty("top_p")
    var topP: Double? = null
}