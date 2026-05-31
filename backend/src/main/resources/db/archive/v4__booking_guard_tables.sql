-- V4__booking_guard_tables.sql
-- Khởi tạo các bảng liên quan đến đặt chỗ để phục vụ tính năng Booking Guard (Bảo vệ tài sản không bị xoá nếu có đơn tương lai)

CREATE TABLE IF NOT EXISTS don_dat_cho (
    id VARCHAR(36) PRIMARY KEY,
    ma_don VARCHAR(255) UNIQUE,
    ngay_lap TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tong_tien DOUBLE PRECISION DEFAULT 0.0,
    trang_thai VARCHAR(50) NOT NULL DEFAULT 'DANG_CHO',
    so_tien_da_thanh_toan DOUBLE PRECISION DEFAULT 0.0,
    email_nguoi_dat VARCHAR(255),
    sdt_nguoi_dat VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS dat_phong (
    id VARCHAR(36) PRIMARY KEY,
    don_dat_cho_id VARCHAR(36) NOT NULL,
    phong_id VARCHAR(36) NOT NULL,
    ngay_check_in DATE,
    ngay_check_out DATE,
    ghi_chu_khach_hang_phong_don_dat TEXT,
    CONSTRAINT fk_dat_phong_don FOREIGN KEY (don_dat_cho_id) REFERENCES don_dat_cho(id),
    CONSTRAINT fk_dat_phong_phong FOREIGN KEY (phong_id) REFERENCES phong(id)
);

CREATE TABLE IF NOT EXISTS don_dat_mon (
    id VARCHAR(36) PRIMARY KEY,
    don_dat_cho_id VARCHAR(36) NOT NULL,
    nha_hang_id VARCHAR(36) NOT NULL,
    ban_id VARCHAR(36),
    thoi_gian_dat TIMESTAMP,
    ghi_chu TEXT,
    CONSTRAINT fk_don_dat_mon_don FOREIGN KEY (don_dat_cho_id) REFERENCES don_dat_cho(id),
    CONSTRAINT fk_don_dat_mon_nha_hang FOREIGN KEY (nha_hang_id) REFERENCES nha_hang(id_tai_san),
    CONSTRAINT fk_don_dat_mon_ban FOREIGN KEY (ban_id) REFERENCES ban(id)
);

CREATE TABLE IF NOT EXISTS chi_tiet_don_dat_mon (
    id VARCHAR(36) PRIMARY KEY,
    don_dat_mon_id VARCHAR(36) NOT NULL,
    mon_an_id VARCHAR(36) NOT NULL,
    so_luong INTEGER,
    gia_tien DOUBLE PRECISION,
    CONSTRAINT fk_ct_don_dat_mon_don FOREIGN KEY (don_dat_mon_id) REFERENCES don_dat_mon(id),
    CONSTRAINT fk_ct_don_dat_mon_mon_an FOREIGN KEY (mon_an_id) REFERENCES mon_an(id)
);

-- Thêm index để tối ưu query cho Booking Guard
CREATE INDEX IF NOT EXISTS idx_dat_phong_checkin ON dat_phong(ngay_check_in);
CREATE INDEX IF NOT EXISTS idx_don_dat_mon_thoi_gian ON don_dat_mon(thoi_gian_dat);
