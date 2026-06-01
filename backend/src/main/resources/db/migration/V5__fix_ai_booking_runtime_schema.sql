-- Runtime compatibility fixes for existing local databases.
-- Older schemas may still contain columns that do not match the merged entity model.

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

    IF ban_status_type IS NOT NULL THEN
        ALTER TABLE ban ALTER COLUMN trang_thai DROP DEFAULT;

        IF ban_status_type IN ('integer', 'smallint', 'bigint') THEN
            ALTER TABLE ban
                ALTER COLUMN trang_thai TYPE VARCHAR(50)
                USING CASE trang_thai
                    WHEN 0 THEN 'SAN_SANG'
                    WHEN 1 THEN 'TAM_DUNG'
                    WHEN 2 THEN 'NGUNG_SU_DUNG'
                    ELSE 'SAN_SANG'
                END;
        ELSE
            UPDATE ban
            SET trang_thai = CASE
                WHEN trang_thai IS NULL OR trim(trang_thai::TEXT) = '' THEN 'SAN_SANG'
                WHEN upper(trang_thai::TEXT) IN ('TRONG', 'SAN_SANG') THEN 'SAN_SANG'
                WHEN upper(trang_thai::TEXT) IN ('SAP_CHECKIN', 'DA_DAT_TRUOC', 'TAM_DUNG') THEN 'TAM_DUNG'
                WHEN upper(trang_thai::TEXT) IN ('DANG_SUDUNG', 'DANG_SU_DUNG', 'DANG_BAO_TRI', 'NGUNG_SU_DUNG') THEN 'NGUNG_SU_DUNG'
                ELSE 'SAN_SANG'
            END;

            ALTER TABLE ban
                ALTER COLUMN trang_thai TYPE VARCHAR(50)
                USING trang_thai::TEXT;
        END IF;

        ALTER TABLE ban
            ALTER COLUMN trang_thai SET DEFAULT 'SAN_SANG',
            ALTER COLUMN trang_thai SET NOT NULL;
    END IF;
END $$;
