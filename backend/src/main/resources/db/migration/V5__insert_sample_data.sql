-- =============================================================
-- PATCH Mammo: Chuẩn hóa dữ liệu sample về 2 tài khoản chính
-- customer: 875f97ce-e548-4507-a4e0-77efc0977ad1 (anhbid1000@)
-- partner : 3816f90e-d594-4018-ab4e-0921e838121f (anhbid2000@gmail.com)
-- =============================================================

-- Xóa các bản ghi sample customer/partner cũ theo pattern CUSTOMER-* và PARTNER-*
DELETE FROM khach_hang WHERE id LIKE 'CUSTOMER-%' OR id = '875f97ce-e548-4507-a4e0-77efc0977ad1';
DELETE FROM doi_tac WHERE id LIKE 'PARTNER-%' OR id = '3816f90e-d594-4018-ab4e-0921e838121f';
DELETE FROM users WHERE id LIKE 'CUSTOMER-%' OR id LIKE 'PARTNER-%' OR id IN ('875f97ce-e548-4507-a4e0-77efc0977ad1', '3816f90e-d594-4018-ab4e-0921e838121f');

-- Tạo/cập nhật 2 tài khoản chính
INSERT INTO users (id, email, username, mat_khau, ho_ten, ngay_sinh, gioi_tinh, so_dien_thoai, trang_thai, vai_tro_id)
VALUES ('875f97ce-e548-4507-a4e0-77efc0977ad1', 'anhbid1000@gmail.com', 'anhbid1000', '$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK', 'Anh Bid Customer', '1995-01-01', 'NAM', '0900000001', 'HOAT_DONG', 'ROLE-GUEST');

INSERT INTO khach_hang (id, diem_thanh_vien, hang_thanh_vien, tong_chi_tieu)
VALUES ('875f97ce-e548-4507-a4e0-77efc0977ad1', 4200, 'KIM_CUONG', 25500000.0);

INSERT INTO users (id, email, username, mat_khau, ho_ten, ngay_sinh, gioi_tinh, so_dien_thoai, trang_thai, vai_tro_id)
VALUES ('3816f90e-d594-4018-ab4e-0921e838121f', 'anhbid2000@gmail.com', 'anhbid2000', '$2a$10$iUQU306yNwtv.nA0.4DYKOpzTyFid302Jsllw0y1N3mmX3I3IxkqK', 'Anh Bid Partner', '1990-01-01', 'NAM', '0900000002', 'HOAT_DONG', 'ROLE-PARTNER');

INSERT INTO doi_tac (id, ti_le_chiet_khau)
VALUES ('3816f90e-d594-4018-ab4e-0921e838121f', 0.12);



