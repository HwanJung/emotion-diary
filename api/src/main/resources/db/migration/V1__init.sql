CREATE TABLE users
(
    id                BIGSERIAL PRIMARY KEY,
    username          VARCHAR(20)  NOT NULL,
    provider          VARCHAR(50)  NOT NULL,
    provider_id       VARCHAR(100) NOT NULL UNIQUE,
    profile_image_url VARCHAR(255),
    email             VARCHAR(100) NOT NULL,
    created_at        TIMESTAMP    NOT NULL,
    updated_at        TIMESTAMP    NOT NULL
);

CREATE INDEX idx_users_provider_provider_id ON users (provider, provider_id);

CREATE TABLE diaries
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    title      VARCHAR(100) NOT NULL,
    content    TEXT         NOT NULL,
    image_key  VARCHAR(2048),
    diary_date DATE         NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,

    CONSTRAINT fk_diary_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_diary_user_date ON diaries (user_id, diary_date DESC);
