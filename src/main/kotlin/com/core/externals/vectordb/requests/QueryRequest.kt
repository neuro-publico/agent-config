package com.core.externals.vectordb.requests

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
class QueryRequest{
    @field:JsonProperty("query")
    var query: String = ""

    @field:JsonProperty("provider_db")
    var providerDB: String = "pinecone"

    @field:JsonProperty("index_name")
    var indexName: String = "startup"

    @field:JsonProperty("top_k")
    var topK: Int = 1

    @field:JsonProperty("ids")
    var ids: List<String> = emptyList()

    @field:JsonProperty("namespace")
    var namespace: String = "agents"

    @field:JsonProperty("metadata_filter")
    var metadataFilter: Map<String, Map<String, Any>> = emptyMap()
}