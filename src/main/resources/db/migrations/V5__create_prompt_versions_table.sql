CREATE SEQUENCE prompt_versions_id_seq START 1 INCREMENT 1;

CREATE TABLE prompt_versions (
    id BIGINT PRIMARY KEY DEFAULT nextval('prompt_versions_id_seq'),
    agent_id VARCHAR(255) NOT NULL,
    previous_prompt TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_prompt_versions_agent_id ON prompt_versions(agent_id); 