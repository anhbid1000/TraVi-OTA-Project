-- Bulk seed: 50 hotels + 50 restaurants and dependent catalog tables.

-- Ensure partner rows exist when matching users are present.
INSERT INTO doi_tac (id, ti_le_chiet_khau)
SELECT u.id, 10.0
FROM users u
WHERE u.id IN ('u-partner-1', 'u-partner-2', 'u-partner-3', 'u-partner-4', 'u-partner-5')
ON CONFLICT DO NOTHING;

-- Seed 50 real hotels with actual data
CREATE TEMP TABLE tmp_bulk_hotel_seed AS
WITH partner_pool AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM doi_tac
),
partner_count AS (
    SELECT COUNT(*) AS cnt FROM partner_pool
)
SELECT 1 i, '001' code, pp.id partner_id, 'Da Lat' city, 'Phan Boi Chau' district, 11.9465 lat, 108.4376 lon FROM partner_pool pp WHERE pp.rn = 1 UNION ALL
SELECT 2, '002', pp.id, 'Da Lat', 'Phan Nhu Thach', 11.9421, 108.4342 FROM partner_pool pp WHERE pp.rn = 2 UNION ALL
SELECT 3, '003', pp.id, 'Da Lat', 'Phan Boi Chau', 11.9458, 108.4381 FROM partner_pool pp WHERE pp.rn = 3 UNION ALL
SELECT 4, '004', pp.id, 'Da Lat', 'Ho Tuy Lam', 11.8972, 108.4305 FROM partner_pool pp WHERE pp.rn = 4 UNION ALL
SELECT 5, '005', pp.id, 'Da Lat', 'Tran Phu', 11.9392, 108.4419 FROM partner_pool pp WHERE pp.rn = 5 UNION ALL
SELECT 6, '006', (SELECT id FROM partner_pool WHERE rn = 1), 'Da Lat', 'Nguyen Chi Thanh', 11.9415, 108.4369 UNION ALL
SELECT 7, '007', (SELECT id FROM partner_pool WHERE rn = 2), 'Da Lat', 'Trieu Viet Vuong', 11.9225, 108.4312 UNION ALL
SELECT 8, '008', (SELECT id FROM partner_pool WHERE rn = 3), 'Nha Trang', 'Vinh Hoa', 12.2854, 109.2173 UNION ALL
SELECT 9, '009', (SELECT id FROM partner_pool WHERE rn = 4), 'Nha Trang', 'Loc Tho', 12.2392, 109.1945 UNION ALL
SELECT 10, '010', (SELECT id FROM partner_pool WHERE rn = 5), 'Nha Trang', 'Loc Tho', 12.2425, 109.1968 UNION ALL
SELECT 11, '011', (SELECT id FROM partner_pool WHERE rn = 1), 'Nha Trang', 'Vinh Hoa', 12.2854, 109.2173 UNION ALL
SELECT 12, '012', (SELECT id FROM partner_pool WHERE rn = 2), 'Nha Trang', 'Vinh Nguyen', 12.2612, 109.1865 UNION ALL
SELECT 13, '013', (SELECT id FROM partner_pool WHERE rn = 3), 'Nha Trang', 'Loc Tho', 12.2356, 109.1932 UNION ALL
SELECT 14, '014', (SELECT id FROM partner_pool WHERE rn = 4), 'Nha Trang', 'Loc Tho', 12.2418, 109.1962 UNION ALL
SELECT 15, '015', (SELECT id FROM partner_pool WHERE rn = 5), 'Da Nang', 'Hai Chau', 16.0715, 108.2241 UNION ALL
SELECT 16, '016', (SELECT id FROM partner_pool WHERE rn = 1), 'Da Nang', 'Son Tra', 16.0662, 108.2435 UNION ALL
SELECT 17, '017', (SELECT id FROM partner_pool WHERE rn = 2), 'Da Nang', 'Ngu Hanh Son', 16.0521, 108.2472 UNION ALL
SELECT 18, '018', (SELECT id FROM partner_pool WHERE rn = 3), 'Da Nang', 'Son Tra', 16.0615, 108.2458 UNION ALL
SELECT 19, '019', (SELECT id FROM partner_pool WHERE rn = 4), 'Da Nang', 'Ngu Hanh Son', 16.0541, 108.2469 UNION ALL
SELECT 20, '020', (SELECT id FROM partner_pool WHERE rn = 5), 'Da Nang', 'Hai Chau', 16.0754, 108.2235 UNION ALL
SELECT 21, '021', (SELECT id FROM partner_pool WHERE rn = 1), 'Da Nang', 'Ngu Hanh Son', 16.0526, 108.2471 UNION ALL
SELECT 22, '022', (SELECT id FROM partner_pool WHERE rn = 2), 'Ha Noi', 'Hoan Kiem', 21.0331, 105.8492 UNION ALL
SELECT 23, '023', (SELECT id FROM partner_pool WHERE rn = 3), 'Ha Noi', 'Hoan Kiem', 21.0346, 105.8485 UNION ALL
SELECT 24, '024', (SELECT id FROM partner_pool WHERE rn = 4), 'Ha Noi', 'Hoan Kiem', 21.0368, 105.8521 UNION ALL
SELECT 25, '025', (SELECT id FROM partner_pool WHERE rn = 5), 'Ha Noi', 'Tay Ho', 21.0635, 105.8284 UNION ALL
SELECT 26, '026', (SELECT id FROM partner_pool WHERE rn = 1), 'Ha Noi', 'Hoan Kiem', 21.0298, 105.8479 UNION ALL
SELECT 27, '027', (SELECT id FROM partner_pool WHERE rn = 2), 'Ha Noi', 'Ba Dinh', 21.0254, 105.8192 UNION ALL
SELECT 28, '028', (SELECT id FROM partner_pool WHERE rn = 3), 'Ha Noi', 'Ba Dinh', 21.0319, 105.8118 UNION ALL
SELECT 29, '029', (SELECT id FROM partner_pool WHERE rn = 4), 'Vung Tau', 'Ward 2', 10.3342, 107.0985 UNION ALL
SELECT 30, '030', (SELECT id FROM partner_pool WHERE rn = 5), 'Vung Tau', 'Ward 2', 10.3298, 107.1001 UNION ALL
SELECT 31, '031', (SELECT id FROM partner_pool WHERE rn = 1), 'Vung Tau', 'Thang Tam', 10.3315, 107.0991 UNION ALL
SELECT 32, '032', (SELECT id FROM partner_pool WHERE rn = 2), 'Vung Tau', 'Ward 1', 10.3612, 107.0725 UNION ALL
SELECT 33, '033', (SELECT id FROM partner_pool WHERE rn = 3), 'Vung Tau', 'Thang Tam', 10.3325, 107.1021 UNION ALL
SELECT 34, '034', (SELECT id FROM partner_pool WHERE rn = 4), 'Vung Tau', 'Thang Tam', 10.3331, 107.1012 UNION ALL
SELECT 35, '035', (SELECT id FROM partner_pool WHERE rn = 5), 'Vung Tau', 'Ward 1', 10.3495, 107.0734 UNION ALL
SELECT 36, '036', (SELECT id FROM partner_pool WHERE rn = 1), 'Ho Chi Minh', 'Quan 1', 10.7725, 106.6952 UNION ALL
SELECT 37, '037', (SELECT id FROM partner_pool WHERE rn = 2), 'Ho Chi Minh', 'Quan 1', 10.7712, 106.6912 UNION ALL
SELECT 38, '038', (SELECT id FROM partner_pool WHERE rn = 3), 'Ho Chi Minh', 'Quan 1', 10.7705, 106.6938 UNION ALL
SELECT 39, '039', (SELECT id FROM partner_pool WHERE rn = 4), 'Ho Chi Minh', 'Quan 1', 10.7692, 106.7011 UNION ALL
SELECT 40, '040', (SELECT id FROM partner_pool WHERE rn = 5), 'Ho Chi Minh', 'Quan 1', 10.7688, 106.6994 UNION ALL
SELECT 41, '041', (SELECT id FROM partner_pool WHERE rn = 1), 'Ho Chi Minh', 'Quan 3', 10.7892, 106.6854 UNION ALL
SELECT 42, '042', (SELECT id FROM partner_pool WHERE rn = 2), 'Ho Chi Minh', 'Quan 1', 10.7718, 106.6955 UNION ALL
SELECT 43, '043', (SELECT id FROM partner_pool WHERE rn = 3), 'Ho Chi Minh', 'Quan 3', 10.7812, 106.6908 UNION ALL
SELECT 44, '044', (SELECT id FROM partner_pool WHERE rn = 4), 'Phu Quoc', 'Ganh Dau', 10.3341, 103.8562 UNION ALL
SELECT 45, '045', (SELECT id FROM partner_pool WHERE rn = 5), 'Phu Quoc', 'Duong To', 10.1542, 103.9682 UNION ALL
SELECT 46, '046', (SELECT id FROM partner_pool WHERE rn = 1), 'Phu Quoc', 'Ganh Gio', 10.2522, 103.9341 UNION ALL
SELECT 47, '047', (SELECT id FROM partner_pool WHERE rn = 2), 'Phu Quoc', 'Duong Dong', 10.2084, 103.9615 UNION ALL
SELECT 48, '048', (SELECT id FROM partner_pool WHERE rn = 3), 'Phu Quoc', 'Ganh Dau', 10.3368, 103.8545 UNION ALL
SELECT 49, '049', (SELECT id FROM partner_pool WHERE rn = 4), 'Phu Quoc', 'Duong Dong', 10.2045, 103.9632 UNION ALL
SELECT 50, '050', (SELECT id FROM partner_pool WHERE rn = 5), 'Phu Quoc', 'Duong To', 10.1412, 103.9715;

