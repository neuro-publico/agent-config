package com.core.extensions

object EvaluatorPinecone {
    fun transformEvaluator(evaluator: String, value: String): Map<String, Any> {
        return when (evaluator) {
            "=" -> {
                mapOf("\$eq" to value)  // Para Pinecone usamos "$eq"
            }

            "!=" -> {
                mapOf("\$ne" to value)  // Para Pinecone usamos "$ne"
            }

            ">" -> {
                mapOf("\$gt" to value)  // Para Pinecone usamos "$gt"
            }

            ">=" -> {
                mapOf("\$gte" to value)  // Para Pinecone usamos "$gte"
            }

            "<" -> {
                mapOf("\$lt" to value)  // Para Pinecone usamos "$lt"
            }

            "<=" -> {
                mapOf("\$lte" to value)  // Para Pinecone usamos "$lte"
            }

            "in" -> {
                // Este caso asume que 'value' es una lista separada por comas
                mapOf("\$in" to value.split(","))
            }

            "nin" -> {
                // Este caso asume que 'value' es una lista separada por comas
                mapOf("\$nin" to value.split(","))
            }

            "exists" -> {
                mapOf("\$exists" to value.toBoolean())  // Para Pinecone usamos "$exists"
            }

            else -> {
                throw IllegalArgumentException("Unknown evaluator '$evaluator' in metadata filter")
            }
        }
    }
}