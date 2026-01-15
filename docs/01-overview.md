# Visión General

## ¿Qué es Agent Config?

**Agent Config Microservice** es un servicio responsable de gestionar agentes de IA, sus configuraciones y herramientas (tools). Actúa como intermediario para el microservicio `Converse`, proporcionando el contexto necesario del agente y las herramientas para la comunicación con sistemas de IA como OpenAI o Amazon Bedrock.

## Propósito

El microservicio permite:

- **Gestionar configuraciones de agentes**: Crear, actualizar y recuperar configuraciones de agentes de IA
- **Administrar herramientas**: Definir y gestionar herramientas que los agentes pueden utilizar
- **Asociar agentes con herramientas**: Vincular herramientas específicas a agentes
- **Búsqueda por similitud**: Encontrar agentes mediante búsqueda semántica usando Vector DB
- **Versionado de prompts**: Mantener historial de cambios en los prompts de los agentes

## Stack Tecnológico

| Componente | Tecnología |
|------------|------------|
| Lenguaje | Kotlin 1.9.25 |
| Framework | Micronaut 4.x |
| Base de datos | PostgreSQL |
| ORM | Hibernate JPA |
| Migraciones | Flyway |
| Servidor HTTP | Netty |
| Contenedorización | Docker |
| Java Version | 21 |

## Dependencias Externas

- **Vector DB Microservice**: Servicio externo para búsquedas por similitud vectorial (soporta Pinecone)
- **PostgreSQL**: Base de datos relacional para persistencia

## Flujo de Datos

```
┌─────────────────┐      ┌──────────────────┐      ┌─────────────────┐
│                 │      │                  │      │                 │
│  Converse MS    │─────▶│  Agent Config MS │─────▶│   Vector DB MS  │
│                 │      │                  │      │   (Pinecone)    │
└─────────────────┘      └────────┬─────────┘      └─────────────────┘
                                  │
                                  ▼
                         ┌─────────────────┐
                         │                 │
                         │   PostgreSQL    │
                         │                 │
                         └─────────────────┘
```

## Características Principales

### 1. Gestión de Agentes
- CRUD completo de configuraciones de agentes
- Soporte para múltiples proveedores de IA (OpenAI, Bedrock, etc.)
- Configuración flexible mediante JSON (preferences, metadata, mcp_config)

### 2. Gestión de Herramientas
- Definición de herramientas con configuración API
- Asociación dinámica de herramientas a agentes
- Configuración de propiedades, headers, query params y body

### 3. Búsqueda Inteligente
- Búsqueda por `agent_id` directo
- Búsqueda por similitud semántica mediante consultas de texto
- Filtros de metadata para búsquedas precisas

### 4. Versionado de Prompts
- Historial automático de cambios en prompts
- Capacidad de revertir a versiones anteriores
- Trazabilidad completa de modificaciones

### 5. Placeholders Dinámicos
- Soporte para variables en prompts
- Reemplazo de placeholders en tiempo de ejecución
