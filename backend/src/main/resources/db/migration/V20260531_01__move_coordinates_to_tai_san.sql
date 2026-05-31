-- =============================================================
-- V5__move_coordinates_to_tai_san.sql
-- Move GPS coordinates and address fields from ho_so_kinh_doanh to tai_san
-- This allows each asset (hotel/restaurant) to have its own location
-- =============================================================

-- 1) Add new columns to tai_san table
ALTER TABLE tai_san ADD COLUMN IF NOT EXISTS dia_chi VARCHAR(255);
ALTER TABLE tai_san ADD COLUMN IF NOT EXISTS thanh_pho VARCHAR(100);
ALTER TABLE tai_san ADD COLUMN IF NOT EXISTS kinh_do DOUBLE PRECISION;
ALTER TABLE tai_san ADD COLUMN IF NOT EXISTS vi_do DOUBLE PRECISION;

-- 2) Migrate data from ho_so_kinh_doanh to tai_san
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'ho_so_kinh_doanh' AND column_name = 'dia_chi'
    ) THEN
        EXECUTE 'UPDATE tai_san ts
                 SET dia_chi = hs.dia_chi
                 FROM ho_so_kinh_doanh hs
                 WHERE ts.ho_so_kinh_doanh_id = hs.id_ho_so';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'ho_so_kinh_doanh' AND column_name = 'thanh_pho'
    ) THEN
        EXECUTE 'UPDATE tai_san ts
                 SET thanh_pho = hs.thanh_pho
                 FROM ho_so_kinh_doanh hs
                 WHERE ts.ho_so_kinh_doanh_id = hs.id_ho_so';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'ho_so_kinh_doanh' AND column_name = 'kinh_do'
    ) THEN
        EXECUTE 'UPDATE tai_san ts
                 SET kinh_do = hs.kinh_do
                 FROM ho_so_kinh_doanh hs
                 WHERE ts.ho_so_kinh_doanh_id = hs.id_ho_so';
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'ho_so_kinh_doanh' AND column_name = 'vi_do'
    ) THEN
        EXECUTE 'UPDATE tai_san ts
                 SET vi_do = hs.vi_do
                 FROM ho_so_kinh_doanh hs
                 WHERE ts.ho_so_kinh_doanh_id = hs.id_ho_so';
    END IF;
END
$$;

-- 3) Create indexes on new columns for search performance
CREATE INDEX IF NOT EXISTS idx_tai_san_thanh_pho ON tai_san(thanh_pho);
CREATE INDEX IF NOT EXISTS idx_tai_san_kinh_do_vi_do ON tai_san(kinh_do, vi_do);

-- 4) Optional: Keep old columns in ho_so_kinh_doanh as "head office" reference
-- (If you want to distinguish between head office and branch locations)
-- Otherwise, you can drop them in a future migration after confirming data integrity

COMMENT ON COLUMN tai_san.dia_chi IS 'Địa chỉ của tài sản (khách sạn/nhà hàng)';
COMMENT ON COLUMN tai_san.thanh_pho IS 'Thành phố nơi tài sản đặt';
COMMENT ON COLUMN tai_san.kinh_do IS 'Kinh độ GPS của tài sản';
COMMENT ON COLUMN tai_san.vi_do IS 'Vĩ độ GPS của tài sản';
