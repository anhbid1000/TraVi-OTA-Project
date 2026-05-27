# Promotion/Voucher/Loyalty - Progress Plan

## Mục tiêu
Triển khai full backend cho Promotion/Voucher/Loyalty theo 6 giai đoạn đã thống nhất.

## Trạng thái tổng quan
- [x] Giai đoạn 1: Database & Entities
- [~] Giai đoạn 2: Repositories & DTOs (DTO đã điền theo ngữ cảnh, repository đã chuẩn hóa JpaRepository)
- [ ] Giai đoạn 3: Partner & Admin APIs
- [ ] Giai đoạn 4: Customer Loyalty APIs
- [ ] Giai đoạn 5: Apply Voucher trong Booking
- [ ] Giai đoạn 6: Tích điểm sau Booking
- [ ] Test & QA end-to-end

---

## Giai đoạn 1 - Database & Entities ✅
### Done
- [x] UuDai (@Inheritance JOINED)
- [x] Voucher
- [x] KhuyenMaiTrucTiep
- [x] CustomerVoucher
- [x] KhachHang cập nhật loyalty fields + @Version
- [x] LichSuDiem
- [x] LoyaltyRule
- [x] Enum liên quan

### Checklist xác nhận nhanh
- [ ] Ràng buộc dữ liệu quan trọng đã enforce (db/service): ngày, số lượng, mức giảm
- [ ] Migration/schema sync chạy sạch

---

## Giai đoạn 2 - Repositories & DTOs 🔄
### Done
- [x] Tạo repository interfaces
- [x] Tạo request/response DTO classes

### Cần hoàn thiện tiếp
- [x] Chuẩn hóa repository theo Spring Data JPA (`extends JpaRepository<Entity, IdType>`)
- [x] Chuẩn hóa DTO naming/structure theo coding style project (class/record)
- [ ] Review package và import consistency
- [ ] Compile verify khi môi trường Java sẵn sàng (`JAVA_HOME` hiện chưa cấu hình)

---

## Giai đoạn 3 - Partner/Admin Promotion APIs
### 3.1 Partner tạo promotions
- [ ] POST `/api/v1/partner/promotions`
- [ ] Check role DOI_TAC
- [ ] Validate businessProfile ownership
- [ ] Validate voucher uniqueness/time/quantity
- [ ] Validate direct promo target ownership + overlap
- [ ] Auto set trạng thái theo thời gian

### 3.2 Admin tạo promotions toàn sàn
- [ ] POST `/api/v1/admin/promotions`
- [ ] Check role QUAN_TRI_VIEN
- [ ] Support businessProfileId = null + phạm vi TOAN_SAN

### 3.3 Danh sách promotions
- [ ] GET `/api/v1/partner/promotions`
- [ ] GET `/api/v1/admin/promotions`

### 3.4 Pause/Resume
- [ ] PATCH `/api/v1/partner/promotions/{id}/pause`
- [ ] PATCH `/api/v1/admin/promotions/{id}/pause`
- [ ] PATCH `/api/v1/partner/promotions/{id}/resume`
- [ ] PATCH `/api/v1/admin/promotions/{id}/resume`

### 3.6 Loyalty rules cho Admin
- [ ] PUT `/api/v1/admin/loyalty/rules`
- [ ] Chỉ 1 rule active

---

## Giai đoạn 4 - Customer Loyalty APIs
- [ ] GET `/api/v1/customers/me/loyalty`
- [ ] GET `/api/v1/customers/me/loyalty/exchangeable-vouchers`
- [ ] POST `/api/v1/customers/me/loyalty/exchange`
- [ ] `@Transactional` + xử lý optimistic locking conflict

---

## Giai đoạn 5 - Apply Voucher trong Booking
- [ ] POST `/api/v1/bookings/{bookingId}/apply-voucher`
- [ ] DELETE `/api/v1/bookings/{bookingId}/voucher`
- [ ] Chỉ mark DA_DUNG sau payment success (không mark lúc apply)

---

## Giai đoạn 6 - Tích điểm sau booking
- [ ] Trigger khi booking = DA_HOAN_THANH
- [ ] Tính điểm theo loyalty rule active
- [ ] Update tổng chi tiêu + hạng thành viên
- [ ] Ghi lịch sử điểm
- [ ] Rule hoàn điểm/voucher khi hủy/hoàn tiền

---

## Test & QA
- [ ] Unit test: service rules (validation, status transitions, scope checks)
- [ ] Integration test: partner/admin/customer API flows
- [ ] Concurrency test: đổi voucher đồng thời (@Version)
- [ ] Booking flow test: apply/remove/mark-used đúng thời điểm

---

## Cách cập nhật tiến độ
- Dùng ký hiệu:
  - `[ ]` chưa làm
  - `[~]` đang làm
  - `[x]` hoàn thành
- Mỗi khi hoàn tất một API/nhóm logic, tick checklist + ghi commit tương ứng.

## Chuẩn "Done" cho từng task
1. Code xong
2. Có validate/authorization phù hợp
3. Có test pass
4. Không phá flow cũ
5. Cập nhật checklist trong file này