CREATE TEMP TABLE tmp_bulk_rest_seed AS
WITH partner_pool AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM doi_tac
),
partner_count AS (
    SELECT COUNT(*) AS cnt FROM partner_pool
)
SELECT
    gs AS i,
    LPAD(gs::text, 3, '0') AS code,
    pp.id AS partner_id,
    CASE (gs - 1) % 5
        WHEN 0 THEN 'Da Lat'
        WHEN 1 THEN 'Da Nang'
        WHEN 2 THEN 'Nha Trang'
        WHEN 3 THEN 'Ho Chi Minh'
        ELSE 'Phu Quoc'
    END AS city,
    CASE (gs - 1) % 5
        WHEN 0 THEN 'Trung tam Da Lat'
        WHEN 1 THEN 'Hai Chau'
        WHEN 2 THEN 'Vinh Nguyen'
        WHEN 3 THEN 'Quan 3'
        ELSE 'An Thoi'
    END AS district,
    CASE (gs - 1) % 5
        WHEN 0 THEN 11.9410
        WHEN 1 THEN 16.0620
        WHEN 2 THEN 12.2420
        WHEN 3 THEN 10.7720
        ELSE 10.2320
    END + ((gs % 9) * 0.001) AS lat,
    CASE (gs - 1) % 5
        WHEN 0 THEN 108.4420
        WHEN 1 THEN 108.2260
        WHEN 2 THEN 109.1930
        WHEN 3 THEN 106.7040
        ELSE 103.9720
    END + ((gs % 9) * 0.001) AS lon
FROM generate_series(1, 50) gs
JOIN partner_count pc ON pc.cnt > 0
JOIN partner_pool pp ON pp.rn = (((gs + 1) % pc.cnt) + 1);

