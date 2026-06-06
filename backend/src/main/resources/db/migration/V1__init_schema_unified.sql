-- =============================================================
-- V1__init_schema_unified.sql
-- Unified schema (Entity + db/migration aligned)
-- =============================================================

-- 1) RBAC + USER CORE
CREATE TABLE vai_tro (
    id VARCHAR(36) PRIMARY KEY,
    ten VARCHAR(50) NOT NULL UNIQUE,
    mo_ta TEXT,
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    mat_khau VARCHAR(255) NOT NULL,
    ho_ten VARCHAR(150) NOT NULL,
    ngay_sinh DATE,
    gioi_tinh VARCHAR(20),
    so_dien_thoai VARCHAR(20),
    trang_thai VARCHAR(50) NOT NULL DEFAULT 'CHUA_XAC_THUC',
    so_lan_dang_nhap_sai INTEGER NOT NULL DEFAULT 0,
    lan_cuoi_dang_nhap TIMESTAMP,
    ngay_tao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ngay_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vai_tro_id VARCHAR(36),
    CONSTRAINT fk_users_vai_tro_id FOREIGN KEY (vai_tro_id) REFERENCES vai_tro(id) ON DELETE SET NULL
);

CREATE TABLE khach_hang (
    id VARCHAR(36) PRIMARY KEY,
    diem_thanh_vien INTEGER DEFAULT 0,
    hang_thanh_vien VARCHAR(50) DEFAULT 'DONG',
    tong_chi_tieu DOUBLE PRECISION DEFAULT 0.0,
    CONSTRAINT fk_khach_hang_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE doi_tac (
    id VARCHAR(36) PRIMARY KEY,
    ti_le_chiet_khau REAL DEFAULT 0.0,
    CONSTRAINT fk_doi_tac_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

-- 2) PREFERENCES
CREATE TABLE danh_muc_so_thich (
    id VARCHAR(36) PRIMARY KEY,
    ten_danh_muc VARCHAR(100) NOT NULL,
    mo_ta TEXT
);

CREATE TABLE so_thich (
    id VARCHAR(36) PRIMARY KEY,
    ten_so_thich VARCHAR(100) NOT NULL,
    danh_muc_id VARCHAR(36),
    CONSTRAINT fk_so_thich_danh_muc FOREIGN KEY (danh_muc_id) REFERENCES danh_muc_so_thich(id) ON DELETE CASCADE
);

CREATE TABLE khach_hang_tu_khoa (
    khach_hang_id VARCHAR(36) NOT NULL,
    tu_khoa VARCHAR(255),
    CONSTRAINT fk_khach_hang_tu_khoa FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id) ON DELETE CASCADE
);

CREATE TABLE khach_hang_so_thich (
    khach_hang_id VARCHAR(36) NOT NULL,
    so_thich_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (khach_hang_id, so_thich_id),
    CONSTRAINT fk_kh_so_thich_kh FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id) ON DELETE CASCADE,
    CONSTRAINT fk_kh_so_thich_st FOREIGN KEY (so_thich_id) REFERENCES so_thich(id) ON DELETE CASCADE
);

CREATE TABLE so_thich_nguoi_dung (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    danh_muc VARCHAR(255) NOT NULL,
    diem_so INTEGER NOT NULL DEFAULT 0,
    cap_nhat_cuoi TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stnd_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_so_thich_user_danh_muc UNIQUE (user_id, danh_muc)
);

-- 3) BUSINESS PROFILE + ASSET
CREATE TABLE ho_so_kinh_doanh (
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
    kinh_do DOUBLE PRECISION,
    vi_do DOUBLE PRECISION,
    trang_thai_hoat_dong VARCHAR(50) NOT NULL DEFAULT 'CHUA_HOAT_DONG',
    thoi_gian_dang_ky TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    thoi_gian_cap_nhat TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_ho_so_kinh_doanh_doi_tac FOREIGN KEY (doi_tac_id) REFERENCES doi_tac(id) ON DELETE CASCADE
);

