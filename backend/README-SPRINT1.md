# TraVi-OTA Backend — Sprint 1

Backend của dự án **TraVi-OTA** được xây dựng với **Spring Boot** và hoàn thiện toàn bộ lớp xác thực (Authentication): đăng ký, xác minh OTP, đăng nhập email/password, đăng nhập Google OAuth2, quên mật khẩu, làm mới token, đăng xuất và phân quyền theo role.

---

## 1. Mục tiêu backend Sprint 1

- Đăng ký tài khoản mới (khách hàng / đối tác)
- Gửi OTP xác minh qua email (async)
- Xác minh OTP & kích hoạt tài khoản
- Gửi lại OTP khi hết hạn
- Đăng nhập bằng email + mật khẩu → phát hành JWT
- Đăng nhập / đăng ký bằng Google (Google Identity Services — ID Token)
- Quên mật khẩu: yêu cầu OTP → xác minh OTP → đặt lại mật khẩu
- Làm mới Access Token bằng Refresh Token
- Đăng xuất an toàn (token blacklist Redis)
- Bảo vệ API bằng Spring Security + JWT
- Phân quyền theo role: `KHACH_HANG`, `DOI_TAC`, `QUAN_TRI_VIEN`

---

## 2. Công nghệ sử dụng

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| Java | 21 | Ngôn ngữ chính |
| Spring Boot | 4.0.5 | Framework |
| Spring Web / WebMVC | — | REST API |
| Spring Security | — | JWT Authentication & Authorization |
| Spring Data JPA (Hibernate) | — | ORM, JOINED inheritance |
| Spring Data Redis | — | OTP storage, Token blacklist |
| Spring Mail (SMTP Gmail) | — | Gửi OTP email |
| JJWT | 0.11.5 | JWT generation & validation |
| PostgreSQL | 12+ | Database chính (dev/prod) |
| H2 | — | In-memory DB (test) |
| Swagger/OpenAPI | 3.0 | API Documentation |
| Lombok | — | Boilerplate reduction |
| Spring AI | 2.0.0-M4 | OpenAI integration (future) |
| Bucket4j | 8.10.1 | Rate Limiting / Anti-spam |
| Flyway | — | Database migrations |
| Google API Client Library | ��� | Xác minh Google ID Token |

---

## 3. Cấu trúc backend

```text
backend/
├── src/main/java/com/ota/travi
│   ├── config/
│   │   ├── AppConfig.java              # PasswordEncoder, RestTemplate
│   │   ├── AsyncConfig.java            # @EnableAsync cho email async
│   │   ├── DataInitializer.java        # Seed dữ liệu VaiTro khi khởi động
│   │   └── SecurityConfig.java         # Spring Security, JWT filter, CORS
│   │
│   ├── constant/
│   │   └── ApiEndpoints.java           # Registry tất cả endpoint (versioned)
│   │
│   ├── controller/
│   │   └── AuthController.java         # Register, Login, Google, Logout, Refresh,
│   │                                   # Verify OTP, Resend OTP, Forgot Password
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest.java           # { email, matKhau }
│   │   │   ├── RegisterRequest.java        # { username, email, hoTen,
│   │   │   │                               #   soDienThoai, matKhau, loaiTaiKhoan }
│   │   │   ├── GoogleAuthRequest.java      # { idToken, loaiTaiKhoan }
│   │   │   ├── VerifyOtpRequest.java       # { email, confirmOTP }
│   │   │   ├── ResendOtpRequest.java       # { email }
│   │   │   ├── ForgotPasswordRequest.java  # { email }
│   │   │   ├── ResetPasswordRequest.java   # { email, matKhauMoi, xacNhanMatKhau }
│   │   │   └── RefreshTokenRequest.java    # { refreshToken }
│   │   └── response/
│   │       ├── AuthResponse.java           # { accessToken, refreshToken,
│   │       │                               #   tokenType, message }
│   │       └── UserDto.java                # User representation
│   │
│   ├── entity/
│   │   ├── User.java                   # Abstract parent (JOINED inheritance)
│   │   ├── KhachHang.java              # Customer (KHACH_HANG)
│   │   ├── DoiTac.java                 # Partner (DOI_TAC)
│   │   ├── QuanTriVien.java            # Admin (QUAN_TRI_VIEN)
│   │   ├── VaiTro.java                 # Role/Permission
│   │   ├── SoThich.java                # Preferences
│   │   ├── DanhMucSoThich.java         # Preference categories
│   │   └── LichSuThaoTac.java          # Action history logs
│   │
│   ├── Enum/
│   │   ├── TrangThaiUser.java          # CHUA_XAC_THUC, HOAT_DONG, BI_KHOA
│   │   ├── GioiTinh.java               # NAM, NU, KHAC
│   │   ├── HangThanhVien.java          # DONG, BAC, VANG, KIM_CUONG
│   │   └── OtpPurpose.java             # REGISTER, RESET_PASSWORD
│   │
│   ├── repository/
│   │   ├── UserRepository.java         # User CRUD operations
│   │   └── VaiTroRepository.java       # Role lookup
│   │
│   ├── security/
│   │   ├── JwtUtil.java                    # JWT generation, validation, extraction
│   │   ├── JwtAuthenticationFilter.java    # Intercept requests, extract JWT
│   │   ├── CustomUserDetails.java          # UserDetails implementation
│   │   ├── CustomUserDetailsService.java   # Load user từ DB
│   │   └── RateLimitFilter.java            # Rate limiting (Bucket4j)
│   │
│   ├── service/
│   │   ├── AuthService.java                # Core business logic (register, login,
│   │   │                                   # Google, OTP, forgot password)
│   │   ├── OTPService.java                 # OTP generation, validation, email
│   │   ├── GoogleTokenVerifierService.java # Verify Google ID Token
│   │   └── TokenBlacklistService.java      # Redis token blacklist
│   │
│   └── TraViOtaApplication.java        # @SpringBootApplication entry point
│
├── src/main/resources
│   ├── application.properties           # Profile mặc định
│   ├── application-dev.properties       # Dev (PostgreSQL local)
│   ├── application-prod.properties      # Production
│   ├── application-test.properties      # Test (H2)
│   └── db/migration/
│       └── v1__init_schema.sql          # Initial schema (Flyway)
│
└── pom.xml
```

