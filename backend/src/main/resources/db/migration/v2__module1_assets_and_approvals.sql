-- =============================================
-- V2__module1_assets_and_approvals.sql
-- Module 1: Quản lý tài sản và kiểm duyệt
-- Mục đích:
--   - Tạo các bảng hồ sơ kinh doanh và kiểm duyệt.
--   - Tạo các bảng kế thừa tài sản cho khách sạn và nhà hàng.
--   - Tạo các bảng tài nguyên con: phòng, bàn, thực đơn, món ăn, combo.
--   - Tạo các bảng ảnh và tiện ích.
--   - Tạo chỉ mục và ràng buộc phục vụ các luồng tra cứu của Module 1
-- =============================================

CREATE TABLE ho_so_kinh_doanh (
    id_ho_so VARCHAR(36) PRIMARY KEY,
    doi_tac_id VARCHAR(36) NOT NULL REFERENCES doi_tac(id) ON DELETE CASCADE,
    ten_co_so VARCHAR(255) NOT NULL,
    sdt_lien_he VARCHAR(20) NOT NULL,
    loai_dich_vu VARCHAR(50) NOT NULL,
    ma_so_thue VARCHAR(50) NOT NULL UNIQUE,
    giay_phep_kinh_doanh VARCHAR(255) NOT NULL,
    toa_do_gps VARCHAR(255) NOT NULL,
    trang_thai_kiem_duyet VARCHAR(50) NOT NULL DEFAULT 'CHO_DUYET',
    thoi_gian_dang_ky TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    thoi_gian_duyet TIMESTAMP
);

CREATE TABLE chinh_sach (
    id VARCHAR(36) PRIMARY KEY,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL UNIQUE REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE,
    loai_chinh_sach VARCHAR(255) NOT NULL,
    noi_dung TEXT,
    ngay_ap_dung DATE
);

CREATE TABLE tai_san (
    id_tai_san VARCHAR(36) PRIMARY KEY,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE,
    mo_ta VARCHAR(255),
    trang_thai VARCHAR(50) DEFAULT 'SAN_SANG',
    gia_co_ban DOUBLE PRECISION,
    is_dynamic_pricing BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE khach_san (
    id_tai_san VARCHAR(36) PRIMARY KEY REFERENCES tai_san(id_tai_san) ON DELETE CASCADE,
    ten VARCHAR(255) NOT NULL,
    hang_sao INT,
    gio_nhan_phong TIME,
    gio_tra_phong TIME
);

CREATE TABLE nha_hang (
    id_tai_san VARCHAR(36) PRIMARY KEY REFERENCES tai_san(id_tai_san) ON DELETE CASCADE,
    ten VARCHAR(255) NOT NULL,
    suc_chua INT,
    loai_am_thuc VARCHAR(255),
    gio_mo_cua TIME,
    gio_dong_cua TIME
);

CREATE TABLE phong (
    id VARCHAR(36) PRIMARY KEY,
    khach_san_id VARCHAR(36) NOT NULL REFERENCES khach_san(id_tai_san) ON DELETE CASCADE,
    so_phong VARCHAR(100) NOT NULL,
    loai_phong VARCHAR(100) NOT NULL,
    suc_chua_toi_da INT,
    dien_tich REAL,
    trang_thai VARCHAR(50) DEFAULT 'SAN_SANG',
    phan_tram_giam_gia REAL DEFAULT 0.0,
    CONSTRAINT uk_phong_khach_san_so_phong UNIQUE (khach_san_id, so_phong)
);

CREATE TABLE phong_tien_ich (
    phong_id VARCHAR(36) NOT NULL REFERENCES phong(id) ON DELETE CASCADE,
    tien_ich VARCHAR(100) NOT NULL,
    PRIMARY KEY (phong_id, tien_ich)
);

CREATE TABLE ban (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36) NOT NULL REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE,
    vi_tri_sanh VARCHAR(255),
    so_cho_ngoi INT,
    trang_thai INT
);

CREATE TABLE thuc_don (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36) NOT NULL REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE,
    phan_loai VARCHAR(255)
);

CREATE TABLE mon_an (
    id VARCHAR(36) PRIMARY KEY,
    thuc_don_id VARCHAR(36) NOT NULL REFERENCES thuc_don(id) ON DELETE CASCADE,
    ten_mon VARCHAR(255) NOT NULL,
    gia_ban DOUBLE PRECISION,
    trang_thai VARCHAR(50) DEFAULT 'CO_SAN',
    duong_dan_url VARCHAR(255)
);

