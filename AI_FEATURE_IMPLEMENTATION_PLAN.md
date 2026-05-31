# Kế hoạch và trạng thái triển khai chức năng AI

## 1. Mục tiêu

Triển khai nhóm chức năng AI cho TraVi theo sơ đồ lớp `AI_Context`, không đưa luồng admin vào phạm vi hiện tại. AI hiện tại tập trung vào nhóm khách hàng:

- Khách hàng: gợi ý khách sạn/nhà hàng dựa trên hồ sơ, sở thích, từ khóa tìm kiếm và thành phố đang quan tâm.

## 2. Căn cứ thiết kế

Đã đối chiếu các SVG sơ đồ lớp người dùng cung cấp. Phần AI có các class chính:

- `HoSoAI`
- `ThongBaoNguCanh`
- `AIEngine`
- `AIRecommendationService`
- `AIIntegrationController`
- `SmartRecommendationController`
- `ContextAwareController`
- `AIProfileManager`

Phần code hiện tại ưu tiên hiện thực các class dữ liệu có nhu cầu lưu trữ DB:

- `HoSoAI`: lưu hồ sơ AI của khách hàng, lịch sử click, từ khóa tìm kiếm, sở thích tổng hợp, trạng thái cá nhân hóa.
- `ThongBaoNguCanh`: lưu thông báo/cảnh báo ngữ cảnh theo thiết kế class.

## 3. Backend đã triển khai

### Entity và repository

Đã thêm:

- `backend/src/main/java/com/ota/travi/entity/HoSoAI.java`
- `backend/src/main/java/com/ota/travi/entity/ThongBaoNguCanh.java`
- `backend/src/main/java/com/ota/travi/repository/HoSoAIRepository.java`
- `backend/src/main/java/com/ota/travi/repository/ThongBaoNguCanhRepository.java`

Đã loại bỏ hướng đặt tên không khớp sơ đồ lớp:

- `AiUserContext`
- `AiRecommendationLog`
- `AiPartnerInsight`

### Migration

Đã thêm:

- `backend/src/main/resources/db/migration/V3__ai_context_schema.sql`

Bảng được tạo:

- `ho_so_ai`
- `ho_so_ai_lich_su_click`
- `ho_so_ai_tu_khoa_tim_kiem`
- `thong_bao_ngu_canh`

### API

Đã thêm controller:

- `backend/src/main/java/com/ota/travi/controller/AiController.java`

Endpoint:

- `GET /api/v1/user/recommendations`
  - Query: `type=ALL|HOTEL|RESTAURANT`, `city`, `limit`
  - Trả về gợi ý AI cá nhân hóa cho khách hàng.

- `POST /api/v1/user/recommendations/feedback`
  - Body: `idTaiSan`, `viewed`, `clicked`, `booked`, `feedbackRating`, `feedbackNote`
  - Hiện tại dùng để cập nhật `HoSoAI.lichSuClick`.

### Service

Đã thêm:

- `backend/src/main/java/com/ota/travi/service/AiRecommendationService.java`

Logic hiện tại:

- Lấy tín hiệu từ `KhachHang.hangThanhVien`, `KhachHang.tuKhoaGanDay`, `KhachHang.danhSachSoThich`.
- Lấy lịch sử đơn đặt chỗ của khách hàng để bổ sung tín hiệu.
- Chấm điểm `KhachSan` và `NhaHang` theo:
  - Thành phố đang quan tâm.
  - Tên, mô tả, loại dịch vụ, địa chỉ khớp từ khóa/sở thích.
  - Hạng sao khách sạn.
  - Giá cơ bản.
- Hỗ trợ đặt bàn và sức chứa nhà hàng.
- Cập nhật `HoSoAI` mỗi khi tạo gợi ý.

## 4. Frontend đã triển khai

### API client và hook

Đã thêm module:

- `frontend/src/features/ai/types.ts`
- `frontend/src/features/ai/services/aiService.ts`
- `frontend/src/features/ai/hooks/useAiRecommendations.ts`
- `frontend/src/features/ai/index.ts`

### Trang chủ khách hàng

