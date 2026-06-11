-- Add real room images from Unsplash for 50 hotels × 3 room types
-- Standard rooms: 5 images
-- Deluxe rooms: 5 images
-- Suite rooms: 5 images

DELETE FROM anh_phong WHERE phong_id LIKE 'brk-%';

INSERT INTO anh_phong (id, phong_id, duong_dan_url, mo_ta_anh, la_anh_dai_dien, ngay_tai_len)
SELECT
    'bip-' || s.code || '-' || r.rn AS id,
    'brk-' || s.code || '-' || r.rn AS phong_id,
    CASE r.rn
        -- Standard rooms: 5 Unsplash images
        WHEN 1 THEN
            CASE ((s.i - 1) % 5 + 1)
                WHEN 1 THEN 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=800&q=80.jpg'
                WHEN 2 THEN 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=800&q=80.jpg'
                WHEN 3 THEN 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=800&q=80.jpg'
                WHEN 4 THEN 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=800&q=80.jpg'
                WHEN 5 THEN 'https://images.unsplash.com/photo-1583847268964-b28ce8f31586?auto=format&fit=crop&w=800&q=80.jpg'
            END
        -- Deluxe rooms: 5 Unsplash images
        WHEN 2 THEN
            CASE ((s.i - 1) % 5 + 1)
                WHEN 1 THEN 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?auto=format&fit=crop&w=800&q=80.jpg'
                WHEN 2 THEN 'https://images.unsplash.com/photo-1591088398332-8a7791972843?auto=format&fit=crop&w=800&q=80.jpg'
                WHEN 3 THEN 'https://images.unsplash.com/photo-1505693314120-0d443867891c?auto=format&fit=crop&w=800&q=80.jpg'
                WHEN 4 THEN 'https://images.unsplash.com/photo-1595576508898-0ad5c879a061?auto=format&fit=crop&w=800&q=80.jpg'
                WHEN 5 THEN 'https://images.unsplash.com/photo-1618221118493-9cfa1a1c00da?auto=format&fit=crop&w=800&q=80.jpg'
            END
        -- Suite rooms: 5 Unsplash images
        WHEN 3 THEN
            CASE ((s.i - 1) % 5 + 1)
                WHEN 1 THEN 'https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?auto=format&fit=crop&w=800&q=80.jpg'
                WHEN 2 THEN 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?auto=format&fit=crop&w=800&q=80.jpg'
                WHEN 3 THEN 'https://images.unsplash.com/photo-1522771731478-44fb4bea4b3f?auto=format&fit=crop&w=800&q=80.jpg'
                WHEN 4 THEN 'https://images.unsplash.com/photo-1574873215043-44119461cb3b?auto=format&fit=crop&w=800&q=80.jpg'
                WHEN 5 THEN 'https://images.unsplash.com/photo-1505691938895-1758d7feb511?auto=format&fit=crop&w=800&q=80.jpg'
            END
    END AS duong_dan_url,
    CASE r.rn
        WHEN 1 THEN 'Standard Room - ' || s.code
        WHEN 2 THEN 'Deluxe Room - ' || s.code
        WHEN 3 THEN 'Family Suite - ' || s.code
    END AS mo_ta_anh,
    TRUE AS la_anh_dai_dien,
    CURRENT_DATE AS ngay_tai_len
FROM (
    SELECT 1 i, '001' code UNION ALL SELECT 2, '002' UNION ALL SELECT 3, '003' UNION ALL SELECT 4, '004' UNION ALL SELECT 5, '005'
    UNION ALL SELECT 6, '006' UNION ALL SELECT 7, '007' UNION ALL SELECT 8, '008' UNION ALL SELECT 9, '009' UNION ALL SELECT 10, '010'
    UNION ALL SELECT 11, '011' UNION ALL SELECT 12, '012' UNION ALL SELECT 13, '013' UNION ALL SELECT 14, '014' UNION ALL SELECT 15, '015'
    UNION ALL SELECT 16, '016' UNION ALL SELECT 17, '017' UNION ALL SELECT 18, '018' UNION ALL SELECT 19, '019' UNION ALL SELECT 20, '020'
    UNION ALL SELECT 21, '021' UNION ALL SELECT 22, '022' UNION ALL SELECT 23, '023' UNION ALL SELECT 24, '024' UNION ALL SELECT 25, '025'
    UNION ALL SELECT 26, '026' UNION ALL SELECT 27, '027' UNION ALL SELECT 28, '028' UNION ALL SELECT 29, '029' UNION ALL SELECT 30, '030'
    UNION ALL SELECT 31, '031' UNION ALL SELECT 32, '032' UNION ALL SELECT 33, '033' UNION ALL SELECT 34, '034' UNION ALL SELECT 35, '035'
    UNION ALL SELECT 36, '036' UNION ALL SELECT 37, '037' UNION ALL SELECT 38, '038' UNION ALL SELECT 39, '039' UNION ALL SELECT 40, '040'
    UNION ALL SELECT 41, '041' UNION ALL SELECT 42, '042' UNION ALL SELECT 43, '043' UNION ALL SELECT 44, '044' UNION ALL SELECT 45, '045'
    UNION ALL SELECT 46, '046' UNION ALL SELECT 47, '047' UNION ALL SELECT 48, '048' UNION ALL SELECT 49, '049' UNION ALL SELECT 50, '050'
) s
CROSS JOIN (VALUES (1), (2), (3)) AS r(rn)
ON CONFLICT DO NOTHING;
