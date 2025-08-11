CREATE TABLE IF NOT EXISTS users_aud (
    id UUID,
    rev INTEGER,
    revtype SMALLINT,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    username VARCHAR(50),
    email VARCHAR(255),
    date_of_birth DATE,
    ssn VARCHAR(11),
    phone_data JSONB,
    address_data JSONB,
    company_id UUID,
    status VARCHAR(20),
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_users_aud_rev FOREIGN KEY (rev) REFERENCES revinfo (id),
    CONSTRAINT users_aud_phone_data_is_array CHECK (
        phone_data IS NULL OR jsonb_typeof(phone_data) = 'array'
    ),
    CONSTRAINT users_aud_address_data_is_array CHECK (
        address_data IS NULL OR jsonb_typeof(address_data) = 'array'
    )
);