INSERT INTO ho_so_kinh_doanh (
    id_ho_so,
    doi_tac_id,
    loai_dich_vu,
    ten_co_so,
    ma_so_thue,
    giay_phep_kinh_doanh,
    toa_do_gps,
    sdt_lien_he,
    email_lien_he,
    dia_chi,
    thanh_pho,
    kinh_do,
    vi_do,
    trang_thai_hoat_dong
)
SELECT
    'bhsk-' || s.code,
    s.partner_id,
    'KHACH_SAN',
    CASE s.i
        WHEN 1 THEN 'Hotel Colline Đà Lạt'
        WHEN 2 THEN 'Hotel Iris Đà Lạt'
        WHEN 3 THEN 'Mường Thanh Holiday Đà Lạt'
        WHEN 4 THEN 'Terracotta Hotel & Resort Dalat'
        WHEN 5 THEN 'Khách Sạn Đà Lạt Palace Heritage'
        WHEN 6 THEN 'Khách Sạn Tulip 2 Đà Lạt'
        WHEN 7 THEN 'Khách sạn Stillus Boutique Đà Lạt'
        WHEN 8 THEN 'Mường Thanh Grand Nha Trang Hotel'
        WHEN 9 THEN 'Khách sạn Panama Nha Trang'
        WHEN 10 THEN 'Khách sạn Havana Nha Trang'
        WHEN 11 THEN 'Champa Island Nha Trang - Resort Hotel & Spa'
        WHEN 12 THEN 'Champa Island Nha Trang - Resort Hotel & Spa'
        WHEN 13 THEN 'Seana Hotel Nha Trang'
        WHEN 14 THEN 'Khách Sạn InterContinental Nha Trang'
        WHEN 15 THEN 'Hilton Đà Nẵng'
        WHEN 16 THEN 'Dương Gia Hotel Đà Nẵng'
        WHEN 17 THEN 'TMS Hotel Danang Beach'
        WHEN 18 THEN 'Sala Danang Beach Hotel'
        WHEN 19 THEN 'Haian Beach Hotel & Spa'
        WHEN 20 THEN 'Novotel Danang Premier Han River'
        WHEN 21 THEN 'Rosamia Đà Nẵng Hotel'
        WHEN 22 THEN 'Hanoi Le Grand Hotel'
        WHEN 23 THEN 'Ha Noi Group Nest Hotel'
        WHEN 24 THEN 'Viet Vintage Hostel and Travel'
        WHEN 25 THEN 'The Autumn Homestay'
        WHEN 26 THEN 'Heritage Corner Hostel'
        WHEN 27 THEN 'Khách sạn Hà Nội (Hanoi Hotel)'
        WHEN 28 THEN 'Khách sạn Lotte Hà Nội'
        WHEN 29 THEN 'Aloha Hotel Vũng Tàu'
        WHEN 30 THEN 'Khách Sạn Biển Ngọc Vũng Tàu'
        WHEN 31 THEN 'Premier Pearl Hotel Vũng Tàu'
        WHEN 32 THEN 'Seaside Resort Vũng Tàu'
        WHEN 33 THEN 'Khách sạn Imperial Vũng Tàu'
        WHEN 34 THEN 'Khách sạn ibis Styles Vũng Tàu'
        WHEN 35 THEN 'Khách sạn Fusion Suites Vũng Tàu'
        WHEN 36 THEN 'La Siesta Premium Saigon'
        WHEN 37 THEN 'Khách Sạn Thiên Phú'
        WHEN 38 THEN 'Khách Sạn Hòa Bình'
        WHEN 39 THEN 'Khách Sạn Anh Anh'
        WHEN 40 THEN 'Hotel Hoa Phượng'
        WHEN 41 THEN 'Khách sạn La Vela Saigon'
        WHEN 42 THEN 'Khách sạn Silverland Bến Thành'
        WHEN 43 THEN 'Mai House Saigon Hotel'
        WHEN 44 THEN 'VinHolidays Fiesta Phu Quoc'
        WHEN 45 THEN 'Golden Dragon Hotel Phu Quoc'
        WHEN 46 THEN 'The Shells Resort and Spa Phú Quốc'
        WHEN 47 THEN 'Khách Sạn Dương Đông Hotel'
        WHEN 48 THEN 'Vinpearl Resort & Spa Phú Quốc'
        WHEN 49 THEN 'Hiệp Thoại Hotel Phú Quốc'
        WHEN 50 THEN 'Novotel Phú Quốc Resort'
    END,
    CASE s.i
        WHEN 1 THEN '5801423451' WHEN 2 THEN '5801423452' WHEN 3 THEN '5801423453' WHEN 4 THEN '5801423454' WHEN 5 THEN '5801423455'
        WHEN 6 THEN '5801423456' WHEN 7 THEN '5801423457' WHEN 8 THEN '5601423458' WHEN 9 THEN '5601423459' WHEN 10 THEN '5601423460'
        WHEN 11 THEN '5601423461' WHEN 12 THEN '5601423462' WHEN 13 THEN '5601423463' WHEN 14 THEN '5601423464' WHEN 15 THEN '0401423465'
        WHEN 16 THEN '0401423466' WHEN 17 THEN '0401423467' WHEN 18 THEN '0401423468' WHEN 19 THEN '0401423469' WHEN 20 THEN '0401423470'
        WHEN 21 THEN '0401423471' WHEN 22 THEN '0101423472' WHEN 23 THEN '0101423473' WHEN 24 THEN '0101423474' WHEN 25 THEN '0101423475'
        WHEN 26 THEN '0101423476' WHEN 27 THEN '0101423477' WHEN 28 THEN '0101423478' WHEN 29 THEN '3501423479' WHEN 30 THEN '3501423480'
        WHEN 31 THEN '3501423481' WHEN 32 THEN '3501423482' WHEN 33 THEN '3501423483' WHEN 34 THEN '3501423484' WHEN 35 THEN '3501423485'
        WHEN 36 THEN '0301423486' WHEN 37 THEN '0301423487' WHEN 38 THEN '0301423488' WHEN 39 THEN '0301423489' WHEN 40 THEN '0301423490'
        WHEN 41 THEN '0301423491' WHEN 42 THEN '0301423492' WHEN 43 THEN '0301423493' WHEN 44 THEN '1701423494' WHEN 45 THEN '1701423495'
        WHEN 46 THEN '1701423496' WHEN 47 THEN '1701423497' WHEN 48 THEN '1701423498' WHEN 49 THEN '1701423499' WHEN 50 THEN '1701423500'
    END,
    'GPKD-' || s.code,
    ROUND(s.lat::numeric, 6)::text || ',' || ROUND(s.lon::numeric, 6)::text,
    CASE s.i
        WHEN 1 THEN '0908123001' WHEN 2 THEN '0908123002' WHEN 3 THEN '0908123003' WHEN 4 THEN '0908123004' WHEN 5 THEN '0908123005'
        WHEN 6 THEN '0908123006' WHEN 7 THEN '0908123007' WHEN 8 THEN '0908123008' WHEN 9 THEN '0908123009' WHEN 10 THEN '0908123010'
        WHEN 11 THEN '0908123011' WHEN 12 THEN '0908123012' WHEN 13 THEN '0908123013' WHEN 14 THEN '0908123014' WHEN 15 THEN '0908123015'
        WHEN 16 THEN '0908123016' WHEN 17 THEN '0908123017' WHEN 18 THEN '0908123018' WHEN 19 THEN '0908123019' WHEN 20 THEN '0908123020'
        WHEN 21 THEN '0908123021' WHEN 22 THEN '0908123022' WHEN 23 THEN '0908123023' WHEN 24 THEN '0908123024' WHEN 25 THEN '0908123025'
        WHEN 26 THEN '0908123026' WHEN 27 THEN '0908123027' WHEN 28 THEN '0908123028' WHEN 29 THEN '0908123029' WHEN 30 THEN '0908123030'
        WHEN 31 THEN '0908123031' WHEN 32 THEN '0908123032' WHEN 33 THEN '0908123033' WHEN 34 THEN '0908123034' WHEN 35 THEN '0908123035'
        WHEN 36 THEN '0908123036' WHEN 37 THEN '0908123037' WHEN 38 THEN '0908123038' WHEN 39 THEN '0908123039' WHEN 40 THEN '0908123040'
        WHEN 41 THEN '0908123041' WHEN 42 THEN '0908123042' WHEN 43 THEN '0908123043' WHEN 44 THEN '0908123044' WHEN 45 THEN '0908123045'
        WHEN 46 THEN '0908123046' WHEN 47 THEN '0908123047' WHEN 48 THEN '0908123048' WHEN 49 THEN '0908123049' WHEN 50 THEN '0908123050'
    END,
    CASE s.i
        WHEN 1 THEN 'hotel.colline.dl@bulk.travi.vn' WHEN 2 THEN 'hotel.iris.dl@bulk.travi.vn' WHEN 3 THEN 'hotel.muongthanh.dl@bulk.travi.vn'
        WHEN 4 THEN 'hotel.terracotta.dl@bulk.travi.vn' WHEN 5 THEN 'hotel.dalatpalace.dl@bulk.travi.vn' WHEN 6 THEN 'hotel.tulip2.dl@bulk.travi.vn'
        WHEN 7 THEN 'hotel.stillus.dl@bulk.travi.vn' WHEN 8 THEN 'hotel.muongthanh.nt@bulk.travi.vn' WHEN 9 THEN 'hotel.panama.nt@bulk.travi.vn'
        WHEN 10 THEN 'hotel.havana.nt@bulk.travi.vn' WHEN 11 THEN 'resort.champaisland.nt@bulk.travi.vn' WHEN 12 THEN 'hotel.seana.nt@bulk.travi.vn'
        WHEN 13 THEN 'hotel.intercontinental.nt@bulk.travi.vn' WHEN 14 THEN 'hotel.sheraton.nt@bulk.travi.vn' WHEN 15 THEN 'hotel.hilton.dn@bulk.travi.vn'
        WHEN 16 THEN 'hotel.duonggia.dn@bulk.travi.vn' WHEN 17 THEN 'hotel.tms.dn@bulk.travi.vn' WHEN 18 THEN 'hotel.sala.dn@bulk.travi.vn'
        WHEN 19 THEN 'hotel.haian.dn@bulk.travi.vn' WHEN 20 THEN 'hotel.novotel.dn@bulk.travi.vn' WHEN 21 THEN 'hotel.rosamia.dn@bulk.travi.vn'
        WHEN 22 THEN 'hotel.legrand.hn@bulk.travi.vn' WHEN 23 THEN 'hotel.groupnest.hn@bulk.travi.vn' WHEN 24 THEN 'hotel.vietvintage.hn@bulk.travi.vn'
        WHEN 25 THEN 'hotel.autumn.hn@bulk.travi.vn' WHEN 26 THEN 'hotel.heritage.hn@bulk.travi.vn' WHEN 27 THEN 'hotel.hanoihotel.hn@bulk.travi.vn'
        WHEN 28 THEN 'hotel.lotte.hn@bulk.travi.vn' WHEN 29 THEN 'hotel.aloha.vt@bulk.travi.vn' WHEN 30 THEN 'hotel.bienngoc.vt@bulk.travi.vn'
        WHEN 31 THEN 'hotel.premierpearl.vt@bulk.travi.vn' WHEN 32 THEN 'resort.seaside.vt@bulk.travi.vn' WHEN 33 THEN 'hotel.imperial.vt@bulk.travi.vn'
        WHEN 34 THEN 'hotel.ibis.vt@bulk.travi.vn' WHEN 35 THEN 'hotel.fusionsuites.vt@bulk.travi.vn' WHEN 36 THEN 'hotel.lasiesta.hcm@bulk.travi.vn'
        WHEN 37 THEN 'hotel.thienphu.hcm@bulk.travi.vn' WHEN 38 THEN 'hotel.hoabinh.hcm@bulk.travi.vn' WHEN 39 THEN 'hotel.anhanh.hcm@bulk.travi.vn'
        WHEN 40 THEN 'hotel.hoaphuong.hcm@bulk.travi.vn' WHEN 41 THEN 'hotel.lavela.hcm@bulk.travi.vn' WHEN 42 THEN 'hotel.silverland.hcm@bulk.travi.vn'
        WHEN 43 THEN 'hotel.maihouse.hcm@bulk.travi.vn' WHEN 44 THEN 'hotel.vinholidays.pq@bulk.travi.vn' WHEN 45 THEN 'hotel.goldendragon.pq@bulk.travi.vn'
        WHEN 46 THEN 'resort.theshells.pq@bulk.travi.vn' WHEN 47 THEN 'hotel.duongdong.pq@bulk.travi.vn' WHEN 48 THEN 'resort.vinpearlspa.pq@bulk.travi.vn'
        WHEN 49 THEN 'hotel.hiepthoai.pq@bulk.travi.vn' WHEN 50 THEN 'resort.novotel.pq@bulk.travi.vn'
    END,
    CASE s.i
        WHEN 1 THEN '10 Phan Bội Châu, Phường 2, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 2 THEN '20 Phan Như Thạch, Phường 1, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 3 THEN 'Số 42 Phan Bội Châu, Phường 1, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 4 THEN 'Phân khu chức năng 7.9 KDL hồ Tuyền Lâm, Phường 3, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 5 THEN '2 Trần Phú, Phường 3, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 6 THEN '14 Nguyễn Chí Thanh, Phường 1, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 7 THEN '107 Đường Triệu Việt Vương, Phường 3, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 8 THEN '6 Dương Hiến Quyền, Vĩnh Hòa, Nha Trang, Khánh Hòa'
        WHEN 9 THEN '18 Hùng Vương, Lộc Thọ, Nha Trang, Khánh Hòa'
        WHEN 10 THEN '38 Trần Phú, Lộc Thọ, Nha Trang, Khánh Hòa'
        WHEN 11 THEN '304 2 Tháng 4, Vĩnh Phước, Nha Trang, Khánh Hòa'
        WHEN 12 THEN '4H-5H Quân Trấn, Hùng Vương, Lộc Thọ, Nha Trang, Khánh Hòa'
        WHEN 13 THEN '32-34 Trần Phú, Phường Lộc Thọ, Thành phố Nha Trang, Khánh Hòa'
        WHEN 14 THEN '26-28 Trần Phú, Phường Lộc Thọ, Thành phố Nha Trang, Khánh Hòa'
        WHEN 15 THEN '50 Bạch Đằng, Hải Châu 1, Hải Châu, Đà Nẵng'
        WHEN 16 THEN '06-08 Phạm Thiều, Phước Mỹ, Sơn Trà, Đà Nẵng'
        WHEN 17 THEN '292 Võ Nguyên Giáp, Mỹ An, Ngũ Hành Sơn, Đà Nẵng'
        WHEN 18 THEN '36-38 Lâm Hoành, Phước Mỹ, Sơn Trà, Đà Nẵng'
        WHEN 19 THEN '278 Võ Nguyên Giáp, Bắc Mỹ Phú, Ngũ Hành Sơn, Đà Nẵng'
        WHEN 20 THEN '36 Bạch Đằng, Thạch Thang, Hải Châu, Đà Nẵng'
        WHEN 21 THEN '282 Võ Nguyên Giáp, Mỹ An, Ngũ Hành Sơn, Đà Nẵng'
        WHEN 22 THEN '44-46 Hàng Hòm, Hàng Gai, Hoàn Kiếm, Hà Nội'
        WHEN 23 THEN '5 Hàng Phèn, Hàng Bồ, Hoàn Kiếm, Hà Nội'
        WHEN 24 THEN '19 Hàng Chĩnh, Hàng Buồm, Hoàn Kiếm, Hà Nội'
        WHEN 25 THEN '32B Xuân Diệu, Quảng An, Tây Hồ, Hà Nội'
        WHEN 26 THEN '20 Ngõ Huyện, Hàng Trống, Hoàn Kiếm, Hà Nội'
        WHEN 27 THEN 'D8 Giảng Võ, Ba Đình, Hà Nội'
        WHEN 28 THEN '54 Liễu Giai, Cống Vị, Ba Đình, Hà Nội'
        WHEN 29 THEN '93 Thùy Vân, Phường 2, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 30 THEN '23 Thùy Vân, Phường 2, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 31 THEN '69 Thùy Vân, Phường 2, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 32 THEN '28 Trần Phú, Phường 1, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 33 THEN '159 Thùy Vân, Thắng Tam, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 34 THEN '117 Thùy Vân, Thắng Tam, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 35 THEN '2 Trương Công Định, Phường 1, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 36 THEN '180 Lý Tự Trọng, Bến Thành, Quận 1, Thành phố Hồ Chí Minh'
        WHEN 37 THEN '44 Bùi Thị Xuân, Bến Thành, Quận 1, Thành phố Hồ Chí Minh'
        WHEN 38 THEN '307 Lý Tự Trọng, Bến Thành, Quận 1, Thành phố Hồ Chí Minh'
        WHEN 39 THEN '36 Nguyễn Thái Bình, Phường Nguyễn Thái Bình, Quận 1, Thành phố Hồ Chí Minh'
        WHEN 40 THEN '55/29 Lê Thị Hồng Gấm, Nguyễn Thái Bình, Quận 1, Thành phố Hồ Chí Minh'
        WHEN 41 THEN '280 Nam Kỳ Khởi Nghĩa, Phường 8, Quận 3, Thành phố Hồ Chí Minh'
        WHEN 42 THEN '14 - 16 Đường Lê Lai, Phường Bến Thành, Quận 1, Thành phố Hồ Chí Minh'
        WHEN 43 THEN '157 Nam Kỳ Khởi Nghĩa, Võ Thị Sáu, Quận 3, Thành phố Hồ Chí Minh'
        WHEN 44 THEN 'Khu Bãi Dài, Gành Dầu, Phú Quốc, Kiên Giang'
        WHEN 45 THEN 'Đường Bào, Dương Tơ, Phú Quốc, Kiên Giang'
        WHEN 46 THEN 'Biển Gành Gió, Dương Đông, Phú Quốc, Kiên Giang'
        WHEN 47 THEN '127D Đường Trần Hưng Đạo, Dương Đông, Phú Quốc, Kiên Giang'
        WHEN 48 THEN 'Khu Bãi Dài, Gành Dầu, Phú Quốc, Kiên Giang'
        WHEN 49 THEN '65 Trần Hưng Đạo, Dương Đông, Phú Quốc, Kiên Giang'
        WHEN 50 THEN 'Khu phố 5, Đường Bào, Xã Dương Tơ, Thành phố Phú Quốc, Kiên Giang'
    END,
    s.city,
    s.lon,
    s.lat,
    'DANG_HOAT_DONG'
FROM tmp_bulk_hotel_seed s
ON CONFLICT DO NOTHING;

