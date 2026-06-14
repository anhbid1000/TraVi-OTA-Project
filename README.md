# TraVi-OTA — Online Travel Agency Platform

TraVi-OTA là nền tảng OTA phục vụ đặt phòng khách sạn, đặt bàn/đặt món nhà hàng và quản lý vận hành cho đối tác. Dự án được tổ chức theo mô hình monorepo gồm **Spring Boot backend**, **React/Vite frontend**, **PostgreSQL**, **Redis** và **Docker Compose**.

> Trạng thái hiện tại: dự án đã có các module chính cho guest/user/partner như catalog khách sạn - nhà hàng, booking, thanh toán, loyalty/voucher, đánh giá - khiếu nại, dashboard đối tác, promotion và AI travel planner.

---

## 1. Tính năng chính

### Guest / Public

- Xem trang chủ, tìm kiếm khách sạn và nhà hàng.
- Xem danh sách nổi bật, bộ lọc, chi tiết khách sạn/nhà hàng.
- Xem review công khai của từng tài sản.
- Xem dự báo thời tiết qua Open-Meteo.
- Xem khuyến mãi đang hoạt động.

### Customer / User

- Đăng ký, đăng nhập, xác thực email OTP, quên mật khẩu.
- Đăng nhập Google OAuth.
- Onboarding sở thích người dùng.
- Đặt phòng/đặt dịch vụ, checkout và thanh toán.
- Xem lịch sử booking và chi tiết booking.
- Áp dụng voucher khi checkout.
- Loyalty dashboard: điểm, hạng thành viên, lịch sử điểm, ví voucher, đổi voucher.
- Gửi review, complaint/khiếu nại và theo dõi xử lý.
- AI travel planner và gợi ý cá nhân hóa.

### Partner / Staff

- Đăng ký/đăng nhập đối tác.
- Quản lý hồ sơ kinh doanh.
- Quản lý khách sạn, phòng, nhà hàng, bàn, menu/món ăn.
- Partner dashboard: KPI, doanh thu, booking gần đây, phòng/món nổi bật.
- Quản lý booking của tài sản.
- Quản lý review và phản hồi review.
- Quản lý complaint/khiếu nại.
- Quản lý promotion/voucher và xem analytics.
- AI insights cho đối tác.

### Admin / System

- Có namespace API cho admin users, approvals, configs, monitoring, moderation/report verdict.
- Có endpoint hệ thống cho payment callback, map integration và notification trigger.

---

## 2. Tech Stack

### Backend

- Java 21
- Spring Boot `4.0.5` theo `backend/pom.xml`
- Spring Web MVC, Spring Security, Spring Data JPA
- JWT authentication
- Spring Mail
- Spring WebSocket
- Spring AI OpenAI
- Springdoc OpenAPI / Swagger UI
- Flyway migration
- PostgreSQL driver
- Redis integration
- Bucket4j rate limiting
- Lombok
- Maven Wrapper

### Frontend

- React `19.x`
- TypeScript `~6.0.x`
- Vite `8.x`
- React Router DOM `7.x`
- Axios
- TanStack React Query
- Zustand
- React Hook Form + Zod
- Ant Design
- Tailwind CSS `4.x`
- Leaflet / React Leaflet
- Google OAuth client

### Database / Infrastructure

- PostgreSQL 16 Alpine
- Redis 7 Alpine
- Docker Compose
- GitHub Actions CI

---

## 3. Cấu trúc thư mục

