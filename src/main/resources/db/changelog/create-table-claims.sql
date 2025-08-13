CREATE TABLE claims
(
    id           UUID PRIMARY KEY        DEFAULT uuid_generate_v4(),

    claim_number VARCHAR(100)   NOT NULL,
    status       VARCHAR(20)    NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'APPROVED', 'DECLINED', 'HOLD')),
    service_date DATE           NOT NULL,
    user_id      UUID           NOT NULL,
    plan_id      UUID           NOT NULL,
    amount       NUMERIC(19, 2) NOT NULL,
    created_by   UUID,
    updated_by   UUID,
    created_at   TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP               DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE claims
    ADD CONSTRAINT uk_claim_claim_number UNIQUE (claim_number);

ALTER TABLE claims
    ADD CONSTRAINT fk_claim_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE claims
    ADD CONSTRAINT fk_claim_plan FOREIGN KEY (plan_id) REFERENCES plans (id);
