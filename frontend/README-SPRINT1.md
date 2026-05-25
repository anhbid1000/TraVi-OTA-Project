# TraVi-OTA Frontend — Sprint 1

Ứng dụng Frontend cho nền tảng quản lý du lịch thông minh **TraVi-OTA**. Xây dựng với React 19, TypeScript, Vite và Tailwind CSS — tập trung hoàn thiện hệ thống Authentication đa vai trò trong Sprint 1.

---

## Tổng Quan

TraVi-OTA Frontend hỗ trợ ba loại người dùng:

- **Khách hàng (KHACH_HANG)** — Đặt phòng, đặt nhà hàng
- **Đối tác (DOI_TAC)** — Quản lý cơ sở lưu trú và dịch vụ
- **Quản trị viên (QUAN_TRI_VIEN)** — Quản lý toàn hệ thống

---

## 📦 Công Nghệ Sử Dụng

### Core

| Package | Version | Mục đích |
|---|---|---|
| React | 19.2.4 | UI framework |
| TypeScript | 6.0.2 | Type-safe JavaScript |
| Vite | 8.0.4 | Build tool (HMR, dev server) |

### Styling & UI

| Package | Version | Mục đích |
|---|---|---|
| Tailwind CSS | 4.2.2 | Utility-first CSS |
| Lucide React | 1.8.0 | Icon library |
| clsx + tailwind-merge | — | CSS class composition |

### Routing & State

| Package | Version | Mục đích |
|---|---|---|
| React Router DOM | 7.14.0 | Client-side routing |
| React Context API | — | Global Auth state |
| Axios | 1.15.0 | HTTP client với interceptor |

### Form & Validation

| Package | Version | Mục đích |
|---|---|---|
| React Hook Form | 7.72.1 | Form state management |
| Zod | 4.3.6 | Schema validation |

### Dev Tools

| Package | Version | Mục đích |
|---|---|---|
| ESLint | 9.39.4 | Linting |
| Prettier | 3.8.2 | Code formatter |
| PostCSS + Autoprefixer | 8.5.x | CSS processing |

---

## 🏗️ Kiến Trúc & Luồng Ứng Dụng

### Khởi Động Ứng Dụng

```
main.tsx (Entry Point)
    ↓
createRoot & render
    ↓
GoogleOAuthProviderWrapper  ← inject VITE_GOOGLE_CLIENT_ID vào <Script>
    ↓
AuthProvider (Global auth state)
    ↓
BrowserRouter (Routing)
    ↓
App (Routes & Pages)
```

### Authentication Flow

#### A. Đăng Nhập (Login)

```
Trang đăng nhập (/login, /partner/login, /admin/login)
    ↓
Nhập email + password → validate
    ↓
useAuth().login(payload)
    ↓
authService.login()  →  POST /v1/auth/login
    ↓
tokenStorage.setTokens() → lưu localStorage
    ↓
AuthProvider sync session
    ↓
Redirect theo role:
  QUAN_TRI_VIEN → /admin
  DOI_TAC       → /partner
  KHACH_HANG    → /
```

#### B. Đăng Ký (Register)

```
/register hoặc /partner/register
    ↓
Điền form: username, hoTen, email, soDienThoai, matKhau, confirmPassword
    ↓
Validate:
  - username: ≥4 ký tự, chỉ [a-zA-Z0-9_.]
  - hoTen: ≥2 ký tự
  - email: format hợp lệ
  - soDienThoai: (0|+84) + 9 chữ số
  - matKhau: 9–20 ký tự, 1 hoa, 1 thường, 1 số, 1 ký tự đặc biệt
  - confirmPassword: khớp với matKhau
    ↓
authService.register(payload)  →  POST /v1/auth/register
    ↓
Redirect → /verify-email (truyền email qua state)
    ↓
Nhập OTP từ email
    ↓
authService.verifyEmail()  →  PUT /v1/auth/verify-email
    ↓
Redirect → trang đăng nhập tương ứng
```

#### C. Đăng Nhập Google (OAuth2)

