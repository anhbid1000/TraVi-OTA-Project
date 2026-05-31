INSERT INTO users (
    id,
    ngay_sinh,
    so_lan_dang_nhap_sai,
    vai_tro_id,
    lan_cuoi_dang_nhap,
    ngay_cap_nhat,
    ngay_tao,
    email,
    gioi_tinh,
    ho_ten,
    mat_khau,
    so_dien_thoai,
    trang_thai,
    username
)
VALUES
    (1, '2000-01-15', 0, 2, NULL, NOW(), NOW(), 'nguyenvana@example.com', 'NAM', 'Nguyễn Văn A', '123456', '0901000001', 'HOAT_DONG', 'nguyenvana'),
    (2, '2001-03-22', 0, 2, NULL, NOW(), NOW(), 'tranthib@example.com', 'NU', 'Trần Thị B', '123456', '0901000002', 'HOAT_DONG', 'tranthib'),
    (3, '1999-07-10', 0, 2, NULL, NOW(), NOW(), 'leminhc@example.com', 'NAM', 'Lê Minh C', '123456', '0901000003', 'HOAT_DONG', 'leminhc'),
    (4, '2002-11-05', 0, 2, NULL, NOW(), NOW(), 'phamthid@example.com', 'NU', 'Phạm Thị D', '123456', '0901000004', 'HOAT_DONG', 'phamthid'),
    (5, '2000-09-18', 0, 2, NULL, NOW(), NOW(), 'hoangvane@example.com', 'NAM', 'Hoàng Văn E', '123456', '0901000005', 'HOAT_DONG', 'hoangvane'),
    (6, '2003-02-28', 0, 2, NULL, NOW(), NOW(), 'dangthif@example.com', 'NU', 'Đặng Thị F', '123456', '0901000006', 'HOAT_DONG', 'dangthif'),
    (7, '1998-12-12', 0, 2, NULL, NOW(), NOW(), 'buiquangh@example.com', 'NAM', 'Bùi Quang H', '123456', '0901000007', 'HOAT_DONG', 'buiquangh'),
    (8, '2001-06-30', 0, 2, NULL, NOW(), NOW(), 'vothik@example.com', 'NU', 'Võ Thị K', '123456', '0901000008', 'HOAT_DONG', 'vothik'),
    (9, '1997-04-08', 0, 2, NULL, NOW(), NOW(), 'dovanl@example.com', 'NAM', 'Đỗ Văn L', '123456', '0901000009', 'HOAT_DONG', 'dovanl'),
    (10, '2002-08-25', 0, 2, NULL, NOW(), NOW(), 'ngothim@example.com', 'NU', 'Ngô Thị M', '123456', '0901000010', 'HOAT_DONG', 'ngothim');


-- Insert Data Hotel Amenities
-- 1. Nhóm Tiện ích phòng (TIEN-ICH-PHONG)
INSERT INTO tien_ich_khach_san (id, loai_tien_ich, ten_tien_ich, mo_ta) VALUES
                                                                            (1, 'TIEN-ICH-PHONG', 'Điều hòa nhiệt độ', 'Máy lạnh công suất lớn, có chế độ lọc không khí'),
                                                                            (2, 'TIEN-ICH-PHONG', 'Wifi tốc độ cao', 'Mạng không dây miễn phí băng thông rộng tại phòng'),
                                                                            (3, 'TIEN-ICH-PHONG', 'Tủ lạnh nhỏ (Minibar)', 'Tủ lạnh chứa nước ngọt, bia và đồ ăn nhẹ'),
                                                                            (4, 'TIEN-ICH-PHONG', 'Máy sấy tóc', 'Máy sấy tóc công suất 2000W trang bị trong nhà tắm'),
                                                                            (5, 'TIEN-ICH-PHONG', 'Két sắt bảo mật', 'Két sắt điện tử an toàn cho tài sản cá nhân');

