package com.core.repositories

import com.core.models.AgentConfig
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface AgentConfigRepository : CrudRepository<AgentConfig, Long>