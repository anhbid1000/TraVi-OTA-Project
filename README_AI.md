# HỆ THỐNG TRÍ TUỆ NHÂN TẠO (AI) TRONG TRAVI OTA

## 1. Tổng quan
Hệ thống AI của TraVi được xây dựng với mục tiêu cá nhân hóa trải nghiệm khách hàng trong quá trình tìm kiếm, lựa chọn dịch vụ và lên kế hoạch du lịch. AI trong TraVi không chỉ là một chatbot hay một API gọi model đơn lẻ, mà là một tập hợp nhiều lớp xử lý bao gồm:

- Thu thập hành vi người dùng
- Xây dựng hồ sơ AI cá nhân hóa
- Gợi ý khách sạn/nhà hàng theo mô hình hybrid
- Gợi ý lịch trình du lịch theo ngày
- Gửi thông báo thông minh theo ngữ cảnh
- Kết hợp rule-based với LLM để tăng độ linh hoạt

Điểm quan trọng là hệ thống AI này bám sát dữ liệu thật trong database của dự án. Điều đó có nghĩa là AI không tự “bịa” ra khách sạn hay nhà hàng mới, mà chỉ phân tích, chấm điểm và sắp xếp các tài sản đã có sẵn trong hệ thống.

## 2. Mục tiêu của chức năng AI
Các chức năng AI cho nhóm khách hàng trong TraVi tập trung vào các mục tiêu sau:

- Hiểu sở thích và hành vi của khách hàng
- Gợi ý tài sản phù hợp hơn thay vì chỉ hiển thị danh sách tĩnh
- Tăng khả năng chuyển đổi đặt phòng / đặt bàn
- Hỗ trợ khách hàng lên lịch trình nhanh hơn
- Tạo thêm trải nghiệm “thông minh” qua thông báo thời tiết, nhắc lịch, ưu đãi phù hợp

### 2.1. Tổng quan các chức năng AI đã triển khai 
Dưới đây là danh sách tổng quan các chức năng AI đã hoàn thiện trong dự án (theo đúng phạm vi khách hàng, không phụ thuộc luồng admin):

- AI User Profile (xây dựng hồ sơ AI cho từng khách hàng)
- Preference setup cho cold-start (đọc sở thích ban đầu để gợi ý khi user mới)
- Search History Tracking (ghi nhận lịch sử tìm kiếm)
- Event Tracking đa tín hiệu: VIEW, CLICK, BOOK, REVIEW, PROMOTION_VIEW, SEARCH
- Recommendation cho Khách sạn và Nhà hàng
- Hybrid Scoring Engine: content-based + behavior-based + collaborative signal + promotion boost
- Collaborative Filtering mức MVP trên dữ liệu tương tác thật trong hệ thống
- Promotion Recommendation từ dữ liệu khuyến mãi active
- Travel Planner theo ngày (sáng/trưa/chiều/tối)
- Travel Planner LLM qua Spring AI + fallback rule-based khi lỗi hoặc thiếu API key
- Smart Notification: booking reminder, weather warning, recommendation, promotion
- Review UI/API đầy đủ và ghi nhận REVIEW signal về AI Profile
- Hard Filter theo thành phố cho Travel Planner để đảm bảo nhất quán điểm đến

Ghi chú:
- Recommendation engine chỉ gợi ý trên dữ liệu tài sản có thật trong database.
- Nếu một thành phố chưa có dữ liệu tài sản, planner sẽ không “bịa” địa điểm ngoài DB.

## 3. Kiến trúc tổng quát của hệ thống AI
Hệ thống được chia thành 4 lớp chính:

### 3.1. Lớp dữ liệu
Chịu trách nhiệm lưu hồ sơ AI, hành vi, lịch sử tìm kiếm, đánh giá, khuyến mãi và dữ liệu tài sản.

### 3.2. Lớp xử lý AI
Bao gồm các service dùng để:

- Tạo gợi ý
- Chấm điểm tài sản
- Tính collaborative filtering
- Sinh lịch trình du lịch
- Sinh thông báo thông minh

### 3.3. Lớp API
Cung cấp endpoint cho frontend gửi event, lấy recommendation, lấy itinerary, lấy notification, gửi feedback.

### 3.4. Lớp giao diện người dùng
Hiển thị recommendation, review, travel planner và các phần AI trên frontend.

