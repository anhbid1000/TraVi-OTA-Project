-- Migration: Partner-Only Model - Cleanup Approval Status Column
-- Date: 2026-05-29
-- Description:
--   After removing Admin approval workflow, the `trang_thai_kiem_duyet` column
--   in `ho_so_kinh_doanh` table is deprecated but kept for backward compatibility.
--   This migration:
--   1. Removes the CHECK constraint that was blocking valid values
--   2. Makes the column nullable (no longer required)
--   3. Updates default from 'DA_DUYET' to 'DANG_HOAT_DONG' (partner-only model)
--   4. Cleans up existing data with invalid values

-- Drop the old CHECK constraint that enforced approval statuses
ALTER TABLE ho_so_kinh_doanh
  DROP CONSTRAINT IF EXISTS ho_so_kinh_doanh_trang_thai_kiem_duyet_check;

-- Make the column nullable (no longer required in partner-only model)
ALTER TABLE ho_so_kinh_doanh
  ALTER COLUMN trang_thai_kiem_duyet DROP NOT NULL;

-- Remove old default
ALTER TABLE ho_so_kinh_doanh
  ALTER COLUMN trang_thai_kiem_duyet DROP DEFAULT;

-- Set new default aligned with partner-only model
-- (hồ sơ mới mặc định hoạt động ngay, không cần duyệt)
ALTER TABLE ho_so_kinh_doanh
  ALTER COLUMN trang_thai_kiem_duyet SET DEFAULT 'DANG_HOAT_DONG';

-- Clean up existing data: convert old approval status to new default
-- This handles any existing records with 'DA_DUYET' or other deprecated values
UPDATE ho_so_kinh_doanh
SET trang_thai_kiem_duyet = 'DANG_HOAT_DONG'
WHERE trang_thai_kiem_duyet = 'DA_DUYET'
   OR trang_thai_kiem_duyet NOT IN ('BAN_NHAP', 'CHO_DUYET', 'BI_TU_CHOI', 'DANG_HOAT_DONG', 'BI_KHOA_TAM_THOI');

-- Note: The column is kept for backward compatibility with existing code/queries.
-- Future refactoring can remove it entirely once all references are cleaned up.
