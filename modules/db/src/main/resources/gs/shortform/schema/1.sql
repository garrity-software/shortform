--liquibase formatted sql

--changeset pfm:1
--CREATE TABLE projects(
--    id BIGSERIAL PRIMARY KEY,
--    unique_slug TEXT NOT NULL UNIQUE,
--    display_name TEXT NOT NULL
--);
--rollback DROP TABLE IF EXISTS projects;
