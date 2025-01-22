package com.core.externals.vectordb.clients

import com.core.externals.vectordb.requests.QueryRequest
import com.core.externals.vectordb.requests.UpsertDataRequestVectorDB
import com.core.externals.vectordb.responses.SearchResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client
import reactor.core.publisher.Mono

@Client("vectordb")
interface VectordbClient {
    @Post("/vector-db/search/{providerDB}/{indexName}")
    fun search(
        providerDB: String,
        indexName: String,
        @Body queryRequest: QueryRequest
    ): Mono<List<SearchResponse>>

    @Post("/vector-db/upsert_data/{providerDB}/{indexName}")
    fun upsertData(
        providerDB: String,
        indexName: String,
        @Body upsertDataRequest: UpsertDataRequestVectorDB
    ): Mono<Map<String, String>>
}