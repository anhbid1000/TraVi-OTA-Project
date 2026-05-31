-- =============================================================
-- V2__complete_catalog_filter_schema.sql
-- Add catalog/search schema missing from V1 according to entities.
-- Non-destructive only: create/alter/index, no drop.
-- =============================================================

-- 1) Business profile and assets backbone
CREATE TABLE IF NOT EXISTS ho_so_kinh_doanh (
    id_ho_so VARCHAR(36) PRIMARY KEY,
    doi_tac_id VARCHAR(36) NOT NULL,
    loai_dich_vu VARCHAR(50) NOT NULL,
    ten_co_so VARCHAR(255) NOT NULL,
    ma_so_thue VARCHAR(50) NOT NULL UNIQUE,
    giay_phep_kinh_doanh VARCHAR(255) NOT NULL,
    toa_do_gps VARCHAR(255) NOT NULL,
    sdt_lien_he VARCHAR(20) NOT NULL,
    email_lien_he VARCHAR(255),
    dia_chi VARCHAR(255),
    thanh_pho VARCHAR(100),
    quan_huyen VARCHAR(100),
    phuong_xa VARCHAR(100),
    kinh_do DOUBLE PRECISION,
    vi_do DOUBLE PRECISION,
    old_ten_co_so VARCHAR(255),
    old_sdt_lien_he VARCHAR(20),
    old_loai_dich_vu VARCHAR(50),
    old_ma_so_thue VARCHAR(50),
    old_giay_phep_kinh_doanh VARCHAR(255),
    old_toa_do_gps VARCHAR(255),
    trang_thai_kiem_duyet VARCHAR(50) NOT NULL DEFAULT 'CHO_DUYET',
    trang_thai_hoat_dong VARCHAR(50) NOT NULL DEFAULT 'CHUA_HOAT_DONG',
    ly_do_tu_choi_gan_nhat VARCHAR(1000),
    thoi_gian_dang_ky TIMESTAMP,
    thoi_gian_cap_nhat TIMESTAMP,
    thoi_gian_duyet TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

ALTER TABLE ho_so_kinh_doanh DROP CONSTRAINT IF EXISTS fk_ho_so_kinh_doanh_doi_tac;
ALTER TABLE ho_so_kinh_doanh ADD CONSTRAINT fk_ho_so_kinh_doanh_doi_tac FOREIGN KEY (doi_tac_id) REFERENCES doi_tac(id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS tai_san (
    id_tai_san VARCHAR(36) PRIMARY KEY,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL UNIQUE,
    mo_ta TEXT,
    trang_thai VARCHAR(50) DEFAULT 'SAN_SANG',
    gia_co_ban DOUBLE PRECISION,
    is_dynamic_pricing BOOLEAN NOT NULL DEFAULT FALSE
);

ALTER TABLE tai_san DROP CONSTRAINT IF EXISTS fk_tai_san_ho_so_kinh_doanh;
ALTER TABLE tai_san ADD CONSTRAINT fk_tai_san_ho_so_kinh_doanh FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE;

-- Enforce 1-1 relationship: mỗi hồ sơ kinh doanh chỉ có 1 tài sản
ALTER TABLE tai_san DROP CONSTRAINT IF EXISTS uk_tai_san_ho_so_kinh_doanh;
ALTER TABLE tai_san ADD CONSTRAINT uk_tai_san_ho_so_kinh_doanh UNIQUE (ho_so_kinh_doanh_id);

-- 2) Hotel/restaurant catalog tables
CREATE TABLE IF NOT EXISTS khach_san (
    id_tai_san VARCHAR(36) PRIMARY KEY,
    ten VARCHAR(255) NOT NULL,
    hang_sao INTEGER,
    loai_khach_san VARCHAR(100),
    gio_nhan_phong TIME,
    gio_tra_phong TIME,
    gio_nhan_phong_mac_dinh TIME,
    gio_tra_phong_mac_dinh TIME,
    so_tang INTEGER,
    tong_so_phong INTEGER,
    diem_danh_gia_trung_binh DECIMAL(3,2) DEFAULT 0.0,
    so_luong_danh_gia INTEGER DEFAULT 0
);

ALTER TABLE khach_san DROP CONSTRAINT IF EXISTS fk_khach_san_tai_san;
ALTER TABLE khach_san ADD CONSTRAINT fk_khach_san_tai_san FOREIGN KEY (id_tai_san) REFERENCES tai_san(id_tai_san) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS nha_hang (
    id_tai_san VARCHAR(36) PRIMARY KEY,
    ten VARCHAR(255) NOT NULL,
    loai_am_thuc VARCHAR(150),
    gio_mo_cua TIME,
    gio_dong_cua TIME,
    suc_chua INTEGER,
    co_dat_ban_truoc BOOLEAN NOT NULL DEFAULT TRUE,
    co_dat_mon_truoc BOOLEAN NOT NULL DEFAULT TRUE,
    diem_danh_gia_trung_binh DECIMAL(3,2) DEFAULT 0.0,
    so_luong_danh_gia INTEGER DEFAULT 0
);

ALTER TABLE nha_hang DROP CONSTRAINT IF EXISTS fk_nha_hang_tai_san;
ALTER TABLE nha_hang ADD CONSTRAINT fk_nha_hang_tai_san FOREIGN KEY (id_tai_san) REFERENCES tai_san(id_tai_san) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS tien_ich_khach_san (
    id VARCHAR(36) PRIMARY KEY,
    ten_tien_ich VARCHAR(100) NOT NULL UNIQUE,
    loai_tien_ich VARCHAR(100),
    mo_ta TEXT
);

CREATE TABLE IF NOT EXISTS khach_san_tien_ich (
    khach_san_id VARCHAR(36) NOT NULL,
    tien_ich_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (khach_san_id, tien_ich_id)
);

ALTER TABLE khach_san_tien_ich DROP CONSTRAINT IF EXISTS fk_khach_san_tien_ich_khach_san;
ALTER TABLE khach_san_tien_ich ADD CONSTRAINT fk_khach_san_tien_ich_khach_san FOREIGN KEY (khach_san_id) REFERENCES khach_san(id_tai_san) ON DELETE CASCADE;
ALTER TABLE khach_san_tien_ich DROP CONSTRAINT IF EXISTS fk_khach_san_tien_ich_tien_ich;
ALTER TABLE khach_san_tien_ich ADD CONSTRAINT fk_khach_san_tien_ich_tien_ich FOREIGN KEY (tien_ich_id) REFERENCES tien_ich_khach_san(id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS tien_ich_nha_hang (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36) NOT NULL,
    ten_tien_ich VARCHAR(100) NOT NULL,
    loai_tien_ich VARCHAR(100),
    mo_ta TEXT,
    co_thu_phi BOOLEAN DEFAULT FALSE,
    phi_su_dung REAL DEFAULT 0.0
);

ALTER TABLE tien_ich_nha_hang DROP CONSTRAINT IF EXISTS fk_tien_ich_nha_hang_nha_hang;
ALTER TABLE tien_ich_nha_hang ADD CONSTRAINT fk_tien_ich_nha_hang_nha_hang FOREIGN KEY (nha_hang_id) REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE;

-- 3) Room, table, menu and dish tables
CREATE TABLE IF NOT EXISTS phong (
    id VARCHAR(36) PRIMARY KEY,
    khach_san_id VARCHAR(36) NOT NULL,
    so_phong VARCHAR(100) NOT NULL,
    ten_phong VARCHAR(255) NOT NULL,
    loai_phong VARCHAR(100) NOT NULL,
    mo_ta VARCHAR(1000),
    suc_chua_toi_da INTEGER,
    so_giuong INTEGER,
    dien_tich REAL,
    gia_co_ban DOUBLE PRECISION,
    so_luong_phong INTEGER,
    trang_thai VARCHAR(50) DEFAULT 'SAN_SANG',
    phan_tram_giam_gia REAL DEFAULT 0.0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

ALTER TABLE phong DROP CONSTRAINT IF EXISTS fk_phong_khach_san;
ALTER TABLE phong ADD CONSTRAINT fk_phong_khach_san FOREIGN KEY (khach_san_id) REFERENCES khach_san(id_tai_san) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS phong_tien_ich (
    phong_id VARCHAR(36) NOT NULL,
    tien_ich VARCHAR(100)
);

ALTER TABLE phong_tien_ich DROP CONSTRAINT IF EXISTS fk_phong_tien_ich_phong;
ALTER TABLE phong_tien_ich ADD CONSTRAINT fk_phong_tien_ich_phong FOREIGN KEY (phong_id) REFERENCES phong(id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS ban (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36) NOT NULL,
    ten_ban VARCHAR(255),
    vi_tri_sanh VARCHAR(255),
    mo_ta VARCHAR(1000),
    trang_thai INTEGER,
    so_cho_ngoi INTEGER,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

ALTER TABLE ban DROP CONSTRAINT IF EXISTS fk_ban_nha_hang;
ALTER TABLE ban ADD CONSTRAINT fk_ban_nha_hang FOREIGN KEY (nha_hang_id) REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS thuc_don (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36) NOT NULL,
    ten_thuc_don VARCHAR(255),
    phan_loai VARCHAR(100),
    trang_thai VARCHAR(50) DEFAULT 'DANG_HIEN_THI',
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

ALTER TABLE thuc_don DROP CONSTRAINT IF EXISTS fk_thuc_don_nha_hang;
ALTER TABLE thuc_don ADD CONSTRAINT fk_thuc_don_nha_hang FOREIGN KEY (nha_hang_id) REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS mon_an (
    id VARCHAR(36) PRIMARY KEY,
    thuc_don_id VARCHAR(36) NOT NULL,
    ten_mon VARCHAR(255) NOT NULL,
    mo_ta VARCHAR(1000),
    gia_ban DOUBLE PRECISION,
    danh_muc_mon VARCHAR(100),
    trang_thai VARCHAR(50) DEFAULT 'DANG_BAN',
    duong_dan_url VARCHAR(1000),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

ALTER TABLE mon_an DROP CONSTRAINT IF EXISTS fk_mon_an_thuc_don;
ALTER TABLE mon_an ADD CONSTRAINT fk_mon_an_thuc_don FOREIGN KEY (thuc_don_id) REFERENCES thuc_don(id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS mon_an_the_ngu_canh (
    mon_an_id VARCHAR(36) NOT NULL,
    the_ngu_canh VARCHAR(255)
);

ALTER TABLE mon_an_the_ngu_canh DROP CONSTRAINT IF EXISTS fk_mon_an_the_ngu_canh_mon_an;
ALTER TABLE mon_an_the_ngu_canh ADD CONSTRAINT fk_mon_an_the_ngu_canh_mon_an FOREIGN KEY (mon_an_id) REFERENCES mon_an(id) ON DELETE CASCADE;

-- 4) Images and policy tables
CREATE TABLE IF NOT EXISTS anh_khach_san (
    id VARCHAR(36) PRIMARY KEY,
    khach_san_id VARCHAR(36) NOT NULL,
    duong_dan_url VARCHAR(1000) NOT NULL,
    mo_ta_anh VARCHAR(500),
    la_anh_dai_dien BOOLEAN DEFAULT FALSE,
    ngay_tai_len DATE
);

CREATE TABLE IF NOT EXISTS anh_nha_hang (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36) NOT NULL,
    duong_dan_url VARCHAR(1000) NOT NULL,
    mo_ta_anh VARCHAR(500),
    la_anh_dai_dien BOOLEAN DEFAULT FALSE,
    ngay_tai_len DATE
);

CREATE TABLE IF NOT EXISTS anh_phong (
    id VARCHAR(36) PRIMARY KEY,
    phong_id VARCHAR(36) NOT NULL,
    duong_dan_url VARCHAR(1000) NOT NULL,
    mo_ta_anh VARCHAR(500),
    la_anh_dai_dien BOOLEAN DEFAULT FALSE,
    ngay_tai_len DATE
);

ALTER TABLE anh_khach_san DROP CONSTRAINT IF EXISTS fk_anh_khach_san_khach_san;
ALTER TABLE anh_khach_san ADD CONSTRAINT fk_anh_khach_san_khach_san FOREIGN KEY (khach_san_id) REFERENCES khach_san(id_tai_san) ON DELETE CASCADE;
ALTER TABLE anh_nha_hang DROP CONSTRAINT IF EXISTS fk_anh_nha_hang_nha_hang;
ALTER TABLE anh_nha_hang ADD CONSTRAINT fk_anh_nha_hang_nha_hang FOREIGN KEY (nha_hang_id) REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE;
ALTER TABLE anh_phong DROP CONSTRAINT IF EXISTS fk_anh_phong_phong;
ALTER TABLE anh_phong ADD CONSTRAINT fk_anh_phong_phong FOREIGN KEY (phong_id) REFERENCES phong(id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS chinh_sach (
    id VARCHAR(36) PRIMARY KEY,
    loai_chinh_sach VARCHAR(100) NOT NULL,
    noi_dung TEXT,
    ngay_ap_dung DATE,
    gio_nhan_phong TIME,
    gio_tra_phong TIME,
    gio_mo_cua TIME,
    gio_dong_cua TIME,
    chinh_sach_huy TEXT,
    chinh_sach_hoan_tien TEXT,
    quy_dinh_tre_em TEXT,
    quy_dinh_vat_nuoi TEXT,
    ghi_chu_khac TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL UNIQUE
);

ALTER TABLE chinh_sach DROP CONSTRAINT IF EXISTS fk_chinh_sach_ho_so_kinh_doanh;
ALTER TABLE chinh_sach ADD CONSTRAINT fk_chinh_sach_ho_so_kinh_doanh FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE;

-- 5) Booking / reservation tables used by availability queries
CREATE TABLE IF NOT EXISTS don_dat_cho (
    id VARCHAR(36) PRIMARY KEY,
    ma_don VARCHAR(50) NOT NULL UNIQUE,
    khach_hang_id VARCHAR(36) NOT NULL,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL,
    ngay_tao TIMESTAMP NOT NULL,
    tong_tien_goc DOUBLE PRECISION,
    tien_khuyen_mai DOUBLE PRECISION,
    tong_tien_thanh_toan DOUBLE PRECISION,
    ten_nguoi_dat VARCHAR(255),
    sdt_nguoi_dat VARCHAR(20),
    email_nguoi_dat VARCHAR(255),
    ghi_chu VARCHAR(1000),
    trang_thai_don VARCHAR(50) NOT NULL DEFAULT 'CHO_THANH_TOAN',
    hold_expired_at TIMESTAMP,
    payment_expired_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancel_reason VARCHAR(500),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

ALTER TABLE don_dat_cho DROP CONSTRAINT IF EXISTS fk_don_dat_cho_khach_hang;
ALTER TABLE don_dat_cho ADD CONSTRAINT fk_don_dat_cho_khach_hang FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id) ON DELETE CASCADE;
ALTER TABLE don_dat_cho DROP CONSTRAINT IF EXISTS fk_don_dat_cho_ho_so_kinh_doanh;
ALTER TABLE don_dat_cho ADD CONSTRAINT fk_don_dat_cho_ho_so_kinh_doanh FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS don_khach_san (
    id VARCHAR(36) PRIMARY KEY,
    ngay_check_in DATE,
    ngay_check_out DATE,
    so_dem INTEGER,
    so_khach INTEGER,
    gio_nhan_phong_du_kien TIME
);

ALTER TABLE don_khach_san DROP CONSTRAINT IF EXISTS fk_don_khach_san_don_dat_cho;
ALTER TABLE don_khach_san ADD CONSTRAINT fk_don_khach_san_don_dat_cho FOREIGN KEY (id) REFERENCES don_dat_cho(id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS don_khach_san_chi_tiet (
    id VARCHAR(36) PRIMARY KEY,
    don_khach_san_id VARCHAR(36) NOT NULL,
    phong_id VARCHAR(36) NOT NULL,
    ten_phong_tai_thoi_diem_dat VARCHAR(255),
    so_luong INTEGER,
    don_gia_tai_thoi_diem_dat DOUBLE PRECISION,
    so_dem INTEGER,
    thanh_tien DOUBLE PRECISION
);

ALTER TABLE don_khach_san_chi_tiet DROP CONSTRAINT IF EXISTS fk_don_khach_san_chi_tiet_don;
ALTER TABLE don_khach_san_chi_tiet ADD CONSTRAINT fk_don_khach_san_chi_tiet_don FOREIGN KEY (don_khach_san_id) REFERENCES don_khach_san(id) ON DELETE CASCADE;
ALTER TABLE don_khach_san_chi_tiet DROP CONSTRAINT IF EXISTS fk_don_khach_san_chi_tiet_phong;
ALTER TABLE don_khach_san_chi_tiet ADD CONSTRAINT fk_don_khach_san_chi_tiet_phong FOREIGN KEY (phong_id) REFERENCES phong(id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS don_nha_hang (
    id VARCHAR(36) PRIMARY KEY,
    ngay_gio_bat_dau TIMESTAMP,
    ngay_gio_ket_thuc TIMESTAMP,
    so_nguoi INTEGER,
    tien_coc DOUBLE PRECISION,
    co_dat_mon_truoc BOOLEAN
);

ALTER TABLE don_nha_hang DROP CONSTRAINT IF EXISTS fk_don_nha_hang_don_dat_cho;
ALTER TABLE don_nha_hang ADD CONSTRAINT fk_don_nha_hang_don_dat_cho FOREIGN KEY (id) REFERENCES don_dat_cho(id) ON DELETE CASCADE;

CREATE TABLE IF NOT EXISTS don_nha_hang_ban (
    id VARCHAR(36) PRIMARY KEY,
    don_nha_hang_id VARCHAR(36) NOT NULL,
    ban_id VARCHAR(36) NOT NULL
);

ALTER TABLE don_nha_hang_ban DROP CONSTRAINT IF EXISTS fk_don_nha_hang_ban_don;
ALTER TABLE don_nha_hang_ban ADD CONSTRAINT fk_don_nha_hang_ban_don FOREIGN KEY (don_nha_hang_id) REFERENCES don_nha_hang(id) ON DELETE CASCADE;
ALTER TABLE don_nha_hang_ban DROP CONSTRAINT IF EXISTS fk_don_nha_hang_ban_ban;
ALTER TABLE don_nha_hang_ban ADD CONSTRAINT fk_don_nha_hang_ban_ban FOREIGN KEY (ban_id) REFERENCES ban(id) ON DELETE CASCADE;

-- 6) Seed filter options for hotel amenities (safe insert, compatible H2/PostgreSQL)
INSERT INTO tien_ich_khach_san (id, ten_tien_ich, loai_tien_ich, mo_ta)
SELECT 'AMENITY-KS-001', 'Ho boi', 'Giai tri', 'Ho boi ngoai troi hoac trong nha'
WHERE NOT EXISTS (
    SELECT 1 FROM tien_ich_khach_san WHERE ten_tien_ich = 'Ho boi'
);

INSERT INTO tien_ich_khach_san (id, ten_tien_ich, loai_tien_ich, mo_ta)
SELECT 'AMENITY-KS-002', 'WiFi mien phi', 'Ket noi', 'Ket noi internet khong day mien phi'
WHERE NOT EXISTS (
    SELECT 1 FROM tien_ich_khach_san WHERE ten_tien_ich = 'WiFi mien phi'
);

INSERT INTO tien_ich_khach_san (id, ten_tien_ich, loai_tien_ich, mo_ta)
SELECT 'AMENITY-KS-003', 'Spa & Massage', 'Suc khoe', 'Dich vu spa va massage tai khach san'
WHERE NOT EXISTS (
    SELECT 1 FROM tien_ich_khach_san WHERE ten_tien_ich = 'Spa & Massage'
);

INSERT INTO tien_ich_khach_san (id, ten_tien_ich, loai_tien_ich, mo_ta)
SELECT 'AMENITY-KS-004', 'Nha hang', 'An uong', 'Nha hang tai khach san'
WHERE NOT EXISTS (
    SELECT 1 FROM tien_ich_khach_san WHERE ten_tien_ich = 'Nha hang'
);

INSERT INTO tien_ich_khach_san (id, ten_tien_ich, loai_tien_ich, mo_ta)
SELECT 'AMENITY-KS-005', 'Gym', 'Suc khoe', 'Phong tap the duc'
WHERE NOT EXISTS (
    SELECT 1 FROM tien_ich_khach_san WHERE ten_tien_ich = 'Gym'
);

-- 7) Search/filter indexes
CREATE INDEX IF NOT EXISTS idx_ho_so_kinh_doanh_city ON ho_so_kinh_doanh(thanh_pho);
-- NOTE:
-- `trang_thai_kiem_duyet` da bi loai bo o mot so schema legacy.
-- Khong tao index phu thuoc cot nay de migration V2 co the chay tren schema cu.
CREATE INDEX IF NOT EXISTS idx_tai_san_ho_so ON tai_san(ho_so_kinh_doanh_id);
CREATE INDEX IF NOT EXISTS idx_khach_san_hang_sao ON khach_san(hang_sao);
CREATE INDEX IF NOT EXISTS idx_khach_san_loai ON khach_san(loai_khach_san);
CREATE INDEX IF NOT EXISTS idx_nha_hang_cuisine ON nha_hang(loai_am_thuc);
CREATE INDEX IF NOT EXISTS idx_tien_ich_khach_san_name ON tien_ich_khach_san(ten_tien_ich);
CREATE INDEX IF NOT EXISTS idx_tien_ich_nha_hang_name ON tien_ich_nha_hang(ten_tien_ich);
CREATE INDEX IF NOT EXISTS idx_khach_san_tien_ich_join ON khach_san_tien_ich(khach_san_id, tien_ich_id);
CREATE INDEX IF NOT EXISTS idx_phong_khach_san_deleted ON phong(khach_san_id, deleted);
CREATE INDEX IF NOT EXISTS idx_ban_nha_hang_deleted ON ban(nha_hang_id, deleted);
-- NOTE:
-- Schema don_dat_cho co the khac nhau giua cac moi truong (trang_thai / trang_thai_don).
-- Bo qua index tong hop de tranh migration fail.
CREATE INDEX IF NOT EXISTS idx_don_ks_date_range ON don_khach_san(ngay_check_in, ngay_check_out);
CREATE INDEX IF NOT EXISTS idx_don_nh_time_range ON don_nha_hang(ngay_gio_bat_dau, ngay_gio_ket_thuc);
CREATE INDEX IF NOT EXISTS idx_don_ks_ct_room ON don_khach_san_chi_tiet(phong_id);
CREATE INDEX IF NOT EXISTS idx_don_nh_ban_table ON don_nha_hang_ban(ban_id);

