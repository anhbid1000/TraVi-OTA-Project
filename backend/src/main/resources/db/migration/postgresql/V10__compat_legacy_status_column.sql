-- =============================================================
-- V5__compat_legacy_status_column.sql
-- Compatibility fix for legacy schemas that still use trang_thai_don
-- =============================================================

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'don_dat_cho'
          AND column_name = 'trang_thai_don'
    )
    AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'don_dat_cho'
          AND column_name = 'trang_thai'
    ) THEN
        ALTER TABLE don_dat_cho RENAME COLUMN trang_thai_don TO trang_thai;
    END IF;
END $$;