CREATE TABLE tai_san (
    id_tai_san VARCHAR(36) PRIMARY KEY,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL UNIQUE,
    mo_ta TEXT,
    trang_thai VARCHAR(50) DEFAULT 'SAN_SANG',
    gia_co_ban DOUBLE PRECISION,
    is_dynamic_pricing BOOLEAN NOT NULL DEFAULT FALSE,
    rating_average DOUBLE PRECISION DEFAULT 0.0,
    review_count INTEGER DEFAULT 0,
    dia_chi VARCHAR(255),
    thanh_pho VARCHAR(100),
    kinh_do DOUBLE PRECISION,
    vi_do DOUBLE PRECISION,
    CONSTRAINT fk_tai_san_ho_so_kinh_doanh FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE
);

CREATE TABLE khach_san (
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
    CONSTRAINT fk_khach_san_tai_san FOREIGN KEY (id_tai_san) REFERENCES tai_san(id_tai_san) ON DELETE CASCADE
);

CREATE TABLE nha_hang (
    id_tai_san VARCHAR(36) PRIMARY KEY,
    ten VARCHAR(255) NOT NULL,
    loai_am_thuc VARCHAR(150),
    gio_mo_cua TIME,
    gio_dong_cua TIME,
    suc_chua INTEGER,
    co_dat_ban_truoc BOOLEAN NOT NULL DEFAULT TRUE,
    co_dat_mon_truoc BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_nha_hang_tai_san FOREIGN KEY (id_tai_san) REFERENCES tai_san(id_tai_san) ON DELETE CASCADE
);

-- 4) AMENITIES + MEDIA + MENU
CREATE TABLE tien_ich_khach_san (
    id VARCHAR(36) PRIMARY KEY,
    ten_tien_ich VARCHAR(100) NOT NULL UNIQUE,
    loai_tien_ich VARCHAR(100),
    mo_ta TEXT
);

CREATE TABLE khach_san_tien_ich (
    khach_san_id VARCHAR(36) NOT NULL,
    tien_ich_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (khach_san_id, tien_ich_id),
    CONSTRAINT fk_kstich_ks FOREIGN KEY (khach_san_id) REFERENCES khach_san(id_tai_san) ON DELETE CASCADE,
    CONSTRAINT fk_kstich_tienich FOREIGN KEY (tien_ich_id) REFERENCES tien_ich_khach_san(id) ON DELETE CASCADE
);

CREATE TABLE tien_ich_nha_hang (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36),
    ten_tien_ich VARCHAR(100) NOT NULL,
    loai_tien_ich VARCHAR(100),
    mo_ta TEXT,
    co_thu_phi BOOLEAN DEFAULT FALSE,
    phi_su_dung REAL DEFAULT 0.0,
    CONSTRAINT fk_tien_ich_nha_hang_nha_hang FOREIGN KEY (nha_hang_id) REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE
);

CREATE TABLE phong (
    id VARCHAR(36) PRIMARY KEY,
    khach_san_id VARCHAR(36) NOT NULL,
    so_phong VARCHAR(50) NOT NULL,
    ten_phong VARCHAR(255) NOT NULL,
    loai_phong VARCHAR(100) NOT NULL,
    mo_ta VARCHAR(1000),
    suc_chua_toi_da INTEGER,
    so_giuong INTEGER,
    dien_tich REAL,
    gia_co_ban DOUBLE PRECISION NOT NULL,
    so_luong_phong INTEGER NOT NULL,
    trang_thai VARCHAR(50) DEFAULT 'SAN_SANG',
    phan_tram_giam_gia REAL DEFAULT 0.0,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_phong_khach_san FOREIGN KEY (khach_san_id) REFERENCES khach_san(id_tai_san) ON DELETE CASCADE
);

CREATE TABLE phong_tien_ich (
    phong_id VARCHAR(36) NOT NULL,
    tien_ich VARCHAR(100),
    CONSTRAINT fk_phong_tien_ich_phong FOREIGN KEY (phong_id) REFERENCES phong(id) ON DELETE CASCADE
);

CREATE TABLE anh_khach_san (
    id VARCHAR(36) PRIMARY KEY,
    khach_san_id VARCHAR(36) NOT NULL,
    duong_dan_url VARCHAR(1000) NOT NULL,
    mo_ta_anh VARCHAR(500),
    la_anh_dai_dien BOOLEAN DEFAULT FALSE,
    ngay_tai_len DATE,
    CONSTRAINT fk_anh_khach_san_khach_san FOREIGN KEY (khach_san_id) REFERENCES khach_san(id_tai_san) ON DELETE CASCADE
);

