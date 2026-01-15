# Agent Config Microservice - Documentación

Este directorio contiene la documentación técnica completa del microservicio **Agent Config**.

## Índice

1. [Visión General](./01-overview.md) - Introducción y propósito del servicio
2. [Arquitectura](./02-architecture.md) - Diseño arquitectónico y componentes
3. [Modelos de Datos](./03-data-models.md) - Entidades y esquema de base de datos
4. [API Reference](./04-api-reference.md) - Documentación de endpoints
5. [Configuración](./05-configuration.md) - Variables de entorno y configuración
6. [Despliegue](./06-deployment.md) - Instrucciones de build y deployment

## Quick Start

```bash
# Build con Docker
docker build -t agent-config-ms .

# Ejecutar
docker run -p 8080:8080 agent-config-ms
```

El servicio estará disponible en `http://localhost:8080/api/ms/agent`
