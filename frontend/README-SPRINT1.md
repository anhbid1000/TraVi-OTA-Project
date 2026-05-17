# TraVi-OTA Frontend

Ứng dụng Frontend cho nền tảng quản lý khách sạn và nhà hàng thông minh (TraVi-OTA). Được xây dựng với React 19, TypeScript, Vite, Tailwind CSS và các công nghệ hiện đại.

## Tổng Quan Dự Án

TraVi-OTA Frontend là một ứng dụng web toàn chức năng hỗ trợ ba loại người dùng chính:

- **Khách hàng (KHACH_HANG)**: Đặt phòng, thanh toán
- **Đối tác (DOI_TAC)**: Quản lý nhà hàng/khách sạn
- **Quản trị viên (QUAN_TRI_VIEN)**: Quản lý hệ thống

---

## 📦 Công Nghệ & Kỹ Thuật Sử Dụng

### Core Framework

- **React 19.2.4**: Framework UI component-based
- **TypeScript 6.0**: Type-safe JavaScript
- **Vite 8.0**: Build tool siêu nhanh (HMR, dev server)

### Styling & UI

- **Tailwind CSS 4.2**: Utility-first CSS framework
- **Lucide React 1.8**: Icon library
- **clsx & tailwind-merge**: Utility cho CSS class composition

### State Management & Data

- **React Context API**: Global state (Authentication)
- **React Router 7.14**: Client-side routing
- **Axios 1.15**: HTTP client với interceptor

### Form & Validation

- **React Hook Form 7.72**: Form state management
- **Zod 4.3**: Schema validation library

### Dev Tools

- **ESLint 9.39**: Linting code
- **Prettier 3.8**: Code formatter
- **PostCSS 8.5 + Autoprefixer**: CSS processing

---

## 🏗️ Kiến Trúc & Luồng Ứng Dụng

### 1. Luồng Khởi Động Ứng Dụng

```
main.tsx (Entry Point)
    ↓
createRoot & render App
    ↓
AuthProvider (Context Provider)
    ↓
BrowserRouter (Routing)
    ↓
App Component (Routes & Pages)
```

**File chính**: `src/main.tsx`

```typescript
- Khởi tạo React DOM root
- Bọc AuthProvider để quản lý authentication
- Render App component
```

### 2. Authentication Flow (Luồng Xác Thực)

#### A. Đăng Nhập (Login)

```
Login Page (/login, /partner/login, /admin/login)
    ↓
User nhập email & password
    ↓
Validation (email format, password required)
    ↓
authService.login(payload)
    ↓
API POST /v1/auth/login
    ↓
Backend trả về: token, refreshToken, type
    ↓
tokenStorage.setTokens() - Lưu localStorage
    ↓
AuthProvider cập nhật state (user, accessToken)
    ↓
Redirect theo role (Admin → /admin, Partner → /partner, Customer → /)
```

**Key Points**:

- Email validation: `/^[^\s@]+@[^\s@]+\.[^\s@]+$/`
- Token được lưu trong localStorage
- User info được decode từ JWT token payload
- Automatic redirect dựa trên role

#### B. Đăng Ký (Register)

```
Register Page (/register, /partner/register)
    ↓
User điền form (username, email, name, phone, password)
    ↓
Validation:
  - Username: ≥4 ký tự, chỉ a-z, 0-9, _, .
  - Họ tên: ≥2 ký tự
  - Email: valid format
  - Phone: format (0|+84) + 9 chữ số
  - Password: ≥9 ký tự, ≤20 ký tự, 1 hoa, 1 thường, 1 số, 1 ký tự đặc biệt (@$!%*?&)
  - Confirm Password: phải khớp
    ↓
authService.register(payload)
    ↓
API POST /v1/auth/register
    ↓
Backend trả về message hoặc success
    ↓
Redirect → VerifyEmailPage (/verify-email)
    ↓
User nhập OTP từ email
    ↓
authService.verifyEmail(payload)
    ↓
API PUT /v1/auth/verify-email
    ↓
Redirect → Login page
```

**Password Validation Rules**:

- Độ dài: 9-20 ký tự
- Bắt buộc: 1 chữ hoa [A-Z], 1 chữ thường [a-z], 1 số [0-9], 1 ký tự đặc biệt
- Chỉ cho phép: A-Za-z, 0-9, @$!%\*?&