```
Click "Tiếp tục với Google" → Google Identity Services popup
    ↓
Google trả về ID Token cho frontend
    ↓
useAuth().loginWithGoogle({ idToken, loaiTaiKhoan })
    ↓
authService.loginWithGoogle()  →  POST /v1/auth/google
    ↓
Backend: verify ID Token, tạo/kích hoạt user, sinh JWT
    ↓
tokenStorage.setTokens() → lưu localStorage
    ↓
Redirect theo role
```

**Lưu ý:** Google Identity Services được khởi tạo bởi `GoogleOAuthProviderWrapper` inject script từ `VITE_GOOGLE_CLIENT_ID`. `GoogleAuthButton` render nút ẩn của Google và dùng button tự tạo để trigger popup — đảm bảo UI nhất quán với toàn bộ design system.

#### D. Quên Mật Khẩu

```
Click "Quên mật khẩu?" → /forgot-password (hoặc /partner/forgot-password)
    ↓
[Bước 1] Nhập email → authService.requestPasswordResetOtp()
    → POST /v1/auth/forgot-password/request-otp
    ↓
[Bước 2] Nhập OTP → authService.verifyPasswordResetOtp()
    → POST /v1/auth/forgot-password/verify-otp
    ↓
[Bước 3] Nhập mật khẩu mới → authService.resetPassword()
    → POST /v1/auth/forgot-password/reset
    ↓
Redirect → trang đăng nhập
```

`ForgotPasswordPage` quản lý cả 3 bước trong một component, chuyển đổi bằng `step` state (`request` → `verify` → `reset`).

#### E. Token Refresh (Auto)

```
Axios request interceptor thêm Authorization: Bearer <token>
    ↓
Response 401?
    ↓
YES → refreshAccessToken()
    → POST /v1/auth/refresh { refreshToken }
    → tokenStorage.setTokens() → retry request gốc
NO  → Response bình thường

Deduplication: nhiều request 401 đồng thời → chỉ gọi refresh 1 lần
```

#### F. Đăng Xuất

```
useAuth().logout()
    ↓
authService.logout()  →  POST /v1/auth/logout { refreshToken }
    ↓
tokenStorage.clearTokens()
    ↓
clearSession() → reset AuthProvider state
    ↓
Dispatch event 'auth:logout'
```

---

## 🛣️ Routing & Authorization

### Route Structure

```
/ (Home — Public)
├── /login                     (Customer Login)
├── /register                  (Customer Register)
├── /verify-email              (Email OTP Verification)
├── /forgot-password           (Customer Forgot Password)
├── /partner/login             (Partner Login)
├── /partner/register          (Partner Register)
├── /partner/forgot-password   (Partner Forgot Password)
├── /admin/login               (Admin Login)
├── /403                       (Forbidden Page)
├── Protected Routes (isAuthenticated required)
│   └── /payment               (Customer)
├── Admin Routes (QUAN_TRI_VIEN only)
│   └── /admin                 (Admin Dashboard)
├── Partner Routes (DOI_TAC only)
│   └── /partner               (Partner Dashboard)
└── * (404)
```

### Route Guards

| Guard | File | Điều kiện | Action khi fail |
|---|---|---|---|
| `ProtectedRoute` | `routes/ProtectedRoute.tsx` | `isAuthenticated` | Redirect `/login` (lưu previous path) |
| `RoleRoute` | `routes/RoleRoute.tsx` | `isAuthenticated` + role | Redirect `/403` hoặc `/login` |
| `AdminRoute` | `routes/AdminRoute.tsx` | role = `QUAN_TRI_VIEN` | Redirect `/403` |
| `StaffRoute` | `routes/StaffRoute.tsx` | role = `DOI_TAC` | Redirect `/403` |

### Route Guard Utilities (`routes/routeGuards.ts`)

```typescript
normalizeRole(role: string): RoleName
  // Xóa prefix "ROLE_", uppercase, trim

hasAllowedRole(user, allowedRoles): boolean
  // Kiểm tra role có trong danh sách

getDefaultPathByRole(role: string): string
  // QUAN_TRI_VIEN → /admin
  // DOI_TAC       → /partner
  // KHACH_HANG    → /
```

---

## 🔐 Authentication Context & State

### AuthContext (`contexts/authContext.ts`)

