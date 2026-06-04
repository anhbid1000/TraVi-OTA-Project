# TraVi-OTA Frontend Coding Standards
Tai lieu nay quy dinh quy uoc code cho frontend React + TypeScript cua TraVi-OTA.
Muc tieu: code de doc, de review, de mo rong va dong bo voi backend auth.
---
## 1) Nguyen tac chung
- Uu tien type-safe, khong dung `any`.
- Uu tien named export, han che default export.
- Component page khong nhung logic API phuc tap truong hop co service.
- Dung lai component va utility trong `features/auth`.
- Form phai co validation + loading + error state ro rang.
---
## 2) Cau truc thu muc (hien tai)
```text
frontend/src/
  App.tsx
  main.tsx
  contexts/
    AuthContext.tsx
    authContext.ts
    GoogleOAuthProviderWrapper.tsx
  features/
    auth/
      components/
        AuthTextField.tsx
        GoogleAuthButton.tsx
        LoginTemplate.tsx
        index.ts
      utils/
        authUi.ts
        authValidation.ts
        index.ts
  hooks/
    useAuth.ts
  pages/
    ForbiddenPage.tsx
    auth/
      AdminLogin.tsx
      CustomerLogin.tsx
      CustomerRegister.tsx
      ForgotPasswordPage.tsx
      PartnerLogin.tsx
      PartnerRegister.tsx
      VerifyEmailPage.tsx
      index.ts
  routes/
  services/
  types/
  utils/
```
Nguyen tac:
- Shared auth UI/validation dat trong `features/auth/*`.
- Page-level component dat trong `pages/auth/*`.
- Service API dat trong `services/*`.
---
## 3) Naming conventions
### File
| Loai | Pattern | Vi du |
|---|---|---|
| React component | `PascalCase.tsx` | `CustomerLogin.tsx` |
| Hook | `useXxx.ts` | `useAuth.ts` |
| Service/utility | `camelCase.ts` | `authService.ts` |
| Barrel file | `index.ts` | `features/auth/components/index.ts` |
### Variables/functions
- camelCase (`isLoading`, `handleSubmit`, `updateField`).
- Boolean dung prefix `is/has/can/should`.
- Constant dung UPPER_SNAKE_CASE.
### Types
- Shared types dat trong `types/auth.ts`.
- Local types dat ngay tren component can dung.
- Khong dung prefix `I` (`AuthUser`, khong dung `IAuthUser`).
---
## 4) Import order
Thu tu import trong moi file:
1. React + external libs
2. Internal services/hooks/context
3. Internal types (`import type`)
4. Internal utilities
5. Components/styles/assets
Vi du:
```typescript
import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import { authService } from '../../services/authService'
import type { LoginRequest } from '../../types/auth'
import { getApiErrorMessage } from '../../utils/apiError'
import { isValidEmail } from '../../features/auth/utils'
import { AuthTextField, LoginTemplate } from '../../features/auth/components'
```
---
## 5) Component standards
- Dung function component + named export.
- Cac khoi code theo thu tu:
  1. local types
  2. hooks
  3. state
  4. derived values
  5. handlers
  6. effects
  7. render
- Form pattern bat buoc:
  - `form`
  - `errors`
  - `submitError`
  - `loading`
  - `updateField()`
  - `validateForm()`
  - `handleSubmit()`
Mau:
```typescript
const [form, setForm] = useState<FormValues>(initialForm)
const [errors, setErrors] = useState<FormErrors>({})
const [submitError, setSubmitError] = useState('')
const [loading, setLoading] = useState(false)
const updateField = (field: keyof FormValues, value: string) => {
  setForm((current) => ({ ...current, [field]: value }))
  setErrors((current) => ({ ...current, [field]: undefined }))
  setSubmitError('')
}
```
---
## 6) Auth component reuse rules
Bat buoc reuse cac module sau thay vi viet lai:
- `features/auth/components/LoginTemplate`
- `features/auth/components/AuthTextField`
- `features/auth/components/GoogleAuthButton`
- `features/auth/utils/authValidation`
- `features/auth/utils/authUi`
Neu can import nhieu, uu tien barrel:
```typescript
import { AuthTextField, GoogleAuthButton, LoginTemplate } from '../../features/auth/components'
import { authButtonBaseClass, isValidEmail } from '../../features/auth/utils'
```
---
## 7) Hook va Context standards
### `useAuth`
- Phai throw error neu dung ngoai `AuthProvider`.
- Return type ro rang theo `AuthContextValue`.
### `AuthProvider`
- Khong de side effect khong can thiet trong render.
- Dong bo state voi `tokenStorage` qua helper `syncSessionFromStorage`.
- Xu ly event:
  - `auth:logout`
  - `auth:refresh`
