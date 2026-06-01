-- =============================================================
-- V3__module4_promotion_voucher_loyalty.sql
-- Module 4: Promotion, Voucher, Loyalty System
-- =============================================================

-- 1) CREATE ENUMS
-- =============================================================

CREATE TYPE loai_giam_gia AS ENUM ('PHAN_TRAM', 'SO_TIEN_CO_DINH');
CREATE TYPE trang_thai_uu_dai AS ENUM ('DA_LEN_LICH', 'DANG_CO_HIEU_LUC', 'TAM_DUNG', 'DA_HET_HAN', 'DA_XOA');
CREATE TYPE created_by_role AS ENUM ('DOI_TAC', 'QUAN_TRI_VIEN');
CREATE TYPE pham_vi_ap_dung AS ENUM ('TOAN_SAN', 'DOI_TAC', 'CO_SO_CU_THE', 'DICH_VU_CU_THE');
CREATE TYPE target_type AS ENUM ('HOTEL', 'ROOM', 'RESTAURANT', 'MENU_ITEM', 'ALL_PLATFORM');
CREATE TYPE trang_thai_customer_voucher AS ENUM ('CHUA_DUNG', 'RESERVED', 'DA_DUNG', 'HET_HAN', 'BI_THU_HOI', 'CANCELLED');
CREATE TYPE loai_giao_dich_diem AS ENUM ('TICH_DIEM', 'DOI_VOUCHER', 'HOAN_DIEM', 'DIEU_CHINH_ADMIN', 'MILESTONE_REWARD', 'COMPENSATION');
CREATE TYPE source_type_voucher AS ENUM ('CAMPAIGN', 'POINT_REDEEM', 'COMPLAINT_COMPENSATION', 'MILESTONE_REWARD', 'SYSTEM_GRANT');

-- 2) LOYALTY CONFIGURATION TABLE
-- =============================================================

CREATE TABLE loyalty_rule (
    id BIGSERIAL PRIMARY KEY,
    money_per_point NUMERIC(12,2) NOT NULL,
    silver_threshold NUMERIC(12,2) NOT NULL,
    gold_threshold NUMERIC(12,2) NOT NULL,
    diamond_threshold NUMERIC(12,2) NOT NULL,
    silver_multiplier NUMERIC(3,2) DEFAULT 1.10,
    gold_multiplier NUMERIC(3,2) DEFAULT 1.25,
    diamond_multiplier NUMERIC(3,2) DEFAULT 1.50,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_thresholds CHECK (silver_threshold < gold_threshold AND gold_threshold < diamond_threshold),
    CONSTRAINT chk_multipliers CHECK (silver_multiplier > 0 AND gold_multiplier > 0 AND diamond_multiplier > 0)
);

CREATE INDEX idx_loyalty_rule_active ON loyalty_rule(is_active);

-- 3) BASE PROMOTION TABLE (UU_DAI)
-- =============================================================

CREATE TABLE uu_dai (
    id BIGSERIAL PRIMARY KEY,
    ten_uu_dai VARCHAR(255) NOT NULL,
    mo_ta TEXT,
    created_by_user_id VARCHAR(36) NOT NULL,
    created_by_role created_by_role NOT NULL,
    business_profile_id VARCHAR(36),
    muc_giam NUMERIC(12,2) NOT NULL,
    loai_giam_gia loai_giam_gia NOT NULL,
    gia_tri_giam_toi_da NUMERIC(12,2),
    ngay_bat_dau TIMESTAMP NOT NULL,
    ngay_ket_thuc TIMESTAMP NOT NULL,
    trang_thai_uu_dai trang_thai_uu_dai DEFAULT 'DA_LEN_LICH',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    version BIGINT DEFAULT 0,
    CONSTRAINT chk_ngay_hop_le CHECK (ngay_ket_thuc > ngay_bat_dau),
    CONSTRAINT chk_muc_giam_hop_le CHECK (muc_giam > 0),
    CONSTRAINT fk_uu_dai_user FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_uu_dai_business_profile FOREIGN KEY (business_profile_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE SET NULL
);

CREATE INDEX idx_uu_dai_created_by ON uu_dai(created_by_user_id);
CREATE INDEX idx_uu_dai_business_profile ON uu_dai(business_profile_id);
CREATE INDEX idx_uu_dai_trang_thai ON uu_dai(trang_thai_uu_dai);
CREATE INDEX idx_uu_dai_ngay_ket_thuc ON uu_dai(ngay_ket_thuc);
CREATE INDEX idx_uu_dai_deleted ON uu_dai(deleted);

-- 4) VOUCHER SUBCLASS TABLE
-- =============================================================