## 4. Danh sách các file chính và nhiệm vụ của từng file
Phần này nêu rõ các file code quan trọng của chức năng AI và vai trò của từng file trong hệ thống.

### 4.1. Backend - API và điều phối

1. `backend/src/main/java/com/ota/travi/controller/AiController.java`
- Nhiệm vụ:
  - Cung cấp API lấy gợi ý AI cho khách hàng
  - Nhận feedback khi người dùng click / tương tác với gợi ý
  - Nhận các event AI như search, view, click, book, review, promotion_view
- Endpoint chính:
  - `GET /api/v1/user/recommendations`
  - `POST /api/v1/user/recommendations/feedback`
  - `POST /api/v1/user/ai-events`

2. `backend/src/main/java/com/ota/travi/controller/PromotionController.java`
- Nhiệm vụ:
  - Trả danh sách khuyến mãi đang active cho tài sản
  - Hỗ trợ recommendation engine đọc ưu đãi thật từ database

3. `backend/src/main/java/com/ota/travi/controller/ReviewController.java`
- Nhiệm vụ:
  - API lấy danh sách đánh giá của tài sản
  - API cho khách hàng gửi review
  - Khi tạo review sẽ bổ sung tín hiệu AI cho hồ sơ người dùng

### 4.2. Backend - Service xử lý AI

1. `backend/src/main/java/com/ota/travi/service/AiRecommendationService.java`
- Đây là file trung tâm quan trọng nhất của toàn bộ recommendation engine.
- Nhiệm vụ:
  - Đọc hồ sơ người dùng
  - Lấy sở thích, từ khóa, lịch sử đặt chỗ, lịch sử click
  - Tạo danh sách gợi ý khách sạn / nhà hàng
  - Kết hợp content-based + behavior-based + collaborative filtering + promotion scoring
  - Cập nhật hồ sơ AI sau mỗi lần gợi ý
  - Ghi nhận feedback và hành vi người dùng
  - Tìm recommendation giảm giá tốt nhất để phục vụ notification
- Điểm đặc biệt:
  - Có hỗ trợ `strictCity` để bật chế độ lọc cứng theo thành phố
  - Travel Planner dùng `strictCity = true` để tránh trường hợp người dùng chọn Bình Định nhưng hệ thống lại gợi ý Đà Lạt
  - Recommendation thường ở trang chủ vẫn dùng soft-filter để tránh trả về danh sách rỗng quá sớm

2. `backend/src/main/java/com/ota/travi/service/AiScoreCalculator.java`
- Nhiệm vụ:
  - Tách riêng logic chấm điểm ra khỏi service chính
  - Cộng điểm từ hành vi người dùng
  - Cộng điểm từ collaborative filtering
  - Cộng điểm từ khuyến mãi
  - Giới hạn điểm tổng hợp nếu cần
- Ý nghĩa:
  - Làm code sạch hơn
  - Dễ test riêng scoring logic

3. `backend/src/main/java/com/ota/travi/service/CollaborativeFilteringService.java`
- Nhiệm vụ:
  - Phân tích các tài sản mà người dùng hiện tại đã tương tác
  - Tìm các hồ sơ AI khác có tương tác trùng với các tài sản đó
  - Từ nhóm người dùng tương tự, lấy thêm các tài sản mà họ đã quan tâm
  - Trả về điểm collaborative cho từng tài sản ứng viên
- Ý nghĩa:
  - Mô phỏng một dạng collaborative filtering thực tế ở mức MVP
  - Tăng khả năng gợi ý chéo, không chỉ dựa vào sở thích tĩnh

4. `backend/src/main/java/com/ota/travi/service/TravelPlannerService.java`
- Nhiệm vụ:
  - Điều phối việc tạo lịch trình du lịch
  - Gọi `AiRecommendationService` để lấy danh sách địa điểm phù hợp
  - Tách danh sách thành hotel và restaurant
  - Gọi LLM nếu có key hợp lệ
  - Nếu LLM lỗi thì fallback sang planner rule-based
- Điểm mới đã sửa:
  - Khi planner gọi recommendation, hệ thống bật `strictCity = true` để chỉ lấy địa điểm đúng thành phố người dùng chọn

