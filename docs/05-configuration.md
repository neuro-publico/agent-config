# Configuración

## Variables de Entorno

### Base de Datos (PostgreSQL)

| Variable | Descripción | Default |
|----------|-------------|---------|
| `DB_HOST` | Host de la base de datos | `localhost` |
| `DB_PORT` | Puerto de la base de datos | `5432` |
| `DB_NAME` | Nombre de la base de datos | `agents` |
| `DB_USER` | Usuario de la base de datos | `postgres` |
| `DB_PASSWORD` | Contraseña del usuario | `mysecretpassword` |

### Vector DB

| Variable | Descripción | Default |
|----------|-------------|---------|
| `VECTOR_DB_URL` | URL del microservicio Vector DB | `http://localhost:9000` |

---

## Archivo de Configuración

**Ubicación**: `src/main/resources/application.properties`

```properties
# Nombre de la aplicación
micronaut.application.name=agent-config-ms

# Context path del servidor
micronaut.server.context-path=/api/ms/agent

# Configuración de base de datos
datasources.default.driver-class-name=org.postgresql.Driver
datasources.default.url=jdbc:postgresql://${DB_HOST:`localhost`}:${DB_PORT:`5432`}/${DB_NAME:`agents`}?socketTimeout=300
datasources.default.username=${DB_USER:`postgres`}
datasources.default.password=${DB_PASSWORD:`mysecretpassword`}
datasources.default.maximumPoolSize=5
datasources.default.connection-timeout=1000

# Configuración de JPA/Hibernate
jpa.default.properties.hibernate.hbm2ddl.auto=none
jpa.default.properties.hibernate.show_sql=false

# Flyway (migraciones)
flyway.datasources.default.enabled=true
flyway.datasources.default.locations[0]=classpath:db/migrations

# Cliente HTTP - Vector DB
micronaut.http.services.vectordb.url=${VECTOR_DB_URL:`http://localhost:9000`}
micronaut.http.services.vectordb.read-timeout=20s
micronaut.http.services.vectordb.connect-timeout=1s
micronaut.http.services.vectordb.pool.enabled=true
micronaut.http.services.vectordb.pool.max-connections=50

# CORS
micronaut.http.cors.enabled=true
micronaut.http.cors.origin=*
micronaut.http.cors.methods=*
micronaut.http.cors.headers=*
micronaut.http.cors.allow-credentials=true
```

---

## Configuración de Pool de Conexiones

El microservicio utiliza HikariCP para el pool de conexiones a PostgreSQL:

| Parámetro | Valor | Descripción |
|-----------|-------|-------------|
| `maximumPoolSize` | 5 | Máximo de conexiones en el pool |
| `connection-timeout` | 1000ms | Timeout para obtener conexión |

---

## Configuración de Cliente HTTP

Configuración del cliente para comunicación con Vector DB:

| Parámetro | Valor | Descripción |
|-----------|-------|-------------|
| `read-timeout` | 20s | Timeout de lectura |
| `connect-timeout` | 1s | Timeout de conexión |
| `pool.enabled` | true | Pool de conexiones habilitado |
| `pool.max-connections` | 50 | Máximo de conexiones |

---

## Migraciones de Base de Datos

Las migraciones se ejecutan automáticamente al iniciar la aplicación gracias a Flyway.

**Ubicación**: `src/main/resources/db/migrations/`

Para deshabilitar las migraciones automáticas:

```properties
flyway.datasources.default.enabled=false
```

---

## Logging

**Configuración**: `src/main/resources/logback.xml`

El microservicio utiliza Logback para logging. Los logs se pueden configurar modificando el archivo `logback.xml`.

---

## Ejemplo de Configuración Docker

```bash
docker run -p 8080:8080 \
  -e DB_HOST=db.example.com \
  -e DB_PORT=5432 \
  -e DB_NAME=agents_prod \
  -e DB_USER=app_user \
  -e DB_PASSWORD=secure_password \
  -e VECTOR_DB_URL=http://vectordb:9000 \
  agent-config-ms
```

---

## Docker Compose (Desarrollo)

```yaml
version: '3.8'
services:
  agent-config-ms:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_HOST=postgres
      - DB_PORT=5432
      - DB_NAME=agents
      - DB_USER=postgres
      - DB_PASSWORD=mysecretpassword
      - VECTOR_DB_URL=http://vectordb:9000
    depends_on:
      - postgres

  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: agents
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: mysecretpassword
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```
