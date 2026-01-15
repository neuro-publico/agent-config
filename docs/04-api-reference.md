# API Reference

**Base URL**: `/api/ms/agent`

---

## Health Check

### GET /health-check

Verifica el estado del servicio.

**Response** `200 OK`

```json
{
  "status": "UP",
  "message": "Service is running normally"
}
```

---

## Agent Configuration

### GET /config

Obtiene todas las configuraciones de agentes.

**Response** `200 OK`

```json
[
  {
    "id": 1,
    "agent_id": "mi_agente",
    "description": "Agente de soporte",
    "prompt": "Eres un asistente...",
    "provider_ai": "openai",
    "model_ai": "gpt-4",
    "preferences": {
      "temperature": 0.8,
      "max_tokens": 200,
      "top_p": 1.0
    },
    "index_name": "startup",
    "namespace": "agents",
    "provider_vector_db": "pinecone",
    "project": "default",
    "metadata": {},
    "mcp_config": null,
    "created_at": "2024-01-15T10:30:00",
    "updated_at": "2024-01-15T10:30:00"
  }
]
```

---

### POST /config

Crea o actualiza una configuración de agente (upsert).

**Request Body**

```json
{
  "agent_id": "mi_agente",
  "description": "Agente de atención al cliente",
  "prompt": "Eres un asistente de atención al cliente. Tu nombre es {{nombre}}.",
  "provider_ai": "openai",
  "model_ai": "gpt-4",
  "preferences": {
    "temperature": 0.7,
    "max_tokens": 500,
    "top_p": 0.9,
    "extra_parameters": {
      "presence_penalty": "0.5"
    }
  },
  "index_name": "startup",
  "namespace": "agents",
  "provider_vector_db": "pinecone",
  "project": "ecommerce",
  "metadata": {
    "category": "support",
    "language": "es"
  },
  "mcp_config": {
    "servers": []
  }
}
```

| Campo | Tipo | Requerido | Default | Descripción |
|-------|------|-----------|---------|-------------|
| `agent_id` | string | ✅ | - | Identificador único del agente |
| `description` | string | ✅ | - | Descripción del agente |
| `preferences` | object | ✅ | - | Preferencias del modelo |
| `prompt` | string | ❌ | "" | Prompt del sistema |
| `provider_ai` | string | ❌ | "" | Proveedor de IA |
| `model_ai` | string | ❌ | "" | Modelo de IA |
| `index_name` | string | ❌ | "startup" | Índice en Vector DB |
| `namespace` | string | ❌ | "agents" | Namespace en Vector DB |
| `provider_vector_db` | string | ❌ | "pinecone" | Proveedor de Vector DB |
| `project` | string | ❌ | "default" | Nombre del proyecto |
| `metadata` | object | ❌ | null | Metadatos adicionales |
| `mcp_config` | object | ❌ | null | Configuración MCP |

**Response** `200 OK`

```json
{
  "id": 1,
  "agent_id": "mi_agente",
  "description": "Agente de atención al cliente",
  "prompt": "Eres un asistente de atención al cliente...",
  ...
}
```

**Comportamiento**:
- Si el `agent_id` existe → Actualiza la configuración
- Si el `agent_id` no existe → Crea nueva configuración
- Si el `prompt` cambia → Guarda versión anterior en `prompt_versions`
- Sincroniza automáticamente con Vector DB

---

### POST /config/search-agent

Busca un agente por ID o por similitud semántica.

**Request Body - Búsqueda por ID**

```json
{
  "agent_id": "mi_agente"
}
```

**Request Body - Búsqueda por similitud**

```json
{
  "query": "necesito ayuda con mi pedido",
  "metadata_filter": [
    {
      "key": "category",
      "value": "support",
      "evaluator": "eq"
    }
  ],
  "parameter_prompt": {
    "nombre": "Carlos",
    "fecha": "2024-01-15"
  }
}
```

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `agent_id` | string | ID del agente (búsqueda directa) |
| `query` | string | Consulta para búsqueda por similitud |
| `metadata_filter` | array | Filtros de metadata |
| `parameter_prompt` | object | Valores para reemplazar placeholders en prompt |

**metadata_filter evaluators**:
- `eq` - Igual a
- `ne` - No igual a
- `gt` - Mayor que
- `gte` - Mayor o igual
- `lt` - Menor que
- `lte` - Menor o igual
- `in` - Está en lista

**Response** `200 OK`

