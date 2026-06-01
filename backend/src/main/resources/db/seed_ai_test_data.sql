-- AI/QA seed data for customer-side testing

UPDATE ho_so_kinh_doanh
SET
  ten_co_so = CASE id_ho_so
    WHEN '6d9cd3b9-f367-4827-bc0d-77f90d10ff77' THEN 'Lavender Hotel Da Lat'
    WHEN '5fa2606d-9653-4451-b6ad-921247ffb6bd' THEN 'Moc Tra Boutique Hotel'
    ELSE ten_co_so
  END,
  dia_chi = CASE id_ho_so
    WHEN '6d9cd3b9-f367-4827-bc0d-77f90d10ff77' THEN '12 Tran Phu, Phuong 3'
    WHEN '5fa2606d-9653-4451-b6ad-921247ffb6bd' THEN '88 Hai Ba Trung, Phuong 6'
    ELSE dia_chi
  END,
  thanh_pho = CASE id_ho_so
    WHEN '6d9cd3b9-f367-4827-bc0d-77f90d10ff77' THEN 'Da Lat'
    WHEN '5fa2606d-9653-4451-b6ad-921247ffb6bd' THEN 'Da Lat'
    ELSE thanh_pho
  END,
  kinh_do = CASE id_ho_so
    WHEN '6d9cd3b9-f367-4827-bc0d-77f90d10ff77' THEN 108.4378
    WHEN '5fa2606d-9653-4451-b6ad-921247ffb6bd' THEN 108.4440
    ELSE kinh_do
  END,
  vi_do = CASE id_ho_so
    WHEN '6d9cd3b9-f367-4827-bc0d-77f90d10ff77' THEN 11.9404
    WHEN '5fa2606d-9653-4451-b6ad-921247ffb6bd' THEN 11.9482
    ELSE vi_do
  END,
  trang_thai_kiem_duyet = 'DA_DUYET',
  trang_thai_hoat_dong = 'DANG_HOAT_DONG',
  deleted = false
WHERE id_ho_so IN ('6d9cd3b9-f367-4827-bc0d-77f90d10ff77','5fa2606d-9653-4451-b6ad-921247ffb6bd');

UPDATE khach_san
SET
  ten = CASE id_tai_san
    WHEN '3968d549-0e95-4be5-bffa-d3c01f88563e' THEN 'Lavender Hotel Da Lat'
    WHEN '7f105953-a5ad-4e9b-81eb-883673c79692' THEN 'Moc Tra Boutique Hotel'
    ELSE ten
  END,
  hang_sao = CASE id_tai_san
    WHEN '3968d549-0e95-4be5-bffa-d3c01f88563e' THEN 4
    WHEN '7f105953-a5ad-4e9b-81eb-883673c79692' THEN 3
    ELSE hang_sao
  END,
  tong_so_phong = COALESCE(tong_so_phong, 20),
  loai_khach_san = COALESCE(loai_khach_san, 'Boutique'),
  gio_nhan_phong = COALESCE(gio_nhan_phong, TIME '14:00'),
  gio_tra_phong = COALESCE(gio_tra_phong, TIME '12:00');

UPDATE tai_san
SET
  gia_co_ban = CASE id_tai_san
    WHEN '3968d549-0e95-4be5-bffa-d3c01f88563e' THEN 1200000
    WHEN '7f105953-a5ad-4e9b-81eb-883673c79692' THEN 850000
    ELSE gia_co_ban
  END,
  mo_ta = CASE id_tai_san
    WHEN '3968d549-0e95-4be5-bffa-d3c01f88563e' THEN 'Khach san trung tam, gan ho Xuan Huong.'
    WHEN '7f105953-a5ad-4e9b-81eb-883673c79692' THEN 'Khach san am cung, phong cach boutique.'
    ELSE mo_ta
  END,
  trang_thai = 'SAN_SANG',
  is_dynamic_pricing = true
WHERE id_tai_san IN ('3968d549-0e95-4be5-bffa-d3c01f88563e','7f105953-a5ad-4e9b-81eb-883673c79692');

INSERT INTO ho_so_kinh_doanh (
  id_ho_so, doi_tac_id, loai_dich_vu, ten_co_so, ma_so_thue, giay_phep_kinh_doanh,
  toa_do_gps, sdt_lien_he, email_lien_he, dia_chi, thanh_pho,
  kinh_do, vi_do, trang_thai_kiem_duyet, trang_thai_hoat_dong, deleted, thoi_gian_dang_ky, thoi_gian_cap_nhat
) VALUES (
  'hsnh-demo-001','2de79fb2-9d2f-4881-9280-276863510cd8','NHA_HANG','Bep Que Da Lat','0312345678','GPKD-NH-001',
  '11.9467,108.4410','0909123456','bepque.dalat@example.com','22 Nguyen Chi Thanh, Phuong 1','Da Lat',
  108.4410,11.9467,'DA_DUYET','DANG_HOAT_DONG',false,NOW(),NOW()
)
ON CONFLICT (id_ho_so) DO NOTHING;

