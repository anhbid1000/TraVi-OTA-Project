# SO SÁNH: PROJECT CŨ vs MODULE_4_FINAL_UPDATED vs PROJECT HIỆN TẠI

## I. TỔNG QUAN

### Project Cũ (D:\HKVI\Project-TraVi-OTA)
- **Trạng thái**: Đã làm sơ sơ logic promotion/voucher/loyalty  
- **Hoàn thành**: ~50-60% logic cơ bản  
- **Thiếu**: State machine, reserve/release, milestone, analytics, events  

### MODULE_4_FINAL_UPDATED.md
- **Yêu cầu**: Triển khai đầy đủ (promotion campaign, loyalty points, tier system, voucher wallet, checkout, analytics, notifications)  
- **5 Phases**: Schema → Campaign APIs → Checkout → Loyalty APIs → Analytics  

### Project Hiện Tại (TraVi-OTA-Project)
- **Trạng thái**: Chưa có module 4 (chỉ có booking + complaint resolution)  
- **Kiến trúc**: Tương thích (Spring Boot, JPA, same entity patterns)  
- **Sẵn có**: Booking system, complaint compensation mechanism  

---

## II. PHÂN TÍCH CHI TIẾT - CÓ THỂ TÁI SỬ DỤNG?

### 2.1 DATABASE SCHEMA

| Thành phần | Project Cũ | Module_4 Required | Khuyến nghị |
|-----------|-----------|------------------|-------------|
| `uu_dai` (base table) | ✅ Có | ✅ Cần | **Tái sử dụng 100%** - Schema giống hệt, chỉ cần migration sang project hiện tại |
| `voucher` (subclass) | ✅ Có | ✅ Cần | **Tái sử dụng 100%** - Kế thừa từ `uu_dai` via JOINED inheritance |
| `khuyen_mai_truc_tiep` | ✅ Có | ✅ Cần | **Tái sử dụng 100%** - Direct promotion subclass |
| `customer_voucher` | ✅ Có | ✅ Cần | **Tái sử dụng 90%** - Cần thêm fields: `reserved_at`, `reserve_expires_at`, `source_type` |
| `loyalty_rule` | ✅ Có | ✅ Cần | **Tái sử dụng 95%** - Cần review multiplier fields |
| `lich_su_diem` | ✅ Có | ✅ Cần | **Tái sử dụng 90%** - Cần thêm `loai_giao_dich` enum cho MILESTONE_REWARD |
| `khach_hang` | ✅ Có | ✅ Cần | **Tái sử dụng 100%** - Đã có `diem_thanh_vien`, `hang_thanh_vien`, `tong_chi_tieu` |
| `promotion_analytics_daily` | ❌ Không | ✅ Cần | **Viết mới** - Chưa có trong project cũ |

**Kết luận Schema**: **60-70% có thể tái sử dụng trực tiếp**, phần còn lại cần enhancement nhỏ.

---

### 2.2 ENTITIES & ENUMS

#### Entities Project Cũ
```
✅ UuDai.java          - Có @Inheritance JOINED
✅ Voucher.java        - Extends UuDai
✅ KhuyenMaiTrucTiep   - Extends UuDai (Direct Promotion)
✅ CustomerVoucher.java
✅ LoyaltyRule.java
✅ LichSuDiem.java
✅ KhachHang.java
```

#### Enums Project Cũ
```
✅ TrangThaiUuDai (DA_LEN_LICH, DANG_CO_HIEU_LUC, TAM_DUNG, DA_HET_HAN)
✅ TrangThaiCustomerVoucher (CHUA_DUNG, DUNG, HET_HAN, HUY)
✅ LoaiGiamGia (PHAN_TRAM, SO_TIEN_CO_DINH)
✅ CreatedByRole (QUAN_TRI_VIEN, DOI_TAC)
✅ HangThanhVien (DONG, BAC, VANG, KIM_CUONG)
✅ LoaiGiaoDichDiem (TICH_DIEM, DOI_VOUCHER, ...)
```

