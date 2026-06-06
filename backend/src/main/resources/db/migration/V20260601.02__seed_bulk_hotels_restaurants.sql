-- Bulk seed: 50 hotels + 50 restaurants and dependent catalog tables.

-- Ensure partner rows exist when matching users are present.
INSERT INTO doi_tac (id, ti_le_chiet_khau)
SELECT u.id, 10.0
FROM users u
WHERE u.id IN ('u-partner-1', 'u-partner-2', 'u-partner-3', 'u-partner-4', 'u-partner-5')
ON CONFLICT DO NOTHING;

CREATE TEMP TABLE tmp_bulk_hotel_seed AS
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
        WHEN 1 THEN 'Son Tra'
        WHEN 2 THEN 'Loc Tho'
        WHEN 3 THEN 'Quan 1'
        ELSE 'Duong Dong'
    END AS district,
    CASE (gs - 1) % 5
        WHEN 0 THEN 11.9400
        WHEN 1 THEN 16.0600
        WHEN 2 THEN 12.2400
        WHEN 3 THEN 10.7700
        ELSE 10.2300
    END + ((gs % 7) * 0.001) AS lat,
    CASE (gs - 1) % 5
        WHEN 0 THEN 108.4400
        WHEN 1 THEN 108.2300
        WHEN 2 THEN 109.1900
        WHEN 3 THEN 106.7000
        ELSE 103.9700
    END + ((gs % 7) * 0.001) AS lon
FROM generate_series(1, 50) gs
JOIN partner_count pc ON pc.cnt > 0
JOIN partner_pool pp ON pp.rn = (((gs - 1) % pc.cnt) + 1);

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
    'TraVi Hotel ' || s.code,
    'BKS' || s.code,
    'GPKD-BKS-' || s.code,
    ROUND(s.lat::numeric, 6)::text || ',' || ROUND(s.lon::numeric, 6)::text,
    '0908' || LPAD(s.i::text, 6, '0'),
    'hotel' || s.code || '@bulk.travi.vn',
    s.i || ' ' || s.district,
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
    'Khach san du lich tong hop tai ' || s.city,
    'SAN_SANG',
    900000 + (s.i * 25000),
    (s.i % 2 = 0),
    4.0 + ((s.i % 10) * 0.08),
    10 + (s.i * 3),
    s.i || ' ' || s.district,
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
    'TraVi Hotel ' || s.code,
    3 + (s.i % 3),
    CASE WHEN s.i % 4 = 0 THEN 'RESORT' ELSE 'KHACH_SAN' END,
    TIME '14:00',
    TIME '12:00',
    TIME '14:00',
    TIME '12:00',
    5 + (s.i % 12),
    20 + (s.i % 40)
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
    'bik-' || s.code,
    'bask-' || s.code,
    'https://picsum.photos/seed/bulk-hotel-' || s.code || '/1200/800',
    'Anh dai dien khach san bulk ' || s.code,
    TRUE,
    CURRENT_DATE
FROM tmp_bulk_hotel_seed s
ON CONFLICT DO NOTHING;

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

INSERT INTO anh_phong (id, phong_id, duong_dan_url, mo_ta_anh, la_anh_dai_dien, ngay_tai_len)
SELECT
    'bip-' || s.code || '-' || r.rn,
    'brk-' || s.code || '-' || r.rn,
    'https://picsum.photos/seed/bulk-room-' || s.code || '-' || r.rn || '/1000/700',
    'Anh phong bulk ' || s.code || '-' || r.rn,
    TRUE,
    CURRENT_DATE
FROM tmp_bulk_hotel_seed s
CROSS JOIN (VALUES (1), (2), (3)) AS r(rn)
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
