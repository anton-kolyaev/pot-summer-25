CREATE TABLE IF NOT EXISTS companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    country_code VARCHAR(3) NOT NULL,
    phone_data JSONB,
    address_data JSONB,
    email VARCHAR(255),
    website VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'DEACTIVATED')),
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE companies
ADD CONSTRAINT phone_data_is_array CHECK (
    phone_data IS NULL OR jsonb_typeof(phone_data) = 'array'
);

ALTER TABLE companies
ADD CONSTRAINT address_data_is_array CHECK (
    address_data IS NULL OR jsonb_typeof(address_data) = 'array'
);