#### Thiếu so với Module_4
```
❌ TrangThaiCustomerVoucher.RESERVED
❌ TrangThaiCustomerVoucher.CANCELLED
❌ LoaiGiaoDichDiem.POINT_REFUND, POINT_ADJUSTMENT, MILESTONE_REWARD
❌ SourceType enum (CAMPAIGN, POINT_REDEEM, COMPLAINT_COMPENSATION, MILESTONE_REWARD, SYSTEM_GRANT)
❌ PhamViApDung enum (PARTNER, BUSINESS_PROFILE, ASSET)
❌ TargetType enum (ALL_PLATFORM, HOTEL, RESTAURANT, ROOM, MENU_ITEM)
```

**Kết luận Entities**: **80-85% có thể tái sử dụng**, cần thêm enum và fields mới.

---

### 2.3 SERVICES

#### PromotionService (Project Cũ)
```java
✅ createDirectPromotion()       - PromotionCreateRequest → PromotionResponse
✅ createVoucher()               - VoucherCreateRequest → PromotionResponse
✅ getPromotionsByPartner()
✅ getAllPromotions()
✅ pausePromotion()
✅ resumePromotion()
✅ applyVoucher()               - Tính discount amount (formula OK)
✅ accumulatePoints()           - Tích điểm sau booking
❌ applyVoucher() cho booking   - Không lưu voucher state vào booking
❌ Reserve/Release logic        - Không có RESERVED state
❌ Milestone rewards            - Không có
```

#### CustomerLoyaltyService (Project Cũ)
```java
✅ getLoyaltySummary()          - Tra về points, tier, progress, history, vouchers
✅ getExchangeableVouchers()
✅ exchangeVoucher()            - Đổi voucher bằng điểm
❌ Milestone reward logic       - Không có
❌ Concurrent redeem handling   - Cơ bản nhưng cần @Version lock
```

#### Thiếu trong Module_4
```
❌ VoucherReserveService        - Reserve/release lifecycle
❌ MilestoneRewardService       - Grant rewards sau booking #5, #10, #20
❌ PromotionAnalyticsService    - Track usage, revenue, discount cost
❌ NotificationEventService     - Emit POINT_EARNED, VOUCHER_RECEIVED, etc.
❌ ComplaintCompensationService - Tạo voucher từ complaint action
```

**Kết luận Services**: **60-70% logic cũ có thể tái sử dụng**, nhưng cần:
- Rewrite applyVoucher() để support reserve/release
- Thêm milestone service
- Thêm analytics service
- Thêm event/notification service

---

### 2.4 CONTROLLERS

#### Project Cũ
```
✅ PartnerPromotionController
  - POST /api/v1/partner/promotions/direct
  - POST /api/v1/partner/promotions/voucher
  - GET /api/v1/partner/promotions
  - PATCH /api/v1/partner/promotions/{id}/pause
  - PATCH /api/v1/partner/promotions/{id}/resume

✅ CustomerLoyaltyController
  - GET /api/v1/customers/me/loyalty
  - GET /api/v1/customers/me/loyalty/exchangeable-vouchers
  - POST /api/v1/customers/me/loyalty/exchange
```

#### Thiếu trong Module_4
```
❌ GET /api/v1/partner/promotions/{id}                    - Chi tiết campaign
❌ PUT /api/v1/partner/promotions/{id}                    - Sửa campaign
❌ DELETE /api/v1/partner/promotions/{id}                 - Soft delete
❌ GET /api/v1/partner/promotions/{id}/analytics          - Metrics campaign
❌ GET /api/v1/partner/promotions/{id}/analytics/daily    - Time-series
❌ GET /api/v1/user/loyalty/wallet/vouchers               - Danh sách vouchers
❌ GET /api/v1/user/loyalty/history                       - Lịch sử điểm chi tiết
❌ POST /api/v1/user/bookings/{id}/apply-voucher          - Apply voucher lúc checkout
❌ DELETE /api/v1/user/bookings/{id}/voucher              - Remove voucher
❌ GET /api/v1/user/bookings/{id}/voucher-preview         - Xem voucher available trước apply
```