-- 2. Nhóm Dịch vụ chung (DICH-VU-CHUNG)
INSERT INTO tien_ich_khach_san (id, loai_tien_ich, ten_tien_ich, mo_ta) VALUES
                                                                            (6, 'DICH-VU-CHUNG', 'Hồ bơi vô cực', 'Hồ bơi ngoài trời view toàn cảnh tầng thượng'),
                                                                            (7, 'DICH-VU-CHUNG', 'Trung tâm Gym & Fitness', 'Phòng tập thể hình đầy đủ máy móc hiện đại'),
                                                                            (8, 'DICH-VU-CHUNG', 'Quầy lễ tân 24/7', 'Hỗ trợ check-in/check-out và giải đáp thắc mắc bất cứ lúc nào'),
                                                                            (9, 'DICH-VU-CHUNG', 'Dịch vụ Spa & Massage', 'Khu vực thư giãn, chăm sóc sức khỏe và trị liệu chuyên nghiệp'),
                                                                            (10, 'DICH-VU-CHUNG', 'Khu vui chơi trẻ em', 'Không gian an toàn, nhiều trò chơi cho gia đình có con nhỏ');

-- 3. Nhóm Ẩm thực & Giải trí (AM-THUC-GIAI-TRI)
INSERT INTO tien_ich_khach_san (id, loai_tien_ich, ten_tien_ich, mo_ta) VALUES
                                                                            (11, 'AM-THUC-GIAI-TRI', 'Nhà hàng Buffet sáng', 'Phục vụ bữa sáng tự chọn đa dạng món Á-Âu miễn phí'),
                                                                            (12, 'AM-THUC-GIAI-TRI', 'Rooftop Bar', 'Quán bar sân thượng phục vụ Cocktail và nhạc Live ban đêm'),
                                                                            (13, 'AM-THUC-GIAI-TRI', 'Dịch vụ ăn tại phòng', 'Phục vụ đồ ăn thức uống tận giường theo yêu cầu của khách');

-- 4. Nhóm Đi lại & Tiện nghi khác (DI-LAI-TIEN-NGHI)
INSERT INTO tien_ich_khach_san (id, loai_tien_ich, ten_tien_ich, mo_ta) VALUES
                                                                            (14, 'DI-LAI-TIEN-NGHI', 'Bãi đỗ xe ô tô miễn phí', 'Bãi giữ xe rộng rãi, an toàn, có bảo vệ trông giữ 24/24'),
                                                                            (15, 'DI-LAI-TIEN-NGHI', 'Xe đưa đón sân bay', 'Dịch vụ xe đưa đón tận nơi từ khách sạn ra sân bay và ngược lại'),
                                                                            (16, 'DI-LAI-TIEN-NGHI', 'Cho thuê xe máy', 'Dịch vụ cho thuê xe số, xe ga đi phượt quanh thành phố');
INSERT INTO tien_ich_khach_san (id, loai_tien_ich, ten_tien_ich, mo_ta) VALUES
                                                                            (17, 'TIEN-ICH-PHONG', 'Ban công hướng biển', 'Ban công rộng rãi có bàn ghế ngắm hoàng hôn'),
                                                                            (18, 'TIEN-ICH-PHONG', 'Tivi thông minh 4K', 'Hỗ trợ kết nối Netflix, Youtube và truyền hình cáp'),
                                                                            (19, 'TIEN-ICH-PHONG', 'Hệ thống cách âm cao cấp', 'Cửa kính cường lực 2 lớp đảm bảo không gian yên tĩnh tuyệt đối'),
                                                                            (20, 'TIEN-ICH-PHONG', 'Bàn làm việc tiêu chuẩn', 'Không gian làm việc thoải mái với đèn đọc sách và ổ cắm đa năng'),
                                                                            (21, 'TIEN-ICH-PHONG', 'Áo choàng tắm & Dép đi trong phòng', 'Đồ dùng cá nhân chất liệu cotton cao cấp, êm ái');
-- 6. Nhóm Tiện ích phòng tắm (TIEN-ICH-PHONG-TAM)
INSERT INTO tien_ich_khach_san (id, loai_tien_ich, ten_tien_ich, mo_ta) VALUES
                                                                            (22, 'TIEN-ICH-PHONG-TAM', 'Bồn tắm nằm (Jacuzzi)', 'Bồn tắm sục massage thư giãn cao cấp'),
                                                                            (23, 'TIEN-ICH-PHONG-TAM', 'Vòi hoa sen tăng áp', 'Hệ thống tắm mưa nước nóng lạnh áp lực mạnh'),
                                                                            (24, 'TIEN-ICH-PHONG-TAM', 'Bộ vệ sinh cá nhân miễn phí', 'Cung cấp đầy đủ bàn chải, kem đánh răng, dầu gội, sữa tắm hàng ngày');


INSERT INTO doi_tac (
    id,
    ti_le_chiet_khau
)
VALUES
    (1, 10),
    (2, 10),
    (3, 10),
    (4, 10),
    (5, 10),
    (6, 10),
    (7, 10),
    (8, 10),
    (9, 10),
    (10, 10);