CREATE TABLE anh_nha_hang (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36) NOT NULL,
    duong_dan_url VARCHAR(1000) NOT NULL,
    mo_ta_anh VARCHAR(500),
    la_anh_dai_dien BOOLEAN DEFAULT FALSE,
    ngay_tai_len DATE,
    CONSTRAINT fk_anh_nha_hang_nha_hang FOREIGN KEY (nha_hang_id) REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE
);

CREATE TABLE ban (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36) NOT NULL,
    ten_ban VARCHAR(255),
    vi_tri_sanh VARCHAR(255),
    mo_ta VARCHAR(1000),
    trang_thai VARCHAR(50) NOT NULL DEFAULT 'SAN_SANG',
    so_cho_ngoi INTEGER,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ban_nha_hang FOREIGN KEY (nha_hang_id) REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE
);

CREATE TABLE anh_phong (
    id VARCHAR(36) PRIMARY KEY,
    phong_id VARCHAR(36) NOT NULL,
    duong_dan_url VARCHAR(1000) NOT NULL,
    mo_ta_anh VARCHAR(500),
    la_anh_dai_dien BOOLEAN DEFAULT FALSE,
    ngay_tai_len DATE,
    CONSTRAINT fk_anh_phong_phong FOREIGN KEY (phong_id) REFERENCES phong(id) ON DELETE CASCADE
);

CREATE TABLE thuc_don (
    id VARCHAR(36) PRIMARY KEY,
    nha_hang_id VARCHAR(36) NOT NULL,
    ten_thuc_don VARCHAR(255),
    phan_loai VARCHAR(100),
    trang_thai VARCHAR(50) DEFAULT 'DANG_HIEN_THI',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_thuc_don_nha_hang FOREIGN KEY (nha_hang_id) REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE
);

CREATE TABLE mon_an (
    id VARCHAR(36) PRIMARY KEY,
    thuc_don_id VARCHAR(36) NOT NULL,
    ten_mon VARCHAR(255) NOT NULL,
    mo_ta VARCHAR(1000),
    gia_ban DOUBLE PRECISION,
    danh_muc_mon VARCHAR(100),
    trang_thai VARCHAR(50) DEFAULT 'DANG_BAN',
    duong_dan_url VARCHAR(1000),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mon_an_thuc_don FOREIGN KEY (thuc_don_id) REFERENCES thuc_don(id) ON DELETE CASCADE
);

CREATE TABLE mon_an_the_ngu_canh (
    mon_an_id VARCHAR(36) NOT NULL,
    the_ngu_canh VARCHAR(255),
    CONSTRAINT fk_mon_an_the_ngu_canh_mon_an FOREIGN KEY (mon_an_id) REFERENCES mon_an(id) ON DELETE CASCADE
);

CREATE TABLE combo (
    id VARCHAR(36) PRIMARY KEY,
    thuc_don_id VARCHAR(36) NOT NULL,
    ten_combo VARCHAR(255),
    mo_ta TEXT,
    gia_combo REAL,
    ngay_bat_dau DATE,
    ngay_ket_thuc DATE,
    trang_thai INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_combo_thuc_don FOREIGN KEY (thuc_don_id) REFERENCES thuc_don(id) ON DELETE CASCADE
);

CREATE TABLE combo_mon_an (
    combo_id VARCHAR(36) NOT NULL,
    mon_an_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (combo_id, mon_an_id),
    CONSTRAINT fk_combo_mon_an_combo FOREIGN KEY (combo_id) REFERENCES combo(id) ON DELETE CASCADE,
    CONSTRAINT fk_combo_mon_an_mon_an FOREIGN KEY (mon_an_id) REFERENCES mon_an(id) ON DELETE CASCADE
);

CREATE TABLE combo_item (
    id VARCHAR(36) PRIMARY KEY,
    combo_id VARCHAR(36) NOT NULL,
    mon_an_id VARCHAR(36) NOT NULL,
    so_luong INTEGER DEFAULT 1,
    CONSTRAINT fk_combo_item_combo FOREIGN KEY (combo_id) REFERENCES combo(id) ON DELETE CASCADE,
    CONSTRAINT fk_combo_item_mon_an FOREIGN KEY (mon_an_id) REFERENCES mon_an(id) ON DELETE CASCADE
);

CREATE TABLE chinh_sach (
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL UNIQUE,
    CONSTRAINT fk_chinh_sach_ho_so_kinh_doanh FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE
);

