CREATE TABLE company_insurance_package (
    company_id UUID NOT NULL,
    insurance_package_id UUID NOT NULL,
    PRIMARY KEY (company_id, insurance_package_id),
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    FOREIGN KEY (insurance_package_id) REFERENCES insurance_packages(id) ON DELETE CASCADE
);