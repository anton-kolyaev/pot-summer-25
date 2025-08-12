CREATE TABLE IF NOT EXISTS enrollments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    plan_id UUID NOT NULL,
    election_amount NUMERIC(19, 4) NOT NULL CHECK (election_amount > 0),
    plan_contribution NUMERIC(19, 4) NOT NULL CHECK (plan_contribution >= 0),
    deleted_at TIMESTAMP WITH TIME ZONE NULL,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_enrollment_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_enrollment_plan FOREIGN KEY (plan_id) REFERENCES plans(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_enrollments_user_plan_active
    ON enrollments(user_id, plan_id)
    WHERE deleted_at IS NULL;