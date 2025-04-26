package com.core.repositories

import com.core.models.PromptVersion
import io.micronaut.core.annotation.NonNull
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface PromptVersionRepository : CrudRepository<PromptVersion, Long> {
    fun findByAgentIdOrderByCreatedAtDesc(agentId: String): List<PromptVersion>
    override fun findById(id: Long): @NonNull Optional<PromptVersion>
} 