CREATE TABLE IF NOT EXISTS companies_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    name VARCHAR(255) NOT NULL,
    country_code VARCHAR(3) NOT NULL,
    phone_data JSONB,
    address_data JSONB,
    email VARCHAR(255),
    website VARCHAR(255),
    status VARCHAR(20),
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_companies_aud_rev FOREIGN KEY (rev) REFERENCES revinfo (id),
    CONSTRAINT companies_aud_phone_data_is_array CHECK (
        phone_data IS NULL OR jsonb_typeof(phone_data) = 'array'
    ),
    CONSTRAINT companies_aud_address_data_is_array CHECK (
        address_data IS NULL OR jsonb_typeof(address_data) = 'array'
    )
);