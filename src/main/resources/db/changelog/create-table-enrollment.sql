CREATE TABLE IF NOT EXISTS enrollments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    plan_id UUID NOT NULL,
    election_amount NUMERIC(19, 4) NOT NULL,
    plan_contribution NUMERIC(19, 4) NOT NULL,
    status VARCHAR(20) DEFAULT 'APPROVED' CHECK (status IN ('APPROVED', 'DECLINED')),
    start_date DATE NOT NULL,
    end_date DATE,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_enrollment_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_enrollment_plan FOREIGN KEY (plan_id) REFERENCES plans(id)
);
