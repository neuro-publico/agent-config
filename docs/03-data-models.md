# Modelos de Datos

## Diagrama Entidad-Relación

```
┌─────────────────────────┐       ┌─────────────────────┐
│      agent_configs      │       │        tools        │
├─────────────────────────┤       ├─────────────────────┤
│ id (PK)                 │       │ id (PK)             │
│ agent_id (UNIQUE)       │       │ tool_name (UNIQUE)  │
│ description             │       │ description         │
│ prompt                  │       │ config (JSONB)      │
│ provider_ai             │       │ created_at          │
│ model_ai                │       │ updated_at          │
│ preferences (JSONB)     │       └──────────┬──────────┘
│ metadata (JSONB)        │                  │
│ mcp_config (JSONB)      │                  │
│ provider_vector_db      │                  │
│ index_name              │       ┌──────────┴──────────┐
│ namespace               │       │     agent_tool      │
│ project                 │       ├─────────────────────┤
│ created_at              │       │ id (PK)             │
│ updated_at              │◀──────│ agent_config_id (FK)│
└─────────────────────────┘       │ tool_id (FK)        │
                                  │ created_at          │
┌─────────────────────────┐       └─────────────────────┘
│    prompt_versions      │
├─────────────────────────┤
│ id (PK)                 │
│ agent_id                │
│ previous_prompt         │
│ created_at              │
└─────────────────────────┘
```

---

## AgentConfig

Almacena la configuración completa de cada agente de IA.

### Campos

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | BIGSERIAL | Identificador único autoincremental |
| `agent_id` | VARCHAR(255) | Identificador único del agente (clave de negocio) |
| `description` | VARCHAR(255) | Descripción del agente |
| `prompt` | TEXT | Prompt del sistema para el agente |
| `provider_ai` | VARCHAR(255) | Proveedor de IA (openai, bedrock, etc.) |
| `model_ai` | VARCHAR(255) | Modelo específico (gpt-4, claude-3, etc.) |
| `preferences` | JSONB | Configuración de preferencias del modelo |
| `metadata` | JSONB | Metadatos adicionales para búsquedas |
| `mcp_config` | JSONB | Configuración MCP (Model Context Protocol) |
| `provider_vector_db` | VARCHAR(255) | Proveedor de Vector DB (default: pinecone) |
| `index_name` | VARCHAR(255) | Nombre del índice en Vector DB (default: startup) |
| `namespace` | VARCHAR(255) | Namespace en Vector DB (default: agents) |
| `project` | VARCHAR(255) | Proyecto al que pertenece (default: default) |
| `created_at` | TIMESTAMP | Fecha de creación |
| `updated_at` | TIMESTAMP | Fecha de última actualización |

### Preference (JSONB)

```json
{
  "temperature": 0.8,
  "max_tokens": 200,
  "top_p": 1.0,
  "extra_parameters": {
    "custom_param": "value"
  }
}
```

| Campo | Tipo | Default | Descripción |
|-------|------|---------|-------------|
| `temperature` | Double | 0.8 | Creatividad del modelo (0-1) |
| `max_tokens` | Long | 200 | Máximo de tokens en respuesta |
| `top_p` | Double | 1.0 | Nucleus sampling |
| `extra_parameters` | Map | null | Parámetros adicionales personalizados |

---

## Tool

Define las herramientas disponibles para los agentes.

### Campos

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | BIGSERIAL | Identificador único |
| `tool_name` | VARCHAR(255) | Nombre único de la herramienta |
| `description` | TEXT | Descripción de la herramienta |
| `config` | JSONB | Configuración de la herramienta |
| `created_at` | TIMESTAMP | Fecha de creación |
| `updated_at` | TIMESTAMP | Fecha de actualización |

### ToolConfig (JSONB)

```json
{
  "name": "search_products",
  "description": "Busca productos en el catálogo",
  "api": "https://api.example.com/products",
  "method": "GET",
  "properties": [
    {
      "name": "query",
      "description": "Término de búsqueda"
    }
  ],
  "headers": [
    { "Authorization": "Bearer {{token}}" }
  ],
  "query_params": {
    "limit": "10"
  },
  "body": {},
  "fake_response": null
}
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `name` | String | Nombre de la función |
| `description` | String | Descripción para el LLM |
| `api` | String | URL del endpoint |
| `method` | String | Método HTTP (GET, POST, etc.) |
| `properties` | List<Property> | Propiedades/parámetros de la función |
| `headers` | List<Map> | Headers HTTP |
| `query_params` | Map | Query parameters |
| `body` | Map | Body template |
| `fake_response` | Map | Respuesta simulada (testing) |

---

## AgentTool

Tabla de relación muchos-a-muchos entre agentes y herramientas.

### Campos

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | BIGSERIAL | Identificador único |
| `agent_config_id` | BIGINT | FK a agent_configs.id |
| `tool_id` | BIGINT | FK a tools.id |
| `created_at` | TIMESTAMP | Fecha de asociación |

### Restricciones

- `UNIQUE (agent_config_id, tool_id)` - Un agente no puede tener la misma herramienta dos veces
- `FK agent_config_id → agent_configs.id`
- `FK tool_id → tools.id`

---

## PromptVersion

Almacena el historial de versiones de prompts para cada agente.

### Campos

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | BIGSERIAL | Identificador único |
| `agent_id` | VARCHAR(255) | Identificador del agente |
| `previous_prompt` | TEXT | Prompt de la versión anterior |
| `created_at` | TIMESTAMP | Fecha de la versión |

---

## Migraciones

Las migraciones de base de datos se gestionan con Flyway:

| Versión | Descripción |
|---------|-------------|
| V1 | Creación de tablas iniciales (agent_configs, tools, agent_tool) |
| V2 | Añade columnas de Vector DB (provider_vector_db, index_name, namespace, metadata) |
| V3 | Añade columna mcp_config |
| V4 | Añade columna project |
| V5 | Crea tabla prompt_versions |

Las migraciones se encuentran en: `src/main/resources/db/migrations/`
