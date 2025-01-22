package com.core.models

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
class ToolConfig {
    @JsonProperty("properties")
    var properties: List<Property> = emptyList()

    @JsonProperty("name")
    var name: String = ""

    @JsonProperty("description")
    var description: String = ""

    @JsonProperty("api")
    var api: String = ""

    @JsonProperty("method")
    var method: String = ""

    @JsonProperty("body")
    var body: Map<String, String> = emptyMap()

    @JsonProperty("headers")
    var headers: List<Map<String, String>> = emptyList()

    @JsonProperty("query_params")
    var queryParams: Map<String, String> = emptyMap()

    @JsonProperty("fake_response")
    var fakeResponse: Map<String, String>? = null
}


@Serdeable
class Property {
    @JsonProperty("name") var name: String = ""
    @JsonProperty("description") var description: String = ""
}