-- 5) ORDER + BOOKING
CREATE TABLE don_dat_cho (
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
    trang_thai VARCHAR(50) NOT NULL DEFAULT 'CHO_THANH_TOAN',
    hold_expired_at TIMESTAMP,
    payment_expired_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancel_reason VARCHAR(500),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_don_dat_cho_khach_hang FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id) ON DELETE CASCADE,
    CONSTRAINT fk_don_dat_cho_ho_so_kinh_doanh FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE
);

CREATE TABLE don_khach_san (
    id VARCHAR(36) PRIMARY KEY,
    ngay_check_in DATE,
    ngay_check_out DATE,
    so_dem INTEGER,
    so_khach INTEGER,
    gio_nhan_phong_du_kien TIME,
    CONSTRAINT fk_don_khach_san_don_dat_cho FOREIGN KEY (id) REFERENCES don_dat_cho(id) ON DELETE CASCADE
);

CREATE TABLE don_khach_san_chi_tiet (
    id VARCHAR(36) PRIMARY KEY,
    don_khach_san_id VARCHAR(36) NOT NULL,
    phong_id VARCHAR(36) NOT NULL,
    ten_phong_tai_thoi_diem_dat VARCHAR(255),
    so_luong INTEGER,
    don_gia_tai_thoi_diem_dat DOUBLE PRECISION,
    so_dem INTEGER,
    thanh_tien DOUBLE PRECISION,
    CONSTRAINT fk_don_ks_ct_don FOREIGN KEY (don_khach_san_id) REFERENCES don_khach_san(id) ON DELETE CASCADE,
    CONSTRAINT fk_don_ks_ct_phong FOREIGN KEY (phong_id) REFERENCES phong(id) ON DELETE CASCADE
);

CREATE TABLE dat_phong (
    id VARCHAR(36) PRIMARY KEY,
    don_dat_cho_id VARCHAR(36) NOT NULL,
    phong_id VARCHAR(36) NOT NULL,
    ngay_check_in DATE,
    ngay_check_out DATE,
    ghi_chu_khach_hang_phong_don_dat VARCHAR(255),
    CONSTRAINT fk_dat_phong_don FOREIGN KEY (don_dat_cho_id) REFERENCES don_dat_cho(id) ON DELETE CASCADE,
    CONSTRAINT fk_dat_phong_phong FOREIGN KEY (phong_id) REFERENCES phong(id) ON DELETE CASCADE
);

CREATE TABLE don_nha_hang (
    id VARCHAR(36) PRIMARY KEY,
    ngay_gio_bat_dau TIMESTAMP,
    ngay_gio_ket_thuc TIMESTAMP,
    so_nguoi INTEGER,
    tien_coc DOUBLE PRECISION,
    co_dat_mon_truoc BOOLEAN,
    CONSTRAINT fk_don_nha_hang_don_dat_cho FOREIGN KEY (id) REFERENCES don_dat_cho(id) ON DELETE CASCADE
);

CREATE TABLE don_nha_hang_ban (
    id VARCHAR(36) PRIMARY KEY,
    don_nha_hang_id VARCHAR(36) NOT NULL,
    ban_id VARCHAR(36) NOT NULL,
    CONSTRAINT fk_don_nha_hang_ban_don FOREIGN KEY (don_nha_hang_id) REFERENCES don_nha_hang(id) ON DELETE CASCADE,
    CONSTRAINT fk_don_nha_hang_ban_ban FOREIGN KEY (ban_id) REFERENCES ban(id) ON DELETE CASCADE
);

CREATE TABLE don_dat_mon (
    id VARCHAR(36) PRIMARY KEY,
    don_dat_cho_id VARCHAR(36) NOT NULL,
    nha_hang_id VARCHAR(36) NOT NULL,
    ban_id VARCHAR(36),
    thoi_gian_dat TIMESTAMP,
    ghi_chu VARCHAR(255),
    CONSTRAINT fk_don_dat_mon_don FOREIGN KEY (don_dat_cho_id) REFERENCES don_dat_cho(id) ON DELETE CASCADE,
    CONSTRAINT fk_don_dat_mon_nh FOREIGN KEY (nha_hang_id) REFERENCES nha_hang(id_tai_san) ON DELETE CASCADE,
    CONSTRAINT fk_don_dat_mon_ban FOREIGN KEY (ban_id) REFERENCES ban(id) ON DELETE SET NULL
);

