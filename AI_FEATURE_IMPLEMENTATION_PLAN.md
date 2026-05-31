# Ke hoach va trang thai trien khai chuc nang AI

## 1. Muc tieu

Trien khai nhom chuc nang AI cho TraVi theo so do lop `AI_Context`, khong dua luong admin vao pham vi hien tai. AI hien tai tap trung vao nhom khach hang:

- Khach hang: goi y khach san/nha hang dua tren ho so, so thich, tu khoa tim kiem va thanh pho dang quan tam.

## 2. Can cu thiet ke

Da doi chieu cac SVG so do lop nguoi dung cung cap. Phan AI co cac class chinh:

- `HoSoAI`
- `ThongBaoNguCanh`
- `AIEngine`
- `AIRecommendationService`
- `AIIntegrationController`
- `SmartRecommendationController`
- `ContextAwareController`
- `AIProfileManager`

Phan code hien tai uu tien hien thuc cac class du lieu co nhu cau luu tru DB:

- `HoSoAI`: luu ho so AI cua khach hang, lich su click, tu khoa tim kiem, so thich tong hop, trang thai ca nhan hoa.
- `ThongBaoNguCanh`: luu thong bao/canh bao ngu canh theo thiet ke class.

## 3. Backend da trien khai

### Entity va repository

Da them:

- `backend/src/main/java/com/ota/travi/entity/HoSoAI.java`
- `backend/src/main/java/com/ota/travi/entity/ThongBaoNguCanh.java`
- `backend/src/main/java/com/ota/travi/repository/HoSoAIRepository.java`
- `backend/src/main/java/com/ota/travi/repository/ThongBaoNguCanhRepository.java`

Da loai bo huong dat ten khong khop so do lop:

- `AiUserContext`
- `AiRecommendationLog`
- `AiPartnerInsight`

### Migration

Da them:

- `backend/src/main/resources/db/migration/V3__ai_context_schema.sql`

Bang duoc tao:

- `ho_so_ai`
- `ho_so_ai_lich_su_click`
- `ho_so_ai_tu_khoa_tim_kiem`
- `thong_bao_ngu_canh`

### API

Da them controller:

- `backend/src/main/java/com/ota/travi/controller/AiController.java`

Endpoint:

- `GET /api/v1/user/recommendations`
  - Query: `type=ALL|HOTEL|RESTAURANT`, `city`, `limit`
  - Tra ve goi y AI ca nhan hoa cho khach hang.

- `POST /api/v1/user/recommendations/feedback`
  - Body: `idTaiSan`, `viewed`, `clicked`, `booked`, `feedbackRating`, `feedbackNote`
  - Hien tai dung de cap nhat `HoSoAI.lichSuClick`.

### Service

Da them:

- `backend/src/main/java/com/ota/travi/service/AiRecommendationService.java`

Logic hien tai:

- Lay tin hieu tu `KhachHang.hangThanhVien`, `KhachHang.tuKhoaGanDay`, `KhachHang.danhSachSoThich`.
- Lay lich su don dat cho cua khach hang de bo sung tin hieu.
- Cham diem `KhachSan` va `NhaHang` theo:
  - Thanh pho dang quan tam.
  - Ten, mo ta, loai dich vu, dia chi khop tu khoa/so thich.
  - Hang sao khach san.
  - Gia co ban.
- Ho tro dat ban va suc chua nha hang.
- Cap nhat `HoSoAI` moi khi tao goi y.

## 4. Frontend da trien khai

### API client va hook

Da them module:

- `frontend/src/features/ai/types.ts`
- `frontend/src/features/ai/services/aiService.ts`
- `frontend/src/features/ai/hooks/useAiRecommendations.ts`
- `frontend/src/features/ai/index.ts`

### Trang chu khach hang

Da them component:

- `frontend/src/features/ai/components/UserAiRecommendations.tsx`

Da gan vao:

- `frontend/src/pages/HomePage.tsx`

Hanh vi:

- Chi hien thi khi user da dang nhap va role la `KHACH_HANG`.
- Goi API `GET /api/v1/user/recommendations`.
- Hien thi danh sach goi y khach san/nha hang.
- Khi click goi y, gui feedback best-effort qua `POST /api/v1/user/recommendations/feedback`.

### Dashboard doi tac

Da hoan lai cac thay doi AI cho dashboard doi tac theo yeu cau moi:

- Khong con route frontend dung dashboard AI rieng cho `/partner`.
- Khong con hook `usePartnerAiInsights`.
- Khong con API `/api/v1/partner/ai-insights`.
- Route `/partner` quay ve dashboard doi tac placeholder trong `App.tsx`.

## 5. Loi phu da xu ly

Trong log Docker co loi runtime cua booking module:

- `operator does not exist: character varying = bigint`

Nguyen nhan:

- Booking module dung id `Long`.
- Bang core `don_dat_cho` dung id UUID/String.
- Hibernate join nham bang core voi bang booking module.

Da xu ly:

- Doi booking module parent table sang `booking_don_dat_cho`.
- Them migration:
  - `backend/src/main/resources/db/migration/V4__booking_module_tables.sql`

## 6. Trang thai kiem thu

Da chay backend compile:

```powershell
docker compose exec -T backend ./mvnw compile
```

Ket qua:

- `BUILD SUCCESS`

Da chay frontend build:

```powershell
npm.cmd run build
```

Tai thu muc:

```text
frontend
```

Ket qua:

- `tsc -b` pass.
- `vite build` pass.

## 7. Viec con lai de hoan thien hon

Nhung viec nay chua bat buoc de demo hien tai, nhung nen lam neu tiep tuc phat trien:

- Bo sung UI quan ly `ThongBaoNguCanh` neu can hien thi canh bao thoi tiet/su kien dia phuong.
- Tach `AIEngine` thanh interface/service rieng neu muon thay rule-based bang OpenAI hoac model ngoai.
- Them dynamic pricing theo `AIIntegrationController`.
- Viet unit test cho `AiRecommendationService`.
- Bo sung contract vao README backend neu day len sprint tiep theo.

## 8. Cap nhat bo sung customer AI ngay 31/05/2026

Sau khi doi chieu lai `AI_TongQuan.md`, da bo sung cac phan con thieu cho customer AI theo huong MVP du demo, khong dung module admin va khong them entity khuyen mai moi khi du an chua co class khuyen mai rieng.

### 8.1. AI Profile hoc tu hanh vi dong

Da bo sung endpoint:

- `POST /api/v1/user/ai-events`

Request:

- `eventType`: `SEARCH`, `VIEW`, `CLICK`, `BOOK`, `REVIEW`, `PROMOTION_VIEW`
- `assetType`: `HOTEL`, `RESTAURANT`, `PLACE`
- `assetId`
- `keyword`
- `city`
- `source`
- `metadata`

Da bo sung DTO:

- `backend/src/main/java/com/ota/travi/dto/request/AiUserEventRequest.java`

Da bo sung bang trong migration:

- `ho_so_ai_lich_su_tim_kiem`
- `ho_so_ai_hanh_vi`

Luu y: project hien tai chua tu dong chay Flyway, nen da apply schema truc tiep vao Postgres container bang `psql`.

### 8.2. Search History

Da gan tracking search vao frontend:

- `frontend/src/features/hotels/hooks/useHotelSearch.ts`
- `frontend/src/features/restaurants/hooks/useRestaurantSearch.ts`

Khi khach hang da dang nhap va tim kiem khach san/nha hang, frontend gui event `SEARCH` ve backend de AI Profile ghi:

- tu khoa
- thanh pho
- loai dich vu
- nguon tim kiem
- metadata nhu ngay, so khach, so ket qua

Tracking nay la best-effort, loi AI tracking khong lam hong luong search catalog.

### 8.3. Click/View/Booking/Review/Promotion Interaction

Da nang cap `AiRecommendationService`:

- `captureFeedback(...)` khong chi luu click cu, ma con ghi them event vao `ho_so_ai_hanh_vi`.
- Ho tro cac tin hieu:
  - click goi y
  - view/click asset
  - booked
  - review/rating/note
  - promotion view

Frontend hien tai da co click feedback tu component goi y. Cac man hinh review thuc te neu duoc lam sau chi can goi chung endpoint `/api/v1/user/ai-events` hoac `/api/v1/user/recommendations/feedback`.

### 8.4. Hybrid Recommendation MVP

Recommendation da duoc nang tu rule-based don gian thanh hybrid-lite:

- Content-based:
  - so thich
  - tu khoa gan day
  - search history
  - thanh pho dang quan tam
  - loai khach san/am thuc/mo ta/dia chi

- Behavior-based:
  - clicked asset
  - viewed asset
  - booking history
  - event behavior trong `ho_so_ai_hanh_vi`

- Popular/promotion fallback:
  - khach san 4-5 sao
  - nha hang co dat ban truoc
  - phong co `phanTramGiamGia`

Chua phai collaborative filtering dung nghia vi chua co ma tran user-item du lon, nhung da co du tin hieu de demo flow AI Profile -> Recommendation.

### 8.5. Promotion Recommendation

Du an chua co entity khuyen mai rieng, nen promotion recommendation dang dung du lieu that co san:

- `Phong.phanTramGiamGia`

Neu khach san co phong dang giam gia, recommendation tang diem va them ly do:

- `Co uu dai phong dang ap dung`

Smart Notification cung co thong bao `PROMOTION` dua tren goi y giam gia phu hop nhat.

### 8.6. Smart Notification mo rong

`SmartNotificationService` da co them:

- `RECOMMENDATION`: nhac goi y phu hop neu score cao.
- `PROMOTION`: nhac uu dai luu tru phu hop.

Van giu cac thong bao cu:

- `BOOKING_REMINDER`
- `WEATHER_WARNING`
- thong bao he thong tu `thong_bao_ngu_canh`

### 8.7. Trang thai sau cap nhat

Da chay backend:

```powershell
docker compose exec -T backend ./mvnw compile
```

Ket qua:

- `BUILD SUCCESS`

Da chay frontend:

```powershell
npm.cmd run build
```

Tai thu muc:

```text
frontend
```

Ket qua:

- `tsc -b` pass
- `vite build` pass

Da restart backend container:

```powershell
docker compose restart backend
```

## 9. Customer AI da lam va con lai

Da lam:

- AI User Profile
- Preference setup cho cold start
- Search History tracking
- Click/View/Booking/Review/Promotion event tracking
- Hotel/Restaurant Recommendation
- Promotion Recommendation bang `phanTramGiamGia`
- Travel Planner theo ngay
- Smart Notification: booking, weather, recommendation, promotion

## 10. Cap nhat nang cao ngay 31/05/2026

Theo yeu cau moi, cac muc "con lai" da duoc day len thanh code that trong du an.

### 10.1. Collaborative Filtering that

Da them:

- `backend/src/main/java/com/ota/travi/service/CollaborativeFilteringService.java`

Nguon du lieu thuc te:

- `ho_so_ai_hanh_vi`
- `ho_so_ai_lich_su_click`
- `don_dat_cho`
- `danh_gia`

Cach cham diem:

- Lay cac tai san user hien tai da xem/click/book/review.
- Tim cac `HoSoAI` cua user khac cung tuong tac voi cac tai san do.
- Cong diem cho tai san ma nhom user tuong tu da quan tam, loai tru tai san user hien tai da tuong tac.
- Diem collaborative duoc dua vao `AiRecommendationService` de tang score hotel/restaurant.

### 10.2. LLM that cho Travel Planner

Da them:

- `backend/src/main/java/com/ota/travi/service/AiTravelPlannerLlmService.java`

Luong xu ly:

- `TravelPlannerService` van lay recommendation that tu scoring engine truoc.
- Neu `OPENAI_API_KEY` hop le, service goi Spring AI `ChatClient` de sinh lich trinh JSON.
- Neu chua co key that hoac LLM loi, tu dong fallback ve rule-based planner cu de UI khong bi hong.