5. `backend/src/main/java/com/ota/travi/service/AiTravelPlannerLlmService.java`
- Nhiệm vụ:
  - Kết nối Spring AI / OpenAI để sinh lịch trình bằng LLM
  - Tạo prompt từ request và danh sách recommendation thật
  - Yêu cầu model trả về JSON hợp lệ theo schema
  - Parse kết quả JSON thành `ItineraryResponse`
  - Trả `Optional.empty()` nếu lỗi để hệ thống fallback an toàn
- Ý nghĩa:
  - LLM không tự quyết định hoàn toàn dữ liệu
  - LLM chỉ sắp xếp và trình bày lịch trình dựa trên recommendation engine có sẵn

6. `backend/src/main/java/com/ota/travi/service/SmartNotificationService.java`
- Nhiệm vụ:
  - Tạo thông báo thông minh cho khách hàng
  - Nhắc booking sắp tới
  - Tạo cảnh báo thời tiết
  - Tạo notification recommendation nếu có gợi ý điểm cao
  - Tạo notification promotion nếu có ưu đãi phù hợp
- Ý nghĩa:
  - Biến AI thành trải nghiệm chủ động, không chỉ chờ người dùng bấm tìm kiếm

7. `backend/src/main/java/com/ota/travi/service/PromotionService.java`
- Nhiệm vụ:
  - Tìm khuyến mãi active tốt nhất cho từng tài sản
  - Cung cấp dữ liệu promotion thật cho scoring engine

8. `backend/src/main/java/com/ota/travi/service/ReviewService.java`
- Nhiệm vụ:
  - Xử lý tạo và đọc review
  - Khi khách gửi review, đồng thời lưu thêm event REVIEW cho AI profile

9. `backend/src/main/java/com/ota/travi/service/PublicCatalogService.java`
- Nhiệm vụ liên quan AI:
  - Tính điểm đánh giá trung bình
  - Tính số lượng đánh giá
  - Đưa dữ liệu rating thật ra catalog / detail để tăng giá trị recommendation và trải nghiệm người dùng

### 4.3. Backend - Entity và Repository

1. `backend/src/main/java/com/ota/travi/entity/HoSoAI.java`
- Nhiệm vụ:
  - Đại diện hồ sơ AI của khách hàng
  - Lưu thông tin nền tảng cho recommendation engine

2. `backend/src/main/java/com/ota/travi/entity/ThongBaoNguCanh.java`
- Nhiệm vụ:
  - Lưu các thông báo / cảnh báo ngữ cảnh có thể tái sử dụng trong Smart Notification

3. `backend/src/main/java/com/ota/travi/entity/KhuyenMai.java`
- Nhiệm vụ:
  - Lưu dữ liệu khuyến mãi thật trong hệ thống
  - Là nguồn dữ liệu cho promotion recommendation

4. `backend/src/main/java/com/ota/travi/entity/DanhGia.java`
- Nhiệm vụ:
  - Lưu review / rating của người dùng cho tài sản
  - Là một nguồn tín hiệu cho AI profile và collaborative filtering

5. Repository chính:
- `backend/src/main/java/com/ota/travi/repository/HoSoAIRepository.java`
- `backend/src/main/java/com/ota/travi/repository/ThongBaoNguCanhRepository.java`
- `backend/src/main/java/com/ota/travi/repository/KhuyenMaiRepository.java`
- `backend/src/main/java/com/ota/travi/repository/DanhGiaRepository.java`
- Nhiệm vụ:
  - Truy xuất dữ liệu hồ sơ AI, thông báo ngữ cảnh, khuyến mãi, review

### 4.4. Backend - DTO phục vụ AI

1. `backend/src/main/java/com/ota/travi/dto/request/AiUserEventRequest.java`
- Nhiệm vụ:
  - Mô tả request gửi sự kiện AI từ frontend lên backend
  - Chứa các trường như `eventType`, `assetType`, `assetId`, `keyword`, `city`, `source`, `metadata`

2. `backend/src/main/java/com/ota/travi/dto/request/AiRecommendationFeedbackRequest.java`
- Nhiệm vụ:
  - Nhận feedback recommendation như viewed, clicked, booked, feedbackRating, feedbackNote