### Google wrapper
- `GoogleOAuthProviderWrapper` la noi duy nhat inject Google script.
- Khong inject script Google truc tiep trong page component.
---
## 8) Service va API standards
### `services/api.ts`
- Base URL doc tu env:
  - `VITE_API_BASE_URL`
  - fallback `VITE_API_URL`
  - default `http://localhost:8080/api`
- Request interceptor: them `Authorization` neu co token.
- Response interceptor: xu ly 401 + refresh token + retry request goc.
- Dedup refresh bang promise singleton (tranh refresh nhieu lan song song).
### `services/authService.ts`
- Method dat ten theo hanh dong nghiep vu:
  - `login`
  - `loginWithGoogle`
  - `logout`
  - `refreshToken`
  - `register`
  - `verifyEmail`
  - `resendOtp`
  - `requestPasswordResetOtp`
  - `verifyPasswordResetOtp`
  - `resetPassword`
- Service khong manipulate UI state. Chi call API + xu ly storage can thiet.
---
## 9) Type safety standards
- Khong dung `any`.
- Dung `unknown` + type guard cho error object.
- Uu tien `Pick`, `Omit`, `Partial`, `Record` cho form types.
- Tach type request/response ro trong `types/auth.ts`.
Vi du:
```typescript
type LoginFormValues = Pick<LoginRequest, 'email' | 'matKhau'>
type LoginFormErrors = Partial<Record<keyof LoginFormValues, string>>
```
---
## 10) Validation standards
- Validation auth dung chung tai `features/auth/utils/authValidation.ts`.
- Rule password frontend phai dong bo backend:
  - 9-20 ky tu
  - it nhat 1 chu hoa, 1 chu thuong, 1 so, 1 ky tu dac biet
- Khong duplicate regex giua nhieu page neu da co helper.
---
## 11) Styling standards
- Uu tien Tailwind utility classes.
- Reuse class constants trong `authUi.ts` cho auth pages.
- Khong hardcode inline style object neu co the dung Tailwind.
- Dynamic class dung `clsx` hoac template string ro rang.
---
## 12) Error handling standards
- Moi submit async phai co `try/catch/finally`.
- Luon clear error cu truoc request moi (`setSubmitError('')`).
- Dung helper `getApiErrorMessage(error, fallback)`.
- Khong swallow error.
Mau:
```typescript
try {
  setLoading(true)
  setSubmitError('')
  await authService.login(payload)
} catch (error) {
  setSubmitError(getApiErrorMessage(error, 'Dang nhap that bai.'))
} finally {
  setLoading(false)
}
```
---
## 13) Security standards (frontend)
- Khong log token/password ra console.
- Token duoc quan ly tap trung qua `tokenStorage`.
- Khong parse JWT bang logic duplicate o nhieu noi.
- Bat buoc env `VITE_GOOGLE_CLIENT_ID` neu su dung Google login.
---
## 14) Testing va quality gate
Truoc khi push:
```powershell
cd frontend
npm run lint
npm run build
```
Checklist:
- [ ] Import order dung convention
- [ ] Khong `any`
- [ ] Form co loading + error + validation
- [ ] Reuse `features/auth/components` va `features/auth/utils`
- [ ] Khong hardcode API base URL trong component
- [ ] Khong con `console.log` debug
- [ ] Build pass
---
## 15) Anti-patterns cam tranh
- Duplicate auth validation regex trong tung page.
- Goi API truc tiep trong component ma bo qua service.
- Hardcode route/API string lap lai nhieu noi.
- Nhung logic refresh token vao page component.
- Dung default export tran lan cho page/component.
---
## 16) Update policy
Khi them auth flow moi, phai cap nhat dong bo:
1. `types/auth.ts`
2. `services/authService.ts`
3. Page lien quan trong `pages/auth/*`
4. Shared components/utils neu co logic dung chung
5. `README-SPRINT1.md`
6. File coding standards nay neu co rule moi
---
**Last Updated:** May 2026  
**Version:** 1.1.0