---

## 4. Các chức năng đã hoàn thiện

### 4.1. Đăng ký tài khoản

**Endpoint:** `POST /api/v1/auth/register`

**Request Body:**
```json
{
  "username": "user123",
  "email": "user@example.com",
  "hoTen": "Nguyễn Văn A",
  "soDienThoai": "0912345678",
  "matKhau": "Password@123",
  "loaiTaiKhoan": "KHACH_HANG"
}
```

**Lu��ng:**
```
1. Kiểm tra trùng username / email
2. Tạo entity theo loại (KhachHang hoặc DoiTac)
   - KhachHang: diemThanhVien=0, hangThanhVien=DONG
   - DoiTac: tiLeChietKhau=0.0
3. Gán trạng thái CHUA_XAC_THUC, mã hóa mật khẩu (BCrypt)
4. Lưu vào PostgreSQL
5. [After commit] Gửi OTP 6 số qua email (async, Redis TTL 4 phút)
→ Response: "Đăng ký thành công..."
```

---

### 4.2. Xác minh email (OTP)

**Endpoint:** `PUT /api/v1/auth/verify-email`

```json
{
  "email": "user@example.com",
  "confirmOTP": "123456"
}
```

**Luồng:**
```
1. Lấy OTP từ Redis (key: email + purpose=REGISTER)
2. OTP rỗng → "OTP đã hết hạn"
3. OTP sai → "OTP không hợp lệ"
4. OTP đúng → xóa Redis, cập nhật trangThai = HOAT_DONG
→ Response: "Xác minh OTP thành công. Tài khoản đã được kích hoạt."
```

---

### 4.3. Gửi lại OTP

**Endpoint:** `POST /api/v1/auth/resend-otp`

```json
{ "email": "user@example.com" }
```

**Luồng:**
```
1. Kiểm tra user tồn tại và còn CHUA_XAC_THUC
2. Tạo OTP mới → lưu Redis (TTL 4 phút, ghi đè OTP cũ)
3. Gửi email OTP async
→ Response: "Mã OTP mới đã được gửi tới email của bạn."
```

---

### 4.4. Đăng nhập bằng email + mật khẩu

**Endpoint:** `POST /api/v1/auth/login`

```json
{
  "email": "user@example.com",
  "matKhau": "Password@123"
}
```