3. `backend/src/main/java/com/ota/travi/dto/request/CreateReviewRequest.java`
- Nhiệm vụ:
  - Dữ liệu tạo review từ người dùng

4. `backend/src/main/java/com/ota/travi/dto/response/UserAiRecommendationResponse.java`
- Nhiệm vụ:
  - Trả về profile summary, signals và danh sách recommendation

5. `backend/src/main/java/com/ota/travi/dto/response/AiRecommendationItemResponse.java`
- Nhiệm vụ:
  - Đại diện một item recommendation gồm id, type, name, city, district, basePrice, score, reasons

6. `backend/src/main/java/com/ota/travi/dto/response/ItineraryResponse.java`
- Nhiệm vụ:
  - Chuẩn dữ liệu trả về cho travel planner

7. `backend/src/main/java/com/ota/travi/dto/response/PromotionResponse.java`
- Nhiệm vụ:
  - Dữ liệu public promotion cho frontend hoặc API client

8. `backend/src/main/java/com/ota/travi/dto/response/ReviewResponse.java`
- Nhiệm vụ:
  - Dữ liệu review trả về cho màn hình chi tiết tài sản

### 4.5. Backend - Migration database

1. `backend/src/main/resources/db/migration/V3__ai_context_schema.sql`
- Nhiệm vụ:
  - Tạo các bảng nền tảng cho AI profile và context
  - Bao gồm `ho_so_ai`, `ho_so_ai_lich_su_click`, `ho_so_ai_tu_khoa_tim_kiem`, `thong_bao_ngu_canh`

2. `backend/src/main/resources/db/migration/V6__ai_reviews_promotions.sql`
- Nhiệm vụ:
  - Tạo thêm schema cho review và promotion
  - Hỗ trợ collaborative filtering và recommendation nâng cao

### 4.6. Frontend - Module AI

1. `frontend/src/features/ai/types.ts`
- Nhiệm vụ:
  - Định nghĩa type cho recommendation, itinerary, event, preferences, notifications

2. `frontend/src/features/ai/services/aiService.ts`
- Nhiệm vụ:
  - Tập trung toàn bộ API client cho AI feature
  - Gọi recommendation API
  - Gửi AI event
  - Gửi feedback
  - Gọi itinerary planner
  - Gọi smart notifications

3. `frontend/src/features/ai/hooks/useAiRecommendations.ts`
- Nhiệm vụ:
  - Custom hook để fetch recommendation từ backend
  - Quản lý loading, error, refetch

4. `frontend/src/features/ai/components/UserAiRecommendations.tsx`
- Nhiệm vụ:
  - Hiển thị danh sách gợi ý AI ngay trên trang chủ
  - Hiển thị score, lý do gợi ý, vị trí, giá cơ bản
  - Khi người dùng click vào item, gửi feedback best-effort về backend

5. `frontend/src/features/ai/index.ts`
- Nhiệm vụ:
  - Export module AI để frontend dùng thống nhất

### 4.7. Frontend - Tracking hành vi và review

1. `frontend/src/features/hotels/hooks/useHotelSearch.ts`
- Nhiệm vụ:
  - Khi khách hàng tìm kiếm khách sạn, gửi event `SEARCH` về backend AI

2. `frontend/src/features/restaurants/hooks/useRestaurantSearch.ts`
- Nhiệm vụ:
  - Khi khách hàng tìm kiếm nhà hàng, gửi event `SEARCH` về backend AI

3. `frontend/src/features/reviews/types.ts`
- Nhiệm vụ:
  - Định nghĩa kiểu dữ liệu review

4. `frontend/src/features/reviews/services/reviewService.ts`
- Nhiệm vụ:
  - API client cho review

5. `frontend/src/features/reviews/components/ReviewPanel.tsx`
- Nhiệm vụ:
  - Hiển thị danh sách review và form gửi review

6. `frontend/src/features/hotels/pages/HotelDetailPage.tsx`
- Nhiệm vụ:
  - Gắn `ReviewPanel` vào trang chi tiết khách sạn

7. `frontend/src/features/restaurants/pages/RestaurantDetailPage.tsx`
- Nhiệm vụ:
  - Gắn `ReviewPanel` vào trang chi tiết nhà hàng

8. `frontend/src/pages/HomePage.tsx`
- Nhiệm vụ:
  - Gắn component AI Recommendation vào trang chủ cho khách hàng