CREATE TABLE mon_an_the_ngu_canh (
    mon_an_id VARCHAR(36) NOT NULL REFERENCES mon_an(id) ON DELETE CASCADE,
    the_ngu_canh VARCHAR(255) NOT NULL,
    PRIMARY KEY (mon_an_id, the_ngu_canh)
);

CREATE TABLE combo (
    id VARCHAR(36) PRIMARY KEY,
    thuc_don_id VARCHAR(36) NOT NULL REFERENCES thuc_don(id) ON DELETE CASCADE,
    ten_combo VARCHAR(255) NOT NULL,
    mo_ta VARCHAR(255),
    gia_combo REAL,
    trang_thai INT,
    ngay_bat_dau DATE,
    ngay_ket_thuc DATE
);

CREATE TABLE combo_mon_an (
    combo_id VARCHAR(36) NOT NULL REFERENCES combo(id) ON DELETE CASCADE,
    mon_an_id VARCHAR(36) NOT NULL REFERENCES mon_an(id) ON DELETE CASCADE,
    PRIMARY KEY (combo_id, mon_an_id)
);

CREATE TABLE tien_ich_khach_san (
    id VARCHAR(36) PRIMARY KEY,
    ten_tien_ich VARCHAR(255) NOT NULL UNIQUE,
    loai_tien_ich VARCHAR(255),
    mo_ta VARCHAR(255)
);

CREATE TABLE khach_san_tien_ich (
    khach_san_id VARCHAR(36) NOT NULL REFERENCES khach_san(id_tai_san) ON DELETE CASCADE,
    tien_ich_id VARCHAR(36) NOT NULL REFERENCES tien_ich_khach_san(id) ON DELETE CASCADE,
    PRIMARY KEY (khach_san_id, tien_ich_id)
);

CREATE TABLE tien_ich_nha_hang (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36) NOT NULL REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE,
    ten_tien_ich VARCHAR(255) NOT NULL,
    loai_tien_ich VARCHAR(255),
    mo_ta VARCHAR(255),
    co_thu_phi BOOLEAN DEFAULT FALSE,
    phi_su_dung REAL DEFAULT 0.0
);

CREATE TABLE anh_khach_san (
    id VARCHAR(36) PRIMARY KEY,
    khach_san_id VARCHAR(36) NOT NULL REFERENCES khach_san(id_tai_san) ON DELETE CASCADE,
    duong_dan_url VARCHAR(255) NOT NULL,
    mo_ta_anh VARCHAR(255),
    la_anh_dai_dien BOOLEAN DEFAULT FALSE,
    ngay_tai_len DATE
);

CREATE TABLE anh_nha_hang (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36) NOT NULL REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE,
    duong_dan_url VARCHAR(255) NOT NULL,
    mo_ta_anh VARCHAR(255),
    la_anh_dai_dien BOOLEAN DEFAULT FALSE,
    ngay_tai_len DATE
);

CREATE TABLE anh_phong (
    id VARCHAR(36) PRIMARY KEY,
    phong_id VARCHAR(36) NOT NULL REFERENCES phong(id) ON DELETE CASCADE,
    duong_dan_url VARCHAR(255) NOT NULL,
    mo_ta_anh VARCHAR(255),
    la_anh_dai_dien BOOLEAN DEFAULT FALSE,
    ngay_tai_len DATE
);

CREATE INDEX idx_ho_so_doi_tac ON ho_so_kinh_doanh(doi_tac_id);
CREATE INDEX idx_ho_so_trang_thai ON ho_so_kinh_doanh(trang_thai_kiem_duyet);
CREATE INDEX idx_ho_so_loai_dich_vu ON ho_so_kinh_doanh(loai_dich_vu);
CREATE INDEX idx_tai_san_ho_so ON tai_san(ho_so_kinh_doanh_id);
CREATE INDEX idx_tai_san_trang_thai ON tai_san(trang_thai);
CREATE INDEX idx_phong_khach_san ON phong(khach_san_id);
CREATE INDEX idx_phong_trang_thai ON phong(trang_thai);
CREATE INDEX idx_ban_nha_hang ON ban(nha_hang_id);
CREATE INDEX idx_thuc_don_nha_hang ON thuc_don(nha_hang_id);
CREATE INDEX idx_mon_an_thuc_don ON mon_an(thuc_don_id);
CREATE INDEX idx_combo_thuc_don ON combo(thuc_don_id);
CREATE INDEX idx_tien_ich_nha_hang ON tien_ich_nha_hang(nha_hang_id);
CREATE INDEX idx_anh_khach_san ON anh_khach_san(khach_san_id);
CREATE INDEX idx_anh_nha_hang ON anh_nha_hang(nha_hang_id);
CREATE INDEX idx_anh_phong ON anh_phong(phong_id);