**Luồng:**
```
1. RateLimitFilter: >5 req/phút → HTTP 429
2. Spring Security xác thực email + password
3. isEnabled() = (trangThai == HOAT_DONG)
4. Sinh Access Token (15 phút) + Refresh Token (7 ngày)
→ Response: { accessToken, refreshToken, tokenType: "Bearer", message }
```

**Response:**
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "message": "Đăng nhập thành công"
}
```

---

### 4.5. Đăng nhập / đăng ký bằng Google OAuth2

**Endpoint:** `POST /api/v1/auth/google`

```json
{
  "idToken": "google-id-token-từ-frontend",
  "loaiTaiKhoan": "KHACH_HANG"
}
```

**Luồng:**
```
1. GoogleTokenVerifierService.verifyIdToken(idToken)
   → Lấy email + tên từ Google API
2. Email chưa tồn tại:
   - Tạo user mới (KhachHang hoặc DoiTac theo loaiTaiKhoan)
   - trangThai = HOAT_DONG ngay (Google đã xác minh email)
3. Email tồn tại + CHUA_XAC_THUC → kích hoạt tài khoản
4. Email tồn tại + HOAT_DONG → đăng nhập bình thường
5. Sinh Access Token + Refresh Token
→ Response: { accessToken, refreshToken, tokenType, message }
```

---

### 4.6. Quên mật khẩu (3 bước)

#### Bước 1 — Yêu cầu OTP

**Endpoint:** `POST /api/v1/auth/forgot-password/request-otp`

```json
{ "email": "user@example.com" }
```

```
1. Kiểm tra user tồn tại và HOAT_DONG
2. Tạo OTP → Redis (key: email + purpose=RESET_PASSWORD, TTL 4 phút)
3. Gửi email OTP async
→ Response: "Mã OTP đặt lại mật khẩu đã được gửi tới email của bạn."
```

#### Bước 2 — Xác minh OTP

**Endpoint:** `POST /api/v1/auth/forgot-password/verify-otp`

```json
{
  "email": "user@example.com",
  "confirmOTP": "123456"
}
```

```
1. Kiểm tra OTP từ Redis (purpose=RESET_PASSWORD)
2. OTP đúng → đánh dấu phiên verified trong Redis
→ Response: "OTP hợp lệ. Vui lòng nhập mật khẩu mới."
```

#### Bước 3 — Đặt lại mật khẩu

**Endpoint:** `POST /api/v1/auth/forgot-password/reset`

```json
{
  "email": "user@example.com",
  "matKhauMoi": "NewPassword@456",
  "xacNhanMatKhau": "NewPassword@456"
}
```

```
1. Kiểm tra phiên verified còn hiệu lực
2. Kiểm tra hai mật khẩu khớp
3. BCrypt encode → lưu DB, xóa phiên verified khỏi Redis
→ Response: "Đặt lại mật khẩu thành công. Bạn có thể đăng nhập lại."
```

---

### 4.7. Làm mới Token

**Endpoint:** `POST /api/v1/auth/refresh`

```json
{ "refreshToken": "eyJhbGc..." }
```

**Luồng:**
```
1. Kiểm tra blacklist → "Thẻ đã bị vô hiệu hóa" nếu đã logout
2. Lấy username → load user mới nhất từ DB
3. Sinh Access Token mới (15 phút)
→ Response: { newAccessToken, refreshToken (cũ), tokenType, message }
```

---

### 4.8. Đăng xuất

**Endpoint:** `POST /api/v1/auth/logout`

**Headers:** `Authorization: Bearer <accessToken>`  
**Body (optional):** `{ "refreshToken": "..." }`

```
1. Access Token → ném vào Redis blacklist (TTL = thời gian còn lại)
2. Refresh Token (nếu có) → cũng ném vào blacklist
→ Response: "Đăng xuất thành công!"
```

---

### 4.9. Bảo mật bổ sung

#### Rate Limiting (Bucket4j)
- **5 requests/phút** cho `/api/v1/auth/login`
- HTTP 429 nếu vượt quá

#### Token Blacklist (Redis)
- Logout lưu token vào Redis với TTL = thời gian còn lại
- `JwtAuthenticationFilter` kiểm tra blacklist mỗi request

#### CORS
- Chỉ cho phép từ `${FRONTEND_URL}` (mặc định: `http://localhost:5173`)
- Methods: GET, POST, PUT, DELETE, OPTIONS; Credentials: true