Đã thêm component:

- `frontend/src/features/ai/components/UserAiRecommendations.tsx`

Đã gắn vào:

- `frontend/src/pages/HomePage.tsx`

Hành vi:

- Chỉ hiển thị khi user đã đăng nhập và role là `KHACH_HANG`.
- Gọi API `GET /api/v1/user/recommendations`.
- Hiển thị danh sách gợi ý khách sạn/nhà hàng.
- Khi click gợi ý, gửi feedback best-effort qua `POST /api/v1/user/recommendations/feedback`.

### Dashboard đối tác

Đã hoàn lại các thay đổi AI cho dashboard đối tác theo yêu cầu mới:

- Không còn route frontend dùng dashboard AI riêng cho `/partner`.
- Không còn hook `usePartnerAiInsights`.
- Không còn API `/api/v1/partner/ai-insights`.
- Route `/partner` quay về dashboard đối tác placeholder trong `App.tsx`.

## 5. Lỗi phụ đã xử lý

Trong log Docker có lỗi runtime của booking module:

- `operator does not exist: character varying = bigint`

Nguyên nhân:

- Booking module dùng id `Long`.
- Bảng core `don_dat_cho` dùng id UUID/String.
- Hibernate join nhầm bảng core với bảng booking module.

Đã xử lý:

- Đổi booking module parent table sang `booking_don_dat_cho`.
- Thêm migration:
  - `backend/src/main/resources/db/migration/V4__booking_module_tables.sql`

## 6. Trạng thái kiểm thử

Đã chạy backend compile:

```powershell
docker compose exec -T backend ./mvnw compile
```

Kết quả:

- `BUILD SUCCESS`

Đã chạy frontend build:

```powershell
npm.cmd run build
```

Tại thư mục:

```text
frontend
```

Kết quả:

- `tsc -b` pass.
- `vite build` pass.

## 7. Việc còn lại để hoàn thiện hơn

Những việc này chưa bắt buộc để demo hiện tại, nhưng nên làm nếu tiếp tục phát triển:

- Bổ sung UI quản lý `ThongBaoNguCanh` nếu cần hiển thị cảnh báo thời tiết/sự kiện địa phương.
- Tách `AIEngine` thành interface/service riêng nếu muốn thay rule-based bằng OpenAI hoặc model ngoài.
- Thêm dynamic pricing theo `AIIntegrationController`.
- Viết unit test cho `AiRecommendationService`.
- Bổ sung contract vào README backend nếu đẩy lên sprint tiếp theo.

## 8. Cập nhật bổ sung customer AI ngày 31/05/2026

Sau khi đối chiếu lại `AI_TongQuan.md`, đã bổ sung các phần còn thiếu cho customer AI theo hướng MVP đủ demo, không dùng module admin và không thêm entity khuyến mãi mới khi dự án chưa có class khuyến mãi riêng.

### 8.1. AI Profile học từ hành vi động

Đã bổ sung endpoint:

- `POST /api/v1/user/ai-events`

Request:

- `eventType`: `SEARCH`, `VIEW`, `CLICK`, `BOOK`, `REVIEW`, `PROMOTION_VIEW`
- `assetType`: `HOTEL`, `RESTAURANT`, `PLACE`
- `assetId`
- `keyword`
- `city`
- `source`
- `metadata`

Đã bổ sung DTO:

- `backend/src/main/java/com/ota/travi/dto/request/AiUserEventRequest.java`

Đã bổ sung bảng trong migration:

- `ho_so_ai_lich_su_tim_kiem`
- `ho_so_ai_hanh_vi`

Lưu ý: project hiện tại chưa tự động chạy Flyway, nên đã apply schema trực tiếp vào Postgres container bằng `psql`.

### 8.2. Search History

Đã gắn tracking search vào frontend:

- `frontend/src/features/hotels/hooks/useHotelSearch.ts`
- `frontend/src/features/restaurants/hooks/useRestaurantSearch.ts`

Khi khách hàng đã đăng nhập và tìm kiếm khách sạn/nhà hàng, frontend gửi event `SEARCH` về backend để AI Profile ghi:

