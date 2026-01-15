# Arquitectura

## Estructura del Proyecto

```
src/main/kotlin/com/core/
├── Application.kt                 # Punto de entrada de la aplicación
├── controllers/                   # Controladores REST
│   ├── AgentConfigController.kt   # Endpoints de configuración de agentes
│   ├── HealthController.kt        # Endpoint de health check
│   └── ToolController.kt          # Endpoints de herramientas
├── extensions/                    # Extensiones y utilidades
│   ├── EvaluatorPinecone.kt       # Transformador de evaluadores para Pinecone
│   └── StringExtension.kt         # Extensiones de String (placeholders)
├── externals/                     # Clientes externos
│   └── vectordb/
│       ├── clients/
│       │   └── VectordbClient.kt  # Cliente HTTP para Vector DB
│       ├── requests/              # DTOs de request
│       └── responses/             # DTOs de response
├── models/                        # Entidades JPA
│   ├── AgentConfig.kt             # Configuración de agente
│   ├── AgentTool.kt               # Relación agente-herramienta
│   ├── Preference.kt              # Preferencias del agente
│   ├── PromptVersion.kt           # Versión de prompt
│   ├── Tool.kt                    # Herramienta
│   └── ToolConfig.kt              # Configuración de herramienta
├── repositories/                  # Repositorios de datos
│   ├── AgentConfigRepository.kt
│   ├── AgentToolRepository.kt
│   ├── PromptVersionRepository.kt
│   └── ToolRepository.kt
├── requests/                      # DTOs de entrada
│   ├── CreateAgentConfigRequest.kt
│   ├── CreateToolRequest.kt
│   └── SearchAgentRequest.kt
└── services/                      # Lógica de negocio
    ├── AgentConfigServiceInterface.kt
    ├── ToolServiceInterface.kt
    └── impl/
        ├── AgentConfigServiceImpl.kt
        └── ToolServiceImpl.kt
```

## Capas de la Aplicación

### 1. Controllers (Capa de Presentación)

Los controladores exponen los endpoints REST y manejan las peticiones HTTP.

| Controlador | Ruta Base | Responsabilidad |
|-------------|-----------|-----------------|
| `AgentConfigController` | `/config` | Gestión de agentes y prompts |
| `ToolController` | `/tool` | Gestión de herramientas |
| `HealthController` | `/health-check` | Monitoreo de salud |

**Contexto base**: `/api/ms/agent`

### 2. Services (Capa de Negocio)

Implementan la lógica de negocio del microservicio.

- **AgentConfigServiceImpl**: 
  - Upsert de configuraciones de agente
  - Asociación/desasociación de herramientas
  - Búsqueda de agentes (por ID o similitud)
  - Versionado y reversión de prompts

- **ToolServiceImpl**:
  - Upsert de herramientas
  - Búsqueda de herramientas por nombre

### 3. Repositories (Capa de Datos)

Repositorios JPA para acceso a base de datos usando Micronaut Data.

```kotlin
interface AgentConfigRepository : JpaRepository<AgentConfig, Long> {
    fun findByAgentId(agentId: String): AgentConfig?
}
```

### 4. External Clients (Integraciones)

Cliente HTTP declarativo para comunicación con Vector DB:

```kotlin
@Client("vectordb")
interface VectordbClient {
    @Post("/api/ms/vector-db/search/{providerDB}/{indexName}")
    fun search(...): Mono<List<SearchResponse>>
    
    @Post("/api/ms/vector-db/upsert_data/{providerDB}/{indexName}")
    fun upsertData(...): Mono<Map<String, String>>
}
```

## Patrones de Diseño

### Dependency Injection
Micronaut inyecta automáticamente las dependencias en constructores:

```kotlin
@Singleton
class AgentConfigServiceImpl(
    private val agentConfigRepository: AgentConfigRepository,
    private val vectordbClient: VectordbClient,
    // ...
) : AgentConfigServiceInterface
```

### Repository Pattern
Abstracción del acceso a datos mediante interfaces de repositorio.

### DTO Pattern
Objetos de transferencia de datos para requests/responses.

### Upsert Pattern
Actualización o inserción condicional basada en existencia del registro.

## Flujo de Ejecución

### Creación/Actualización de Agente

```
1. Controller recibe CreateAgentConfigRequest
2. Service busca si existe el agente por agent_id
3. Si existe → Actualiza campos y guarda versión de prompt si cambió
4. Si no existe → Crea nuevo registro
5. Upsert asíncrono en Vector DB (Pinecone)
6. Retorna AgentConfig actualizado
```

### Búsqueda de Agente

```
1. Controller recibe SearchAgentRequest
2. Si tiene agent_id → Búsqueda directa en PostgreSQL
3. Si tiene query → Búsqueda por similitud en Vector DB
4. Obtiene herramientas asociadas al agente
5. Reemplaza placeholders en el prompt
6. Retorna AgentConfig con tools incluidas
```

## Programación Reactiva

El servicio utiliza Project Reactor para operaciones asíncronas:

```kotlin
fun searchAgent(@Body request: SearchAgentRequest): Mono<AgentConfig>
```

Las operaciones de Vector DB son asíncronas y no bloquean el hilo principal.