#### Tài khoản chưa xác thực
- `isEnabled()` = `trangThai == HOAT_DONG`
- Tài khoản `CHUA_XAC_THUC` không thể đăng nhập email/password

---

### 4.10. Entity & Database Schema

```
User (Abstract — JOINED inheritance)
├── KhachHang (KHACH_HANG)  → diemThanhVien, hangThanhVien
│   └── [many] SoThich
├── DoiTac (DOI_TAC)         → tiLeChietKhau
└── QuanTriVien (QUAN_TRI_VIEN)

User → VaiTro (many-to-one)
User → LichSuThaoTac (one-to-many)
```

### 4.11. Trạng thái người dùng

| Trạng thái | Mô tả |
|---|---|
| `CHUA_XAC_THUC` | Vừa đăng ký, chờ verify OTP |
| `HOAT_DONG` | Đã kích hoạt, có thể đăng nhập |
| `BI_KHOA` | Bị admin khóa (Sprint 2) |

---

## 5. REST API Endpoints — Tổng hợp

| Method | Endpoint | Auth | Status | Mô tả |
|---|---|---|---|---|
| POST | `/api/v1/auth/register` | ❌ | ✅ | Đăng ký tài khoản mới |
| PUT | `/api/v1/auth/verify-email` | ❌ | ✅ | Xác minh OTP kích hoạt |
| POST | `/api/v1/auth/resend-otp` | ❌ | ✅ | Gửi lại OTP xác minh email |
| POST | `/api/v1/auth/login` | ❌ | ✅ | Đăng nhập email + password |
| POST | `/api/v1/auth/google` | ❌ | ✅ | Đăng nhập / đăng ký bằng Google |
| POST | `/api/v1/auth/forgot-password/request-otp` | ❌ | ✅ | Yêu cầu OTP quên mật khẩu |
| POST | `/api/v1/auth/forgot-password/verify-otp` | ❌ | ✅ | Xác minh OTP quên mật khẩu |
| POST | `/api/v1/auth/forgot-password/reset` | ❌ | ✅ | Đặt lại mật khẩu |
| POST | `/api/v1/auth/refresh` | ❌ | ✅ | Làm mới Access Token |
| POST | `/api/v1/auth/logout` | ✅ JWT | ✅ | Đăng xuất (blacklist token) |

> ✅ = Đã hoàn thiện | ❌ Auth = Không cần JWT (public)

---

## 6. Security Route Rules

| Route | Quyền | Mô tả |
|---|---|---|
| `/api/v1/auth/**` | Public | Toàn bộ auth endpoints |
| `/api/v1/public/**` | Public | Tìm kiếm công khai (Sprint 2) |
| `/api/v1/admin/**` | `QUAN_TRI_VIEN` | Dashboard quản trị (Sprint 2) |
| `/api/v1/partner/**` | `DOI_TAC` | Dashboard đối tác (Sprint 2) |
| Các API còn lại | Authenticated | Bắt buộc JWT hợp lệ |

---

## 7. Mô tả Service chính

### `AuthService`
- `register(RegisterRequest)` → Đăng ký, gửi OTP
- `login(LoginRequest)` → Xác thực, sinh JWT
- `loginWithGoogle(GoogleAuthRequest)` → Google OAuth2 login/register
- `verifyRegisterOtp(email, otp)` → Xác minh OTP đăng ký
- `resendRegisterOtp(email)` → Gửi lại OTP đăng ký
- `requestPasswordResetOtp(email)` → Yêu cầu OTP quên mật khẩu
- `verifyPasswordResetOtp(email, otp)` → Xác minh OTP quên mật khẩu
- `resetPassword(ResetPasswordRequest)` → Đặt lại mật khẩu

### `OTPService`
- `createOtp(email, purpose)` → Tạo OTP, lưu Redis TTL 4 phút
- `checkOtp(email, confirmOTP, purpose)` → Xác minh OTP
- `sendVerificationRegister(user)` → Email OTP đăng ký (**@Async**)
- `sendPasswordResetOtp(email, otp)` → Email OTP reset mật khẩu (**@Async**)

### `GoogleTokenVerifierService`
- `verifyIdToken(idToken)` → Gọi Google API, trả về `GoogleUserInfo(email, name)`
- Throw exception nếu token không hợp lệ