#### C. Token Refresh Flow

```
API Request được gửi
    ↓
axios interceptor kiểm tra token trong localStorage
    ↓
Thêm Authorization header: "Bearer {token}"
    ↓
Response có lỗi 401?
    ↓
YES: Gọi refreshAccessToken()
    ↓
API POST /v1/auth/refresh { refreshToken }
    ↓
Backend trả về token mới
    ↓
tokenStorage.setTokens() - Update localStorage
    ↓
Retry request ban đầu với token mới
    ↓
NO: Trả về response bình thường
```

**Deduplication**: Nếu nhiều request 401 cùng lúc, chỉ refresh 1 lần (sử dụng `refreshPromise`)

#### D. Logout Flow

```
User click Logout
    ↓
authService.logout()
    ↓
API POST /v1/auth/logout { refreshToken }
    ↓
tokenStorage.clearTokens() - Xóa localStorage
    ↓
Dispatch event 'auth:logout'
    ↓
AuthProvider xóa state (user, tokens)
    ↓
Redirect → /login hoặc home
```

---

## 🛣️ Routing & Authorization (Định Tuyến & Phân Quyền)

### Route Structure

```
/ (Home Page - Public)
├── /login (Customer Login)
├── /register (Customer Register)
├── /verify-email (Email Verification)
├── /partner/login (Partner Login)
├── /partner/register (Partner Register)
├── /admin/login (Admin Login)
├── /403 (Forbidden - Unauthorized Access)
├── Protected Routes (Require Authentication)
│   └── /payment (Customer - Protected)
├── Admin Routes (Require QUAN_TRI_VIEN role)
│   └── /admin (Admin Dashboard)
├── Partner Routes (Require DOI_TAC role)
│   └── /partner (Partner Dashboard)
└── * (404 Not Found)
```

### Route Guards Implementation

#### ProtectedRoute

- Kiểm tra: `isAuthenticated`
- Nếu chưa login → Redirect `/login` (với state lưu previous page)
- Có loading state → Render `null` khi loading

**File**: `src/routes/ProtectedRoute.tsx`

#### RoleRoute

- Kiểm tra: `isAuthenticated` + user's role
- Normalize role: loại bỏ "ROLE\_" prefix, convert uppercase
- Nếu chưa login → Redirect `/login`
- Nếu không có quyền → Redirect `/403` (Forbidden)
- Hỗ trợ multiple roles: `allowedRoles` array

**File**: `src/routes/RoleRoute.tsx`

#### AdminRoute & StaffRoute

- Admin: chỉ cho phép `QUAN_TRI_VIEN`
- Staff/Partner: chỉ cho phép `DOI_TAC`
- Customize redirect paths qua props

**Files**:

- `src/routes/AdminRoute.tsx`
- `src/routes/StaffRoute.tsx`

#### Route Guards Utilities

**File**: `src/routes/routeGuards.ts`

```typescript
normalizeRole(role: string): RoleName
  - Remove "ROLE_" or "ROLE-" prefix
  - Convert to uppercase
  - Trim whitespace

hasAllowedRole(user, allowedRoles): boolean
  - Kiểm tra user role có trong allowedRoles array hay không

getDefaultPathByRole(role: string): string
  - QUAN_TRI_VIEN → /admin
  - DOI_TAC → /partner
  - KHACH_HANG → /
  - Default → /
```

---

## 🔐 Authentication Context & State Management

### AuthContext (Global State)

**File**: `src/contexts/authContext.ts`

```typescript
type AuthContextValue = {
  user: AuthUser | null; // User info từ JWT
  accessToken: string | null; // Access token
  refreshToken: string | null; // Refresh token
  isAuthenticated: boolean; // Is valid token?
  isLoading: boolean; // Loading state
  login: (payload: LoginRequest) => Promise<AuthResponse>;
  logout: () => Promise<void>;
  refreshSession: () => Promise<AuthResponse>;
};
```

### AuthProvider (Context Provider)

**File**: `src/contexts/AuthContext.tsx`

**Initialization**:

- Đọc tokens từ localStorage (app khởi động)
- Decode JWT để lấy user info
- Kiểm tra token expiry

**State Management**:

- `user`: AuthUser object (từ JWT payload)
- `accessToken`: JWT token từ login response
- `refreshToken`: Refresh token để lấy token mới
- `isLoading`: Loading flag cho login/logout

**Methods**:

1. `login(payload)`:
   - Call authService.login()
   - Tự động sync session từ localStorage
   - Throw error nếu fail

2. `logout()`:
   - Call authService.logout()
   - Clear tokens từ localStorage
   - Reset state

3. `refreshSession()`:
   - Call authService.refreshToken()
   - Update localStorage & state

**Auto-Refresh Logic**:

```typescript
useEffect(() => {
  if (!accessToken || !tokenStorage.isAccessTokenExpired()) {
    return;
  }
  // Token expired → attempt refresh
  refreshSession().catch(clearSession);
}, [accessToken]);
```

**Event Listeners**:

- `auth:logout` → force logout (ví dụ: token revoked)
- `auth:refresh` → sync session từ localStorage (multi-tab)

### Token Storage Service

**File**: `src/services/tokenStorage.ts`

```typescript
tokenStorage.getAccessToken(): string | null
tokenStorage.getRefreshToken(): string | null
tokenStorage.getTokenType(): string (default: "Bearer")
tokenStorage.setTokens(auth: AuthResponse): void
tokenStorage.clearTokens(): void

tokenStorage.getUserFromToken(): AuthUser | null
  - Decode JWT manually (không cần jwt-decode library)
  - Parse Base64URL payload
  - Extract user object

tokenStorage.isAccessTokenExpired(): boolean
  - Check user.exp (expiry timestamp)
  - Compare với Date.now()
```

**JWT Decoding**:

```typescript
// Format: header.payload.signature
// Payload là Base64URL encoded JSON
const decodeBase64Url = (value: string) => {
  const base64 = value.replace(/-/g, '+').replace(/_/g, '/');
  const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
  return atob(padded);
};
```

### useAuth Hook

**File**: `src/hooks/useAuth.ts`

```typescript
function useAuth(): AuthContextValue
  - Access authentication context
  - Throw error nếu không được wrapped trong AuthProvider
  - Use này ở tất cả components cần auth data
```

---

## 📡 API & HTTP Client

### Axios Configuration

**File**: `src/services/api.ts`

**Base URL Resolution**:

```typescript
Priority:
1. VITE_API_BASE_URL environment variable
2. VITE_API_URL environment variable
3. Default: http://localhost:8080/api

Normalized: Đảm bảo kết thúc bằng /api
```

**Default Headers**:

```
Content-Type: application/json
```

### Request Interceptor

```typescript
- Thêm Authorization header với access token
- Format: "Bearer {token}" hoặc "{tokenType} {token}"
- Nếu không có token, không thêm header
```

### Response Interceptor (Token Refresh Logic)

```
1. Check status: 401 Unauthorized?
2. Check: Không phải /auth/login, /auth/refresh request?
3. Check: Chưa retry request này?
4. Nếu tất cả true:
   - Set _retry flag = true
   - Call refreshAccessToken()
   - Update Authorization header
   - Retry original request
5. Nếu refresh fail:
   - Clear tokens
   - Dispatch auth:logout event
   - Reject error

Deduplication:
- Nếu refreshPromise đang pending → await current
- Không call API lần nữa
```

### Auth Service

**File**: `src/services/authService.ts`

```typescript
authService.login(payload: LoginRequest): Promise<AuthResponse>
  - POST /v1/auth/login
  - Payload: { email, matKhau, nhoMatKhau? }
  - Tự động lưu tokens vào localStorage

authService.logout(): Promise<void>
  - POST /v1/auth/logout
  - Payload: { refreshToken } (nếu có)
  - Tự động xóa tokens từ localStorage

authService.refreshToken(): Promise<AuthResponse>
  - POST /v1/auth/refresh
  - Payload: { refreshToken }
  - Tự động update tokens

authService.register(payload: RegisterRequest): Promise<string>
  - POST /v1/auth/register
  - Trả về message

authService.verifyEmail(payload: VerifyEmailRequest): Promise<string>
  - PUT /v1/auth/verify-email
  - Payload: { email, confirmOTP }

authService.resendOtp(payload: ResendOtpRequest): Promise<string>
  - POST /v1/auth/resend-otp
  - Payload: { email }
```