```text
TraVi-OTA-Project/
├── backend/                         # Spring Boot API
│   ├── src/main/java/com/ota/travi/
│   │   ├── controller/              # REST controllers
│   │   ├── service/                 # Business logic
│   │   ├── repository/              # Spring Data JPA repositories
│   │   ├── entity/                  # JPA entities
│   │   ├── dto/request/             # Request DTOs
│   │   ├── dto/response/            # Response DTOs
│   │   ├── config/                  # Security/CORS/app configs
│   │   ├── constant/ApiEndpoints.java
│   │   └── booking/                 # Booking/payment submodule
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── application-dev.properties
│   │   ├── application-test.properties
│   │   ├── application-prod.properties
│   │   └── db/migration/common/     # Flyway SQL migrations
│   ├── uploads/                     # Local uploaded/static images
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                        # React + Vite app
│   ├── src/
│   │   ├── features/                # Feature modules: hotels, restaurants, feedback...
│   │   ├── pages/                   # Route-level pages
│   │   ├── services/                # API clients/services
│   │   ├── contexts/                # React contexts
│   │   ├── routes/                  # ProtectedRoute/StaffRoute
│   │   └── App.tsx                  # Main route config
│   ├── Dockerfile
│   └── package.json
├── docs/
│   └── partner-onboarding.md
├── docker-compose.yml
├── .github/workflows/ci.yml
└── README.md
```

---

## 4. Yêu cầu môi trường

### Chạy bằng Docker — khuyến nghị

- Docker
- Docker Compose plugin
- File `.env` ở thư mục gốc project

### Chạy local từng phần

- JDK 21
- Node.js 20+
- PostgreSQL 16
- Redis 7

---

## 5. Biến môi trường

Project dùng `docker-compose.yml` đọc file `.env` ở thư mục gốc.

Tạo file `.env`:

```bash
cp .env.example .env
```

Nếu chưa có `.env.example`, tạo `.env` tối thiểu như sau:

```env
# Database
POSTGRES_PASSWORD=your_postgres_password

# Backend security
JWT_SECRET_KEY=replace_with_a_long_random_secret
JWT_EXPIRATION_TIME=3600000

# Mail OTP / notification
MAIL_USERNAME=your_gmail@gmail.com
MAIL_PASSWORD=your_gmail_app_password

# AI
OPENAI_API_KEY=sk-...

# Frontend / OAuth
VITE_GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_ID_ENV=your_google_client_id
```

> Không commit `.env` thật lên Git.

---

## 6. Chạy dự án bằng Docker Compose

Ở thư mục gốc project:

```bash
docker compose up -d --build
```

Nếu máy cần quyền root cho Docker:

```bash
sudo docker compose up -d --build
```

Các service sau khi chạy:

| Service | URL / Port |
|---|---|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| PostgreSQL | localhost:5432 |
| Redis | localhost:6379 |

Lệnh thường dùng:

```bash
# Xem container
sudo docker compose ps

# Xem log backend
sudo docker compose logs -f backend

# Xem log frontend
sudo docker compose logs -f frontend

# Restart backend sau khi sửa Java code
sudo docker compose restart backend

# Dừng và xóa container
sudo docker compose down

# Dừng và xóa cả volume database/redis
sudo docker compose down -v
```

> Backend và frontend đang mount source code vào container. Frontend có hot reload, backend thường cần restart container để nhận code Java mới.

---

## 7. Chạy local không qua Docker

### 7.1. Chạy backend

Cần PostgreSQL và Redis đang chạy sẵn.

```bash
cd backend
./mvnw clean spring-boot:run -Dspring-boot.run.profiles=dev
```

Backend mặc định chạy ở:

```text
http://localhost:8080
```

### 7.2. Chạy frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend mặc định chạy ở:

```text
http://localhost:5173
```

Build frontend:

```bash
npm run build
```

Lint frontend:

```bash
npm run lint
```

---

## 8. API namespace chính

Các endpoint được quản lý trong:

```text
backend/src/main/java/com/ota/travi/constant/ApiEndpoints.java
```

Base API version hiện tại:

```text
/api/v1
```

Nhóm endpoint chính:

| Nhóm | Prefix | Mục đích |
|---|---|---|
| Public | `/api/v1/public` | Catalog, search, weather, public review, active promotions |
| Auth | `/api/v1/auth` | Register, login, Google login, OTP, refresh/logout |
| User | `/api/v1/user` | Profile, booking, review, complaint, loyalty, recommendation |
| Partner | `/api/v1/partner` | Dashboard, asset management, booking, promotion, review, complaint |
| Admin | `/api/v1/admin` | User/admin config/approval/moderation/report |
| Systems | `/api/v1/systems` | Payment callback, map, notification trigger |

