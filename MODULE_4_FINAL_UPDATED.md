# MODULE 4 FINAL UPDATED - KHUYEN MAI, VOUCHER, TICH DIEM, HANG THANH VIEN

## 0) MUC TIEU TAI LIEU

Tai lieu nay la ban dac ta **implementation-ready** cho Module 4 theo huong production:

- Partner-first (DOI_TAC la role van hanh chinh).
- Khong phu thuoc luong admin approval.
- Tuong thich voi codebase hien tai (booking + complaint resolution da ton tai).
- Co lo trinh rollout an toan theo phase.

---

## 1) PHAM VI CHUC NANG CAN CO

Module 4 bao gom day du cac nhom tinh nang sau:

1. Loyalty Point System
2. Member Tier System
3. Tier Progress
4. Voucher Wallet
5. Redeem Point
6. Promotion Campaign (Partner)
7. Promotion Analytics (Partner)
8. Milestone Reward
9. Checkout Discount Engine
10. Complaint Compensation
11. Loyalty Dashboard
12. Notification Events

---

## 2) NGUYEN TAC THIET KE

1. Partner-first:
   - DOI_TAC tao/quan ly campaign cho co so cua minh.
   - USER su dung voucher, tich diem, doi diem.
   - Khong dua admin vao flow van hanh thuong xuyen.
2. Booking money la source of truth:
   - `tong_tien_goc`, `tien_khuyen_mai`, `tong_tien_thanh_toan` tren booking.
3. Voucher consume muon:
   - Apply -> RESERVED
   - Payment success -> USED
   - Payment fail/cancel -> AVAILABLE
4. Transaction safety:
   - Doi diem, reserve/consume voucher phai transaction + lock.
5. Co the release theo phase:
   - Moi phase co DoD rieng, khong block toan module.

---

## 3) DOMAIN MODEL CHI TIET

## 3.1 Bang uu dai/campaign

### `uu_dai`
- id
- ten_uu_dai
- mo_ta
- loai_uu_dai (VOUCHER_CAMPAIGN | DIRECT_PROMOTION)
- loai_giam_gia (PERCENT | FIXED_AMOUNT)
- muc_giam
- muc_giam_toi_da
- don_hang_toi_thieu
- ngay_bat_dau
- ngay_ket_thuc
- trang_thai (SCHEDULED | ACTIVE | PAUSED | EXPIRED | DELETED)
- pham_vi_ap_dung (PARTNER | BUSINESS_PROFILE | ASSET)
- partner_id
- business_profile_id (nullable)
- asset_type (HOTEL | ROOM | RESTAURANT | MENU_ITEM, nullable)
- asset_id (nullable)
- created_at
- updated_at
- deleted

### `voucher_campaign`
- id (FK -> uu_dai.id)
- ma_voucher (unique voi loai public)
- so_luong_phat_hanh
- so_luong_da_dung
- usage_limit_per_user
- cho_phep_doi_diem
- diem_can_doi
- reserve_timeout_seconds

## 3.2 Bang voucher cua khach

### `customer_voucher`
- id
- customer_id
- voucher_campaign_id
- ma_voucher_ca_nhan
- trang_thai (AVAILABLE | RESERVED | USED | EXPIRED | CANCELLED)
- reserved_at
- reserve_expires_at
- used_at
- expired_at
- booking_id (nullable)
- source_type (CAMPAIGN | POINT_REDEEM | COMPLAINT_COMPENSATION | MILESTONE_REWARD | SYSTEM_GRANT)
- created_at
- updated_at

## 3.3 Bang diem/hang thanh vien

### `loyalty_rule`
- id
- money_per_point
- tier_bac_threshold
- tier_vang_threshold
- tier_kim_cuong_threshold
- bac_multiplier
- vang_multiplier
- kim_cuong_multiplier
- active
- created_at
- updated_at

### `lich_su_diem`
- id
- customer_id
- loai_giao_dich (POINT_EARNED | POINT_REDEEMED | POINT_REFUND | POINT_ADJUSTMENT | MILESTONE_REWARD)
- so_diem_thay_doi
- diem_truoc
- diem_sau
- booking_id (nullable)
- customer_voucher_id (nullable)
- mo_ta
- created_at

### Reuse `khach_hang`
- diem_thanh_vien
- hang_thanh_vien (DONG | BAC | VANG | KIM_CUONG)
- tong_chi_tieu