INSERT INTO tai_san (id_tai_san, gia_co_ban, is_dynamic_pricing, mo_ta, trang_thai, ho_so_kinh_doanh_id)
VALUES ('nh-demo-001',250000,true,'Nha hang mon Viet, khong gian am cung.','SAN_SANG','hsnh-demo-001')
ON CONFLICT (id_tai_san) DO NOTHING;

INSERT INTO nha_hang (id_tai_san, ten, loai_am_thuc, suc_chua, gio_mo_cua, gio_dong_cua, co_dat_ban_truoc, co_dat_mon_truoc)
VALUES ('nh-demo-001','Bep Que Da Lat','viet nam',80,TIME '09:00',TIME '22:00',true,true)
ON CONFLICT (id_tai_san) DO NOTHING;

INSERT INTO ban (id, ten_ban, so_cho_ngoi, vi_tri_sanh, mo_ta, trang_thai, nha_hang_id, deleted, created_at, updated_at) VALUES
('ban-demo-001','Ban cua so 01',4,'Tang 1','Ban nhom nho','SAN_SANG','nh-demo-001',false,NOW(),NOW()),
('ban-demo-002','Ban gia dinh 02',6,'Tang 1','Ban gia dinh','SAN_SANG','nh-demo-001',false,NOW(),NOW()),
('ban-demo-003','Ban tiec 03',10,'Tang 2','Ban nhom dong','SAN_SANG','nh-demo-001',false,NOW(),NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO thuc_don (id, ten_thuc_don, phan_loai, trang_thai, nha_hang_id, created_at, updated_at)
VALUES ('menu-demo-001','Thuc don chinh','COMBO','DANG_HIEN_THI','nh-demo-001',NOW(),NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO mon_an (id, ten_mon, gia_ban, danh_muc_mon, mo_ta, duong_dan_url, trang_thai, thuc_don_id, deleted, created_at, updated_at) VALUES
('dish-demo-001','Lau ga la e',320000,'Mon chinh','Dac san Da Lat','https://picsum.photos/seed/dish1/640/360','CO_SAN','menu-demo-001',false,NOW(),NOW()),
('dish-demo-002','Com nieu ca kho',180000,'Mon chinh','Mon truyen thong','https://picsum.photos/seed/dish2/640/360','CO_SAN','menu-demo-001',false,NOW(),NOW()),
('dish-demo-003','Rau cu nuong',90000,'Mon phu','Rau cu tuoi','https://picsum.photos/seed/dish3/640/360','CO_SAN','menu-demo-001',false,NOW(),NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO anh_khach_san (id, duong_dan_url, la_anh_dai_dien, mo_ta_anh, ngay_tai_len, khach_san_id) VALUES
('img-hotel-001','https://picsum.photos/seed/hotel1/900/500',true,'Anh dai dien 1',CURRENT_DATE,'3968d549-0e95-4be5-bffa-d3c01f88563e'),
('img-hotel-002','https://picsum.photos/seed/hotel2/900/500',true,'Anh dai dien 2',CURRENT_DATE,'7f105953-a5ad-4e9b-81eb-883673c79692')
ON CONFLICT (id) DO NOTHING;

INSERT INTO anh_nha_hang (id, duong_dan_url, la_anh_dai_dien, mo_ta_anh, ngay_tai_len, nha_hang_id)
VALUES ('img-restaurant-001','https://picsum.photos/seed/restaurant1/900/500',true,'Anh dai dien nha hang',CURRENT_DATE,'nh-demo-001')
ON CONFLICT (id) DO NOTHING;

INSERT INTO anh_phong (id, duong_dan_url, la_anh_dai_dien, mo_ta_anh, ngay_tai_len, phong_id) VALUES
('img-room-001','https://picsum.photos/seed/room1/900/500',true,'Anh phong 1',CURRENT_DATE,'ecef07f6-2287-405e-949f-c698fcaa96a0'),
('img-room-002','https://picsum.photos/seed/room2/900/500',true,'Anh phong 2',CURRENT_DATE,'46cbc784-dfb2-45cb-be89-da908673117f')
ON CONFLICT (id) DO NOTHING;

INSERT INTO don_dat_cho (
  id, ma_don, ten_nguoi_dat, sdt_nguoi_dat, email_nguoi_dat,
  trang_thai, trang_thai_don, tong_tien, tong_tien_goc, tong_tien_thanh_toan,
  so_tien_da_thanh_toan, tien_khuyen_mai,
  ngay_lap, ngay_tao, created_at, updated_at,
  deleted, khach_hang_id, ho_so_kinh_doanh_id, ghi_chu
) VALUES (
  'booking-ai-demo-001','BOOK-AI-001','Nguyen Ngoc Lan','0909000111','demo.customer@example.com',
  'DANG_CHO','DA_XAC_NHAN',1200000,1300000,1200000,200000,100000,
  NOW(),NOW(),NOW(),NOW(),
  false,'85048fda-9884-4753-8d84-d7e7e74ff461','6d9cd3b9-f367-4827-bc0d-77f90d10ff77','Don test AI notification'
)
ON CONFLICT (id) DO NOTHING;
