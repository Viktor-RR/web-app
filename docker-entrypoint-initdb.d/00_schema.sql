CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username TEXT        NOT NULL UNIQUE,
    password TEXT        NOT NULL,
    active   BOOLEAN     NOT NULL DEFAULT TRUE,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);
CREATE TABLE tokens
(
    token    TEXT PRIMARY KEY,
    "userId" BIGINT      NOT NULL REFERENCES users,
    created  timestamptz NOT NULL DEFAULT current_timestamp
);

CREATE TABLE cards
(
    id        BIGSERIAL PRIMARY KEY,
    "ownerId" BIGINT  NOT NULL REFERENCES users,
    number    TEXT    NOT NULL,
    balance   BIGINT  NOT NULL DEFAULT 0 CONSTRAINT positive_balance CHECK (balance > 0 OR balance = 0),
    active    BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE roles
(
    id BIGINT PRIMARY KEY NOT NULL references users,
    role TEXT NOT NULL
);

CREATE TABLE codes
(
    id BIGINT PRIMARY KEY UNIQUE NOT NULL REFERENCES users,
    code BIGINT NOT NULL
)