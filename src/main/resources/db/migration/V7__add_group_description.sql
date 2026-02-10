-- V7: Add description field to engineer_groups table

ALTER TABLE engineer_groups
ADD COLUMN description TEXT;

-- Add comment to clarify the purpose
COMMENT ON COLUMN engineer_groups.description IS 'Description of the engineer group purpose and responsibilities';