```json
{
  "id": 1,
  "agent_id": "mi_agente",
  "description": "Agente de atención al cliente",
  "prompt": "Eres un asistente de atención al cliente. Tu nombre es Carlos.",
  "tools": [
    {
      "id": 1,
      "tool_name": "search_orders",
      "description": "Busca pedidos del cliente",
      "config": {...}
    }
  ],
  ...
}
```

---

### POST /config/{agentId}/tools/{toolName}

Asocia una herramienta a un agente.

**Path Parameters**

| Parámetro | Descripción |
|-----------|-------------|
| `agentId` | ID del agente |
| `toolName` | Nombre de la herramienta |

**Response** `201 Created`

```json
{
  "id": 1,
  "agent_config_id": 5,
  "tool_id": 3,
  "created_at": "2024-01-15T10:30:00"
}
```

**Errores**:
- `400` - Agent not found
- `400` - Tool not found
- `400` - tool agent already exists

---

### DELETE /config/{agentId}/tools/{toolName}

Elimina la asociación entre una herramienta y un agente.

**Path Parameters**

| Parámetro | Descripción |
|-----------|-------------|
| `agentId` | ID del agente |
| `toolName` | Nombre de la herramienta |

**Response** `200 OK`

---

### GET /config/{agentId}/prompts/history

Obtiene el historial de versiones del prompt de un agente.

**Path Parameters**

| Parámetro | Descripción |
|-----------|-------------|
| `agentId` | ID del agente |

**Response** `200 OK`

```json
[
  {
    "id": 3,
    "agent_id": "mi_agente",
    "previous_prompt": "Versión anterior del prompt...",
    "created_at": "2024-01-14T15:00:00"
  },
  {
    "id": 2,
    "agent_id": "mi_agente",
    "previous_prompt": "Versión más antigua...",
    "created_at": "2024-01-13T10:00:00"
  }
]
```

---

### PUT /config/{agentId}/prompts/revert/{promptVersionId}

Revierte el prompt del agente a una versión anterior.

**Path Parameters**

| Parámetro | Descripción |
|-----------|-------------|
| `agentId` | ID del agente |
| `promptVersionId` | ID de la versión del prompt |

**Response** `200 OK`

```json
{
  "id": 1,
  "agent_id": "mi_agente",
  "prompt": "Prompt revertido a versión anterior...",
  ...
}
```

**Comportamiento**:
- Guarda el prompt actual como nueva versión antes de revertir
- Actualiza el prompt del agente con la versión seleccionada

**Errores**:
- `400` - La versión del prompt no existe
- `400` - La versión del prompt no pertenece al agente especificado

---

## Tools

### GET /tool

Obtiene todas las herramientas.

**Response** `200 OK`

```json
[
  {
    "id": 1,
    "tool_name": "search_products",
    "description": "Busca productos en el catálogo",
    "config": {
      "name": "search_products",
      "description": "Busca productos",
      "api": "https://api.example.com/products",
      "method": "GET",
      "properties": [
        { "name": "query", "description": "Término de búsqueda" }
      ],
      "headers": [],
      "query_params": { "limit": "10" },
      "body": {},
      "fake_response": null
    },
    "created_at": "2024-01-15T10:30:00",
    "updated_at": "2024-01-15T10:30:00"
  }
]
```

---

### POST /tool

Crea o actualiza una herramienta (upsert).

**Request Body**

```json
{
  "name": "search_products",
  "description": "Busca productos en el catálogo",
  "config": {
    "name": "search_products",
    "description": "Busca productos por nombre o categoría",
    "api": "https://api.example.com/products",
    "method": "GET",
    "properties": [
      { "name": "query", "description": "Término de búsqueda" },
      { "name": "category", "description": "Categoría del producto" }
    ],
    "headers": [
      { "Authorization": "Bearer {{api_key}}" }
    ],
    "query_params": {
      "limit": "20"
    },
    "body": {},
    "fake_response": {
      "products": "[{\"id\": 1, \"name\": \"Test Product\"}]"
    }
  }
}
```

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| `name` | string | ✅ | Nombre único de la herramienta |
| `description` | string | ✅ | Descripción de la herramienta |
| `config` | object | ❌ | Configuración de la herramienta |

**Response** `201 Created`

```json
{
  "id": 1,
  "tool_name": "search_products",
  "description": "Busca productos en el catálogo",
  "config": {...},
  "created_at": "2024-01-15T10:30:00",
  "updated_at": "2024-01-15T10:30:00"
}
```

---

## Códigos de Estado

| Código | Descripción |
|--------|-------------|
| `200` | Operación exitosa |
| `201` | Recurso creado exitosamente |
| `400` | Error de validación o recurso no encontrado |
| `500` | Error interno del servidor |