INSERT INTO ho_so_kinh_doanh (
    id_ho_so,
    doi_tac_id,
    loai_dich_vu,
    ten_co_so,
    ma_so_thue,
    giay_phep_kinh_doanh,
    toa_do_gps,
    sdt_lien_he,
    email_lien_he,
    dia_chi,
    thanh_pho,
    kinh_do,
    vi_do,
    trang_thai_hoat_dong
)
SELECT
    'bhsn-' || s.code,
    s.partner_id,
    'NHA_HANG',
    'TraVi Bistro ' || s.code,
    'BNH' || s.code,
    'GPKD-BNH-' || s.code,
    ROUND(s.lat::numeric, 6)::text || ',' || ROUND(s.lon::numeric, 6)::text,
    '0918' || LPAD(s.i::text, 6, '0'),
    'restaurant' || s.code || '@bulk.travi.vn',
    s.i || ' ' || s.district,
    s.city,
    s.lon,
    s.lat,
    'DANG_HOAT_DONG'
FROM tmp_bulk_rest_seed s
ON CONFLICT DO NOTHING;

INSERT INTO tai_san (
    id_tai_san,
    ho_so_kinh_doanh_id,
    mo_ta,
    trang_thai,
    gia_co_ban,
    is_dynamic_pricing,
    rating_average,
    review_count,
    dia_chi,
    thanh_pho,
    kinh_do,
    vi_do
)
SELECT
    'bask-' || s.code,
    'bhsk-' || s.code,
    CASE s.i
        WHEN 1 THEN 'Khách sạn du lịch tổng hợp tại Đà Lạt với kiến trúc hình khối độc đáo ngay trung tâm.'
        WHEN 2 THEN 'Khách sạn du lịch tổng hợp tại Đà Lạt mang phong cách Châu Âu hiện đại.'
        WHEN 3 THEN 'Khách sạn du lịch tổng hợp tại Đà Lạt thuộc chuỗi Mường Thanh nổi tiếng.'
        WHEN 4 THEN 'Khách sạn du lịch tổng hợp tại Đà Lạt nằm ẩn mình dưới rừng thông bên hồ Tuyền Lâm.'
        WHEN 5 THEN 'Khách sạn du lịch tổng hợp tại Đà Lạt mang phong cách hoàng gia Pháp cổ kính.'
        WHEN 6 THEN 'Khách sạn du lịch tổng hợp tại Đà Lạt giá bình dân ngay sát chợ đêm.'
        WHEN 7 THEN 'Khách sạn du lịch tổng hợp tại Đà Lạt phong cách cổ điển lãng mạn.'
        WHEN 8 THEN 'Khách sạn sang trọng có phòng ốc trang nhã, 3 nhà hàng, spa và bể bơi ngoài trời.'
        WHEN 9 THEN 'Khách sạn du lịch tổng hợp tại Nha Trang gần biển phong cách hiện đại.'
        WHEN 10 THEN 'Khách sạn sang trọng với phòng ốc hiện đại, có lối đi thẳng ra biển, nhà hàng, khu spa và bể bơi.'
        WHEN 11 THEN 'Resort ấm cúng nằm trên đảo, có nhà hàng, bể bơi, spa ngoài trời và cầu đi bộ treo đèn lồng.'
        WHEN 12 THEN 'Các phòng và dãy phòng thoáng đãng trong một khách sạn cao cấp với hồ bơi trên sân thượng, nhà hàng trang nhã và phòng tập thể dục.'
        WHEN 13 THEN 'Khách sạn đẳng cấp quốc tế 5 sao nằm sát bờ biển Nha Trang.'
        WHEN 14 THEN 'Khách sạn cao cấp với 100% phòng hướng biển tại trung tâm Trần Phú.'
        WHEN 15 THEN 'Khách sạn du lịch tổng hợp tại Đà Nẵng đẳng cấp 5 sao nằm bên bờ sông Hàn thơ mộng.'
        WHEN 16 THEN 'Khách sạn du lịch tổng hợp tại Đà Nẵng tiêu chuẩn gần biển Mỹ Khê.'
        WHEN 17 THEN 'Khách sạn du lịch tổng hợp tại Đà Nẵng có bể bơi vô cực cao nhất thành phố.'
        WHEN 18 THEN 'Khách sạn du lịch tổng hợp tại Đà Nẵng phong cách tinh tế hướng biển.'
        WHEN 19 THEN 'Khách sạn du lịch tổng hợp tại Đà Nẵng cung cấp dịch vụ nghỉ dưỡng cao cấp.'
        WHEN 20 THEN 'Khách sạn thương gia đẳng cấp bên sông Hàn lý tưởng cho khách công vụ.'
        WHEN 21 THEN 'Khách sạn tiêu chuẩn quốc tế 5 sao nằm ngay mặt biển Mỹ Khê.'
        WHEN 22 THEN 'Khách sạn cổ kính mang nét đặc trưng của Phố cổ Hà Nội.'
        WHEN 23 THEN 'Khách sạn ấm cúng thích hợp cho nhóm bạn du lịch tại Hà Nội.'
        WHEN 24 THEN 'Hostel phong cách vintage mang hơi thở Hà Nội xưa.'
        WHEN 25 THEN 'Không gian homestay nhẹ nhàng tĩnh lặp bên khu vực Hồ Tây.'
        WHEN 26 THEN 'Điểm dừng chân lý tưởng cho khách ba lô nước ngoài gần Nhà Thờ Lớn.'
        WHEN 27 THEN 'Khách sạn liên doanh quốc tế đầu tiên tại thủ đô nhìn ra hồ Giảng Võ.'
        WHEN 28 THEN 'Khách sạn 5 sao sang trọng bậc nhất tọa lạc tại những tầng cao nhất tòa nhà Lotte Center.'
        WHEN 29 THEN 'Khách sạn du lịch tổng hợp tại Vũng Tàu nằm ngay trục đường bãi sau náo nhiệt.'
        WHEN 30 THEN 'Phòng ốc có không gian thư thái gần bãi biển, có chỗ đậu xe và bữa sáng miễn phí.'
        WHEN 31 THEN 'Khách sạn đẹp mắt có sân thượng, bể bơi vô cực, quầy bar, nhà hàng, spa và phòng tập thể dục.'
        WHEN 32 THEN 'Resort tựa lưng vào núi hướng nhìn ra biển cực kỳ trong lành.'
        WHEN 33 THEN 'Khách sạn du lịch tổng hợp tại Vũng Tàu mang phong cách kiến trúc hoàng gia châu Âu Victoria cổ điển.'
        WHEN 34 THEN 'Khách sạn phong cách trẻ trung năng động màu sắc tươi sáng quốc tế.'
        WHEN 35 THEN 'Căn hộ dịch vụ cao cấp có thiết kế sáng tạo và hồ bơi tràn bờ trên cao.'
        WHEN 36 THEN 'Các phòng nghỉ sang trọng, một số phòng có sân hiên, cùng với quầy bar cocktail trên tầng thượng và hồ bơi vô cực.'
        WHEN 37 THEN 'Khách sạn phân khúc bình dân phù hợp cho khách du lịch tự túc tại trung tâm.'
        WHEN 38 THEN 'Khách sạn du lịch tiêu chuẩn tiết kiệm ngay gần Chợ Bến Thành.'
        WHEN 39 THEN 'Khách sạn nhỏ gọn tiện lợi di chuyển tới các khu trung tâm tài chính.'
        WHEN 40 THEN 'Nhà nghỉ gia đình, yên tĩnh, sạch sẽ giữa trung tâm Quận 1 năng động.'
        WHEN 41 THEN 'Khách sạn biểu tượng nổi tiếng với bể bơi vô cực rộng lớn trên tầng thượng ngắm toàn cảnh Sài Gòn.'
        WHEN 42 THEN 'Khách sạn boutique mang đậm chất nghệ thuật mộc mạc tinh tế cạnh công viên 23/9.'
        WHEN 43 THEN 'Khách sạn thiết kế theo trường phái kiến trúc Đông Dương đầy lãng mạn hoài cổ.'
        WHEN 44 THEN 'Khách sạn cao cấp có quầy bar, nhà hàng thời thượng, bể bơi ngoài trời và phòng ở hiện đại, ấm cúng.'
        WHEN 45 THEN 'Khách sạn mới mẻ khang trang phục vụ phân khúc nghỉ dưỡng tiện nghi.'
        WHEN 46 THEN 'Resort cao cấp sát biển có thiết kế hình vỏ sò độc bản uốn lượn.'
        WHEN 47 THEN 'Khách sạn nằm trên trục đường chính thuận tiện tham quan ăn uống.'
        WHEN 48 THEN 'Khu nghỉ dưỡng sang trọng gần biển có sân gôn và không gian ở tinh tế, công viên nước, nhà hàng và quán bar.'
        WHEN 49 THEN 'Khách sạn du lịch tổng hợp tại Phú Quốc phân khúc bình dân gần trung tâm thị trấn.'
        WHEN 50 THEN 'Khu nghỉ dưỡng sang trọng đẳng cấp quốc tế thương hiệu Novotel bên vịnh Thái Lan.'
    END,
    'SAN_SANG',
    CASE s.i
        WHEN 1 THEN 925000 WHEN 2 THEN 950000 WHEN 3 THEN 975000 WHEN 4 THEN 1000000 WHEN 5 THEN 1025000
        WHEN 6 THEN 1050000 WHEN 7 THEN 1075000 WHEN 8 THEN 873413 WHEN 9 THEN 1357684 WHEN 10 THEN 1566000
        WHEN 11 THEN 1099329 WHEN 12 THEN 710796 WHEN 13 THEN 1225000 WHEN 14 THEN 1250000 WHEN 15 THEN 3508305
        WHEN 16 THEN 1300000 WHEN 17 THEN 1325000 WHEN 18 THEN 1350000 WHEN 19 THEN 1375000 WHEN 20 THEN 1400000
        WHEN 21 THEN 1425000 WHEN 22 THEN 312723 WHEN 23 THEN 296666 WHEN 24 THEN 177947 WHEN 25 THEN 342995
        WHEN 26 THEN 234279 WHEN 27 THEN 1575000 WHEN 28 THEN 1600000 WHEN 29 THEN 599649 WHEN 30 THEN 605342
        WHEN 31 THEN 1925000 WHEN 32 THEN 1185780 WHEN 33 THEN 1725000 WHEN 34 THEN 1750000 WHEN 35 THEN 1775000
        WHEN 36 THEN 2863205 WHEN 37 THEN 206329 WHEN 38 THEN 351347 WHEN 39 THEN 221380 WHEN 40 THEN 321203
        WHEN 41 THEN 1925000 WHEN 42 THEN 1950000 WHEN 43 THEN 1975000 WHEN 44 THEN 1165341 WHEN 45 THEN 314829
        WHEN 46 THEN 1438052 WHEN 47 THEN 344937 WHEN 48 THEN 2014739 WHEN 49 THEN 2125000 WHEN 50 THEN 2150000
    END,
    CASE s.i
        WHEN 1 THEN false WHEN 2 THEN true WHEN 3 THEN false WHEN 4 THEN true WHEN 5 THEN false
        WHEN 6 THEN true WHEN 7 THEN false WHEN 8 THEN true WHEN 9 THEN false WHEN 10 THEN true
        WHEN 11 THEN false WHEN 12 THEN true WHEN 13 THEN false WHEN 14 THEN true WHEN 15 THEN false
        WHEN 16 THEN true WHEN 17 THEN false WHEN 18 THEN true WHEN 19 THEN false WHEN 20 THEN true
        WHEN 21 THEN false WHEN 22 THEN true WHEN 23 THEN false WHEN 24 THEN true WHEN 25 THEN false
        WHEN 26 THEN true WHEN 27 THEN false WHEN 28 THEN true WHEN 29 THEN false WHEN 30 THEN true
        WHEN 31 THEN false WHEN 32 THEN true WHEN 33 THEN false WHEN 34 THEN true WHEN 35 THEN false
        WHEN 36 THEN true WHEN 37 THEN false WHEN 38 THEN true WHEN 39 THEN false WHEN 40 THEN true
        WHEN 41 THEN false WHEN 42 THEN true WHEN 43 THEN false WHEN 44 THEN true WHEN 45 THEN false
        WHEN 46 THEN true WHEN 47 THEN false WHEN 48 THEN true WHEN 49 THEN false WHEN 50 THEN true
    END,
    CASE s.i
        WHEN 1 THEN 4.5 WHEN 2 THEN 4.2 WHEN 3 THEN 4.1 WHEN 4 THEN 4.4 WHEN 5 THEN 4.6
        WHEN 6 THEN 4.0 WHEN 7 THEN 4.3 WHEN 8 THEN 4.1 WHEN 9 THEN 4.8 WHEN 10 THEN 4.5
        WHEN 11 THEN 4.4 WHEN 12 THEN 4.7 WHEN 13 THEN 4.7 WHEN 14 THEN 4.6 WHEN 15 THEN 4.6
        WHEN 16 THEN 4.3 WHEN 17 THEN 4.7 WHEN 18 THEN 4.6 WHEN 19 THEN 4.5 WHEN 20 THEN 4.5
        WHEN 21 THEN 4.6 WHEN 22 THEN 4.0 WHEN 23 THEN 4.1 WHEN 24 THEN 4.6 WHEN 25 THEN 4.2
        WHEN 26 THEN 4.2 WHEN 27 THEN 4.0 WHEN 28 THEN 4.7 WHEN 29 THEN 4.8 WHEN 30 THEN 4.4
        WHEN 31 THEN 4.5 WHEN 32 THEN 4.2 WHEN 33 THEN 4.5 WHEN 34 THEN 4.4 WHEN 35 THEN 4.5
        WHEN 36 THEN 4.9 WHEN 37 THEN 3.1 WHEN 38 THEN 4.1 WHEN 39 THEN 3.5 WHEN 40 THEN 4.6
        WHEN 41 THEN 4.6 WHEN 42 THEN 4.5 WHEN 43 THEN 4.7 WHEN 44 THEN 4.9 WHEN 45 THEN 4.0
        WHEN 46 THEN 4.2 WHEN 47 THEN 4.6 WHEN 48 THEN 4.9 WHEN 49 THEN 4.1 WHEN 50 THEN 4.5
    END,
    CASE s.i
        WHEN 1 THEN 1250 WHEN 2 THEN 450 WHEN 3 THEN 850 WHEN 4 THEN 3200 WHEN 5 THEN 1100
        WHEN 6 THEN 350 WHEN 7 THEN 280 WHEN 8 THEN 970 WHEN 9 THEN 2418 WHEN 10 THEN 4815
        WHEN 11 THEN 4413 WHEN 12 THEN 951 WHEN 13 THEN 2100 WHEN 14 THEN 1850 WHEN 15 THEN 3153
        WHEN 16 THEN 364 WHEN 17 THEN 2500 WHEN 18 THEN 1800 WHEN 19 THEN 2200 WHEN 20 THEN 3400
        WHEN 21 THEN 1300 WHEN 22 THEN 448 WHEN 23 THEN 114 WHEN 24 THEN 190 WHEN 25 THEN 165
        WHEN 26 THEN 133 WHEN 27 THEN 950 WHEN 28 THEN 2800 WHEN 29 THEN 649 WHEN 30 THEN 211
        WHEN 31 THEN 3635 WHEN 32 THEN 1842 WHEN 33 THEN 5200 WHEN 34 THEN 2100 WHEN 35 THEN 1900
        WHEN 36 THEN 915 WHEN 37 THEN 84 WHEN 38 THEN 148 WHEN 39 THEN 148 WHEN 40 THEN 36
        WHEN 41 THEN 4200 WHEN 42 THEN 850 WHEN 43 THEN 1200 WHEN 44 THEN 14050 WHEN 45 THEN 79
        WHEN 46 THEN 2384 WHEN 47 THEN 267 WHEN 48 THEN 41333 WHEN 49 THEN 180 WHEN 50 THEN 3100
    END,
    CASE s.i
        WHEN 1 THEN '10 Phan Bội Châu, Phường 2, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 2 THEN '20 Phan Như Thạch, Phường 1, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 3 THEN 'Số 42 Phan Bội Châu, Phường 1, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 4 THEN 'Phân khu chức năng 7.9 KDL hồ Tuyền Lâm, Phường 3, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 5 THEN '2 Trần Phú, Phường 3, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 6 THEN '14 Nguyễn Chí Thanh, Phường 1, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 7 THEN '107 Đường Triệu Việt Vương, Phường 3, Thành phố Đà Lạt, Lâm Đồng'
        WHEN 8 THEN '6 Dương Hiến Quyền, Vĩnh Hòa, Nha Trang, Khánh Hòa'
        WHEN 9 THEN '18 Hùng Vương, Lộc Thọ, Nha Trang, Khánh Hòa'
        WHEN 10 THEN '38 Trần Phú, Lộc Thọ, Nha Trang, Khánh Hòa'
        WHEN 11 THEN '304 2 Tháng 4, Vĩnh Phước, Nha Trang, Khánh Hòa'
        WHEN 12 THEN '4H-5H Quân Trấn, Hùng Vương, Lộc Thọ, Nha Trang, Khánh Hòa'
        WHEN 13 THEN '32-34 Trần Phú, Phường Lộc Thọ, Thành phố Nha Trang, Khánh Hòa'
        WHEN 14 THEN '26-28 Trần Phú, Phường Lộc Thọ, Thành phố Nha Trang, Khánh Hòa'
        WHEN 15 THEN '50 Bạch Đằng, Hải Châu 1, Hải Châu, Đà Nẵng'
        WHEN 16 THEN '06-08 Phạm Thiều, Phước Mỹ, Sơn Trà, Đà Nẵng'
        WHEN 17 THEN '292 Võ Nguyên Giáp, Mỹ An, Ngũ Hành Sơn, Đà Nẵng'
        WHEN 18 THEN '36-38 Lâm Hoành, Phước Mỹ, Sơn Trà, Đà Nẵng'
        WHEN 19 THEN '278 Võ Nguyên Giáp, Bắc Mỹ Phú, Ngũ Hành Sơn, Đà Nẵng'
        WHEN 20 THEN '36 Bạch Đằng, Thạch Thang, Hải Châu, Đà Nẵng'
        WHEN 21 THEN '282 Võ Nguyên Giáp, Mỹ An, Ngũ Hành Sơn, Đà Nẵng'
        WHEN 22 THEN '44-46 Hàng Hòm, Hàng Gai, Hoàn Kiếm, Hà Nội'
        WHEN 23 THEN '5 Hàng Phèn, Hàng Bồ, Hoàn Kiếm, Hà Nội'
        WHEN 24 THEN '19 Hàng Chĩnh, Hàng Buồm, Hoàn Kiếm, Hà Nội'
        WHEN 25 THEN '32B Xuân Diệu, Quảng An, Tây Hồ, Hà Nội'
        WHEN 26 THEN '20 Ngõ Huyện, Hàng Trống, Hoàn Kiếm, Hà Nội'
        WHEN 27 THEN 'D8 Giảng Võ, Ba Đình, Hà Nội'
        WHEN 28 THEN '54 Liễu Giai, Cống Vị, Ba Đình, Hà Nội'
        WHEN 29 THEN '93 Thùy Vân, Phường 2, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 30 THEN '23 Thùy Vân, Phường 2, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 31 THEN '69 Thùy Vân, Phường 2, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 32 THEN '28 Trần Phú, Phường 1, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 33 THEN '159 Thùy Vân, Thắng Tam, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 34 THEN '117 Thùy Vân, Thắng Tam, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 35 THEN '2 Trương Công Định, Phường 1, Thành phố Vũng Tàu, Bà Rịa - Vũng Tàu'
        WHEN 36 THEN '180 Lý Tự Trọng, Bến Thành, Quận 1, Thành phố Hồ Chí Minh'
        WHEN 37 THEN '44 Bùi Thị Xuân, Bến Thành, Quận 1, Thành phố Hồ Chí Minh'
        WHEN 38 THEN '307 Lý Tự Trọng, Bến Thành, Quận 1, Thành phố Hồ Chí Minh'
        WHEN 39 THEN '36 Nguyễn Thái Bình, Phường Nguyễn Thái Bình, Quận 1, Thành phố Hồ Chí Minh'
        WHEN 40 THEN '55/29 Lê Thị Hồng Gấm, Nguyễn Thái Bình, Quận 1, Thành phố Hồ Chí Minh'
        WHEN 41 THEN '280 Nam Kỳ Khởi Nghĩa, Phường 8, Quận 3, Thành phố Hồ Chí Minh'
        WHEN 42 THEN '14 - 16 Đường Lê Lai, Phường Bến Thành, Quận 1, Thành phố Hồ Chí Minh'
        WHEN 43 THEN '157 Nam Kỳ Khởi Nghĩa, Võ Thị Sáu, Quận 3, Thành phố Hồ Chí Minh'
        WHEN 44 THEN 'Khu Bãi Dài, Gành Dầu, Phú Quốc, Kiên Giang'
        WHEN 45 THEN 'Đường Bào, Dương Tơ, Phú Quốc, Kiên Giang'
        WHEN 46 THEN 'Biển Gành Gió, Dương Đông, Phú Quốc, Kiên Giang'
        WHEN 47 THEN '127D Đường Trần Hưng Đạo, Dương Đông, Phú Quốc, Kiên Giang'
        WHEN 48 THEN 'Khu Bãi Dài, Gành Dầu, Phú Quốc, Kiên Giang'
        WHEN 49 THEN '65 Trần Hưng Đạo, Dương Đông, Phú Quốc, Kiên Giang'
        WHEN 50 THEN 'Khu phố 5, Đường Bào, Xã Dương Tơ, Thành phố Phú Quốc, Kiên Giang'
    END,
    s.city,
    s.lon,
    s.lat
