-- User table
CREATE TABLE "user" (
    id                   BIGSERIAL                   NOT NULL,
    uuid                 UUID                        NOT NULL,
    username             VARCHAR(255)                NOT NULL,
    password             VARCHAR(255),
    email                VARCHAR(255)                NOT NULL,
    total_size           BIGINT                      NOT NULL,
    deleted              BOOLEAN DEFAULT FALSE       NOT NULL,
    created_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at           TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT ch_user CHECK (id > 0),
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_uuid UNIQUE (uuid)
);

CREATE UNIQUE INDEX ux_user_email
ON "user" (email)
WHERE deleted = false;

-- role table
CREATE TABLE role (
    id   BIGSERIAL    NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT ch_role CHECK (id > 0),
    CONSTRAINT pk_role PRIMARY KEY (id),
    CONSTRAINT uq_role_name UNIQUE (name)
);

-- user_role relationship table
CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    CONSTRAINT pk_user_role PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE
);

-- insert default roles
INSERT INTO role (name)
VALUES
  ('ADMIN'),
  ('USER');

-- refresh_token table
CREATE TABLE refresh_token (
    id         BIGSERIAL                   NOT NULL,
    uuid       UUID                        NOT NULL,
    token      TEXT                        NOT NULL,
    user_id    BIGINT                      NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT ch_refresh_token CHECK (id > 0),
    CONSTRAINT pk_refresh_token PRIMARY KEY (id),
    CONSTRAINT uq_refresh_token_uuid UNIQUE (uuid),
    CONSTRAINT uq_refresh_token_token UNIQUE (token),
    CONSTRAINT fk_refresh_token_user_id FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE
);

-- file table
CREATE TABLE file (
    id         BIGSERIAL     NOT NULL,
    uuid       UUID          NOT NULL,
    name       VARCHAR(255)  NOT NULL,
    extension  VARCHAR(255)  NOT NULL,
    path       VARCHAR(255)  NOT NULL,
    digest     VARCHAR(255)  NOT NULL,
    size       BIGINT        NOT NULL,
    user_id    BIGINT        NOT NULL,
    deleted    BOOLEAN       NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT ch_fie CHECK (id > 0),
    CONSTRAINT pk_file PRIMARY KEY (id),
    CONSTRAINT uq_file__uuid UNIQUE (uuid),
    CONSTRAINT fk_file__user_id FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE
);
