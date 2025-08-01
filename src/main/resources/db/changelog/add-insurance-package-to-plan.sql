ALTER TABLE plans
ADD COLUMN insurance_package_id UUID NOT NULL;

ALTER TABLE plans
ADD CONSTRAINT fk_insurance_package
FOREIGN KEY (insurance_package_id) REFERENCES insurance_packages(id);