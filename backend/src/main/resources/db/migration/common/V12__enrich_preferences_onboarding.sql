-- V12: Bổ sung sở thích cho onboarding - nâng tổng từ 5 lên 25 items
-- Giữ nguyên 3 danh mục: Ẩm Thực, Du Lịch, Trải Nghiệm

-- Cập nhật tên danh mục cho thân thiện tiếng Việt
UPDATE danh_muc_so_thich SET ten_danh_muc = 'Ẩm thực', mo_ta = 'Khám phá ẩm thực Việt Nam và quốc tế' WHERE id = 'pref-cat-1';
UPDATE danh_muc_so_thich SET ten_danh_muc = 'Du lịch', mo_ta = 'Phong cách du lịch và điểm đến yêu thích' WHERE id = 'pref-cat-2';
UPDATE danh_muc_so_thich SET ten_danh_muc = 'Trải nghiệm', mo_ta = 'Hoạt động trải nghiệm và giải trí' WHERE id = 'pref-cat-3';

-- Cập nhật tên 5 sở thích cũ cho thân thiện tiếng Việt hơn
UPDATE so_thich SET ten_so_thich = 'Hải sản' WHERE id = 'pref-1';
UPDATE so_thich SET ten_so_thich = 'Đồ nướng' WHERE id = 'pref-2';
UPDATE so_thich SET ten_so_thich = 'Nghỉ dưỡng biển' WHERE id = 'pref-3';
UPDATE so_thich SET ten_so_thich = 'Homestay / Farmstay' WHERE id = 'pref-4';
UPDATE so_thich SET ten_so_thich = 'Spa & Massage' WHERE id = 'pref-5';

-- ==================== ẨM THỰC (pref-cat-1): Thêm 6 món ====================
INSERT INTO so_thich (id, ten_so_thich, danh_muc_id) VALUES
('pref-food-01', 'Lẩu', 'pref-cat-1'),
('pref-food-02', 'Buffet', 'pref-cat-1'),
('pref-food-03', 'Món Việt truyền thống', 'pref-cat-1'),
('pref-food-04', 'Món Hàn - Nhật', 'pref-cat-1'),
('pref-food-05', 'Món Âu', 'pref-cat-1'),
('pref-food-06', 'Ăn chay / Healthy', 'pref-cat-1')
ON CONFLICT DO NOTHING;

-- ==================== DU LỊCH (pref-cat-2): Thêm 7 kiểu ====================
INSERT INTO so_thich (id, ten_so_thich, danh_muc_id) VALUES
('pref-travel-01', 'Resort sang trọng', 'pref-cat-2'),
('pref-travel-02', 'Khách sạn 5 sao', 'pref-cat-2'),
('pref-travel-03', 'Du lịch khám phá', 'pref-cat-2'),
('pref-travel-04', 'Du lịch sinh thái', 'pref-cat-2'),
('pref-travel-05', 'Du lịch tâm linh', 'pref-cat-2'),
('pref-travel-06', 'Trekking / Leo núi', 'pref-cat-2'),
('pref-travel-07', 'City tour thành phố', 'pref-cat-2')
ON CONFLICT DO NOTHING;

-- ==================== TRẢI NGHIỆM (pref-cat-3): Thêm 7 hoạt động ====================
INSERT INTO so_thich (id, ten_so_thich, danh_muc_id) VALUES
('pref-exp-01', 'Yoga / Thiền', 'pref-cat-3'),
('pref-exp-02', 'Thể thao mạo hiểm', 'pref-cat-3'),
('pref-exp-03', 'Workshop nấu ăn', 'pref-cat-3'),
('pref-exp-04', 'Tour ẩm thực đường phố', 'pref-cat-3'),
('pref-exp-05', 'Chụp ảnh / Check-in', 'pref-cat-3'),
('pref-exp-06', 'Show diễn / Nghệ thuật', 'pref-cat-3'),
('pref-exp-07', 'Teambuilding / Sự kiện', 'pref-cat-3')
ON CONFLICT DO NOTHING;
