# TraVi-OTA Backend

Backend hiện tại của dự án **TraVi-OTA** được xây dựng bằng **Spring Boot** và tập trung vào các phần nền tảng quan trọng: xác thực người dùng, đăng ký tài khoản, gửi OTP qua email, xác minh OTP, đăng nhập bằng JWT, và phân quyền theo role.

## 1. Mục tiêu của backend hiện tại

Backend đang hoàn thiện các khối chức năng cốt lõi để hỗ trợ frontend và các module nghiệp vụ phía sau:

- Đăng ký tài khoản mới
- Gửi OTP xác minh qua email
- Xác minh OTP do người dùng nhập
- Kích hoạt tài khoản sau khi xác minh thành công
- Đăng nhập và phát hành JWT
- Bảo vệ API bằng Spring Security
- Phân quyền theo role: `QUAN_TRI_VIEN`, `DOI_TAC`, `KHACH_HANG`

---

## 2. Công nghệ đang dùng

- **Java 21**
- **Spring Boot 4.0.5**
- **Spring Web / WebMVC**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** (with Hibernate JOINED inheritance strategy)
- **Spring Data Redis** (OTP storage, Token blacklist)
- **Spring Mail** (SMTP Gmail)
- **JWT** (`jjwt 0.11.5`)
- **PostgreSQL**
- **H2** (test database)
- **Swagger/OpenAPI 3.0** (API Documentation)
- **Lombok**
- **Spring AI 2.0.0-M4** (OpenAI integration)
- **Bucket4j 8.10.1** (Rate Limiting / Anti-spam)
- **Flyway** (Database migrations)

---

## 3. Cấu trúc backend hiện tại

```text
backend/
├── src/main/java/com/ota/travi
│   ├── config/           # Cấu hình app, security, async, CORS
│   │   ├── AppConfig.java              # Password encoder, RestTemplate
│   │   ├── AsyncConfig.java            # @EnableAsync cho email async
│   │   └── SecurityConfig.java         # Spring Security, JWT filter, CORS
│   ├── constant/         # Hằng số dùng chung
│   │   └── ApiEndpoints.java           # Registry các endpoint auth đang triển khai
│   ├── controller/       # REST API endpoints
│   │   └── AuthController.java         # Register, Login, Logout, Refresh, Verify OTP
│   ├── dto/              # Request/Response models
│   │   ├── AuthResponse.java           # {accessToken, refreshToken, tokenType, message}
│   │   ├── LoginRequest.java           # {email, matKhau}
│   │   ├── RegisterRequest.java        # {username, email, hoTen, soDienThoai, matKhau, loaiTaiKhoan}
│   │   ├── VerifyOtpRequest.java       # {email, confirmOTP}
│   │   ├── RefreshTokenRequest.java    # {refreshToken}
│   │   └── UserDto.java                # User representation
│   ├── entity/           # JPA entities (Database models)
│   │   ├── User.java                   # Abstract parent class (JOINED inheritance)
│   │   ├── KhachHang.java              # Customer (KHACH_HANG)
│   │   ├── DoiTac.java                 # Partner (DOI_TAC)
│   │   ├── QuanTriVien.java            # Admin (QUAN_TRI_VIEN)
│   │   ├── VaiTro.java                 # Role/Permission
│   │   ├── SoThich.java                # Preferences
│   │   ├── DanhMucSoThich.java         # Preference categories
│   │   └── LichSuThaoTac.java          # Action history logs
│   ├── enum/             # Enumerations
│   │   ├── TrangThaiUser.java          # User status: CHUA_XAC_THUC, HOAT_DONG, BI_KHOA
│   │   ├── GioiTinh.java               # Gender: NAM, NU, KHAC
│   │   └── HangThanhVien.java          # Member tier: DONG, BAC, VANG, KIM_CUONG
│   ├── repository/       # Spring Data JPA repositories
│   │   └── UserRepository.java         # User CRUD operations
│   ├── security/         # Authentication & Authorization
│   │   ├── JwtUtil.java                # JWT token generation, validation, extraction
│   │   ├── JwtAuthenticationFilter.java # Intercept requests, extract JWT, set authentication
│   │   ├── CustomUserDetails.java      # User details implementation
│   │   ├── CustomUserDetailsService.java# Load user from DB by email/username
│   │   └── RateLimitFilter.java        # Rate limiting for login (5 req/min)
│   ├── service/          # Business logic
│   │   ├── AuthService.java            # Register, Login, Verify OTP
│   │   ├── OTPService.java             # OTP generation, validation, email sending (@Async)
│   │   └── TokenBlacklistService.java  # Token blacklist for logout
│   ├── util/             # Utilities
│   ├── ai/               # AI module (for future use)
│   └── TraViOtaApplication.java        # @SpringBootApplication entry point
│
├── src/main/resources
│   ├── application.properties           # Default profile config
│   ├── application-dev.properties       # Dev environment (PostgreSQL local)
│   ├── application-prod.properties      # Production environment
│   ├── application-test.properties      # Test environment (H2)
│   └── db/migration/
│       └── v1__init_schema.sql          # Initial database schema (Flyway)
│
└── pom.xml                              # Maven dependencies & plugins
```

