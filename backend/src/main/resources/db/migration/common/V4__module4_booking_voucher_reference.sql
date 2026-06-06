-- =============================================================
-- V4__module4_booking_voucher_reference.sql
-- Align booking schema with Module 4 voucher lifecycle
-- =============================================================

ALTER TABLE don_dat_cho
    ADD COLUMN IF NOT EXISTS voucher_id BIGINT;

ALTER TABLE don_dat_cho
    ADD CONSTRAINT fk_don_dat_cho_customer_voucher
    FOREIGN KEY (voucher_id) REFERENCES customer_voucher(id) ON DELETE SET NULL;

CREATE INDEX idx_don_dat_cho_voucher_id ON don_dat_cho(voucher_id);
