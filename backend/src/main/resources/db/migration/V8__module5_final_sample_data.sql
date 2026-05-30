-- =============================================================
-- V8__module5_final_sample_data.sql
-- Sample data bổ sung cho entity mới của Module 5 FINAL
-- Tương thích PostgreSQL + H2
-- =============================================================

-- 1) Cập nhật category cho complaint mẫu đã được insert ở V5.
-- V5 chạy trước V7 nên chưa thể insert trực tiếp cột category; V7 thêm cột và backfill OTHER, V8 cập nhật lại category đúng nghiệp vụ.
UPDATE complaint_khieu_nai SET category = 'SERVICE_ATTITUDE' WHERE id IN (
    'COMPLAINT-700', 'COMPLAINT-701', 'COMPLAINT-702', 'COMPLAINT-703', 'COMPLAINT-704',
    'COMPLAINT-705', 'COMPLAINT-706', 'COMPLAINT-707', 'COMPLAINT-708', 'COMPLAINT-709'
);

-- 2) ReviewAspectScore cho review mẫu
INSERT INTO review_aspect_score (id, review_id, aspect, score, created_at)
VALUES
('RAS-700-1','REVIEW-700','CLEANLINESS',5,CURRENT_TIMESTAMP - INTERVAL '9' DAY),
('RAS-700-2','REVIEW-700','SERVICE',5,CURRENT_TIMESTAMP - INTERVAL '9' DAY),
('RAS-701-1','REVIEW-701','CLEANLINESS',3,CURRENT_TIMESTAMP - INTERVAL '10' DAY),
('RAS-710-1','REVIEW-710','FOOD_QUALITY',4,CURRENT_TIMESTAMP - INTERVAL '8' DAY),
('RAS-710-2','REVIEW-710','SERVICE',4,CURRENT_TIMESTAMP - INTERVAL '8' DAY);

-- 3) ResolutionAction cho complaint mẫu
INSERT INTO hanh_dong_xu_ly_khieu_nai
(id, khieu_nai_id, action_type, tieu_de, mo_ta, amount, currency, voucher_code, discount_percent,
 status, proposed_by_partner_id, customer_response_note, partner_completion_note,
 proposed_at, customer_responded_at, completed_at, created_at, updated_at)
VALUES
('ACTION-700-1','COMPLAINT-700','APOLOGY','Xin lỗi chính thức','Đối tác xin lỗi và cam kết cải thiện dịch vụ',NULL,NULL,NULL,NULL,
 'CUSTOMER_REJECTED','3816f90e-d594-4018-ab4e-0921e838121f','Khách chưa đồng ý vì cần phương án cụ thể',NULL,
 CURRENT_TIMESTAMP - INTERVAL '5' DAY,CURRENT_TIMESTAMP - INTERVAL '4' DAY,NULL,CURRENT_TIMESTAMP - INTERVAL '5' DAY,CURRENT_TIMESTAMP - INTERVAL '4' DAY),
('ACTION-705-1','COMPLAINT-705','PARTIAL_REFUND','Hoàn tiền 30%','Đề xuất hoàn tiền 30% do phục vụ chậm',180000,'VND',NULL,NULL,
 'COMPLETED','3816f90e-d594-4018-ab4e-0921e838121f','Khách đồng ý phương án','Đã xử lý hoàn tiền thủ công',
 CURRENT_TIMESTAMP - INTERVAL '3' DAY,CURRENT_TIMESTAMP - INTERVAL '2' DAY,CURRENT_TIMESTAMP - INTERVAL '1' DAY,
 CURRENT_TIMESTAMP - INTERVAL '3' DAY,CURRENT_TIMESTAMP - INTERVAL '1' DAY),
('ACTION-709-1','COMPLAINT-709','VOUCHER','Tặng voucher bù trải nghiệm','Tặng voucher 20% cho lần đặt tiếp theo',NULL,NULL,'PQ-SORRY-20',20,
 'PROPOSED','3816f90e-d594-4018-ab4e-0921e838121f',NULL,NULL,
 CURRENT_TIMESTAMP - INTERVAL '2' DAY,NULL,NULL,CURRENT_TIMESTAMP - INTERVAL '2' DAY,CURRENT_TIMESTAMP - INTERVAL '2' DAY);

-- 4) Activity timeline cho complaint mẫu
INSERT INTO lich_su_hoat_dong_khieu_nai (id, khieu_nai_id, activity_type, actor_id, actor_role, summary, metadata, created_at)
VALUES
('ACT-700-1','COMPLAINT-700','COMPLAINT_CREATED','875f97ce-e548-4507-a4e0-77efc0977ad1','KHACH_HANG','Khách hàng đã tạo khiếu nại.','{"source":"seed"}',CURRENT_TIMESTAMP - INTERVAL '6' DAY),
('ACT-700-2','COMPLAINT-700','ACTION_PROPOSED','3816f90e-d594-4018-ab4e-0921e838121f','DOI_TAC','Đối tác đã đề xuất phương án xử lý.','{"actionId":"ACTION-700-1"}',CURRENT_TIMESTAMP - INTERVAL '5' DAY),
('ACT-700-3','COMPLAINT-700','ACTION_REJECTED','875f97ce-e548-4507-a4e0-77efc0977ad1','KHACH_HANG','Khách hàng đã từ chối phương án xử lý.','{"actionId":"ACTION-700-1"}',CURRENT_TIMESTAMP - INTERVAL '4' DAY),
('ACT-705-1','COMPLAINT-705','ACTION_PROPOSED','3816f90e-d594-4018-ab4e-0921e838121f','DOI_TAC','Đối tác đề xuất hoàn tiền một phần.','{"actionId":"ACTION-705-1"}',CURRENT_TIMESTAMP - INTERVAL '3' DAY),
('ACT-705-2','COMPLAINT-705','ACTION_ACCEPTED','875f97ce-e548-4507-a4e0-77efc0977ad1','KHACH_HANG','Khách hàng đã chấp nhận phương án.','{"actionId":"ACTION-705-1"}',CURRENT_TIMESTAMP - INTERVAL '2' DAY),
('ACT-705-3','COMPLAINT-705','ACTION_COMPLETED','3816f90e-d594-4018-ab4e-0921e838121f','DOI_TAC','Đối tác đã hoàn tất phương án xử lý.','{"actionId":"ACTION-705-1"}',CURRENT_TIMESTAMP - INTERVAL '1' DAY),
('ACT-705-4','COMPLAINT-705','COMPLAINT_RESOLVED',NULL,'SYSTEM','Khiếu nại được đánh dấu đã giải quyết.','{"actionId":"ACTION-705-1"}',CURRENT_TIMESTAMP - INTERVAL '1' DAY);

-- =============================================================
-- END V8
-- =============================================================
