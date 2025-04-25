ALTER TABLE agent_configs
    ADD COLUMN project VARCHAR(255) NOT NULL DEFAULT 'default';