---

## 4. Các phần đã làm được ở backend hiện tại

### 4.1. Đăng ký tài khoản (Register)

Backend có luồng đăng ký tài khoản mới trong `AuthService`.

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

**Luồng xử lý:**
```
1. Kiểm tra trùng username
2. Kiểm tra trùng email
3. Tạo entity theo loại tài khoản:
   - KHACH_HANG: khởi tạo diemThanhVien=0, hangThanhVien=DONG
   - DOI_TAC: khởi tạo tiLeChietKhau=0.0
4. Gán trạng thái tài khoản = CHUA_XAC_THUC
5. Mã hóa mật khẩu bằng BCrypt
6. Lưu user xuống PostgreSQL
7. Sau transaction commit, gửi OTP qua email (async)
```

### 4.2. Gửi OTP xác minh email

`OTPService` đảm nhiệm:
- Tạo OTP ngẫu nhiên 6 chữ số (000000-999999)
- Lưu OTP vào **Redis** với TTL **4 phút**
- Gửi OTP qua email bằng `@Async` (không chặn request)
- Xóa OTP từ Redis khi xác minh thành công

**Email Template:**
```
Xin chào <HỌ TÊN>,

Mã xác minh tạo tài khoản TraVi của bạn là:

<OTP-CODE>

Mã OTP có hiệu lực trong vòng 4 phút.

Nếu bạn không yêu cầu, vui lòng bỏ qua email này.

Trân trọng!
```

### 4.3. Xác minh OTP (Verify Email)

**Endpoint:** `PUT /api/v1/auth/verify-email`

**Request Body:**
```json
{
  "email": "user@example.com",
  "confirmOTP": "123456"
}
```

**Luồng xử lý:**
```
1. Kiểm tra OTP rỗng → "OTP đã hết hạn"
2. Kiểm tra OTP sai → "OTP không hợp lệ"
3. Nếu OTP đúng:
   - Xóa OTP khỏi Redis
   - Cập nhật trạng thái user → HOAT_DONG
   - Trả về: "Xác minh OTP thành công. Tài khoản đã được kích hoạt."
```

### 4.4. Đăng nhập bằng JWT

**Endpoint:** `POST /api/v1/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "matKhau": "Password@123"
}
```

**Luồng xử lý:**
```
1. Spring Security kiểm tra email + mật khẩu
2. CustomUserDetailsService load user từ DB
3. isEnabled() kiểm tra: chỉ user HOAT_DONG mới đăng nhập được
4. Nếu hợp lệ → sinh 2 token:
   - Access Token (15 phút)
   - Refresh Token (7 ngày)
5. Trả về: {accessToken, refreshToken, tokenType: "Bearer", message}
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

### 4.5. Làm mới Token (Refresh)

**Endpoint:** `POST /api/v1/auth/refresh`

**Request Body:**
```json
{
  "refreshToken": "eyJhbGc..."
}
```

**Luồng xử lý:**
```
1. Kiểm tra Refresh Token có bị blacklist (đăng xuất từ trước)?
2. Nếu bị blacklist → "Thẻ đã bị vô hiệu hóa (Đăng xuất)."
3. Nếu hợp lệ:
   - Lấy username từ Refresh Token
   - Load user mới nhất từ DB (kiểm tra quyền/khóa)
   - Sinh Access Token mới
   - Trả về: {newAccessToken, refreshToken (cũ hoặc mới), tokenType, message}
