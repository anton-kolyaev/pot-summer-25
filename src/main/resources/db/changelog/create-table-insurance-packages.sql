create TABLE insurance_packages (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    company_id UUID NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    payroll_frequency VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT fk_insurance_package_company
        FOREIGN KEY (company_id)
        REFERENCES companies(id)
);