### `TokenBlacklistService`
- `addToBlacklist(token, ttlMs)` → Lưu Redis blacklist
- `isBlacklisted(token)` → Kiểm tra token đã logout chưa

### `JwtUtil`
- `generateToken(username, role)` → Access Token (15 phút)
- `generateRefreshToken(username)` → Refresh Token (7 ngày)
- `extractUsername(token)`, `validateToken(token)`, `getRemainingExpirationTime(token)`

### `JwtAuthenticationFilter`
- Extract Bearer token → validate signature + expiry + blacklist
- Set `Authentication` vào `SecurityContextHolder`

### `RateLimitFilter`
- **5 req/phút** cho `/api/v1/auth/login` → HTTP 429

---

## 8. Cấu hình Chi tiết

### Biến môi trường bắt buộc

```bash
JWT_SECRET_KEY=your-secret-key-at-least-32-chars-long
JWT_EXPIRATION_TIME=900000          # 15 phút (ms)
FRONTEND_URL_ENV=http://localhost:5173
MAIL_USERNAME=your-gmail@gmail.com
MAIL_PASSWORD=your-gmail-app-password   # Gmail App Password
OPENAI_API_KEY=sk-your-openai-key       # Optional — AI module
```

### `application.properties`

```properties
spring.profiles.active=dev
spring.application.name=TraVi-OTA
server.port=8080

SECRET_KEY=${JWT_SECRET_KEY}
JWT_EXPIRATION=${JWT_EXPIRATION_TIME}
FRONTEND_URL=${FRONTEND_URL_ENV:http://localhost:5173}
```

### `application-dev.properties`

```properties
spring.datasource.url=jdbc:postgresql://postgres:5432/travi_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_DB_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.data.redis.host=localhost
spring.data.redis.port=6379

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

spring.ai.openai.api-key=${OPENAI_API_KEY:sk-dummy-key-for-dev}
spring.ai.openai.chat.options.model=gpt-4o-mini

springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs

logging.level.com.ota.travi=DEBUG
```

### `application-prod.properties`

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}
spring.data.redis.password=${REDIS_PASSWORD}

springdoc.swagger-ui.enabled=false

logging.level.root=WARN
logging.level.com.ota.travi=INFO
```

### JWT Token Configuration

| Tham số | Giá trị | Ghi chú |
|---|---|---|
| Access Token TTL | 15 phút (900 000 ms) | |
| Refresh Token TTL | 7 ngày (604 800 000 ms) | |
| OTP TTL | 4 phút (Redis) | REGISTER & RESET_PASSWORD |
| Algorithm | HS256 | HMAC SHA-256 |

### Database Tables (Flyway `v1__init_schema.sql`)

| Table | Mô tả |
|---|---|
| `vai_tro` | Roles |
| `users` | Parent (JOINED) |
| `khach_hang` | Customers |
| `doi_tac` | Partners |
| `quan_tri_vien` | Admins |
| `danh_muc_so_thich` | Preference categories |
| `so_thich` | User preferences |
| `khach_hang_so_thich` | M2M Customer ↔ Preferences |
| `lich_su_thao_tac` | Action logs |

---

## 9. Cách chạy backend

### Yêu cầu

- **Java 21+** | **Maven 3.8.1+** | **PostgreSQL 12+** | **Redis 7.0+**
- **Gmail App Password** (Gmail → Bảo mật → 2FA → App Passwords)

### Development (Maven)

```powershell
cd backend

$env:JWT_SECRET_KEY="your-secret-key-at-least-32-chars-long"
$env:JWT_EXPIRATION_TIME="900000"
$env:MAIL_USERNAME="your-gmail@gmail.com"
$env:MAIL_PASSWORD="your-gmail-app-password"
$env:OPENAI_API_KEY="sk-your-openai-key"
$env:FRONTEND_URL="http://localhost:5173"

mvn spring-boot:run
```

> Swagger UI: **http://localhost:8080/swagger-ui.html**

### Docker

```bash
docker build -f Dockerfile -t travi-backend:latest .

docker run -d \
  --name travi-backend \
  -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://postgres:5432/travi_db" \
  -e MAIL_USERNAME="your-gmail@gmail.com" \
  -e MAIL_PASSWORD="your-app-password" \
  -e JWT_SECRET_KEY="your-secret-key" \
  --network travi-network \
  travi-backend:latest