**Kết luận Controllers**: **50-60% endpoints cũ có thể tái sử dụng**, cần thêm 8-10 endpoints mới.

---

### 2.5 BUSINESS LOGIC & VALIDATION

| Rule | Project Cũ | Module_4 Required | Assessment |
|------|-----------|------------------|-----------|
| **Voucher overlap validation** | ✅ Có `existsOverlappingPromotion()` | ✅ Cần | Tái sử dụng 100% |
| **Partner ownership check** | ✅ Có `requirePartnerBusinessProfileId()` | ✅ Cần | Tái sử dụng 100% |
| **Asset ownership** (hotel/room/menu) | ✅ Có `requireOwnedHotel()`, etc | ✅ Cần | Tái sử dụng 100% |
| **Voucher eligibility** | ✅ Có status/expired/min order check | ✅ Cần | Tái sử dụng 90% |
| **Point earning formula** | ✅ Có `accumulatePoints()` | ✅ Cần | Tái sử dụng 100% |
| **Tier calculation** | ✅ Có `resolveTier()` + thresholds | ✅ Cần | Tái sử dụng 100% |
| **Voucher reserve/release** | ❌ Không | ✅ Cần | Viết mới |
| **Idempotency key check** | ❌ Không | ✅ Cần | Viết mới |
| **Concurrent redeem lock** | ⚠️ @Version có nhưng chưa robust | ✅ Cần | Enhance |
| **Milestone tracking** | ❌ Không | ✅ Cần | Viết mới |
| **Analytics aggregation** | ❌ Không | ✅ Cần | Viết mới |

**Kết luận Logic**: **65-75% validation rules có thể tái sử dụng**, phần reserve/release/milestone/analytics viết mới.

---

### 2.6 INTEGRATION POINTS

#### Project Cũ - Booking Integration
```
✅ accumulatePoints() gọi sau booking success
❌ Apply voucher không lưu vào booking table
❌ Không có reserve/release workflow với payment callback
```

#### Module_4 - Booking Integration (Yêu cầu)
```
❌ POST /api/v1/user/bookings/{id}/apply-voucher   - Cần viết
❌ DELETE /api/v1/user/bookings/{id}/voucher       - Cần viết
✅ Payment success → consume voucher (logic có, cần integrate)
✅ Payment fail → release voucher (logic không có, cần viết)
```

#### Project Cũ - Complaint Integration
```
❌ Không có logic tạo voucher từ complaint compensation
```

#### Module_4 - Complaint Integration (Yêu cầu)
```
❌ Complaint action VOUCHER/DISCOUNT_CODE → customer_voucher
❌ Source_type = COMPLAINT_COMPENSATION
❌ Notification VOUCHER_RECEIVED
```

**Kết luận Integration**: **20-30% có thể tái sử dụng**, phần lớn cần viết mới.

---

## III. RECOMMENDATION SUMMARY

### ✅ CÓ THỂ COPY/REUSE 100% (Không cần modify):
1. **Entity base classes**: `UuDai`, `Voucher`, `KhuyenMaiTrucTiep`
2. **Entity fields**: Tất cả fields loyalty/voucher trong `KhachHang`, `LichSuDiem`
3. **Enums**: `TrangThaiUuDai`, `LoaiGiamGia`, `CreatedByRole`, `HangThanhVien`
4. **Core logic**:
   - Overlap validation: `existsOverlappingPromotion()`
   - Ownership checks: `requirePartnerBusinessProfileId()`, asset ownership methods
   - Point earning: `accumulatePoints()` formula
   - Tier calculation: `resolveTier()`, `resolveNextTier()`, `thresholdFor()`
5. **Controllers**: PartnerPromotionController base (pause/resume endpoints)
6. **Controllers**: CustomerLoyaltyController base (loyalty summary/exchange endpoints)