INSERT INTO ho_so_kinh_doanh (
    id_ho_so,
    deleted,
    kinh_do,
    vi_do,
    thoi_gian_cap_nhat,
    thoi_gian_dang_ky,
    sdt_lien_he,
    ma_so_thue,
    thanh_pho,
    dia_chi,
    doi_tac_id,
    email_lien_he,
    giay_phep_kinh_doanh,
    loai_dich_vu,
    ten_co_so,
    toa_do_gps,
    trang_thai_hoat_dong
)
VALUES
    (1, false, 107.0843, 10.3460, NOW(), NOW(), '0902000001', '0310000001', 'Vũng Tàu', '12 Hạ Long, Phường 2', 1, 'seaview.vungtau@example.com', 'GPKD-VT-001', 'KHACH_SAN', 'Sea View Vũng Tàu Hotel', '10.3460,107.0843', 'DANG_HOAT_DONG'),

    (2, false, 107.0765, 10.3502, NOW(), NOW(), '0902000002', '0310000002', 'Vũng Tàu', '25 Thùy Vân, Phường 2', 2, 'bienxanh.vungtau@example.com', 'GPKD-VT-002', 'NHA_HANG', 'Nhà hàng Biển Xanh Vũng Tàu', '10.3502,107.0765', 'DANG_HOAT_DONG'),

    (3, false, 107.0912, 10.3711, NOW(), NOW(), '0902000003', '0310000003', 'Vũng Tàu', '88 Trần Phú, Phường 5', 3, 'sunrise.vungtau@example.com', 'GPKD-VT-003', 'KHACH_SAN', 'Sunrise Vũng Tàu Resort', '10.3711,107.0912', 'DANG_HOAT_DONG'),

    (4, false, 108.2208, 16.0678, NOW(), NOW(), '0902000004', '0310000004', 'Đà Nẵng', '120 Võ Nguyên Giáp, Sơn Trà', 4, 'ocean.danang@example.com', 'GPKD-DN-001', 'KHACH_SAN', 'Ocean Đà Nẵng Hotel', '16.0678,108.2208', 'DANG_HOAT_DONG'),

    (5, false, 108.2441, 16.0544, NOW(), NOW(), '0902000005', '0310000005', 'Đà Nẵng', '45 Nguyễn Văn Thoại, Ngũ Hành Sơn', 5, 'mykhe.food@example.com', 'GPKD-DN-002', 'NHA_HANG', 'Nhà hàng Mỹ Khê Đà Nẵng', '16.0544,108.2441', 'DANG_HOAT_DONG'),

    (6, false, 108.2022, 16.0471, NOW(), NOW(), '0902000006', '0310000006', 'Đà Nẵng', '30 Bạch Đằng, Hải Châu', 6, 'hanriver.danang@example.com', 'GPKD-DN-003', 'KHACH_SAN', 'Han River Đà Nẵng Hotel', '16.0471,108.2022', 'DANG_HOAT_DONG'),

    (7, false, 109.1967, 12.2388, NOW(), NOW(), '0902000007', '0310000007', 'Nha Trang', '18 Trần Phú, Lộc Thọ', 7, 'bluebay.nhatrang@example.com', 'GPKD-NT-001', 'KHACH_SAN', 'Blue Bay Nha Trang Hotel', '12.2388,109.1967', 'DANG_HOAT_DONG'),

    (8, false, 109.1984, 12.2442, NOW(), NOW(), '0902000008', '0310000008', 'Nha Trang', '72 Nguyễn Thiện Thuật, Lộc Thọ', 8, 'haisan.nhatrang@example.com', 'GPKD-NT-002', 'NHA_HANG', 'Nhà hàng Hải Sản Nha Trang', '12.2442,109.1984', 'DANG_HOAT_DONG'),

    (9, false, 109.1905, 12.2501, NOW(), NOW(), '0902000009', '0310000009', 'Nha Trang', '10 Biệt Thự, Tân Lập', 9, 'coral.nhatrang@example.com', 'GPKD-NT-003', 'KHACH_SAN', 'Coral Nha Trang Resort', '12.2501,109.1905', 'DANG_HOAT_DONG'),

    (10, false, 109.2046, 12.2357, NOW(), NOW(), '0902000010', '0310000010', 'Nha Trang', '55 Phạm Văn Đồng, Vĩnh Hải', 10, 'sunsea.nhatrang@example.com', 'GPKD-NT-004', 'NHA_HANG', 'Sun Sea Nha Trang Restaurant', '12.2357,109.2046', 'DANG_HOAT_DONG');


