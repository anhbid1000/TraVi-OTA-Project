-- V6: Feedback attachments for Module 5
-- Tạo bảng đính kèm dùng chung cho Review, Complaint và ComplaintMessage.
-- Dùng polymorphic owner_type + owner_id theo tài liệu module 5 để giữ schema gọn.

-- 1) Bảng attachment dùng chung
CREATE TABLE IF NOT EXISTS dinh_kem_phan_hoi (
    id VARCHAR(36) PRIMARY KEY,

    owner_type VARCHAR(50) NOT NULL,
    owner_id VARCHAR(36) NOT NULL,

    file_url TEXT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,

    mo_ta TEXT,

    uploaded_by_id VARCHAR(36) NOT NULL,
    uploaded_by_role VARCHAR(50) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_attachment_owner_type
        CHECK (owner_type IN ('REVIEW', 'COMPLAINT', 'COMPLAINT_MESSAGE')),
    CONSTRAINT chk_attachment_file_type
        CHECK (file_type IN ('IMAGE', 'PDF', 'OTHER')),
    CONSTRAINT chk_attachment_uploaded_by_role
        CHECK (uploaded_by_role IN ('KHACH_HANG', 'DOI_TAC')),
    CONSTRAINT chk_attachment_file_size_positive
        CHECK (file_size > 0)
);

CREATE INDEX IF NOT EXISTS idx_attachment_owner
ON dinh_kem_phan_hoi(owner_type, owner_id);

CREATE INDEX IF NOT EXISTS idx_attachment_uploaded_by
ON dinh_kem_phan_hoi(uploaded_by_id, uploaded_by_role);

-- 2) DB invariant cho Review: đúng 1 trong booking_id/reservation_id theo loai_dich_vu.
-- PostgreSQL/H2 đều hỗ trợ CHECK constraint dạng này.
ALTER TABLE review_danh_gia DROP CONSTRAINT IF EXISTS chk_review_service_order_invariant;
ALTER TABLE review_danh_gia ADD CONSTRAINT chk_review_service_order_invariant CHECK (
    (
        loai_dich_vu = 'KHACH_SAN'
        AND booking_id IS NOT NULL
        AND reservation_id IS NULL
    )
    OR
    (
        loai_dich_vu = 'NHA_HANG'
        AND reservation_id IS NOT NULL
        AND booking_id IS NULL
    )
);

-- 3) DB invariant cho Complaint: áp dụng cùng rule booking/reservation.
ALTER TABLE complaint_khieu_nai DROP CONSTRAINT IF EXISTS chk_complaint_service_order_invariant;
ALTER TABLE complaint_khieu_nai ADD CONSTRAINT chk_complaint_service_order_invariant CHECK (
    (
        loai_dich_vu = 'KHACH_SAN'
        AND booking_id IS NOT NULL
        AND reservation_id IS NULL
    )
    OR
    (
        loai_dich_vu = 'NHA_HANG'
        AND reservation_id IS NOT NULL
        AND booking_id IS NULL
    )
);

-- 4) Index hỗ trợ truy vấn theo đơn thật và tránh review trùng.
-- Unique với nullable column chạy khác nhau giữa DB, nên vẫn validate trùng ở service layer.
-- Các index dưới đây phục vụ query nhanh và tương thích H2/PostgreSQL.
CREATE INDEX IF NOT EXISTS idx_review_booking
ON review_danh_gia(khach_hang_id, booking_id);

CREATE INDEX IF NOT EXISTS idx_review_reservation
ON review_danh_gia(khach_hang_id, reservation_id);

CREATE INDEX IF NOT EXISTS idx_complaint_booking
ON complaint_khieu_nai(khach_hang_id, booking_id);

CREATE INDEX IF NOT EXISTS idx_complaint_reservation
ON complaint_khieu_nai(khach_hang_id, reservation_id);