### Error Handling

**File**: `src/utils/apiError.ts`

```typescript
getApiErrorMessage(error, fallbackMessage): string
  - Check axios error response
  - Extract message từ: data.message || data.error || data.details
  - Special case 401: "Email hoặc mật khẩu không đúng."
  - Fallback nếu không lấy được message
```

---

## 📂 Cấu Trúc Folder & File

```
frontend/
├── src/
│   ├── main.tsx                          # Entry point
│   ├── App.tsx                           # Main component với routes
│   ├── App.css                           # App styling
│   ├── index.css                         # Global styling
│   │
│   ├── contexts/
│   │   ├── authContext.ts               # Context definition
│   │   └── AuthContext.tsx              # Context provider component
│   │
│   ├── hooks/
│   │   └── useAuth.ts                   # useAuth hook
│   │
│   ├── routes/
│   │   ├── index.ts                     # Exports
│   │   ├── routeGuards.ts               # Guard utilities
│   │   ├── ProtectedRoute.tsx           # Authentication guard
│   │   ├── RoleRoute.tsx                # Role-based guard
│   │   ├── AdminRoute.tsx               # Admin-only route
│   │   └── StaffRoute.tsx               # Partner-only route
│   │
│   ├── pages/
│   │   ├── ForbiddenPage.tsx            # 403 page
│   │   └── auth/
│   │       ├── index.ts                 # Exports
│   │       ├── CustomerLogin.tsx        # Customer login page
│   │       ├── CustomerRegister.tsx     # Customer register page
│   │       ├── PartnerLogin.tsx         # Partner login page
│   │       ├── PartnerRegister.tsx      # Partner register page
│   │       ├── AdminLogin.tsx           # Admin login page
│   │       ├── VerifyEmailPage.tsx      # Email verification page
│   │       ├── AuthTextField.tsx        # Input component
│   │       ├── authValidation.ts        # Validation rules
│   │       └── authUi.ts                # Tailwind class constants
│   │
│   ├── services/
│   │   ├── api.ts                       # Axios instance + interceptors
│   │   ├── authService.ts               # Auth API calls
│   │   └── tokenStorage.ts              # Token management
│   │
│   ├── types/
│   │   └── auth.ts                      # TypeScript interfaces
│   │
│   ├── utils/
│   │   └── apiError.ts                  # Error message extraction
│   │
│   ├── assets/                          # Images, icons, etc.
│   ├── features/                        # Feature modules (future)
│   └── store/                           # State management (future)
│
├── public/                              # Static files
├── index.html                           # HTML entry point
│
├── vite.config.ts                       # Vite configuration
├── tailwind.config.js                   # Tailwind configuration
├── tsconfig.json                        # TypeScript config (React)
├── tsconfig.app.json                    # App-specific TS config
├── tsconfig.node.json                   # Node-specific TS config
├── eslint.config.js                     # ESLint configuration
├── postcss.config.js                    # PostCSS configuration
│
├── package.json                         # Dependencies & scripts
└── README.md                            # This file
```

---

## 🎨 Validation & UI Patterns

### Form Validation

**File**: `src/pages/auth/authValidation.ts`

```typescript
isValidEmail(email: string): boolean
  - Pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/

isValidPhoneNumber(phone: string): boolean
  - Pattern: /^(0|\+84)\d{9}$/
  - Bắt đầu: 0 hoặc +84, theo sau 9 chữ số

validatePassword(password: string): string (error message hoặc empty)
  - Length: 9-20 ký tự
  - Bắt buộc: 1 hoa [A-Z], 1 thường [a-z], 1 số [0-9]
  - Bắt buộc: 1 ký tự đặc biệt (@$!%*?&)
  - Không cho phép: ký tự đặc biệt khác
```

### Form State Pattern