Can cau hinh them:

- Bien moi truong `OPENAI_API_KEY` phai la key that. Hien `.env` dang de placeholder `sk-your-real-openai-api-key-here`, service se khong dam bao goi LLM thanh cong neu key nay chua doi.

### 10.3. Module khuyen mai rieng

Da them:

- `backend/src/main/java/com/ota/travi/entity/KhuyenMai.java`
- `backend/src/main/java/com/ota/travi/repository/KhuyenMaiRepository.java`
- `backend/src/main/java/com/ota/travi/service/PromotionService.java`
- `backend/src/main/java/com/ota/travi/controller/PromotionController.java`
- `backend/src/main/java/com/ota/travi/dto/response/PromotionResponse.java`
- `backend/src/main/resources/db/migration/V6__ai_reviews_promotions.sql`

Endpoint:

- `GET /api/v1/public/promotions/active?assetId=&type=`

Recommendation engine da doc `KhuyenMai` dang active va cong diem neu hotel/restaurant co khuyen mai phu hop.

### 10.4. Review UI/API day du

Da them backend:

- `backend/src/main/java/com/ota/travi/entity/DanhGia.java`
- `backend/src/main/java/com/ota/travi/repository/DanhGiaRepository.java`
- `backend/src/main/java/com/ota/travi/service/ReviewService.java`
- `backend/src/main/java/com/ota/travi/controller/ReviewController.java`
- `backend/src/main/java/com/ota/travi/dto/request/CreateReviewRequest.java`
- `backend/src/main/java/com/ota/travi/dto/response/ReviewResponse.java`

Endpoint:

- `GET /api/v1/public/assets/{assetId}/reviews`
- `POST /api/v1/user/reviews`

Da gan rating/count that vao catalog/detail:

- `PublicCatalogService` tinh `diemDanhGiaTrungBinh`
- `PublicCatalogService` tinh `soLuongDanhGia`

Da them frontend:

- `frontend/src/features/reviews/types.ts`
- `frontend/src/features/reviews/services/reviewService.ts`
- `frontend/src/features/reviews/components/ReviewPanel.tsx`

Da gan UI vao:

- `frontend/src/features/hotels/pages/HotelDetailPage.tsx`
- `frontend/src/features/restaurants/pages/RestaurantDetailPage.tsx`

Khi khach gui review, backend dong thoi ghi event `REVIEW` vao AI Profile de Collaborative Filtering co them tin hieu.

### 10.5. Unit test rieng cho scoring AI

Da them:

- `backend/src/main/java/com/ota/travi/service/AiScoreCalculator.java`
- `backend/src/test/java/com/ota/travi/service/AiScoreCalculatorTest.java`

Test bao phu:

- Collaborative boost va gioi han diem.
- Behavior score voi click/book va collaborative signal.
- Promotion score theo phan tram/so tien giam.

Lenh da chay:

```powershell
docker compose exec -T backend ./mvnw test -Dtest=AiScoreCalculatorTest
```

Ket qua:

- `Tests run: 3, Failures: 0, Errors: 0`

### 10.6. Trang thai kiem thu moi nhat

Da chay:

```powershell
docker compose exec -T backend ./mvnw compile
npm.cmd run build
curl.exe -s -o NUL -w "%{http_code}" http://localhost:8080/api/v1/public/promotions/active
curl.exe -s -o NUL -w "%{http_code}" http://localhost:8080/api/v1/public/assets/test/reviews
```

Ket qua:

- Backend compile: `BUILD SUCCESS`
- Frontend build: `vite build` pass
- Public promotions API: `200`
- Public reviews API: `200`
- Backend container dang `Up` tren cong `8080`.

Luu y van can:

- Doi `OPENAI_API_KEY` trong `.env` thanh key that de LLM planner goi model that.
- Tao du lieu `khuyen_mai` mau neu muon thay banner/score khuyen mai ro trong demo.
