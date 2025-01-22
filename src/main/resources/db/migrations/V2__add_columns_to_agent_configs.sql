ALTER TABLE agent_configs
    ADD COLUMN provider_vector_db VARCHAR(255),
    ADD COLUMN index_name         VARCHAR(255),
    ADD COLUMN namespace          VARCHAR(255),
    ADD COLUMN metadata           JSONB;