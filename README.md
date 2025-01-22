# Agent Config Microservice

## Overview
The `Agent Config` microservice is responsible for managing agents, their configurations, and tools. It serves as an intermediary for the `Converse` microservice, providing necessary agent context and tools for communication with AI systems like OpenAI or Amazon Bedrock.

This microservice is written in Kotlin and uses PostgreSQL as its database. It has three main tables:
- **agent_configs**: Stores the configuration details of each agent.
- **tools**: Stores the details of various tools.
- **agent_tool**: Represents the relationship between agents and their tools.

## Features
1. **Agent Configuration Management**: Create and manage agent configurations.
2. **Tool Management**: Add, associate, and remove tools for agents.
3. **Agent Retrieval**:
   - Retrieve an agent by its `agent_id`.
   - Retrieve an agent by query similarity via a separate microservice called `Vector DB`.

## Endpoints

### 1. Create an Agent Configuration
**Endpoint:**
```http
POST /api/ms/agent/config
```
**Request Headers:**
```
Content-Type: application/json
```
**Request Body:**
```json
{
    "agent_id": "mi_primer_agente",
    "preferences": {
        "example_response": "dasdasdas"
    }
}
```
**Description:**
Creates a new agent configuration.

---

### 2. Create a Tool
**Endpoint:**
```http
POST /api/ms/agent/tool
```
**Request Headers:**
```
Content-Type: application/json
```
**Request Body:**
```json
{
    "name": "mi_primera_herramienta",
    "description": "Descripción de la herramienta",
    "type": "Tipo de la herramienta",
    "config": {"xasxa": "xaaz"}
}
```
**Description:**
Creates a new tool.

---

### 3. Associate a Tool with an Agent
**Endpoint:**
```http
POST /api/ms/agent/config/{agent_id}/tools/{tool_name}
```
**Description:**
Associates an existing tool with a specific agent.

**Example:**
```bash
curl --location --request POST 'http://localhost:8080/api/ms/agent/config/mi_primer_agente/tools/mi_primera_herramienta'
```

---

### 4. Remove a Tool from an Agent
**Endpoint:**
```http
DELETE /api/ms/agent/config/{agent_id}/tools/{tool_name}
```
**Description:**
Removes the association between a tool and an agent.

**Example:**
```bash
curl --location --request DELETE 'http://localhost:8080/api/ms/agent/config/mi_primer_agente/tools/mi_primera_herramienta'
```

---

### 5. Retrieve an Agent by Query Similarity
**Description:**
To retrieve an agent by query similarity, the `Agent Config` microservice communicates with the `Vector DB` microservice. This external microservice handles the similarity search and returns the most relevant agent.

---

## Technologies Used
- **Programming Language:** Kotlin
- **Database:** PostgreSQL
- **API Framework:** Micronaut

## Running Locally
1. Clone the repository.
2. Ensure PostgreSQL is installed and running.
3. Update the database configuration in the application properties.
4. Run the application using:
   ```bash
   ./gradlew run
   ```

## Notes
- Make sure the `Vector DB` microservice is up and running to use the query similarity feature.
- Customize the database schema and table names as needed.

## Future Improvements
- Add more detailed logging.
- Enhance security features such as authentication and authorization.
- Implement rate limiting for API endpoints.

