-- =============================================================
-- V7__module5_final_phase1_entities.sql
-- Thêm các bảng và cột mới cho Module 5 (Danh giá & Khiếu nại) theo tài liệu FINAL
-- Tương thích: PostgreSQL + H2
-- =============================================================

-- 1. Cập nhật bảng complaint_khieu_nai thêm category
ALTER TABLE complaint_khieu_nai ADD COLUMN category VARCHAR(50);

-- Backfill dữ liệu cũ
UPDATE complaint_khieu_nai SET category = 'OTHER' WHERE category IS NULL;

-- Không set NOT NULL ngay để an toàn với H2 nếu có lỗi syntax (service layer sẽ validate nullable).
-- Tuy nhiên H2 hỗ trợ ALTER TABLE ... ALTER COLUMN ... SET NOT NULL.
ALTER TABLE complaint_khieu_nai ALTER COLUMN category SET NOT NULL;

-- 2. Bảng review_aspect_score (P2)
CREATE TABLE IF NOT EXISTS review_aspect_score (
    id VARCHAR(36) PRIMARY KEY,
    review_id VARCHAR(36) NOT NULL,
    aspect VARCHAR(50) NOT NULL,
    score INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_review_aspect_review FOREIGN KEY (review_id) REFERENCES review_danh_gia(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_review_aspect ON review_aspect_score(review_id, aspect);
CREATE INDEX IF NOT EXISTS idx_review_aspect_review ON review_aspect_score(review_id);


-- 3. Bảng hanh_dong_xu_ly_khieu_nai (Resolution Action)
CREATE TABLE IF NOT EXISTS hanh_dong_xu_ly_khieu_nai (
    id VARCHAR(36) PRIMARY KEY,
    khieu_nai_id VARCHAR(36) NOT NULL,
    
    action_type VARCHAR(50) NOT NULL,
    tieu_de VARCHAR(255) NOT NULL,
    mo_ta TEXT NOT NULL,
    
    amount DECIMAL(12,2),
    currency VARCHAR(10),
    voucher_code VARCHAR(100),
    discount_percent INT,
    
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

CREATE INDEX IF NOT EXISTS idx_resolution_action_complaint ON hanh_dong_xu_ly_khieu_nai(khieu_nai_id);
CREATE INDEX IF NOT EXISTS idx_resolution_action_status ON hanh_dong_xu_ly_khieu_nai(status);


-- 4. Bảng lich_su_hoat_dong_khieu_nai (Complaint Activity)
CREATE TABLE IF NOT EXISTS lich_su_hoat_dong_khieu_nai (
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

CREATE INDEX IF NOT EXISTS idx_activity_complaint ON lich_su_hoat_dong_khieu_nai(khieu_nai_id);
CREATE INDEX IF NOT EXISTS idx_activity_created_at ON lich_su_hoat_dong_khieu_nai(created_at);