-- =============================================================
-- 21. EXTRA DATASET MAMMO - DỮ LIỆU MỞ RỘNG ĐA DẠNG CHO 2 TÀI KHOẢN
-- =============================================================
INSERT INTO ho_so_kinh_doanh (id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh, toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho, quan_huyen, phuong_xa, kinh_do, vi_do, trang_thai_kiem_duyet, trang_thai_hoat_dong, thoi_gian_dang_ky)
VALUES ('HOSOKD-AH001', '3816f90e-d594-4018-ab4e-0921e838121f', 'KHACH_SAN', 'Aurora Hotel Saigon', 'MS-AH001', 'LP-AH001', '10.7868,106.6844', '0900000002', 'anhbid2000@gmail.com', 'Địa chỉ Aurora Hotel Saigon', 'Hồ Chí Minh', 'Quận 3', 'Phường 6', 106.6844, 10.7868, 'DA_DUYET', 'DANG_HOAT_DONG', CURRENT_TIMESTAMP);
INSERT INTO tai_san (id_tai_san, ho_so_kinh_doanh_id, mo_ta, trang_thai, gia_co_ban, is_dynamic_pricing, rating_average, review_count)
VALUES ('TAISAN-AH001', 'HOSOKD-AH001', 'Khách sạn business trung tâm', 'SAN_SANG', 1450000.0, TRUE, 0.0, 0);
INSERT INTO khach_san (id_tai_san, ten, hang_sao, loai_khach_san, gio_nhan_phong, gio_tra_phong, gio_nhan_phong_mac_dinh, gio_tra_phong_mac_dinh, so_tang, tong_so_phong, diem_danh_gia_trung_binh, so_luong_danh_gia)
VALUES ('TAISAN-AH001', 'Aurora Hotel Saigon', 4, 'Khách sạn 4 sao', '14:00:00', '12:00:00', '14:00:00', '12:00:00', 7, 160, 0.0, 0);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH001-01', 'TAISAN-AH001', '101', 'Phòng Standard Aurora Hotel Saigon', 'Standard', 'Phòng Standard cho Aurora Hotel Saigon', 3, 1, 30, 1160000.0, 2, 'SAN_SANG', 5, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH001-02', 'TAISAN-AH001', '202', 'Phòng Deluxe Aurora Hotel Saigon', 'Deluxe', 'Phòng Deluxe cho Aurora Hotel Saigon', 4, 1, 38, 1450000.0, 2, 'SAN_SANG', 10, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH001-03', 'TAISAN-AH001', '303', 'Phòng Suite Aurora Hotel Saigon', 'Suite', 'Phòng Suite cho Aurora Hotel Saigon', 5, 2, 46, 1957500.0, 1, 'SAN_SANG', 15, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ho_so_kinh_doanh (id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh, toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho, quan_huyen, phuong_xa, kinh_do, vi_do, trang_thai_kiem_duyet, trang_thai_hoat_dong, thoi_gian_dang_ky)
VALUES ('HOSOKD-AH002', '3816f90e-d594-4018-ab4e-0921e838121f', 'KHACH_SAN', 'Mekong Riverside Can Tho', 'MS-AH002', 'LP-AH002', '10.0365,105.7849', '0900000002', 'anhbid2000@gmail.com', 'Địa chỉ Mekong Riverside Can Tho', 'Cần Thơ', 'Ninh Kiều', 'Tân An', 105.7849, 10.0365, 'DA_DUYET', 'DANG_HOAT_DONG', CURRENT_TIMESTAMP);
INSERT INTO tai_san (id_tai_san, ho_so_kinh_doanh_id, mo_ta, trang_thai, gia_co_ban, is_dynamic_pricing, rating_average, review_count)
VALUES ('TAISAN-AH002', 'HOSOKD-AH002', 'Khách sạn ven sông', 'SAN_SANG', 1250000.0, TRUE, 0.0, 0);
INSERT INTO khach_san (id_tai_san, ten, hang_sao, loai_khach_san, gio_nhan_phong, gio_tra_phong, gio_nhan_phong_mac_dinh, gio_tra_phong_mac_dinh, so_tang, tong_so_phong, diem_danh_gia_trung_binh, so_luong_danh_gia)
VALUES ('TAISAN-AH002', 'Mekong Riverside Can Tho', 4, 'Khách sạn 4 sao', '14:00:00', '12:00:00', '14:00:00', '12:00:00', 7, 160, 0.0, 0);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH002-01', 'TAISAN-AH002', '101', 'Phòng Standard Mekong Riverside Can Tho', 'Standard', 'Phòng Standard cho Mekong Riverside Can Tho', 3, 1, 30, 1000000.0, 2, 'SAN_SANG', 5, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH002-02', 'TAISAN-AH002', '202', 'Phòng Deluxe Mekong Riverside Can Tho', 'Deluxe', 'Phòng Deluxe cho Mekong Riverside Can Tho', 4, 1, 38, 1250000.0, 2, 'SAN_SANG', 10, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH002-03', 'TAISAN-AH002', '303', 'Phòng Suite Mekong Riverside Can Tho', 'Suite', 'Phòng Suite cho Mekong Riverside Can Tho', 5, 2, 46, 1687500.0, 1, 'SAN_SANG', 15, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ho_so_kinh_doanh (id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh, toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho, quan_huyen, phuong_xa, kinh_do, vi_do, trang_thai_kiem_duyet, trang_thai_hoat_dong, thoi_gian_dang_ky)
VALUES ('HOSOKD-AH003', '3816f90e-d594-4018-ab4e-0921e838121f', 'KHACH_SAN', 'Cloudy Peaks Da Lat', 'MS-AH003', 'LP-AH003', '11.9404,108.4419', '0900000002', 'anhbid2000@gmail.com', 'Địa chỉ Cloudy Peaks Da Lat', 'Lâm Đồng', 'Đà Lạt', 'Phường 3', 108.4419, 11.9404, 'DA_DUYET', 'DANG_HOAT_DONG', CURRENT_TIMESTAMP);
INSERT INTO tai_san (id_tai_san, ho_so_kinh_doanh_id, mo_ta, trang_thai, gia_co_ban, is_dynamic_pricing, rating_average, review_count)
VALUES ('TAISAN-AH003', 'HOSOKD-AH003', 'Khách sạn view đồi thông', 'SAN_SANG', 1100000.0, TRUE, 0.0, 0);
INSERT INTO khach_san (id_tai_san, ten, hang_sao, loai_khach_san, gio_nhan_phong, gio_tra_phong, gio_nhan_phong_mac_dinh, gio_tra_phong_mac_dinh, so_tang, tong_so_phong, diem_danh_gia_trung_binh, so_luong_danh_gia)
VALUES ('TAISAN-AH003', 'Cloudy Peaks Da Lat', 3, 'Khách sạn 3 sao', '14:00:00', '12:00:00', '14:00:00', '12:00:00', 6, 140, 0.0, 0);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH003-01', 'TAISAN-AH003', '101', 'Phòng Standard Cloudy Peaks Da Lat', 'Standard', 'Phòng Standard cho Cloudy Peaks Da Lat', 3, 1, 30, 880000.0, 2, 'SAN_SANG', 5, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH003-02', 'TAISAN-AH003', '202', 'Phòng Deluxe Cloudy Peaks Da Lat', 'Deluxe', 'Phòng Deluxe cho Cloudy Peaks Da Lat', 4, 1, 38, 1100000.0, 2, 'SAN_SANG', 10, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH003-03', 'TAISAN-AH003', '303', 'Phòng Suite Cloudy Peaks Da Lat', 'Suite', 'Phòng Suite cho Cloudy Peaks Da Lat', 5, 2, 46, 1485000.0, 1, 'SAN_SANG', 15, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ho_so_kinh_doanh (id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh, toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho, quan_huyen, phuong_xa, kinh_do, vi_do, trang_thai_kiem_duyet, trang_thai_hoat_dong, thoi_gian_dang_ky)
VALUES ('HOSOKD-AH004', '3816f90e-d594-4018-ab4e-0921e838121f', 'KHACH_SAN', 'Hanoi Old Quarter Nest', 'MS-AH004', 'LP-AH004', '21.0343,105.8524', '0900000002', 'anhbid2000@gmail.com', 'Địa chỉ Hanoi Old Quarter Nest', 'Hà Nội', 'Hoàn Kiếm', 'Hàng Bạc', 105.8524, 21.0343, 'DA_DUYET', 'DANG_HOAT_DONG', CURRENT_TIMESTAMP);
INSERT INTO tai_san (id_tai_san, ho_so_kinh_doanh_id, mo_ta, trang_thai, gia_co_ban, is_dynamic_pricing, rating_average, review_count)
VALUES ('TAISAN-AH004', 'HOSOKD-AH004', 'Khách sạn phố cổ', 'SAN_SANG', 1380000.0, TRUE, 0.0, 0);
INSERT INTO khach_san (id_tai_san, ten, hang_sao, loai_khach_san, gio_nhan_phong, gio_tra_phong, gio_nhan_phong_mac_dinh, gio_tra_phong_mac_dinh, so_tang, tong_so_phong, diem_danh_gia_trung_binh, so_luong_danh_gia)
VALUES ('TAISAN-AH004', 'Hanoi Old Quarter Nest', 4, 'Khách sạn 4 sao', '14:00:00', '12:00:00', '14:00:00', '12:00:00', 7, 160, 0.0, 0);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH004-01', 'TAISAN-AH004', '101', 'Phòng Standard Hanoi Old Quarter Nest', 'Standard', 'Phòng Standard cho Hanoi Old Quarter Nest', 3, 1, 30, 1104000.0, 2, 'SAN_SANG', 5, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH004-02', 'TAISAN-AH004', '202', 'Phòng Deluxe Hanoi Old Quarter Nest', 'Deluxe', 'Phòng Deluxe cho Hanoi Old Quarter Nest', 4, 1, 38, 1380000.0, 2, 'SAN_SANG', 10, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH004-03', 'TAISAN-AH004', '303', 'Phòng Suite Hanoi Old Quarter Nest', 'Suite', 'Phòng Suite cho Hanoi Old Quarter Nest', 5, 2, 46, 1863000.0, 1, 'SAN_SANG', 15, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ho_so_kinh_doanh (id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh, toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho, quan_huyen, phuong_xa, kinh_do, vi_do, trang_thai_kiem_duyet, trang_thai_hoat_dong, thoi_gian_dang_ky)
VALUES ('HOSOKD-AH005', '3816f90e-d594-4018-ab4e-0921e838121f', 'KHACH_SAN', 'Mui Ne Ocean Breeze', 'MS-AH005', 'LP-AH005', '10.9559,108.2641', '0900000002', 'anhbid2000@gmail.com', 'Địa chỉ Mui Ne Ocean Breeze', 'Bình Thuận', 'Phan Thiết', 'Mũi Né', 108.2641, 10.9559, 'DA_DUYET', 'DANG_HOAT_DONG', CURRENT_TIMESTAMP);
INSERT INTO tai_san (id_tai_san, ho_so_kinh_doanh_id, mo_ta, trang_thai, gia_co_ban, is_dynamic_pricing, rating_average, review_count)
VALUES ('TAISAN-AH005', 'HOSOKD-AH005', 'Resort biển nghỉ dưỡng', 'SAN_SANG', 2100000.0, TRUE, 0.0, 0);
INSERT INTO khach_san (id_tai_san, ten, hang_sao, loai_khach_san, gio_nhan_phong, gio_tra_phong, gio_nhan_phong_mac_dinh, gio_tra_phong_mac_dinh, so_tang, tong_so_phong, diem_danh_gia_trung_binh, so_luong_danh_gia)
VALUES ('TAISAN-AH005', 'Mui Ne Ocean Breeze', 5, 'Khách sạn 5 sao', '14:00:00', '12:00:00', '14:00:00', '12:00:00', 8, 180, 0.0, 0);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH005-01', 'TAISAN-AH005', '101', 'Phòng Standard Mui Ne Ocean Breeze', 'Standard', 'Phòng Standard cho Mui Ne Ocean Breeze', 3, 1, 30, 1680000.0, 2, 'SAN_SANG', 5, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH005-02', 'TAISAN-AH005', '202', 'Phòng Deluxe Mui Ne Ocean Breeze', 'Deluxe', 'Phòng Deluxe cho Mui Ne Ocean Breeze', 4, 1, 38, 2100000.0, 2, 'SAN_SANG', 10, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia, deleted, created_at, updated_at)
VALUES ('PHONG-AH005-03', 'TAISAN-AH005', '303', 'Phòng Suite Mui Ne Ocean Breeze', 'Suite', 'Phòng Suite cho Mui Ne Ocean Breeze', 5, 2, 46, 2835000.0, 1, 'SAN_SANG', 15, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ho_so_kinh_doanh (id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh, toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho, quan_huyen, phuong_xa, kinh_do, vi_do, trang_thai_kiem_duyet, trang_thai_hoat_dong, thoi_gian_dang_ky)
VALUES ('HOSOKD-AR001', '3816f90e-d594-4018-ab4e-0921e838121f', 'NHA_HANG', 'Saigon Grill House', 'MS-AR001', 'LP-AR001', '10.7762,106.705', '0900000002', 'anhbid2000@gmail.com', 'Địa chỉ Saigon Grill House', 'Hồ Chí Minh', 'Quận 1', 'Bến Nghé', 106.705, 10.7762, 'DA_DUYET', 'DANG_HOAT_DONG', CURRENT_TIMESTAMP);
INSERT INTO tai_san (id_tai_san, ho_so_kinh_doanh_id, mo_ta, trang_thai, gia_co_ban, is_dynamic_pricing, rating_average, review_count)
VALUES ('TAISAN-AR001', 'HOSOKD-AR001', 'Nhà hàng Nướng hiện đại', 'SAN_SANG', 420000.0, FALSE, 0.0, 0);
INSERT INTO nha_hang (id_tai_san, ten, loai_am_thuc, gio_mo_cua, gio_dong_cua, suc_chua, co_dat_ban_truoc, co_dat_mon_truoc, diem_danh_gia_trung_binh, so_luong_danh_gia)
VALUES ('TAISAN-AR001', 'Saigon Grill House', 'Nướng hiện đại', '10:00:00', '22:30:00', 180, TRUE, TRUE, 0.0, 0);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR001-01', 'TAISAN-AR001', 'Bàn 1', 'Sảnh 1', 'Bàn 2 chỗ tại Saigon Grill House', 1, 2, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR001-02', 'TAISAN-AR001', 'Bàn 2', 'Sảnh 1', 'Bàn 4 chỗ tại Saigon Grill House', 1, 4, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR001-03', 'TAISAN-AR001', 'Bàn 3', 'Sảnh 2', 'Bàn 6 chỗ tại Saigon Grill House', 1, 6, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR001-04', 'TAISAN-AR001', 'Bàn 4', 'Sảnh 2', 'Bàn 8 chỗ tại Saigon Grill House', 1, 8, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO thuc_don (id, nha_hang_id, ten_thuc_don, phan_loai, trang_thai, created_at, updated_at)
VALUES ('TD-AR001-01', 'TAISAN-AR001', 'Thực đơn chính Saigon Grill House', 'MAIN', 'DANG_HIEN_THI', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR001-01', 'TD-AR001-01', 'Món khai vị Saigon Grill House', 'Món 1 của Saigon Grill House', 273000.0, 'CATEGORY-1', 'DANG_BAN', '/uploads/ar001/dish-1.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR001-02', 'TD-AR001-01', 'Món chính Saigon Grill House', 'Món 2 của Saigon Grill House', 357000.0, 'CATEGORY-2', 'DANG_BAN', '/uploads/ar001/dish-2.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR001-03', 'TD-AR001-01', 'Món tráng miệng Saigon Grill House', 'Món 3 của Saigon Grill House', 441000.0, 'CATEGORY-3', 'DANG_BAN', '/uploads/ar001/dish-3.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ho_so_kinh_doanh (id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh, toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho, quan_huyen, phuong_xa, kinh_do, vi_do, trang_thai_kiem_duyet, trang_thai_hoat_dong, thoi_gian_dang_ky)
VALUES ('HOSOKD-AR002', '3816f90e-d594-4018-ab4e-0921e838121f', 'NHA_HANG', 'Hue Heritage Kitchen', 'MS-AR002', 'LP-AR002', '16.4637,107.5908', '0900000002', 'anhbid2000@gmail.com', 'Địa chỉ Hue Heritage Kitchen', 'Thừa Thiên Huế', 'Huế', 'Phú Hội', 107.5908, 16.4637, 'DA_DUYET', 'DANG_HOAT_DONG', CURRENT_TIMESTAMP);
INSERT INTO tai_san (id_tai_san, ho_so_kinh_doanh_id, mo_ta, trang_thai, gia_co_ban, is_dynamic_pricing, rating_average, review_count)
VALUES ('TAISAN-AR002', 'HOSOKD-AR002', 'Nhà hàng Ẩm thực cung đình Huế', 'SAN_SANG', 360000.0, FALSE, 0.0, 0);
INSERT INTO nha_hang (id_tai_san, ten, loai_am_thuc, gio_mo_cua, gio_dong_cua, suc_chua, co_dat_ban_truoc, co_dat_mon_truoc, diem_danh_gia_trung_binh, so_luong_danh_gia)
VALUES ('TAISAN-AR002', 'Hue Heritage Kitchen', 'Ẩm thực cung đình Huế', '10:00:00', '22:30:00', 180, TRUE, TRUE, 0.0, 0);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR002-01', 'TAISAN-AR002', 'Bàn 1', 'Sảnh 1', 'Bàn 2 chỗ tại Hue Heritage Kitchen', 1, 2, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR002-02', 'TAISAN-AR002', 'Bàn 2', 'Sảnh 1', 'Bàn 4 chỗ tại Hue Heritage Kitchen', 1, 4, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR002-03', 'TAISAN-AR002', 'Bàn 3', 'Sảnh 2', 'Bàn 6 chỗ tại Hue Heritage Kitchen', 1, 6, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR002-04', 'TAISAN-AR002', 'Bàn 4', 'Sảnh 2', 'Bàn 8 chỗ tại Hue Heritage Kitchen', 1, 8, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO thuc_don (id, nha_hang_id, ten_thuc_don, phan_loai, trang_thai, created_at, updated_at)
VALUES ('TD-AR002-01', 'TAISAN-AR002', 'Thực đơn chính Hue Heritage Kitchen', 'MAIN', 'DANG_HIEN_THI', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR002-01', 'TD-AR002-01', 'Món khai vị Hue Heritage Kitchen', 'Món 1 của Hue Heritage Kitchen', 234000.0, 'CATEGORY-1', 'DANG_BAN', '/uploads/ar002/dish-1.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR002-02', 'TD-AR002-01', 'Món chính Hue Heritage Kitchen', 'Món 2 của Hue Heritage Kitchen', 306000.0, 'CATEGORY-2', 'DANG_BAN', '/uploads/ar002/dish-2.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR002-03', 'TD-AR002-01', 'Món tráng miệng Hue Heritage Kitchen', 'Món 3 của Hue Heritage Kitchen', 378000.0, 'CATEGORY-3', 'DANG_BAN', '/uploads/ar002/dish-3.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ho_so_kinh_doanh (id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh, toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho, quan_huyen, phuong_xa, kinh_do, vi_do, trang_thai_kiem_duyet, trang_thai_hoat_dong, thoi_gian_dang_ky)
VALUES ('HOSOKD-AR003', '3816f90e-d594-4018-ab4e-0921e838121f', 'NHA_HANG', 'Ha Long Seafood Bay', 'MS-AR003', 'LP-AR003', '20.9536,107.0448', '0900000002', 'anhbid2000@gmail.com', 'Địa chỉ Ha Long Seafood Bay', 'Quảng Ninh', 'Hạ Long', 'Bãi Cháy', 107.0448, 20.9536, 'DA_DUYET', 'DANG_HOAT_DONG', CURRENT_TIMESTAMP);
INSERT INTO tai_san (id_tai_san, ho_so_kinh_doanh_id, mo_ta, trang_thai, gia_co_ban, is_dynamic_pricing, rating_average, review_count)
VALUES ('TAISAN-AR003', 'HOSOKD-AR003', 'Nhà hàng Hải sản tươi sống', 'SAN_SANG', 480000.0, FALSE, 0.0, 0);
INSERT INTO nha_hang (id_tai_san, ten, loai_am_thuc, gio_mo_cua, gio_dong_cua, suc_chua, co_dat_ban_truoc, co_dat_mon_truoc, diem_danh_gia_trung_binh, so_luong_danh_gia)
VALUES ('TAISAN-AR003', 'Ha Long Seafood Bay', 'Hải sản tươi sống', '10:00:00', '22:30:00', 180, TRUE, TRUE, 0.0, 0);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR003-01', 'TAISAN-AR003', 'Bàn 1', 'Sảnh 1', 'Bàn 2 chỗ tại Ha Long Seafood Bay', 1, 2, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR003-02', 'TAISAN-AR003', 'Bàn 2', 'Sảnh 1', 'Bàn 4 chỗ tại Ha Long Seafood Bay', 1, 4, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR003-03', 'TAISAN-AR003', 'Bàn 3', 'Sảnh 2', 'Bàn 6 chỗ tại Ha Long Seafood Bay', 1, 6, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR003-04', 'TAISAN-AR003', 'Bàn 4', 'Sảnh 2', 'Bàn 8 chỗ tại Ha Long Seafood Bay', 1, 8, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO thuc_don (id, nha_hang_id, ten_thuc_don, phan_loai, trang_thai, created_at, updated_at)
VALUES ('TD-AR003-01', 'TAISAN-AR003', 'Thực đơn chính Ha Long Seafood Bay', 'MAIN', 'DANG_HIEN_THI', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR003-01', 'TD-AR003-01', 'Món khai vị Ha Long Seafood Bay', 'Món 1 của Ha Long Seafood Bay', 312000.0, 'CATEGORY-1', 'DANG_BAN', '/uploads/ar003/dish-1.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR003-02', 'TD-AR003-01', 'Món chính Ha Long Seafood Bay', 'Món 2 của Ha Long Seafood Bay', 408000.0, 'CATEGORY-2', 'DANG_BAN', '/uploads/ar003/dish-2.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR003-03', 'TD-AR003-01', 'Món tráng miệng Ha Long Seafood Bay', 'Món 3 của Ha Long Seafood Bay', 504000.0, 'CATEGORY-3', 'DANG_BAN', '/uploads/ar003/dish-3.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ho_so_kinh_doanh (id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh, toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho, quan_huyen, phuong_xa, kinh_do, vi_do, trang_thai_kiem_duyet, trang_thai_hoat_dong, thoi_gian_dang_ky)
VALUES ('HOSOKD-AR004', '3816f90e-d594-4018-ab4e-0921e838121f', 'NHA_HANG', 'Da Nang Street Bites', 'MS-AR004', 'LP-AR004', '16.0678,108.2478', '0900000002', 'anhbid2000@gmail.com', 'Địa chỉ Da Nang Street Bites', 'Đà Nẵng', 'Sơn Trà', 'An Hải', 108.2478, 16.0678, 'DA_DUYET', 'DANG_HOAT_DONG', CURRENT_TIMESTAMP);
INSERT INTO tai_san (id_tai_san, ho_so_kinh_doanh_id, mo_ta, trang_thai, gia_co_ban, is_dynamic_pricing, rating_average, review_count)
VALUES ('TAISAN-AR004', 'HOSOKD-AR004', 'Nhà hàng Món địa phương Đà Nẵng', 'SAN_SANG', 290000.0, FALSE, 0.0, 0);
INSERT INTO nha_hang (id_tai_san, ten, loai_am_thuc, gio_mo_cua, gio_dong_cua, suc_chua, co_dat_ban_truoc, co_dat_mon_truoc, diem_danh_gia_trung_binh, so_luong_danh_gia)
VALUES ('TAISAN-AR004', 'Da Nang Street Bites', 'Món địa phương Đà Nẵng', '10:00:00', '22:30:00', 180, TRUE, TRUE, 0.0, 0);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR004-01', 'TAISAN-AR004', 'Bàn 1', 'Sảnh 1', 'Bàn 2 chỗ tại Da Nang Street Bites', 1, 2, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR004-02', 'TAISAN-AR004', 'Bàn 2', 'Sảnh 1', 'Bàn 4 chỗ tại Da Nang Street Bites', 1, 4, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR004-03', 'TAISAN-AR004', 'Bàn 3', 'Sảnh 2', 'Bàn 6 chỗ tại Da Nang Street Bites', 1, 6, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR004-04', 'TAISAN-AR004', 'Bàn 4', 'Sảnh 2', 'Bàn 8 chỗ tại Da Nang Street Bites', 1, 8, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO thuc_don (id, nha_hang_id, ten_thuc_don, phan_loai, trang_thai, created_at, updated_at)
VALUES ('TD-AR004-01', 'TAISAN-AR004', 'Thực đơn chính Da Nang Street Bites', 'MAIN', 'DANG_HIEN_THI', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR004-01', 'TD-AR004-01', 'Món khai vị Da Nang Street Bites', 'Món 1 của Da Nang Street Bites', 188500.0, 'CATEGORY-1', 'DANG_BAN', '/uploads/ar004/dish-1.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR004-02', 'TD-AR004-01', 'Món chính Da Nang Street Bites', 'Món 2 của Da Nang Street Bites', 246500.0, 'CATEGORY-2', 'DANG_BAN', '/uploads/ar004/dish-2.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR004-03', 'TD-AR004-01', 'Món tráng miệng Da Nang Street Bites', 'Món 3 của Da Nang Street Bites', 304500.0, 'CATEGORY-3', 'DANG_BAN', '/uploads/ar004/dish-3.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ho_so_kinh_doanh (id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh, toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho, quan_huyen, phuong_xa, kinh_do, vi_do, trang_thai_kiem_duyet, trang_thai_hoat_dong, thoi_gian_dang_ky)
VALUES ('HOSOKD-AR005', '3816f90e-d594-4018-ab4e-0921e838121f', 'NHA_HANG', 'Phu Quoc Sunset Dining', 'MS-AR005', 'LP-AR005', '10.2299,103.9612', '0900000002', 'anhbid2000@gmail.com', 'Địa chỉ Phu Quoc Sunset Dining', 'Kiên Giang', 'Phú Quốc', 'Dương Đông', 103.9612, 10.2299, 'DA_DUYET', 'DANG_HOAT_DONG', CURRENT_TIMESTAMP);
INSERT INTO tai_san (id_tai_san, ho_so_kinh_doanh_id, mo_ta, trang_thai, gia_co_ban, is_dynamic_pricing, rating_average, review_count)
VALUES ('TAISAN-AR005', 'HOSOKD-AR005', 'Nhà hàng Fusion hải sản nhiệt đới', 'SAN_SANG', 520000.0, FALSE, 0.0, 0);
INSERT INTO nha_hang (id_tai_san, ten, loai_am_thuc, gio_mo_cua, gio_dong_cua, suc_chua, co_dat_ban_truoc, co_dat_mon_truoc, diem_danh_gia_trung_binh, so_luong_danh_gia)
VALUES ('TAISAN-AR005', 'Phu Quoc Sunset Dining', 'Fusion hải sản nhiệt đới', '10:00:00', '22:30:00', 180, TRUE, TRUE, 0.0, 0);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR005-01', 'TAISAN-AR005', 'Bàn 1', 'Sảnh 1', 'Bàn 2 chỗ tại Phu Quoc Sunset Dining', 1, 2, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR005-02', 'TAISAN-AR005', 'Bàn 2', 'Sảnh 1', 'Bàn 4 chỗ tại Phu Quoc Sunset Dining', 1, 4, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR005-03', 'TAISAN-AR005', 'Bàn 3', 'Sảnh 2', 'Bàn 6 chỗ tại Phu Quoc Sunset Dining', 1, 6, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted, created_at, updated_at)
VALUES ('BAN-AR005-04', 'TAISAN-AR005', 'Bàn 4', 'Sảnh 2', 'Bàn 8 chỗ tại Phu Quoc Sunset Dining', 1, 8, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO thuc_don (id, nha_hang_id, ten_thuc_don, phan_loai, trang_thai, created_at, updated_at)
VALUES ('TD-AR005-01', 'TAISAN-AR005', 'Thực đơn chính Phu Quoc Sunset Dining', 'MAIN', 'DANG_HIEN_THI', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR005-01', 'TD-AR005-01', 'Món khai vị Phu Quoc Sunset Dining', 'Món 1 của Phu Quoc Sunset Dining', 338000.0, 'CATEGORY-1', 'DANG_BAN', '/uploads/ar005/dish-1.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR005-02', 'TD-AR005-01', 'Món chính Phu Quoc Sunset Dining', 'Món 2 của Phu Quoc Sunset Dining', 442000.0, 'CATEGORY-2', 'DANG_BAN', '/uploads/ar005/dish-2.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted, created_at, updated_at)
VALUES ('MON-AR005-03', 'TD-AR005-01', 'Món tráng miệng Phu Quoc Sunset Dining', 'Món 3 của Phu Quoc Sunset Dining', 546000.0, 'CATEGORY-3', 'DANG_BAN', '/uploads/ar005/dish-3.jpg', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('BOOKING-700', 'MAH700', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH001', CURRENT_TIMESTAMP - INTERVAL '12' DAY, 1800000.0, 150000.0, 1650000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Đơn hotel mở rộng Aurora Hotel Saigon', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '12' DAY, CURRENT_TIMESTAMP - INTERVAL '12' DAY);
INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien)
VALUES ('BOOKING-700', CURRENT_DATE - INTERVAL '11' DAY, CURRENT_DATE - INTERVAL '10' DAY, 1, 3, '14:00:00');
INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien)
VALUES ('DKCT-700', 'BOOKING-700', 'PHONG-AH001-01', 'Phòng Standard Aurora Hotel Saigon', 1, 1800000.0, 1, 1800000.0);
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-700', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH001', 'KHACH_SAN', 'BOOKING-700', 5, 'Review mở rộng Aurora Hotel Saigon - trải nghiệm mức 5 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '9' DAY, CURRENT_TIMESTAMP - INTERVAL '9' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-700', 'REVIEW-700', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi cảm ơn từ đối tác cho review REVIEW-700', CURRENT_TIMESTAMP - INTERVAL '8' DAY, CURRENT_TIMESTAMP - INTERVAL '8' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('BOOKING-701', 'MAH701', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH001', CURRENT_TIMESTAMP - INTERVAL '13' DAY, 1800000.0, 150000.0, 1650000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Đơn hotel mở rộng Aurora Hotel Saigon', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '13' DAY, CURRENT_TIMESTAMP - INTERVAL '13' DAY);
INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien)
VALUES ('BOOKING-701', CURRENT_DATE - INTERVAL '12' DAY, CURRENT_DATE - INTERVAL '11' DAY, 1, 4, '14:00:00');
INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien)
VALUES ('DKCT-701', 'BOOKING-701', 'PHONG-AH001-01', 'Phòng Standard Aurora Hotel Saigon', 1, 1800000.0, 1, 1800000.0);
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-701', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH001', 'KHACH_SAN', 'BOOKING-701', 3, 'Review mở rộng Aurora Hotel Saigon - trải nghiệm mức 3 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP - INTERVAL '10' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-701', 'REVIEW-701', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi cảm ơn từ đối tác cho review REVIEW-701', CURRENT_TIMESTAMP - INTERVAL '9' DAY, CURRENT_TIMESTAMP - INTERVAL '9' DAY);
INSERT INTO complaint_khieu_nai (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, tieu_de, noi_dung_tom_tat, muc_do, trang_thai, created_at, updated_at)
VALUES ('COMPLAINT-700', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH001', 'KHACH_SAN', 'BOOKING-701', 'Khiếu nại mở rộng Aurora Hotel Saigon', 'Nội dung khiếu nại mở rộng cho Aurora Hotel Saigon', 'BINH_THUONG', 'DANG_XU_LY', CURRENT_TIMESTAMP - INTERVAL '6' DAY, CURRENT_TIMESTAMP - INTERVAL '6' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-700', 'COMPLAINT-700', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách mô tả vấn đề - Aurora Hotel Saigon', CURRENT_TIMESTAMP - INTERVAL '5' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-701', 'COMPLAINT-700', '3816f90e-d594-4018-ab4e-0921e838121f', 'DOI_TAC', 'Đối tác phản hồi lần 1 - Aurora Hotel Saigon', CURRENT_TIMESTAMP - INTERVAL '4' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-702', 'COMPLAINT-700', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách bổ sung bằng chứng - Aurora Hotel Saigon', CURRENT_TIMESTAMP - INTERVAL '3' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('BOOKING-702', 'MAH702', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH002', CURRENT_TIMESTAMP - INTERVAL '13' DAY, 1800000.0, 150000.0, 1650000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Đơn hotel mở rộng Mekong Riverside Can Tho', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '13' DAY, CURRENT_TIMESTAMP - INTERVAL '13' DAY);
INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien)
VALUES ('BOOKING-702', CURRENT_DATE - INTERVAL '12' DAY, CURRENT_DATE - INTERVAL '11' DAY, 1, 3, '14:00:00');
INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien)
VALUES ('DKCT-702', 'BOOKING-702', 'PHONG-AH002-01', 'Phòng Standard Mekong Riverside Can Tho', 1, 1800000.0, 1, 1800000.0);
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-702', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH002', 'KHACH_SAN', 'BOOKING-702', 5, 'Review mở rộng Mekong Riverside Can Tho - trải nghiệm mức 5 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP - INTERVAL '10' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-702', 'REVIEW-702', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi cảm ơn từ đối tác cho review REVIEW-702', CURRENT_TIMESTAMP - INTERVAL '9' DAY, CURRENT_TIMESTAMP - INTERVAL '9' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('BOOKING-703', 'MAH703', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH002', CURRENT_TIMESTAMP - INTERVAL '14' DAY, 1800000.0, 150000.0, 1650000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Đơn hotel mở rộng Mekong Riverside Can Tho', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '14' DAY, CURRENT_TIMESTAMP - INTERVAL '14' DAY);
INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien)
VALUES ('BOOKING-703', CURRENT_DATE - INTERVAL '13' DAY, CURRENT_DATE - INTERVAL '12' DAY, 1, 4, '14:00:00');
INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien)
VALUES ('DKCT-703', 'BOOKING-703', 'PHONG-AH002-01', 'Phòng Standard Mekong Riverside Can Tho', 1, 1800000.0, 1, 1800000.0);
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-703', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH002', 'KHACH_SAN', 'BOOKING-703', 3, 'Review mở rộng Mekong Riverside Can Tho - trải nghiệm mức 3 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '11' DAY, CURRENT_TIMESTAMP - INTERVAL '11' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-703', 'REVIEW-703', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi cảm ơn từ đối tác cho review REVIEW-703', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP - INTERVAL '10' DAY);
INSERT INTO complaint_khieu_nai (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, tieu_de, noi_dung_tom_tat, muc_do, trang_thai, created_at, updated_at)
VALUES ('COMPLAINT-701', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH002', 'KHACH_SAN', 'BOOKING-703', 'Khiếu nại mở rộng Mekong Riverside Can Tho', 'Nội dung khiếu nại mở rộng cho Mekong Riverside Can Tho', 'BINH_THUONG', 'DANG_XU_LY', CURRENT_TIMESTAMP - INTERVAL '7' DAY, CURRENT_TIMESTAMP - INTERVAL '7' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-703', 'COMPLAINT-701', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách mô tả vấn đề - Mekong Riverside Can Tho', CURRENT_TIMESTAMP - INTERVAL '6' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-704', 'COMPLAINT-701', '3816f90e-d594-4018-ab4e-0921e838121f', 'DOI_TAC', 'Đối tác phản hồi lần 1 - Mekong Riverside Can Tho', CURRENT_TIMESTAMP - INTERVAL '5' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-705', 'COMPLAINT-701', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách bổ sung bằng chứng - Mekong Riverside Can Tho', CURRENT_TIMESTAMP - INTERVAL '4' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('BOOKING-704', 'MAH704', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH003', CURRENT_TIMESTAMP - INTERVAL '14' DAY, 1800000.0, 150000.0, 1650000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Đơn hotel mở rộng Cloudy Peaks Da Lat', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '14' DAY, CURRENT_TIMESTAMP - INTERVAL '14' DAY);
INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien)
VALUES ('BOOKING-704', CURRENT_DATE - INTERVAL '13' DAY, CURRENT_DATE - INTERVAL '12' DAY, 1, 3, '14:00:00');
INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien)
VALUES ('DKCT-704', 'BOOKING-704', 'PHONG-AH003-01', 'Phòng Standard Cloudy Peaks Da Lat', 1, 1800000.0, 1, 1800000.0);
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-704', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH003', 'KHACH_SAN', 'BOOKING-704', 5, 'Review mở rộng Cloudy Peaks Da Lat - trải nghiệm mức 5 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '11' DAY, CURRENT_TIMESTAMP - INTERVAL '11' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-704', 'REVIEW-704', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi cảm ơn từ đối tác cho review REVIEW-704', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP - INTERVAL '10' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('BOOKING-705', 'MAH705', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH003', CURRENT_TIMESTAMP - INTERVAL '15' DAY, 1800000.0, 150000.0, 1650000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Đơn hotel mở rộng Cloudy Peaks Da Lat', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '15' DAY, CURRENT_TIMESTAMP - INTERVAL '15' DAY);
INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien)
VALUES ('BOOKING-705', CURRENT_DATE - INTERVAL '14' DAY, CURRENT_DATE - INTERVAL '13' DAY, 1, 4, '14:00:00');
INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien)
VALUES ('DKCT-705', 'BOOKING-705', 'PHONG-AH003-01', 'Phòng Standard Cloudy Peaks Da Lat', 1, 1800000.0, 1, 1800000.0);
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-705', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH003', 'KHACH_SAN', 'BOOKING-705', 3, 'Review mở rộng Cloudy Peaks Da Lat - trải nghiệm mức 3 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '12' DAY, CURRENT_TIMESTAMP - INTERVAL '12' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-705', 'REVIEW-705', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi cảm ơn từ đối tác cho review REVIEW-705', CURRENT_TIMESTAMP - INTERVAL '11' DAY, CURRENT_TIMESTAMP - INTERVAL '11' DAY);
INSERT INTO complaint_khieu_nai (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, tieu_de, noi_dung_tom_tat, muc_do, trang_thai, created_at, updated_at)
VALUES ('COMPLAINT-702', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH003', 'KHACH_SAN', 'BOOKING-705', 'Khiếu nại mở rộng Cloudy Peaks Da Lat', 'Nội dung khiếu nại mở rộng cho Cloudy Peaks Da Lat', 'BINH_THUONG', 'DANG_XU_LY', CURRENT_TIMESTAMP - INTERVAL '8' DAY, CURRENT_TIMESTAMP - INTERVAL '8' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-706', 'COMPLAINT-702', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách mô tả vấn đề - Cloudy Peaks Da Lat', CURRENT_TIMESTAMP - INTERVAL '7' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-707', 'COMPLAINT-702', '3816f90e-d594-4018-ab4e-0921e838121f', 'DOI_TAC', 'Đối tác phản hồi lần 1 - Cloudy Peaks Da Lat', CURRENT_TIMESTAMP - INTERVAL '6' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-708', 'COMPLAINT-702', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách bổ sung bằng chứng - Cloudy Peaks Da Lat', CURRENT_TIMESTAMP - INTERVAL '5' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('BOOKING-706', 'MAH706', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH004', CURRENT_TIMESTAMP - INTERVAL '15' DAY, 1800000.0, 150000.0, 1650000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Đơn hotel mở rộng Hanoi Old Quarter Nest', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '15' DAY, CURRENT_TIMESTAMP - INTERVAL '15' DAY);
INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien)
VALUES ('BOOKING-706', CURRENT_DATE - INTERVAL '14' DAY, CURRENT_DATE - INTERVAL '13' DAY, 1, 3, '14:00:00');
INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien)
VALUES ('DKCT-706', 'BOOKING-706', 'PHONG-AH004-01', 'Phòng Standard Hanoi Old Quarter Nest', 1, 1800000.0, 1, 1800000.0);
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-706', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH004', 'KHACH_SAN', 'BOOKING-706', 5, 'Review mở rộng Hanoi Old Quarter Nest - trải nghiệm mức 5 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '12' DAY, CURRENT_TIMESTAMP - INTERVAL '12' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-706', 'REVIEW-706', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi cảm ơn từ đối tác cho review REVIEW-706', CURRENT_TIMESTAMP - INTERVAL '11' DAY, CURRENT_TIMESTAMP - INTERVAL '11' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('BOOKING-707', 'MAH707', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH004', CURRENT_TIMESTAMP - INTERVAL '16' DAY, 1800000.0, 150000.0, 1650000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Đơn hotel mở rộng Hanoi Old Quarter Nest', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '16' DAY, CURRENT_TIMESTAMP - INTERVAL '16' DAY);
INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien)
VALUES ('BOOKING-707', CURRENT_DATE - INTERVAL '15' DAY, CURRENT_DATE - INTERVAL '14' DAY, 1, 4, '14:00:00');
INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien)
VALUES ('DKCT-707', 'BOOKING-707', 'PHONG-AH004-01', 'Phòng Standard Hanoi Old Quarter Nest', 1, 1800000.0, 1, 1800000.0);
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-707', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH004', 'KHACH_SAN', 'BOOKING-707', 3, 'Review mở rộng Hanoi Old Quarter Nest - trải nghiệm mức 3 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '13' DAY, CURRENT_TIMESTAMP - INTERVAL '13' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-707', 'REVIEW-707', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi cảm ơn từ đối tác cho review REVIEW-707', CURRENT_TIMESTAMP - INTERVAL '12' DAY, CURRENT_TIMESTAMP - INTERVAL '12' DAY);
INSERT INTO complaint_khieu_nai (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, tieu_de, noi_dung_tom_tat, muc_do, trang_thai, created_at, updated_at)
VALUES ('COMPLAINT-703', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH004', 'KHACH_SAN', 'BOOKING-707', 'Khiếu nại mở rộng Hanoi Old Quarter Nest', 'Nội dung khiếu nại mở rộng cho Hanoi Old Quarter Nest', 'BINH_THUONG', 'DANG_XU_LY', CURRENT_TIMESTAMP - INTERVAL '9' DAY, CURRENT_TIMESTAMP - INTERVAL '9' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-709', 'COMPLAINT-703', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách mô tả vấn đề - Hanoi Old Quarter Nest', CURRENT_TIMESTAMP - INTERVAL '8' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-710', 'COMPLAINT-703', '3816f90e-d594-4018-ab4e-0921e838121f', 'DOI_TAC', 'Đối tác phản hồi lần 1 - Hanoi Old Quarter Nest', CURRENT_TIMESTAMP - INTERVAL '7' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-711', 'COMPLAINT-703', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách bổ sung bằng chứng - Hanoi Old Quarter Nest', CURRENT_TIMESTAMP - INTERVAL '6' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('BOOKING-708', 'MAH708', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH005', CURRENT_TIMESTAMP - INTERVAL '16' DAY, 1800000.0, 150000.0, 1650000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Đơn hotel mở rộng Mui Ne Ocean Breeze', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '16' DAY, CURRENT_TIMESTAMP - INTERVAL '16' DAY);
INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien)
VALUES ('BOOKING-708', CURRENT_DATE - INTERVAL '15' DAY, CURRENT_DATE - INTERVAL '14' DAY, 1, 3, '14:00:00');
INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien)
VALUES ('DKCT-708', 'BOOKING-708', 'PHONG-AH005-01', 'Phòng Standard Mui Ne Ocean Breeze', 1, 1800000.0, 1, 1800000.0);
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-708', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH005', 'KHACH_SAN', 'BOOKING-708', 5, 'Review mở rộng Mui Ne Ocean Breeze - trải nghiệm mức 5 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '13' DAY, CURRENT_TIMESTAMP - INTERVAL '13' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-708', 'REVIEW-708', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi cảm ơn từ đối tác cho review REVIEW-708', CURRENT_TIMESTAMP - INTERVAL '12' DAY, CURRENT_TIMESTAMP - INTERVAL '12' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('BOOKING-709', 'MAH709', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH005', CURRENT_TIMESTAMP - INTERVAL '17' DAY, 1800000.0, 150000.0, 1650000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Đơn hotel mở rộng Mui Ne Ocean Breeze', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '17' DAY, CURRENT_TIMESTAMP - INTERVAL '17' DAY);
INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien)
VALUES ('BOOKING-709', CURRENT_DATE - INTERVAL '16' DAY, CURRENT_DATE - INTERVAL '15' DAY, 1, 4, '14:00:00');
INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien)
VALUES ('DKCT-709', 'BOOKING-709', 'PHONG-AH005-01', 'Phòng Standard Mui Ne Ocean Breeze', 1, 1800000.0, 1, 1800000.0);
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-709', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH005', 'KHACH_SAN', 'BOOKING-709', 3, 'Review mở rộng Mui Ne Ocean Breeze - trải nghiệm mức 3 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '14' DAY, CURRENT_TIMESTAMP - INTERVAL '14' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-709', 'REVIEW-709', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi cảm ơn từ đối tác cho review REVIEW-709', CURRENT_TIMESTAMP - INTERVAL '13' DAY, CURRENT_TIMESTAMP - INTERVAL '13' DAY);
INSERT INTO complaint_khieu_nai (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, tieu_de, noi_dung_tom_tat, muc_do, trang_thai, created_at, updated_at)
VALUES ('COMPLAINT-704', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH005', 'KHACH_SAN', 'BOOKING-709', 'Khiếu nại mở rộng Mui Ne Ocean Breeze', 'Nội dung khiếu nại mở rộng cho Mui Ne Ocean Breeze', 'BINH_THUONG', 'DANG_XU_LY', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP - INTERVAL '10' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-712', 'COMPLAINT-704', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách mô tả vấn đề - Mui Ne Ocean Breeze', CURRENT_TIMESTAMP - INTERVAL '9' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-713', 'COMPLAINT-704', '3816f90e-d594-4018-ab4e-0921e838121f', 'DOI_TAC', 'Đối tác phản hồi lần 1 - Mui Ne Ocean Breeze', CURRENT_TIMESTAMP - INTERVAL '8' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-714', 'COMPLAINT-704', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách bổ sung bằng chứng - Mui Ne Ocean Breeze', CURRENT_TIMESTAMP - INTERVAL '7' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('RES-710', 'NHA710', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR001', CURRENT_TIMESTAMP - INTERVAL '10' DAY, 650000.0, 50000.0, 600000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Reservation mở rộng Saigon Grill House', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP - INTERVAL '10' DAY);
INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc)
VALUES ('RES-710', CURRENT_TIMESTAMP - INTERVAL '10' DAY + INTERVAL '4' HOUR, CURRENT_TIMESTAMP - INTERVAL '10' DAY + INTERVAL '2' HOUR, 4, 120000.0, TRUE);
INSERT INTO don_nha_hang_ban (id, don_nha_hang_id, ban_id)
VALUES ('DTHB-710', 'RES-710', 'BAN-AR001-02');
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-710', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR001', 'NHA_HANG', 'RES-710', 4, 'Review mở rộng Saigon Grill House - chất lượng 4 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '8' DAY, CURRENT_TIMESTAMP - INTERVAL '8' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-710', 'REVIEW-710', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi từ đối tác cho review REVIEW-710', CURRENT_TIMESTAMP - INTERVAL '7' DAY, CURRENT_TIMESTAMP - INTERVAL '7' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('RES-711', 'NHA711', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR001', CURRENT_TIMESTAMP - INTERVAL '11' DAY, 650000.0, 50000.0, 600000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Reservation mở rộng Saigon Grill House', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '11' DAY, CURRENT_TIMESTAMP - INTERVAL '11' DAY);
INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc)
VALUES ('RES-711', CURRENT_TIMESTAMP - INTERVAL '11' DAY + INTERVAL '4' HOUR, CURRENT_TIMESTAMP - INTERVAL '11' DAY + INTERVAL '2' HOUR, 5, 120000.0, TRUE);
INSERT INTO don_nha_hang_ban (id, don_nha_hang_id, ban_id)
VALUES ('DTHB-711', 'RES-711', 'BAN-AR001-02');
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-711', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR001', 'NHA_HANG', 'RES-711', 5, 'Review mở rộng Saigon Grill House - chất lượng 5 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '9' DAY, CURRENT_TIMESTAMP - INTERVAL '9' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-711', 'REVIEW-711', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi từ đối tác cho review REVIEW-711', CURRENT_TIMESTAMP - INTERVAL '8' DAY, CURRENT_TIMESTAMP - INTERVAL '8' DAY);
INSERT INTO complaint_khieu_nai (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, tieu_de, noi_dung_tom_tat, muc_do, trang_thai, created_at, updated_at)
VALUES ('COMPLAINT-705', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR001', 'NHA_HANG', 'RES-711', 'Khiếu nại phục vụ Saigon Grill House', 'Khách phản ánh tốc độ phục vụ tại Saigon Grill House', 'NGHIEM_TRONG', 'CHO_PHAN_HOI', CURRENT_TIMESTAMP - INTERVAL '5' DAY, CURRENT_TIMESTAMP - INTERVAL '5' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-715', 'COMPLAINT-705', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách phản ánh chi tiết - Saigon Grill House', CURRENT_TIMESTAMP - INTERVAL '4' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-716', 'COMPLAINT-705', '3816f90e-d594-4018-ab4e-0921e838121f', 'DOI_TAC', 'Đối tác xin lỗi và đề xuất hoàn tiền - Saigon Grill House', CURRENT_TIMESTAMP - INTERVAL '3' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('RES-712', 'NHA712', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR002', CURRENT_TIMESTAMP - INTERVAL '11' DAY, 650000.0, 50000.0, 600000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Reservation mở rộng Hue Heritage Kitchen', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '11' DAY, CURRENT_TIMESTAMP - INTERVAL '11' DAY);
INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc)
VALUES ('RES-712', CURRENT_TIMESTAMP - INTERVAL '11' DAY + INTERVAL '4' HOUR, CURRENT_TIMESTAMP - INTERVAL '11' DAY + INTERVAL '2' HOUR, 4, 120000.0, TRUE);
INSERT INTO don_nha_hang_ban (id, don_nha_hang_id, ban_id)
VALUES ('DTHB-712', 'RES-712', 'BAN-AR002-02');
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-712', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR002', 'NHA_HANG', 'RES-712', 4, 'Review mở rộng Hue Heritage Kitchen - chất lượng 4 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '9' DAY, CURRENT_TIMESTAMP - INTERVAL '9' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-712', 'REVIEW-712', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi từ đối tác cho review REVIEW-712', CURRENT_TIMESTAMP - INTERVAL '8' DAY, CURRENT_TIMESTAMP - INTERVAL '8' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('RES-713', 'NHA713', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR002', CURRENT_TIMESTAMP - INTERVAL '12' DAY, 650000.0, 50000.0, 600000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Reservation mở rộng Hue Heritage Kitchen', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '12' DAY, CURRENT_TIMESTAMP - INTERVAL '12' DAY);
INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc)
VALUES ('RES-713', CURRENT_TIMESTAMP - INTERVAL '12' DAY + INTERVAL '4' HOUR, CURRENT_TIMESTAMP - INTERVAL '12' DAY + INTERVAL '2' HOUR, 5, 120000.0, TRUE);
INSERT INTO don_nha_hang_ban (id, don_nha_hang_id, ban_id)
VALUES ('DTHB-713', 'RES-713', 'BAN-AR002-02');
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-713', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR002', 'NHA_HANG', 'RES-713', 5, 'Review mở rộng Hue Heritage Kitchen - chất lượng 5 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP - INTERVAL '10' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-713', 'REVIEW-713', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi từ đối tác cho review REVIEW-713', CURRENT_TIMESTAMP - INTERVAL '9' DAY, CURRENT_TIMESTAMP - INTERVAL '9' DAY);
INSERT INTO complaint_khieu_nai (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, tieu_de, noi_dung_tom_tat, muc_do, trang_thai, created_at, updated_at)
VALUES ('COMPLAINT-706', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR002', 'NHA_HANG', 'RES-713', 'Khiếu nại phục vụ Hue Heritage Kitchen', 'Khách phản ánh tốc độ phục vụ tại Hue Heritage Kitchen', 'NGHIEM_TRONG', 'CHO_PHAN_HOI', CURRENT_TIMESTAMP - INTERVAL '6' DAY, CURRENT_TIMESTAMP - INTERVAL '6' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-717', 'COMPLAINT-706', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách phản ánh chi tiết - Hue Heritage Kitchen', CURRENT_TIMESTAMP - INTERVAL '5' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-718', 'COMPLAINT-706', '3816f90e-d594-4018-ab4e-0921e838121f', 'DOI_TAC', 'Đối tác xin lỗi và đề xuất hoàn tiền - Hue Heritage Kitchen', CURRENT_TIMESTAMP - INTERVAL '4' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('RES-714', 'NHA714', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR003', CURRENT_TIMESTAMP - INTERVAL '12' DAY, 650000.0, 50000.0, 600000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Reservation mở rộng Ha Long Seafood Bay', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '12' DAY, CURRENT_TIMESTAMP - INTERVAL '12' DAY);
INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc)
VALUES ('RES-714', CURRENT_TIMESTAMP - INTERVAL '12' DAY + INTERVAL '4' HOUR, CURRENT_TIMESTAMP - INTERVAL '12' DAY + INTERVAL '2' HOUR, 4, 120000.0, TRUE);
INSERT INTO don_nha_hang_ban (id, don_nha_hang_id, ban_id)
VALUES ('DTHB-714', 'RES-714', 'BAN-AR003-02');
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-714', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR003', 'NHA_HANG', 'RES-714', 4, 'Review mở rộng Ha Long Seafood Bay - chất lượng 4 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP - INTERVAL '10' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-714', 'REVIEW-714', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi từ đối tác cho review REVIEW-714', CURRENT_TIMESTAMP - INTERVAL '9' DAY, CURRENT_TIMESTAMP - INTERVAL '9' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('RES-715', 'NHA715', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR003', CURRENT_TIMESTAMP - INTERVAL '13' DAY, 650000.0, 50000.0, 600000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Reservation mở rộng Ha Long Seafood Bay', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '13' DAY, CURRENT_TIMESTAMP - INTERVAL '13' DAY);
INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc)
VALUES ('RES-715', CURRENT_TIMESTAMP - INTERVAL '13' DAY + INTERVAL '4' HOUR, CURRENT_TIMESTAMP - INTERVAL '13' DAY + INTERVAL '2' HOUR, 5, 120000.0, TRUE);
INSERT INTO don_nha_hang_ban (id, don_nha_hang_id, ban_id)
VALUES ('DTHB-715', 'RES-715', 'BAN-AR003-02');
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-715', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR003', 'NHA_HANG', 'RES-715', 5, 'Review mở rộng Ha Long Seafood Bay - chất lượng 5 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '11' DAY, CURRENT_TIMESTAMP - INTERVAL '11' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-715', 'REVIEW-715', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi từ đối tác cho review REVIEW-715', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP - INTERVAL '10' DAY);
INSERT INTO complaint_khieu_nai (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, tieu_de, noi_dung_tom_tat, muc_do, trang_thai, created_at, updated_at)
VALUES ('COMPLAINT-707', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR003', 'NHA_HANG', 'RES-715', 'Khiếu nại phục vụ Ha Long Seafood Bay', 'Khách phản ánh tốc độ phục vụ tại Ha Long Seafood Bay', 'NGHIEM_TRONG', 'CHO_PHAN_HOI', CURRENT_TIMESTAMP - INTERVAL '7' DAY, CURRENT_TIMESTAMP - INTERVAL '7' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-719', 'COMPLAINT-707', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách phản ánh chi tiết - Ha Long Seafood Bay', CURRENT_TIMESTAMP - INTERVAL '6' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-720', 'COMPLAINT-707', '3816f90e-d594-4018-ab4e-0921e838121f', 'DOI_TAC', 'Đối tác xin lỗi và đề xuất hoàn tiền - Ha Long Seafood Bay', CURRENT_TIMESTAMP - INTERVAL '5' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('RES-716', 'NHA716', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR004', CURRENT_TIMESTAMP - INTERVAL '13' DAY, 650000.0, 50000.0, 600000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Reservation mở rộng Da Nang Street Bites', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '13' DAY, CURRENT_TIMESTAMP - INTERVAL '13' DAY);
INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc)
VALUES ('RES-716', CURRENT_TIMESTAMP - INTERVAL '13' DAY + INTERVAL '4' HOUR, CURRENT_TIMESTAMP - INTERVAL '13' DAY + INTERVAL '2' HOUR, 4, 120000.0, TRUE);
INSERT INTO don_nha_hang_ban (id, don_nha_hang_id, ban_id)
VALUES ('DTHB-716', 'RES-716', 'BAN-AR004-02');
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-716', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR004', 'NHA_HANG', 'RES-716', 4, 'Review mở rộng Da Nang Street Bites - chất lượng 4 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '11' DAY, CURRENT_TIMESTAMP - INTERVAL '11' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-716', 'REVIEW-716', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi từ đối tác cho review REVIEW-716', CURRENT_TIMESTAMP - INTERVAL '10' DAY, CURRENT_TIMESTAMP - INTERVAL '10' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('RES-717', 'NHA717', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR004', CURRENT_TIMESTAMP - INTERVAL '14' DAY, 650000.0, 50000.0, 600000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Reservation mở rộng Da Nang Street Bites', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '14' DAY, CURRENT_TIMESTAMP - INTERVAL '14' DAY);
INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc)
VALUES ('RES-717', CURRENT_TIMESTAMP - INTERVAL '14' DAY + INTERVAL '4' HOUR, CURRENT_TIMESTAMP - INTERVAL '14' DAY + INTERVAL '2' HOUR, 5, 120000.0, TRUE);
INSERT INTO don_nha_hang_ban (id, don_nha_hang_id, ban_id)
VALUES ('DTHB-717', 'RES-717', 'BAN-AR004-02');
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-717', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR004', 'NHA_HANG', 'RES-717', 5, 'Review mở rộng Da Nang Street Bites - chất lượng 5 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '12' DAY, CURRENT_TIMESTAMP - INTERVAL '12' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-717', 'REVIEW-717', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi từ đối tác cho review REVIEW-717', CURRENT_TIMESTAMP - INTERVAL '11' DAY, CURRENT_TIMESTAMP - INTERVAL '11' DAY);
INSERT INTO complaint_khieu_nai (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, tieu_de, noi_dung_tom_tat, muc_do, trang_thai, created_at, updated_at)
VALUES ('COMPLAINT-708', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR004', 'NHA_HANG', 'RES-717', 'Khiếu nại phục vụ Da Nang Street Bites', 'Khách phản ánh tốc độ phục vụ tại Da Nang Street Bites', 'NGHIEM_TRONG', 'CHO_PHAN_HOI', CURRENT_TIMESTAMP - INTERVAL '8' DAY, CURRENT_TIMESTAMP - INTERVAL '8' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-721', 'COMPLAINT-708', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách phản ánh chi tiết - Da Nang Street Bites', CURRENT_TIMESTAMP - INTERVAL '7' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-722', 'COMPLAINT-708', '3816f90e-d594-4018-ab4e-0921e838121f', 'DOI_TAC', 'Đối tác xin lỗi và đề xuất hoàn tiền - Da Nang Street Bites', CURRENT_TIMESTAMP - INTERVAL '6' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('RES-718', 'NHA718', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR005', CURRENT_TIMESTAMP - INTERVAL '14' DAY, 650000.0, 50000.0, 600000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Reservation mở rộng Phu Quoc Sunset Dining', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '14' DAY, CURRENT_TIMESTAMP - INTERVAL '14' DAY);
INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc)
VALUES ('RES-718', CURRENT_TIMESTAMP - INTERVAL '14' DAY + INTERVAL '4' HOUR, CURRENT_TIMESTAMP - INTERVAL '14' DAY + INTERVAL '2' HOUR, 4, 120000.0, TRUE);
INSERT INTO don_nha_hang_ban (id, don_nha_hang_id, ban_id)
VALUES ('DTHB-718', 'RES-718', 'BAN-AR005-02');
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-718', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR005', 'NHA_HANG', 'RES-718', 4, 'Review mở rộng Phu Quoc Sunset Dining - chất lượng 4 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '12' DAY, CURRENT_TIMESTAMP - INTERVAL '12' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-718', 'REVIEW-718', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi từ đối tác cho review REVIEW-718', CURRENT_TIMESTAMP - INTERVAL '11' DAY, CURRENT_TIMESTAMP - INTERVAL '11' DAY);
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('RES-719', 'NHA719', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR005', CURRENT_TIMESTAMP - INTERVAL '15' DAY, 650000.0, 50000.0, 600000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@', 'Reservation mở rộng Phu Quoc Sunset Dining', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '15' DAY, CURRENT_TIMESTAMP - INTERVAL '15' DAY);
INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc)
VALUES ('RES-719', CURRENT_TIMESTAMP - INTERVAL '15' DAY + INTERVAL '4' HOUR, CURRENT_TIMESTAMP - INTERVAL '15' DAY + INTERVAL '2' HOUR, 5, 120000.0, TRUE);
INSERT INTO don_nha_hang_ban (id, don_nha_hang_id, ban_id)
VALUES ('DTHB-719', 'RES-719', 'BAN-AR005-02');
INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, so_sao, noi_dung, trang_thai, created_at, updated_at)
VALUES ('REVIEW-719', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR005', 'NHA_HANG', 'RES-719', 5, 'Review mở rộng Phu Quoc Sunset Dining - chất lượng 5 sao', 'DA_HIEN_THI', CURRENT_TIMESTAMP - INTERVAL '13' DAY, CURRENT_TIMESTAMP - INTERVAL '13' DAY);
INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung, created_at, updated_at)
VALUES ('REPLY-719', 'REVIEW-719', '3816f90e-d594-4018-ab4e-0921e838121f', 'Phản hồi từ đối tác cho review REVIEW-719', CURRENT_TIMESTAMP - INTERVAL '12' DAY, CURRENT_TIMESTAMP - INTERVAL '12' DAY);
INSERT INTO complaint_khieu_nai (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, tieu_de, noi_dung_tom_tat, muc_do, trang_thai, created_at, updated_at)
VALUES ('COMPLAINT-709', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR005', 'NHA_HANG', 'RES-719', 'Khiếu nại phục vụ Phu Quoc Sunset Dining', 'Khách phản ánh tốc độ phục vụ tại Phu Quoc Sunset Dining', 'NGHIEM_TRONG', 'CHO_PHAN_HOI', CURRENT_TIMESTAMP - INTERVAL '9' DAY, CURRENT_TIMESTAMP - INTERVAL '9' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-723', 'COMPLAINT-709', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'KHACH_HANG', 'Khách phản ánh chi tiết - Phu Quoc Sunset Dining', CURRENT_TIMESTAMP - INTERVAL '8' DAY);
INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at)
VALUES ('MSG-724', 'COMPLAINT-709', '3816f90e-d594-4018-ab4e-0921e838121f', 'DOI_TAC', 'Đối tác xin lỗi và đề xuất hoàn tiền - Phu Quoc Sunset Dining', CURRENT_TIMESTAMP - INTERVAL '7' DAY);

