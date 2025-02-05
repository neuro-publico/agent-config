package com.core.services.impl

import com.core.extensions.EvaluatorPinecone
import com.core.extensions.replacePlaceholders
import com.core.externals.vectordb.clients.VectordbClient
import com.core.externals.vectordb.requests.QueryRequest
import com.core.externals.vectordb.requests.Record
import com.core.externals.vectordb.requests.UpsertDataRequestVectorDB
import com.core.models.AgentConfig
import com.core.models.AgentTool
import com.core.repositories.AgentToolRepository
import com.core.repositories.ToolRepository
import com.core.repositories.AgentConfigRepository
import com.core.services.AgentConfigServiceInterface
import com.core.requests.CreateAgentConfigRequest
import com.core.requests.SearchAgentRequest
import jakarta.inject.Singleton
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime

@Singleton
class AgentConfigServiceImpl(
    private val agentConfigRepository: AgentConfigRepository,
    private val agentToolRepository: AgentToolRepository,
    private val vectordbClient: VectordbClient,
    private val toolRepository: ToolRepository
) : AgentConfigServiceInterface {

    override fun upsertAgentConfig(request: CreateAgentConfigRequest): AgentConfig {
        val existingConfig = this.agentConfigRepository.findByAgentId(request.agentId)

        val agentConfig = if (existingConfig != null) {
            existingConfig.preferences = request.preferences
            existingConfig.prompt = request.prompt
            existingConfig.description = request.description
            existingConfig.metadata = request.metadata
            existingConfig.modelAI = request.modelAI
            existingConfig.providerAI = request.providerAI
            existingConfig.updatedAt = LocalDateTime.now()
            this.agentConfigRepository.update(existingConfig)
        } else {
            val newConfig = AgentConfig().apply {
                this.agentId = request.agentId
                this.preferences = request.preferences
                this.prompt = request.prompt
                this.description = request.description
                this.metadata = request.metadata
                this.modelAI = request.modelAI
                this.indexName = request.indexName
                this.namespace = request.namespace
                this.providerAI = request.providerAI
                this.providerVectorDB = request.providerVectorDB
            }
            this.agentConfigRepository.save(newConfig)
        }

        this.vectordbClient.upsertData(
            agentConfig.providerVectorDB, agentConfig.indexName, UpsertDataRequestVectorDB().apply {
                this.namespace = agentConfig.namespace
                this.records = listOf(Record().apply {
                    this.id = agentConfig.agentId
                    this.data = mapOf("prompt" to agentConfig.prompt, "description" to agentConfig.description)
                    this.metadata = request.metadata ?: mapOf()
                })
            }
        ).subscribeOn(Schedulers.boundedElastic()).subscribe()

        return agentConfig
    }

    override fun addToolToAgent(agentId: String, toolId: Long): AgentTool {
        val agentConfig = this.agentConfigRepository.findByAgentId(agentId)
            ?: throw IllegalArgumentException("Agent not found")
        val agentToolExists = this.agentToolRepository.findByAgentConfigIdAndToolId(agentConfig.id!!, toolId)
        if (agentToolExists != null) throw IllegalArgumentException("tool agent already exists")

        val agentTool = AgentTool().apply {
            this.agentConfigId = agentConfig.id!!;
            this.toolId = toolId
        }

        return this.agentToolRepository.save(agentTool)
    }

    override fun removeToolFromAgent(agentId: String, toolId: Long) {
        val agentConfig =
            this.agentConfigRepository.findByAgentId(agentId) ?: throw IllegalArgumentException("Agent not found")
        this.agentToolRepository.deleteByAgentConfigIdAndToolId(agentConfig.id!!, toolId)
    }

    override fun getAgent(request: SearchAgentRequest): Mono<AgentConfig> {
        val agent = when {
            request.agentId != null -> {
                Mono.just(this.searchAgentId(request.agentId))
            }

            request.query != null -> {
                val queryRequest = QueryRequest().apply {
                    this.query = request.query
                    this.metadataFilter = request.metadataFilter?.associate { item ->
                        val key =
                            item["key"] as? String ?: throw IllegalArgumentException("Missing 'key' in metadata filter")
                        val value = item["value"] as? String
                            ?: throw IllegalArgumentException("Missing 'value' in metadata filter")
                        val evaluator = item["evaluator"] as? String
                            ?: throw IllegalArgumentException("Missing 'evaluator' in metadata filter")

                        //TODO ONLY PINECONE SUPPORTED
                        key to EvaluatorPinecone.transformEvaluator(evaluator, value)
                    } ?: emptyMap()
                }
                this.vectordbClient.search(queryRequest.providerDB, queryRequest.indexName, queryRequest).map {
                    val response = it.firstOrNull() ?: throw IllegalArgumentException("GET Agent not found")
                    this.searchAgentId(response.id)
                }
            }

            else -> throw IllegalArgumentException("GET Agent not found")
        }

        return agent.map {
            val toolIds = this.agentToolRepository.findByAgentConfigId(it.id!!).map { it.toolId }
            it.tools = this.toolRepository.findByIdIn(toolIds)
            it.prompt = it.prompt.replacePlaceholders(request.parameterPrompt)
            it
        }
    }

    override fun findAll(): List<AgentConfig> {
        return this.agentConfigRepository.findAll()
    }

    private fun searchAgentId(agentId: String): AgentConfig {
        return this.agentConfigRepository.findByAgentId(agentId)
            ?: throw IllegalArgumentException("GET Agent not found")
    }
}