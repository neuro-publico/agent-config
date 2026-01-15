package com.core.controllers

import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

@MicronautTest(environments = ["mock"])
class HealthControllerTest {

    @Inject
    @field:Client("/api/ms/agent")
    lateinit var client: HttpClient

    @Test
    fun `health endpoint should return UP status`() {
        val response = client.toBlocking().exchange("/health-check", Map::class.java)
        
        assertEquals(HttpStatus.OK, response.status)
        
        val body = response.body()
        assertNotNull(body)
        assertEquals("UP", body["status"])
        assertEquals("Service is running normally", body["message"])
    }
}
