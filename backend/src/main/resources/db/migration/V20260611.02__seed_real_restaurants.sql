-- Seed 50 real restaurants with actual data (simplified)
DELETE FROM nha_hang WHERE id_tai_san LIKE 'basn-%';
DELETE FROM tai_san WHERE id_tai_san LIKE 'basn-%';
DELETE FROM ho_so_kinh_doanh WHERE id_ho_so LIKE 'bhsn-%';

CREATE TEMP TABLE tmp_bulk_rest_seed AS
SELECT * FROM (VALUES
(1, '001', 'Đà Lạt', 'Phường 1', 11.9416, 108.4428, '1 Trần Quốc Toản, Phường 1, Đà Lạt, Lâm Đồng'),
(2, '002', 'Đà Lạt', 'Phường 3', 11.9268, 108.4355, 'Lầu 1 Khu Cáp Treo Đồi Robin, Phường 3, Đà Lạt, Lâm Đồng'),
(3, '003', 'Đà Lạt', 'Phường 2', 11.9425, 108.4350, '94 Lý Tự Trọng, Phường 2, Đà Lạt, Lâm Đồng'),
(4, '004', 'Đà Lạt', 'Phường 10', 11.9421, 108.4625, '24/7 Hùng Vương, Phường 10, Đà Lạt, Lâm Đồng'),
(5, '005', 'Đà Lạt', 'Phường 3', 11.9362, 108.4377, '38 Phạm Ngũ Lão, Phường 3, Đà Lạt, Lâm Đồng'),
(6, '006', 'Đà Lạt', 'Phường 6', 11.9472, 108.4358, '1 Thi Sách, Phường 6, Đà Lạt, Lâm Đồng'),
(7, '007', 'Đà Lạt', 'Phường 2', 11.9479, 108.4359, '427/3 Phan Đình Phùng, Phường 2, Đà Lạt, Lâm Đồng'),
(8, '008', 'Nha Trang', 'Lộc Thọ', 12.2388, 109.1967, '32-34 Trần Phú, Lộc Thọ, Nha Trang, Khánh Hòa'),
(9, '009', 'Nha Trang', 'Lộc Thọ', 12.2335, 109.1970, 'Lô 29 Trần Phú, Lộc Thọ, Nha Trang, Khánh Hòa'),
(10, '010', 'Nha Trang', 'Vĩnh Thọ', 12.2645, 109.2012, '9C Phạm Văn Đồng, Vĩnh Thọ, Nha Trang, Khánh Hòa'),
(11, '011', 'Nha Trang', 'Lộc Thọ', 12.2356, 109.1952, '73/6 Trần Quang Khải, Lộc Thọ, Nha Trang, Khánh Hòa'),
(12, '012', 'Nha Trang', 'Lộc Thọ', 12.2350, 109.1948, '1A Biệt Thự, Lộc Thọ, Nha Trang, Khánh Hòa'),
(13, '013', 'Nha Trang', 'Lộc Thọ', 12.2355, 109.1965, '72-74 Trần Phú, Lộc Thọ, Nha Trang, Khánh Hòa'),
(14, '014', 'Nha Trang', 'Lộc Thọ', 12.2359, 109.1960, '14 Trần Quang Khải, Lộc Thọ, Nha Trang, Khánh Hòa'),
(15, '015', 'Đà Nẵng', 'Ngũ Hành Sơn', 16.0532, 108.2435, '90 Lê Quang Đạo, Mỹ An, Ngũ Hành Sơn, Đà Nẵng'),
(16, '016', 'Đà Nẵng', 'Sơn Trà', 16.0965, 108.2450, 'K139/H59/38 Trần Quang Khải, Thọ Quang, Sơn Trà, Đà Nẵng'),
(17, '017', 'Đà Nẵng', 'Ngũ Hành Sơn', 16.0562, 108.2415, '54 Nguyễn Văn Thoại, Bắc Mỹ Phú, Ngũ Hành Sơn, Đà Nẵng'),
(18, '018', 'Đà Nẵng', 'Thanh Khê', 16.0570, 108.2430, '195/9 Nguyễn Văn Thoại, Đà Nẵng'),
(19, '019', 'Đà Nẵng', 'Sơn Trà', 16.0645, 108.2430, 'Lô 1-2 Mỹ Khê 4, Phước Mỹ, Sơn Trà, Đà Nẵng'),
(20, '020', 'Đà Nẵng', 'Hải Châu', 16.0745, 108.2165, '122/11 Hải Phòng, Thạch Thang, Hải Châu, Đà Nẵng'),
(21, '021', 'Đà Nẵng', 'Thanh Khê', 16.0612, 108.2045, '176 Nguyễn Tri Phương, Thạc Gián, Thanh Khê, Đà Nẵng'),
(22, '022', 'Hà Nội', 'Hoàn Kiếm', 21.0315, 105.8471, '21 Đường Thành, Cửa Đông, Hoàn Kiếm, Hà Nội'),
(23, '023', 'Hà Nội', 'Hoàn Kiếm', 21.0252, 105.8540, '43 Tràng Tiền, Hoàn Kiếm, Hà Nội'),
(24, '024', 'Hà Nội', 'Hoàn Kiếm', 21.0310, 105.8505, '14 Lê Thái Tổ, Hoàn Kiếm, Hà Nội'),
(25, '025', 'Hà Nội', 'Hoàn Kiếm', 21.0220, 105.8520, '34 Hàng Bài, Hoàn Kiếm, Hà Nội'),
(26, '026', 'Hà Nội', 'Cầu Giấy', 21.0312, 105.7900, 'Ngõ 84 Trần Thái Tông, Cầu Giấy, Hà Nội'),
(27, '027', 'Hà Nội', 'Tây Hồ', 21.0740, 105.8150, '614 Lạc Long Quân, Tây Hồ, Hà Nội'),
(28, '028', 'Hà Nội', 'Nam Từ Liêm', 21.0088, 105.7865, 'JW Marriott Hotel, Số 8 Đỗ Đức Dục, Nam Từ Liêm, Hà Nội'),
(29, '029', 'Vũng Tàu', 'Phường 5', 10.3540, 107.0670, '03 Trần Phú, Phường 5, Vũng Tàu'),
(30, '030', 'Vũng Tàu', 'Phường 2', 10.3420, 107.0815, 'Ngã tư Phan Chu Trinh, Phường 2, Vũng Tàu'),
(31, '031', 'Vũng Tàu', 'Phường 2', 10.3395, 107.0750, '3 Hạ Long, Phường 2, Vũng Tàu'),
(32, '032', 'Vũng Tàu', 'Phường 3', 10.3475, 107.0780, '40 Trương Công Định, Phường 3, Vũng Tàu'),
(33, '033', 'Vũng Tàu', 'Thắng Tam', 10.3440, 107.0920, '215 Võ Thị Sáu, Thắng Tam, Vũng Tàu'),
(34, '034', 'Vũng Tàu', 'Thắng Tam', 10.3450, 107.0950, '1 Đào Duy Từ, Thắng Tam, Vũng Tàu'),
(35, '035', 'Vũng Tàu', 'Phường 2', 10.3345, 107.0795, '230 Phan Chu Trinh, Phường 2, Vũng Tàu'),
(36, '036', 'Hồ Chí Minh', 'Quận 1', 10.7745, 106.7050, '35-37 Ngô Đức Kế, Bến Nghé, Quận 1, Hồ Chí Minh'),
(37, '037', 'Hồ Chí Minh', 'Quận 1', 10.7765, 106.6990, '158 Bis/40-41 Pasteur, Bến Nghé, Quận 1, Hồ Chí Minh'),
(38, '038', 'Hồ Chí Minh', 'Quận 3', 10.7810, 106.6825, '25 Kỳ Đồng, Phường 9, Quận 3, Hồ Chí Minh'),
(39, '039', 'Hồ Chí Minh', 'Quận 3', 10.7760, 106.6920, '32 Võ Văn Tần, Phường 6, Quận 3, Hồ Chí Minh'),
(40, '040', 'Hồ Chí Minh', 'Quận 3', 10.7805, 106.6935, '163 Pasteur, Phường 6, Quận 3, Hồ Chí Minh'),
(41, '041', 'Hồ Chí Minh', 'Quận 1', 10.7780, 106.7020, 'L5-03 Vincom Lê Thánh Tôn, Quận 1, Hồ Chí Minh'),
(42, '042', 'Hồ Chí Minh', 'Quận 1', 10.7712, 106.7001, 'Số 2 Bến Nghé, Quận 1, Hồ Chí Minh'),
(43, '043', 'Hồ Chí Minh', 'Quận 1', 10.7850, 106.6975, '178-180D Hai Bà Trưng, Đa Kao, Quận 1, Hồ Chí Minh'),
(44, '044', 'Phú Quốc', 'Dương Đông', 10.2105, 103.9635, '66 Trần Hưng Đạo, Dương Đông, Phú Quốc'),
(45, '045', 'Phú Quốc', 'Dương Đông', 10.2289, 103.9573, '28 Bạch Đằng, Dương Đông, Phú Quốc'),
(46, '046', 'Phú Quốc', 'Dương Đông', 10.2225, 103.9680, '131 30 Tháng 4, Dương Đông, Phú Quốc'),
(47, '047', 'Phú Quốc', 'Dương Tơ', 10.1500, 103.9685, 'Bãi Trường, Dương Tơ, Phú Quốc, Kiên Giang'),
(48, '048', 'Phú Quốc', 'Dương Đông', 10.2245, 103.9660, '77 Đường 30 Tháng 4, Dương Đông, Phú Quốc'),
(49, '049', 'Phú Quốc', 'Dương Đông', 10.2185, 103.9592, '26 Nguyễn Trãi, Dương Đông, Phú Quốc'),
(50, '050', 'Phú Quốc', 'Dương Đông', 10.2120, 103.9622, 'Đồi Sao Mai, 69 Trần Hưng Đạo, Dương Đông, Phú Quốc')
) t(i, code, city, district, lat, lon, address);