INSERT INTO tai_san (
    id_tai_san,
    gia_co_ban,
    is_dynamic_pricing,
    ho_so_kinh_doanh_id,
    mo_ta,
    trang_thai
)
VALUES
    (1, 850000, false, 1, 'Phòng tiêu chuẩn hướng biển tại Vũng Tàu, phù hợp cho 2 khách.', 'SAN_SANG'),
    (2, 1200000, false, 2, 'Khu vực đặt bàn nhà hàng hải sản, sức chứa 4 đến 6 khách.', 'SAN_SANG'),
    (3, 1800000, false, 3, 'Phòng deluxe resort gần biển, có ban công và tiện nghi đầy đủ.', 'SAN_SANG'),

    (4, 950000, false, 4, 'Phòng khách sạn gần biển Mỹ Khê, phù hợp cho khách du lịch.', 'SAN_SANG'),
    (5, 650000, false, 5, 'Bàn ăn nhà hàng đặc sản Đà Nẵng, phù hợp nhóm gia đình.', 'SAN_SANG'),
    (6, 1350000, false, 6, 'Phòng khách sạn view sông Hàn, gần trung tâm thành phố.', 'SAN_SANG'),

    (7, 1100000, false, 7, 'Phòng khách sạn gần biển Trần Phú, tiện di chuyển tham quan.', 'SAN_SANG'),
    (8, 700000, false, 8, 'Bàn nhà hàng hải sản Nha Trang, phù hợp nhóm 4 khách.', 'SAN_SANG'),
    (9, 2000000, false, 9, 'Phòng resort cao cấp tại Nha Trang, có không gian nghỉ dưỡng riêng tư.', 'SAN_SANG'),
    (10, 750000, false, 10, 'Khu vực đặt bàn nhà hàng ven biển Nha Trang, phù hợp nhóm nhỏ.', 'SAN_SANG');


INSERT INTO nha_hang (
    id_tai_san,
    co_dat_ban_truoc,
    co_dat_mon_truoc,
    gio_dong_cua,
    gio_mo_cua,
    suc_chua,
    loai_am_thuc,
    ten
)
VALUES
    (2, true, true, '22:00:00', '09:00:00', 80, 'HAI_SAN', 'Nhà hàng Biển Xanh Vũng Tàu'),

    (5, true, true, '23:00:00', '10:00:00', 120, 'DAC_SAN_DIA_PHUONG', 'Nhà hàng Mỹ Khê Đà Nẵng'),

    (8, true, true, '22:30:00', '09:30:00', 100, 'HAI_SAN', 'Nhà hàng Hải Sản Nha Trang'),

    (10, true, false, '22:00:00', '08:30:00', 70, 'HAI_SAN', 'Sun Sea Nha Trang Restaurant');



INSERT INTO khach_san (
    id_tai_san,
    gio_nhan_phong,
    gio_nhan_phong_mac_dinh,
    gio_tra_phong,
    gio_tra_phong_mac_dinh,
    hang_sao,
    so_tang,
    tong_so_phong,
    loai_khach_san,
    ten
)
VALUES
    (1, '14:00:00', '14:00:00', '12:00:00', '12:00:00', 3, 8, 60, 'KHACH_SAN', 'Sea View Vũng Tàu Hotel'),

    (3, '14:00:00', '14:00:00', '12:00:00', '12:00:00', 4, 12, 120, 'RESORT', 'Sunrise Vũng Tàu Resort'),

    (4, '14:00:00', '14:00:00', '12:00:00', '12:00:00', 4, 15, 150, 'KHACH_SAN', 'Ocean Đà Nẵng Hotel'),

    (6, '14:00:00', '14:00:00', '12:00:00', '12:00:00', 4, 18, 180, 'KHACH_SAN', 'Han River Đà Nẵng Hotel'),

    (7, '14:00:00', '14:00:00', '12:00:00', '12:00:00', 3, 10, 90, 'KHACH_SAN', 'Blue Bay Nha Trang Hotel'),

    (9, '15:00:00', '15:00:00', '11:00:00', '11:00:00', 5, 20, 220, 'RESORT', 'Coral Nha Trang Resort');