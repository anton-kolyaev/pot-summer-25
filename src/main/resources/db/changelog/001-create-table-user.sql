CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    date_of_birth DATE NOT NULL,
    ssn VARCHAR(11) UNIQUE NOT NULL,
    phone_data JSONB,
    address_data JSONB,
    company_id UUID NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_company_id FOREIGN KEY (company_id) REFERENCES companies(id)
);

ALTER TABLE companies
ADD CONSTRAINT fk_created_by FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE companies
ADD CONSTRAINT fk_updated_by FOREIGN KEY (updated_by) REFERENCES users(id);