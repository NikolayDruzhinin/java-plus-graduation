DROP TABLE if EXISTS endpoint_hit;


CREATE TABLE IF NOT EXISTS endpoint_hit
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    app       VARCHAR(100)                NOT NULL,
    uri       VARCHAR(100)                NOT NULL,
    ip        VARCHAR(100)                NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
