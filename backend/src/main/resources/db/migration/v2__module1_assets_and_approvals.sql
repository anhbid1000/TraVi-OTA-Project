-- =============================================
-- V2__module1_assets_and_approvals.sql
-- Module 1: Quan ly tai san va kiem duyet - Stage 1 (full)
-- =============================================

-- 1) Ho so kinh doanh
CREATE TABLE ho_so_kinh_doanh (
    id_ho_so VARCHAR(36) PRIMARY KEY,
    doi_tac_id VARCHAR(36) NOT NULL REFERENCES doi_tac(id) ON DELETE CASCADE,

    ten_co_so VARCHAR(255) NOT NULL,
    sdt_lien_he VARCHAR(20) NOT NULL,
    email_lien_he VARCHAR(255),

    dia_chi VARCHAR(255),
    thanh_pho VARCHAR(100),
    quan_huyen VARCHAR(100),
    phuong_xa VARCHAR(100),
    kinh_do DOUBLE PRECISION,
    vi_do DOUBLE PRECISION,

    loai_dich_vu VARCHAR(50) NOT NULL,
    ma_so_thue VARCHAR(50) NOT NULL UNIQUE,
    giay_phep_kinh_doanh VARCHAR(255) NOT NULL,
    toa_do_gps VARCHAR(255) NOT NULL,

    old_ten_co_so VARCHAR(255),
    old_sdt_lien_he VARCHAR(255),
    old_loai_dich_vu VARCHAR(255),
    old_ma_so_thue VARCHAR(255),
    old_giay_phep_kinh_doanh VARCHAR(255),
    old_toa_dogps VARCHAR(255),

    trang_thai_kiem_duyet VARCHAR(50) NOT NULL DEFAULT 'CHO_DUYET',
    trang_thai_hoat_dong VARCHAR(50) NOT NULL DEFAULT 'CHUA_HOAT_DONG',
    ly_do_tu_choi_gan_nhat VARCHAR(1000),

    thoi_gian_dang_ky TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    thoi_gian_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    thoi_gian_duyet TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- 2) Chinh sach
CREATE TABLE chinh_sach (
    id VARCHAR(36) PRIMARY KEY,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL UNIQUE REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE,

    loai_chinh_sach VARCHAR(255) NOT NULL,
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

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3) Lich su kiem duyet ho so
CREATE TABLE lich_su_kiem_duyet_ho_so (
    id VARCHAR(36) PRIMARY KEY,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE,
    admin_id VARCHAR(36) REFERENCES quan_tri_vien(id) ON DELETE SET NULL,
    trang_thai_cu VARCHAR(50) NOT NULL,
    trang_thai_moi VARCHAR(50) NOT NULL,
    ly_do VARCHAR(1000),
    ghi_chu_noi_bo VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4) Tai san cha
CREATE TABLE tai_san (
    id_tai_san VARCHAR(36) PRIMARY KEY,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE,
    mo_ta VARCHAR(255),
    trang_thai VARCHAR(50) DEFAULT 'SAN_SANG',
    gia_co_ban DOUBLE PRECISION,
    is_dynamic_pricing BOOLEAN NOT NULL DEFAULT FALSE
);

-- 5) Khach san
CREATE TABLE khach_san (
    id_tai_san VARCHAR(36) PRIMARY KEY REFERENCES tai_san(id_tai_san) ON DELETE CASCADE,
    ten VARCHAR(255) NOT NULL,
    hang_sao INT,
    loai_khach_san VARCHAR(100),
    gio_nhan_phong TIME,
    gio_tra_phong TIME,
    gio_nhan_phong_mac_dinh TIME,
    gio_tra_phong_mac_dinh TIME,
    so_tang INT,
    tong_so_phong INT
);

-- 6) Nha hang
CREATE TABLE nha_hang (
    id_tai_san VARCHAR(36) PRIMARY KEY REFERENCES tai_san(id_tai_san) ON DELETE CASCADE,
    ten VARCHAR(255) NOT NULL,
    suc_chua INT,
    loai_am_thuc VARCHAR(255),
    gio_mo_cua TIME,
    gio_dong_cua TIME,
    co_dat_ban_truoc BOOLEAN NOT NULL DEFAULT TRUE,
    co_dat_mon_truoc BOOLEAN NOT NULL DEFAULT TRUE
);

-- 7) Phong
CREATE TABLE phong (
    id VARCHAR(36) PRIMARY KEY,
    khach_san_id VARCHAR(36) NOT NULL REFERENCES khach_san(id_tai_san) ON DELETE CASCADE,

    so_phong VARCHAR(100) NOT NULL,
    ten_phong VARCHAR(255) NOT NULL,
    loai_phong VARCHAR(100) NOT NULL,
    mo_ta TEXT,

    dien_tich REAL,
    suc_chua_toi_da INT,
    so_giuong INT,
    gia_co_ban DOUBLE PRECISION,
    so_luong_phong INT,

    trang_thai VARCHAR(50) DEFAULT 'DANG_BAN',
    phan_tram_giam_gia REAL DEFAULT 0.0,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_phong_khach_san_so_phong UNIQUE (khach_san_id, so_phong)
);