```typescript
type AuthContextValue = {
  user: AuthUser | null
  accessToken: string | null
  refreshToken: string | null
  isAuthenticated: boolean      // accessToken tồn tại & chưa hết hạn
  isLoading: boolean
  login: (payload: LoginRequest) => Promise<AuthResponse>
  loginWithGoogle: (payload: GoogleAuthRequest) => Promise<AuthResponse>
  logout: () => Promise<void>
  refreshSession: () => Promise<AuthResponse>
}
```

### AuthProvider (`contexts/AuthContext.tsx`)

**Khởi tạo:**
- Đọc tokens từ `localStorage` khi app mount
- Decode JWT lấy user info
- Kiểm tra expiry

**Methods:**
- `login(payload)` → `authService.login()` → `syncSessionFromStorage()`
- `loginWithGoogle(payload)` → `authService.loginWithGoogle()` → sync
- `logout()` → `authService.logout()` → `clearSession()`
- `refreshSession()` → `authService.refreshToken()` → sync

**Auto-refresh & Events:**
```typescript
// Auto-refresh khi accessToken đã hết hạn
useEffect(() => {
  if (!accessToken || !tokenStorage.isAccessTokenExpired()) return
  void refreshSession().catch(clearSession)
}, [accessToken])

// Multi-tab sync
window.addEventListener('auth:logout', clearSession)
window.addEventListener('auth:refresh', syncSessionFromStorage)
```

### Token Storage (`services/tokenStorage.ts`)

```typescript
tokenStorage.getAccessToken(): string | null
tokenStorage.getRefreshToken(): string | null
tokenStorage.setTokens(auth: AuthResponse): void
tokenStorage.clearTokens(): void

tokenStorage.getUserFromToken(): AuthUser | null
  // Decode JWT Base64URL payload thủ công (không cần lib)
  // Trả về: { username, email, role, exp, iat }

tokenStorage.isAccessTokenExpired(): boolean
  // So sánh user.exp (Unix timestamp) với Date.now()
```

### GoogleOAuthProviderWrapper (`contexts/GoogleOAuthProviderWrapper.tsx`)

- Inject `<script src="https://accounts.google.com/gsi/client">` vào DOM khi mount
- Truyền `VITE_GOOGLE_CLIENT_ID` vào context cho `GoogleAuthButton`
- Bọc toàn bộ App để Google Identity Services sẵn sàng trước khi render form

### useAuth Hook (`hooks/useAuth.ts`)

```typescript
function useAuth(): AuthContextValue
  // Throw error nếu dùng ngoài AuthProvider
  // Dùng trong mọi component cần auth data
```

---

## 📡 API & HTTP Client

### Axios Instance (`services/api.ts`)

**Base URL Resolution (theo thứ tự ưu tiên):**
1. `VITE_API_BASE_URL`
2. `VITE_API_URL`
3. Default: `http://localhost:8080/api`

**Request Interceptor:**
```
Thêm Authorization: "Bearer <accessToken>" nếu có token
```

**Response Interceptor (401 Auto-Refresh):**
```
1. Status 401 + không phải /auth/login, /auth/refresh + chưa retry?
2. Gọi refreshAccessToken()
3. Update header → Retry request gốc
4. Nếu refresh fail → clearTokens() → dispatch 'auth:logout'

Deduplication: dùng refreshPromise singleton để tránh gọi refresh nhiều lần
```

### Auth Service (`services/authService.ts`)

```typescript
authService.login(payload: LoginRequest)
  → POST /v1/auth/login  →  setTokens()

authService.loginWithGoogle(payload: GoogleAuthRequest)
  → POST /v1/auth/google  →  setTokens()

authService.logout()
  → POST /v1/auth/logout { refreshToken }  →  clearTokens()

authService.refreshToken()
  → POST /v1/auth/refresh { refreshToken }  →  setTokens()

authService.register(payload: RegisterRequest)
  → POST /v1/auth/register  →  trả về message string

authService.verifyEmail(payload: VerifyEmailRequest)
  → PUT /v1/auth/verify-email { email, confirmOTP }

authService.resendOtp(payload: ResendOtpRequest)
  → POST /v1/auth/resend-otp { email }

authService.requestPasswordResetOtp(payload: ForgotPasswordRequest)
  → POST /v1/auth/forgot-password/request-otp { email }

authService.verifyPasswordResetOtp(payload: VerifyEmailRequest)
  → POST /v1/auth/forgot-password/verify-otp { email, confirmOTP }

authService.resetPassword(payload: ResetPasswordRequest)
  → POST /v1/auth/forgot-password/reset { email, matKhauMoi, xacNhanMatKhau }
```

