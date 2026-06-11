-- Seed missing supporting data for 50 real restaurants (images, tables, menus, dishes)
-- Root cause: restaurants existed in nha_hang but had no ban / anh_nha_hang / thuc_don / mon_an,
-- so public catalog filtered them out because soGheConTrong = 0.

DELETE FROM mon_an WHERE thuc_don_id LIKE 'bmn-%';
DELETE FROM thuc_don WHERE id LIKE 'bmn-%';
DELETE FROM ban WHERE nha_hang_id LIKE 'basn-%';
DELETE FROM anh_nha_hang WHERE nha_hang_id LIKE 'basn-%';

-- 3 local images per restaurant
INSERT INTO anh_nha_hang (id, nha_hang_id, duong_dan_url, mo_ta_anh, la_anh_dai_dien, ngay_tai_len)
SELECT
    'bir-' || s.code || '-' || img.pos,
    'basn-' || s.code,
    '/uploads/restaurant/' || img.filename,
    CASE
        WHEN img.pos = 1 THEN 'Anh dai dien nha hang ' || s.code
        ELSE 'Anh nha hang ' || s.code || ' - ' || img.pos
    END,
    img.pos = 1,
    CURRENT_DATE
FROM (
    SELECT LPAD(gs::text, 3, '0') AS code, gs AS i
    FROM generate_series(1, 50) gs
) s
CROSS JOIN LATERAL (
    VALUES
        (1, CASE (((s.i - 1) * 3) % 20) + 1
            WHEN 1 THEN 'restaurant1.png' WHEN 2 THEN 'restaurant2.png' WHEN 3 THEN 'restaurant3.png' WHEN 4 THEN 'restaurant4.png'
            WHEN 5 THEN 'restaurant5.png' WHEN 6 THEN 'restaurant6.png' WHEN 7 THEN 'restaurant7.png' WHEN 8 THEN 'restaurant8.png'
            WHEN 9 THEN 'restaurant9.png' WHEN 10 THEN 'restaurant10.png' WHEN 11 THEN 'restaurant11.png' WHEN 12 THEN 'restaurant12.png'
            WHEN 13 THEN 'restaurant13.png' WHEN 14 THEN 'restaurant14.png' WHEN 15 THEN 'restaurant15.png' WHEN 16 THEN 'restaurant16.png'
            WHEN 17 THEN 'restaurant17.png' WHEN 18 THEN 'restaurant18.png' WHEN 19 THEN 'restaurant19.png' ELSE 'restaurant20.png'
        END),
        (2, CASE ((((s.i - 1) * 3) + 1) % 20) + 1
            WHEN 1 THEN 'restaurant1.png' WHEN 2 THEN 'restaurant2.png' WHEN 3 THEN 'restaurant3.png' WHEN 4 THEN 'restaurant4.png'
            WHEN 5 THEN 'restaurant5.png' WHEN 6 THEN 'restaurant6.png' WHEN 7 THEN 'restaurant7.png' WHEN 8 THEN 'restaurant8.png'
            WHEN 9 THEN 'restaurant9.png' WHEN 10 THEN 'restaurant10.png' WHEN 11 THEN 'restaurant11.png' WHEN 12 THEN 'restaurant12.png'
            WHEN 13 THEN 'restaurant13.png' WHEN 14 THEN 'restaurant14.png' WHEN 15 THEN 'restaurant15.png' WHEN 16 THEN 'restaurant16.png'
            WHEN 17 THEN 'restaurant17.png' WHEN 18 THEN 'restaurant18.png' WHEN 19 THEN 'restaurant19.png' ELSE 'restaurant20.png'
        END),
        (3, CASE ((((s.i - 1) * 3) + 2) % 20) + 1
            WHEN 1 THEN 'restaurant1.png' WHEN 2 THEN 'restaurant2.png' WHEN 3 THEN 'restaurant3.png' WHEN 4 THEN 'restaurant4.png'
            WHEN 5 THEN 'restaurant5.png' WHEN 6 THEN 'restaurant6.png' WHEN 7 THEN 'restaurant7.png' WHEN 8 THEN 'restaurant8.png'
            WHEN 9 THEN 'restaurant9.png' WHEN 10 THEN 'restaurant10.png' WHEN 11 THEN 'restaurant11.png' WHEN 12 THEN 'restaurant12.png'
            WHEN 13 THEN 'restaurant13.png' WHEN 14 THEN 'restaurant14.png' WHEN 15 THEN 'restaurant15.png' WHEN 16 THEN 'restaurant16.png'
            WHEN 17 THEN 'restaurant17.png' WHEN 18 THEN 'restaurant18.png' WHEN 19 THEN 'restaurant19.png' ELSE 'restaurant20.png'
        END)
) AS img(pos, filename)
WHERE EXISTS (
    SELECT 1 FROM nha_hang nh WHERE nh.id_tai_san = 'basn-' || s.code
);

