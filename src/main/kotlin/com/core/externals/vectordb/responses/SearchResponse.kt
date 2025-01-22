package com.core.externals.vectordb.responses

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class SearchResponse(
    val id: String,
    val score: Double? = null,
    val metadata: Map<String, Any>? = null,
    val vector: List<Double>? = null
)