```

### 4.6. Đăng xuất (Logout)

**Endpoint:** `POST /api/v1/auth/logout`

**Request Body (optional):**
```json
{
  "refreshToken": "eyJhbGc..."
}
```

**Luồng xử lý:**
```
1. Lấy JWT từ Authorization Header (Bearer token)
2. Tính thời gian còn lại của JWT
3. Ném JWT vào danh sách đen (Redis blacklist)
4. Nếu có Refresh Token trong body, cũng ném vào blacklist
5. Tokens sẽ tự động xóa khỏi Redis khi hết hạn
6. Trả về: "Đăng xuất thành công!"
```

### 4.7. Phân quyền và bảo vệ API (Authorization)

`SecurityConfig` cấu hình tiếp cận:

| Route | Quyền | Mô tả |
|---|---|---|
| `/api/v1/auth/**` | Public | Đăng ký, Login, Logout, Verify OTP, Refresh |
| `/api/hotels/public/**` | Public | Tìm kiếm khách sạn (chưa làm) |
| `/api/restaurants/public/**` | Public | Tìm kiếm nhà hàng (chưa làm) |
| `/api/admin/**` | QUAN_TRI_VIEN | Dashboard quản trị (chưa làm) |
| `/api/partner/**` | DOI_TAC | Dashboard đối tác (chưa làm) |
| Các API khác | Authenticated | Bắt buộc có JWT hợp lệ |

### 4.8. Bảo mật bổ sung

#### a) Rate Limiting (Chống Brute-force)
- Giới hạn **5 requests/phút** cho `/api/v1/auth/login`
- Trả về HTTP 429 (Too Many Requests) nếu vượt quá
- Sử dụng **Bucket4j** library

#### b) Token Blacklist
- Khi logout, token được lưu vào **Redis blacklist**
- Thời gian lưu = thời gian còn lại của token
- Tự động xóa khỏi Redis khi token hết hạn
- Ngăn chặn sử dụng lại token đã logout

#### c) CORS (Cross-Origin Resource Sharing)
- Chỉ cho phép requests từ `${FRONTEND_URL}` (mặc định: http://localhost:5173)
- Methods: GET, POST, PUT, DELETE, OPTIONS
- Headers: Authorization, Content-Type
- Credentials: true

#### d) Tài khoản chưa xác thực không đăng nhập được
- `CustomUserDetails.isEnabled()` kiểm tra trạng thái
- Chỉ user có `trangThai = HOAT_DONG` mới đăng nhập được
- User đăng ký xong nhưng chưa verify OTP → chưa thể đăng nhập

### 4.9. Entity Relationships (Database Schema)

```
User (Abstract parent - JOINED inheritance)
├── KhachHang (Customer)
│   └── many SoThich (Preferences)
├── DoiTac (Partner)
└── QuanTriVien (Admin)

All Users have:
├── VaiTro (Role) - many-to-one
└── LichSuThaoTac (History logs)
```

### 4.10. Trạng thái người dùng

| Trạng thái | Mô tả |
|---|---|
| CHUA_XAC_THUC | Vừa đăng ký, chờ verify OTP |
| HOAT_DONG | Đã kích hoạt, có thể đăng nhập |
| BI_KHOA | Bị admin khóa (chưa làm) |

---

## 5. Quy trình nghiệp vụ chi tiết

### 5.1. Đăng ký tài khoản

**Endpoint:** `POST /api/v1/auth/register`

**Luồng xử lý:**
```
Client gửi thông tin đăng ký
→ AuthController.register(@Valid RegisterRequest)
→ AuthService.register()
    ├─ Kiểm tra trùng username
    ├─ Kiểm tra trùng email
    ├─ Tạo entity KhachHang hoặc DoiTac dựa trên loaiTaiKhoan
    ├─ Gán trạng thái CHUA_XAC_THUC
    ├─ Mã hóa mật khẩu (BCrypt)
    └─ Lưu vào PostgreSQL
→ [After commit] OTPService.sendVerificationRegister() (@Async)
    ├─ Tạo OTP 6 số
    ├─ Lưu vào Redis (TTL: 4 phút)
    └─ Gửi OTP qua email SMTP Gmail
→ Trả về: "Đăng ký thành công..."
```

### 5.2. Verify OTP

**Endpoint:** `PUT /api/v1/auth/verify-email`

**Luồng xử lý:**
```
Client gửi email + OTP
→ AuthController.verifyEmail(@Valid VerifyOtpRequest)
→ AuthService.verifyRegisterOtp(email, otp)
    ├─ Tìm user qua email
    └─ OTPService.checkOtp(email, confirmOTP)
        ├─ Kiểm tra OTP rỗng → "OTP đã hết hạn"
        ├─ Kiểm tra OTP sai → "OTP không hợp lệ"
        └─ OTP đúng → Delete khỏi Redis
→ Update user.trangThai → HOAT_DONG
→ Trả về: "Xác minh OTP thành công..."
```

### 5.3. Đăng nhập

**Endpoint:** `POST /api/v1/auth/login`

**Luồng xử lý:**
```
Client gửi email + mật khẩu
→ RateLimitFilter kiểm tra
    └─ Nếu >5 lần/phút → HTTP 429
→ AuthController.login(@Valid LoginRequest)
→ AuthService.login()
    ├─ AuthenticationManager.authenticate()
    │   ├─ CustomUserDetailsService.loadUserByUsername()
    │   ├─ Kiểm tra mật khẩu (PasswordEncoder)
    │   └─ custom.isEnabled() = (user.trangThai == HOAT_DONG)
    ├─ JwtUtil.generateToken() → Access Token (15 phút)
    ├─ JwtUtil.generateRefreshToken() → Refresh Token (7 ngày)
    └─ Trả về AuthResponse
```

### 5.4. Refresh Token

**Endpoint:** `POST /api/v1/auth/refresh`

**Luồng xử lý:**
```
Client gửi refreshToken
→ AuthController.refreshToken(@Valid RefreshTokenRequest)
→ TokenBlacklistService.isBlacklisted(token)?
    └─ Có → Người dùng đã logout, trả lỗi
→ JwtUtil.extractUsername(refreshToken) → Lấy tên user
→ CustomUserDetailsService.loadUserByUsername()
    ├─ Verify user chưa bị khóa/đổi quyền/hết hạn
    └─ Lấy role hiện tại
→ JwtUtil.generateToken() → Access Token mới (15 phút)
→ Trả về: {newAccessToken, refreshToken (cũ), tokenType, message}
```

### 5.5. Đăng xuất

**Endpoint:** `POST /api/v1/auth/logout`

**Luồng xử lý:**
```
Client gửi request (Authorization header + optional refreshToken)
→ AuthController.logout()
→ TokenBlacklistService.addToBlacklist(accessToken, remainingMs)
→ TokenBlacklistService.addToBlacklist(refreshToken, remainingMs) [nếu có]
→ Redis tự động xóa token khi hết hạn
→ Trả về: "Đăng xuất thành công!"
```

---

## 6. REST API Endpoints

| Method | Endpoint | Xác thực | Quyền | Trạng thái | Mô tả |
|---|---|---|---|---:|---|
| POST | `/api/v1/auth/register` | ❌ | - | ✅ | Đăng ký tài khoản mới |
| PUT | `/api/v1/auth/verify-email` | ❌ | - | ✅ | Xác minh OTP và kích hoạt tài khoản |
| POST | `/api/v1/auth/login` | ❌ | - | ✅ | Đăng nhập và lấy JWT (Access + Refresh) |
| POST | `/api/v1/auth/logout` | ✅ | - | ✅ | Đăng xuất (blacklist token) |
| POST | `/api/v1/auth/refresh` | ❌ | - | ✅ | Làm mới Access Token bằng Refresh Token |

> Hiện tại backend mới triển khai nhóm `auth`. Các route `user`, `partner`, `admin` và hệ thống tích hợp sẽ được bổ sung khi có controller/service tương ứng.

> ✅ = Đã hoàn thiện  
> ❌ = Không cần đăng nhập / public

---

## 7. Các Service chính

### `AuthService` (com.ota.travi.service)
- `register(RegisterRequest)` → Đăng ký tài khoản mới
- `login(AuthenticationManager, JwtUtil, LoginRequest)` → Đăng nhập và phát hành 2 token
- `verifyRegisterOtp(email, otp)` → Xác minh OTP và kích hoạt tài khoản
- Kiểm tra trùng username/email
- Gán role, trạng thái người dùng
- Xử lý entity inheritance (KhachHang vs DoiTac)

### `OTPService` (com.ota.travi.service)
- `createOtp(email)` → Tạo OTP 6 chữ số ngẫu nhiên
- `checkOtp(email, confirmOTP)` → Xác minh OTP (throw RuntimeException nếu sai/hết hạn)
- `sendVerificationRegister(user)` → Gửi email OTP **bất đồng bộ** (@Async)
- Lưu OTP vào Redis (TTL: 4 phút)
- Xóa OTP khỏi Redis sau khi xác minh thành công

### `TokenBlacklistService` (com.ota.travi.service)
- `addToBlacklist(token, expirationDurationMs)` → Ném token vào danh sách đen (Redis)
- `isBlacklisted(token)` → Kiểm tra token có bị logout hay không
- Token tự động xóa khỏi Redis khi hết hạn (dùng TTL)

### `CustomUserDetailsService` (com.ota.travi.security)
- `loadUserByUsername(email)` → Load user từ DB theo email để phục vụ đăng nhập
- `loadUserByUsernameValue(username)` → Load user theo username để phục vụ JWT refresh / subject
- Trả về `CustomUserDetails` chứa:
    - username, email, password (hashed)
    - isEnabled = (trangThai == HOAT_DONG)
    - authorities = [role từ VaiTro]

### `JwtUtil` (com.ota.travi.security)
- `generateToken(username, role)` → Tạo Access Token (15 phút)
- `generateRefreshToken(username)` → Tạo Refresh Token (7 ngày)
- `extractUsername(token)` → Lấy username từ JWT
- `validateToken(token)` → Xác minh JWT hợp lệ
- `getRemainingExpirationTime(token)` → Tính thời gian còn lại

### `JwtAuthenticationFilter` (com.ota.travi.security)
- Chặn mỗi HTTP request
- Lấy JWT từ Authorization header (`Bearer <token>`)
- Xác minh signature + expiration
- Kiểm tra token có trong blacklist không
- Set `Authentication` vào `SecurityContextHolder`

### `RateLimitFilter` (com.ota.travi.security)
- Sử dụng **Bucket4j** để giới hạn request
- Giới hạn: **5 requests/phút** cho `/api/v1/auth/login`
- Trả về HTTP 429 nếu vượt quá
- Bảo vệ chống brute-force attack

---

## 8. Cấu hình Chi tiết

### Biến môi trường bắt buộc

```bash
# JWT Configuration
JWT_SECRET_KEY=your-secret-key-at-least-32-chars-long
JWT_EXPIRATION_TIME=900000  # 15 phút (ms)

# Frontend URL (CORS)
FRONTEND_URL_ENV=http://localhost:5173

# SMTP Gmail (Email OTP)
MAIL_USERNAME=your-gmail@gmail.com
MAIL_PASSWORD=your-gmail-app-password  # Use App Password, not account password

# OpenAI (cho AI module)
OPENAI_API_KEY=sk-your-openai-key
```

### `application.properties` (mặc định)

```properties
spring.profiles.active=dev
spring.application.name=TraVi-OTA
server.port=8080

SECRET_KEY=${JWT_SECRET_KEY}
JWT_EXPIRATION=${JWT_EXPIRATION_TIME}
FRONTEND_URL=${FRONTEND_URL_ENV:http://localhost:5173}
```

### `application-dev.properties` (Development - PostgreSQL địa phương)

```properties
# Database
spring.datasource.url=jdbc:postgresql://postgres:5432/travi_db
spring.datasource.username=postgres
spring.datasource.password=Lumitap2026

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Email (SMTP Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

# Spring AI / OpenAI
spring.ai.openai.api-key=${OPENAI_API_KEY:sk-dummy-key-for-dev}
spring.ai.openai.chat.options.model=gpt-4o-mini
spring.ai.openai.chat.options.temperature=0.7

# Swagger/Springdoc
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs

# Logging
logging.level.root=INFO
logging.level.com.ota.travi=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### `application-test.properties` (Testing - H2 in-memory)

```properties
# H2 embedded database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### `application-prod.properties` (Production)

```properties
# Database (từ biến môi trường)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA
spring.jpa.hibernate.ddl-auto=validate  # Chỉ validate, không tự tạo
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Redis
spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}
spring.data.redis.password=${REDIS_PASSWORD}

# Email
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

# Spring AI
spring.ai.openai.api-key=${OPENAI_API_KEY}

# Disable Swagger in production
springdoc.swagger-ui.enabled=false

# Logging
logging.level.root=WARN
logging.level.com.ota.travi=INFO
```

### JWT Token Configuration

| Tham số | Giá trị | Mô tả |
|---|---|---|
| Access Token TTL | 15 phút (900 tế giây) | Thời gian sống của access token |
| Refresh Token TTL | 7 ngày (604800 tế giây) | Thời gian sống của refresh token |
| Signature Algorithm | HS256 | HMAC SHA-256 |
| Secret Key | Min 32 chars | Mã hóa JWT |

### Database Schema (Flyway v1__init_schema.sql)

**Tables:**
- `vai_tro` - Roles (KHACH_HANG, DOI_TAC, QUAN_TRI_VIEN)
- `users` - Parent table (JOINED inheritance)
- `khach_hang` - Customers
- `doi_tac` - Partners
- `quan_tri_vien` - Admins
- `danh_muc_so_thich` - Preference categories
- `so_thich` - User preferences
- `khach_hang_so_thich` - M2M: Customer preferences
- `lich_su_thao_tac` - Admin action logs

**Constraints:**
- Email, Username UNIQUE
- User states: CHUA_XAC_THUC, HOAT_DONG, BI_KHOA
- Customer tiers: DONG, BAC, VANG, KIM_CUONG
- Gender: NAM, NU, KHAC

---

## 9. Cách chạy Backend

### Yêu cầu

- **Java 21** or higher
- **Maven 3.8.1** or higher
- **PostgreSQL 12** or higher (Dev mode)
- **Redis 7.0** or higher (OTP & Token blacklist)
- **SMTP Gmail account** (Send OTP emails)

### Chạy bằng Maven (Development)

```bash
# Navigate to backend folder
cd D:\HKVI\Project-TraVi-OTA\backend

# Set environment variables (PowerShell)
$env:JWT_SECRET_KEY="your-secret-key-at-least-32-chars-long"
$env:JWT_EXPIRATION_TIME="900000"
$env:MAIL_USERNAME="your-gmail@gmail.com"
$env:MAIL_PASSWORD="your-gmail-app-password"
$env:OPENAI_API_KEY="sk-your-openai-key"
$env:FRONTEND_URL="http://localhost:5173"

# Run Spring Boot application
mvn spring-boot:run

# Access Swagger UI: http://localhost:8080/swagger-ui.html
```

### Chạy với Docker

```bash
# Build Docker image
docker build -f Dockerfile -t travi-backend:latest .

# Run container (link với PostgreSQL & Redis containers)
docker run -d \
  --name travi-backend \
  -p 8080:8080 \
  -e DB_URL="jdbc:postgresql://postgres:5432/travi_db" \
  -e MAIL_USERNAME="your-gmail@gmail.com" \
  -e MAIL_PASSWORD="your-gmail-app-password" \
  -e JWT_SECRET_KEY="your-secret-key" \
  --network travi-network \
  travi-backend:latest
```

### Build kiểm tra (không chạy tests)

```powershell
cd D:\HKVI\Project-TraVi-OTA\backend
mvn compile -DskipTests
```

### Chạy Unit Tests

```powershell
cd D:\HKVI\Project-TraVi-OTA\backend
mvn test
```

### Clean & Build Package

```powershell
cd D:\HKVI\Project-TraVi-OTA\backend
mvn clean package -DskipTests
```

### Kiểm tra API bằng cURL

```bash
# 1. Register
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user123",
    "email": "user@example.com",
    "hoTen": "Nguyễn Văn A",
    "soDienThoai": "0912345678",
    "matKhau": "Password@123",
    "loaiTaiKhoan": "KHACH_HANG"
  }'

# 2. Verify OTP (sau khi nhận email)
curl -X PUT http://localhost:8080/api/v1/auth/verify-email \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "confirmOTP": "123456"
  }'

# 3. Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "matKhau": "Password@123"
  }'

# 4. Use Access Token
curl -X GET http://localhost:8080/api/protected-route \
  -H "Authorization: Bearer <accessToken>"

# 5. Refresh Token
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "<refreshToken>"}'

# 6. Logout
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "<refreshToken>"}'
```

---

## 10. Trạng thái hiện tại của Backend

### ✅ Đã hoàn thiện

#### Authentication & Security
- [x] JWT generation & validation (Access + Refresh tokens)
- [x] JWT Authentication Filter
- [x] Spring Security configuration
- [x] Rate Limiting (5 req/min cho login)
- [x] Token Blacklist (Redis)
- [x] CORS configuration (Frontend whitelist)
- [x] Password encoding (BCrypt)
- [x] Role-based access control (@PreAuthorize)

#### User Management
- [x] User registration (Register endpoint)
- [x] Email verification (OTP via Gmail)
- [x] OTP generation & validation (Redis TTL: 4 phút)
- [x] User account activation
- [x] Login endpoint (JWT token generation)
- [x] Logout endpoint (Token blacklist)
- [x] Refresh token endpoint
- [x] Async email sending (@Async)

#### Database
- [x] PostgreSQL integration (JPA/Hibernate)
- [x] User inheritance strategy (JOINED)
- [x] KhachHang entity (Customer)
- [x] DoiTac entity (Partner)
- [x] QuanTriVien entity (Admin)
- [x] VaiTro entity (Role/Permission)
- [x] Flyway migrations (v1__init_schema.sql)

#### Infrastructure
- [x] API endpoints (/api/auth/**)
- [x] Swagger/OpenAPI documentation
- [x] Application profiles (dev, prod, test)
- [x] Configuration management (environment variables)
- [x] Docker support (Dockerfile)
- [x] Maven build configuration
- [x] Spring Boot Actuator (health & metrics)
- [x] DevTools (hot reload)

### ⏳ Sắp làm (Sprint 2)

#### Hotel Management
- [ ] Hotel entity & repository
- [ ] Hotel search/filter API
- [ ] Hotel image management
- [ ] Hotel availability calendar
- [ ] `/api/hotels/public/**` endpoints

#### Restaurant Management
- [ ] Restaurant entity & repository
- [ ] Restaurant search/filter API
- [ ] Menu management
- [ ] Restaurant availability
- [ ] `/api/restaurants/public/**` endpoints

#### Booking System
- [ ] Booking entity
- [ ] Booking management API
- [ ] Payment integration
- [ ] Booking history

#### Admin Dashboard
- [ ] Admin panel API (`/api/admin/**`)
- [ ] User management
- [ ] Content moderation
- [ ] Analytics & reports

#### Partner Dashboard
- [ ] Partner panel API (`/api/partner/**`)
- [ ] Reservation management
- [ ] Revenue tracking
- [ ] Rating & reviews

### 📋 Future Enhancements (Sprint 3+)

- [ ] Payment gateway (Stripe, VNPay)
- [ ] Notification system (Email, SMS, Push)
- [ ] Real-time chat (WebSocket)
- [ ] AI recommendations (Spring AI)
- [ ] Dynamic pricing
- [ ] Advanced analytics
- [ ] Multi-language support
- [ ] Two-factor authentication (2FA)

## 11. Kiến trúc bảo mật

### Authentication Flow

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       │ 1. POST /api/auth/login
       │    (email + password)
       │
       ▼
┌─────────────────────────────┐
│  RateLimitFilter            │ ◄─ Kiểm tra: 5 req/min?
│  (Bucket4j)                 │
└──────┬──────────────────────┘
       │
       ▼
┌─────────────────────────────┐
│  JwtAuthenticationFilter    │ ◄─ Bypass: /api/auth/*
│  (Except /api/auth/*)       │
└──────┬──────────────────────┘
       │
       ▼
┌─────────────────────────────┐
│  AuthenticationManager      │ ◄─ DaoAuthenticationProvider
│                             │
│  1. Load user by email      │
│  2. Check password          │
│  3. Check isEnabled()       │
│     (trangThai == HOAT_DONG)
└──────┬──────────────────────┘
       │
       ▼
┌─────────────────────────────┐
│  Generate JWT Tokens        │ ◄─ JwtUtil
│  • accessToken (15 phút)    │
│  • refreshToken (7 ngày)    │
└──────┬──────────────────────┘
       │
       │ 2. Response with tokens
       │
       ▼
┌─────────────┐
│   Client    │ (Store tokens in localStorage/sessionStorage)
└─────────────┘
```

### Authorization Flow

```
┌─────────────┐
│   Client    │
│ Authorization: Bearer <token>
└──────┬──────┘
       │
       ▼
┌─────────────────────────────┐
│  RateLimitFilter            │ (Pass through)
└──────┬──────────────────────┘
       │
       ▼
┌─────────────────────────────┐
│  JwtAuthenticationFilter    │
│  1. Extract JWT from header │
│  2. Validate signature      │
│  3. Check expiration        │
│  4. Check blacklist (logout)│
│  5. Load user & authorities │
└──────┬──────────────────────┘
       │
       ▼ No errors
┌─────────────────────────────┐
│  SecurityContext            │
│  Set Authentication         │
└──────┬──────────────────────┘
       │
       ▼
┌─────────────────────────────┐
│  HttpSecurity.authorize()   │
│  1. Check @PreAuthorize     │
│  2. Check role requirements │
│  3. Match request route     │
└──────┬──────────────────────┘
       │
       ▼ Authorized
┌──────────────────┐
│  Controller API  │
└──────────────────┘
```

## 12. Ví dụ Request/Response

### Register Request
```http
POST /api/auth/register HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "username": "nguyenvana",
  "email": "nguyenvana@example.com",
  "hoTen": "Nguyễn Văn A",
  "soDienThoai": "0912345678",
  "matKhau": "Password@123456",
  "loaiTaiKhoan": "KHACH_HANG"
}
```

### Register Response (201 Created)
```json
"Đăng ký thành công tài khoản: nguyenvana. Vui lòng kiểm tra email để lấy OTP xác minh."
```

### Login Request
```http
POST /api/auth/login HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "email": "nguyenvana@example.com",
  "matKhau": "Password@123456"
}
```

### Login Response (200 OK)
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "message": "Đăng nhập thành công"
}
```

### Protected API Request
```http
GET /api/some-protected-endpoint HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 13. Ghi chú kỹ thuật quan trọng

1. **OTP TTL**: 4 phút (240 giây) trong Redis
2. **Access Token TTL**: 15 phút
3. **Refresh Token TTL**: 7 ngày
4. **Rate Limiting**: 5 requests/phút cho `/api/auth/login`
5. **CORS**: Chỉ cho phép từ `${FRONTEND_URL_ENV}`
6. **Database Inheritance**: JOINED strategy (user + khach_hang/doi_tac trong cùng transaction)
7. **Email**: Gửi async để không làm chậm request main
8. **Token Blacklist**: Tự động xóa khỏi Redis khi hết hạn (sử dụng TTL)
9. **isEnabled()**: Chỉ user HOAT_DONG mới đăng nhập được
10. **Flyway**: Tự động chạy migration khi ứng dụng khởi động

## 14. Kết luận

TraVi-OTA backend đã có một **nền tảng xác thực và bảo mật rất vững chắc**:

### Điểm mạnh
✅ JWT authentication với access + refresh tokens  
✅ OTP email verification (async, Redis)  
✅ Role-based access control (KHACH_HANG, DOI_TAC, QUAN_TRI_VIEN)  
✅ Rate limiting chống brute-force  
✅ Token blacklist cho logout an toàn  
✅ CORS configuration cho frontend  
✅ Database inheritance với JPA/Hibernate  
✅ Spring Security configuration đầy đủ  
✅ Swagger API documentation  
✅ Docker support & multi-environment config

### API Sẵn Sàng
Các API này đã sẵn sàng để sử dụng:
1. Register → Verify OTP → Activate Account
2. Login → 2 Tokens (Access + Refresh)
3. Protected APIs → JWT validation
4. Logout → Token blacklist
5. Refresh → New Access Token

**Backend hiện tại đã đủ để phát triển các module con (Hotel, Restaurant, Booking)** trong Sprint 2. Chỉ cần extend các service & thêm các endpoint mới, Spring Security sẽ tự động bảo vệ chúng.

---

**Last Updated:** May 2026  
**Version:** 1.0  
**Status:** Complete for Sprint 1 ✅

