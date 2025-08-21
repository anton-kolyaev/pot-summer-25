-- Add auth0_user_id column to users table
ALTER TABLE users ADD COLUMN auth0_user_id VARCHAR(255) UNIQUE;
