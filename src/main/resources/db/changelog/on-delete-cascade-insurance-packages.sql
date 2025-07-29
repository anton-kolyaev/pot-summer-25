ALTER TABLE insurance_packages
DROP CONSTRAINT fk_insurance_package_company;

ALTER TABLE insurance_packages
ADD CONSTRAINT fk_insurance_package_company
    FOREIGN KEY (company_id)
    REFERENCES companies(id)
    ON DELETE CASCADE;