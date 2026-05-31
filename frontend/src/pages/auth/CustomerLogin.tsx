import { useState, type FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { cn } from '../../utils/cn'
import { useAuth } from '../../hooks/useAuth'
import { tokenStorage } from '../../services/tokenStorage'
import type { LoginRequest } from '../../types/auth'
import { getDefaultPathByRole, getUserRole, normalizeRole } from '../../routes/routeGuards'
import { getApiErrorMessage } from '../../utils/apiError'
import { AuthTextField, GoogleAuthButton, LoginTemplate } from '../../features/auth/components'
import {
  authAlertErrorClass,
  authAlertSuccessClass,
  authButtonBaseClass,
  authFooterClass,
  authFormGroupClass,
  authSmallLinkClass,
  isValidEmail,
} from '../../features/auth/utils'

type LoginFormValues = Pick<LoginRequest, 'email' | 'matKhau'>

type LoginFormErrors = Partial<Record<keyof LoginFormValues, string>>

type LocationState = {
  from?: {
    pathname?: string
  }
  email?: string
  registerMessage?: string
}

export function CustomerLogin() {
  const navigate = useNavigate()
  const location = useLocation()
  const { login, loginWithGoogle, logout } = useAuth()
  const locationState = location.state as LocationState | null

  const [form, setForm] = useState<LoginFormValues>({
    email: locationState?.email ?? '',
    matKhau: '',
  })

  const [errors, setErrors] = useState<LoginFormErrors>({})
  const [submitError, setSubmitError] = useState('')
  const [loading, setLoading] = useState(false)

  const redirectPath = locationState?.from?.pathname
  const registerMessage = locationState?.registerMessage

  const validateCustomerRole = async () => {
    const user = tokenStorage.getUserFromToken()
    const role = normalizeRole(getUserRole(user))

    if (role !== 'KHACH_HANG') {
      await logout()
      setSubmitError('Tài khoản này không thể đăng nhập ở cổng khách hàng. Vui lòng dùng đúng trang đăng nhập.')
      return null
    }

    return getDefaultPathByRole(getUserRole(user))
  }

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

      const defaultPath = await validateCustomerRole()
      if (!defaultPath) {
        return
      }

      navigate(redirectPath || defaultPath, { replace: true })
    } catch (error) {
      setSubmitError(getApiErrorMessage(error, 'Email hoặc mật khẩu không chính xác.'))
    } finally {
      setLoading(false)
    }
  }

  const handleGoogleLogin = async (idToken: string) => {
    setLoading(true)
    setSubmitError('')

    try {
      await loginWithGoogle({
        idToken,
        loaiTaiKhoan: 'KHACH_HANG',
      })

      const defaultPath = await validateCustomerRole()
      if (!defaultPath) {
        return
      }

      navigate(redirectPath || defaultPath, { replace: true })
    } catch (error) {
      setSubmitError(getApiErrorMessage(error, 'Đăng nhập Google thất bại.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <LoginTemplate
      activeRole="KHACH_HANG"
      title="Chào mừng trở lại"
      subtitle="Đăng nhập tài khoản khách hàng để đặt phòng và quản lý hành trình"
    >
      {registerMessage && <div className={authAlertSuccessClass}>{registerMessage}</div>}
      {submitError && <div className={authAlertErrorClass}>{submitError}</div>}

      <form onSubmit={handleSubmit} noValidate>
        <div className={authFormGroupClass}>
          <AuthTextField
            id="customer-email"
            name="email"
            label="Địa chỉ email"
            type="email"
            value={form.email}
            placeholder="name@example.com"
            autoComplete="email"
            error={errors.email}
            icon="mail"
            tone="blue"
            onChange={(value) => updateField('email', value)}
          />
        </div>

        <div className="mb-6">
          <AuthTextField
            id="customer-password"
            name="matKhau"
            label="Mật khẩu"
            type="password"
            value={form.matKhau}
            placeholder="••••••••"
            autoComplete="current-password"
            error={errors.matKhau}
            icon="lock"
            tone="blue"
            labelAction={
              <Link
                to="/forgot-password"
                state={{ email: form.email.trim() }}
                className={authSmallLinkClass}
              >
                Quên mật khẩu?
              </Link>
            }
            showPasswordToggle
            onChange={(value) => updateField('matKhau', value)}
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className={cn(
            authButtonBaseClass,
            'cursor-pointer bg-blue-500 shadow-[0_12px_30px_rgba(0,59,27,0.16)] hover:bg-blue-600',
          )}
        >
          {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
        </button>

        <GoogleAuthButton
          disabled={loading}
          isLoading={loading}
          onCredential={handleGoogleLogin}
          onError={setSubmitError}
        />
      </form>

      <div className={authFooterClass}>
        Chưa có tài khoản?{' '}
        <Link to="/register" className={cn('cursor-pointer font-bold text-blue-500 hover:text-blue-600')}>
          Đăng ký ngay
        </Link>
      </div>
    </LoginTemplate>
  );
}