## 3.4 Bang analytics campaign

### `promotion_analytics_daily` (pre-aggregate)
- id
- campaign_id
- ngay
- usage_count
- booking_count
- generated_revenue
- discount_cost
- conversion_rate
- created_at
- updated_at

---

## 4) API CONTRACT CHI TIET

## 4.1 Partner - Promotion Campaign

1. `POST /api/v1/partner/promotions`
   - Tao campaign.
2. `GET /api/v1/partner/promotions`
   - Danh sach + filter theo status/date/scope.
3. `GET /api/v1/partner/promotions/{id}`
   - Chi tiet campaign.
4. `PUT /api/v1/partner/promotions/{id}`
   - Sua campaign (neu cho phep theo rule).
5. `PATCH /api/v1/partner/promotions/{id}/pause`
6. `PATCH /api/v1/partner/promotions/{id}/resume`
7. `DELETE /api/v1/partner/promotions/{id}`
   - Soft delete.

Ownership rule:
- Campaign.partner_id phai trung current partner.
- business_profile/asset phai thuoc partner.

## 4.2 Partner - Promotion Analytics

1. `GET /api/v1/partner/promotions/{id}/analytics`
   - Tong hop metrics.
2. `GET /api/v1/partner/promotions/{id}/analytics/daily`
   - Time-series cho chart.

## 4.3 User - Loyalty Wallet

1. `GET /api/v1/user/loyalty/summary`
2. `GET /api/v1/user/loyalty/progress`
3. `GET /api/v1/user/loyalty/history`
4. `GET /api/v1/user/loyalty/wallet/vouchers`
5. `GET /api/v1/user/loyalty/exchangeable-vouchers`
6. `POST /api/v1/user/loyalty/exchange`

## 4.4 User - Checkout Discount Engine

1. `POST /api/v1/user/bookings/{bookingId}/apply-voucher`
2. `DELETE /api/v1/user/bookings/{bookingId}/voucher`
3. `GET /api/v1/user/bookings/{bookingId}/voucher-preview` (optional nhung nen co)

## 4.5 Complaint Compensation integration

Khi action complaint `VOUCHER` hoac `DISCOUNT_CODE` hoan tat:
- Tao `customer_voucher` cho customer.
- Source = `COMPLAINT_COMPENSATION`.
- Phat event `VOUCHER_RECEIVED`.

---

## 5) STATE MACHINE

## 5.1 Voucher wallet state

`AVAILABLE -> RESERVED -> USED`

Nhanh re:
- `RESERVED -> AVAILABLE` khi payment fail/cancel/timeout
- `AVAILABLE -> EXPIRED` khi qua han
- `AVAILABLE/RESERVED -> CANCELLED` khi campaign bi thu hoi

## 5.2 Promotion campaign state

`SCHEDULED -> ACTIVE -> EXPIRED`

Nhanh re:
- `ACTIVE -> PAUSED`
- `PAUSED -> ACTIVE` (neu chua het han)
- `* -> DELETED` (soft delete, khong dung cho campaign da dung xong quy mo lon neu policy cam)

## 5.3 Loyalty/tier state

- Tier update dua tren `tong_chi_tieu` sau booking hoan tat.
- Tich diem xay ra sau booking `DA_HOAN_THANH`.

---

## 6) BUSINESS RULE BAT BUOC

## 6.1 Rule tao campaign

1. `ngay_ket_thuc > ngay_bat_dau`
2. `muc_giam > 0`
3. Neu loai % -> `muc_giam <= 100`
4. `don_hang_toi_thieu >= 0`
5. `so_luong_phat_hanh > 0`
6. `usage_limit_per_user > 0`
7. Khong overlap trai policy voi cung target/doc quyen.

## 6.2 Rule apply voucher

1. Booking phai thuoc user hien tai.
2. Booking dang o trang thai cho phep thanh toan.
3. Voucher con hieu luc, con quota, dung scope.
4. Khong vuot usage per user.
5. Dat min order.
6. Discount amount <= max discount (neu co).
7. Sau apply phai cap nhat tong tien booking.

## 6.3 Rule redeem point

1. Kiem tra diem du.
2. Tranh double spend khi mo nhieu tab:
   - lock customer row hoac optimistic lock version.
