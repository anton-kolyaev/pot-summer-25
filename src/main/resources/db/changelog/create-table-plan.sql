CREATE TABLE IF NOT EXISTS plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50),
    contribution NUMERIC(19, 4) NOT NULL,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_plan_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_plan_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);