-- =============================================================
-- PATCH MAMMO 2026-05-30: Bổ sung dữ liệu MyBooking đa dạng để test UI
-- Có đủ case: đã đánh giá, chưa đánh giá, đang xử lý, chờ thanh toán, đã hủy
-- =============================================================
INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('BOOKING-730', 'MAH730', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH001', CURRENT_TIMESTAMP - INTERVAL '1' DAY, 1800000.0, 0.0, 1800000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@gmail.com', 'Hotel chưa đánh giá - dùng để test nút Viết đánh giá', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '1' DAY, CURRENT_TIMESTAMP - INTERVAL '1' DAY);
INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien)
VALUES ('BOOKING-730', CURRENT_DATE - INTERVAL '1' DAY, CURRENT_DATE, 1, 2, '14:00:00');
INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien)
VALUES ('DKCT-730', 'BOOKING-730', 'PHONG-AH001-01', 'Phòng Standard Aurora Hotel Saigon', 1, 1800000.0, 1, 1800000.0);

INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('BOOKING-731', 'MAH731', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH002', CURRENT_TIMESTAMP - INTERVAL '2' DAY, 1250000.0, 0.0, 1250000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@gmail.com', 'Hotel đang chờ thanh toán - không hiện nút đánh giá', 'CHO_THANH_TOAN', CURRENT_TIMESTAMP - INTERVAL '2' DAY, CURRENT_TIMESTAMP - INTERVAL '2' DAY);
INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien)
VALUES ('BOOKING-731', CURRENT_DATE + INTERVAL '2' DAY, CURRENT_DATE + INTERVAL '3' DAY, 1, 2, '14:00:00');
INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien)
VALUES ('DKCT-731', 'BOOKING-731', 'PHONG-AH002-01', 'Phòng Standard Mekong Riverside Can Tho', 1, 1250000.0, 1, 1250000.0);

INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('BOOKING-732', 'MAH732', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AH003', CURRENT_TIMESTAMP - INTERVAL '3' DAY, 1100000.0, 0.0, 1100000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@gmail.com', 'Hotel đã hủy - test trạng thái', 'DA_HUY', CURRENT_TIMESTAMP - INTERVAL '3' DAY, CURRENT_TIMESTAMP - INTERVAL '3' DAY);
INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien)
VALUES ('BOOKING-732', CURRENT_DATE + INTERVAL '5' DAY, CURRENT_DATE + INTERVAL '6' DAY, 1, 2, '14:00:00');
INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien)
VALUES ('DKCT-732', 'BOOKING-732', 'PHONG-AH003-01', 'Phòng Standard Cloudy Peaks Da Lat', 1, 1100000.0, 1, 1100000.0);

INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('RES-733', 'NHA733', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR001', CURRENT_TIMESTAMP - INTERVAL '4' HOUR, 650000.0, 50000.0, 600000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@gmail.com', 'Nhà hàng chưa đánh giá - dùng để test nút Viết đánh giá', 'DA_HOAN_THANH', CURRENT_TIMESTAMP - INTERVAL '4' HOUR, CURRENT_TIMESTAMP - INTERVAL '4' HOUR);
INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc)
VALUES ('RES-733', CURRENT_TIMESTAMP - INTERVAL '4' HOUR, CURRENT_TIMESTAMP - INTERVAL '2' HOUR, 4, 120000.0, TRUE);

INSERT INTO don_dat_cho (id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai_don, created_at, updated_at)
VALUES ('RES-734', 'NHA734', '875f97ce-e548-4507-a4e0-77efc0977ad1', 'HOSOKD-AR002', CURRENT_TIMESTAMP - INTERVAL '6' HOUR, 360000.0, 0.0, 360000.0, 'Anh Bid Customer', '0900000001', 'anhbid1000@gmail.com', 'Nhà hàng đã xác nhận - test trạng thái', 'DA_XAC_NHAN', CURRENT_TIMESTAMP - INTERVAL '6' HOUR, CURRENT_TIMESTAMP - INTERVAL '6' HOUR);
INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc)
VALUES ('RES-734', CURRENT_TIMESTAMP + INTERVAL '1' DAY, (CURRENT_TIMESTAMP + INTERVAL '1' DAY + INTERVAL '2' HOUR), 3, 100000.0, TRUE);

-- =============================================================
-- END EXTRA DATASET MAMMO
-- =============================================================
