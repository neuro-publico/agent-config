package com.core.controllers


import com.core.models.Tool
import com.core.requests.CreateToolRequest
import com.core.services.ToolServiceInterface
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn

@Controller("/tool")
@ExecuteOn(TaskExecutors.IO)
class ToolController(
    private val toolService: ToolServiceInterface
) {

    @Post
    fun upsertTool(@Body request: CreateToolRequest): HttpResponse<Tool> {
        val tool = toolService.upsertTool(
            name = request.name,
            description = request.description,
            config = request.config
        )
        return HttpResponse.created(tool)
    }
}