### ⚠️ CÓ THỂ REUSE VỚI ENHANCEMENT (~40-50% code):
1. **CustomerVoucher entity**: Add `reserved_at`, `reserve_expires_at`, `source_type` fields
2. **TrangThaiCustomerVoucher enum**: Add `RESERVED`, `CANCELLED` states
3. **LoaiGiaoDichDiem enum**: Add `POINT_REFUND`, `POINT_ADJUSTMENT`, `MILESTONE_REWARD`
4. **PromotionService.applyVoucher()**: Rewrite để support reserve + release + booking integration
5. **LoyaltyRuleService**: Review multiplier fields alignment
6. **Repositories**: Add queries cho new fields (reserved_at, source_type)

### ❌ CẦN VIẾT MỚI (~40-50% code):
1. **New Services**:
   - `VoucherReserveService` - Reserve/release lifecycle
   - `MilestoneRewardService` - Grant rewards (#5, #10, #20 bookings)
   - `PromotionAnalyticsService` - Track metrics
   - `NotificationEventService` - Emit events
   - `BookingVoucherService` - Checkout integration

2. **New Entities/Enums**:
   - `PromotionAnalyticsDaily` - Pre-aggregate metrics
   - `SourceType` enum
   - `PhamViApDung` enum
   - `TargetType` enum

3. **New Controllers**:
   - Endpoints for GET/PUT/DELETE/analytics promotions
   - Checkout voucher endpoints
   - Analytics endpoints

4. **New DTOs**:
   - Request/Response cho campaign edit, analytics
   - Voucher preview DTO
   - Milestone reward DTOs

5. **Database**: 
   - Add reserve fields (`customer_voucher`)
   - Add `source_type` column
   - Add `promotion_analytics_daily` table
   - Add indexes for performance

6. **Complaint Integration**:
   - Hook vào complaint compensation logic
   - Tạo voucher từ VOUCHER/DISCOUNT_CODE action

---

## IV. KIẾN TRÚC PROJECT CŨ - PHÁT HIỆN

### Điểm Tích Cực
✅ Clear separation of concerns (Entity → DTO → Service → Controller)
✅ Transaction management (@Transactional)
✅ Permission/ownership checks robust
✅ Logical schema design (JOINED inheritance)
✅ Good use of Spring Data JPA
✅ Enum-based state management

### Điểm Cần Cải Thiện
⚠️ Không có reserve/release pattern → khó integrate với payment callback
⚠️ Không có milestone tracking
⚠️ Không có analytics aggregation
⚠️ `applyVoucher()` return DTO thay vì update booking
⚠️ Không có idempotency key mechanism
⚠️ Redis integration có nhưng chưa dùng hiệu quả

---

## V. PHIÊN BẢN FINAL CẮT TỨC (TL;DR)

| Phần | Tái Sử Dụng | Effort |
|-----|-----------|--------|
| **Schema & Entities** | 70% | Thêm fields, enums |
| **Services** | 60% | Viết reserve/milestone/analytics |
| **Controllers** | 50% | Thêm endpoints, analytics |
| **Business Logic** | 70% | Booking integration, events |
| **Overall** | **60%** | **Copypaste 60%, viết mới 40%** |

### Khuyến Nghị Chiến Lược
1. **COPY trực tiếp**: Schema, base entities, basic services logic (validation, tier calculation)
2. **ENHANCE**: Thêm enums, fields, reserve/release lifecycle
3. **WRITE NEW**: Analytics, milestone, notification events, booking integration
4. **TIME**: Nhanh gấp 2-3x so với viết from scratch nhờ có foundation

---

## VI. ACTION ITEMS

- [ ] Copy migration SQL (uu_dai, voucher, khuyen_mai_truc_tiep, customer_voucher, etc)
- [ ] Copy entity classes (adapt với project hiện tại)
- [ ] Copy DTOs + repositories
- [ ] Copy validation logic từ PromotionService
- [ ] Copy tier calculation logic từ CustomerLoyaltyService
- [ ] Rewrite applyVoucher() cho reserve/release pattern
- [ ] Viết MilestoneRewardService
- [ ] Viết PromotionAnalyticsService
- [ ] Viết BookingVoucherService (checkout integration)
- [ ] Viết NotificationEventService
- [ ] Viết complaint compensation hook
- [ ] Test + QA full flow