FROM tmp_bulk_hotel_seed s
ON CONFLICT DO NOTHING;

INSERT INTO tai_san (
    id_tai_san,
    ho_so_kinh_doanh_id,
    mo_ta,
    trang_thai,
    gia_co_ban,
    is_dynamic_pricing,
    rating_average,
    review_count,
    dia_chi,
    thanh_pho,
    kinh_do,
    vi_do
)
SELECT
    'basn-' || s.code,
    'bhsn-' || s.code,
    'Nha hang phong cach hien dai tai ' || s.city,
    'SAN_SANG',
    150000 + (s.i * 5000),
    (s.i % 3 = 0),
    3.9 + ((s.i % 10) * 0.09),
    8 + (s.i * 2),
    s.i || ' ' || s.district,
    s.city,
    s.lon,
    s.lat
FROM tmp_bulk_rest_seed s
ON CONFLICT DO NOTHING;

INSERT INTO khach_san (
    id_tai_san,
    ten,
    hang_sao,
    loai_khach_san,
    gio_nhan_phong,
    gio_tra_phong,
    gio_nhan_phong_mac_dinh,
    gio_tra_phong_mac_dinh,
    so_tang,
    tong_so_phong
)
SELECT
    'bask-' || s.code,
    CASE s.i
        WHEN 1 THEN 'Hotel Colline Đà Lạt'
        WHEN 2 THEN 'Hotel Iris Đà Lạt'
        WHEN 3 THEN 'Mường Thanh Holiday Đà Lạt'
        WHEN 4 THEN 'Terracotta Hotel & Resort Dalat'
        WHEN 5 THEN 'Khách Sạn Đà Lạt Palace Heritage'
        WHEN 6 THEN 'Khách Sạn Tulip 2 Đà Lạt'
        WHEN 7 THEN 'Khách sạn Stillus Boutique Đà Lạt'
        WHEN 8 THEN 'Mường Thanh Grand Nha Trang Hotel'
        WHEN 9 THEN 'Khách sạn Panama Nha Trang'
        WHEN 10 THEN 'Khách sạn Havana Nha Trang'
        WHEN 11 THEN 'Champa Island Nha Trang - Resort Hotel & Spa'
        WHEN 12 THEN 'Champa Island Nha Trang - Resort Hotel & Spa'
        WHEN 13 THEN 'Seana Hotel Nha Trang'
        WHEN 14 THEN 'Khách Sạn InterContinental Nha Trang'
        WHEN 15 THEN 'Hilton Đà Nẵng'
        WHEN 16 THEN 'Dương Gia Hotel Đà Nẵng'
        WHEN 17 THEN 'TMS Hotel Danang Beach'
        WHEN 18 THEN 'Sala Danang Beach Hotel'
        WHEN 19 THEN 'Haian Beach Hotel & Spa'
        WHEN 20 THEN 'Novotel Danang Premier Han River'
        WHEN 21 THEN 'Rosamia Đà Nẵng Hotel'
        WHEN 22 THEN 'Hanoi Le Grand Hotel'
        WHEN 23 THEN 'Ha Noi Group Nest Hotel'
        WHEN 24 THEN 'Viet Vintage Hostel and Travel'
        WHEN 25 THEN 'The Autumn Homestay'
        WHEN 26 THEN 'Heritage Corner Hostel'
        WHEN 27 THEN 'Khách sạn Hà Nội (Hanoi Hotel)'
        WHEN 28 THEN 'Khách sạn Lotte Hà Nội'
        WHEN 29 THEN 'Aloha Hotel Vũng Tàu'
        WHEN 30 THEN 'Khách Sạn Biển Ngọc Vũng Tàu'
        WHEN 31 THEN 'Premier Pearl Hotel Vũng Tàu'
        WHEN 32 THEN 'Seaside Resort Vũng Tàu'
        WHEN 33 THEN 'Khách sạn Imperial Vũng Tàu'
        WHEN 34 THEN 'Khách sạn ibis Styles Vũng Tàu'
        WHEN 35 THEN 'Khách sạn Fusion Suites Vũng Tàu'
        WHEN 36 THEN 'La Siesta Premium Saigon'
        WHEN 37 THEN 'Khách Sạn Thiên Phú'
        WHEN 38 THEN 'Khách Sạn Hòa Bình'
        WHEN 39 THEN 'Khách Sạn Anh Anh'
        WHEN 40 THEN 'Hotel Hoa Phượng'
        WHEN 41 THEN 'Khách sạn La Vela Saigon'
        WHEN 42 THEN 'Khách sạn Silverland Bến Thành'
        WHEN 43 THEN 'Mai House Saigon Hotel'
        WHEN 44 THEN 'VinHolidays Fiesta Phu Quoc'
        WHEN 45 THEN 'Golden Dragon Hotel Phu Quoc'
        WHEN 46 THEN 'The Shells Resort and Spa Phú Quốc'
        WHEN 47 THEN 'Khách Sạn Dương Đông Hotel'
        WHEN 48 THEN 'Vinpearl Resort & Spa Phú Quốc'
        WHEN 49 THEN 'Hiệp Thoại Hotel Phú Quốc'
        WHEN 50 THEN 'Novotel Phú Quốc Resort'
    END,
    CASE s.i
        WHEN 1 THEN 4 WHEN 2 THEN 3 WHEN 3 THEN 4 WHEN 4 THEN 4 WHEN 5 THEN 5
        WHEN 6 THEN 3 WHEN 7 THEN 3 WHEN 8 THEN 4 WHEN 9 THEN 4 WHEN 10 THEN 5
        WHEN 11 THEN 5 WHEN 12 THEN 3 WHEN 13 THEN 5 WHEN 14 THEN 5 WHEN 15 THEN 5
        WHEN 16 THEN 3 WHEN 17 THEN 5 WHEN 18 THEN 4 WHEN 19 THEN 4 WHEN 20 THEN 5
        WHEN 21 THEN 5 WHEN 22 THEN 2 WHEN 23 THEN 3 WHEN 24 THEN 3 WHEN 25 THEN 3
        WHEN 26 THEN 3 WHEN 27 THEN 4 WHEN 28 THEN 5 WHEN 29 THEN 3 WHEN 30 THEN 3
        WHEN 31 THEN 4 WHEN 32 THEN 4 WHEN 33 THEN 5 WHEN 34 THEN 3 WHEN 35 THEN 4
        WHEN 36 THEN 5 WHEN 37 THEN 3 WHEN 38 THEN 1 WHEN 39 THEN 2 WHEN 40 THEN 2
        WHEN 41 THEN 5 WHEN 42 THEN 4 WHEN 43 THEN 5 WHEN 44 THEN 4 WHEN 45 THEN 5
        WHEN 46 THEN 5 WHEN 47 THEN 2 WHEN 48 THEN 5 WHEN 49 THEN 2 WHEN 50 THEN 5
    END,
    CASE s.i
        WHEN 4 THEN 'RESORT' WHEN 11 THEN 'RESORT' WHEN 32 THEN 'RESORT' WHEN 33 THEN 'RESORT' WHEN 46 THEN 'RESORT' WHEN 48 THEN 'RESORT' WHEN 50 THEN 'RESORT'
        ELSE 'KHACH_SAN'
    END,
    TIME '14:00',
    TIME '12:00',
    TIME '14:00',
    TIME '12:00',
    CASE s.i
        WHEN 1 THEN 6 WHEN 2 THEN 7 WHEN 3 THEN 8 WHEN 4 THEN 9 WHEN 5 THEN 10
        WHEN 6 THEN 11 WHEN 7 THEN 12 WHEN 8 THEN 5 WHEN 9 THEN 6 WHEN 10 THEN 7
        WHEN 11 THEN 8 WHEN 12 THEN 9 WHEN 13 THEN 10 WHEN 14 THEN 11 WHEN 15 THEN 5
        WHEN 16 THEN 6 WHEN 17 THEN 7 WHEN 18 THEN 8 WHEN 19 THEN 9 WHEN 20 THEN 10
        WHEN 21 THEN 11 WHEN 22 THEN 5 WHEN 23 THEN 6 WHEN 24 THEN 7 WHEN 25 THEN 8
        WHEN 26 THEN 9 WHEN 27 THEN 10 WHEN 28 THEN 11 WHEN 29 THEN 5 WHEN 30 THEN 6
        WHEN 31 THEN 7 WHEN 32 THEN 8 WHEN 33 THEN 9 WHEN 34 THEN 10 WHEN 35 THEN 11
        WHEN 36 THEN 5 WHEN 37 THEN 6 WHEN 38 THEN 7 WHEN 39 THEN 8 WHEN 40 THEN 9
        WHEN 41 THEN 10 WHEN 42 THEN 11 WHEN 43 THEN 12 WHEN 44 THEN 5 WHEN 45 THEN 6
        WHEN 46 THEN 7 WHEN 47 THEN 8 WHEN 48 THEN 9 WHEN 49 THEN 10 WHEN 50 THEN 11
    END,
    CASE s.i
        WHEN 1 THEN 21 WHEN 2 THEN 22 WHEN 3 THEN 23 WHEN 4 THEN 24 WHEN 5 THEN 25
        WHEN 6 THEN 26 WHEN 7 THEN 27 WHEN 8 THEN 28 WHEN 9 THEN 29 WHEN 10 THEN 30
        WHEN 11 THEN 31 WHEN 12 THEN 32 WHEN 13 THEN 33 WHEN 14 THEN 34 WHEN 15 THEN 35
        WHEN 16 THEN 36 WHEN 17 THEN 37 WHEN 18 THEN 38 WHEN 19 THEN 39 WHEN 20 THEN 40
        WHEN 21 THEN 41 WHEN 22 THEN 42 WHEN 23 THEN 43 WHEN 24 THEN 4 WHEN 25 THEN 25
        WHEN 26 THEN 26 WHEN 27 THEN 27 WHEN 28 THEN 28 WHEN 29 THEN 29 WHEN 30 THEN 30
        WHEN 31 THEN 31 WHEN 32 THEN 32 WHEN 33 THEN 33 WHEN 34 THEN 34 WHEN 35 THEN 35
        WHEN 36 THEN 36 WHEN 37 THEN 37 WHEN 38 THEN 38 WHEN 39 THEN 39 WHEN 40 THEN 20
        WHEN 41 THEN 21 WHEN 42 THEN 22 WHEN 43 THEN 23 WHEN 44 THEN 24 WHEN 45 THEN 25
        WHEN 46 THEN 26 WHEN 47 THEN 27 WHEN 48 THEN 28 WHEN 49 THEN 29 WHEN 50 THEN 30
    END
