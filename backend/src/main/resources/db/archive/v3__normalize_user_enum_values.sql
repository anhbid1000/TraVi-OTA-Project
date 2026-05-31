-- =============================================
-- V3__normalize_user_enum_values.sql
-- Muc dich:
--   - Chuyển dữ liệu enum cũ sang enum constant mới.
--   - Đồng bộ default DB voi enum Java đang dùng EnumType.STRING.
-- Chỉ cần chạy nếu database đã có dữ liệu cũ.
-- Nếu đang ở môi trường dev và có thể drop schema, có thể không cần file này.
-- =============================================

UPDATE users
SET gioi_tinh = CASE gioi_tinh
    WHEN 'Nam' THEN 'NAM'
    WHEN 'Nu' THEN 'NU'
    WHEN 'Khac' THEN 'KHAC'
    ELSE gioi_tinh
END
WHERE gioi_tinh IN ('Nam', 'Nu', 'Khac');

UPDATE users
SET trang_thai = 'BI_KHOA_TAM_THOI'
WHERE trang_thai = 'BI_KHOA';

UPDATE khach_hang
SET hang_thanh_vien = CASE hang_thanh_vien
    WHEN 'Dong' THEN 'DONG'
    WHEN 'Bac' THEN 'BAC'
    WHEN 'Vang' THEN 'VANG'
    WHEN 'Kim Cuong' THEN 'KIM_CUONG'
    WHEN 'Kim_Cuong' THEN 'KIM_CUONG'
    WHEN 'KIM CUONG' THEN 'KIM_CUONG'
    ELSE hang_thanh_vien
END
WHERE hang_thanh_vien IN ('Dong', 'Bac', 'Vang', 'Kim Cuong', 'Kim_Cuong', 'KIM CUONG');

ALTER TABLE khach_hang
ALTER COLUMN hang_thanh_vien SET DEFAULT 'DONG';
