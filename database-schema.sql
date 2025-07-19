-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Companies table
CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    country_code VARCHAR(3) NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    website VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    who_created UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (who_created) REFERENCES users(id)
);

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    date_of_birth DATE NOT NULL,
    ssn VARCHAR(11) UNIQUE NOT NULL,
    company_id UUID NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    who_created UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id),
    FOREIGN KEY (who_created) REFERENCES users(id)
);

-- Roles table
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    who_created UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (who_created) REFERENCES users(id)
);

-- User roles junction table
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    who_created UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (who_created) REFERENCES users(id)
);

-- Insurance packages table
CREATE TABLE insurance_packages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    company_id UUID NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    payroll_frequency VARCHAR(20) NOT NULL CHECK (payroll_frequency IN ('WEEKLY', 'BIWEEKLY', 'MONTHLY')),
    status VARCHAR(20) DEFAULT 'INITIALIZED' CHECK (status IN ('INITIALIZED', 'ACTIVE', 'INACTIVE')),
    who_created UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id),
    FOREIGN KEY (who_created) REFERENCES users(id)
);

-- Benefit packages table
CREATE TABLE benefit_packages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL CHECK (type IN ('MEDICAL', 'DENTAL', 'VISION', 'PHARMACY', 'LIFE', 'DISABILITY')),
    coverage_percentage DECIMAL(5,2),
    deductible_amount DECIMAL(10,2),
    max_benefit_amount DECIMAL(10,2),
    who_created UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (who_created) REFERENCES users(id)
);

-- Package benefits junction table
CREATE TABLE package_benefits (
    package_id UUID NOT NULL,
    benefit_id UUID NOT NULL,
    who_created UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (package_id, benefit_id),
    FOREIGN KEY (package_id) REFERENCES insurance_packages(id) ON DELETE CASCADE,
    FOREIGN KEY (benefit_id) REFERENCES benefit_packages(id) ON DELETE CASCADE,
    FOREIGN KEY (who_created) REFERENCES users(id)
);

-- Enrollments table
CREATE TABLE enrollments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    company_id UUID NOT NULL,
    package_id UUID NOT NULL,
    election_amount DECIMAL(10,2) NOT NULL,
    contribution_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('ACTIVE', 'INACTIVE', 'PENDING')),
    effective_date DATE NOT NULL,
    end_date DATE,
    who_created UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (company_id) REFERENCES companies(id),
    FOREIGN KEY (package_id) REFERENCES insurance_packages(id),
    FOREIGN KEY (who_created) REFERENCES users(id)
);

-- Claims table
CREATE TABLE claims (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    claim_number VARCHAR(50) UNIQUE NOT NULL,
    user_id UUID NOT NULL,
    company_id UUID NOT NULL,
    enrollment_id UUID,
    type VARCHAR(20) NOT NULL CHECK (type IN ('MEDICAL', 'PHARMACY', 'DENTAL', 'VISION')),
    amount DECIMAL(10,2) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'DENIED')),
    submitted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_date TIMESTAMP NULL,
    approved_amount DECIMAL(10,2),
    denied_reason TEXT,
    notes TEXT,
    who_created UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (company_id) REFERENCES companies(id),
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(id),
    FOREIGN KEY (who_created) REFERENCES users(id)
);

-- Claim attachments table
CREATE TABLE claim_attachments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    claim_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    who_created UUID,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (claim_id) REFERENCES claims(id) ON DELETE CASCADE,
    FOREIGN KEY (who_created) REFERENCES users(id)
);

-- Audit log table
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID,
    action VARCHAR(100) NOT NULL,
    table_name VARCHAR(100) NOT NULL,
    record_id UUID,
    old_values JSONB,
    new_values JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    who_created UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (who_created) REFERENCES users(id)
);

-- Create indexes for better performance
CREATE INDEX idx_companies_name ON companies(name);
CREATE INDEX idx_companies_country_code ON companies(country_code);
CREATE INDEX idx_companies_status ON companies(status);
CREATE INDEX idx_companies_created_at ON companies(created_at);
CREATE INDEX idx_companies_who_created ON companies(who_created);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_ssn ON users(ssn);
CREATE INDEX idx_users_company_id ON users(company_id);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_who_created ON users(who_created);

CREATE INDEX idx_roles_who_created ON roles(who_created);

CREATE INDEX idx_user_roles_who_created ON user_roles(who_created);

CREATE INDEX idx_packages_company_id ON insurance_packages(company_id);
CREATE INDEX idx_packages_status ON insurance_packages(status);
CREATE INDEX idx_packages_dates ON insurance_packages(start_date, end_date);
CREATE INDEX idx_packages_created_at ON insurance_packages(created_at);
CREATE INDEX idx_packages_who_created ON insurance_packages(who_created);

CREATE INDEX idx_benefit_packages_who_created ON benefit_packages(who_created);

CREATE INDEX idx_package_benefits_who_created ON package_benefits(who_created);

CREATE INDEX idx_claims_claim_number ON claims(claim_number);
CREATE INDEX idx_claims_user_id ON claims(user_id);
CREATE INDEX idx_claims_company_id ON claims(company_id);
CREATE INDEX idx_claims_status ON claims(status);
CREATE INDEX idx_claims_submitted_date ON claims(submitted_date);
CREATE INDEX idx_claims_created_at ON claims(created_at);
CREATE INDEX idx_claims_who_created ON claims(who_created);

CREATE INDEX idx_enrollments_user_id ON enrollments(user_id);
CREATE INDEX idx_enrollments_company_id ON enrollments(company_id);
CREATE INDEX idx_enrollments_package_id ON enrollments(package_id);
CREATE INDEX idx_enrollments_status ON enrollments(status);
CREATE INDEX idx_enrollments_created_at ON enrollments(created_at);
CREATE INDEX idx_enrollments_who_created ON enrollments(who_created);

CREATE INDEX idx_attachments_claim_id ON claim_attachments(claim_id);
CREATE INDEX idx_attachments_who_created ON claim_attachments(who_created);

CREATE INDEX idx_audit_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_table_name ON audit_logs(table_name);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at);
CREATE INDEX idx_audit_who_created ON audit_logs(who_created);

-- Insert default roles
INSERT INTO roles (id, name, description) VALUES
(gen_random_uuid(), 'ADMIN', 'System administrator with full access'),
(gen_random_uuid(), 'COMPANY_MANAGER', 'Company manager with company-level access'),
(gen_random_uuid(), 'COMPANY_CLAIM_MANAGER', 'Company claim manager'),
(gen_random_uuid(), 'COMPANY_SETTING_MANAGER', 'Company setting manager'),
(gen_random_uuid(), 'COMPANY_REPORT_MANAGER', 'Company report manager'),
(gen_random_uuid(), 'CONSUMER', 'Regular consumer user'),
(gen_random_uuid(), 'CONSUMER_CLAIM_MANAGER', 'Consumer claim manager');
