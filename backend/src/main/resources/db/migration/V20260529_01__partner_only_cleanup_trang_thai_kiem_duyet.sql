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

DO $$
BEGIN
  -- Luon an toan khi constraint khong ton tai.
  EXECUTE 'ALTER TABLE ho_so_kinh_doanh
           DROP CONSTRAINT IF EXISTS ho_so_kinh_doanh_trang_thai_kiem_duyet_check';

  -- Chi thuc thi khi cot trang_thai_kiem_duyet con ton tai.
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'ho_so_kinh_doanh'
      AND column_name = 'trang_thai_kiem_duyet'
  ) THEN
    EXECUTE 'ALTER TABLE ho_so_kinh_doanh
             ALTER COLUMN trang_thai_kiem_duyet DROP NOT NULL';
    EXECUTE 'ALTER TABLE ho_so_kinh_doanh
             ALTER COLUMN trang_thai_kiem_duyet DROP DEFAULT';
    EXECUTE 'ALTER TABLE ho_so_kinh_doanh
             ALTER COLUMN trang_thai_kiem_duyet SET DEFAULT ''DANG_HOAT_DONG''';
    EXECUTE 'UPDATE ho_so_kinh_doanh
             SET trang_thai_kiem_duyet = ''DANG_HOAT_DONG''
             WHERE trang_thai_kiem_duyet = ''DA_DUYET''
                OR trang_thai_kiem_duyet NOT IN
                   (''BAN_NHAP'', ''CHO_DUYET'', ''BI_TU_CHOI'', ''DANG_HOAT_DONG'', ''BI_KHOA_TAM_THOI'')';
  END IF;
END
$$;

-- Note: The column is kept for backward compatibility with existing code/queries.
-- Future refactoring can remove it entirely once all references are cleaned up.