## 5. Giải thích cách hệ thống hoạt động

### 5.1. AI User Profile hoạt động như thế nào?
Khi người dùng tương tác với hệ thống, frontend gửi event về backend. Backend lưu lại thông tin đó thành các tín hiệu hành vi. Ví dụ:

- Tìm kiếm Bình Định -> lưu keyword/city vào lịch sử tìm kiếm AI
- Xem chi tiết khách sạn -> lưu event VIEW
- Click recommendation -> lưu event CLICK
- Đặt phòng -> lưu event BOOK
- Đánh giá nhà hàng -> lưu event REVIEW

Từ những dữ liệu này, backend dần xây dựng một hồ sơ AI riêng cho từng người dùng.

### 5.2. Recommendation Engine hoạt động như thế nào?
Quá trình gợi ý gồm các bước:

1. Lấy hồ sơ người dùng
2. Đọc sở thích và từ khóa gần đây
3. Đọc lịch sử booking
4. Đọc lịch sử click / behavior
5. Tính collaborative score từ người dùng tương tự
6. Duyệt toàn bộ khách sạn / nhà hàng khả dụng
7. Chấm điểm từng tài sản
8. Sắp xếp giảm dần theo score
9. Trả về top N item tốt nhất

### 5.3. Soft Filter và Hard Filter theo thành phố
Đây là điểm rất quan trọng trong hệ thống.

- Soft Filter:
  - Thành phố chỉ là một yếu tố cộng điểm
  - Nếu không có tài sản đúng thành phố, hệ thống vẫn có thể trả về nơi khác nếu tổng điểm cao
  - Phù hợp với recommendation trên trang chủ

- Hard Filter:
  - Thành phố trở thành điều kiện lọc bắt buộc
  - Nếu người dùng chọn Bình Định thì chỉ lấy dữ liệu trong Bình Định
  - Nếu database chưa có dữ liệu Bình Định thì planner sẽ không lấy địa điểm khác thay thế
  - Phù hợp với Travel Planner vì lịch trình phải nhất quán với điểm đến đã chọn

### 5.4. Collaborative Filtering hoạt động như thế nào?
Hệ thống không xây mô hình machine learning phức tạp mà áp dụng một phiên bản collaborative filtering theo logic:

1. Xác định các tài sản người dùng hiện tại từng tương tác
2. Tìm các hồ sơ AI khác cũng từng tương tác các tài sản đó
3. Lấy thêm các tài sản mà nhóm người dùng tương tự từng quan tâm
4. Dùng số lần xuất hiện như một dạng điểm cộng đồng
5. Cộng điểm này vào recommendation score cuối cùng

Cách làm này đơn giản nhưng hiệu quả cho MVP vì tận dụng được dữ liệu hành vi thật mà không cần huấn luyện model riêng.

### 5.5. Travel Planner hoạt động như thế nào?
Luồng planner gồm 2 nhánh:

- Nhánh 1: LLM thành công
  - Recommendation engine tạo danh sách địa điểm
  - Hệ thống build prompt
  - Gọi Spring AI / OpenAI
  - Model trả JSON lịch trình
  - Backend parse JSON và trả về frontend

- Nhánh 2: LLM lỗi hoặc chưa có API key
  - Hệ thống tự động fallback sang rule-based planner
  - Rule-based planner chia hoạt động theo ngày, theo buổi, dùng danh sách recommendation có sẵn

Nhờ đó hệ thống luôn có kết quả, không bị phụ thuộc tuyệt đối vào model bên ngoài.

## 6. Một số đoạn code trọng tâm

### 6.1. Chấm điểm theo thành phố trong recommendation
File: `backend/src/main/java/com/ota/travi/service/AiRecommendationService.java`

```java
if (matchesCity(profile, city)) {
    score += 25;
    reasons.add("Phu hop thanh pho dang quan tam");
}
```

Ý nghĩa:
- Nếu tài sản nằm đúng thành phố người dùng quan tâm, hệ thống cộng thêm điểm ưu tiên.

### 6.2. Lọc cứng theo thành phố cho planner
File: `backend/src/main/java/com/ota/travi/service/AiRecommendationService.java`

