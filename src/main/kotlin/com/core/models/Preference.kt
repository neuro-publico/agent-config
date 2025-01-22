package com.core.models

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
class Preference {
    @field:JsonProperty("example_response")
   var exampleResponse: String? = null
}