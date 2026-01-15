# Despliegue

## Requisitos Previos

- **Java 21** o superior
- **PostgreSQL 12+** en ejecución
- **Docker** (opcional, para contenedorización)
- **Vector DB Microservice** en ejecución (para búsquedas por similitud)

---

## Build Local

### Usando Gradle

```bash
# Limpiar y compilar
./gradlew clean build

# Ejecutar la aplicación
./gradlew run
```

El servicio estará disponible en: `http://localhost:8080/api/ms/agent`

### Ejecutar Tests

```bash
./gradlew test
```

---

## Docker

### Build de la Imagen

```bash
docker build -t agent-config-ms .
```

### Ejecutar el Contenedor

```bash
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=agents \
  -e DB_USER=postgres \
  -e DB_PASSWORD=mysecretpassword \
  -e VECTOR_DB_URL=http://host.docker.internal:9000 \
  agent-config-ms
```

### Detener el Contenedor

```bash
docker stop $(docker ps -q --filter ancestor=agent-config-ms)
```

---

## Dockerfile

El proyecto incluye un Dockerfile multi-stage optimizado:

```dockerfile
# El Dockerfile del proyecto utiliza Gradle para compilar
# y Micronaut para ejecutar la aplicación
```

**Características**:
- Compilación multi-stage para imagen más pequeña
- Uso de JDK 21
- Optimizaciones de Micronaut AOT

---

## Verificación de Salud

Una vez desplegado, verifica que el servicio esté funcionando:

```bash
# Health Check
GET http://localhost:8080/api/ms/agent/health-check
```

**Respuesta esperada**:

```json
{
  "status": "UP",
  "message": "Service is running normally"
}
```

---

## Preparación de la Base de Datos

### 1. Crear la Base de Datos

```sql
CREATE DATABASE agents;
```

### 2. Migraciones Automáticas

Las migraciones se ejecutan automáticamente al iniciar la aplicación gracias a Flyway. No es necesario ejecutar scripts SQL manualmente.

### 3. Verificar Migraciones

Las tablas creadas serán:
- `agent_configs`
- `tools`
- `agent_tool`
- `prompt_versions`
- `flyway_schema_history` (control de migraciones)

---

## Dependencias de Runtime

### Vector DB Microservice

El microservicio requiere que Vector DB esté disponible para:
- Sincronizar configuraciones de agentes
- Realizar búsquedas por similitud

Asegúrate de que la URL esté correctamente configurada en `VECTOR_DB_URL`.

---

## Consideraciones de Producción

### 1. Pool de Conexiones

Ajusta el tamaño del pool según la carga esperada:

```properties
datasources.default.maximumPoolSize=20
```

### 2. Timeouts

Configura timeouts apropiados para producción:

```properties
datasources.default.connection-timeout=5000
micronaut.http.services.vectordb.read-timeout=30s
```

### 3. CORS

En producción, restringe los orígenes permitidos:

```properties
micronaut.http.cors.origin=https://tudominio.com
```

### 4. Logs

Configura el nivel de logs apropiado en `logback.xml`:

```xml
<root level="INFO">
    <appender-ref ref="STDOUT" />
</root>
```

### 5. Métricas

Considera agregar endpoints de métricas para monitoreo.

---

## Troubleshooting

### Error de Conexión a Base de Datos

```
Connection refused to host: localhost:5432
```

**Solución**: Verifica que PostgreSQL esté corriendo y las credenciales sean correctas.

### Error de Conexión a Vector DB

```
Connection timed out: vectordb
```

**Solución**: Verifica que el microservicio Vector DB esté disponible en la URL configurada.

### Migraciones Fallidas

```
Migration checksum mismatch
```

**Solución**: No modifiques migraciones ya aplicadas. Crea una nueva migración para cambios.

---

## Comandos Útiles

```bash
# Ver logs del contenedor
docker logs -f $(docker ps -q --filter ancestor=agent-config-ms)

# Acceder al contenedor
docker exec -it $(docker ps -q --filter ancestor=agent-config-ms) /bin/sh

# Verificar que la app está corriendo
curl http://localhost:8080/api/ms/agent/health-check

# Ver todas las configuraciones de agentes
curl http://localhost:8080/api/ms/agent/config

# Ver todas las herramientas
curl http://localhost:8080/api/ms/agent/tool
```
