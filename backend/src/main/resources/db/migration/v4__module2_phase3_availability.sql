-- Module 2/3 alignment - booking core model with joined inheritance

CREATE TABLE IF NOT EXISTS don_dat_cho (
    id VARCHAR(255) PRIMARY KEY,
    ma_don VARCHAR(50) NOT NULL UNIQUE,
    khach_hang_id VARCHAR(255) NOT NULL,
    ho_so_kinh_doanh_id VARCHAR(255) NOT NULL,
    ngay_tao TIMESTAMP NOT NULL,
    tong_tien_goc DOUBLE PRECISION,
    tien_khuyen_mai DOUBLE PRECISION,
    tong_tien_thanh_toan DOUBLE PRECISION,
    ten_nguoi_dat VARCHAR(255),
    sdt_nguoi_dat VARCHAR(255),
    email_nguoi_dat VARCHAR(255),
    ghi_chu VARCHAR(1000),
    trang_thai_don VARCHAR(50) NOT NULL,
    hold_expired_at TIMESTAMP,
    payment_expired_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancel_reason VARCHAR(500),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_don_dat_cho_khach_hang FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id),
    CONSTRAINT fk_don_dat_cho_ho_so FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES ho_so_kinh_doanh(id_ho_so)
);

CREATE TABLE IF NOT EXISTS don_khach_san (
    id VARCHAR(255) PRIMARY KEY,
    ngay_check_in DATE,
    ngay_check_out DATE,
    so_dem INTEGER,
    so_khach INTEGER,
    gio_nhan_phong_du_kien TIME,
    CONSTRAINT fk_don_khach_san_don_dat_cho FOREIGN KEY (id) REFERENCES don_dat_cho(id)
);

CREATE TABLE IF NOT EXISTS don_khach_san_chi_tiet (
    id VARCHAR(255) PRIMARY KEY,
    don_khach_san_id VARCHAR(255) NOT NULL,
    phong_id VARCHAR(255) NOT NULL,
    ten_phong_tai_thoi_diem_dat VARCHAR(255),
    so_luong INTEGER,
    don_gia_tai_thoi_diem_dat DOUBLE PRECISION,
    so_dem INTEGER,
    thanh_tien DOUBLE PRECISION,
    CONSTRAINT fk_don_khach_san_ct_don FOREIGN KEY (don_khach_san_id) REFERENCES don_khach_san(id),
    CONSTRAINT fk_don_khach_san_ct_phong FOREIGN KEY (phong_id) REFERENCES phong(id)
);

CREATE INDEX IF NOT EXISTS idx_don_khach_san_overlap
    ON don_khach_san (ngay_check_in, ngay_check_out);

CREATE INDEX IF NOT EXISTS idx_don_khach_san_ct_phong
    ON don_khach_san_chi_tiet (phong_id, don_khach_san_id);

CREATE TABLE IF NOT EXISTS don_nha_hang (
    id VARCHAR(255) PRIMARY KEY,
    ngay_gio_bat_dau TIMESTAMP,
    ngay_gio_ket_thuc TIMESTAMP,
    so_nguoi INTEGER,
    tien_coc DOUBLE PRECISION,
    co_dat_mon_truoc BOOLEAN,
    CONSTRAINT fk_don_nha_hang_don_dat_cho FOREIGN KEY (id) REFERENCES don_dat_cho(id)
);

CREATE TABLE IF NOT EXISTS don_nha_hang_ban (
    id VARCHAR(255) PRIMARY KEY,
    don_nha_hang_id VARCHAR(255) NOT NULL,
    ban_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_don_nha_hang_ban_don FOREIGN KEY (don_nha_hang_id) REFERENCES don_nha_hang(id),
    CONSTRAINT fk_don_nha_hang_ban_ban FOREIGN KEY (ban_id) REFERENCES ban(id)
);

CREATE INDEX IF NOT EXISTS idx_don_nha_hang_overlap
    ON don_nha_hang (ngay_gio_bat_dau, ngay_gio_ket_thuc);

CREATE INDEX IF NOT EXISTS idx_don_nha_hang_ban_lookup
    ON don_nha_hang_ban (ban_id, don_nha_hang_id);