CREATE TABLE voucher (
    uu_dai_id BIGINT PRIMARY KEY,
    ma_voucher VARCHAR(50) UNIQUE NOT NULL,
    so_luong_phat_hanh INT NOT NULL,
    so_luong_da_dung INT DEFAULT 0,
    don_hang_toi_thieu NUMERIC(12,2) DEFAULT 0.00,
    usage_limit_per_user INT DEFAULT 1,
    diem_can_doi INT DEFAULT 0,
    cho_phep_doi_bang_diem BOOLEAN DEFAULT FALSE,
    pham_vi_ap_dung pham_vi_ap_dung NOT NULL,
    CONSTRAINT chk_so_luong_hop_le CHECK (so_luong_da_dung <= so_luong_phat_hanh),
    CONSTRAINT fk_voucher_uu_dai FOREIGN KEY (uu_dai_id) REFERENCES uu_dai(id) ON DELETE CASCADE
);

CREATE INDEX idx_voucher_ma_voucher ON voucher(ma_voucher);
CREATE INDEX idx_voucher_cho_phep_doi ON voucher(cho_phep_doi_bang_diem);

-- 5) DIRECT PROMOTION SUBCLASS TABLE (KHUYEN_MAI_TRUC_TIEP)
-- =============================================================

CREATE TABLE khuyen_mai_truc_tiep (
    uu_dai_id BIGINT PRIMARY KEY,
    target_type target_type NOT NULL,
    target_id VARCHAR(255),
    CONSTRAINT fk_khuyen_mai_uu_dai FOREIGN KEY (uu_dai_id) REFERENCES uu_dai(id) ON DELETE CASCADE
);

CREATE INDEX idx_khuyen_mai_target ON khuyen_mai_truc_tiep(target_type, target_id);

-- 6) CUSTOMER VOUCHER WALLET
-- =============================================================

CREATE TABLE customer_voucher (
    id BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(36) NOT NULL,
    voucher_id BIGINT NOT NULL,
    ma_voucher_ca_nhan VARCHAR(100),
    trang_thai trang_thai_customer_voucher DEFAULT 'CHUA_DUNG',
    source_type source_type_voucher DEFAULT 'CAMPAIGN',
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reserved_at TIMESTAMP,
    reserve_expires_at TIMESTAMP,
    used_at TIMESTAMP,
    expired_at TIMESTAMP,
    booking_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_voucher_customer FOREIGN KEY (customer_id) REFERENCES khach_hang(id) ON DELETE CASCADE,
    CONSTRAINT fk_customer_voucher_voucher FOREIGN KEY (voucher_id) REFERENCES uu_dai(id) ON DELETE CASCADE
);

CREATE INDEX idx_customer_voucher_customer ON customer_voucher(customer_id);
CREATE INDEX idx_customer_voucher_voucher ON customer_voucher(voucher_id);
CREATE INDEX idx_customer_voucher_status ON customer_voucher(trang_thai);
CREATE INDEX idx_customer_voucher_reserve_expires ON customer_voucher(reserve_expires_at) WHERE trang_thai = 'RESERVED';
CREATE INDEX idx_customer_voucher_source ON customer_voucher(source_type);

-- 7) POINT HISTORY TABLE (LICH_SU_DIEM)
-- =============================================================

CREATE TABLE lich_su_diem (
    id BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(36) NOT NULL,
    so_diem_thay_doi INT NOT NULL,
    loai_giao_dich_diem loai_giao_dich_diem NOT NULL,
    diem_truoc_giao_dich INT NOT NULL,
    diem_sau_giao_dich INT NOT NULL,
    booking_id BIGINT,
    voucher_id BIGINT,
    ghi_chu TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lich_su_diem_customer FOREIGN KEY (customer_id) REFERENCES khach_hang(id) ON DELETE CASCADE,
    CONSTRAINT fk_lich_su_diem_voucher FOREIGN KEY (voucher_id) REFERENCES uu_dai(id) ON DELETE SET NULL
);