### TypeScript Types (`types/auth.ts`)

```typescript
AuthUser          // { username, email, role, exp, iat }
LoginRequest      // { email, matKhau, nhoMatKhau? }
AuthResponse      // { token, refreshToken, type, message }
RegisterRequest   // { username, email, hoTen, soDienThoai, matKhau, loaiTaiKhoan }
GoogleAuthRequest // { idToken, loaiTaiKhoan }
VerifyEmailRequest // { email, confirmOTP }
ResendOtpRequest  // { email }
ForgotPasswordRequest // { email }
ResetPasswordRequest  // { email, matKhauMoi, xacNhanMatKhau }
RefreshTokenRequest   // { refreshToken }
AccountType       // 'KHACH_HANG' | 'DOI_TAC'
```

### Error Handling (`utils/apiError.ts`)

```typescript
getApiErrorMessage(error, fallbackMessage): string
  // Lấy message từ: response.data.message || .error || .details
  // Special case 401 → "Email hoặc mật khẩu không đúng."
  // Fallback nếu không parse được
```

---

## 📂 Cấu Trúc Folder & File

```
frontend/
├���─ src/
│   ├── main.tsx                         # Entry point
│   ├── App.tsx                          # Routes
│   ├── App.css / index.css              # Global styles
│   │
│   ├── features/                        # Feature-scoped modules
│   │   └── auth/
│   │       ├── components/              # Shared auth UI components
│   │       │   ├── index.ts             # Barrel export
│   │       │   ├── AuthTextField.tsx    # Input với icon, label, toggle password
│   │       │   ├── GoogleAuthButton.tsx # Social button Google (Google Identity Services)
│   │       │   └── LoginTemplate.tsx    # Layout 2 cột (hero + form card)
│   │       └── utils/                   # Shared auth utilities
│   │           ├── index.ts             # Barrel export
│   │           ├── authUi.ts            # Tailwind class constants
│   │           └── authValidation.ts    # Validation functions
│   │
│   ├── pages/
│   │   ├── ForbiddenPage.tsx            # 403 page
│   │   └── auth/
│   │       ├── index.ts                 # Barrel export pages
│   │       ├── CustomerLogin.tsx        # Đăng nhập khách hàng
│   │       ├── CustomerRegister.tsx     # Đăng ký khách hàng
│   │       ├── PartnerLogin.tsx         # Đăng nhập đối tác
│   │       ├── PartnerRegister.tsx      # Đăng ký đối tác
│   │       ├── AdminLogin.tsx           # Đăng nhập quản trị
│   │       ├── VerifyEmailPage.tsx      # Xác minh email OTP
│   │       └── ForgotPasswordPage.tsx   # Quên mật khẩu (3 bước)
│   │
│   ├── contexts/
│   │   ├── authContext.ts               # AuthContext definition + types
│   │   ├── AuthContext.tsx              # AuthProvider component
│   │   └── GoogleOAuthProviderWrapper.tsx # Google Identity Services script injection
│   │
│   ├── hooks/
│   │   └── useAuth.ts                   # useAuth hook
│   │
│   ├── routes/
│   │   ├── index.ts                     # Barrel export
│   │   ├── routeGuards.ts               # normalizeRole, hasAllowedRole, getDefaultPathByRole
│   │   ├── ProtectedRoute.tsx           # isAuthenticated guard
│   │   ├── RoleRoute.tsx                # Role-based guard
│   │   ├── AdminRoute.tsx               # QUAN_TRI_VIEN only
│   │   └── StaffRoute.tsx               # DOI_TAC only
│   │
│   ├── services/
│   │   ├── api.ts                       # Axios instance + interceptors
│   │   ├── authService.ts               # Tất cả auth API calls
│   │   └── tokenStorage.ts              # Token get/set/clear + JWT decode
│   │
│   ├── types/
│   │   └── auth.ts                      # TypeScript interfaces & types
│   │
│   ├── utils/
│   │   └── apiError.ts                  # API error message extractor
│   │
│   ├── assets/
│   │   └── hero.png                     # Ảnh hero trên trang login
│   │
│   ├── features/                        # (Future: hotel, booking, ...)
│   └── store/                           # (Future: Redux/Zustand)
│
├── public/
│   ├── favicon.svg
│   └── icons.svg
├── index.html
│
├── vite.config.ts
├── tailwind.config.js
├── tsconfig.json / tsconfig.app.json / tsconfig.node.json
├── eslint.config.js
├── postcss.config.js
└── package.json
```

