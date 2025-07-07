-- Create the insurance service database
CREATE DATABASE insurance_service;

-- Create application user
CREATE USER insurance_app WITH PASSWORD 'insurance_app_password';

-- Grant necessary privileges to the application user
GRANT CONNECT ON DATABASE insurance_service TO insurance_app;

-- Connect to the insurance_service database
\c insurance_service;

-- Enable uuid-ossp extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant schema privileges on the insurance_service database
GRANT USAGE ON SCHEMA public TO insurance_app;
GRANT CREATE ON SCHEMA public TO insurance_app;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO insurance_app;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO insurance_app;

-- Set default privileges for future tables and sequences in insurance_service database
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO insurance_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO insurance_app; 