```typescript
const [form, setForm] = useState<FormValues>(initialState)
const [errors, setErrors] = useState<FormErrors>({})
const [submitError, setSubmitError] = useState('')
const [loading, setLoading] = useState(false)

// Clear errors khi user chỉnh sửa field
const updateField = (field: keyof FormValues, value: string) => {
  setForm(...)
  setErrors(current => ({ ...current, [field]: undefined }))
  setSubmitError('')
}

// Validation before submit
const validateForm = (): boolean => {
  // Build nextErrors object
  // setErrors(nextErrors)
  // return isEmpty(nextErrors)
}

// Handle submit
const handleSubmit = async (e: FormEvent) => {
  e.preventDefault()
  if (!validateForm()) return

  setLoading(true)
  setSubmitError('')

  try {
    // Call API
  } catch (error) {
    setSubmitError(getApiErrorMessage(...))
  } finally {
    setLoading(false)
  }
}
```

### UI/Styling Patterns

**File**: `src/pages/auth/authUi.ts`

Các Tailwind class constants được định nghĩa để:

- Tái sử dụng styling consistent
- Dễ maintain và update theme
- Ví dụ:
  ```typescript
  authCardClass = 'w-full max-w-[480px] bg-white/90 backdrop-blur-xl...';
  authButtonBaseClass = 'h-12 w-full border-none text-white...';
  authErrorClass = 'mt-2 text-sm font-medium text-red-600';
  ```

**Tailwind Features Used**:

- Backdrop blur: `backdrop-blur-xl`
- Transparency: `bg-white/90`
- Responsive: `px-4 py-10`
- Dark mode ready (có thể extend)
- Smooth transitions: `transition-all`
- Hover effects: `hover:-translate-y-0.5`
- Disabled states: `disabled:opacity-70`

---

## 🚀 Development & Build

### Scripts

```bash
# Development server (hot reload)
npm run dev
# http://localhost:5173

# Type checking
npm run build
# Compiles TypeScript + bundles with Vite

# Preview production build locally
npm run preview

# Linting
npm run lint
# ESLint check .ts, .tsx files
# --max-warnings 0 (fail nếu có warning)

# Format code
npm run format
# Prettier: src/**/*.{ts,tsx,css,md}
```

### Environment Variables

```bash
# .env hoặc .env.local
VITE_API_BASE_URL=http://localhost:8080/api
# hoặc
VITE_API_URL=http://localhost:8080/api

# Default: http://localhost:8080/api
```

### TypeScript Configuration

**tsconfig.json**: Global settings

- Target: ES2020
- Module: ESNext
- Lib: ES2020 + DOM

**tsconfig.app.json**: App-specific

- Include: src/\*\*
- Exclude: node_modules, dist

**tsconfig.node.json**: Build tools

- Include: vite.config.ts, eslint.config.js

### Vite Configuration

**File**: `vite.config.ts`

```typescript
- Plugin: @vitejs/plugin-react (Oxc/SWC transpilation)
- Server: { watch: { usePolling: true } } (Docker compatibility)
- Default port: 5173
```

### Tailwind Configuration

**File**: `tailwind.config.js`

```javascript
- Content: index.html + src/**/*.{js,ts,jsx,tsx}
- Theme: extend (standard theme + custom)
- Plugins: (none currently)
```

---

## 🔄 Component Lifecycle & Data Flow

### Component Hierarchy

```
App
├── BrowserRouter
│   └── Routes
│       ├── Route: / (HomePage)
│       ├── Route: /login (CustomerLogin)
│       ├── Route: /register (CustomerRegister)
│       ├── Route: /verify-email (VerifyEmailPage)
│       ├── Route: /partner/login (PartnerLogin)
│       ├── Route: /partner/register (PartnerRegister)
│       ├── Route: /admin/login (AdminLogin)
│       ├── Route: /403 (ForbiddenPage)
│       ├── Route: /payment (ProtectedRoute → PaymentPage)
│       ├── Route: /admin (AdminRoute → AdminDashboardPage)
│       └── Route: /partner (StaffRoute → PartnerDashboardPage)
```

### Data Flow Pattern

**Login Component**:

```
User Input
    ↓
updateField() → Update form state, clear errors
    ↓
handleSubmit() → Validate, call useAuth().login()
    ↓
useAuth().login() → Call authService.login(payload)
    ↓
authService.login() → POST API, tokenStorage.setTokens()
    ↓
AuthProvider updated → Context value changed
    ↓
All useAuth() hooks re-render với dữ liệu mới
    ↓
ProtectedRoute/RoleRoute check isAuthenticated
    ↓
Redirect dựa trên role
```