---

## 🎨 Auth Feature — Components & Utilities

### `LoginTemplate` (`features/auth/components/LoginTemplate.tsx`)

Layout 2 cột dùng chung cho tất cả trang auth:

- **Cột trái** (ẩn trên mobile): Ảnh hero + branding TraVi OTA
- **Cột phải**: Form card với:
  - Badge portal (Khách hàng / Đối tác / Quản trị)
  - Tab chuyển đổi vai trò (3 tabs: Customer / Partner / Admin)
  - Title + Subtitle động
  - Scrollable khi form dài (đăng ký)

**Props:**
```typescript
activeRole: 'KHACH_HANG' | 'DOI_TAC' | 'QUAN_TRI_VIEN'
title?: string
subtitle?: string
children: ReactNode
```

### `AuthTextField` (`features/auth/components/AuthTextField.tsx`)

Input component tái sử dụng:

- Icon bên trái (mail, lock, user, phone, key)
- `labelAction` — slot cho link bên phải label (dùng cho "Quên mật khẩu?")
- Toggle hiện/ẩn mật khẩu
- Tone màu: `blue` (khách hàng), `emerald` (đối tác), `slate` (admin)
- Hiển thị lỗi validation bên dưới

**Props:**
```typescript
id, name, label: string
labelAction?: ReactNode   // Link "Quên mật khẩu?" nằm cạnh label
type?, value, placeholder?, autoComplete?
error?: string
icon?: 'key' | 'lock' | 'mail' | 'phone' | 'user'
tone?: 'blue' | 'emerald' | 'slate'
showPasswordToggle?: boolean
onChange: (value: string) => void
```

### `GoogleAuthButton` (`features/auth/components/GoogleAuthButton.tsx`)

- Render nút ẩn của Google Identity Services (`renderButton`)
- Hiển thị custom button "Tiếp tục với Google" với SVG logo Google
- Khi click → trigger click vào nút Google ẩn → mở popup Google
- `disabled` khi form đang submit
- Hiển thị loader khi đang xác thực

### Auth UI Constants (`features/auth/utils/authUi.ts`)

```typescript
authButtonBaseClass    // Base class cho submit button
authFormGroupClass     // mb-5 spacing giữa các field
authLastFormGroupClass // mb-7 cho field cuối trước nút submit
authFooterClass        // Container link footer (đăng ký / đăng nhập)
authSmallLinkClass     // Link nhỏ (quên mật khẩu, back)
authAlertErrorClass    // Alert lỗi đỏ
authAlertSuccessClass  // Alert thành công xanh
authInputBaseClass, authInputWrapperClass, authInputIconClass
authLabelClass, authErrorClass
// ... (authLayoutClass, authCardClass, authIconBoxClass, authTitleClass,
//      authSubtitleClass — dùng cho VerifyEmailPage)
```

### Auth Validation (`features/auth/utils/authValidation.ts`)

```typescript
isValidEmail(email: string): boolean
  // Pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/

isValidPhoneNumber(phone: string): boolean
  // Pattern: /^(0|\+84)\d{9}$/

validatePassword(password: string): string   // '' nếu hợp lệ, message nếu sai
  // Độ dài: 9–20 ký tự
  // Bắt buộc: 1 hoa, 1 thường, 1 số, 1 ký tự đặc biệt (@$!%*?&)
  // Chỉ cho phép A-Za-z0-9@$!%*?&
```

---

## 🔄 Form State Pattern

Tất cả form auth dùng cùng một pattern:

```typescript
const [form, setForm] = useState<FormValues>(initialState)
const [errors, setErrors] = useState<FormErrors>({})
const [submitError, setSubmitError] = useState('')
const [loading, setLoading] = useState(false)

// Xóa error khi user sửa field
const updateField = (field: keyof FormValues, value: string) => {
  setForm(current => ({ ...current, [field]: value }))
  setErrors(current => ({ ...current, [field]: undefined }))
  setSubmitError('')
}

const handleSubmit = async (e: FormEvent) => {
  e.preventDefault()
  if (!validateForm()) return
  setLoading(true)
  setSubmitError('')
  try {
    // gọi API
  } catch (error) {
    setSubmitError(getApiErrorMessage(error, 'Fallback message'))
  } finally {
    setLoading(false)
  }
}
```

---

## 🔒 Security Considerations

| Điểm | Hiện tại | Ghi chú |
|---|---|---|
| Token Storage | localStorage | Đơn giản; có thể upgrade sang httpOnly cookie |
| XSS | ✅ React auto-escapes | Không dùng `dangerouslySetInnerHTML` |
| CORS | ✅ Backend controlled | Frontend không tự config |
| Password Validation | ✅ Client + Server | Kiểm tra cả 2 phía |
| Auto Token Refresh | ✅ Axios interceptor | Retry request sau khi refresh |
| Google OAuth | ✅ ID Token verified phía backend | Frontend không decode token Google |

---

## 🚀 Development & Build

### Environment Variables

```bash
# .env hoặc .env.local
VITE_API_BASE_URL=http://localhost:8080/api   # Backend URL
VITE_GOOGLE_CLIENT_ID=your-google-client-id  # Google OAuth Client ID
```