FROM tmp_bulk_hotel_seed s
ON CONFLICT DO NOTHING;

INSERT INTO nha_hang (
    id_tai_san,
    ten,
    loai_am_thuc,
    gio_mo_cua,
    gio_dong_cua,
    suc_chua,
    co_dat_ban_truoc,
    co_dat_mon_truoc
)
SELECT
    'basn-' || s.code,
    'TraVi Bistro ' || s.code,
    CASE s.i % 5
        WHEN 0 THEN 'VIET_NAM'
        WHEN 1 THEN 'HAI_SAN'
        WHEN 2 THEN 'NUONG_BBQ'
        WHEN 3 THEN 'AU_A'
        ELSE 'CHAY'
    END,
    TIME '09:00',
    TIME '22:30',
    40 + (s.i % 120),
    TRUE,
    (s.i % 4 <> 0)
FROM tmp_bulk_rest_seed s
ON CONFLICT DO NOTHING;

INSERT INTO tien_ich_khach_san (id, ten_tien_ich, loai_tien_ich, mo_ta)
VALUES
('bkamen-01', 'Bulk Ho boi', 'GIAI_TRI', 'Ho boi tieu chuan cho lo seed bulk'),
('bkamen-02', 'Bulk Buffet sang', 'AM_THUC', 'Buffet sang theo goi phong'),
('bkamen-03', 'Bulk Gym', 'SUC_KHOE', 'Phong tap co ban'),
('bkamen-04', 'Bulk Dua don san bay', 'DI_CHUYEN', 'Dich vu xe dua don')
ON CONFLICT DO NOTHING;

