-- =============================================================
-- V3__init_feedback_schema.sql
-- Module 5: Danh Gia & Khieu Nai (No-Admin Edition)
-- Tao schema moi theo document MODULE_5_DANH_GIA_KHIEU_NAI_UPDATED.md
-- =============================================================

-- =============================================================
-- 1. BANG: REVIEW (DanhGia)
-- =============================================================
CREATE TABLE review_danh_gia (
    id VARCHAR(36) PRIMARY KEY,
    khach_hang_id VARCHAR(36) NOT NULL,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL,
    loai_dich_vu VARCHAR(50) NOT NULL,
    booking_id VARCHAR(36),
    reservation_id VARCHAR(36),
    so_sao INT NOT NULL CHECK (so_sao >= 1 AND so_sao <= 5),
    noi_dung TEXT NOT NULL,
    trang_thai VARCHAR(50) NOT NULL DEFAULT 'DA_HIEN_THI',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Invariants (PostgreSQL logic)
    -- Either booking_id has value OR reservation_id has value, but not both
    CONSTRAINT chk_review_invariant CHECK (
        (loai_dich_vu = 'KHACH_SAN' AND booking_id IS NOT NULL AND reservation_id IS NULL) OR
        (loai_dich_vu = 'NHA_HANG' AND reservation_id IS NOT NULL AND booking_id IS NULL)
    ),
    
    -- Foreign keys
    CONSTRAINT fk_review_khach_hang FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_ho_so FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE
);

-- Unique constraint for reviewing (H2 + PostgreSQL compatible)
-- Không dùng partial index WHERE ... IS NOT NULL vì H2 không hỗ trợ.
-- Với unique index composite, NULL vẫn được phép lặp trong H2/PostgreSQL nên vẫn đáp ứng logic cần thiết.
CREATE UNIQUE INDEX IF NOT EXISTS uk_review_booking ON review_danh_gia (khach_hang_id, booking_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_review_reservation ON review_danh_gia (khach_hang_id, reservation_id);

-- =============================================================
-- 2. BANG: REVIEW REPLY (PhanHoiDanhGia)
-- =============================================================
CREATE TABLE review_phan_hoi_partner (
    id VARCHAR(36) PRIMARY KEY,
    review_id VARCHAR(36) NOT NULL UNIQUE,
    partner_id VARCHAR(36) NOT NULL,
    noi_dung TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_reply_review FOREIGN KEY (review_id) REFERENCES review_danh_gia(id) ON DELETE CASCADE,
    CONSTRAINT fk_reply_partner FOREIGN KEY (partner_id) REFERENCES doi_tac(id) ON DELETE CASCADE
);

-- =============================================================
-- 3. BANG: COMPLAINT TICKET (KhieuNai)
-- =============================================================
CREATE TABLE complaint_khieu_nai (
    id VARCHAR(36) PRIMARY KEY,
    khach_hang_id VARCHAR(36) NOT NULL,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL,
    loai_dich_vu VARCHAR(50) NOT NULL,
    booking_id VARCHAR(36),
    reservation_id VARCHAR(36),
    tieu_de VARCHAR(150) NOT NULL,
    noi_dung_tom_tat TEXT NOT NULL,
    muc_do VARCHAR(50) NOT NULL,
    trang_thai VARCHAR(50) NOT NULL DEFAULT 'CHO_PHAN_HOI',
    
    last_customer_message_at TIMESTAMP,
    last_partner_response_at TIMESTAMP,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Invariants
    CONSTRAINT chk_complaint_invariant CHECK (
        (loai_dich_vu = 'KHACH_SAN' AND booking_id IS NOT NULL AND reservation_id IS NULL) OR
        (loai_dich_vu = 'NHA_HANG' AND reservation_id IS NOT NULL AND booking_id IS NULL)
    ),
    
    CONSTRAINT fk_complaint_khach_hang FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id) ON DELETE CASCADE,
    CONSTRAINT fk_complaint_ho_so FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE
);

-- =============================================================
-- 4. BANG: COMPLAINT MESSAGES (TinNhanKhieuNai)
-- =============================================================
CREATE TABLE complaint_tin_nhan (
    id VARCHAR(36) PRIMARY KEY,
    complaint_id VARCHAR(36) NOT NULL,
    nguoi_gui_id VARCHAR(36) NOT NULL,
    vai_tro_nguoi_gui VARCHAR(50) NOT NULL,
    noi_dung TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_msg_complaint FOREIGN KEY (complaint_id) REFERENCES complaint_khieu_nai(id) ON DELETE CASCADE
);