3. Tao customer_voucher + tru diem + ghi lich_su_diem trong 1 transaction.

## 6.4 Rule consume voucher

1. Chi consume khi payment success.
2. Neu payment fail/cancel:
   - release reserve.
3. Neu callback duplicate:
   - idempotency key + check trang thai truoc khi update.

---

## 7) CONG THUC LOYALTY/TIER

## 7.1 Earn point

- `basePoints = floor(tong_tien_thanh_toan / money_per_point)`
- `finalPoints = floor(basePoints * tier_multiplier)`

Mac dinh de xuat:
- DONG: 1.00
- BAC: 1.10
- VANG: 1.25
- KIM_CUONG: 1.50

## 7.2 Tier threshold (vi du)

- DONG: >= 0
- BAC: >= 5,000,000
- VANG: >= 20,000,000
- KIM_CUONG: >= 50,000,000

## 7.3 Tier progress response

Tra ve:
- currentTier
- totalSpending
- nextTier
- requiredSpending
- remainingSpending
- progressPercent

---

## 8) MILESTONE REWARD

Milestone theo so booking hoan tat:
- booking #5 -> +100 diem
- booking #10 -> voucher fixed
- booking #20 -> voucher percent

Rule:
1. Moi milestone chi nhan 1 lan / customer.
2. Reward grant phai idempotent (co unique key customer+milestone).
3. Phat event `MILESTONE_REWARD_GRANTED`.

---

## 9) CHECKOUT DISCOUNT ENGINE CHI TIET

## 9.1 Apply flow

1. Load booking.
2. Validate ownership + status.
3. Validate voucher eligibility.
4. Reserve voucher (neu voucher ca nhan) hoac reserve usage slot.
5. Tinh `discountAmount`.
6. Update booking:
   - `tien_khuyen_mai = discountAmount`
   - `tong_tien_thanh_toan = tong_tien_goc - discountAmount`
7. Return breakdown.

## 9.2 Remove flow

1. Validate booking status + ownership.
2. Release reserve.
3. Reset discount booking.

## 9.3 Payment webhook flow

1. Payment success:
   - consume voucher (USED)
   - tang `so_luong_da_dung`
2. Payment fail/cancel:
   - release reserve ve AVAILABLE.

---

## 10) COMPLAINT COMPENSATION

Loai action boi thuong su dung:
- FULL_REFUND
- PARTIAL_REFUND
- VOUCHER
- DISCOUNT_CODE

Neu la `VOUCHER`/`DISCOUNT_CODE`:
1. Partner complete action
2. Tao `customer_voucher`
3. Gan source `COMPLAINT_COMPENSATION`
4. Gui notification cho customer

---

## 11) PROMOTION ANALYTICS

Metrics toi thieu:
1. Usage Count
2. Booking Count
3. Generated Revenue
4. Discount Cost
5. Conversion Rate

Cong thuc goi y:
- conversion = bookings_applied / impressions (neu co tracking)
- discount_cost = sum(tien_khuyen_mai cua booking thanh cong)

---

## 12) NOTIFICATION EVENTS

Module phat sinh cac event:
1. POINT_EARNED
2. TIER_UPGRADED
3. VOUCHER_RECEIVED
4. VOUCHER_EXPIRING_SOON
5. MILESTONE_REWARD_GRANTED
6. VOUCHER_APPLIED
7. VOUCHER_CONSUMED
8. VOUCHER_RELEASED

Event payload toi thieu:
- eventType
- userId
- businessId (neu can)
- refId
- metadata
- createdAt

---

## 13) SECURITY & PERMISSION

1. Partner API:
   - Chi DOI_TAC.
   - Ownership check theo partner/businessProfile/asset.
2. User API:
   - Chi USER.
   - Booking, wallet, voucher deu phai thuoc current user.
3. Forbidden cases:
   - apply voucher nguoi khac
   - doi diem thay nguoi khac
   - partner sua campaign cua partner khac

---

## 14) VALIDATION & ERROR CONTRACT

## 14.1 Error code goi y

- PROMOTION_NOT_ACTIVE
- VOUCHER_EXPIRED
- VOUCHER_OUT_OF_QUOTA
- VOUCHER_USAGE_LIMIT_EXCEEDED
- VOUCHER_SCOPE_MISMATCH
- BOOKING_NOT_PAYABLE
- INSUFFICIENT_POINTS
- CONCURRENT_REDEEM_CONFLICT
- RESERVE_TIMEOUT