> `VITE_GOOGLE_CLIENT_ID` là bắt buộc để nút đăng nhập Google hoạt động.  
> Lấy tại: [console.cloud.google.com](https://console.cloud.google.com) → OAuth 2.0 Client IDs

### Scripts

```bash
npm run dev      # Dev server (http://localhost:5173)
npm run build    # TypeScript check + Vite bundle → dist/
npm run preview  # Preview production build (http://localhost:4173)
npm run lint     # ESLint --max-warnings 0
npm run format   # Prettier: src/**/*.{ts,tsx,css,md}
```

### TypeScript Configuration

- `tsconfig.json` → Global settings (ESNext, DOM, ES2020)
- `tsconfig.app.json` → App source (`src/**`)
- `tsconfig.node.json` → Build tools (`vite.config.ts`, `eslint.config.js`)

### Vite Configuration (`vite.config.ts`)

```typescript
plugins: [@vitejs/plugin-react]             // Oxc/SWC transpilation
server:  { watch: { usePolling: true } }    // Docker compatibility
port:    5173 (default)
```

---

## 🔧 Hướng Dẫn Mở Rộng

### Thêm trang đăng nhập cho vai trò mới

1. Tạo `src/pages/auth/[Role]Login.tsx`
2. Dùng `<LoginTemplate activeRole="...">` + `<AuthTextField>` + `<GoogleAuthButton>`
3. Thêm route vào `App.tsx`
4. Tạo `src/routes/[Role]Route.tsx` nếu cần guard riêng.

### Thêm Protected Route mới

```tsx
// App.tsx
<Route element={<ProtectedRoute />}>
  <Route path="/new-feature" element={<NewFeaturePage />} />
</Route>
```

### Sửa Validation Rules

Chỉnh sửa `src/features/auth/utils/authValidation.ts` — tất cả form auth dùng chung file này.

### Đổi Backend URL

Đặt `VITE_API_BASE_URL` trong `.env.local` hoặc sửa `src/services/api.ts`.

---

## 🚢 Deployment

### Production Build

```bash
npm run build
# Output: dist/
npm run preview    # Test tại http://localhost:4173
```

### Environment (Production)

```bash
VITE_API_BASE_URL=https://api.travi-ota.com/api
VITE_GOOGLE_CLIENT_ID=your-production-google-client-id
```

### Docker (từ root project)

Frontend được build thành static files:
- **Build stage**: `node:20-alpine` → `npm install` + `npm run build`
- **Runtime stage**: Nginx serve `dist/`

---

## 📋 Component Hierarchy

```
App
└── BrowserRouter
    └── Routes
        ├── / (HomePage)
        ├── /login                   → CustomerLogin
        ├── /register                → CustomerRegister
        ├── /verify-email            → VerifyEmailPage
        ├── /forgot-password         → ForgotPasswordPage (tone=blue)
        ├── /partner/login           → PartnerLogin
        ├── /partner/register        → PartnerRegister
        ├── /partner/forgot-password → ForgotPasswordPage (tone=emerald)
        ├── /admin/login             → AdminLogin
        ├── /403                     → ForbiddenPage
        ├── ProtectedRoute → /payment
        ├── AdminRoute    → /admin
        └── StaffRoute    → /partner
```

---

## 🎯 Key Design Patterns

1. **Feature-first structure**: Components, utils tái sử dụng trong auth nằm ở `features/auth/` — tách khỏi page-level
2. **Barrel exports**: Mỗi folder có `index.ts` → import gọn qua `@features/auth/components`
3. **Context API**: Global auth state thay vì Redux (đủ đơn giản cho Sprint 1)
4. **Custom Hook**: `useAuth()` cung cấp API đồng nhất cho mọi component
5. **HOC Route Guards**: `ProtectedRoute` / `RoleRoute` bọc nhau (composition)
6. **Axios Interceptors**: Auto-attach token + auto-refresh 401
7. **Window Events**: `auth:logout`, `auth:refresh` cho multi-tab sync
8. **Type Safety**: TypeScript full-coverage trên toàn bộ auth layer
9. **Controlled inputs + inline validation**: Clear lỗi ngay khi user sửa field

---

## 📊 Trạng Thái Hoàn Thiện

### ✅ Sprint 1

**Authentication UI**
- [x] CustomerLogin — đăng nhập email/password + Google
- [x] CustomerRegister — đăng ký (6 fields)
- [x] PartnerLogin — đăng nhập email/password + Google
- [x] PartnerRegister — đăng ký (6 fields)
- [x] AdminLogin — đăng nhập email/password
- [x] VerifyEmailPage — nhập OTP + gửi lại OTP
- [x] ForgotPasswordPage — 3 bước (request OTP → verify → reset)
- [x] LoginTemplate — layout 2 cột dùng chung
- [x] AuthTextField — input component với tone/icon/labelAction
- [x] GoogleAuthButton — nút Google OAuth (Google Identity Services)
- [x] GoogleOAuthProviderWrapper — inject Google script

**Infrastructure**
- [x] AuthProvider + AuthContext + useAuth hook
- [x] tokenStorage (JWT decode thủ công, expiry check)
- [x] authService (10 methods bao phủ toàn bộ auth flow)
- [x] Axios interceptors (auto-token + 401 refresh + dedup)
- [x] Route guards (ProtectedRoute, RoleRoute, AdminRoute, StaffRoute)
- [x] routeGuards utils (normalizeRole, hasAllowedRole, getDefaultPathByRole)
- [x] Feature-first folder refactor (`features/auth/components` + `features/auth/utils`)

### ⏳ Sprint 2

- [ ] Hotel search & listing UI
- [ ] Restaurant search & listing UI
- [ ] Booking flow (khách hàng)
- [ ] Partner dashboard (quản lý khách sạn/nhà hàng)
- [ ] Admin dashboard (quản lý user, nội dung)
- [ ] Payment flow

### 📋 Sprint 3+

- [ ] Code splitting (`React.lazy` + `Suspense`)
- [ ] Unit tests (Vitest + Testing Library)
- [ ] Dark mode
- [ ] PWA support
- [ ] Internationalization (i18n)
- [ ] httpOnly Cookie (nâng cao bảo mật token)

---

## 📝 Notes

- **Ngôn ngữ**: Tiếng Việt (dễ mở rộng i18n)
- **Responsive**: Mobile-first với Tailwind
- **Accessibility**: Semantic HTML, form labels, ARIA (có thể cải thiện thêm)
- **Validation**: Double-validation — frontend (UX) + backend (security)

---

**Last Updated:** May 2026  
**Version:** 1.0.0  
**Status:** Sprint 1 Complete ✅

