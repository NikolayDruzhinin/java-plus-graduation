CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP NOT NULL,
    event_id     BIGINT    NOT NULL,
    requester_id BIGINT    NOT NULL,
    status       VARCHAR   NOT NULL
);