- từ khóa
- thành phố
- loại dịch vụ
- nguồn tìm kiếm
- metadata như ngày, số khách, số kết quả

Tracking này là best-effort, lỗi AI tracking không làm hỏng luồng search catalog.

### 8.3. Click/View/Booking/Review/Promotion Interaction

Đã nâng cấp `AiRecommendationService`:

- `captureFeedback(...)` không chỉ lưu click cũ, mà còn ghi thêm event vào `ho_so_ai_hanh_vi`.
- Hỗ trợ các tín hiệu:
  - click gợi ý
  - view/click asset
  - booked
  - review/rating/note
  - promotion view

Frontend hiện tại đã có click feedback từ component gợi ý. Các màn hình review thực tế nếu được làm sau chỉ cần gọi chung endpoint `/api/v1/user/ai-events` hoặc `/api/v1/user/recommendations/feedback`.

### 8.4. Hybrid Recommendation MVP

Recommendation đã được nâng từ rule-based đơn giản thành hybrid-lite:

- Content-based:
  - sở thích
  - từ khóa gần đây
  - search history
  - thành phố đang quan tâm
  - loại khách sạn/ẩm thực/mô tả/địa chỉ

- Behavior-based:
  - clicked asset
  - viewed asset
  - booking history
  - event behavior trong `ho_so_ai_hanh_vi`

- Popular/promotion fallback:
  - khách sạn 4-5 sao
  - nhà hàng có đặt bàn trước
  - phòng có `phanTramGiamGia`

Chưa phải collaborative filtering đúng nghĩa vì chưa có ma trận user-item đủ lớn, nhưng đã có đủ tín hiệu để demo flow AI Profile -> Recommendation.

### 8.5. Promotion Recommendation

Dự án chưa có entity khuyến mãi riêng, nên promotion recommendation đang dùng dữ liệu thật có sẵn:

- `Phong.phanTramGiamGia`

Nếu khách sạn có phòng đang giảm giá, recommendation tăng điểm và thêm lý do:

- `Có ưu đãi phòng đang áp dụng`

Smart Notification cũng có thông báo `PROMOTION` dựa trên gợi ý giảm giá phù hợp nhất.

### 8.6. Smart Notification mở rộng

`SmartNotificationService` đã có thêm:

- `RECOMMENDATION`: nhắc gợi ý phù hợp nếu score cao.
- `PROMOTION`: nhắc ưu đãi lưu trú phù hợp.

Vẫn giữ các thông báo cũ:

- `BOOKING_REMINDER`
- `WEATHER_WARNING`
- thông báo hệ thống từ `thong_bao_ngu_canh`

### 8.7. Trạng thái sau cập nhật

Đã chạy backend:

```powershell
docker compose exec -T backend ./mvnw compile
```

Kết quả:

- `BUILD SUCCESS`

Đã chạy frontend:

```powershell
npm.cmd run build
```

Tại thư mục:

```text
frontend
```

Kết quả:

- `tsc -b` pass
- `vite build` pass

Đã restart backend container:

```powershell
docker compose restart backend
```

## 9. Customer AI đã làm và còn lại

Đã làm:

- AI User Profile
- Preference setup cho cold start
- Search History tracking
- Click/View/Booking/Review/Promotion event tracking
- Hotel/Restaurant Recommendation
- Promotion Recommendation bằng `phanTramGiamGia`
- Travel Planner theo ngày
- Smart Notification: booking, weather, recommendation, promotion

## 10. Cập nhật nâng cao ngày 31/05/2026

Theo yêu cầu mới, các mục "còn lại" đã được đẩy lên thành code thật trong dự án.

### 10.1. Collaborative Filtering thật

Đã thêm:

- `backend/src/main/java/com/ota/travi/service/CollaborativeFilteringService.java`

Nguồn dữ liệu thực tế:

- `ho_so_ai_hanh_vi`
- `ho_so_ai_lich_su_click`
- `don_dat_cho`
- `danh_gia`

Cách chấm điểm:

- Lấy các tài sản user hiện tại đã xem/click/book/review.
- Tìm các `HoSoAI` của user khác cùng tương tác với các tài sản đó.
- Cộng điểm cho tài sản mà nhóm user tương tự đã quan tâm, loại trừ tài sản user hiện tại đã tương tác.
- Điểm collaborative được đưa vào `AiRecommendationService` để tăng score hotel/restaurant.

### 10.2. LLM thật cho Travel Planner

Đã thêm:

- `backend/src/main/java/com/ota/travi/service/AiTravelPlannerLlmService.java`

Luồng xử lý:

- `TravelPlannerService` vẫn lấy recommendation thật từ scoring engine trước.
- Nếu `OPENAI_API_KEY` hợp lệ, service gọi Spring AI `ChatClient` để sinh lịch trình JSON.
- Nếu chưa có key thật hoặc LLM lỗi, tự động fallback về rule-based planner cũ để UI không bị hỏng.

Cần cấu hình thêm:

- Biến môi trường `OPENAI_API_KEY` phải là key thật. Hiện `.env` đang để placeholder `sk-your-real-openai-api-key-here`, service sẽ không đảm bảo gọi LLM thành công nếu key này chưa đổi.

### 10.3. Module khuyến mãi riêng

Đã thêm:

- `backend/src/main/java/com/ota/travi/entity/KhuyenMai.java`
- `backend/src/main/java/com/ota/travi/repository/KhuyenMaiRepository.java`
- `backend/src/main/java/com/ota/travi/service/PromotionService.java`
- `backend/src/main/java/com/ota/travi/controller/PromotionController.java`
- `backend/src/main/java/com/ota/travi/dto/response/PromotionResponse.java`
- `backend/src/main/resources/db/migration/V6__ai_reviews_promotions.sql`

Endpoint:

- `GET /api/v1/public/promotions/active?assetId=&type=`

Recommendation engine đã đọc `KhuyenMai` đang active và cộng điểm nếu hotel/restaurant có khuyến mãi phù hợp.

### 10.4. Review UI/API đầy đủ

Đã thêm backend:

- `backend/src/main/java/com/ota/travi/entity/DanhGia.java`
- `backend/src/main/java/com/ota/travi/repository/DanhGiaRepository.java`
- `backend/src/main/java/com/ota/travi/service/ReviewService.java`
- `backend/src/main/java/com/ota/travi/controller/ReviewController.java`
- `backend/src/main/java/com/ota/travi/dto/request/CreateReviewRequest.java`
- `backend/src/main/java/com/ota/travi/dto/response/ReviewResponse.java`

Endpoint:

- `GET /api/v1/public/assets/{assetId}/reviews`
- `POST /api/v1/user/reviews`

Đã gắn rating/count thật vào catalog/detail:

- `PublicCatalogService` tính `diemDanhGiaTrungBinh`
- `PublicCatalogService` tính `soLuongDanhGia`

Đã thêm frontend:

- `frontend/src/features/reviews/types.ts`
- `frontend/src/features/reviews/services/reviewService.ts`
- `frontend/src/features/reviews/components/ReviewPanel.tsx`

Đã gắn UI vào:

- `frontend/src/features/hotels/pages/HotelDetailPage.tsx`
- `frontend/src/features/restaurants/pages/RestaurantDetailPage.tsx`

Khi khách gửi review, backend đồng thời ghi event `REVIEW` vào AI Profile để Collaborative Filtering có thêm tín hiệu.

### 10.5. Unit test riêng cho scoring AI

Đã thêm:

- `backend/src/main/java/com/ota/travi/service/AiScoreCalculator.java`
- `backend/src/test/java/com/ota/travi/service/AiScoreCalculatorTest.java`

Test bao phủ:

- Collaborative boost và giới hạn điểm.
- Behavior score với click/book và collaborative signal.
- Promotion score theo phần trăm/số tiền giảm.

Lệnh đã chạy:

```powershell
docker compose exec -T backend ./mvnw test -Dtest=AiScoreCalculatorTest
```

Kết quả:

- `Tests run: 3, Failures: 0, Errors: 0`

### 10.6. Trạng thái kiểm thử mới nhất