INSERT INTO ho_so_kinh_doanh (id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh, toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho, kinh_do, vi_do, trang_thai_hoat_dong)
SELECT
    'bhsn-' || s.code,
    (SELECT id FROM doi_tac ORDER BY id LIMIT 1 OFFSET ((s.i - 1) % (SELECT COUNT(*) FROM doi_tac))),
    'NHA_HANG',
    CASE s.i
        WHEN 1 THEN 'Nhà hàng Bờ Hồ Đà Lạt' WHEN 2 THEN 'Leguda Buffet Rau Đà Lạt'
        WHEN 3 THEN 'Biang Bistro' WHEN 4 THEN 'Nhà hàng Memory Đà Lạt'
        WHEN 5 THEN 'Chu Quán BBQ' WHEN 6 THEN 'Émai Dalat'
        WHEN 7 THEN 'Quán Xưa Đà Lạt' WHEN 8 THEN 'Costa Seafood'
        WHEN 9 THEN 'Louisiane Brewhouse' WHEN 10 THEN 'Làng Biển Quán'
        WHEN 11 THEN E'Yen''s Restaurant' WHEN 12 THEN 'Galangal'
        WHEN 13 THEN 'Sailing Club Nha Trang' WHEN 14 THEN 'Olivia Restaurant'
        WHEN 15 THEN 'Ngon Villa Đà Nẵng' WHEN 16 THEN 'Hải sản Năm Đảnh'
        WHEN 17 THEN 'Bếp Cuốn Đà Nẵng' WHEN 18 THEN 'Thùng Phi BBQ'
        WHEN 19 THEN 'Cội Nguồn Veggie' WHEN 20 THEN 'Le Bambino'
        WHEN 21 THEN 'Cơm Niêu Nhà Đỏ' WHEN 22 THEN 'Chả Cá Thăng Long'
        WHEN 23 THEN E'Pizza 4P''s Tràng Tiền' WHEN 24 THEN 'Gogi House Lê Thái Tổ'
        WHEN 25 THEN 'Ưu Đàm Chay' WHEN 26 THEN 'Hải Sản Biển Đông'
        WHEN 27 THEN 'Sen Tây Hồ' WHEN 28 THEN 'French Grill'
        WHEN 29 THEN 'Gành Hào Vũng Tàu' WHEN 30 THEN 'Chợ Lưới Seafood'
        WHEN 31 THEN 'Marina Club' WHEN 32 THEN 'Lẩu Cá Đuối Trương Công Định'
        WHEN 33 THEN 'Nướng Hàn Quốc KangZ' WHEN 34 THEN 'Goodlife Vegetarian'
        WHEN 35 THEN 'Luca Pizza & Italian Restaurant' WHEN 36 THEN 'Moo Beef Steak Prime'
        WHEN 37 THEN 'Secret Garden' WHEN 38 THEN 'Rạn Biển'
        WHEN 39 THEN 'ShHum Vegetarian' WHEN 40 THEN 'Góc Hà Nội'
        WHEN 41 THEN 'Gyu-Kaku BBQ' WHEN 42 THEN 'Phố Trực Sinh Thái'
        WHEN 43 THEN 'Noir. Dining in the Dark' WHEN 44 THEN 'Xin Chào Restaurant'
        WHEN 45 THEN 'Bún Quậy Kiến Xây' WHEN 46 THEN 'Nhà hàng Ra Khơi'
        WHEN 47 THEN 'Sunset Sanato Beach Club' WHEN 48 THEN 'Bún Quậy Thanh Hùng'
        WHEN 49 THEN 'Crab House Phú Quốc' WHEN 50 THEN 'Chuồn Chuồn Bistro & Sky Bar'
    END,
    'REST' || s.code,
    'GPKD-REST-' || s.code,
    ROUND(s.lat::numeric, 6)::text || ',' || ROUND(s.lon::numeric, 6)::text,
    '0908123' || s.code,
    'restaurant.' || s.code || '@travi.vn',
    s.address,
    s.city,
    s.lon,
    s.lat,
    'DANG_HOAT_DONG'
