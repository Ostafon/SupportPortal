-- Migration: Add updated_at and is_active columns to users table
-- Description: Add audit and user status fields

-- Add updated_at column
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- Add is_active column with default value
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE;

-- Update existing rows to have updated_at same as created_at
UPDATE users SET updated_at = created_at WHERE updated_at IS NULL;

-- Create index on is_active for faster queries
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);

-- Add comment to columns
COMMENT ON COLUMN users.updated_at IS 'Timestamp of last update';
COMMENT ON COLUMN users.is_active IS 'User account active status';

