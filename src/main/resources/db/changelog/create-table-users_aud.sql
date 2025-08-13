CREATE TABLE IF NOT EXISTS users_aud
(
    id            UUID    NOT NULL,
    rev           INTEGER NOT NULL,
    revtype       SMALLINT,
    first_name    VARCHAR(100),
    last_name     VARCHAR(100),
    username      VARCHAR(50),
    email         VARCHAR(255),
    date_of_birth DATE,
    ssn           VARCHAR(11),
    phone_data    JSONB,
    address_data  JSONB,
    company_id    UUID,
    status        VARCHAR,
    created_by    UUID,
    updated_by    UUID,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_users_aud_rev FOREIGN KEY (rev) REFERENCES revinfo (rev)
);