---

## 🔒 Security Considerations

1. **Token Storage**:
   - localStorage (không secure nhất nhưng simple)
   - Có thể upgrade sang httpOnly cookies (cần backend support)

2. **CORS**:
   - Controlled bởi backend CORS headers
   - Frontend không cần config

3. **XSS Protection**:
   - React auto-escapes output
   - Không dùng dangerouslySetInnerHTML

4. **CSRF**:
   - Backend nên implement CSRF token hoặc SameSite cookie

5. **Password Validation**:
   - Strong password requirements implemented
   - Front-end validation + backend validation

6. **Token Expiry**:
   - Access token expiry checked via JWT exp claim
   - Auto-refresh trước expiry
   - Graceful logout nếu refresh fail

---

## 📦 Dependencies Overview

| Package          | Version | Purpose           |
| ---------------- | ------- | ----------------- |
| react            | 19.2.4  | UI framework      |
| react-router-dom | 7.14.0  | Routing           |
| axios            | 1.15.0  | HTTP client       |
| react-hook-form  | 7.72.1  | Form state        |
| zod              | 4.3.6   | Schema validation |
| tailwindcss      | 4.2.2   | CSS framework     |
| lucide-react     | 1.8.0   | Icons             |
| typescript       | 6.0.2   | Type safety       |
| vite             | 8.0.4   | Build tool        |
| eslint           | 9.39.4  | Linting           |
| prettier         | 3.8.2   | Code formatter    |

---

## 🎯 Key Design Patterns

1. **Context API**: Global auth state (thay vì Redux cho đơn giản)
2. **Custom Hooks**: `useAuth()` để access auth context
3. **Higher-Order Components**: Route guards (ProtectedRoute, RoleRoute)
4. **Interceptors**: Axios để auto-refresh token
5. **Event-based Communication**: Window events cho multi-tab sync
6. **Composition**: Route components bọc nhau (AdminRoute → RoleRoute)
7. **Type Safety**: Full TypeScript usage

---

## 🚀 Deployment

### Production Build

```bash
npm run build
# Output: dist/ folder

# Test locally:
npm run preview
# http://localhost:4173
```

### Environment Setup

```bash
# Production .env
VITE_API_BASE_URL=https://api.travi-ota.com/api
```

### Docker (in root project)

```dockerfile
Frontend được build vào Docker container
- Base: node:20-alpine
- Build stage: npm install + npm run build
- Runtime: nginx để serve static files
```

---

## 🔧 Common Tasks

### Add New Authentication Provider

1. Tạo login page tại `src/pages/auth/[Provider]Login.tsx`
2. Implement form validation + submission
3. Thêm route vào `App.tsx`
4. Thêm route guard nếu cần (tạo `[Provider]Route.tsx`)

### Add New Protected Route

1. Tạo page component
2. Thêm route vào `App.tsx` bọc với guard:
   ```tsx
   <Route element={<ProtectedRoute />}>
     <Route path="/path" element={<Component />} />
   </Route>
   ```

### Modify Validation Rules

1. Update rules trong `src/pages/auth/authValidation.ts`
2. Update error messages (tiếng Việt)
3. Test validation flow

### Change API Base URL

1. Set environment variable `VITE_API_BASE_URL`
2. Hoặc modify `src/services/api.ts`
3. Vite tự reload khi .env thay đổi

---

## 📝 Notes

- **Multi-language**: Currently Vietnamese (Vi) - dễ extend sang multi-lang
- **Responsive**: Mobile-first design với Tailwind
- **Accessibility**: Semantic HTML, proper form labels (có thể improve)
- **Performance**: Code splitting via React.lazy() + Vite (chưa implement)
- **Testing**: Unit tests chưa implement (add Jest/Vitest)

---

## 👥 Team & Contributing

Xem [CONTRIBUTING.md](../CONTRIBUTING.md) nếu có.

---

## 📄 License

[Xem LICENSE file](../LICENSE)

---

**Last Updated**: May 2026
**Version**: 1.0.0
