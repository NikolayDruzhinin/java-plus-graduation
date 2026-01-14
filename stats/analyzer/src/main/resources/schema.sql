CREATE TABLE IF NOT EXISTS event_similarities (
    first_event BIGINT,
    second_event BIGINT,
    score DOUBLE PRECISION,
    PRIMARY KEY(first_event, second_event)
);

CREATE TABLE IF NOT EXISTS user_actions (
    user_id BIGINT,
    event_id BIGINT,
    user_score DOUBLE PRECISION,
    timestamp_action TIMESTAMP WITHOUT TIME ZONE,
    PRIMARY KEY(user_id, event_id)
);