-- Crear la secuencia para el ID de prompt_versions
CREATE SEQUENCE prompt_versions_id_seq START 1 INCREMENT 1;

-- Crear la tabla prompt_versions
CREATE TABLE prompt_versions (
    id BIGINT PRIMARY KEY DEFAULT nextval('prompt_versions_id_seq'),
    agent_id VARCHAR(255) NOT NULL,
    previous_prompt TEXT, -- El prompt antes del cambio
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- Este campo ahora indica cuándo se guardó esta versión histórica
);

-- Crear un índice en agent_id para búsquedas rápidas
CREATE INDEX idx_prompt_versions_agent_id ON prompt_versions(agent_id);

-- Opcional: Añadir una clave foránea si tienes una tabla 'agents' con 'agent_id' como clave única/primaria
-- ALTER TABLE prompt_versions
-- ADD CONSTRAINT fk_prompt_versions_agent_id
-- FOREIGN KEY (agent_id) REFERENCES agents(agent_id); -- Ajusta 'agents(agent_id)' según tu esquema 