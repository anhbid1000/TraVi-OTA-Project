-- Keep the legacy booking module tables separate from the core catalog booking tables.
-- The booking module uses bigint identifiers, while core don_dat_cho uses varchar UUIDs.

CREATE TABLE IF NOT EXISTS booking_don_dat_cho (
    id BIGSERIAL PRIMARY KEY,
    ma_don VARCHAR(255) NOT NULL UNIQUE,
    ngay_tao TIMESTAMP NOT NULL,
    tong_tien NUMERIC(38, 2) NOT NULL,
    tien_khuyen_mai NUMERIC(38, 2),
    ten_nguoi_dat VARCHAR(255) NOT NULL,
    sdt_nguoi_dat VARCHAR(255) NOT NULL,
    email_nguoi_dat VARCHAR(255),
    trang_thai VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS booking_don_khach_san (
    id BIGINT PRIMARY KEY,
    id_phong BIGINT,
    ngay_check_in DATE,
    ngay_check_out DATE
);

CREATE TABLE IF NOT EXISTS booking_don_nha_hang (
    id BIGINT PRIMARY KEY,
    id_ban BIGINT,
    ngay_gio_dat_cho TIMESTAMP,
    so_nguoi INTEGER
);

CREATE TABLE IF NOT EXISTS booking_giao_dich_thanh_toan (
    id BIGSERIAL PRIMARY KEY,
    don_dat_cho_id BIGINT NOT NULL,
    ma_giao_dich_ngan_hang VARCHAR(255),
    phuong_thuc VARCHAR(255),
    so_tien NUMERIC(38, 2)
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_booking_don_khach_san_parent') THEN
        ALTER TABLE booking_don_khach_san
            ADD CONSTRAINT fk_booking_don_khach_san_parent
            FOREIGN KEY (id) REFERENCES booking_don_dat_cho(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_booking_don_nha_hang_parent') THEN
        ALTER TABLE booking_don_nha_hang
            ADD CONSTRAINT fk_booking_don_nha_hang_parent
            FOREIGN KEY (id) REFERENCES booking_don_dat_cho(id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_booking_giao_dich_don_dat_cho') THEN
        ALTER TABLE booking_giao_dich_thanh_toan
            ADD CONSTRAINT fk_booking_giao_dich_don_dat_cho
            FOREIGN KEY (don_dat_cho_id) REFERENCES booking_don_dat_cho(id) ON DELETE CASCADE;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_booking_don_dat_cho_status_time
    ON booking_don_dat_cho(trang_thai, ngay_tao);

CREATE INDEX IF NOT EXISTS idx_booking_giao_dich_ma_ngan_hang
    ON booking_giao_dich_thanh_toan(ma_giao_dich_ngan_hang);
