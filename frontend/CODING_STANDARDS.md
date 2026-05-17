# TraVi-OTA Frontend - Coding Standards & Style Guide

Hướng dẫn toàn diện cho việc viết code, đặt tên, tạo file, và tổ chức mã nguồn trong TraVi-OTA Frontend.

---

## 📋 Mục Lục

1. [File & Folder Naming](#file--folder-naming)
2. [Folder Structure](#folder-structure)
3. [TypeScript & Type Definitions](#typescript--type-definitions)
4. [Import Statements](#import-statements)
5. [Naming Conventions](#naming-conventions)
6. [Component Patterns](#component-patterns)
7. [Hook Patterns](#hook-patterns)
8. [Service & Utility Patterns](#service--utility-patterns)
9. [Error Handling](#error-handling)
10. [Styling & CSS](#styling--css)
11. [Comments & Documentation](#comments--documentation)
12. [Best Practices](#best-practices)

---

## 📁 File & Folder Naming

### File Naming Conventions

| Type            | Pattern                  | Example                                |
| --------------- | ------------------------ | -------------------------------------- |
| React Component | `PascalCase.tsx`         | `CustomerLogin.tsx`, `AuthContext.tsx` |
| TypeScript File | `camelCase.ts`           | `authService.ts`, `tokenStorage.ts`    |
| Utility File    | `camelCase.ts`           | `apiError.ts`, `authValidation.ts`     |
| Constants File  | `camelCase.ts`           | `authUi.ts` (constants + utilities)    |
| Index File      | `index.ts` / `index.tsx` | `src/pages/auth/index.ts`              |
| Style File      | `ComponentName.css`      | `App.css`                              |
| Config File     | `fileName.config.js`     | `vite.config.ts`, `tailwind.config.js` |
| Test File       | `fileName.test.ts(x)`    | `authService.test.ts`                  |

**Quy tắc**:

- Component file (tạo React component) → **PascalCase.tsx**
- Non-component TypeScript → **camelCase.ts**
- Folder name → **camelCase** hoặc **lowercase** (ưu tiên camelCase)
- Hạn chế dùng index.tsx trong components (dễ confuse), thay vào dùng component name

### Folder Naming Examples

```
✅ GOOD:
src/pages/auth/
src/contexts/
src/services/
src/types/
src/hooks/
src/utils/
src/routes/

❌ AVOID:
src/Pages/auth/
src/PAGES/auth/
src/AuthPages/
src/page-auth/
```

---

## 🗂️ Folder Structure

### Standard Project Structure

```
frontend/
├── public/                              # Static assets
│   └── favicon.ico, etc.
│
├── src/
│   ├── main.tsx                         # Application entry point
│   ├── App.tsx                          # Root component
│   ├── App.css                          # App global styles
│   ├── index.css                        # Global CSS (Tailwind + custom)
│   │
│   ├── types/                           # TypeScript type definitions
│   │   └── auth.ts                      # Auth-related types
│   │
│   ├── contexts/                        # React Context providers
│   │   ├── authContext.ts               # Context definition
│   │   └── AuthContext.tsx              # Context provider component
│   │
│   ├── hooks/                           # Custom React hooks
│   │   ├── useAuth.ts
│   │   └── useCustomHook.ts
│   │
│   ├── services/                        # API & business logic services
│   │   ├── api.ts                       # Axios instance + interceptors
│   │   ├── authService.ts               # Auth API calls
│   │   └── tokenStorage.ts              # Token management
│   │
│   ├── utils/                           # Utility functions
│   │   ├── apiError.ts
│   │   └── helperFunction.ts
│   │
│   ├── routes/                          # Routing & guards
│   │   ├── index.ts                     # Exports
│   │   ├── routeGuards.ts               # Route guard utilities
│   │   ├── ProtectedRoute.tsx
│   │   ├── RoleRoute.tsx
│   │   ├── AdminRoute.tsx
│   │   └── StaffRoute.tsx
│   │
│   ├── pages/                           # Page components
│   │   ├── ForbiddenPage.tsx
│   │   └── auth/
│   │       ├── index.ts                 # Exports
│   │       ├── CustomerLogin.tsx
│   │       ├── CustomerRegister.tsx
│   │       ├── AdminLogin.tsx
│   │       ├── PartnerLogin.tsx
│   │       ├── PartnerRegister.tsx
│   │       ├── VerifyEmailPage.tsx
│   │       ├── AuthTextField.tsx
│   │       ├── authValidation.ts        # Validation logic
│   │       └── authUi.ts                # Tailwind constants
│   │
│   ├── features/                        # Feature modules (for large features)
│   │   └── featureName/
│   │       ├── components/
│   │       ├── hooks/
│   │       ├── services/
│   │       └── types/
│   │
│   ├── assets/                          # Images, icons, etc.
│   │   ├── images/
│   │   └── icons/
│   │
│   └── store/                           # State management (Redux, Zustand, etc.)
│       └── (future use)
│
├── index.html                           # HTML entry point
├── vite.config.ts                       # Vite config
├── tailwind.config.js                   # Tailwind config
├── postcss.config.js                    # PostCSS config
├── eslint.config.js                     # ESLint config
├── tsconfig.json                        # Main TypeScript config
├── tsconfig.app.json                    # App TypeScript config
├── tsconfig.node.json                   # Node TypeScript config
├── package.json                         # Dependencies
├── README.md                            # Project README
├── CODING_STANDARDS.md                  # This file
└── .env.example                         # Example environment variables
```

### Folder Organization Principles

1. **Colocation**: Letzte code gần những file có liên quan

   ```
   ✅ GOOD:
   pages/auth/
   ├── CustomerLogin.tsx
   ├── authValidation.ts (validation for auth pages)
   └── authUi.ts (Tailwind constants for auth UI)

   ❌ AVOID:
   utils/authValidation.ts (xa từ pages)
   ```

2. **Separation of Concerns**: Tách logic riêng

   ```
   ✅ GOOD:
   services/authService.ts (API calls)
   hooks/useAuth.ts (Custom hook)
   pages/auth/authValidation.ts (Form validation)

   ❌ AVOID:
   utils/auth.ts (mixing everything)
   ```

3. **Type Safety**: Type definitions gần code sử dụng

   ```
   ✅ GOOD:
   types/auth.ts (shared auth types)
   pages/auth/AuthTextField.tsx (component-specific types inline)

   ❌ AVOID:
   types/authTextField.ts (quá granular)
   ```

---

## 🏷️ TypeScript & Type Definitions

### Type Definition Location

```typescript
// types/auth.ts - SHARED types
export type AuthUser = {
  username?: string;
  email?: string;
  role?: string;
  exp?: number;
  iat?: number;
  [key: string]: unknown;
};

export type LoginRequest = {
  email: string;
  matKhau: string;
  nhoMatKhau?: boolean;
};

export type AccountType = 'DOI_TAC' | 'KHACH_HANG';

// pages/auth/CustomerLogin.tsx - COMPONENT-SPECIFIC types
type LoginFormValues = Pick<LoginRequest, 'email' | 'matKhau'>;
type LoginFormErrors = Partial<Record<keyof LoginFormValues, string>>;
type LocationState = {
  from?: {
    pathname?: string;
  };
  registerMessage?: string;
};
```

**Quy tắc**:

- **Global/Shared types** → `types/` folder
- **Component-specific types** → Định nghĩa ở file component (sau imports)
- **Avoid**: Type files cho mỗi component

### Type Naming

```typescript
// ✅ GOOD
type AuthUser = { ... }           // Data model
type LoginRequest = { ... }       // Request payload
type AuthResponse = { ... }       // Response payload
type FormValues = { ... }         // Form state
type FormErrors = { ... }         // Form errors
type LocationState = { ... }      // Route location state

// ❌ AVOID
type LoginPageProps = { ... }     (use directly in component)
type IAuthUser = { ... }          (no I prefix)
type AuthUserType = { ... }       (redundant "Type")
```

### Union Types & Literals

```typescript
// ✅ GOOD - Use literal types for fixed values
type AccountType = 'DOI_TAC' | 'KHACH_HANG';
type RoleName = 'QUAN_TRI_VIEN' | 'DOI_TAC' | 'KHACH_HANG';

// ✅ GOOD - Use as const for exhaustive checks
const ADMIN_ROLES = ['QUAN_TRI_VIEN'] as const;
const STAFF_ROLES = ['DOI_TAC'] as const;
```

### Optional Fields

```typescript
// ✅ GOOD - Use optional chaining for potentially undefined
type AuthUser = {
  username?: string;
  email?: string;
  role?: string;
};

// Use when getting values
const role = user?.role;
const name = user?.username ?? 'Anonymous';
```

---

## 📥 Import Statements

### Import Order (Must Follow)

```typescript
// 1. React & External Libraries
import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { UserPlus } from 'lucide-react';
import axios from 'axios';

// 2. Internal Services & Context
import { authService } from '../../services/authService';
import { useAuth } from '../../hooks/useAuth';
import { AuthContext } from '../contexts/AuthContext';

// 3. Types
import type { LoginRequest, AuthResponse } from '../../types/auth';

// 4. Utils & Helpers
import { getApiErrorMessage } from '../../utils/apiError';
import { isValidEmail } from './authValidation';

// 5. Styling & Assets
import { authCardClass, authButtonBaseClass } from './authUi';
import './ComponentName.css';

// Empty line between groups
```

**Quy tắc**:

- Phân tách imports thành 5 nhóm (như trên)
- Dòng trống giữa các nhóm
- `type` keyword cho type imports
- Alphabetical order trong mỗi nhóm (optional nhưng consistent)

### Import Paths

```typescript
// ✅ GOOD - Use relative paths
import { authService } from '../../services/authService';
import { useAuth } from '../hooks/useAuth';

// ❌ AVOID - Tuyệt đối paths (không config path aliases)
import { authService } from '/src/services/authService';

// ✅ GOOD - Re-export từ index.ts
// pages/auth/index.ts
export { AdminLogin } from './AdminLogin';
export { CustomerLogin } from './CustomerLogin';

// pages/auth/AdminLogin.tsx
// Thay vì
import { isValidEmail } from '../pages/auth/authValidation';
// Dùng
import { isValidEmail } from './authValidation';
```

---

## 🎯 Naming Conventions

### Variables & Functions

```typescript
// ✅ GOOD - camelCase
const user = null
const isAuthenticated = true
const loginRequest = { email, password }
const handleSubmit = () => {}
const validateForm = () => {}
const syncSessionFromStorage = () => {}

// ✅ GOOD - Prefix for specific patterns
const isLoading = true               // boolean - is/has prefix
const shouldRefresh = true
const handleClick = () => {}         // event handler - handle prefix
const updateField = () => {}         // state update - update prefix
const fetchUserData = () => {}       // async - fetch/get prefix

// ❌ AVOID
const User = null                    // Looks like class
const loading = true                 (não clear nó là boolean)
const userLogin = () => {}           (function = verb, không noun)
const form_values = {}               (snake_case instead of camelCase)
```

### Constants

```typescript
// ✅ GOOD - CONSTANT_CASE for true constants
const API_BASE_URL = 'http://localhost:8080/api'
const ACCESS_TOKEN_KEY = 'travi_access_token'
const REFRESH_TOKEN_KEY = 'travi_refresh_token'
const RESEND_COOLDOWN_SECONDS = 60
const ADMIN_ROLES = ['QUAN_TRI_VIEN'] as const

// ✅ GOOD - camelCase cho Tailwind constants (CSS utility values)
const authCardClass = 'w-full max-w-[480px] bg-white...'
const authButtonBaseClass = 'h-12 w-full border-none...'
const authInputBaseClass = 'w-full rounded-xl border...'

// ❌ AVOID
const api_base_url = '...'          (CONSTANT_CASE for values)
const class_constants = '...'
```

### React Components

```typescript
// ✅ GOOD - PascalCase
function CustomerLogin() { ... }
export function AdminRoute() { ... }
const HomePage = () => { ... }

// ❌ AVOID
function customerLogin() { ... }    (không PascalCase)
const customer_login = () => { ... }
```

### Event Handlers & Callbacks

```typescript
// ✅ GOOD - handle + EventName or update + FieldName
const handleSubmit = (e: FormEvent) => {}
const handleClick = () => {}
const handleChange = () => {}
const updateField = (field, value) => {}
const onEmailChange = (value) => {}

// ❌ AVOID
const submit = () => {}             (không clear nó là handler)
const doSubmit = () => {}
const form_submit = () => {}
```

### API & Service Methods

```typescript
// ✅ GOOD - Động từ + Danh từ
authService.login(payload)
authService.logout()
authService.register(payload)
authService.verifyEmail(payload)
tokenStorage.getAccessToken()
tokenStorage.setTokens(auth)
tokenStorage.clearTokens()
api.post(url, data)
api.get(url)

// ✅ GOOD - Boolean checks
tokenStorage.isAccessTokenExpired()
hasAllowedRole(user, roles)

// ❌ AVOID
authService.doLogin()
authService.performLogout()
tokenStorage.fetchAccessToken()  (get* cho getter)
```

### Boolean Variables

```typescript
// ✅ GOOD - is/has/should/can prefix
const isLoading = false
const isAuthenticated = true
const hasError = false
const shouldRefresh = true
const canAccess = true

// ❌ AVOID
const loading = false                (không clear boolean)
const authenticated = true
const error = false                  (ambiguous - error object hoặc flag?)
```

---

## 🧩 Component Patterns

### Functional Component Template

```typescript
// src/pages/auth/CustomerLogin.tsx

// 1. Imports (ordered as per guidelines)
import { useState, type FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import { tokenStorage } from '../../services/tokenStorage'
import type { LoginRequest } from '../../types/auth'
import { getDefaultPathByRole } from '../../routes/routeGuards'
import { getApiErrorMessage } from '../../utils/apiError'
import { AuthTextField } from './AuthTextField'
import { isValidEmail } from './authValidation'
import {
  authAlertErrorClass,
  authButtonBaseClass,
  authCardClass,
  authFormGroupClass,
  authLayoutClass,
  authTitleClass,
} from './authUi'

// 2. Component-specific types
type LoginFormValues = Pick<LoginRequest, 'email' | 'matKhau'>
type LoginFormErrors = Partial<Record<keyof LoginFormValues, string>>
type LocationState = {
  from?: {
    pathname?: string
  }
  registerMessage?: string
}

// 3. Component definition
export function CustomerLogin() {
  // 3.1. Hooks (useState, useAuth, useRouter, etc.)
  const navigate = useNavigate()
  const location = useLocation()
  const { login } = useAuth()

  // 3.2. State (grouped by purpose)
  const [form, setForm] = useState<LoginFormValues>({
    email: '',
    matKhau: '',
  })
  const [errors, setErrors] = useState<LoginFormErrors>({})
  const [submitError, setSubmitError] = useState('')
  const [loading, setLoading] = useState(false)

  // 3.3. Derived values / computed state
  const locationState = location.state as LocationState | null
  const redirectPath = locationState?.from?.pathname
  const registerMessage = locationState?.registerMessage

  // 3.4. Event handlers (updateField, validate, handleSubmit)
  const updateField = (field: keyof LoginFormValues, value: string) => {
    setForm((current) => ({
      ...current,
      [field]: value,
    }))
    setErrors((current) => ({
      ...current,
      [field]: undefined,
    }))
    setSubmitError('')
  }

  const validateForm = () => {
    const nextErrors: LoginFormErrors = {}

    if (!form.email.trim()) {
      nextErrors.email = 'Vui lòng nhập email.'
    } else if (!isValidEmail(form.email.trim())) {
      nextErrors.email = 'Email không hợp lệ.'
    }

    if (!form.matKhau) {
      nextErrors.matKhau = 'Vui lòng nhập mật khẩu.'
    }

    setErrors(nextErrors)
    return Object.keys(nextErrors).length === 0
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()

    if (!validateForm()) {
      return
    }

    setLoading(true)
    setSubmitError('')

    try {
      await login({
        email: form.email.trim(),
        matKhau: form.matKhau,
      })

      const user = tokenStorage.getUserFromToken()
      const defaultPath = getDefaultPathByRole(user?.role)
      navigate(redirectPath || defaultPath, { replace: true })
    } catch (error) {
      setSubmitError(getApiErrorMessage(error, 'Đăng nhập thất bại.'))
    } finally {
      setLoading(false)
    }
  }

  // 3.5. Render
  return (
    <div className={authLayoutClass}>
      <div className={authCardClass}>
        <h1 className={authTitleClass}>Đăng Nhập</h1>
        {registerMessage && (
          <div className={authAlertSuccessClass}>{registerMessage}</div>
        )}
        {submitError && (
          <div className={authAlertErrorClass}>{submitError}</div>
        )}
        <form onSubmit={handleSubmit}>
          <AuthTextField
            label="Email"
            type="email"
            value={form.email}
            onChange={(value) => updateField('email', value)}
            error={errors.email}
            placeholder="name@example.com"
          />
          <AuthTextField
            label="Mật khẩu"
            type="password"
            value={form.matKhau}
            onChange={(value) => updateField('matKhau', value)}
            error={errors.matKhau}
            placeholder="••••••••"
          />
          <button
            type="submit"
            disabled={loading}
            className={authButtonBaseClass}
          >
            {loading ? 'Đang xử lý...' : 'Đăng Nhập'}
          </button>
        </form>
        <p className={authFooterClass}>
          Chưa có tài khoản? <Link to="/register">Đăng ký ngay</Link>
        </p>
      </div>
    </div>
  )
}
```

**Quy tắc**:

1. **Comment sections** để tách phần code logic
2. **Hooks ở trên** (useState, useContext, etc.)
3. **State và derived values**
4. **Event handlers**
5. **Render cuối**
6. **Named export** (`export function` không `export default`)

### Reusable Component Template

```typescript
// src/pages/auth/AuthTextField.tsx

import type { ChangeEvent } from 'react'
import { Mail } from 'lucide-react'
import {
  authInputBaseClass,
  authInputIconClass,
  authInputWrapperClass,
  authErrorClass,
  authLabelClass,
  authFormGroupClass,
} from './authUi'

type AuthTextFieldProps = {
  label: string
  type?: string
  value: string
  onChange: (value: string) => void
  error?: string
  placeholder?: string
  icon?: React.ReactNode
}

export function AuthTextField({
  label,
  type = 'text',
  value,
  onChange,
  error,
  placeholder,
  icon,
}: AuthTextFieldProps) {
  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    onChange(e.target.value)
  }

  return (
    <div className={authFormGroupClass}>
      <label className={authLabelClass}>{label}</label>
      <div className={authInputWrapperClass}>
        {icon && <div className={authInputIconClass}>{icon}</div>}
        <input
          type={type}
          value={value}
          onChange={handleChange}
          placeholder={placeholder}
          className={authInputBaseClass}
        />
      </div>
      {error && <div className={authErrorClass}>{error}</div>}
    </div>
  )
}
```

**Quy tắc**:

1. **Props type** định nghĩa ở trên
2. **Destructure props** trong function signature
3. **JSX return** cuối cùng
4. **Default props** trong type hoặc function parameters

---

## 🎣 Hook Patterns

### Custom Hook Template

```typescript
// src/hooks/useCustomHook.ts

import { useContext } from 'react';
import { MyContext } from '../contexts/MyContext';

// ✅ GOOD - Descriptive error message
export function useCustomHook() {
  const context = useContext(MyContext);

  if (!context) {
    throw new Error('useCustomHook must be used inside MyContextProvider');
  }

  return context;
}
```

**Quy tắc**:

- **Tên hook**: `use*` prefix
- **Error handling**: Throw rõ ràng khi missing provider
- **Return type**: Explicit type annotation

### useAuth Hook Example

```typescript
// src/hooks/useAuth.ts
import { useContext } from 'react';
import { AuthContext } from '../contexts/authContext';

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider');
  }

  return context;
}
```

---

## 🔧 Service & Utility Patterns

### Service Pattern

```typescript
// src/services/authService.ts

import { api } from './api';
import { tokenStorage } from './tokenStorage';
import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  VerifyEmailRequest,
} from '../types/auth';

const AUTH_ENDPOINT = '/v1/auth';

export const authService = {
  async login(payload: LoginRequest) {
    const { data } = await api.post<AuthResponse>(`${AUTH_ENDPOINT}/login`, payload);
    tokenStorage.setTokens(data);
    return data;
  },

  async logout(): Promise<void> {
    const refreshToken = tokenStorage.getRefreshToken();
    const payload = refreshToken ? { refreshToken } : undefined;

    try {
      await api.post(`${AUTH_ENDPOINT}/logout`, payload);
    } finally {
      tokenStorage.clearTokens();
    }
  },

  async register(payload: RegisterRequest) {
    const { data } = await api.post<string>(`${AUTH_ENDPOINT}/register`, payload);
    return data;
  },

  // ... other methods
};
```

**Quy tắc**:

- **Export object pattern**: `export const serviceName = { method1, method2 }`
- **Endpoint constant**: `const ENDPOINT = '/v1/path'`
- **Async/await**: Prefer async/await over .then()
- **Generic types**: `api.post<ResponseType>(url, data)`
- **Try/finally**: Cleanup logic trong finally

### Utility Function Pattern

```typescript
// src/utils/apiError.ts

import axios from 'axios';

type ApiErrorPayload = {
  message?: string;
  error?: string;
  details?: string;
  [key: string]: unknown;
};

// ✅ GOOD - Pure function, no side effects
export function getApiErrorMessage(error: unknown, fallbackMessage: string): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data;

    if (typeof data === 'string') {
      return data;
    }

    if (data && typeof data === 'object') {
      const payload = data as ApiErrorPayload;
      return payload.message || payload.error || payload.details || fallbackMessage;
    }

    if (error.response?.status === 401) {
      return 'Email hoặc mật khẩu không đúng.';
    }

    return error.message || fallbackMessage;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return fallbackMessage;
}
```

**Quy tắc**:

- **Pure functions**: Không side effects
- **Type guards**: Check type trước khi access properties
- **Default values**: Fallback khi không có value
- **Descriptive parameters**: Tên tham số rõ ràng

### Token Storage Pattern

```typescript
// src/services/tokenStorage.ts

import type { AuthResponse, AuthUser } from '../types/auth';

const ACCESS_TOKEN_KEY = 'travi_access_token';
const REFRESH_TOKEN_KEY = 'travi_refresh_token';
const TOKEN_TYPE_KEY = 'travi_token_type';

const decodeBase64Url = (value: string) => {
  const base64 = value.replace(/-/g, '+').replace(/_/g, '/');
  const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
  return atob(padded);
};

export const tokenStorage = {
  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  },

  setTokens(auth: AuthResponse): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, auth.token);
    localStorage.setItem(REFRESH_TOKEN_KEY, auth.refreshToken);
    localStorage.setItem(TOKEN_TYPE_KEY, auth.type || 'Bearer');
  },

  getUserFromToken(): AuthUser | null {
    const token = this.getAccessToken();
    if (!token) return null;

    try {
      const [, payload] = token.split('.');
      if (!payload) return null;
      return JSON.parse(decodeBase64Url(payload)) as AuthUser;
    } catch {
      return null;
    }
  },

  isAccessTokenExpired(): boolean {
    const user = this.getUserFromToken();
    if (!user?.exp) return true;
    return user.exp * 1000 <= Date.now();
  },
};
```

**Quy tắc**:

- **Encapsulation**: Tất cả logic localStorage trong service
- **Return types**: Explicit type annotations
- **Error handling**: Try/catch cho parsing
- **Helpers**: Private-like functions (decodeBase64Url)

---

## ⚠️ Error Handling

### API Error Handling

```typescript
// ✅ GOOD - Comprehensive error handling
const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
  event.preventDefault();

  if (!validateForm()) {
    return;
  }

  setLoading(true);
  setSubmitError('');

  try {
    const result = await authService.login(payload);
    // Success handling
    navigate('/dashboard');
  } catch (error) {
    // Extract message từ error
    setSubmitError(getApiErrorMessage(error, 'Đăng nhập thất bại. Vui lòng thử lại.'));
  } finally {
    setLoading(false);
  }
};

// ✅ GOOD - Specific error handling
try {
  const auth = await refreshAccessToken();
  tokenStorage.setTokens(auth);
} catch (refreshError) {
  tokenStorage.clearTokens();
  window.dispatchEvent(new Event('auth:logout'));
  return Promise.reject(refreshError);
}

// ❌ AVOID - Silent failures
try {
  await authService.login(payload);
} catch (error) {
  // Swallowed error
}

// ❌ AVOID - Generic error messages
setSubmitError('Error occurred');
setSubmitError(error.message);
```

### Validation Error Handling

```typescript
// ✅ GOOD - Build error object before setting state
const validateForm = () => {
  const nextErrors: FormErrors = {};

  if (!form.email.trim()) {
    nextErrors.email = 'Vui lòng nhập email.';
  } else if (!isValidEmail(form.email.trim())) {
    nextErrors.email = 'Email không hợp lệ.';
  }

  if (!form.password) {
    nextErrors.password = 'Vui lòng nhập mật khẩu.';
  }

  setErrors(nextErrors);
  return Object.keys(nextErrors).length === 0;
};

// ❌ AVOID - Multiple setErrors calls
if (!form.email) {
  setErrors((current) => ({ ...current, email: '...' }));
}
if (!form.password) {
  setErrors((current) => ({ ...current, password: '...' }));
}
```

### Null/Undefined Handling

```typescript
// ✅ GOOD - Optional chaining & nullish coalescing
const user = tokenStorage.getUserFromToken();
const role = user?.role; // Optional chaining
const name = user?.username ?? 'Anonymous'; // Nullish coalescing
const hasRole = Boolean(user?.role); // Explicit boolean

// ✅ GOOD - Guard clauses
if (!token) {
  return null;
}
if (!user?.exp) {
  return true;
}

// ❌ AVOID - Risky property access
const role = user.role; // May throw if user is null
const name = user.username || ''; // Doesn't distinguish null/undefined
```

---

## 🎨 Styling & CSS

### Tailwind Constants Pattern

```typescript
// src/pages/auth/authUi.ts

// ✅ GOOD - Descriptive names matching components
export const authLayoutClass = 'flex items-center justify-center min-h-screen px-4 py-10'

export const authCardClass =
  'w-full max-w-[480px] bg-white/90 backdrop-blur-xl p-10 rounded-[28px] shadow-[0_24px_60px_rgba(15,_23,_42,_0.1)] border'

export const authTitleClass = 'mb-2 text-3xl font-extrabold tracking-tight text-gray-900'

export const authButtonBaseClass =
  'h-12 w-full border-none text-white rounded-xl font-bold text-base transition-all hover:-translate-y-0.5 disabled:opacity-70'

export const authErrorClass = 'mt-2 text-sm font-medium text-red-600'

export const authAlertErrorClass =
  'mb-6 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700'

// ❌ AVOID
export const CARD_CLASS = '...'     (không descriptive)
export const style1 = '...'         (meaningless name)
export const button = '...'         (too generic)
```

**Quy tắc**:

- **Tên**: `{component}{Element}Class`
- **Base classes**: Chứa styling cơ bản
- **Variant classes**: State-specific (error, disabled, etc.)
- **Tailwind features**: Responsive, dark mode, transitions

### Inline Styling

```typescript
// ✅ GOOD - Use Tailwind className
<div className="flex items-center justify-center min-h-screen">
  Content
</div>

// ✅ GOOD - Dynamic classes với clsx
import { clsx } from 'clsx'

<button
  className={clsx(
    authButtonBaseClass,
    loading && 'opacity-50 cursor-not-allowed'
  )}
>
  {loading ? 'Đang xử lý...' : 'Gửi'}
</button>

// ❌ AVOID - Inline style objects
<div style={{ display: 'flex', justifyContent: 'center' }}>

// ❌ AVOID - String concatenation
<div className={'flex' + (isActive ? 'active' : '')}>
```

### Responsive Design

```typescript
// ✅ GOOD - Mobile-first responsive
export const authLayoutClass = 'flex items-center justify-center min-h-screen px-4 py-10'
export const authCardClass = 'w-full max-w-[480px]'

<div className="text-sm sm:text-base md:text-lg lg:text-xl">
  Responsive text
</div>

// ❌ AVOID - Desktop-first
<div className="text-xl lg:text-sm">
```

---

## 📝 Comments & Documentation

### Comment Guidelines

```typescript
// ✅ GOOD - Explain WHY, not WHAT
// Deduplication: Multiple 401 errors should use same refresh request
if (!refreshPromise) {
  refreshPromise = axios.post(...)
}

// ✅ GOOD - Comment for non-obvious logic
// Check token expiry: exp is in seconds, Date.now() is in milliseconds
return user.exp * 1000 <= Date.now()

// ✅ GOOD - TODOs for future improvements
// TODO: Add retry logic for failed refresh
// TODO: Implement refresh token rotation

// ✅ GOOD - JSDoc for exported functions
/**
 * Extracts error message from API response
 * @param error - Error object (Axios or native Error)
 * @param fallbackMessage - Message khi không thể extract
 * @returns Formatted error message
 */
export function getApiErrorMessage(error: unknown, fallbackMessage: string): string

// ❌ AVOID - Obvious comments
const user = null  // Set user to null

// ❌ AVOID - Over-commenting
// Check if email is valid
if (!isValidEmail(email)) {
  // Set error
  setError('Invalid email')
}

// ❌ AVOID - Outdated comments
// Last updated 2024 - removes token
const clearStorage = () => { ... }
```

### File Header Documentation

```typescript
// ✅ GOOD - Optional: Top-level file documentation

/**
 * Authentication Service
 *
 * Handles all authentication-related API calls:
 * - Login/Logout
 * - Token refresh
 * - Email verification
 * - Account registration
 */

import { api } from './api';
// ...
```

### Component Documentation

```typescript
/**
 * CustomerLogin Component
 *
 * Displays login form for customers. Handles:
 * - Email & password validation
 * - Login API call
 * - Token storage
 * - Navigation based on user role
 *
 * Features:
 * - Error messages
 * - Loading state
 * - Remember me (nếu implement)
 */
export function CustomerLogin() {
  // ...
}
```

---

## ✅ Best Practices

### State Management

```typescript
// ✅ GOOD - Group related state
const [form, setForm] = useState<FormValues>(initialState);
const [errors, setErrors] = useState<FormErrors>({});
const [submitError, setSubmitError] = useState('');
const [loading, setLoading] = useState(false);

// ✅ GOOD - Batch state updates
const clearForm = () => {
  setForm(initialState);
  setErrors({});
  setSubmitError('');
};

// ❌ AVOID - Many individual state variables
const [email, setEmail] = useState('');
const [password, setPassword] = useState('');
const [emailError, setEmailError] = useState('');
const [passwordError, setPasswordError] = useState('');
// ...
```

### Event Handler Patterns

```typescript
// ✅ GOOD - Clear naming with update pattern
const updateField = (field: keyof FormValues, value: string) => {
  setForm((current) => ({
    ...current,
    [field]: value,
  }))
  setErrors((current) => ({
    ...current,
    [field]: undefined,
  }))
  setSubmitError('')
}

// ✅ GOOD - Closure to avoid inline arrows
const handleSubmit = async (e: FormEvent) => {
  e.preventDefault()
  // ...
}

// ❌ AVOID - Inline logic
<input onChange={(e) => {
  setForm({...form, email: e.target.value})
  setErrors({...errors, email: undefined})
  setSubmitError('')
}} />
```

### Async Operations

```typescript
// ✅ GOOD - Loading state + error handling
try {
  setLoading(true);
  setSubmitError('');
  const result = await authService.login(payload);
  navigate('/dashboard');
} catch (error) {
  setSubmitError(getApiErrorMessage(error, 'Thất bại'));
} finally {
  setLoading(false);
}

// ❌ AVOID - Not updating loading state
const result = await authService.login(payload);

// ❌ AVOID - Not clearing previous errors
try {
  const result = await authService.login(payload);
} catch (error) {
  setSubmitError(error.message); // Không clear error before
}
```

### Type Safety

```typescript
// ✅ GOOD - Full type annotations
type LoginRequest = {
  email: string;
  matKhau: string;
};

const handleSubmit = async (event: FormEvent<HTMLFormElement>): Promise<void> => {
  const payload: LoginRequest = { email, matKhau };
  const response: AuthResponse = await authService.login(payload);
};

// ✅ GOOD - Use Pick/Omit for derived types
type LoginFormValues = Pick<LoginRequest, 'email' | 'matKhau'>;

// ❌ AVOID - any type
const handleSubmit = async (event: any) => {
  const payload: any = { email, matKhau };
};

// ❌ AVOID - Type assertions everywhere
const data = response as any;
```

### Performance Considerations

```typescript
// ✅ GOOD - useMemo for expensive computations
const isFormValid = useMemo(
  () => email !== '' && isValidEmail(email),
  [email]
)

// ✅ GOOD - useCallback for event handlers
const handleClick = useCallback(() => {
  // ...
}, [dependency])

// ❌ AVOID - Inline object literals in render
<Component config={{ key: 'value' }} />  // New object every render

// ❌ AVOID - Arrow functions in JSX
<button onClick={() => handleClick()}>  // Creates new function every render
```

### Code Organization

```typescript
// ✅ GOOD - Logical grouping
// 1. Imports
// 2. Types
// 3. Constants
// 4. Component
// 5. Hooks
// 6. State
// 7. Derived values
// 8. Handlers
// 9. Effects
// 10. Render

// ❌ AVOID - Random order
```

---

## 🚀 Checklist trước khi Push Code

- [ ] Tất cả files đặt tên theo conventions (PascalCase cho components, camelCase cho utilities)
- [ ] TypeScript types được define rõ (không `any`)
- [ ] Imports organized theo guidelines (5 nhóm, alphabetical)
- [ ] Error handling + loading states
- [ ] Form validation đầy đủ
- [ ] Comments cho non-obvious logic
- [ ] No console.log/debugger statements
- [ ] ESLint pass: `npm run lint`
- [ ] Code formatted: `npm run format`
- [ ] No unused imports/variables
- [ ] Component exports named (không default)
- [ ] Tailwind classes dùng constants từ authUi.ts

---

## 📚 References

- [React Documentation](https://react.dev)
- [TypeScript Handbook](https://www.typescriptlang.org/docs)
- [Tailwind CSS](https://tailwindcss.com)
- [Axios Documentation](https://axios-http.com)
- [React Router](https://reactrouter.com)

---

**Last Updated**: May 2026
**Version**: 1.0.0