-- 5 tables per restaurant so they appear in public catalog search/featured
INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi, deleted)
SELECT
    'bbn-' || s.code || '-' || t.n,
    'basn-' || s.code,
    'Ban ' || t.n,
    CASE
        WHEN t.n <= 2 THEN 'Tang tret'
        WHEN t.n <= 4 THEN 'Tang lung'
        ELSE 'San vuon'
    END,
    'Ban seed cho nha hang ' || s.code,
    'SAN_SANG',
    CASE t.n
        WHEN 1 THEN 2
        WHEN 2 THEN 4
        WHEN 3 THEN 4
        WHEN 4 THEN 6
        ELSE 8
    END,
    FALSE
FROM (
    SELECT LPAD(gs::text, 3, '0') AS code
    FROM generate_series(1, 50) gs
) s
CROSS JOIN (VALUES (1), (2), (3), (4), (5)) AS t(n)
WHERE EXISTS (
    SELECT 1 FROM nha_hang nh WHERE nh.id_tai_san = 'basn-' || s.code
);

-- 1 visible menu per restaurant
INSERT INTO thuc_don (id, nha_hang_id, ten_thuc_don, phan_loai, trang_thai)
SELECT
    'bmn-' || s.code,
    'basn-' || s.code,
    'Thuc don chinh ' || s.code,
    'A_LA_CARTE',
    'DANG_HIEN_THI'
FROM (
    SELECT LPAD(gs::text, 3, '0') AS code
    FROM generate_series(1, 50) gs
) s
WHERE EXISTS (
    SELECT 1 FROM nha_hang nh WHERE nh.id_tai_san = 'basn-' || s.code
);

-- 3 dishes per restaurant
INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted)
SELECT
    'bmd-' || s.code || '-' || d.n,
    'bmn-' || s.code,
    CASE d.n
        WHEN 1 THEN 'Mon khai vi ' || s.code
        WHEN 2 THEN 'Mon chinh ' || s.code
        ELSE 'Trang mieng ' || s.code
    END,
    'Mon an seed nha hang ' || s.code,
    CASE d.n
        WHEN 1 THEN 89000 + (s.i * 1000)
        WHEN 2 THEN 149000 + (s.i * 2000)
        ELSE 59000 + (s.i * 800)
    END,
    CASE d.n
        WHEN 1 THEN 'KHAI_VI'
        WHEN 2 THEN 'MON_CHINH'
        ELSE 'TRANG_MIENG'
    END,
    'DANG_BAN',
    '/uploads/dishes/' || CASE (((s.i - 1) * 3 + d.n - 1) % 20) + 1
        WHEN 1 THEN 'dish1.png' WHEN 2 THEN 'dish2.png' WHEN 3 THEN 'dish3.png' WHEN 4 THEN 'dish4.png'
        WHEN 5 THEN 'dish5.png' WHEN 6 THEN 'dish6.png' WHEN 7 THEN 'dish7.png' WHEN 8 THEN 'dish8.png'
        WHEN 9 THEN 'dish9.png' WHEN 10 THEN 'dish10.png' WHEN 11 THEN 'dish11.png' WHEN 12 THEN 'dish12.png'
        WHEN 13 THEN 'dish13.png' WHEN 14 THEN 'dish14.png' WHEN 15 THEN 'dish15.png' WHEN 16 THEN 'dish16.png'
        WHEN 17 THEN 'dish17.png' WHEN 18 THEN 'dish18.png' WHEN 19 THEN 'dish19.png' ELSE 'dish20.png'
    END,
    FALSE
FROM (
    SELECT LPAD(gs::text, 3, '0') AS code, gs AS i
    FROM generate_series(1, 50) gs
) s
CROSS JOIN (VALUES (1), (2), (3)) AS d(n)
WHERE EXISTS (
    SELECT 1 FROM thuc_don td WHERE td.id = 'bmn-' || s.code
);