Một số public API quan trọng:

```text
GET /api/v1/public/hotels/search
GET /api/v1/public/hotels/featured
GET /api/v1/public/hotels/{id}
GET /api/v1/public/restaurants/search
GET /api/v1/public/restaurants/featured
GET /api/v1/public/restaurants/{id}
GET /api/v1/public/weather/forecast
GET /api/v1/public/promotions/active
```

---

## 9. Frontend routes chính

Route được khai báo trong:

```text
frontend/src/App.tsx
```

| Route | Ý nghĩa |
|---|---|
| `/` | Trang chủ |
| `/hotels` | Danh sách khách sạn |
| `/hotels/:id` | Chi tiết khách sạn |
| `/restaurants` | Danh sách nhà hàng |
| `/restaurants/:id` | Chi tiết nhà hàng |
| `/search` | Tìm kiếm tổng hợp |
| `/checkout` | Checkout |
| `/payment` | Thanh toán |
| `/login`, `/register` | Auth khách hàng |
| `/partner/login`, `/partner/register` | Auth đối tác |
| `/onboarding` | Onboarding sở thích |
| `/ai/travel-planner` | AI travel planner |
| `/user/bookings-v2` | Booking của user |
| `/user/complaints` | Khiếu nại của user |
| `/user/loyalty` | Loyalty dashboard |
| `/partner/dashboard` | Dashboard đối tác |
| `/partner/business-profile` | Hồ sơ kinh doanh |
| `/partner/hotel` | Quản lý khách sạn |
| `/partner/restaurant` | Quản lý nhà hàng |
| `/partner/room` | Quản lý phòng |
| `/partner/tables` | Quản lý bàn |
| `/partner/menus` | Quản lý menu |
| `/partner/bookings` | Booking phía partner |
| `/partner/reviews` | Review phía partner |
| `/partner/complaints` | Complaint phía partner |
| `/partner/promotions` | Promotion/voucher phía partner |
| `/partner/ai-insights` | AI insight phía partner |

---

## 10. Database migration

Flyway migration nằm tại:

```text
backend/src/main/resources/db/migration/common/
```

Một số nhóm migration hiện có:

- `V1__init_schema_unified.sql`: schema ban đầu.
- `V2__seed_full_unified.sql`: seed dữ liệu tổng hợp.
- `V3__ai_context_schema.sql`: AI context.
- `V4__booking_module_tables.sql`: booking module.
- `V6__ai_training_partner_insight_schema.sql`: AI/partner insight.
- `V7__module4_promotion_voucher_loyalty.sql`: promotion, voucher, loyalty.
- `V8__add_voucher_id_to_don_dat_cho.sql`: liên kết voucher với đơn đặt chỗ.
- Các migration `V202606xx...`: seed dữ liệu khách sạn, nhà hàng, ảnh, support data.

Quy tắc khi thêm migration:

```text
V{version}__mo_ta_ngan_gon.sql
```

Ví dụ:

```text
V13__add_partner_notification_table.sql
```

Lưu ý:

- Không sửa migration đã chạy trên database chung nếu không có kế hoạch reset/repair rõ ràng.
- Với seed data nên ưu tiên idempotent: `INSERT ... ON CONFLICT DO NOTHING`.
- Trong dev profile, project đang cấu hình Flyway linh hoạt hơn để dễ chạy với DB cũ sau merge.

---

## 11. Kiến trúc backend

Luồng chuẩn:

```text
Controller -> Service -> Repository -> Entity
                     -> DTO response/request
```

Quy ước chính:

- Controller: `backend/src/main/java/com/ota/travi/controller`
- Service: `backend/src/main/java/com/ota/travi/service`
- Repository: `backend/src/main/java/com/ota/travi/repository`
- Entity: `backend/src/main/java/com/ota/travi/entity`
- DTO: `backend/src/main/java/com/ota/travi/dto/request|response`
- API constants: `ApiEndpoints.java`

Backend hiện có nhiều domain/entity như:

- User, KhachHang, DoiTac, VaiTro
- TaiSan, HoSoKinhDoanh, KhachSan, Phong, NhaHang, Ban, ThucDon, MonAn
- DonDatCho, DonKhachSan, DonNhaHang, DatPhong
- Review, ReviewReply, Complaint, ComplaintMessage, FeedbackAttachment
- Voucher, CustomerVoucher, LoyaltyRule, LichSuDiem, MilestoneProgress
- KhuyenMaiTrucTiep, PromotionAnalyticsDaily
- HoSoAI, SuKienHanhVi, ThongBaoNguCanh, NotificationEvent

---

## 12. Kiến trúc frontend

Luồng frontend hiện tại:

```text
pages/ hoặc features/ -> services/ -> backend API
```

Quy ước chính:

- `frontend/src/pages`: route-level pages như auth, checkout, partner dashboard, user dashboard.
- `frontend/src/features`: module độc lập như hotels, restaurants, feedback.
- `frontend/src/services`: API clients dùng Axios.
- `frontend/src/contexts/AuthContext.tsx`: trạng thái auth.
- `frontend/src/routes`: route guard cho user/staff.

Lưu ý quan trọng khi gọi API:

- Axios base URL lấy từ `VITE_API_BASE_URL`, mặc định qua Docker là `http://localhost:8080`.
- API backend dùng prefix `/api/v1`.
- Service frontend nên gọi đúng version, ví dụ `/api/v1/user/...` hoặc theo cấu hình axios tương ứng.

---

## 13. File upload và ảnh

Backend dùng local storage:

```properties
file.upload.dir=uploads
```

Thư mục ảnh hiện nằm trong:

```text
backend/uploads/
├── hotel/
├── restaurant/
├── room/
└── partner-assets/
```

Khi backend trả ảnh cho frontend, cần đảm bảo đường dẫn tương thích browser, ví dụ chuyển `/uploads/...` thành URL đầy đủ `http://localhost:8080/uploads/...` nếu cần.

---

## 14. CI/CD

GitHub Actions workflow nằm tại:

```text
.github/workflows/ci.yml
```

Pipeline hiện chạy:

1. Backend build/test với JDK 21:

```bash
cd backend
./mvnw clean verify -Dspring.profiles.active=test --no-transfer-progress
```

2. Frontend install/build/lint với Node.js 20:

```bash
cd frontend
npm ci
npm run build
npm run lint --if-present
```

3. Docker build check:

```bash
docker compose build --no-cache
docker compose config
```

Workflow trigger khi push vào `main`, `develop`, mọi branch `*`, và pull request vào `main`/`develop`.

---

## 15. Checklist dev nhanh

### Sau khi pull code mới

```bash
git pull
sudo docker compose up -d --build
sudo docker compose logs -f backend
```

### Khi sửa backend

```bash
sudo docker compose restart backend
sudo docker compose logs -f backend
```

### Khi sửa frontend

Frontend container có hot reload. Nếu lỗi dependency:

```bash
sudo docker compose build frontend --no-cache
sudo docker compose up -d frontend
```

### Khi migration lỗi

Kiểm tra log Flyway:

```bash
sudo docker compose logs backend | grep -i flyway
```

Nếu cần reset database dev hoàn toàn:

```bash
sudo docker compose down -v
sudo docker compose up -d --build
```

> Lệnh `down -v` sẽ xóa volume PostgreSQL/Redis, mất dữ liệu local.

---

## 16. Tài liệu liên quan

- Partner onboarding: [`docs/partner-onboarding.md`](docs/partner-onboarding.md)
- Swagger UI khi app chạy: http://localhost:8080/swagger-ui.html
- API constants: `backend/src/main/java/com/ota/travi/constant/ApiEndpoints.java`
- Frontend route config: `frontend/src/App.tsx`

---

## 17. Ghi chú bảo mật

- Không commit `.env`, password, Gmail app password, JWT secret, OpenAI key.
- Không để secret thật trong issue, screenshot hoặc README.
- Với JWT secret nên dùng chuỗi dài, random, khác nhau giữa dev/prod.
- Với Gmail SMTP nên dùng App Password, không dùng password tài khoản chính.