## 14.2 API error shape

```json
{
  "timestamp": "2026-06-01T02:00:00Z",
  "status": 400,
  "errorCode": "VOUCHER_EXPIRED",
  "message": "Voucher da het han",
  "path": "/api/v1/user/bookings/abc/apply-voucher"
}
```

---

## 15) PHASE PLAN TRIEN KHAI

## Phase 1 - Schema + Entity + Repository
- Migrations, enums, entities, indexes, constraints.
- DoD: migrate pass + compile pass.

## Phase 2 - Eligibility engine + Partner campaign APIs
- Tao/sua/list/pause/resume + validation overlap.
- DoD: unit + integration test cho campaign.

## Phase 3 - Checkout apply/remove + reserve/release
- Booking recalc + reserve lifecycle.
- DoD: checkout flow pass voi payment fail/success cases.

## Phase 4 - Loyalty APIs + redeem + milestone
- summary/progress/history/exchange.
- DoD: point/tier/redeem/milestone pass voi race-condition tests.

## Phase 5 - Analytics + Notification + Hardening
- analytics APIs + event stream.
- DoD: dashboard metrics nhat quan, event phat dung.

---

## 16) FRONTEND SCOPE

## 16.1 Partner
- Promotion list page
- Create/edit form
- Pause/resume actions
- Analytics page

## 16.2 User
- Checkout voucher box (apply/remove + error states)
- Loyalty dashboard (summary/progress/history)
- Voucher wallet (available/expiring soon/used)
- Redeem voucher flow

---

## 17) TEST MATRIX BAT BUOC

## 17.1 Unit tests
1. Eligibility pass/fail theo tung rule.
2. Point earn formula + tier multiplier.
3. Redeem lock/concurrency.
4. Reserve/consume/release state transitions.

## 17.2 Integration tests
1. Partner promotions CRUD + pause/resume.
2. Apply/remove voucher booking.
3. Payment success/fail callback with idempotency.
4. Loyalty summary/history/exchange.
5. Complaint compensation -> wallet.

## 17.3 E2E/UAT scenarios
1. User apply voucher hop le -> thanh toan thanh cong -> USED.
2. User apply voucher hop le -> thanh toan that bai -> AVAILABLE.
3. Hai request redeem dong thoi -> 1 thanh cong, 1 conflict.
4. Partner campaign het han khong resume duoc.
5. Voucher sai scope bi chan dung message.

---

## 18) DATA SEED DE XUAT CHO QA

Can seed da dang:
1. Campaign active/scheduled/paused/expired.
2. Voucher fixed/% + max discount + min order.
3. Wallet vouchers o tat ca trang thai.
4. Customer du/khong du diem.
5. Milestone da nhan/chua nhan.

Tai khoan test chuan:
- Customer: `anhbid1000@gmail.com`
- Partner: `anhbid2000@gmail.com`

---

## 19) DEFINITION OF DONE (FULL MODULE 4)

Module 4 duoc xem la hoan thanh khi:

1. Loyalty:
   - Cong diem sau booking hoan tat.
   - Tier update dung rule.
   - Co lich su diem day du.
2. Voucher:
   - Wallet hoat dong day du states.
   - Redeem point hoat dong va an toan dong thoi.
3. Campaign:
   - Partner CRUD + pause/resume + ownership chuan.
4. Checkout:
   - Apply/remove voucher dung.
   - Reserve/consume/release dung voi payment lifecycle.
5. Compensation:
   - Complaint action tao voucher vao wallet thanh cong.
6. Analytics:
   - Metrics campaign tra ve dung, thong nhat voi booking data.
7. Notification:
   - Event phat sinh dung tai cac moc quan trong.
8. Security:
   - Khong co hole ownership/role.
9. QA:
   - Pass test matrix unit/integration/UAT.

---

## 20) GHI CHU VAN HANH

1. Bat feature theo flag:
   - `feature.partnerPromotions`
   - `feature.checkoutVoucher`
   - `feature.loyaltyWallet`
2. Khuyen nghi rollout:
   - staging soak test truoc khi mo full production.
3. Theo doi:
   - ty le apply fail
   - reserve timeout ratio
   - redeem conflict rate
   - mismatch booking discount anomaly

