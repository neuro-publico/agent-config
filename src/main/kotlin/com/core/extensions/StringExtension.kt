package com.core.extensions

fun String.replacePlaceholders(parameters: Map<String, Any>?): String {
    if (parameters == null) return this

    var result = this
    parameters.forEach { (key, value) ->
        result = result.replace("{$key}", value.toString())
    }
    return result
}