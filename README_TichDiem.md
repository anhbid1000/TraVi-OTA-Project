# README - Chuc nang tich diem, hang thanh vien va voucher

Tai lieu nay tong hop phan da hoan thien cho Module 4 lien quan den loyalty: tich diem, hang thanh vien, doi diem lay voucher, vi voucher, milestone reward va luong hoan tat don de cong diem.

## 1. Trang thai hoan thien

Chuc nang tich diem da hoan thien:

- Khach hang thanh toan thanh cong thi don chuyen sang `DA_THANH_TOAN`.
- Diem chi duoc cong khi doi tac xac nhan don da hoan thanh, tuc trang thai don thanh `DA_HOAN_THANH`.
- He thong tu dong tinh diem theo so tien thanh toan va he so hang thanh vien.
- He thong cap nhat tong chi tieu, hang thanh vien, lich su diem va thong bao su kien.
- He thong chong cong diem trung cho cung mot booking.
- Khach hang co the doi diem lay voucher neu du diem va voucher con hieu luc.
- Voucher trong vi duoc reserve khi ap dung vao don, consume khi thanh toan thanh cong va release khi thanh toan that bai/khach go voucher.
- Milestone booking da co logic thuong diem/voucher va chong cap trung.
- Da co unit test tu dong cho cac luong chinh.

## 2. Luong nghiep vu chinh

### 2.1. Tao don va thanh toan

1. Khach hang tao don dat phong/dat ban.
2. Don o trang thai `CHO_THANH_TOAN`.
3. Khach hang co the ap dung voucher trong vi neu voucher hop le.
4. Khi thanh toan mock/pay callback thanh cong:
   - `PaymentCallbackService.onPaymentSuccess(...)` doi trang thai don tu `CHO_THANH_TOAN` sang `DA_THANH_TOAN`.
   - Voucher dang reserve duoc consume.
   - Chua cong diem o buoc nay.

Ly do: theo nghiep vu loyalty, khach chi duoc cong diem khi don da thuc su hoan thanh dich vu, khong phai ngay khi vua thanh toan.

### 2.2. Doi tac hoan tat don

Doi tac bam nut **Hoan tat** tren dashboard doi tac, hoac goi API:

```http
POST /api/v1/partner/bookings/{bookingId}/complete
Authorization: Bearer <partner_token>
```

Dieu kien:

- User phai co role `DOI_TAC`.
- Booking phai thuoc ho so kinh doanh cua doi tac hien tai.
- Booking chi duoc hoan tat tu cac trang thai:
  - `DA_THANH_TOAN`
  - `DA_XAC_NHAN`
  - `DANG_PHUC_VU`

Ket qua:

- Don duoc chuyen sang `DA_HOAN_THANH`.
- `CustomerLoyaltyService.processBookingCompletionReward(...)` duoc goi.
- Neu don da `DA_HOAN_THANH` tu truoc, service van goi lai loyalty theo cach idempotent; lich su diem se chan cong trung.

### 2.3. Cong diem sau khi don hoan thanh

Cong thuc:

```text
basePoints = tongTienThanhToan / moneyPerPoint
earnedPoints = floor(basePoints * tierMultiplier)
```

Hang thanh vien:

- `DONG`: he so mac dinh `1.0`
- `BAC`: dung `silverMultiplier`
- `VANG`: dung `goldMultiplier`
- `KIM_CUONG`: dung `diamondMultiplier`

Sau khi cong diem:

- Cap nhat `diemThanhVien`.
- Cap nhat `tongChiTieu`.
- Tu dong tinh lai `hangThanhVien`.
- Ghi mot dong vao `lich_su_diem` voi `loaiGiaoDichDiem = TICH_DIEM`.
- Tang tien do milestone booking.
- Emit event `POINT_EARNED`.
- Neu len hang, emit event `TIER_UPGRADED`.

### 2.4. Chong cong diem trung

Moi booking sau khi duoc cong diem se tao ghi chu:

```text
Tich diem cho don <bookingId>
```

Truoc khi cong diem, service kiem tra:

```java
existsByCustomerIdAndLoaiGiaoDichDiemAndGhiChu(...)
```

Neu da ton tai lich su diem cho booking do, service return ngay va khong cong lai diem.

## 3. Doi diem lay voucher

API customer loyalty co luong doi diem:

- Lay danh sach voucher co the doi bang diem.
- Kiem tra voucher dang co hieu luc.
- Kiem tra `choPhepDoiBangDiem = true`.
- Kiem tra so luong phat hanh chua het.
- Kiem tra khach hang du diem.
- Tru diem khach hang.
- Tao `CustomerVoucher` trong vi voi `sourceType = POINT_REDEEM`.
- Ghi lich su diem voi `loaiGiaoDichDiem = DOI_VOUCHER`.
- Emit event `VOUCHER_RECEIVED`.

## 4. Vi voucher va checkout

Trang thai voucher trong vi:

- `CHUA_DUNG`: voucher san sang su dung.
- `RESERVED`: voucher dang giu cho mot don chua thanh toan xong.
- `DA_DUNG`: voucher da duoc consume sau thanh toan thanh cong.
- `HET_HAN`, `BI_THU_HOI`, `CANCELLED`: cac trang thai khong kha dung.

Luong ap dung voucher:

