CREATE TABLE if NOT EXISTS categories
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR   NOT NULL,
    description        VARCHAR   NOT NULL,
    event_date         TIMESTAMP,
    lat                DOUBLE PRECISION,
    lon                DOUBLE PRECISION,
    paid               BOOLEAN   NOT NULL,
    participant_limit  INTEGER   NOT NULL,
    request_moderation BOOLEAN   NOT NULL,
    state              VARCHAR   NOT NULL,
    title              VARCHAR   NOT NULL,
    created_on         TIMESTAMP NOT NULL,
    published_on       TIMESTAMP,
    initiator_id       BIGINT    NOT NULL,
    category_id        BIGINT,
    views              BIGINT    NOT NULL,
    confirmed_requests BIGINT    NOT NULL

);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title  VARCHAR NOT NULL,
    pinned BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_events
(
    compilation_id BIGINT NOT NULL,
    event_id       BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations (id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS event_comments
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text         VARCHAR,
    author_id    BIGINT,
    event_id     BIGINT,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    is_updated   BOOLEAN,
    updated_on   TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);