CREATE TABLE chi_tiet_don_dat_mon (
    id VARCHAR(36) PRIMARY KEY,
    don_dat_mon_id VARCHAR(36) NOT NULL,
    mon_an_id VARCHAR(36) NOT NULL,
    so_luong INTEGER,
    gia_tien DOUBLE PRECISION,
    CONSTRAINT fk_ctddm_don FOREIGN KEY (don_dat_mon_id) REFERENCES don_dat_mon(id) ON DELETE CASCADE,
    CONSTRAINT fk_ctddm_mon FOREIGN KEY (mon_an_id) REFERENCES mon_an(id) ON DELETE CASCADE
);

-- 6) REVIEW + COMPLAINT
CREATE TABLE review_danh_gia (
    id VARCHAR(36) PRIMARY KEY,
    khach_hang_id VARCHAR(36) NOT NULL,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL,
    loai_dich_vu VARCHAR(50) NOT NULL,
    booking_id VARCHAR(36),
    reservation_id VARCHAR(36),
    so_sao INTEGER NOT NULL CHECK (so_sao BETWEEN 1 AND 5),
    noi_dung TEXT NOT NULL,
    trang_thai VARCHAR(50) NOT NULL DEFAULT 'DA_HIEN_THI',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_khach_hang FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_ho_so FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE,
    CONSTRAINT chk_review_invariant CHECK (
        (loai_dich_vu = 'KHACH_SAN' AND booking_id IS NOT NULL AND reservation_id IS NULL) OR
        (loai_dich_vu = 'NHA_HANG' AND reservation_id IS NOT NULL AND booking_id IS NULL)
    )
);

CREATE UNIQUE INDEX uk_review_booking ON review_danh_gia (khach_hang_id, booking_id);
CREATE UNIQUE INDEX uk_review_reservation ON review_danh_gia (khach_hang_id, reservation_id);

CREATE TABLE review_aspect_score (
    id VARCHAR(36) PRIMARY KEY,
    review_id VARCHAR(36) NOT NULL,
    aspect VARCHAR(50) NOT NULL,
    score INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_aspect_review FOREIGN KEY (review_id) REFERENCES review_danh_gia(id) ON DELETE CASCADE,
    CONSTRAINT uk_review_aspect UNIQUE (review_id, aspect)
);

CREATE TABLE review_phan_hoi_partner (
    id VARCHAR(36) PRIMARY KEY,
    review_id VARCHAR(36) NOT NULL UNIQUE,
    partner_id VARCHAR(36) NOT NULL,
    noi_dung TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_reply_review FOREIGN KEY (review_id) REFERENCES review_danh_gia(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_reply_partner FOREIGN KEY (partner_id) REFERENCES doi_tac(id) ON DELETE CASCADE
);

CREATE TABLE complaint_khieu_nai (
    id VARCHAR(36) PRIMARY KEY,
    khach_hang_id VARCHAR(36) NOT NULL,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL,
    loai_dich_vu VARCHAR(50) NOT NULL,
    booking_id VARCHAR(36),
    reservation_id VARCHAR(36),
    tieu_de VARCHAR(150) NOT NULL,
    noi_dung_tom_tat TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    muc_do VARCHAR(50) NOT NULL,
    trang_thai VARCHAR(50) NOT NULL DEFAULT 'CHO_PHAN_HOI',
    last_customer_message_at TIMESTAMP,
    last_partner_response_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaint_khach_hang FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id) ON DELETE CASCADE,
    CONSTRAINT fk_complaint_ho_so FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE,
    CONSTRAINT chk_complaint_invariant CHECK (
        (loai_dich_vu = 'KHACH_SAN' AND booking_id IS NOT NULL AND reservation_id IS NULL) OR
        (loai_dich_vu = 'NHA_HANG' AND reservation_id IS NOT NULL AND booking_id IS NULL)
    )
);

CREATE TABLE complaint_tin_nhan (
    id VARCHAR(36) PRIMARY KEY,
    complaint_id VARCHAR(36) NOT NULL,
    nguoi_gui_id VARCHAR(36) NOT NULL,
    vai_tro_nguoi_gui VARCHAR(50) NOT NULL,
    noi_dung TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaint_message_complaint FOREIGN KEY (complaint_id) REFERENCES complaint_khieu_nai(id) ON DELETE CASCADE
);