CREATE TABLE phong_tien_ich (
    phong_id VARCHAR(36) NOT NULL REFERENCES phong(id) ON DELETE CASCADE,
    tien_ich VARCHAR(100) NOT NULL,
    PRIMARY KEY (phong_id, tien_ich)
);

-- 8) Ban
CREATE TABLE ban (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36) NOT NULL REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE,

    ten_ban VARCHAR(255),
    vi_tri_sanh VARCHAR(255),
    mo_ta TEXT,
    so_cho_ngoi INT,
    trang_thai INT,

    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 9) Thuc don
CREATE TABLE thuc_don (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36) NOT NULL REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE,

    ten_thuc_don VARCHAR(255),
    phan_loai VARCHAR(255),
    trang_thai VARCHAR(50) DEFAULT 'DANG_HIEN_THI',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10) Mon an
CREATE TABLE mon_an (
    id VARCHAR(36) PRIMARY KEY,
    thuc_don_id VARCHAR(36) NOT NULL REFERENCES thuc_don(id) ON DELETE CASCADE,

    ten_mon VARCHAR(255) NOT NULL,
    mo_ta TEXT,
    gia_ban DOUBLE PRECISION,
    danh_muc_mon VARCHAR(255),
    duong_dan_url VARCHAR(255),
    trang_thai VARCHAR(50) DEFAULT 'DANG_BAN',

    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mon_an_the_ngu_canh (
    mon_an_id VARCHAR(36) NOT NULL REFERENCES mon_an(id) ON DELETE CASCADE,
    the_ngu_canh VARCHAR(255) NOT NULL,
    PRIMARY KEY (mon_an_id, the_ngu_canh)
);

-- 11) Combo
CREATE TABLE combo (
    id VARCHAR(36) PRIMARY KEY,
    thuc_don_id VARCHAR(36) NOT NULL REFERENCES thuc_don(id) ON DELETE CASCADE,

    ten_combo VARCHAR(255) NOT NULL,
    mo_ta VARCHAR(255),
    gia_combo REAL,
    trang_thai INT,
    ngay_bat_dau DATE,
    ngay_ket_thuc DATE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE combo_mon_an (
    combo_id VARCHAR(36) NOT NULL REFERENCES combo(id) ON DELETE CASCADE,
    mon_an_id VARCHAR(36) NOT NULL REFERENCES mon_an(id) ON DELETE CASCADE,
    PRIMARY KEY (combo_id, mon_an_id)
);

CREATE TABLE combo_item (
    id VARCHAR(36) PRIMARY KEY,
    combo_id VARCHAR(36) NOT NULL REFERENCES combo(id) ON DELETE CASCADE,
    mon_an_id VARCHAR(36) NOT NULL REFERENCES mon_an(id) ON DELETE CASCADE,
    so_luong INT NOT NULL DEFAULT 1
);

-- 12) Tien ich
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

-- 13) Anh
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

-- 14) Indexes
CREATE INDEX idx_ho_so_doi_tac ON ho_so_kinh_doanh(doi_tac_id);
CREATE INDEX idx_ho_so_trang_thai_kiem_duyet ON ho_so_kinh_doanh(trang_thai_kiem_duyet);
CREATE INDEX idx_ho_so_trang_thai_hoat_dong ON ho_so_kinh_doanh(trang_thai_hoat_dong);
CREATE INDEX idx_ho_so_loai_dich_vu ON ho_so_kinh_doanh(loai_dich_vu);
CREATE INDEX idx_ho_so_deleted ON ho_so_kinh_doanh(deleted);

CREATE INDEX idx_lich_su_kiem_duyet_ho_so ON lich_su_kiem_duyet_ho_so(ho_so_kinh_doanh_id);
CREATE INDEX idx_lich_su_kiem_duyet_admin ON lich_su_kiem_duyet_ho_so(admin_id);

CREATE INDEX idx_tai_san_ho_so ON tai_san(ho_so_kinh_doanh_id);
CREATE INDEX idx_tai_san_trang_thai ON tai_san(trang_thai);

CREATE INDEX idx_phong_khach_san ON phong(khach_san_id);
CREATE INDEX idx_phong_trang_thai ON phong(trang_thai);
CREATE INDEX idx_phong_deleted ON phong(deleted);

CREATE INDEX idx_ban_nha_hang ON ban(nha_hang_id);
CREATE INDEX idx_ban_deleted ON ban(deleted);

CREATE INDEX idx_thuc_don_nha_hang ON thuc_don(nha_hang_id);
CREATE INDEX idx_mon_an_thuc_don ON mon_an(thuc_don_id);
CREATE INDEX idx_mon_an_deleted ON mon_an(deleted);
CREATE INDEX idx_combo_thuc_don ON combo(thuc_don_id);

CREATE INDEX idx_tien_ich_nha_hang ON tien_ich_nha_hang(nha_hang_id);
CREATE INDEX idx_combo_item_combo ON combo_item(combo_id);
CREATE INDEX idx_combo_item_mon_an ON combo_item(mon_an_id);

CREATE INDEX idx_anh_khach_san ON anh_khach_san(khach_san_id);
CREATE INDEX idx_anh_nha_hang ON anh_nha_hang(nha_hang_id);
CREATE INDEX idx_anh_phong ON anh_phong(phong_id);
