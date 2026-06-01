# Module 4 - Phase 3 Implementation Report

## 1) Current conclusion

- **Phase 3 backend logic is completed** for checkout voucher flow: apply/remove voucher, reserve/release lifecycle, and payment callback consume/release behavior.
- This report focuses on what was implemented for Phase 3 and related integrations that are already present in the current working tree.

## 2) Implemented in Phase 3 (checkout + reserve lifecycle)

### 2.1 Core service: checkout voucher flow

**File:** `backend/src/main/java/com/ota/travi/service/BookingVoucherService.java`

Implemented behaviors:
- Apply voucher to booking:
  - Validate booking ownership.
  - Validate booking is payable (`CHO_THANH_TOAN`).
  - Validate voucher active window, quota, min order, per-user usage.
  - Reserve `customer_voucher` before applying discount.
  - Recalculate booking totals (`tien_khuyen_mai`, `tong_tien_thanh_toan`).
- Remove voucher from booking:
  - Validate ownership + payable state.
  - Release reserved voucher when needed.
  - Reset booking discount fields back to original total.
- Preview/available vouchers:
  - Return eligible and ineligible reasons.
  - Sort eligible vouchers by discount descending.
- Discount calculation:
  - Support `PHAN_TRAM` and `SO_TIEN_CO_DINH`.
  - Respect max cap (`giaTriGiamToiDa`).
  - Never exceed order amount.

### 2.2 Reserve/release consistency fix

**File:** `backend/src/main/java/com/ota/travi/service/VoucherReserveService.java`

Implemented/fixed:
- `reserveVoucher(...)` now resolves voucher by `customerVoucher.getVoucherId()` (correct semantic id), avoiding wrong lookup by customer voucher id.

### 2.3 Payment callback integration

**File:** `backend/src/main/java/com/ota/travi/service/PaymentCallbackService.java`

Implemented/fixed:
- Validate booking belongs to callback customer.
- Payment success:
  - Consume reserved voucher.
  - Clear booking voucher reference.
- Payment fail:
  - Release reserved voucher.
  - Clear booking voucher reference.
  - Reset booking discount totals to original amount.

### 2.4 User API endpoints for Phase 3 checkout flow

**File:** `backend/src/main/java/com/ota/travi/controller/UserBookingController.java`

Added endpoints:
- `POST /api/v1/user/bookings/{bookingId}/apply-voucher`
- `DELETE /api/v1/user/bookings/{bookingId}/voucher`
- `GET /api/v1/user/bookings/{bookingId}/voucher-preview?maVoucher=...`
- `GET /api/v1/user/bookings/{bookingId}/available-vouchers`

## 3) Related fixes already included in current working tree

### 3.1 Complaint compensation voucher compile/runtime alignment

**File:** `backend/src/main/java/com/ota/travi/service/ComplaintCompensationVoucherService.java`

Fixed:
- Enum values aligned with codebase (`SO_TIEN_CO_DINH`, `DANG_CO_HIEU_LUC`).
- `emitVoucherReceived(...)` call updated to new method signature (includes source type).

### 3.2 Partner analytics endpoints exposed

**File:** `backend/src/main/java/com/ota/travi/controller/PartnerPromotionController.java`

Added endpoints:
- `GET /api/v1/partner/promotions/{id}/analytics`
- `GET /api/v1/partner/promotions/{id}/analytics/daily`

## 4) Build/test status

- **Compile:** pass (`mvn -DskipTests compile`).
- **Test suite:** currently fails due to test-environment Flyway migration compatibility (not from Phase 3 business logic).

## 5) Flyway test failure root cause

Error observed:
- `Unknown data type: "JSONB"` on H2 during Flyway migrate.
- File: `backend/src/main/resources/db/migration/V1__init_schema_unified.sql`
- Line area: `metadata JSONB` in table `su_kien_hanh_vi`.

Why:
- Test profile uses H2 (`backend/src/test/resources/application-test.yml`), while migration script includes PostgreSQL-specific type `JSONB`.

## 6) Recommended fix path for Flyway test failure

### Option A (recommended) - run integration tests on PostgreSQL (Testcontainers)

Best long-term because schema is PostgreSQL-oriented.

Steps:
1. In test profile, switch datasource to PostgreSQL Testcontainers.
2. Keep Flyway migrations unchanged (native PG syntax remains valid).
3. Mark DB-dependent tests as integration tests (or keep current suite if acceptable).

### Option B - keep H2, add Flyway placeholders for JSON type

Use placeholder in migration:
- Replace `metadata JSONB` with `metadata ${json_type}`
- In prod/dev: `spring.flyway.placeholders.json_type=JSONB`
- In test(H2): `spring.flyway.placeholders.json_type=JSON`

### Option C - separate migration location for test/H2

Maintain two migration sets:
- `db/migration` for PostgreSQL.
- `db/migration-h2` for H2-compatible schema.

This works but increases maintenance overhead.

## 7) Practical recommendation

- If your target runtime is PostgreSQL, choose **Option A** for reliable behavior parity.
- If you need quick CI unblock first, implement **Option B** immediately, then migrate to Option A when ready.

