-- Add auth0_user_id column to users_aud table
ALTER TABLE users_aud ADD COLUMN auth0_user_id VARCHAR(255);
