CREATE TABLE users (
    id                BIGSERIAL     PRIMARY KEY,
    username          VARCHAR(20)   NOT NULL,
    profile_image_key VARCHAR(255),
    email             VARCHAR(100)  UNIQUE,
    created_at        TIMESTAMP     NOT NULL    DEFAULT now(),
    updated_at        TIMESTAMP     NOT NULL    DEFAULT now()
);

CREATE TABLE sns_accounts (
    id          BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          NOT NULL,
    provider    VARCHAR(50)     NOT NULL,
    provider_id VARCHAR(100)    NOT NULL,
    email       VARCHAR(100),
    created_at  TIMESTAMP       NOT NULL    DEFAULT now(),

    CONSTRAINT fk_sns_accounts__users
        FOREIGN KEY (user_id) REFERENCES users(id)
            ON DELETE CASCADE,
    CONSTRAINT uq_sns_accounts__provider_provider_id
        UNIQUE (provider, provider_id)
);

CREATE INDEX idx_sns_accounts__user_id ON sns_accounts(user_id);

CREATE TABLE diaries (
    id         BIGSERIAL    PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    title      VARCHAR(100) NOT NULL,
    content    TEXT         NOT NULL,
    image_key  VARCHAR(255),
    diary_date DATE         NOT NULL,
    deleted    BOOLEAN      NOT NULL    DEFAULT false,
    created_at TIMESTAMP    NOT NULL    DEFAULT now(),
    updated_at TIMESTAMP    NOT NULL    DEFAULT now(),

    CONSTRAINT fk_diaries__users
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_diaries__user_id_diary_date ON diaries (user_id, diary_date DESC);

CREATE TABLE emotion_analysis (
    id	            BIGSERIAL	PRIMARY KEY,
    diary_id        BIGINT      NOT NULL,
    emotion	        VARCHAR(20),
    color	        VARCHAR(16),
    status	        VARCHAR(16)	NOT NULL DEFAULT 'PENDING',
    requested_at    TIMESTAMP   NOT NULL DEFAULT now(),
    analyzed_at	    TIMESTAMP,

    CONSTRAINT fk_emotion_analysis__diaries
        FOREIGN KEY (diary_id) REFERENCES diaries (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_emotion_analysis__diary_id
        UNIQUE (diary_id)
)

CREATE INDEX idx_emotion_analysis__diary_id ON emotion_analysis (diary_id);