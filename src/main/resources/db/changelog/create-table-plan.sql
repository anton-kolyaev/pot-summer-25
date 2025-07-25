CREATE TABLE IF NOT EXISTS plans (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    plan_type_id INTEGER NOT NULL,
    contribution NUMERIC(19, 4) NOT NULL,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_plan_type FOREIGN KEY (plan_type_id) REFERENCES plan_types(id)
);