CREATE TABLE hanh_dong_xu_ly_khieu_nai (
    id VARCHAR(36) PRIMARY KEY,
    khieu_nai_id VARCHAR(36) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    tieu_de VARCHAR(255) NOT NULL,
    mo_ta TEXT NOT NULL,
    amount DECIMAL(12,2),
    currency VARCHAR(10),
    voucher_code VARCHAR(100),
    discount_percent INTEGER,
    status VARCHAR(50) NOT NULL,
    proposed_by_partner_id VARCHAR(36) NOT NULL,
    customer_response_note TEXT,
    partner_completion_note TEXT,
    proposed_at TIMESTAMP NOT NULL,
    customer_responded_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resolution_action_complaint FOREIGN KEY (khieu_nai_id) REFERENCES complaint_khieu_nai(id) ON DELETE CASCADE,
    CONSTRAINT fk_resolution_action_partner FOREIGN KEY (proposed_by_partner_id) REFERENCES doi_tac(id) ON DELETE CASCADE
);

CREATE TABLE lich_su_hoat_dong_khieu_nai (
    id VARCHAR(36) PRIMARY KEY,
    khieu_nai_id VARCHAR(36) NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    actor_id VARCHAR(36),
    actor_role VARCHAR(50) NOT NULL,
    summary VARCHAR(500) NOT NULL,
    metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activity_complaint FOREIGN KEY (khieu_nai_id) REFERENCES complaint_khieu_nai(id) ON DELETE CASCADE
);

CREATE TABLE dinh_kem_phan_hoi (
    id VARCHAR(36) PRIMARY KEY,
    owner_type VARCHAR(50) NOT NULL,
    owner_id VARCHAR(36) NOT NULL,
    file_url TEXT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL CHECK (file_size > 0),
    mo_ta TEXT,
    uploaded_by_id VARCHAR(36) NOT NULL,
    uploaded_by_role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_attachment_owner_type CHECK (owner_type IN ('REVIEW', 'COMPLAINT', 'COMPLAINT_MESSAGE', 'RESOLUTION_ACTION')),
    CONSTRAINT chk_attachment_file_type CHECK (file_type IN ('IMAGE', 'PDF', 'OTHER')),
    CONSTRAINT chk_attachment_uploaded_by_role CHECK (uploaded_by_role IN ('KHACH_HANG', 'DOI_TAC'))
);

-- 7) BEHAVIOR TRACKING
CREATE TABLE su_kien_hanh_vi (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    hanh_dong VARCHAR(50) NOT NULL,
    doi_tuan_id BIGINT NOT NULL,
    loai_doi_tuong VARCHAR(50) NOT NULL,
    thoi_luong_xem_ms INTEGER,
    metadata JSONB,
    thoi_gian TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_su_kien_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 8) PERFORMANCE INDEXES
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_ho_so_kinh_doanh_city ON ho_so_kinh_doanh(thanh_pho);
CREATE INDEX idx_tai_san_city ON tai_san(thanh_pho);
CREATE INDEX idx_tai_san_coord ON tai_san(kinh_do, vi_do);
CREATE INDEX idx_phong_khach_san_deleted ON phong(khach_san_id, deleted);
CREATE INDEX idx_ban_nha_hang_deleted ON ban(nha_hang_id, deleted);
CREATE INDEX idx_don_ks_date_range ON don_khach_san(ngay_check_in, ngay_check_out);
CREATE INDEX idx_don_nh_time_range ON don_nha_hang(ngay_gio_bat_dau, ngay_gio_ket_thuc);
CREATE INDEX idx_don_ks_ct_room ON don_khach_san_chi_tiet(phong_id);
CREATE INDEX idx_don_nh_ban_table ON don_nha_hang_ban(ban_id);
CREATE INDEX idx_complaint_status ON complaint_khieu_nai(trang_thai);
CREATE INDEX idx_resolution_status ON hanh_dong_xu_ly_khieu_nai(status);
CREATE INDEX idx_activity_created_at ON lich_su_hoat_dong_khieu_nai(created_at);
CREATE INDEX idx_attachment_owner ON dinh_kem_phan_hoi(owner_type, owner_id);
CREATE INDEX idx_attachment_uploaded_by ON dinh_kem_phan_hoi(uploaded_by_id, uploaded_by_role);
