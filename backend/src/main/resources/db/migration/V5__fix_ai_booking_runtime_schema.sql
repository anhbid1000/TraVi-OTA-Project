-- Runtime compatibility fixes for existing local databases.
-- Older schemas may still contain don_dat_cho.trang_thai and ban.trang_thai as VARCHAR.

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'don_dat_cho'
          AND column_name = 'trang_thai'
          AND is_nullable = 'NO'
    ) THEN
        ALTER TABLE don_dat_cho ALTER COLUMN trang_thai DROP NOT NULL;
    END IF;
END $$;

DO $$
DECLARE
    ban_status_type TEXT;
BEGIN
    SELECT data_type
    INTO ban_status_type
    FROM information_schema.columns
    WHERE table_name = 'ban'
      AND column_name = 'trang_thai';

    IF ban_status_type IS NOT NULL AND ban_status_type <> 'integer' THEN
        ALTER TABLE ban
            ALTER COLUMN trang_thai TYPE INTEGER
            USING CASE
                WHEN trang_thai IS NULL OR trim(trang_thai::TEXT) = '' THEN NULL
                WHEN trang_thai::TEXT ~ '^[0-9]+$' THEN trang_thai::TEXT::INTEGER
                WHEN upper(trang_thai::TEXT) IN ('TRONG', 'SAN_SANG') THEN 0
                WHEN upper(trang_thai::TEXT) IN ('SAP_CHECKIN', 'DA_DAT_TRUOC') THEN 1
                WHEN upper(trang_thai::TEXT) IN ('DANG_SUDUNG', 'DANG_SU_DUNG') THEN 2
                WHEN upper(trang_thai::TEXT) IN ('DANG_BAO_TRI') THEN 3
                ELSE 0
            END;
    END IF;
END $$;