INSERT INTO khach_san_tien_ich (khach_san_id, tien_ich_id)
SELECT
    'bask-' || s.code,
    amenity_id
FROM tmp_bulk_hotel_seed s
CROSS JOIN LATERAL (
    VALUES
        ('bkamen-01'::varchar),
        ('bkamen-02'::varchar),
        (CASE WHEN s.i % 2 = 0 THEN 'bkamen-03' ELSE 'bkamen-04' END)
) AS a(amenity_id)
ON CONFLICT DO NOTHING;

INSERT INTO tien_ich_nha_hang (id, nha_hang_id, ten_tien_ich, loai_tien_ich, mo_ta, co_thu_phi, phi_su_dung)
SELECT
    'bramen-' || s.code || '-1',
    'basn-' || s.code,
    'Khong gian ngoai troi',
    'KHONG_GIAN',
    'Ban ngoai troi phu hop nhom nho',
    FALSE,
    0
FROM tmp_bulk_rest_seed s
ON CONFLICT DO NOTHING;

INSERT INTO tien_ich_nha_hang (id, nha_hang_id, ten_tien_ich, loai_tien_ich, mo_ta, co_thu_phi, phi_su_dung)
SELECT
    'bramen-' || s.code || '-2',
    'basn-' || s.code,
    'Phong rieng',
    'KHONG_GIAN',
    'Phong rieng cho tiep khach',
    TRUE,
    120000
FROM tmp_bulk_rest_seed s
ON CONFLICT DO NOTHING;

INSERT INTO phong (
    id,
    khach_san_id,
    so_phong,
    ten_phong,
    loai_phong,
    mo_ta,
    suc_chua_toi_da,
    so_giuong,
    dien_tich,
    gia_co_ban,
    so_luong_phong,
    trang_thai,
    phan_tram_giam_gia
)
SELECT
    'brk-' || s.code || '-' || r.rn,
    'bask-' || s.code,
    s.code || r.rn,
    CASE r.rn
        WHEN 1 THEN 'Standard Room'
        WHEN 2 THEN 'Deluxe Room'
        ELSE 'Family Suite'
    END,
    CASE r.rn
        WHEN 1 THEN 'STANDARD'
        WHEN 2 THEN 'DELUXE'
        ELSE 'SUITE'
    END,
    'Phong seed bulk ' || s.code,
    CASE r.rn WHEN 1 THEN 2 WHEN 2 THEN 3 ELSE 4 END,
    CASE r.rn WHEN 1 THEN 1 ELSE 2 END,
    CASE r.rn WHEN 1 THEN 24 WHEN 2 THEN 32 ELSE 45 END,
    (900000 + (s.i * 25000)) * (1 + (r.rn - 1) * 0.35),
    3 + (s.i % 5),
    'SAN_SANG',
    CASE WHEN r.rn = 1 THEN (s.i % 15) ELSE (s.i % 8) END
FROM tmp_bulk_hotel_seed s
CROSS JOIN (VALUES (1), (2), (3)) AS r(rn)
ON CONFLICT DO NOTHING;

INSERT INTO phong_tien_ich (phong_id, tien_ich)
SELECT
    x.phong_id,
    x.tien_ich
FROM (
    SELECT
        'brk-' || s.code || '-' || r.rn AS phong_id,
        UNNEST(
            CASE r.rn
                WHEN 1 THEN ARRAY['WIFI', 'DIEU_HOA']
                WHEN 2 THEN ARRAY['VIEW_DEP', 'BON_TAM']
                ELSE ARRAY['TU_LANH', 'BAN_CONG']
            END
        ) AS tien_ich
    FROM tmp_bulk_hotel_seed s
    CROSS JOIN (VALUES (1), (2), (3)) AS r(rn)
) x
WHERE NOT EXISTS (
    SELECT 1
    FROM phong_tien_ich p
    WHERE p.phong_id = x.phong_id
      AND p.tien_ich = x.tien_ich
);

INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi)
SELECT
    'brt-' || s.code || '-' || t.n,
    'basn-' || s.code,
    'Ban ' || t.n,
    CASE t.n
        WHEN 1 THEN 'Tang tret'
        WHEN 2 THEN 'Tang lung'
        ELSE 'Khu ngoai troi'
    END,
    'Ban seed bulk ' || s.code,
    'SAN_SANG',
    CASE t.n WHEN 1 THEN 4 WHEN 2 THEN 6 ELSE 8 END
FROM tmp_bulk_rest_seed s
CROSS JOIN (VALUES (1), (2), (3)) AS t(n)
ON CONFLICT DO NOTHING;

INSERT INTO anh_khach_san (id, khach_san_id, duong_dan_url, mo_ta_anh, la_anh_dai_dien, ngay_tai_len)
SELECT
    'bik-' || s.code || '-' || img.pos,
    'bask-' || s.code,
    '/uploads/hotel/' || img.filename,
    CASE img.pos
        WHEN 1 THEN 'Anh dai dien khach san ' || s.code
        ELSE 'Anh khach san ' || s.code || ' - ' || img.pos
    END,
    img.pos = 1,
    CURRENT_DATE