Đã chạy:

```powershell
docker compose exec -T backend ./mvnw compile
npm.cmd run build
curl.exe -s -o NUL -w "%{http_code}" http://localhost:8080/api/v1/public/promotions/active
curl.exe -s -o NUL -w "%{http_code}" http://localhost:8080/api/v1/public/assets/test/reviews
```

Kết quả:

- Backend compile: `BUILD SUCCESS`
- Frontend build: `vite build` pass
- Public promotions API: `200`
- Public reviews API: `200`
- Backend container đang `Up` trên cổng `8080`.

Lưu ý vẫn cần:

- Đổi `OPENAI_API_KEY` trong `.env` thành key thật để LLM planner gọi model thật.
- Tạo dữ liệu `khuyen_mai` mẫu nếu muốn thấy banner/score khuyến mãi rõ trong demo.

## 11. Nâng cấp recommendation lên mô hình Two-Tower

Sau nhận xét recommendation cũ còn nặng tính rule-based, đã bổ sung lớp model `TwoTowerRecommendationModel`. Mục tiêu là để điểm gợi ý chính đến từ độ tương đồng vector giữa người dùng và dịch vụ, thay vì chỉ cộng điểm thủ công.

### 11.1. Cách hoạt động

Model chia recommendation thành 2 tower:

- `User Tower`: biến thông tin khách hàng, thành phố đang quan tâm, loại dịch vụ, sở thích, từ khóa tìm kiếm, lịch sử click/đặt chỗ và collaborative signal thành user embedding.
- `Item Tower`: biến thông tin khách sạn/nhà hàng, thành phố, quận huyện, loại dịch vụ, mô tả, giá, chất lượng và tiện ích thành item embedding.
- `Dot product`: tính tích vô hướng giữa user embedding và item embedding.
- `Sigmoid`: chuyển dot product thành xác suất người dùng có khả năng chọn dịch vụ.
- `Score`: chuyển xác suất thành điểm 0-100 để frontend tiếp tục hiển thị như hiện tại.

### 11.2. Dữ liệu mô phỏng khi chưa có dataset lớn

Chưa cần thêm bảng mới. Model hiện dùng feature hashing để tạo embedding từ dữ liệu sẵn có:

- Mỗi feature text/context được hash vào vector 32 chiều.
- Nếu user và item có đặc trưng gần nhau, dot product tăng.
- Lịch sử click/đặt chỗ và collaborative signal được đưa vào embedding như tín hiệu học từ hành vi.
- Context thời tiết được suy luận từ thành phố và mùa hiện tại, ví dụ Đà Lạt/Sapa là `COOL`, Phú Quốc/Đà Nẵng/Nha Trang là `SUNNY`, mùa 5-10 là `RAINY`.

Đây là phiên bản phục vụ/ranking theo hướng AI: có embedding, có dot product, có xác suất. Khi có dataset thật, có thể thay feature hashing bằng model huấn luyện bằng Python mà không cần đổi contract API.

### 11.3. File đã thêm/sửa

- `backend/src/main/java/com/ota/travi/service/TwoTowerRecommendationModel.java`
- `backend/src/main/java/com/ota/travi/service/AiRecommendationService.java`
- `backend/src/test/java/com/ota/travi/service/TwoTowerRecommendationModelTest.java`

### 11.4. Vai trò của rule-based sau khi nâng cấp

Rule-based không còn là cách chấm điểm chính. Điểm chính đến từ Two-Tower:

```text
score = sigmoid(dot(userEmbedding, itemEmbedding)) * 100
```

Phần rule còn lại chỉ là boost nhỏ để giải thích và giữ hành vi sản phẩm ổn định:

- boost theo thành phố sau Two-Tower
- boost theo từ khóa để bổ trợ embedding
- boost nhỏ theo chất lượng, ưu đãi, booking và click

### 11.5. Test mới

Đã thêm test riêng cho Two-Tower:

- Item khớp user/context phải có dot product và score cao hơn item không liên quan.
- Collaborative signal phải tạo lý do gợi ý và tăng khả năng xếp hạng cho item liên quan.
