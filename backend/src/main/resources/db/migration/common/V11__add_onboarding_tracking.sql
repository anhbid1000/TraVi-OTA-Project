-- Add onboarding completion tracking to khach_hang
ALTER TABLE khach_hang
ADD COLUMN da_hoan_thanh_onboarding BOOLEAN DEFAULT FALSE;

-- Create index for quick lookup
CREATE INDEX idx_khach_hang_onboarding ON khach_hang(da_hoan_thanh_onboarding);