FROM tmp_bulk_rest_seed s
ON CONFLICT DO NOTHING;

INSERT INTO tai_san (id_tai_san, ho_so_kinh_doanh_id, mo_ta, trang_thai, gia_co_ban, is_dynamic_pricing, rating_average, review_count, dia_chi, thanh_pho, kinh_do, vi_do)
SELECT
    'basn-' || s.code,
    'bhsn-' || s.code,
    'Nhà hàng du lịch tổng hợp tại ' || s.city,
    'SAN_SANG',
    150000 + (s.i * 5000),
    (s.i % 2 = 0),
    4.0 + ((s.i % 10) * 0.08),
    10 + (s.i * 3),
    s.address,
    s.city,
    s.lon,
    s.lat
FROM tmp_bulk_rest_seed s
ON CONFLICT DO NOTHING;

INSERT INTO nha_hang (id_tai_san, ten, loai_am_thuc, gio_mo_cua, gio_dong_cua, suc_chua, co_dat_ban_truoc, co_dat_mon_truoc)
SELECT
    'basn-' || s.code,
    CASE s.i
        WHEN 1 THEN 'Nhà hàng Bờ Hồ Đà Lạt' WHEN 2 THEN 'Leguda Buffet Rau Đà Lạt'
        WHEN 3 THEN 'Biang Bistro' WHEN 4 THEN 'Nhà hàng Memory Đà Lạt'
        WHEN 5 THEN 'Chu Quán BBQ' WHEN 6 THEN 'Émai Dalat'
        WHEN 7 THEN 'Quán Xưa Đà Lạt' WHEN 8 THEN 'Costa Seafood'
        WHEN 9 THEN 'Louisiane Brewhouse' WHEN 10 THEN 'Làng Biển Quán'
        WHEN 11 THEN E'Yen''s Restaurant' WHEN 12 THEN 'Galangal'
        WHEN 13 THEN 'Sailing Club Nha Trang' WHEN 14 THEN 'Olivia Restaurant'
        WHEN 15 THEN 'Ngon Villa Đà Nẵng' WHEN 16 THEN 'Hải sản Năm Đảnh'
        WHEN 17 THEN 'Bếp Cuốn Đà Nẵng' WHEN 18 THEN 'Thùng Phi BBQ'
        WHEN 19 THEN 'Cội Nguồn Veggie' WHEN 20 THEN 'Le Bambino'
        WHEN 21 THEN 'Cơm Niêu Nhà Đỏ' WHEN 22 THEN 'Chả Cá Thăng Long'
        WHEN 23 THEN E'Pizza 4P''s Tràng Tiền' WHEN 24 THEN 'Gogi House Lê Thái Tổ'
        WHEN 25 THEN 'Ưu Đàm Chay' WHEN 26 THEN 'Hải Sản Biển Đông'
        WHEN 27 THEN 'Sen Tây Hồ' WHEN 28 THEN 'French Grill'
        WHEN 29 THEN 'Gành Hào Vũng Tàu' WHEN 30 THEN 'Chợ Lưới Seafood'
        WHEN 31 THEN 'Marina Club' WHEN 32 THEN 'Lẩu Cá Đuối Trương Công Định'
        WHEN 33 THEN 'Nướng Hàn Quốc KangZ' WHEN 34 THEN 'Goodlife Vegetarian'
        WHEN 35 THEN 'Luca Pizza & Italian Restaurant' WHEN 36 THEN 'Moo Beef Steak Prime'
        WHEN 37 THEN 'Secret Garden' WHEN 38 THEN 'Rạn Biển'
        WHEN 39 THEN 'ShHum Vegetarian' WHEN 40 THEN 'Góc Hà Nội'
        WHEN 41 THEN 'Gyu-Kaku BBQ' WHEN 42 THEN 'Phố Trực Sinh Thái'
        WHEN 43 THEN 'Noir. Dining in the Dark' WHEN 44 THEN 'Xin Chào Restaurant'
        WHEN 45 THEN 'Bún Quậy Kiến Xây' WHEN 46 THEN 'Nhà hàng Ra Khơi'
        WHEN 47 THEN 'Sunset Sanato Beach Club' WHEN 48 THEN 'Bún Quậy Thanh Hùng'
        WHEN 49 THEN 'Crab House Phú Quốc' WHEN 50 THEN 'Chuồn Chuồn Bistro & Sky Bar'
    END,
    CASE (s.i - 1) % 5
        WHEN 0 THEN 'VIET_NAM' WHEN 1 THEN 'HAI_SAN'
        WHEN 2 THEN 'NUONG_BBQ' WHEN 3 THEN 'AU_A' ELSE 'CHAY'
    END,
    TIME '09:00', TIME '22:30',
    40 + (s.i % 120),
    TRUE,
    (s.i % 4 <> 0)
FROM tmp_bulk_rest_seed s
ON CONFLICT DO NOTHING;

DROP TABLE IF EXISTS tmp_bulk_rest_seed;