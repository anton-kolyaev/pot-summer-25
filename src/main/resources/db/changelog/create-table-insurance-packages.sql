CREATE TABLE insurance_packages (
    id UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    payroll_frequency VARCHAR(20) NOT NULL,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);