CREATE INDEX idx_lich_su_diem_customer ON lich_su_diem(customer_id);
CREATE INDEX idx_lich_su_diem_loai_giao_dich ON lich_su_diem(loai_giao_dich_diem);
CREATE INDEX idx_lich_su_diem_booking ON lich_su_diem(booking_id);
CREATE INDEX idx_lich_su_diem_created_at ON lich_su_diem(created_at DESC);

-- 8) PROMOTION ANALYTICS (DAILY AGGREGATION)
-- =============================================================

CREATE TABLE promotion_analytics_daily (
    id BIGSERIAL PRIMARY KEY,
    campaign_id BIGINT NOT NULL,
    ngay DATE NOT NULL,
    usage_count INT DEFAULT 0,
    booking_count INT DEFAULT 0,
    generated_revenue NUMERIC(15,2) DEFAULT 0,
    discount_cost NUMERIC(15,2) DEFAULT 0,
    conversion_rate NUMERIC(5,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_analytics_campaign FOREIGN KEY (campaign_id) REFERENCES uu_dai(id) ON DELETE CASCADE,
    CONSTRAINT uk_analytics_campaign_date UNIQUE (campaign_id, ngay)
);

CREATE INDEX idx_analytics_campaign ON promotion_analytics_daily(campaign_id);
CREATE INDEX idx_analytics_date ON promotion_analytics_daily(ngay DESC);

-- 9) MILESTONE TRACKING (TIEN TRINH THANH TICH)
-- =============================================================

CREATE TABLE milestone_progress (
    id BIGSERIAL PRIMARY KEY,
    customer_id VARCHAR(36) NOT NULL,
    completed_bookings INT DEFAULT 0,
    milestone_5_granted BOOLEAN DEFAULT FALSE,
    milestone_5_granted_at TIMESTAMP,
    milestone_10_granted BOOLEAN DEFAULT FALSE,
    milestone_10_granted_at TIMESTAMP,
    milestone_20_granted BOOLEAN DEFAULT FALSE,
    milestone_20_granted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_milestone_customer FOREIGN KEY (customer_id) REFERENCES khach_hang(id) ON DELETE CASCADE,
    CONSTRAINT uk_milestone_customer UNIQUE (customer_id)
);

CREATE INDEX idx_milestone_customer ON milestone_progress(customer_id);

-- 10) NOTIFICATION EVENTS (SU KIEN THONG BAO)
-- =============================================================

CREATE TABLE notification_event (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    customer_id VARCHAR(36) NOT NULL,
    business_id VARCHAR(36),
    ref_id BIGINT,
    metadata JSONB,
    is_processed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    CONSTRAINT fk_event_customer FOREIGN KEY (customer_id) REFERENCES khach_hang(id) ON DELETE CASCADE
);

CREATE INDEX idx_notification_event_customer ON notification_event(customer_id);
CREATE INDEX idx_notification_event_type ON notification_event(event_type);
CREATE INDEX idx_notification_event_created_at ON notification_event(created_at DESC);
CREATE INDEX idx_notification_event_processed ON notification_event(is_processed);

-- 11) IDEMPOTENCY KEY TRACKING
-- =============================================================

CREATE TABLE idempotency_key (
    id BIGSERIAL PRIMARY KEY,
    idempotency_key VARCHAR(100) UNIQUE NOT NULL,
    operation_type VARCHAR(100) NOT NULL,
    customer_id VARCHAR(36),
    response_body JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_idempotency_customer FOREIGN KEY (customer_id) REFERENCES khach_hang(id) ON DELETE CASCADE
);

CREATE INDEX idx_idempotency_key ON idempotency_key(idempotency_key);
CREATE INDEX idx_idempotency_customer ON idempotency_key(customer_id);

-- 12) ADD OPTIMISTIC LOCKING TO KHACH_HANG IF NOT EXISTS
-- =============================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name='khach_hang' AND column_name='version'
    ) THEN
        ALTER TABLE khach_hang ADD COLUMN version BIGINT DEFAULT 0;
    END IF;
END $$;

-- 13) SEED DEFAULT LOYALTY RULE
-- =============================================================

INSERT INTO loyalty_rule (money_per_point, silver_threshold, gold_threshold, diamond_threshold, is_active)
VALUES (10000, 5000000, 20000000, 50000000, TRUE)
ON CONFLICT DO NOTHING;