```

### Build & Test

```powershell
mvn compile -DskipTests        # Build nhanh
mvn test                       # Chạy unit tests
mvn clean package -DskipTests  # Build JAR
```

### Kiểm tra API (cURL)

```bash
# Đăng ký
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user123","email":"user@example.com","hoTen":"Nguyễn Văn A",
       "soDienThoai":"0912345678","matKhau":"Password@123","loaiTaiKhoan":"KHACH_HANG"}'

# Xác minh OTP
curl -X PUT http://localhost:8080/api/v1/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","confirmOTP":"123456"}'

# Gửi lại OTP
curl -X POST http://localhost:8080/api/v1/auth/resend-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com"}'

# Đăng nhập
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","matKhau":"Password@123"}'

# Đăng nhập Google
curl -X POST http://localhost:8080/api/v1/auth/google \
  -H "Content-Type: application/json" \
  -d '{"idToken":"<google-id-token>","loaiTaiKhoan":"KHACH_HANG"}'

# Quên mật khẩu — bước 1
curl -X POST http://localhost:8080/api/v1/auth/forgot-password/request-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com"}'

# Quên mật khẩu — bước 2
curl -X POST http://localhost:8080/api/v1/auth/forgot-password/verify-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","confirmOTP":"123456"}'

# Quên mật khẩu — bước 3
curl -X POST http://localhost:8080/api/v1/auth/forgot-password/reset \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","matKhauMoi":"NewPass@456","xacNhanMatKhau":"NewPass@456"}'

# Làm mới token
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refreshToken>"}'

