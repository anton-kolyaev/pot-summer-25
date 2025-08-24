CREATE TABLE IF NOT EXISTS claims
(
    id              UUID PRIMARY KEY        DEFAULT uuid_generate_v4(),

    claim_number    VARCHAR(100)   NOT NULL,
    status          VARCHAR(20)    NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'APPROVED', 'DENIED', 'HOLD')),
    service_date    DATE           NOT NULL,
    processed_date  TIMESTAMP      NULL,
    user_id         UUID           NOT NULL,
    enrollment_id   UUID           NOT NULL,
    amount          NUMERIC(19, 2) NOT NULL,
    approved_amount NUMERIC(19, 2) NULL,
    notes           TEXT           NULL,
    denied_reason   TEXT           NULL,
    created_by      UUID,
    updated_by      UUID,
    created_at      TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP               DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE claims
    ADD CONSTRAINT uk_claim_claim_number UNIQUE (claim_number);

ALTER TABLE claims
    ADD CONSTRAINT fk_claim_enrollment
        FOREIGN KEY (enrollment_id) REFERENCES enrollments (id);

ALTER TABLE claims
    ADD CONSTRAINT fk_claim_user
        FOREIGN KEY (user_id) REFERENCES users (id);
