package com.core.externals.vectordb.requests

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
class UpsertDataRequestVectorDB{
    @field:JsonProperty("namespace")
    var namespace: String = ""

    @field:JsonProperty("records")
    var records: List<Record> = emptyList()
}

@Serdeable
class Record {
    @field:JsonProperty("id")
    var id: String = ""

    @field:JsonProperty("data")
    var data: Map<String, Any> = mapOf()

    @field:JsonProperty("metadata")
    var metadata: Map<String, Any> = mapOf()
}