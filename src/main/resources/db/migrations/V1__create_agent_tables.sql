-- Crear tabla agent_configs
CREATE TABLE agent_configs
(
    id          BIGSERIAL PRIMARY KEY,
    agent_id    VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    prompt      VARCHAR(255),
    provider_ai VARCHAR(255),
    model_ai    VARCHAR(255),
    preferences JSONB,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla tools
CREATE TABLE tools
(
    id          BIGSERIAL PRIMARY KEY,
    tool_name   VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    type        VARCHAR(100) NOT NULL,
    config      JSONB,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla agent_tools
CREATE TABLE agent_tool
(
    id              BIGSERIAL PRIMARY KEY,
    agent_config_id BIGINT NOT NULL,
    tool_id         BIGINT NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (agent_config_id) REFERENCES agent_configs (id),
    FOREIGN KEY (tool_id) REFERENCES tools (id),
    UNIQUE (agent_config_id, tool_id)
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX idx_agent_tools_config_id ON agent_tool (agent_config_id);
CREATE INDEX idx_agent_tools_tool_id ON agent_tool (tool_id);


