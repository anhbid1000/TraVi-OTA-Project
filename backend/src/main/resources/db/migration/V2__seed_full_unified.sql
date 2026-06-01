-- =============================================================
-- V2__seed_full_unified.sql
-- Full sample data (large + diverse) for integrated testing
-- =============================================================

-- 0) ROLES
INSERT INTO vai_tro (id, ten, mo_ta) VALUES
('role-guest', 'KHACH_HANG', 'Khach hang su dung dich vu'),
('role-partner', 'DOI_TAC', 'Doi tac cung cap dich vu'),
('role-admin', 'QUAN_TRI_VIEN', 'Quan tri he thong')
ON CONFLICT DO NOTHING;

-- 1) USERS
-- Password hash (bcrypt): 123456Aa@
INSERT INTO users (id, email, username, mat_khau, ho_ten, ngay_sinh, gioi_tinh, so_dien_thoai, trang_thai, vai_tro_id) VALUES
('u-admin-1', 'admin@travi.vn', 'admin.root', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Nguyen Quan Tri', '1992-02-10', 'NAM', '0909990001', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'QUAN_TRI_VIEN')),

('u-partner-1', 'partner.saigon.hotel@travi.vn', 'saigon_hospitality', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Cong ty Sai Gon Hospitality', '1988-06-12', 'NAM', '0902000001', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'DOI_TAC')),
('u-partner-2', 'partner.danang.fnb@travi.vn', 'danang_fnb_group', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Danang F&B Group', '1990-09-19', 'NU', '0902000002', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'DOI_TAC')),
('u-partner-3', 'partner.nhatrang.travel@travi.vn', 'nhatrang_travel_hub', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Nha Trang Travel Hub', '1987-01-28', 'NAM', '0902000003', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'DOI_TAC')),
('u-partner-4', 'partner.phuquoc.stay@travi.vn', 'phuquoc_stay_plus', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Phu Quoc Stay Plus', '1991-03-08', 'NU', '0902000004', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'DOI_TAC')),

('u-guest-1', 'an.nguyen@example.com', 'annguyen', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Nguyen Quoc An', '1998-04-15', 'NAM', '0903000001', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'KHACH_HANG')),
('u-guest-2', 'binh.tran@example.com', 'tranbinh', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Tran Thi Binh', '1997-11-23', 'NU', '0903000002', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'KHACH_HANG')),
('u-guest-3', 'chi.le@example.com', 'lechi', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Le Minh Chi', '2001-08-09', 'NU', '0903000003', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'KHACH_HANG')),
('u-guest-4', 'duy.pham@example.com', 'phamduy', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Pham Hoang Duy', '1995-05-31', 'NAM', '0903000004', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'KHACH_HANG')),
('u-guest-5', 'em.vo@example.com', 'voem', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Vo Thanh Em', '1999-12-02', 'KHAC', '0903000005', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'KHACH_HANG')),
('u-guest-6', 'giang.bui@example.com', 'buigiang', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Bui Thu Giang', '1996-07-17', 'NU', '0903000006', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'KHACH_HANG')),
('u-guest-7', 'hung.do@example.com', 'dohung', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Do Quang Hung', '2000-01-21', 'NAM', '0903000007', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'KHACH_HANG')),
('u-guest-8', 'khanh.ngo@example.com', 'ngokhanh', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Ngo Bao Khanh', '2002-10-12', 'NU', '0903000008', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'KHACH_HANG'))
ON CONFLICT DO NOTHING;

INSERT INTO doi_tac (id, ti_le_chiet_khau) VALUES
('u-partner-1', 12.0),
('u-partner-2', 10.5),
('u-partner-3', 9.5),
('u-partner-4', 11.0)
ON CONFLICT DO NOTHING;

INSERT INTO khach_hang (id, diem_thanh_vien, hang_thanh_vien, tong_chi_tieu) VALUES
('u-guest-1', 140, 'BAC', 6200000),
('u-guest-2', 980, 'VANG', 26800000),
('u-guest-3', 45, 'DONG', 1800000),
('u-guest-4', 1560, 'KIM_CUONG', 61200000),
('u-guest-5', 300, 'BAC', 9200000),
('u-guest-6', 640, 'VANG', 17500000),
('u-guest-7', 220, 'BAC', 7400000),
('u-guest-8', 10, 'DONG', 450000)
ON CONFLICT DO NOTHING;

ALTER TABLE IF EXISTS ban ALTER COLUMN deleted SET DEFAULT FALSE;
ALTER TABLE IF EXISTS danh_gia ALTER COLUMN deleted SET DEFAULT FALSE;
ALTER TABLE IF EXISTS don_dat_cho ALTER COLUMN deleted SET DEFAULT FALSE;
ALTER TABLE IF EXISTS khuyen_mai ALTER COLUMN deleted SET DEFAULT FALSE;
ALTER TABLE IF EXISTS mon_an ALTER COLUMN deleted SET DEFAULT FALSE;
ALTER TABLE IF EXISTS phong ALTER COLUMN deleted SET DEFAULT FALSE;

-- 2) PREFERENCES
INSERT INTO danh_muc_so_thich (id, ten_danh_muc, mo_ta) VALUES
('pref-cat-1', 'AM_THUC', 'So thich lien quan an uong'),
('pref-cat-2', 'DU_LICH', 'So thich lien quan du lich nghi duong'),
('pref-cat-3', 'TRAI_NGHIEM', 'So thich trai nghiem dac biet')
ON CONFLICT DO NOTHING;

INSERT INTO so_thich (id, ten_so_thich, danh_muc_id) VALUES
('pref-1', 'Hai san', 'pref-cat-1'),
('pref-2', 'Do nuong', 'pref-cat-1'),
('pref-3', 'Nghi duong bien', 'pref-cat-2'),
('pref-4', 'Homestay', 'pref-cat-2'),
('pref-5', 'Spa', 'pref-cat-3'),
('pref-6', 'Am nhac song', 'pref-cat-3')
ON CONFLICT DO NOTHING;

INSERT INTO khach_hang_so_thich (khach_hang_id, so_thich_id) VALUES
('u-guest-1', 'pref-3'), ('u-guest-1', 'pref-5'),
('u-guest-2', 'pref-1'), ('u-guest-2', 'pref-2'),
('u-guest-3', 'pref-4'),
('u-guest-4', 'pref-3'), ('u-guest-4', 'pref-6'),
('u-guest-5', 'pref-2'),
('u-guest-6', 'pref-1'), ('u-guest-6', 'pref-5'),
('u-guest-7', 'pref-3'),
('u-guest-8', 'pref-4')
ON CONFLICT DO NOTHING;

INSERT INTO khach_hang_tu_khoa (khach_hang_id, tu_khoa) VALUES
('u-guest-1', 'khach san gan bien'),
('u-guest-1', 'spa massage'),
('u-guest-2', 'lau hai san'),
('u-guest-4', 'resort 5 sao'),
('u-guest-6', 'fine dining da nang'),
('u-guest-8', 'homestay gia re')
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS so_thich_nguoi_dung (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    danh_muc VARCHAR(255) NOT NULL,
    diem_so INTEGER NOT NULL DEFAULT 0,
    cap_nhat_cuoi TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stnd_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_so_thich_user_danh_muc UNIQUE (user_id, danh_muc)
);

INSERT INTO so_thich_nguoi_dung (user_id, danh_muc, diem_so) VALUES
('u-guest-1', 'AM_THUC', 42),
('u-guest-1', 'DU_LICH', 88),
('u-guest-2', 'AM_THUC', 91),
('u-guest-3', 'TRAI_NGHIEM', 55),
('u-guest-4', 'DU_LICH', 97),
('u-guest-5', 'AM_THUC', 63),
('u-guest-6', 'TRAI_NGHIEM', 72),
('u-guest-7', 'DU_LICH', 67),
('u-guest-8', 'DU_LICH', 21)
ON CONFLICT DO NOTHING;

-- 3) BUSINESS PROFILES + ASSETS
INSERT INTO ho_so_kinh_doanh (
    id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh,
    toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho, kinh_do, vi_do, trang_thai_hoat_dong
) VALUES
('hskd-ks-1', 'u-partner-1', 'KHACH_SAN', 'Lotus Riverside Hotel', '0319000001', 'GPKD-HCM-9001', '10.7752,106.7008', '02838110001', 'lotus@hotel.vn', '12 Ton Duc Thang, Quan 1', 'Ho Chi Minh', 106.7008, 10.7752, 'DANG_HOAT_DONG'),
('hskd-ks-2', 'u-partner-3', 'KHACH_SAN', 'Coral Bay Resort', '4209000002', 'GPKD-KH-9002', '12.2387,109.1969', '02583880002', 'coral@resort.vn', '18 Tran Phu, Nha Trang', 'Nha Trang', 109.1969, 12.2387, 'DANG_HOAT_DONG'),
('hskd-ks-3', 'u-partner-4', 'KHACH_SAN', 'Sunset Phu Quoc Stay', '1709000003', 'GPKD-PQ-9003', '10.2271,103.9672', '02973990003', 'sunset@stay.vn', '6 Tran Hung Dao, Duong Dong', 'Phu Quoc', 103.9672, 10.2271, 'DANG_HOAT_DONG'),
('hskd-nh-1', 'u-partner-2', 'NHA_HANG', 'Da Nang Seafood Hub', '0409000004', 'GPKD-DN-9004', '16.0641,108.2238', '02363660004', 'seafood@hub.vn', '90 Vo Nguyen Giap, Son Tra', 'Da Nang', 108.2238, 16.0641, 'DANG_HOAT_DONG'),
('hskd-nh-2', 'u-partner-1', 'NHA_HANG', 'Saigon Garden Bistro', '0319000005', 'GPKD-HCM-9005', '10.7739,106.7040', '02838110005', 'garden@bistro.vn', '20 Nguyen Hue, Quan 1', 'Ho Chi Minh', 106.7040, 10.7739, 'DANG_HOAT_DONG'),
('hskd-nh-3', 'u-partner-3', 'NHA_HANG', 'Nha Trang Grill & Chill', '4209000006', 'GPKD-KH-9006', '12.2451,109.1941', '02583880006', 'grillchill@nt.vn', '68 Nguyen Thien Thuat', 'Nha Trang', 109.1941, 12.2451, 'DANG_HOAT_DONG')
ON CONFLICT DO NOTHING;

ALTER TABLE tai_san ADD COLUMN IF NOT EXISTS rating_average DOUBLE PRECISION DEFAULT 0.0;
ALTER TABLE tai_san ADD COLUMN IF NOT EXISTS review_count INTEGER DEFAULT 0;
ALTER TABLE tai_san ADD COLUMN IF NOT EXISTS dia_chi VARCHAR(255);
ALTER TABLE tai_san ADD COLUMN IF NOT EXISTS thanh_pho VARCHAR(100);
ALTER TABLE tai_san ADD COLUMN IF NOT EXISTS kinh_do DOUBLE PRECISION;
ALTER TABLE tai_san ADD COLUMN IF NOT EXISTS vi_do DOUBLE PRECISION;

INSERT INTO tai_san (
    id_tai_san, ho_so_kinh_doanh_id, mo_ta, trang_thai, gia_co_ban, is_dynamic_pricing, rating_average, review_count,
    dia_chi, thanh_pho, kinh_do, vi_do
) VALUES
('asset-ks-1', 'hskd-ks-1', 'Khach san trung tam quan 1, phu hop cong tac va du lich', 'SAN_SANG', 1350000, TRUE, 4.6, 214, '12 Ton Duc Thang, Quan 1', 'Ho Chi Minh', 106.7008, 10.7752),
('asset-ks-2', 'hskd-ks-2', 'Resort view bien, co khu vui choi gia dinh', 'SAN_SANG', 2200000, TRUE, 4.7, 188, '18 Tran Phu, Nha Trang', 'Nha Trang', 109.1969, 12.2387),
('asset-ks-3', 'hskd-ks-3', 'Khach san gan bai bien, phong gia dinh rong', 'SAN_SANG', 1750000, FALSE, 4.4, 96, '6 Tran Hung Dao, Duong Dong', 'Phu Quoc', 103.9672, 10.2271),
('asset-nh-1', 'hskd-nh-1', 'Nha hang hai san tuoi song, phuc vu nhom dong', 'SAN_SANG', 280000, FALSE, 4.5, 141, '90 Vo Nguyen Giap, Son Tra', 'Da Nang', 108.2238, 16.0641),
('asset-nh-2', 'hskd-nh-2', 'Bistro phong cach vuon giua trung tam sai gon', 'SAN_SANG', 240000, FALSE, 4.3, 110, '20 Nguyen Hue, Quan 1', 'Ho Chi Minh', 106.7040, 10.7739),
('asset-nh-3', 'hskd-nh-3', 'Nhau nuong va am nhac song vao cuoi tuan', 'SAN_SANG', 190000, TRUE, 4.2, 87, '68 Nguyen Thien Thuat', 'Nha Trang', 109.1941, 12.2451)
ON CONFLICT DO NOTHING;

INSERT INTO khach_san (id_tai_san, ten, hang_sao, loai_khach_san, gio_nhan_phong, gio_tra_phong, gio_nhan_phong_mac_dinh, gio_tra_phong_mac_dinh, so_tang, tong_so_phong) VALUES
('asset-ks-1', 'Lotus Riverside Hotel', 4, 'KHACH_SAN', '14:00', '12:00', '14:00', '12:00', 16, 140),
('asset-ks-2', 'Coral Bay Resort', 5, 'RESORT', '15:00', '11:00', '15:00', '11:00', 9, 92),
('asset-ks-3', 'Sunset Phu Quoc Stay', 4, 'KHACH_SAN', '14:00', '12:00', '14:00', '12:00', 11, 84)
ON CONFLICT DO NOTHING;

INSERT INTO nha_hang (id_tai_san, ten, loai_am_thuc, gio_mo_cua, gio_dong_cua, suc_chua, co_dat_ban_truoc, co_dat_mon_truoc) VALUES
('asset-nh-1', 'Da Nang Seafood Hub', 'HAI_SAN', '09:00', '23:00', 220, TRUE, TRUE),
('asset-nh-2', 'Saigon Garden Bistro', 'AU_A', '10:00', '22:30', 120, TRUE, TRUE),
('asset-nh-3', 'Nha Trang Grill & Chill', 'NUONG_BBQ', '11:00', '23:30', 180, TRUE, FALSE)
ON CONFLICT DO NOTHING;

-- 4) AMENITIES + ROOMS + TABLES + MENU
INSERT INTO tien_ich_khach_san (id, ten_tien_ich, loai_tien_ich, mo_ta) VALUES
('amen-ks-1', 'Ho boi', 'GIAI_TRI', 'Ho boi ngoai troi'),
('amen-ks-2', 'Gym', 'SUC_KHOE', 'Phong tap day du thiet bi'),
('amen-ks-3', 'Spa', 'THU_GIAN', 'Dich vu massage va cham soc da'),
('amen-ks-4', 'Xe dua don san bay', 'DI_CHUYEN', 'Xe 7 cho theo lich hen'),
('amen-ks-5', 'Buffet sang', 'AM_THUC', 'Buffet sang 6:00 - 10:00'),
('amen-ks-6', 'Tre em', 'GIA_DINH', 'Khu vui choi tre em')
ON CONFLICT DO NOTHING;

INSERT INTO khach_san_tien_ich (khach_san_id, tien_ich_id) VALUES
('asset-ks-1', 'amen-ks-1'), ('asset-ks-1', 'amen-ks-2'), ('asset-ks-1', 'amen-ks-5'),
('asset-ks-2', 'amen-ks-1'), ('asset-ks-2', 'amen-ks-2'), ('asset-ks-2', 'amen-ks-3'), ('asset-ks-2', 'amen-ks-6'),
('asset-ks-3', 'amen-ks-1'), ('asset-ks-3', 'amen-ks-4'), ('asset-ks-3', 'amen-ks-5')
ON CONFLICT DO NOTHING;

INSERT INTO tien_ich_nha_hang (id, nha_hang_id, ten_tien_ich, loai_tien_ich, mo_ta, co_thu_phi, phi_su_dung) VALUES
('amen-nh-1', 'asset-nh-1', 'Phong VIP', 'KHONG_GIAN', 'Phong rieng cho nhom', TRUE, 300000),
('amen-nh-2', 'asset-nh-1', 'Ban view bien', 'KHONG_GIAN', 'View truc dien bien', FALSE, 0),
('amen-nh-3', 'asset-nh-2', 'Nhac acoustic', 'GIAI_TRI', 'Bieu dien thu 6-7-CN', FALSE, 0),
('amen-nh-4', 'asset-nh-3', 'Khu nuong ngoai troi', 'TRAI_NGHIEM', 'Nuong tai ban', TRUE, 120000)
ON CONFLICT DO NOTHING;

ALTER TABLE phong ALTER COLUMN deleted SET DEFAULT FALSE;

INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia) VALUES
('room-ks1-1', 'asset-ks-1', '501', 'Deluxe City View', 'DELUXE', 'Phong view pho', 2, 1, 32, 1550000, 12, 'SAN_SANG', 5),
('room-ks1-2', 'asset-ks-1', '701', 'Executive River View', 'EXECUTIVE', 'Phong huong song', 3, 2, 45, 2450000, 8, 'SAN_SANG', 10),
('room-ks1-3', 'asset-ks-1', '901', 'Family Suite', 'SUITE', 'Phong gia dinh', 4, 2, 58, 3200000, 4, 'SAN_SANG', 0),

('room-ks2-1', 'asset-ks-2', 'B12', 'Garden Villa', 'VILLA', 'Villa co san vuon', 4, 2, 74, 4100000, 6, 'SAN_SANG', 0),
('room-ks2-2', 'asset-ks-2', 'C08', 'Ocean Front Suite', 'SUITE', 'View truc dien bien', 3, 1, 62, 4650000, 5, 'SAN_SANG', 0),

('room-ks3-1', 'asset-ks-3', '303', 'Superior Double', 'SUPERIOR', 'Phong tieu chuan', 2, 1, 28, 1420000, 10, 'SAN_SANG', 8),
('room-ks3-2', 'asset-ks-3', '505', 'Premium Family', 'FAMILY', 'Phong 2 phong ngu', 5, 3, 68, 3350000, 3, 'SAN_SANG', 0)
ON CONFLICT DO NOTHING;

INSERT INTO phong_tien_ich (phong_id, tien_ich) VALUES
('room-ks1-1', 'WIFI'), ('room-ks1-1', 'DIEU_HOA'),
('room-ks1-2', 'BAN_CONG'), ('room-ks1-2', 'BON_TAM'),
('room-ks1-3', 'TU_LANH'),
('room-ks2-1', 'VIEW_DEP'),
('room-ks2-2', 'TV'),
('room-ks3-1', 'TV'),
('room-ks3-2', 'TU_LANH')
ON CONFLICT DO NOTHING;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'ban'
          AND column_name = 'trang_thai'
          AND data_type IN ('integer', 'smallint', 'bigint')
    ) THEN
        ALTER TABLE ban ALTER COLUMN trang_thai TYPE VARCHAR(50)
        USING CASE trang_thai
            WHEN 0 THEN 'SAN_SANG'
            WHEN 1 THEN 'TAM_DUNG'
            WHEN 2 THEN 'NGUNG_SU_DUNG'
            ELSE 'SAN_SANG'
        END;
    END IF;
END $$;

ALTER TABLE ban ALTER COLUMN trang_thai SET DEFAULT 'SAN_SANG';
ALTER TABLE ban ALTER COLUMN deleted SET DEFAULT FALSE;

INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi) VALUES
('ban-nh1-1', 'asset-nh-1', 'Ban Cua So 01', 'Tang 1', 'Gan cua so', 'SAN_SANG', 4),
('ban-nh1-2', 'asset-nh-1', 'Ban VIP 02', 'Tang 2', 'Phong rieng', 'SAN_SANG', 8),
('ban-nh2-1', 'asset-nh-2', 'Ban Vuon 01', 'San vuon', 'Ngoai troi', 'SAN_SANG', 6),
('ban-nh2-2', 'asset-nh-2', 'Ban Trong Nha 05', 'Tang tret', 'Gan san khau', 'SAN_SANG', 4),
('ban-nh3-1', 'asset-nh-3', 'Ban BBQ 10', 'Khu nuong', 'Ban nuong than hoa', 'SAN_SANG', 6),
('ban-nh3-2', 'asset-nh-3', 'Ban Nhom 12', 'Khu trong nha', 'Phu hop tiec nho', 'SAN_SANG', 10)
ON CONFLICT DO NOTHING;

INSERT INTO anh_khach_san (id, khach_san_id, duong_dan_url, mo_ta_anh, la_anh_dai_dien, ngay_tai_len) VALUES
('img-ks-1', 'asset-ks-1', 'https://cdn.travi.vn/ks1/front.jpg', 'Mat tien khach san', TRUE, '2026-04-12'),
('img-ks-2', 'asset-ks-2', 'https://cdn.travi.vn/ks2/pool.jpg', 'Ho boi vo cuc', TRUE, '2026-04-13'),
('img-ks-3', 'asset-ks-3', 'https://cdn.travi.vn/ks3/lobby.jpg', 'Sanh chinh', TRUE, '2026-04-14')
ON CONFLICT DO NOTHING;

INSERT INTO anh_nha_hang (id, nha_hang_id, duong_dan_url, mo_ta_anh, la_anh_dai_dien, ngay_tai_len) VALUES
('img-nh-1', 'asset-nh-1', 'https://cdn.travi.vn/nh1/main.jpg', 'Khong gian chinh', TRUE, '2026-04-15'),
('img-nh-2', 'asset-nh-2', 'https://cdn.travi.vn/nh2/garden.jpg', 'Khu vuon', TRUE, '2026-04-16'),
('img-nh-3', 'asset-nh-3', 'https://cdn.travi.vn/nh3/bbq.jpg', 'Khu nuong', TRUE, '2026-04-17')
ON CONFLICT DO NOTHING;

INSERT INTO anh_phong (id, phong_id, duong_dan_url, mo_ta_anh, la_anh_dai_dien, ngay_tai_len) VALUES
('img-room-1', 'room-ks1-1', 'https://cdn.travi.vn/room/ks1-1.jpg', 'Phong Deluxe', TRUE, '2026-04-20'),
('img-room-2', 'room-ks2-2', 'https://cdn.travi.vn/room/ks2-2.jpg', 'Ocean suite', TRUE, '2026-04-20'),
('img-room-3', 'room-ks3-2', 'https://cdn.travi.vn/room/ks3-2.jpg', 'Family premium', TRUE, '2026-04-20')
ON CONFLICT DO NOTHING;

INSERT INTO thuc_don (id, nha_hang_id, ten_thuc_don, phan_loai, trang_thai) VALUES
('menu-nh1-main', 'asset-nh-1', 'Thuc don hai san', 'A_LA_CARTE', 'DANG_HIEN_THI'),
('menu-nh2-main', 'asset-nh-2', 'Thuc don Au A', 'A_LA_CARTE', 'DANG_HIEN_THI'),
('menu-nh3-bbq', 'asset-nh-3', 'Set nuong BBQ', 'SET_MENU', 'DANG_HIEN_THI')
ON CONFLICT DO NOTHING;

ALTER TABLE mon_an DROP CONSTRAINT IF EXISTS mon_an_trang_thai_check;
ALTER TABLE mon_an ADD CONSTRAINT mon_an_trang_thai_check
    CHECK (trang_thai IN ('DANG_BAN', 'CO_SAN', 'TAM_HET'));

INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted) VALUES
('dish-1', 'menu-nh1-main', 'Tom hum nuong pho mai', 'Tom hum tuoi nuong', 690000, 'HAI_SAN', 'DANG_BAN', 'https://cdn.travi.vn/dish/tomhum.jpg', FALSE),
('dish-2', 'menu-nh1-main', 'Muc hap gung hanh', 'Muc tuoi hap', 220000, 'HAI_SAN', 'DANG_BAN', 'https://cdn.travi.vn/dish/muc.jpg', FALSE),
('dish-3', 'menu-nh2-main', 'Steak bo my', 'Than bo medium rare', 390000, 'MON_CHINH', 'DANG_BAN', 'https://cdn.travi.vn/dish/steak.jpg', FALSE),
('dish-4', 'menu-nh2-main', 'Salad ca hoi xong khoi', 'Rau huu co', 190000, 'KHAI_VI', 'DANG_BAN', 'https://cdn.travi.vn/dish/salad.jpg', FALSE),
('dish-5', 'menu-nh3-bbq', 'Set nuong 2 nguoi', 'Thit bo + hai san', 520000, 'SET', 'DANG_BAN', 'https://cdn.travi.vn/dish/set2.jpg', FALSE),
('dish-6', 'menu-nh3-bbq', 'Set nuong 4 nguoi', 'Set tong hop', 980000, 'SET', 'DANG_BAN', 'https://cdn.travi.vn/dish/set4.jpg', FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO mon_an_the_ngu_canh (mon_an_id, the_ngu_canh) VALUES
('dish-1', 'MON_NOI_BAT'),
('dish-1', 'PHU_HOP_NHOM'),
('dish-3', 'TIEC_TOI'),
('dish-4', 'AN_KIENG'),
('dish-5', 'BBQ'),
('dish-6', 'GIA_DINH')
ON CONFLICT DO NOTHING;

INSERT INTO combo (id, thuc_don_id, ten_combo, mo_ta, gia_combo, ngay_bat_dau, ngay_ket_thuc, trang_thai) VALUES
('combo-1', 'menu-nh1-main', 'Combo Hai San Cap Doi', '2 mon hai san + 2 nuoc', 820000, '2026-05-01', '2026-12-31', 1),
('combo-2', 'menu-nh3-bbq', 'Combo BBQ Ban Be', 'Set nuong 4 nguoi + bia', 1150000, '2026-05-01', '2026-12-31', 1)
ON CONFLICT DO NOTHING;

INSERT INTO combo_mon_an (combo_id, mon_an_id) VALUES
('combo-1', 'dish-1'),
('combo-1', 'dish-2'),
('combo-2', 'dish-5'),
('combo-2', 'dish-6')
ON CONFLICT DO NOTHING;

INSERT INTO combo_item (id, combo_id, mon_an_id, so_luong) VALUES
('combo-item-1', 'combo-1', 'dish-1', 1),
('combo-item-2', 'combo-1', 'dish-2', 1),
('combo-item-3', 'combo-2', 'dish-5', 1),
('combo-item-4', 'combo-2', 'dish-6', 1)
ON CONFLICT DO NOTHING;

INSERT INTO chinh_sach (
    id, loai_chinh_sach, noi_dung, ngay_ap_dung, gio_nhan_phong, gio_tra_phong, gio_mo_cua, gio_dong_cua,
    chinh_sach_huy, chinh_sach_hoan_tien, quy_dinh_tre_em, quy_dinh_vat_nuoi, ghi_chu_khac, ho_so_kinh_doanh_id
) VALUES
('policy-ks-1', 'KHACH_SAN', 'Chinh sach luu tru co ban', '2026-01-01', '14:00', '12:00', NULL, NULL, 'Huy truoc 48h', 'Hoan 100%', 'Tre duoi 6 tuoi mien phi', 'Khong vat nuoi', 'Nhan vien ho tro 24/7', 'hskd-ks-1'),
('policy-ks-2', 'KHACH_SAN', 'Chinh sach resort', '2026-01-01', '15:00', '11:00', NULL, NULL, 'Huy truoc 72h', 'Hoan 70%', 'Tre duoi 5 tuoi mien phi', 'Vat nuoi nho duoc phep', 'Phu thu cuoi tuan', 'hskd-ks-2'),
('policy-ks-3', 'KHACH_SAN', 'Chinh sach phu quoc', '2026-01-01', '14:00', '12:00', NULL, NULL, 'Huy truoc 24h', 'Hoan 80%', 'Tre duoi 7 tuoi mien phi', 'Khong vat nuoi', 'Co dua don san bay', 'hskd-ks-3'),
('policy-nh-1', 'NHA_HANG', 'Dat ban va giu cho', '2026-01-01', NULL, NULL, '09:00', '23:00', 'Huy truoc 2h', 'Khong ap dung', NULL, NULL, 'Giu ban 15 phut', 'hskd-nh-1'),
('policy-nh-2', 'NHA_HANG', 'Dat ban bistro', '2026-01-01', NULL, NULL, '10:00', '22:30', 'Huy truoc 1h', 'Khong ap dung', NULL, NULL, 'Can coc voi nhom >10 nguoi', 'hskd-nh-2'),
('policy-nh-3', 'NHA_HANG', 'Dat ban BBQ', '2026-01-01', NULL, NULL, '11:00', '23:30', 'Huy truoc 3h', 'Khong ap dung', NULL, NULL, 'Ban nuong gioi han 120 phut', 'hskd-nh-3')
ON CONFLICT DO NOTHING;

-- 5) BOOKINGS + ORDERS
INSERT INTO don_dat_cho (
    id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan,
    ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai, hold_expired_at, payment_expired_at
) VALUES
('book-ks-1', 'OD-KS-0001', 'u-guest-1', 'hskd-ks-1', '2026-05-02 09:10:00', 4650000, 250000, 4400000, 'Nguyen Quoc An', '0903000001', 'an.nguyen@example.com', 'Can phong cao tang', 'DA_HOAN_THANH', '2026-05-02 09:25:00', '2026-05-02 09:40:00'),
('book-ks-2', 'OD-KS-0002', 'u-guest-2', 'hskd-ks-2', '2026-05-03 11:20:00', 9300000, 300000, 9000000, 'Tran Thi Binh', '0903000002', 'binh.tran@example.com', 'Can them giuong phu', 'DA_HOAN_THANH', '2026-05-03 11:35:00', '2026-05-03 11:50:00'),
('book-ks-3', 'OD-KS-0003', 'u-guest-4', 'hskd-ks-3', '2026-05-04 18:02:00', 3350000, 0, 3350000, 'Pham Hoang Duy', '0903000004', 'duy.pham@example.com', 'Check-in muon', 'DA_HOAN_THANH', '2026-05-04 18:17:00', '2026-05-04 18:32:00'),
('book-ks-4', 'OD-KS-0004', 'u-guest-6', 'hskd-ks-1', '2026-05-07 14:15:00', 3100000, 100000, 3000000, 'Bui Thu Giang', '0903000006', 'giang.bui@example.com', 'Gan thang may', 'DA_XAC_NHAN', '2026-05-07 14:30:00', '2026-05-07 14:45:00'),

('book-nh-1', 'OD-NH-0001', 'u-guest-1', 'hskd-nh-1', '2026-05-10 16:30:00', 910000, 50000, 860000, 'Nguyen Quoc An', '0903000001', 'an.nguyen@example.com', 'Ban view bien', 'DA_HOAN_THANH', '2026-05-10 16:45:00', '2026-05-10 17:00:00'),
('book-nh-2', 'OD-NH-0002', 'u-guest-3', 'hskd-nh-2', '2026-05-11 12:00:00', 580000, 0, 580000, 'Le Minh Chi', '0903000003', 'chi.le@example.com', 'Sinh nhat nho', 'DA_HOAN_THANH', '2026-05-11 12:15:00', '2026-05-11 12:30:00'),
('book-nh-3', 'OD-NH-0003', 'u-guest-7', 'hskd-nh-3', '2026-05-12 19:25:00', 1200000, 120000, 1080000, 'Do Quang Hung', '0903000007', 'hung.do@example.com', 'Nhom 8 nguoi', 'DA_HOAN_THANH', '2026-05-12 19:40:00', '2026-05-12 19:55:00')
ON CONFLICT DO NOTHING;

INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien) VALUES
('book-ks-1', '2026-05-16', '2026-05-18', 2, 2, '14:30'),
('book-ks-2', '2026-05-20', '2026-05-23', 3, 3, '15:00'),
('book-ks-3', '2026-05-25', '2026-05-26', 1, 2, '21:00'),
('book-ks-4', '2026-06-02', '2026-06-04', 2, 2, '14:00')
ON CONFLICT DO NOTHING;

INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien) VALUES
('book-ks-ct-1', 'book-ks-1', 'room-ks1-2', 'Executive River View', 1, 2450000, 2, 4900000),
('book-ks-ct-2', 'book-ks-2', 'room-ks2-2', 'Ocean Front Suite', 1, 4650000, 2, 9300000),
('book-ks-ct-3', 'book-ks-3', 'room-ks3-2', 'Premium Family', 1, 3350000, 1, 3350000),
('book-ks-ct-4', 'book-ks-4', 'room-ks1-1', 'Deluxe City View', 1, 1550000, 2, 3100000)
ON CONFLICT DO NOTHING;

INSERT INTO dat_phong (id, don_dat_cho_id, phong_id, ngay_check_in, ngay_check_out, ghi_chu_khach_hang_phong_don_dat) VALUES
('dp-1', 'book-ks-1', 'room-ks1-2', '2026-05-16', '2026-05-18', 'Khong hut thuoc'),
('dp-2', 'book-ks-2', 'room-ks2-2', '2026-05-20', '2026-05-23', 'Them giuong phu'),
('dp-3', 'book-ks-3', 'room-ks3-2', '2026-05-25', '2026-05-26', 'Nhan phong tre')
ON CONFLICT DO NOTHING;

INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc) VALUES
('book-nh-1', '2026-05-14 19:00:00', '2026-05-14 21:00:00', 4, 200000, TRUE),
('book-nh-2', '2026-05-15 18:30:00', '2026-05-15 20:00:00', 3, 100000, TRUE),
('book-nh-3', '2026-05-16 20:00:00', '2026-05-16 22:00:00', 8, 350000, TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO don_nha_hang_ban (id, don_nha_hang_id, ban_id) VALUES
('nh-ban-1', 'book-nh-1', 'ban-nh1-1'),
('nh-ban-2', 'book-nh-2', 'ban-nh2-1'),
('nh-ban-3', 'book-nh-3', 'ban-nh3-2')
ON CONFLICT DO NOTHING;

INSERT INTO don_dat_mon (id, don_dat_cho_id, nha_hang_id, ban_id, thoi_gian_dat, ghi_chu) VALUES
('ddm-1', 'book-nh-1', 'asset-nh-1', 'ban-nh1-1', '2026-05-14 19:05:00', 'Uu tien mon it cay'),
('ddm-2', 'book-nh-2', 'asset-nh-2', 'ban-nh2-1', '2026-05-15 18:35:00', 'Trang tri sinh nhat nho'),
('ddm-3', 'book-nh-3', 'asset-nh-3', 'ban-nh3-2', '2026-05-16 20:05:00', 'Them 2 suat an chay')
ON CONFLICT DO NOTHING;

INSERT INTO chi_tiet_don_dat_mon (id, don_dat_mon_id, mon_an_id, so_luong, gia_tien) VALUES
('ddm-ct-1', 'ddm-1', 'dish-1', 1, 690000),
('ddm-ct-2', 'ddm-1', 'dish-2', 1, 220000),
('ddm-ct-3', 'ddm-2', 'dish-3', 1, 390000),
('ddm-ct-4', 'ddm-2', 'dish-4', 1, 190000),
('ddm-ct-5', 'ddm-3', 'dish-5', 1, 520000),
('ddm-ct-6', 'ddm-3', 'dish-6', 1, 980000)
ON CONFLICT DO NOTHING;

-- 6) REVIEWS + REPLIES + ASPECT SCORE
CREATE TABLE IF NOT EXISTS review_danh_gia (
    id VARCHAR(36) PRIMARY KEY,
    khach_hang_id VARCHAR(36) NOT NULL,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL,
    loai_dich_vu VARCHAR(50) NOT NULL,
    booking_id VARCHAR(36),
    reservation_id VARCHAR(36),
    so_sao INTEGER NOT NULL,
    noi_dung TEXT NOT NULL,
    trang_thai VARCHAR(50) NOT NULL DEFAULT 'DA_HIEN_THI',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_khach_hang FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_ho_so FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS review_aspect_score (
    id VARCHAR(36) PRIMARY KEY,
    review_id VARCHAR(36) NOT NULL,
    aspect VARCHAR(50) NOT NULL,
    score INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_aspect_review FOREIGN KEY (review_id) REFERENCES review_danh_gia(id) ON DELETE CASCADE,
    CONSTRAINT uk_review_aspect UNIQUE (review_id, aspect)
);

CREATE TABLE IF NOT EXISTS review_phan_hoi_partner (
    id VARCHAR(36) PRIMARY KEY,
    review_id VARCHAR(36) NOT NULL UNIQUE,
    partner_id VARCHAR(36) NOT NULL,
    noi_dung TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_reply_review FOREIGN KEY (review_id) REFERENCES review_danh_gia(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_reply_partner FOREIGN KEY (partner_id) REFERENCES doi_tac(id) ON DELETE CASCADE
);

INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, so_sao, noi_dung, trang_thai, created_at, updated_at) VALUES
('review-ks-1', 'u-guest-1', 'hskd-ks-1', 'KHACH_SAN', 'book-ks-1', 5, 'Phong sach, nhan vien than thien, vi tri rat tien.', 'DA_HIEN_THI', '2026-05-18 13:00:00', '2026-05-18 13:00:00'),
('review-ks-2', 'u-guest-2', 'hskd-ks-2', 'KHACH_SAN', 'book-ks-2', 4, 'View dep, buffet on. Check-in hoi dong.', 'DA_HIEN_THI', '2026-05-23 10:20:00', '2026-05-23 10:20:00'),
('review-ks-3', 'u-guest-4', 'hskd-ks-3', 'KHACH_SAN', 'book-ks-3', 3, 'Phong rong nhung cach am chua tot.', 'DA_HIEN_THI', '2026-05-26 11:10:00', '2026-05-26 11:10:00')
ON CONFLICT DO NOTHING;

INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, so_sao, noi_dung, trang_thai, created_at, updated_at) VALUES
('review-nh-1', 'u-guest-1', 'hskd-nh-1', 'NHA_HANG', 'book-nh-1', 5, 'Hai san tuoi, phuc vu nhanh.', 'DA_HIEN_THI', '2026-05-14 22:10:00', '2026-05-14 22:10:00'),
('review-nh-2', 'u-guest-3', 'hskd-nh-2', 'NHA_HANG', 'book-nh-2', 4, 'Khong gian dep, mon an vua vi.', 'DA_HIEN_THI', '2026-05-15 21:10:00', '2026-05-15 21:10:00'),
('review-nh-3', 'u-guest-7', 'hskd-nh-3', 'NHA_HANG', 'book-nh-3', 3, 'Do an tam duoc, phuc vu gio cao diem hoi cham.', 'DA_HIEN_THI', '2026-05-16 23:00:00', '2026-05-16 23:00:00')
ON CONFLICT DO NOTHING;

INSERT INTO review_aspect_score (id, review_id, aspect, score) VALUES
('aspect-1', 'review-ks-1', 'CLEANLINESS', 5),
('aspect-2', 'review-ks-1', 'SERVICE', 5),
('aspect-3', 'review-ks-1', 'LOCATION', 5),
('aspect-4', 'review-ks-2', 'SERVICE', 4),
('aspect-5', 'review-ks-2', 'VALUE', 4),
('aspect-6', 'review-ks-3', 'AMENITIES', 2),
('aspect-7', 'review-nh-1', 'FOOD_QUALITY', 5),
('aspect-8', 'review-nh-2', 'AMBIENCE', 4),
('aspect-9', 'review-nh-3', 'SERVICE', 3)
ON CONFLICT DO NOTHING;

INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung) VALUES
('reply-1', 'review-ks-2', 'u-partner-3', 'Cam on ban da gop y, chung toi da bo sung nhan su gio cao diem.'),
('reply-2', 'review-ks-3', 'u-partner-4', 'Xin loi ban ve van de cach am, co so da len ke hoach nang cap.'),
('reply-3', 'review-nh-3', 'u-partner-3', 'Cam on phan hoi, nha hang se toi uu quy trinh phuc vu.')
ON CONFLICT DO NOTHING;

-- 7) COMPLAINT FLOW (multiple statuses)
CREATE TABLE IF NOT EXISTS complaint_khieu_nai (
    id VARCHAR(36) PRIMARY KEY,
    khach_hang_id VARCHAR(36) NOT NULL,
    ho_so_kinh_doanh_id VARCHAR(36) NOT NULL,
    loai_dich_vu VARCHAR(50) NOT NULL,
    booking_id VARCHAR(36),
    reservation_id VARCHAR(36),
    tieu_de VARCHAR(150) NOT NULL,
    noi_dung_tom_tat TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    muc_do VARCHAR(50) NOT NULL,
    trang_thai VARCHAR(50) NOT NULL DEFAULT 'CHO_PHAN_HOI',
    last_customer_message_at TIMESTAMP,
    last_partner_response_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaint_khach_hang FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id) ON DELETE CASCADE,
    CONSTRAINT fk_complaint_ho_so FOREIGN KEY (ho_so_kinh_doanh_id) REFERENCES ho_so_kinh_doanh(id_ho_so) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS complaint_tin_nhan (
    id VARCHAR(36) PRIMARY KEY,
    complaint_id VARCHAR(36) NOT NULL,
    nguoi_gui_id VARCHAR(36) NOT NULL,
    vai_tro_nguoi_gui VARCHAR(50) NOT NULL,
    noi_dung TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaint_message_complaint FOREIGN KEY (complaint_id) REFERENCES complaint_khieu_nai(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS hanh_dong_xu_ly_khieu_nai (
    id VARCHAR(36) PRIMARY KEY,
    khieu_nai_id VARCHAR(36) NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    tieu_de VARCHAR(255) NOT NULL,
    mo_ta TEXT NOT NULL,
    amount NUMERIC(12,2),
    currency VARCHAR(10),
    voucher_code VARCHAR(100),
    discount_percent INTEGER,
    status VARCHAR(50) NOT NULL,
    proposed_by_partner_id VARCHAR(36) NOT NULL,
    customer_response_note TEXT,
    partner_completion_note TEXT,
    proposed_at TIMESTAMP NOT NULL,
    customer_responded_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaint_action_complaint FOREIGN KEY (khieu_nai_id) REFERENCES complaint_khieu_nai(id) ON DELETE CASCADE,
    CONSTRAINT fk_complaint_action_partner FOREIGN KEY (proposed_by_partner_id) REFERENCES doi_tac(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lich_su_hoat_dong_khieu_nai (
    id VARCHAR(36) PRIMARY KEY,
    khieu_nai_id VARCHAR(36) NOT NULL,
    activity_type VARCHAR(50) NOT NULL,
    actor_id VARCHAR(36),
    actor_role VARCHAR(50) NOT NULL,
    summary VARCHAR(500) NOT NULL,
    metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaint_activity_complaint FOREIGN KEY (khieu_nai_id) REFERENCES complaint_khieu_nai(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS dinh_kem_phan_hoi (
    id VARCHAR(36) PRIMARY KEY,
    owner_type VARCHAR(50) NOT NULL,
    owner_id VARCHAR(36) NOT NULL,
    file_url TEXT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    mo_ta TEXT,
    uploaded_by_id VARCHAR(36) NOT NULL,
    uploaded_by_role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO complaint_khieu_nai (
    id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, tieu_de, noi_dung_tom_tat, category, muc_do, trang_thai,
    last_customer_message_at, last_partner_response_at, created_at, updated_at
) VALUES
('comp-ks-1', 'u-guest-2', 'hskd-ks-2', 'KHACH_SAN', 'book-ks-2', 'Check-in cham', 'Toi phai doi hon 40 phut de nhan phong.', 'SERVICE_ATTITUDE', 'BINH_THUONG', 'DANG_XU_LY', '2026-05-20 15:05:00', '2026-05-20 15:40:00', '2026-05-20 15:00:00', '2026-05-20 16:10:00'),
('comp-ks-2', 'u-guest-4', 'hskd-ks-3', 'KHACH_SAN', 'book-ks-3', 'Phong on ao', 'Tieng on hanh lang lon vao dem khuya.', 'FACILITY_PROBLEM', 'BINH_THUONG', 'DA_GIAI_QUYET', '2026-05-25 23:20:00', '2026-05-26 08:10:00', '2026-05-25 23:00:00', '2026-05-26 09:00:00')
ON CONFLICT DO NOTHING;

INSERT INTO complaint_khieu_nai (
    id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, tieu_de, noi_dung_tom_tat, category, muc_do, trang_thai,
    last_customer_message_at, last_partner_response_at, created_at, updated_at
) VALUES
('comp-nh-1', 'u-guest-7', 'hskd-nh-3', 'NHA_HANG', 'book-nh-3', 'Phuc vu mon cham', 'Nhom toi doi mon nuong qua lau.', 'SERVICE_ATTITUDE', 'BINH_THUONG', 'CHO_PHAN_HOI', '2026-05-16 22:15:00', NULL, '2026-05-16 22:10:00', '2026-05-16 22:15:00')
ON CONFLICT DO NOTHING;

INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at) VALUES
('comp-msg-1', 'comp-ks-1', 'u-guest-2', 'KHACH_HANG', 'Toi den 14:30 nhung 15:10 moi co phong.', '2026-05-20 15:05:00'),
('comp-msg-2', 'comp-ks-1', 'u-partner-3', 'DOI_TAC', 'Chung toi xin loi, hom do cong suat cao. Se boi thuong cho quy khach.', '2026-05-20 15:40:00'),
('comp-msg-3', 'comp-ks-2', 'u-guest-4', 'KHACH_HANG', 'Dem khuya lang nghe ro tieng dong hanh lang.', '2026-05-25 23:20:00'),
('comp-msg-4', 'comp-ks-2', 'u-partner-4', 'DOI_TAC', 'Da bo tri phong o tang yen tinh hon cho lan luu tru tiep theo.', '2026-05-26 08:10:00'),
('comp-msg-5', 'comp-nh-1', 'u-guest-7', 'KHACH_HANG', 'Mon set 4 nguoi len rat cham.', '2026-05-16 22:15:00')
ON CONFLICT DO NOTHING;

INSERT INTO hanh_dong_xu_ly_khieu_nai (
    id, khieu_nai_id, action_type, tieu_de, mo_ta, amount, currency, voucher_code, discount_percent, status, proposed_by_partner_id,
    customer_response_note, partner_completion_note, proposed_at, customer_responded_at, completed_at
) VALUES
('action-1', 'comp-ks-1', 'PARTIAL_REFUND', 'Hoan tien 15%', 'Hoan 15% gia tri hoa don phong.', 1350000.00, 'VND', NULL, NULL, 'PROPOSED', 'u-partner-3',
 'Toi dong y phuong an nay.', 'Da hoan tien qua vi dien tu', '2026-05-20 16:00:00', '2026-05-20 16:20:00', '2026-05-20 18:30:00'),
('action-2', 'comp-ks-2', 'VOUCHER', 'Tang voucher 20%', 'Voucher ap dung cho booking tiep theo.', NULL, NULL, 'STAY20', 20, 'COMPLETED', 'u-partner-4',
 'Cam on, toi dong y.', 'Voucher da gui qua email', '2026-05-26 08:20:00', '2026-05-26 08:45:00', '2026-05-26 09:00:00')
ON CONFLICT DO NOTHING;

INSERT INTO lich_su_hoat_dong_khieu_nai (id, khieu_nai_id, activity_type, actor_id, actor_role, summary, metadata, created_at) VALUES
('act-1', 'comp-ks-1', 'COMPLAINT_CREATED', 'u-guest-2', 'KHACH_HANG', 'Khach hang tao khieu nai.', '{"channel":"app"}', '2026-05-20 15:00:00'),
('act-2', 'comp-ks-1', 'PARTNER_MESSAGE_SENT', 'u-partner-3', 'DOI_TAC', 'Doi tac gui loi xin loi.', '{"template":"apology_v1"}', '2026-05-20 15:40:00'),
('act-3', 'comp-ks-1', 'ACTION_PROPOSED', 'u-partner-3', 'DOI_TAC', 'De xuat hoan tien 15%.', '{"amount":1350000}', '2026-05-20 16:00:00'),
('act-4', 'comp-ks-2', 'COMPLAINT_CREATED', 'u-guest-4', 'KHACH_HANG', 'Khach hang phan anh on ao.', '{"room":"room-ks3-2"}', '2026-05-25 23:00:00'),
('act-5', 'comp-ks-2', 'COMPLAINT_RESOLVED', 'u-partner-4', 'DOI_TAC', 'Hoan tat xu ly va gui voucher.', '{"voucher":"STAY20"}', '2026-05-26 09:00:00'),
('act-6', 'comp-nh-1', 'COMPLAINT_CREATED', 'u-guest-7', 'KHACH_HANG', 'Khieu nai thoi gian phuc vu cham.', '{"table":"ban-nh3-2"}', '2026-05-16 22:10:00')
ON CONFLICT DO NOTHING;

INSERT INTO dinh_kem_phan_hoi (
    id, owner_type, owner_id, file_url, file_name, file_type, mime_type, file_size, mo_ta, uploaded_by_id, uploaded_by_role
) VALUES
('att-1', 'REVIEW', 'review-ks-1', 'https://cdn.travi.vn/review/ks1-room.jpg', 'ks1-room.jpg', 'IMAGE', 'image/jpeg', 1480000, 'Anh phong thuc te', 'u-guest-1', 'KHACH_HANG'),
('att-2', 'COMPLAINT', 'comp-ks-1', 'https://cdn.travi.vn/complaint/checkin-delay.pdf', 'checkin-delay.pdf', 'PDF', 'application/pdf', 320000, 'Bien ban su co', 'u-guest-2', 'KHACH_HANG'),
('att-3', 'COMPLAINT_MESSAGE', 'comp-msg-2', 'https://cdn.travi.vn/complaint/apology.png', 'apology.png', 'IMAGE', 'image/png', 210000, 'Tin nhan xin loi', 'u-partner-3', 'DOI_TAC'),
('att-4', 'RESOLUTION_ACTION', 'action-2', 'https://cdn.travi.vn/voucher/stay20.jpg', 'stay20.jpg', 'IMAGE', 'image/jpeg', 188000, 'Anh voucher', 'u-partner-4', 'DOI_TAC')
ON CONFLICT DO NOTHING;

-- 8) BEHAVIOR EVENTS
CREATE TABLE IF NOT EXISTS su_kien_hanh_vi (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    hanh_dong VARCHAR(50) NOT NULL,
    doi_tuan_id BIGINT NOT NULL,
    loai_doi_tuong VARCHAR(50) NOT NULL,
    thoi_luong_xem_ms INTEGER,
    metadata JSONB,
    thoi_gian TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_su_kien_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO su_kien_hanh_vi (user_id, hanh_dong, doi_tuan_id, loai_doi_tuong, thoi_luong_xem_ms, metadata, thoi_gian) VALUES
('u-guest-1', 'VIEW_DETAIL', 1, 'KHACH_SAN', 85000, '{"assetId":"asset-ks-1","source":"search"}', '2026-05-01 10:01:00'),
('u-guest-1', 'ADD_TO_WISHLIST', 1, 'KHACH_SAN', NULL, '{"assetId":"asset-ks-1"}', '2026-05-01 10:05:00'),
('u-guest-2', 'SEARCH', 1, 'NHA_HANG', 120000, '{"city":"Da Nang","keyword":"hai san"}', '2026-05-02 19:01:00'),
('u-guest-2', 'VIEW_DETAIL', 1, 'NHA_HANG', 43000, '{"assetId":"asset-nh-1"}', '2026-05-02 19:03:00'),
('u-guest-3', 'VIEW_DETAIL', 2, 'NHA_HANG', 39000, '{"assetId":"asset-nh-2"}', '2026-05-03 11:31:00'),
('u-guest-4', 'BOOKING_SUCCESS', 3, 'KHACH_SAN', NULL, '{"orderId":"book-ks-3"}', '2026-05-04 18:02:00'),
('u-guest-6', 'VIEW_DETAIL', 4, 'KHACH_SAN', 54000, '{"assetId":"asset-ks-1"}', '2026-05-07 14:00:00'),
('u-guest-7', 'BOOKING_SUCCESS', 5, 'NHA_HANG', NULL, '{"orderId":"book-nh-3"}', '2026-05-12 19:25:00'),
('u-guest-8', 'SEARCH', 6, 'KHACH_SAN', 73000, '{"city":"Phu Quoc","priceMax":2000000}', '2026-05-13 09:10:00')
ON CONFLICT DO NOTHING;

-- 9) EXTRA DIVERSE DATA FOR TARGET CUSTOMER + PARTNER ACCOUNTS
INSERT INTO users (id, email, username, mat_khau, ho_ten, ngay_sinh, gioi_tinh, so_dien_thoai, trang_thai, vai_tro_id) VALUES
('u-guest-9', 'anhbid1000@gmail.com', 'anhbid1000', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Anh Bid Customer', '1994-10-10', 'NAM', '0903000009', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'KHACH_HANG')),
('u-partner-5', 'anhbid2000@gmail.com', 'anhbid2000', '$2a$10$X8I.N6kL8w.Q1uE6jA.b1eE3E5nL2T2L6rM8P3Y/2Z1wM/3D5r7pW', 'Anh Bid Partner', '1989-08-20', 'NAM', '0902000005', 'HOAT_DONG', (SELECT id FROM vai_tro WHERE ten = 'DOI_TAC'))
ON CONFLICT DO NOTHING;

INSERT INTO doi_tac (id, ti_le_chiet_khau) VALUES
('u-partner-5', 8.5)
ON CONFLICT DO NOTHING;

INSERT INTO khach_hang (id, diem_thanh_vien, hang_thanh_vien, tong_chi_tieu) VALUES
('u-guest-9', 760, 'VANG', 31400000)
ON CONFLICT DO NOTHING;

INSERT INTO khach_hang_so_thich (khach_hang_id, so_thich_id) VALUES
('u-guest-9', 'pref-1'),
('u-guest-9', 'pref-3'),
('u-guest-9', 'pref-5')
ON CONFLICT DO NOTHING;

INSERT INTO khach_hang_tu_khoa (khach_hang_id, tu_khoa) VALUES
('u-guest-9', 'khach san da lat'),
('u-guest-9', 'nha hang sang trong'),
('u-guest-9', 'combo gia dinh')
ON CONFLICT DO NOTHING;

INSERT INTO so_thich_nguoi_dung (user_id, danh_muc, diem_so) VALUES
('u-guest-9', 'AM_THUC', 74),
('u-guest-9', 'DU_LICH', 93),
('u-guest-9', 'TRAI_NGHIEM', 61)
ON CONFLICT DO NOTHING;

INSERT INTO ho_so_kinh_doanh (
    id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh,
    toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho, kinh_do, vi_do, trang_thai_hoat_dong
) VALUES
('hskd-ks-4', 'u-partner-5', 'KHACH_SAN', 'Anh Bid Boutique Hotel', '0589000007', 'GPKD-DL-9007', '11.9404,108.4583', '02633880007', 'hotel@anhbid.vn', '25 Phan Dinh Phung, Da Lat', 'Da Lat', 108.4583, 11.9404, 'DANG_HOAT_DONG'),
('hskd-nh-4', 'u-partner-5', 'NHA_HANG', 'Anh Bid Garden Kitchen', '0589000008', 'GPKD-DL-9008', '11.9398,108.4568', '02633880008', 'kitchen@anhbid.vn', '31 Hai Ba Trung, Da Lat', 'Da Lat', 108.4568, 11.9398, 'DANG_HOAT_DONG')
ON CONFLICT DO NOTHING;

INSERT INTO tai_san (
    id_tai_san, ho_so_kinh_doanh_id, mo_ta, trang_thai, gia_co_ban, is_dynamic_pricing, rating_average, review_count,
    dia_chi, thanh_pho, kinh_do, vi_do
) VALUES
('asset-ks-4', 'hskd-ks-4', 'Boutique hotel Da Lat, nhieu phong doi va gia dinh', 'SAN_SANG', 1680000, TRUE, 4.8, 67, '25 Phan Dinh Phung, Da Lat', 'Da Lat', 108.4583, 11.9404),
('asset-nh-4', 'hskd-nh-4', 'Nha hang vuon canh ho, co set menu va ban ngoai troi', 'SAN_SANG', 260000, TRUE, 4.6, 53, '31 Hai Ba Trung, Da Lat', 'Da Lat', 108.4568, 11.9398)
ON CONFLICT DO NOTHING;

INSERT INTO khach_san (id_tai_san, ten, hang_sao, loai_khach_san, gio_nhan_phong, gio_tra_phong, gio_nhan_phong_mac_dinh, gio_tra_phong_mac_dinh, so_tang, tong_so_phong) VALUES
('asset-ks-4', 'Anh Bid Boutique Hotel', 4, 'BOUTIQUE', '14:00', '12:00', '14:00', '12:00', 7, 36)
ON CONFLICT DO NOTHING;

INSERT INTO nha_hang (id_tai_san, ten, loai_am_thuc, gio_mo_cua, gio_dong_cua, suc_chua, co_dat_ban_truoc, co_dat_mon_truoc) VALUES
('asset-nh-4', 'Anh Bid Garden Kitchen', 'AU_A', '09:00', '22:00', 88, TRUE, TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO tien_ich_khach_san (id, ten_tien_ich, loai_tien_ich, mo_ta) VALUES
('amen-ks-7', 'Bep pantry', 'AM_THUC', 'Khu phuc vu do an nhe'),
('amen-ks-8', 'Cho do xe rong', 'DI_CHUYEN', 'Bai do xe may va o to')
ON CONFLICT DO NOTHING;

INSERT INTO khach_san_tien_ich (khach_san_id, tien_ich_id) VALUES
('asset-ks-4', 'amen-ks-7'),
('asset-ks-4', 'amen-ks-8')
ON CONFLICT DO NOTHING;

INSERT INTO tien_ich_nha_hang (id, nha_hang_id, ten_tien_ich, loai_tien_ich, mo_ta, co_thu_phi, phi_su_dung) VALUES
('amen-nh-5', 'asset-nh-4', 'Ban san vuon', 'KHONG_GIAN', 'Ban ngoai troi co den lung linh', FALSE, 0),
('amen-nh-6', 'asset-nh-4', 'Set menu tasting', 'AM_THUC', 'Set thu mon dac trung cua bep', TRUE, 150000)
ON CONFLICT DO NOTHING;

INSERT INTO phong (id, khach_san_id, so_phong, ten_phong, loai_phong, mo_ta, suc_chua_toi_da, so_giuong, dien_tich, gia_co_ban, so_luong_phong, trang_thai, phan_tram_giam_gia) VALUES
('room-ks4-1', 'asset-ks-4', '401', 'Garden View Double', 'DELUXE', 'Phong view vuon', 2, 1, 30, 1680000, 6, 'SAN_SANG', 12),
('room-ks4-2', 'asset-ks-4', '602', 'Family Loft', 'FAMILY', 'Phong 2 tang cho gia dinh', 4, 2, 52, 2380000, 4, 'SAN_SANG', 5)
ON CONFLICT DO NOTHING;

INSERT INTO phong_tien_ich (phong_id, tien_ich) VALUES
('room-ks4-1', 'WIFI'),
('room-ks4-1', 'DIEU_HOA'),
('room-ks4-2', 'VIEW_DEP'),
('room-ks4-2', 'TU_LANH')
ON CONFLICT DO NOTHING;

INSERT INTO ban (id, nha_hang_id, ten_ban, vi_tri_sanh, mo_ta, trang_thai, so_cho_ngoi) VALUES
('ban-nh4-1', 'asset-nh-4', 'Ban Vuon 01', 'San vuon', 'Ban co view ho', 'SAN_SANG', 4),
('ban-nh4-2', 'asset-nh-4', 'Ban VIP 03', 'Tang 2', 'Phong rieng cho nhom nho', 'SAN_SANG', 6)
ON CONFLICT DO NOTHING;

INSERT INTO anh_khach_san (id, khach_san_id, duong_dan_url, mo_ta_anh, la_anh_dai_dien, ngay_tai_len) VALUES
('img-ks-4', 'asset-ks-4', 'https://cdn.travi.vn/ks4/front.jpg', 'Mat tien boutique hotel', TRUE, '2026-05-20')
ON CONFLICT DO NOTHING;

INSERT INTO anh_nha_hang (id, nha_hang_id, duong_dan_url, mo_ta_anh, la_anh_dai_dien, ngay_tai_len) VALUES
('img-nh-4', 'asset-nh-4', 'https://cdn.travi.vn/nh4/garden.jpg', 'Khong gian san vuon', TRUE, '2026-05-20')
ON CONFLICT DO NOTHING;

INSERT INTO anh_phong (id, phong_id, duong_dan_url, mo_ta_anh, la_anh_dai_dien, ngay_tai_len) VALUES
('img-room-4', 'room-ks4-2', 'https://cdn.travi.vn/room/ks4-2.jpg', 'Family loft', TRUE, '2026-05-20')
ON CONFLICT DO NOTHING;

INSERT INTO thuc_don (id, nha_hang_id, ten_thuc_don, phan_loai, trang_thai) VALUES
('menu-nh4-main', 'asset-nh-4', 'Thuc don vuon Da Lat', 'A_LA_CARTE', 'DANG_HIEN_THI')
ON CONFLICT DO NOTHING;

INSERT INTO mon_an (id, thuc_don_id, ten_mon, mo_ta, gia_ban, danh_muc_mon, trang_thai, duong_dan_url, deleted) VALUES
('dish-7', 'menu-nh4-main', 'Salad rau rung', 'Rau tuoi Da Lat', 180000, 'KHAI_VI', 'DANG_BAN', 'https://cdn.travi.vn/dish/salad-dalat.jpg', FALSE),
('dish-8', 'menu-nh4-main', 'Bo kho vang', 'Mon chinh am cung', 320000, 'MON_CHINH', 'DANG_BAN', 'https://cdn.travi.vn/dish/bo-kho.jpg', FALSE),
('dish-9', 'menu-nh4-main', 'Banh flan caramen', 'Trang mieng nha lam', 90000, 'TRANG_MIENG', 'DANG_BAN', 'https://cdn.travi.vn/dish/flan.jpg', FALSE)
ON CONFLICT DO NOTHING;

INSERT INTO mon_an_the_ngu_canh (mon_an_id, the_ngu_canh) VALUES
('dish-7', 'AN_KIENG'),
('dish-8', 'MON_NOI_BAT'),
('dish-9', 'TRANG_MIENG')
ON CONFLICT DO NOTHING;

INSERT INTO combo (id, thuc_don_id, ten_combo, mo_ta, gia_combo, ngay_bat_dau, ngay_ket_thuc, trang_thai) VALUES
('combo-3', 'menu-nh4-main', 'Combo Thuong Hieu Da Lat', '3 mon dac trung + nuoc uong', 470000, '2026-05-01', '2026-12-31', 1)
ON CONFLICT DO NOTHING;

INSERT INTO combo_mon_an (combo_id, mon_an_id) VALUES
('combo-3', 'dish-7'),
('combo-3', 'dish-8'),
('combo-3', 'dish-9')
ON CONFLICT DO NOTHING;

INSERT INTO combo_item (id, combo_id, mon_an_id, so_luong) VALUES
('combo-item-5', 'combo-3', 'dish-7', 1),
('combo-item-6', 'combo-3', 'dish-8', 1),
('combo-item-7', 'combo-3', 'dish-9', 1)
ON CONFLICT DO NOTHING;

INSERT INTO chinh_sach (
    id, loai_chinh_sach, noi_dung, ngay_ap_dung, gio_nhan_phong, gio_tra_phong, gio_mo_cua, gio_dong_cua,
    chinh_sach_huy, chinh_sach_hoan_tien, quy_dinh_tre_em, quy_dinh_vat_nuoi, ghi_chu_khac, ho_so_kinh_doanh_id
) VALUES
('policy-ks-4', 'KHACH_SAN', 'Chinh sach boutique hotel Da Lat', '2026-01-01', '14:00', '12:00', NULL, NULL, 'Huy truoc 48h', 'Hoan 80%', 'Tre duoi 6 tuoi mien phi', 'Vat nuoi nho duoc phep', 'Co goi trang tri sinh nhat', 'hskd-ks-4'),
('policy-nh-4', 'NHA_HANG', 'Chinh sach nha hang Da Lat', '2026-01-01', NULL, NULL, '09:00', '22:00', 'Huy truoc 2h', 'Khong ap dung', NULL, NULL, 'Co set tasting menu cho 2 nguoi', 'hskd-nh-4')
ON CONFLICT DO NOTHING;

INSERT INTO don_dat_cho (
    id, ma_don, khach_hang_id, ho_so_kinh_doanh_id, ngay_tao, tong_tien_goc, tien_khuyen_mai, tong_tien_thanh_toan,
    ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat, ghi_chu, trang_thai, hold_expired_at, payment_expired_at
) VALUES
('book-ks-5', 'OD-KS-0005', 'u-guest-9', 'hskd-ks-4', '2026-05-18 08:20:00', 4760000, 360000, 4400000, 'Anh Bid', '0903000009', 'anhbid1000@gmail.com', 'Can phong family, co view vuon', 'DA_HOAN_THANH', '2026-05-18 08:35:00', '2026-05-18 08:50:00'),
('book-ks-6', 'OD-KS-0006', 'u-guest-9', 'hskd-ks-2', '2026-05-22 13:40:00', 6600000, 200000, 6400000, 'Anh Bid', '0903000009', 'anhbid1000@gmail.com', 'Uu tien phong gan ho boi', 'DA_HOAN_THANH', '2026-05-22 13:55:00', '2026-05-22 14:10:00'),
('book-nh-4', 'OD-NH-0004', 'u-guest-9', 'hskd-nh-4', '2026-05-23 18:15:00', 650000, 50000, 600000, 'Anh Bid', '0903000009', 'anhbid1000@gmail.com', 'Ban san vuon va set tasting', 'DA_HOAN_THANH', '2026-05-23 18:30:00', '2026-05-23 18:45:00'),
('book-nh-5', 'OD-NH-0005', 'u-guest-9', 'hskd-nh-2', '2026-05-24 19:10:00', 780000, 80000, 700000, 'Anh Bid', '0903000009', 'anhbid1000@gmail.com', 'Sinh nhat nho 4 nguoi', 'DA_HOAN_THANH', '2026-05-24 19:25:00', '2026-05-24 19:40:00'),
('book-ks-7', 'OD-KS-0007', 'u-guest-9', 'hskd-ks-1', '2026-06-09 09:20:00', 3100000, 100000, 3000000, 'Anh Bid', '0903000009', 'anhbid1000@gmail.com', 'De cho thanh toan sau 30 phut', 'CHO_THANH_TOAN', '2026-06-09 09:50:00', '2026-06-09 10:05:00'),
('book-ks-8', 'OD-KS-0008', 'u-guest-9', 'hskd-ks-3', '2026-06-11 11:00:00', 1420000, 0, 1420000, 'Anh Bid', '0903000009', 'anhbid1000@gmail.com', 'Huy vi thay doi lich cong tac', 'DA_HUY', '2026-06-11 11:15:00', '2026-06-11 11:30:00'),
('book-nh-6', 'OD-NH-0006', 'u-guest-9', 'hskd-nh-1', '2026-06-12 18:20:00', 910000, 0, 910000, 'Anh Bid', '0903000009', 'anhbid1000@gmail.com', 'Dat ban tiep khach doi tac', 'DANG_PHUC_VU', '2026-06-12 18:35:00', '2026-06-12 18:50:00'),
('book-nh-7', 'OD-NH-0007', 'u-guest-9', 'hskd-nh-4', '2026-06-13 19:10:00', 590000, 0, 590000, 'Anh Bid', '0903000009', 'anhbid1000@gmail.com', 'Yeu cau hoan tien coc do doi mua', 'YEU_CAU_HOAN_TIEN', '2026-06-13 19:25:00', '2026-06-13 19:40:00')
ON CONFLICT DO NOTHING;

INSERT INTO don_khach_san (id, ngay_check_in, ngay_check_out, so_dem, so_khach, gio_nhan_phong_du_kien) VALUES
('book-ks-5', '2026-05-30', '2026-06-02', 3, 4, '14:30'),
('book-ks-6', '2026-06-05', '2026-06-07', 2, 2, '15:00'),
('book-ks-7', '2026-06-15', '2026-06-17', 2, 2, '14:00'),
('book-ks-8', '2026-06-18', '2026-06-19', 1, 2, '14:30')
ON CONFLICT DO NOTHING;

INSERT INTO don_khach_san_chi_tiet (id, don_khach_san_id, phong_id, ten_phong_tai_thoi_diem_dat, so_luong, don_gia_tai_thoi_diem_dat, so_dem, thanh_tien) VALUES
('book-ks-ct-5', 'book-ks-5', 'room-ks4-2', 'Family Loft', 1, 2380000, 3, 7140000),
('book-ks-ct-6', 'book-ks-6', 'room-ks2-1', 'Garden Villa', 1, 3300000, 2, 6600000),
('book-ks-ct-7', 'book-ks-7', 'room-ks1-1', 'Deluxe City View', 1, 1550000, 2, 3100000),
('book-ks-ct-8', 'book-ks-8', 'room-ks3-1', 'Superior Double', 1, 1420000, 1, 1420000)
ON CONFLICT DO NOTHING;

INSERT INTO dat_phong (id, don_dat_cho_id, phong_id, ngay_check_in, ngay_check_out, ghi_chu_khach_hang_phong_don_dat) VALUES
('dp-4', 'book-ks-5', 'room-ks4-2', '2026-05-30', '2026-06-02', 'Can noi o cho 4 nguoi'),
('dp-5', 'book-ks-6', 'room-ks2-1', '2026-06-05', '2026-06-07', 'Uu tien view ho boi'),
('dp-6', 'book-ks-7', 'room-ks1-1', '2026-06-15', '2026-06-17', 'Can xac nhan thanh toan sau'),
('dp-7', 'book-ks-8', 'room-ks3-1', '2026-06-18', '2026-06-19', 'Yeu cau huy ngay trong ngay')
ON CONFLICT DO NOTHING;

INSERT INTO don_nha_hang (id, ngay_gio_bat_dau, ngay_gio_ket_thuc, so_nguoi, tien_coc, co_dat_mon_truoc) VALUES
('book-nh-4', '2026-05-23 19:00:00', '2026-05-23 21:00:00', 4, 150000, TRUE),
('book-nh-5', '2026-05-24 19:30:00', '2026-05-24 21:00:00', 4, 150000, TRUE),
('book-nh-6', '2026-06-12 19:00:00', '2026-06-12 21:00:00', 5, 200000, TRUE),
('book-nh-7', '2026-06-13 19:30:00', '2026-06-13 20:30:00', 4, 150000, TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO don_nha_hang_ban (id, don_nha_hang_id, ban_id) VALUES
('nh-ban-4', 'book-nh-4', 'ban-nh4-1'),
('nh-ban-5', 'book-nh-5', 'ban-nh2-2'),
('nh-ban-6', 'book-nh-6', 'ban-nh1-2'),
('nh-ban-7', 'book-nh-7', 'ban-nh4-2')
ON CONFLICT DO NOTHING;

INSERT INTO don_dat_mon (id, don_dat_cho_id, nha_hang_id, ban_id, thoi_gian_dat, ghi_chu) VALUES
('ddm-4', 'book-nh-4', 'asset-nh-4', 'ban-nh4-1', '2026-05-23 19:05:00', 'Set tasting va trang tri sinh nhat'),
('ddm-5', 'book-nh-5', 'asset-nh-2', 'ban-nh2-2', '2026-05-24 19:35:00', 'Them banh kem sinh nhat'),
('ddm-6', 'book-nh-6', 'asset-nh-1', 'ban-nh1-2', '2026-06-12 19:05:00', 'Them 1 suat an chay cho khach'),
('ddm-7', 'book-nh-7', 'asset-nh-4', 'ban-nh4-2', '2026-06-13 19:35:00', 'Du kien doi lich neu mua lon')
ON CONFLICT DO NOTHING;

INSERT INTO chi_tiet_don_dat_mon (id, don_dat_mon_id, mon_an_id, so_luong, gia_tien) VALUES
('ddm-ct-7', 'ddm-4', 'dish-7', 1, 180000),
('ddm-ct-8', 'ddm-4', 'dish-8', 1, 320000),
('ddm-ct-9', 'ddm-4', 'dish-9', 1, 90000),
('ddm-ct-10', 'ddm-5', 'dish-3', 1, 390000),
('ddm-ct-11', 'ddm-5', 'dish-4', 1, 190000),
('ddm-ct-12', 'ddm-6', 'dish-1', 1, 690000),
('ddm-ct-13', 'ddm-6', 'dish-2', 1, 220000),
('ddm-ct-14', 'ddm-7', 'dish-7', 1, 180000),
('ddm-ct-15', 'ddm-7', 'dish-8', 1, 320000)
ON CONFLICT DO NOTHING;

INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, so_sao, noi_dung, trang_thai, created_at, updated_at) VALUES
('review-ks-4', 'u-guest-9', 'hskd-ks-4', 'KHACH_SAN', 'book-ks-5', 5, 'Da Lat dep, phong rong va co them trang tri theo yeu cau.', 'DA_HIEN_THI', '2026-06-02 11:30:00', '2026-06-02 11:30:00'),
('review-ks-5', 'u-guest-9', 'hskd-ks-2', 'KHACH_SAN', 'book-ks-6', 4, 'Resort dep, ho boi tot, chi co thu tuc check-in can nhanh hon.', 'BI_AN', '2026-06-07 10:15:00', '2026-06-07 10:15:00')
ON CONFLICT DO NOTHING;

INSERT INTO review_danh_gia (id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, so_sao, noi_dung, trang_thai, created_at, updated_at) VALUES
('review-nh-4', 'u-guest-9', 'hskd-nh-4', 'NHA_HANG', 'book-nh-4', 5, 'Set tasting rat ngon, khong gian san vuon dang tien tra tien.', 'DA_HIEN_THI', '2026-05-23 22:05:00', '2026-05-23 22:05:00'),
('review-nh-5', 'u-guest-9', 'hskd-nh-2', 'NHA_HANG', 'book-nh-5', 2, 'Mon an on nhung toi co dung tu khong phu hop trong binh luan nay.', 'BI_AN', '2026-05-24 22:00:00', '2026-05-24 22:00:00')
ON CONFLICT DO NOTHING;

INSERT INTO review_aspect_score (id, review_id, aspect, score) VALUES
('aspect-10', 'review-ks-4', 'CLEANLINESS', 5),
('aspect-11', 'review-ks-4', 'SERVICE', 5),
('aspect-12', 'review-ks-4', 'LOCATION', 5),
('aspect-13', 'review-ks-5', 'SERVICE', 4),
('aspect-14', 'review-ks-5', 'VALUE', 4),
('aspect-15', 'review-nh-4', 'FOOD_QUALITY', 5),
('aspect-16', 'review-nh-4', 'AMBIENCE', 5),
('aspect-17', 'review-nh-5', 'SERVICE', 4)
ON CONFLICT DO NOTHING;

INSERT INTO review_phan_hoi_partner (id, review_id, partner_id, noi_dung) VALUES
('reply-4', 'review-ks-4', 'u-partner-5', 'Cam on anh da trai nghiem, chung toi se giu chat luong phong va trang tri theo yeu cau.'),
('reply-5', 'review-nh-4', 'u-partner-5', 'Cam on anh bid, ben bep rat vui khi quy khach hai long voi set tasting da lat.'),
('reply-6', 'review-nh-5', 'u-partner-5', 'Cam on phan hoi, chung toi se nang cap phan phuc vu cho nhom dong nguoi.')
ON CONFLICT DO NOTHING;

INSERT INTO complaint_khieu_nai (
    id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, booking_id, tieu_de, noi_dung_tom_tat, category, muc_do, trang_thai,
    last_customer_message_at, last_partner_response_at, created_at, updated_at
) VALUES
('comp-ks-3', 'u-guest-9', 'hskd-ks-4', 'KHACH_SAN', 'book-ks-5', 'Phong chua san sang', 'Toi den som nhung phong chua xong khau ve sinh.', 'SERVICE_ATTITUDE', 'BINH_THUONG', 'DANG_THUC_HIEN_PHUONG_AN', '2026-05-30 14:10:00', '2026-05-30 14:45:00', '2026-05-30 14:00:00', '2026-05-30 15:10:00'),
('comp-ks-4', 'u-guest-9', 'hskd-ks-2', 'KHACH_SAN', 'book-ks-6', 'Sai hoa don phu thu', 'Hoa don co phu thu mini bar ma toi khong su dung.', 'BILLING', 'BINH_THUONG', 'DA_DONG', '2026-06-07 09:10:00', '2026-06-07 10:00:00', '2026-06-07 09:00:00', '2026-06-07 12:30:00')
ON CONFLICT DO NOTHING;

INSERT INTO complaint_khieu_nai (
    id, khach_hang_id, ho_so_kinh_doanh_id, loai_dich_vu, reservation_id, tieu_de, noi_dung_tom_tat, category, muc_do, trang_thai,
    last_customer_message_at, last_partner_response_at, created_at, updated_at
) VALUES
('comp-nh-2', 'u-guest-9', 'hskd-nh-4', 'NHA_HANG', 'book-nh-4', 'Phuc vu cham', 'Mon khai vi len cham hon du kien.', 'SERVICE_ATTITUDE', 'NGHIEM_TRONG', 'CHO_XAC_NHAN_KHACH', '2026-05-23 19:30:00', '2026-05-23 20:05:00', '2026-05-23 19:25:00', '2026-05-23 20:30:00')
ON CONFLICT DO NOTHING;

INSERT INTO complaint_tin_nhan (id, complaint_id, nguoi_gui_id, vai_tro_nguoi_gui, noi_dung, created_at) VALUES
('comp-msg-6', 'comp-ks-3', 'u-guest-9', 'KHACH_HANG', 'Phong cua toi chua duoc ban giao dung gio.', '2026-05-30 14:10:00'),
('comp-msg-7', 'comp-ks-3', 'u-partner-5', 'DOI_TAC', 'Ben toi da ho tro doi phong va hoan phi don phong.', '2026-05-30 14:45:00'),
('comp-msg-8', 'comp-nh-2', 'u-guest-9', 'KHACH_HANG', 'Toi phai doi lau moi co mon khai vi.', '2026-05-23 19:30:00'),
('comp-msg-9', 'comp-nh-2', 'u-partner-5', 'DOI_TAC', 'Chung toi xin loi va de xuat giam gia 25% cho bua toi nay.', '2026-05-23 20:05:00'),
('comp-msg-10', 'comp-ks-4', 'u-guest-9', 'KHACH_HANG', 'Toi can doi soat lai phan phu thu mini bar.', '2026-06-07 09:10:00'),
('comp-msg-11', 'comp-ks-4', 'u-partner-5', 'DOI_TAC', 'Da doi soat va xac nhan hoan tien phu thu sai.', '2026-06-07 10:00:00')
ON CONFLICT DO NOTHING;

INSERT INTO hanh_dong_xu_ly_khieu_nai (
    id, khieu_nai_id, action_type, tieu_de, mo_ta, amount, currency, voucher_code, discount_percent, status, proposed_by_partner_id,
    customer_response_note, partner_completion_note, proposed_at, customer_responded_at, completed_at
) VALUES
('action-3', 'comp-ks-3', 'VOUCHER', 'Tang voucher nghi duong', 'Tang voucher 20% cho lan dat tiep theo.', NULL, NULL, 'DALAT20', 20, 'COMPLETED', 'u-partner-5',
 'Toi dong y phuong an voucher nay.', 'Voucher da duoc gui qua email.', '2026-05-30 15:00:00', '2026-05-30 15:20:00', '2026-05-30 16:00:00'),
('action-4', 'comp-nh-2', 'DISCOUNT_CODE', 'Giam 25% hoa don', 'Giam truc tiep 25% cho don nha hang.', NULL, NULL, 'ABID25', 25, 'CUSTOMER_REJECTED', 'u-partner-5',
 'Toi chua dong y muc boi thuong hien tai.', NULL, '2026-05-23 20:05:00', '2026-05-23 20:15:00', NULL),
('action-5', 'comp-nh-2', 'PARTIAL_REFUND', 'Hoan tien mot phan', 'Hoan 200000VND cho trai nghiem phuc vu cham.', 200000.00, 'VND', NULL, NULL, 'IN_PROGRESS', 'u-partner-5',
 'Toi dong y voi phuong an hoan tien.', 'Dang xu ly lenh hoan qua vi dien tu.', '2026-05-23 20:20:00', '2026-05-23 20:30:00', NULL),
('action-6', 'comp-ks-4', 'PARTIAL_REFUND', 'Hoan phu thu mini bar', 'Hoan lai khoan phu thu mini bar tinh sai.', 180000.00, 'VND', NULL, NULL, 'COMPLETED', 'u-partner-5',
 'Toi dong y va da nhan tien hoan.', 'Da hoan tien vao the thanh toan cua khach.', '2026-06-07 10:05:00', '2026-06-07 10:25:00', '2026-06-07 11:00:00')
ON CONFLICT DO NOTHING;

INSERT INTO lich_su_hoat_dong_khieu_nai (id, khieu_nai_id, activity_type, actor_id, actor_role, summary, metadata, created_at) VALUES
('act-7', 'comp-ks-3', 'COMPLAINT_CREATED', 'u-guest-9', 'KHACH_HANG', 'Khach hang tao khieu nai ve phong.', '{"bookingId":"book-ks-5"}', '2026-05-30 14:00:00'),
('act-8', 'comp-ks-3', 'ACTION_PROPOSED', 'u-partner-5', 'DOI_TAC', 'De xuat voucher boi thuong.', '{"voucher":"DALAT20"}', '2026-05-30 15:00:00'),
('act-9', 'comp-nh-2', 'COMPLAINT_CREATED', 'u-guest-9', 'KHACH_HANG', 'Khach hang phan anh phuc vu cham.', '{"reservationId":"book-nh-4"}', '2026-05-23 19:25:00'),
('act-10', 'comp-nh-2', 'PARTNER_MESSAGE_SENT', 'u-partner-5', 'DOI_TAC', 'Doi tac phan hoi va de xuat boi thuong.', '{"code":"ABID25"}', '2026-05-23 20:05:00'),
('act-11', 'comp-nh-2', 'ACTION_REJECTED', 'u-guest-9', 'KHACH_HANG', 'Khach tu choi phuong an giam gia ban dau.', '{"actionId":"action-4"}', '2026-05-23 20:15:00'),
('act-12', 'comp-ks-4', 'ACTION_COMPLETED', 'u-partner-5', 'DOI_TAC', 'Hoan tien phu thu da hoan tat.', '{"actionId":"action-6"}', '2026-06-07 11:00:00'),
('act-13', 'comp-ks-4', 'COMPLAINT_CLOSED', 'u-guest-9', 'KHACH_HANG', 'Khach dong y va dong ticket.', '{"closedBy":"customer"}', '2026-06-07 12:30:00')
ON CONFLICT DO NOTHING;

INSERT INTO dinh_kem_phan_hoi (
    id, owner_type, owner_id, file_url, file_name, file_type, mime_type, file_size, mo_ta, uploaded_by_id, uploaded_by_role
) VALUES
('att-5', 'REVIEW', 'review-ks-4', 'https://cdn.travi.vn/review/ks4-room.jpg', 'ks4-room.jpg', 'IMAGE', 'image/jpeg', 1660000, 'Anh phong family loft', 'u-guest-9', 'KHACH_HANG'),
('att-6', 'COMPLAINT', 'comp-ks-3', 'https://cdn.travi.vn/complaint/room-not-ready.jpg', 'room-not-ready.jpg', 'IMAGE', 'image/jpeg', 240000, 'Anh chp luc nhan phong', 'u-guest-9', 'KHACH_HANG'),
('att-7', 'COMPLAINT_MESSAGE', 'comp-msg-7', 'https://cdn.travi.vn/complaint/response-voucher.png', 'response-voucher.png', 'IMAGE', 'image/png', 190000, 'Anh voucher boi thuong', 'u-partner-5', 'DOI_TAC'),
('att-8', 'RESOLUTION_ACTION', 'action-3', 'https://cdn.travi.vn/voucher/dalat20.jpg', 'dalat20.jpg', 'IMAGE', 'image/jpeg', 175000, 'Anh voucher khuyen mai', 'u-partner-5', 'DOI_TAC')
ON CONFLICT DO NOTHING;

INSERT INTO su_kien_hanh_vi (user_id, hanh_dong, doi_tuan_id, loai_doi_tuong, thoi_luong_xem_ms, metadata, thoi_gian) VALUES
('u-guest-9', 'SEARCH', 7, 'KHACH_SAN', 92000, '{"city":"Da Lat","keyword":"boutique hotel"}', '2026-05-18 07:55:00'),
('u-guest-9', 'VIEW_DETAIL', 7, 'KHACH_SAN', 62000, '{"assetId":"asset-ks-4"}', '2026-05-18 07:57:00'),
('u-guest-9', 'BOOKING_SUCCESS', 7, 'KHACH_SAN', NULL, '{"orderId":"book-ks-5"}', '2026-05-18 08:20:00'),
('u-guest-9', 'VIEW_DETAIL', 8, 'NHA_HANG', 48000, '{"assetId":"asset-nh-4"}', '2026-05-23 18:05:00'),
('u-guest-9', 'BOOKING_SUCCESS', 8, 'NHA_HANG', NULL, '{"orderId":"book-nh-4"}', '2026-05-23 18:15:00'),
('u-guest-9', 'BOOKING_START', 9, 'KHACH_SAN', NULL, '{"orderId":"book-ks-7"}', '2026-06-09 09:20:00'),
('u-guest-9', 'LEAVE_PAGE', 9, 'KHACH_SAN', 17000, '{"assetId":"asset-ks-1","reason":"payment_timeout"}', '2026-06-09 09:48:00'),
('u-partner-5', 'CLICK_CARD', 8, 'KHACH_SAN', NULL, '{"businessCount":2}', '2026-05-23 08:00:00'),
('u-partner-5', 'CLICK_CARD', 8, 'NHA_HANG', NULL, '{"reviewId":"review-nh-4"}', '2026-05-23 22:10:00'),
('u-partner-5', 'VIEW_DETAIL', 9, 'KHACH_SAN', 25000, '{"complaintId":"comp-ks-4"}', '2026-06-07 10:02:00');