```java
return khachSanRepository.findByTrangThai(TrangThaiTaiSan.SAN_SANG).stream()
        .filter(hotel -> !strictCity || matchesCity(hotel.getHoSoKinhDoanh(), city))
        .map(hotel -> scoreHotel(hotel, searchTokens, city, clickedAssetIds, bookedAssetIds, collaborativeScores))
        .toList();
```

Ý nghĩa:
- Nếu `strictCity = true` thì chỉ giữ lại các địa điểm đúng thành phố.
- Đây là sửa đổi quan trọng để Travel Planner không đề xuất sai điểm đến.

### 6.3. Tính collaborative filtering
File: `backend/src/main/java/com/ota/travi/service/CollaborativeFilteringService.java`

```java
List<String> similarProfileIds = currentAssetIds.stream()
        .flatMap(assetId -> jdbcTemplate.queryForList(
                """
                SELECT DISTINCT ho_so_ai_id
                FROM ho_so_ai_hanh_vi
                WHERE id_tai_san = ? AND ho_so_ai_id <> ?
                """,
                String.class,
                assetId,
                hoSoAIId
        ).stream())
        .distinct()
        .limit(50)
        .toList();
```

Ý nghĩa:
- Từ các tài sản user đã tương tác, hệ thống tìm ra các user khác có chung hành vi.
- Đây là bước nền để sinh điểm collaborative.

### 6.4. Gọi LLM để sinh lịch trình
File: `backend/src/main/java/com/ota/travi/service/AiTravelPlannerLlmService.java`

```java
String content = chatClient.prompt()
        .system("Ban la AI lap lich trinh du lich cho TraVi...")
        .user(buildPrompt(request, recommendations))
        .call()
        .content();
```

Ý nghĩa:
- Hệ thống dùng LLM để biến danh sách recommendation thành lịch trình có cấu trúc.

### 6.5. Fallback rule-based khi LLM lỗi
File: `backend/src/main/java/com/ota/travi/service/TravelPlannerService.java`

```java
return aiTravelPlannerLlmService.generateWithLlm(request, recommendations.recommendations())
        .orElseGet(() -> generateRuleBasedItinerary(request, hotels, restaurants));
```

Ý nghĩa:
- Nếu LLM lỗi, hệ thống vẫn tạo được itinerary bằng logic backend thông thường.

## 7. Cấu hình cần thiết
Để dùng đầy đủ Travel Planner dạng LLM, cần cấu hình API key thật trong `.env` hoặc biến môi trường backend.

Ví dụ:

```env
SPRING_AI_OPENAI_API_KEY=sk-xxxx...your_real_key...
```

Nếu không có key hợp lệ:
- Recommendation engine vẫn hoạt động bình thường
- Event tracking vẫn hoạt động
- Review / promotion / notification vẫn hoạt động
- Travel Planner sẽ tự fallback sang rule-based

## 8. Kết quả đạt được
Sau khi hoàn thiện, hệ thống AI của TraVi đã đạt được các chức năng sau:

- AI User Profile
- Search History Tracking
- Click / View / Booking / Review / Promotion Event Tracking
- Recommendation cho khách sạn và nhà hàng
- Collaborative Filtering mức MVP thực tế
- Promotion Recommendation
- Travel Planner theo ngày
- Smart Notification theo booking, thời tiết, recommendation, promotion
- Hard Filter theo thành phố cho Travel Planner
- Review UI/API gắn với AI signals

## 9. Kết luận
Chức năng AI trong TraVi được triển khai theo hướng thực dụng và phù hợp với đồ án:

- Không phụ thuộc hoàn toàn vào model bên ngoài
- Tận dụng dữ liệu thật trong hệ thống
- Có logic recommendation rõ ràng, giải thích được
- Có fallback an toàn nếu LLM lỗi
- Có khả năng mở rộng thêm trong tương lai

Điểm mạnh của phần AI này là nó không chỉ dừng ở mức “gọi API AI”, mà đã hình thành được một pipeline khá đầy đủ:

Hành vi người dùng -> Hồ sơ AI -> Recommendation Engine -> Travel Planner / Notification -> Hiển thị trên Frontend

Đây là nền tảng tốt để phát triển thêm các tính năng AI nâng cao trong các giai đoạn tiếp theo như dynamic pricing, ranking model, embedding search hoặc recommendation learning-to-rank.

