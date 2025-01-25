package com.core.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller("/health-check")
class HealthController {

    @Get
    fun health(): HttpResponse<Map<String, Any>> {
        val response = mapOf(
            "status" to "UP",
            "message" to "Service is running normally"
        )
        return HttpResponse.ok(response)
    }
} 