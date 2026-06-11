-- Refresh hotel/room images to local /uploads URLs
-- Reason: old seed migration may already be applied, so changing it does not update existing DB rows.

DELETE FROM anh_khach_san
WHERE khach_san_id LIKE 'bask-%';

INSERT INTO anh_khach_san (id, khach_san_id, duong_dan_url, mo_ta_anh, la_anh_dai_dien, ngay_tai_len)
SELECT
    'bik-' || s.code || '-' || img.pos,
    'bask-' || s.code,
    '/uploads/hotel/' || img.filename,
    CASE
        WHEN img.pos = 1 THEN 'Anh dai dien khach san ' || s.code
        ELSE 'Anh khach san ' || s.code || ' - ' || img.pos
    END,
    img.pos = 1,
    CURRENT_DATE
FROM (
    SELECT LPAD(gs::text, 3, '0') AS code, gs AS i
    FROM generate_series(1, 50) gs
) s
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
WHERE EXISTS (
    SELECT 1 FROM khach_san ks WHERE ks.id_tai_san = 'bask-' || s.code
);

DELETE FROM anh_phong
WHERE phong_id LIKE 'brk-%';

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
FROM (
    SELECT LPAD(gs::text, 3, '0') AS code, gs AS i
    FROM generate_series(1, 50) gs
) s
CROSS JOIN (VALUES (1), (2), (3)) AS r(rn)
WHERE EXISTS (
    SELECT 1 FROM phong p WHERE p.id = 'brk-' || s.code || '-' || r.rn
);