FROM tmp_bulk_hotel_seed s
CROSS JOIN LATERAL (
    VALUES
        (1, CASE (((s.i - 1) * 3) % 22) + 1
            WHEN 1 THEN 'hotel1.png' WHEN 2 THEN 'hotel2.png' WHEN 3 THEN 'hotel3.png' WHEN 4 THEN 'hotel4.png'
            WHEN 5 THEN 'hotel5.png' WHEN 6 THEN 'hotel6.png' WHEN 7 THEN 'hotel7.png' WHEN 8 THEN 'hotel8.png'
            WHEN 9 THEN 'hotel9.png' WHEN 10 THEN 'hotel10.png' WHEN 11 THEN 'hotel11.png' WHEN 12 THEN 'hotel12.png'
            WHEN 13 THEN 'hotel13.png' WHEN 14 THEN 'hotel14.png' WHEN 15 THEN 'hotel15.png' WHEN 16 THEN 'hotel16.png'
            WHEN 17 THEN 'hotel17.png' WHEN 18 THEN 'hotel18.png' WHEN 19 THEN 'hotel19.png' WHEN 20 THEN 'hotel20.png'
            WHEN 21 THEN 'hotel21.png' ELSE 'hotel22.png'
        END),
        (2, CASE ((((s.i - 1) * 3) + 1) % 22) + 1
            WHEN 1 THEN 'hotel1.png' WHEN 2 THEN 'hotel2.png' WHEN 3 THEN 'hotel3.png' WHEN 4 THEN 'hotel4.png'
            WHEN 5 THEN 'hotel5.png' WHEN 6 THEN 'hotel6.png' WHEN 7 THEN 'hotel7.png' WHEN 8 THEN 'hotel8.png'
            WHEN 9 THEN 'hotel9.png' WHEN 10 THEN 'hotel10.png' WHEN 11 THEN 'hotel11.png' WHEN 12 THEN 'hotel12.png'
            WHEN 13 THEN 'hotel13.png' WHEN 14 THEN 'hotel14.png' WHEN 15 THEN 'hotel15.png' WHEN 16 THEN 'hotel16.png'
            WHEN 17 THEN 'hotel17.png' WHEN 18 THEN 'hotel18.png' WHEN 19 THEN 'hotel19.png' WHEN 20 THEN 'hotel20.png'
            WHEN 21 THEN 'hotel21.png' ELSE 'hotel22.png'
        END),
        (3, CASE ((((s.i - 1) * 3) + 2) % 22) + 1
            WHEN 1 THEN 'hotel1.png' WHEN 2 THEN 'hotel2.png' WHEN 3 THEN 'hotel3.png' WHEN 4 THEN 'hotel4.png'
            WHEN 5 THEN 'hotel5.png' WHEN 6 THEN 'hotel6.png' WHEN 7 THEN 'hotel7.png' WHEN 8 THEN 'hotel8.png'
            WHEN 9 THEN 'hotel9.png' WHEN 10 THEN 'hotel10.png' WHEN 11 THEN 'hotel11.png' WHEN 12 THEN 'hotel12.png'
            WHEN 13 THEN 'hotel13.png' WHEN 14 THEN 'hotel14.png' WHEN 15 THEN 'hotel15.png' WHEN 16 THEN 'hotel16.png'
            WHEN 17 THEN 'hotel17.png' WHEN 18 THEN 'hotel18.png' WHEN 19 THEN 'hotel19.png' WHEN 20 THEN 'hotel20.png'
            WHEN 21 THEN 'hotel21.png' ELSE 'hotel22.png'
        END)
) AS img(pos, filename)
ON CONFLICT DO NOTHING;

DELETE FROM anh_phong WHERE phong_id LIKE 'brk-%';

INSERT INTO anh_phong (id, phong_id, duong_dan_url, mo_ta_anh, la_anh_dai_dien, ngay_tai_len)
SELECT
    'bip-' || s.code || '-' || r.rn,
    'brk-' || s.code || '-' || r.rn,
    '/uploads/room/' || CASE (((s.i - 1) * 3 + r.rn - 1) % 20) + 1
        WHEN 1 THEN 'room1.png' WHEN 2 THEN 'room2.png' WHEN 3 THEN 'room3.png' WHEN 4 THEN 'room4.png'
        WHEN 5 THEN 'room5.png' WHEN 6 THEN 'room6.png' WHEN 7 THEN 'room7.png' WHEN 8 THEN 'room8.png'
        WHEN 9 THEN 'room9.png' WHEN 10 THEN 'room10.png' WHEN 11 THEN 'room11.png' WHEN 12 THEN 'room12.png'
        WHEN 13 THEN 'room13.png' WHEN 14 THEN 'room14.png' WHEN 15 THEN 'room15.png' WHEN 16 THEN 'room16.png'
        WHEN 17 THEN 'room17.png' WHEN 18 THEN 'room18.png' WHEN 19 THEN 'room19.png' ELSE 'room20.png'
    END,
    'Anh phong bulk ' || s.code || '-' || r.rn,
    TRUE,
    CURRENT_DATE
FROM tmp_bulk_hotel_seed s
CROSS JOIN (VALUES (1), (2), (3)) AS r(rn)
ON CONFLICT DO NOTHING;

DELETE FROM anh_nha_hang WHERE nha_hang_id LIKE 'basn-%';

INSERT INTO anh_nha_hang (id, nha_hang_id, duong_dan_url, mo_ta_anh, la_anh_dai_dien, ngay_tai_len)
SELECT
    'bir-' || s.code,
    'basn-' || s.code,
    'https://picsum.photos/seed/bulk-restaurant-' || s.code || '/1200/800',
    'Anh dai dien nha hang bulk ' || s.code,
    TRUE,
    CURRENT_DATE
FROM tmp_bulk_rest_seed s
ON CONFLICT DO NOTHING;

INSERT INTO thuc_don (id, nha_hang_id, ten_thuc_don, phan_loai, trang_thai)
SELECT
    'bmn-' || s.code,
    'basn-' || s.code,
    'Thuc don chinh ' || s.code,
    'A_LA_CARTE',
    'DANG_HIEN_THI'
FROM tmp_bulk_rest_seed s
ON CONFLICT DO NOTHING;

INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted)
SELECT
    'bmd-' || s.code || '-' || d.n,
    'bmn-' || s.code,
    CASE d.n
        WHEN 1 THEN 'Mon khai vi ' || s.code
        WHEN 2 THEN 'Mon chinh ' || s.code
        ELSE 'Trang mieng ' || s.code
    END,
    'Mon an seed bulk ' || s.code,
    (150000 + (s.i * 5000)) * (0.8 + (d.n * 0.25)),
    CASE d.n
        WHEN 1 THEN 'KHAI_VI'
        WHEN 2 THEN 'MON_CHINH'
        ELSE 'TRANG_MIENG'
    END,
    'DANG_BAN',
    'https://picsum.photos/seed/bulk-dish-' || s.code || '-' || d.n || '/800/600',
    FALSE
FROM tmp_bulk_rest_seed s
CROSS JOIN (VALUES (1), (2), (3)) AS d(n)
ON CONFLICT DO NOTHING;

INSERT INTO mon_an_the_ngu_canh (mon_an_id, the_ngu_canh)
SELECT
    y.mon_an_id,
    y.the_ngu_canh
FROM (
    SELECT
        'bmd-' || s.code || '-' || d.n AS mon_an_id,
        CASE d.n
            WHEN 1 THEN 'AN_NHE'
            WHEN 2 THEN 'MON_NOI_BAT'
            ELSE 'TRANG_MIENG'
        END AS the_ngu_canh
    FROM tmp_bulk_rest_seed s
    CROSS JOIN (VALUES (1), (2), (3)) AS d(n)
) y
WHERE NOT EXISTS (
    SELECT 1
    FROM mon_an_the_ngu_canh m
    WHERE m.mon_an_id = y.mon_an_id
      AND m.the_ngu_canh = y.the_ngu_canh
);

INSERT INTO combo (id, thuc_don_id, ten_combo, mo_ta, gia_combo, ngay_bat_dau, ngay_ket_thuc, trang_thai)
SELECT
    'bcb-' || s.code,
    'bmn-' || s.code,
    'Combo dac biet ' || s.code,
    'Combo 2 mon ban chay + nuoc',
    280000 + (s.i * 7000),
    CURRENT_DATE,
    CURRENT_DATE + 365,
    1
FROM tmp_bulk_rest_seed s
ON CONFLICT DO NOTHING;

INSERT INTO combo_mon_an (combo_id, mon_an_id)
SELECT
    'bcb-' || s.code,
    'bmd-' || s.code || '-' || d.n
FROM tmp_bulk_rest_seed s
CROSS JOIN (VALUES (1), (2)) AS d(n)
ON CONFLICT DO NOTHING;

INSERT INTO combo_item (id, combo_id, mon_an_id, so_luong)
SELECT
    'bci-' || s.code || '-' || d.n,
    'bcb-' || s.code,
    'bmd-' || s.code || '-' || d.n,
    1
FROM tmp_bulk_rest_seed s
CROSS JOIN (VALUES (1), (2)) AS d(n)
ON CONFLICT DO NOTHING;

INSERT INTO chinh_sach (
    id,
    loai_chinh_sach,
    noi_dung,
    ngay_ap_dung,
    gio_nhan_phong,
    gio_tra_phong,
    gio_mo_cua,
    gio_dong_cua,
    chinh_sach_huy,
    chinh_sach_hoan_tien,
    quy_dinh_tre_em,
    quy_dinh_vat_nuoi,
    ghi_chu_khac,
    ho_so_kinh_doanh_id
)
SELECT
    'bpk-' || s.code,
    'KHACH_SAN',
    'Chinh sach luu tru bulk ' || s.code,
    CURRENT_DATE,
    TIME '14:00',
    TIME '12:00',
    NULL,
    NULL,
    'Huy truoc 48h',
    'Hoan 70%',
    'Tre em duoi 6 tuoi mien phi',
    'Vat nuoi nho duoc phep',
    'Ap dung cho du lieu demo bulk',
    'bhsk-' || s.code
FROM tmp_bulk_hotel_seed s
ON CONFLICT DO NOTHING;

INSERT INTO chinh_sach (
    id,
    loai_chinh_sach,
    noi_dung,
    ngay_ap_dung,
    gio_nhan_phong,
    gio_tra_phong,
    gio_mo_cua,
    gio_dong_cua,
    chinh_sach_huy,
    chinh_sach_hoan_tien,
    quy_dinh_tre_em,
    quy_dinh_vat_nuoi,
    ghi_chu_khac,
    ho_so_kinh_doanh_id
)
SELECT
    'bpn-' || s.code,
    'NHA_HANG',
    'Chinh sach dat ban bulk ' || s.code,
    CURRENT_DATE,
    NULL,
    NULL,
    TIME '09:00',
    TIME '22:30',
    'Huy truoc 2h',
    'Khong ap dung',
    NULL,
    NULL,
    'Ap dung cho du lieu demo bulk',
    'bhsn-' || s.code
FROM tmp_bulk_rest_seed s
ON CONFLICT DO NOTHING;

DROP TABLE IF EXISTS tmp_bulk_hotel_seed;
DROP TABLE IF EXISTS tmp_bulk_rest_seed;