# Đăng xuất
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refreshToken>"}'
```

---

## 10. Trạng thái hoàn thiện

### ✅ Đã xong (Sprint 1)

**Authentication & Security**
- [x] JWT generation & validation (Access + Refresh)
- [x] JWT Authentication Filter
- [x] Spring Security configuration
- [x] Rate Limiting (5 req/min — Bucket4j)
- [x] Token Blacklist (Redis)
- [x] CORS configuration
- [x] Password encoding (BCrypt)
- [x] Role-based access control

**User Management**
- [x] Đăng ký tài khoản
- [x] Xác minh email OTP
- [x] Gửi lại OTP
- [x] Đăng nhập email + password
- [x] Đăng nhập / đăng ký Google OAuth2
- [x] Quên mật khẩu (3 bước OTP)
- [x] Đăng xuất + Token blacklist
- [x] Làm mới Access Token
- [x] Async email OTP (@Async)

**Database**
- [x] PostgreSQL + JPA/Hibernate (JOINED inheritance)
- [x] KhachHang, DoiTac, QuanTriVien entities
- [x] VaiTro entity (Role/Permission)
- [x] OtpPurpose enum (REGISTER / RESET_PASSWORD)
- [x] Flyway migrations
- [x] DataInitializer (seed Role khi khởi động)

**Infrastructure**
- [x] Swagger/OpenAPI docs
- [x] Application profiles (dev, prod, test)
- [x] Docker support
- [x] Maven build
- [x] Spring Boot Actuator
- [x] Unit tests (AuthServiceTest)
- [x] DevTools (hot reload)

### ⏳ Sprint 2

- [ ] Hotel entity & Search API (`/api/v1/public/rooms/search`)
- [ ] Restaurant entity & Menu management
- [ ] Booking System & Payment integration
- [ ] `/api/v1/user/**` — User dashboard APIs
- [ ] `/api/v1/partner/**` — Partner dashboard APIs
- [ ] `/api/v1/admin/**` — Admin dashboard APIs
- [ ] User locking/unlocking by admin

### 📋 Sprint 3+

- [ ] Payment gateway (VNPay, Stripe)
- [ ] Notification (Email, SMS, Push)
- [ ] Real-time chat (WebSocket)
- [ ] AI recommendations (Spring AI)
- [ ] Two-factor authentication (2FA)
- [ ] Advanced analytics

---

## 11. Kiến trúc bảo mật

### Authentication Flow

```
Client
  │ POST /api/v1/auth/login (email + password)
  ▼
RateLimitFilter  ──────────────────── >5 req/min → HTTP 429
  │
  ▼
JwtAuthenticationFilter (bypass /auth/*)
  │
  ▼
AuthenticationManager
  ├─ loadUserByUsername(email)
  ├─ BCrypt password check
  └─ isEnabled() = (trangThai == HOAT_DONG)
  │
  ▼
JwtUtil.generateToken()       → Access Token (15 phút)
JwtUtil.generateRefreshToken() → Refresh Token (7 ngày)
  │
  ▼
Client → store tokens (localStorage)
```

### Authorization Flow

```
Client  Authorization: Bearer <token>
  │
  ▼
JwtAuthenticationFilter
  ├─ Extract JWT
  ├─ Validate signature + expiry
  ├─ Check Redis blacklist
  └─ Set SecurityContextHolder
  │
  ▼
HttpSecurity.authorize() → Check role rules → Controller
```

### Google OAuth2 Flow

```
Frontend (Google Identity Services popup)
  │ Nhận ID Token từ Google
  ▼
POST /api/v1/auth/google { idToken, loaiTaiKhoan }
  │
  ▼
GoogleTokenVerifierService.verifyIdToken(idToken)
  │ Google API → { email, name }
  ▼
AuthService.loginWithGoogle()
  ├─ Email mới?         → Tạo user (HOAT_DONG ngay)
  ├─ CHUA_XAC_THUC?    → Kích hoạt tài khoản
  └─ HOAT_DONG?        �� Đăng nhập bình thường
  │
  ▼
Sinh JWT → Trả về { accessToken, refreshToken, tokenType, message }
```

### Forgot Password Flow

```
[Bước 1] POST /forgot-password/request-otp  { email }
  → Tạo OTP → Redis (purpose=RESET_PASSWORD, TTL 4 phút)
  → Gửi email async

[Bước 2] POST /forgot-password/verify-otp  { email, confirmOTP }
  → Kiểm tra OTP Redis
  → Đúng → đánh dấu verified session trong Redis

[Bước 3] POST /forgot-password/reset  { email, matKhauMoi, xacNhanMatKhau }
  → Kiểm tra verified session còn hiệu lực
  → BCrypt encode mật khẩu mới → lưu DB
  → Xóa verified session khỏi Redis
```

---

## 12. Ghi chú kỹ thuật

1. **OtpPurpose enum**: Phân biệt OTP đăng ký (`REGISTER`) và OTP quên mật khẩu (`RESET_PASSWORD`) trong Redis key — tránh nhầm lẫn giữa 2 luồng
2. **Google OAuth**: Backend chỉ nhận và verify ID Token — không tự mở popup. Toàn bộ Google flow UI nằm ở frontend (`GoogleAuthButton.tsx`)
3. **Async email**: `@Async` → email gửi nền, lỗi email không fail request chính
4. **JOINED inheritance**: Một `save()` insert cả `users` lẫn `khach_hang`/`doi_tac` trong cùng transaction
5. **DataInitializer**: Seed 3 role vào `vai_tro` nếu chưa tồn tại → chạy mỗi lần app khởi động
6. **Token Blacklist TTL**: = thời gian còn lại của token → Redis tự xóa, không lãng phí bộ nhớ
7. **Flyway**: Tự động chạy migration khi Spring Boot khởi động

---

## 13. Kết luận

TraVi-OTA backend Sprint 1 đã hoàn thiện **toàn bộ lớp Identity & Authentication**:

| Tính năng | Trạng thái |
|---|---|
| Đăng ký + OTP email | ✅ |
| Xác minh email + kích hoạt | ✅ |
| Gửi lại OTP | ✅ |
| Đăng nhập email/password + JWT | ✅ |
| Đăng nhập Google OAuth2 | ✅ |
| Qu��n mật khẩu (3 bước OTP) | ✅ |
| Refresh Token | ✅ |
| Logout + Token Blacklist | ✅ |
| Rate Limiting (Bucket4j) | ✅ |
| Spring Security + CORS | ✅ |
| Swagger/OpenAPI docs | ✅ |
| Docker + multi-env config | ✅ |
| Unit Tests | ✅ |

**Backend đã sẵn sàng để phát triển các module Sprint 2** (Hotel, Restaurant, Booking) — chỉ cần thêm entity/service/endpoint mới, Spring Security tự bảo vệ theo cấu hình role hiện có.

---

**Last Updated:** May 2026  
**Version:** 1.0  
**Status:** Sprint 1 Complete ✅

