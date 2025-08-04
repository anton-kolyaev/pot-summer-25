CREATE TABLE insurance_package_plans (
    insurance_package_id UUID NOT NULL,
    plan_id UUID NOT NULL,

    CONSTRAINT pk_insurance_package_plans PRIMARY KEY (insurance_package_id, plan_id),

    CONSTRAINT fk_ipp_insurance_package FOREIGN KEY (insurance_package_id)
        REFERENCES insurance_packages(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ipp_plan FOREIGN KEY (plan_id)
        REFERENCES plans(id)
        ON DELETE CASCADE
);