1. Khach hang nhap ma voucher o trang thanh toan.
2. `BookingVoucherService.applyVoucherToBooking(...)` validate:
   - Don thuoc dung khach hang.
   - Don dang `CHO_THANH_TOAN`.
   - Voucher active, chua het han, dat gia tri toi thieu.
   - Khach hang co voucher trong vi.
   - Chua vuot gioi han su dung.
3. Voucher duoc reserve trong 1800 giay.
4. Don luu `voucherId` la id cua `CustomerVoucher`.
5. Tinh `tienKhuyenMai` va `tongTienThanhToan`.

Preview voucher hien cung dung `validateVoucherEligibility(...)`, nen voucher het han/khong du dieu kien se tra `isEligible = false`.

## 5. Milestone reward

He thong tang `completedBookings` moi khi booking duoc cong diem thanh cong.

Cac moc hien tai:

- Moc 5 booking: thuong diem milestone.
- Moc 10 booking: cap voucher milestone.
- Moc 20 booking: cap voucher milestone.

Service dung lock o repository milestone de giam rui ro cap trung khi co nhieu request dong thoi.

## 6. Database va migration lien quan

Bang/field chinh:

- `khach_hang.diem_thanh_vien`
- `khach_hang.tong_chi_tieu`
- `khach_hang.hang_thanh_vien`
- `lich_su_diem`
- `customer_voucher`
- `loyalty_rule`
- `milestone_progress`
- `notification_event.metadata`

Migration bo sung:

```text
backend/src/main/resources/db/migration/common/V6__loyalty_history_booking_uuid.sql
```

Muc dich:

- Doi `lich_su_diem.booking_id` sang `VARCHAR(36)` de luu booking UUID.

Ngoai ra da cau hinh Hibernate enum/jsonb de tranh loi PostgreSQL:

- PostgreSQL enum duoc map bang `@JdbcTypeCode(SqlTypes.NAMED_ENUM)`.
- `notification_event.metadata` duoc map `jsonb` bang `@JdbcTypeCode(SqlTypes.JSON)`.

## 7. Frontend da hoan thien

### 7.1. Customer loyalty dashboard

Trang customer loyalty hien thi:

- Diem hien tai.
- Hang hien tai.
- Hang tiep theo.
- Tong chi tieu.
- So tien con can de len hang.
- Progress bar.
- Lich su diem.
- Voucher trong vi.

Da bo sung label cho cac loai giao dich diem:

- `TICH_DIEM`
- `DOI_VOUCHER`
- `HOAN_DIEM`
- `DIEU_CHINH_ADMIN`
- `MILESTONE_REWARD`
- `COMPENSATION`

### 7.2. Payment page

Trang thanh toan:

- Ap dung voucher.
- Preview voucher.
- Go voucher.
- Hien loi API ro hon bang `getApiErrorMessage(...)`.

### 7.3. Partner dashboard

Bang don gan day cua doi tac co nut **Hoan tat** cho don co trang thai:

- `DA_THANH_TOAN`
- `DA_XAC_NHAN`
- `DANG_PHUC_VU`

Khi bam nut, frontend goi:

```http
POST /api/v1/partner/bookings/{bookingId}/complete
```

Sau do dashboard reload de cap nhat trang thai don.

## 8. Unit test da bo sung

### BookingVoucherServiceTest

Kiem thu:

- Ap dung voucher thanh cong.
- Chan voucher het han.
- Chan don khong dat gia tri toi thieu.
- Chan vuot gioi han su dung.
- Go voucher thanh cong.
- Chan user go voucher cua don nguoi khac.
- Preview voucher het han tra `isEligible = false`.

### CustomerLoyaltyServiceTest

Kiem thu:

- Cong diem khi booking `DA_HOAN_THANH`.
- He so hang `BAC x1.1`.
- Don chua hoan thanh thi khong cong diem.
- Khong cong diem trung cho cung booking.
- Len hang va emit event upgrade.
- Doi diem lay voucher thanh cong.
- Chan doi voucher khi khong du diem.
- Tinh progress len hang dung.

### PartnerBookingLifecycleServiceTest

Kiem thu:

- Doi tac hoan tat don thanh cong.
- Chan doi tac khong so huu don.
- Chan hoan tat tu trang thai khong hop le.
- Don da hoan tat van idempotent.

### PaymentCallbackServiceTest

Kiem thu:

- Thanh toan thanh cong chi chuyen don sang `DA_THANH_TOAN`, khong emit cong diem.

### MilestoneRewardServiceTest

Kiem thu:

- Booking thu 5 cap diem milestone.
- Khong cap lai milestone 5 lan hai.

## 9. Lenh kiem thu da chay

Chay rieng nhom loyalty/voucher:

```bash
docker compose exec backend ./mvnw -q "-Dtest=CustomerLoyaltyServiceTest,BookingVoucherServiceTest,PartnerBookingLifecycleServiceTest,PaymentCallbackServiceTest,MilestoneRewardServiceTest" test
```

Chay toan bo backend test:

```bash
docker compose exec backend ./mvnw -q test
```

Ket qua: tat ca test pass.

## 10. Ket luan

Chuc nang tich diem da hoan thien:

- Dung thoi diem cong diem: sau khi don hoan thanh.
- Co chong cong trung.
- Co tich hop hang thanh vien.
- Co doi diem lay voucher.
- Co vi voucher va checkout voucher.
- Co milestone reward.
- Co frontend cho customer va partner.
- Co unit test bao ve cac luong chinh.

