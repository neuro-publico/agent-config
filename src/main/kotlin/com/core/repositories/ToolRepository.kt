package com.core.repositories

import com.core.models.Tool
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface ToolRepository : CrudRepository<Tool, Long> {
    fun findByToolName(name: String): Tool?
}