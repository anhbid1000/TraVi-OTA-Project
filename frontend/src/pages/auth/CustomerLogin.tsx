import { useState, type FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import { tokenStorage } from '../../services/tokenStorage'
import type { LoginRequest } from '../../types/auth'
import { getDefaultPathByRole, normalizeRole } from '../../routes/routeGuards'
import { getApiErrorMessage } from '../../utils/apiError'
import { AuthTextField } from './AuthTextField'
import { GoogleAuthButton } from './GoogleAuthButton'
import { isValidEmail } from './authValidation'
import {
  authAlertErrorClass,
  authAlertSuccessClass,
  authButtonBaseClass,
  authCardClass,
  authFooterClass,
  authFormGroupClass,
  authLayoutClass,
  authSmallLinkClass,
  authSubtitleClass,
  authTitleClass,
} from './authUi'

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
    const role = normalizeRole(user?.role)

    if (role !== 'KHACH_HANG') {
      await logout()
      setSubmitError('Tài khoản này không thể đăng nhập ở cổng khách hàng. Vui lòng dùng đúng trang đăng nhập.')
      return null
    }

    return getDefaultPathByRole(user?.role)
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
    <div className={`${authLayoutClass} bg-gradient-to-br from-blue-50 via-white to-indigo-50`}>
      <div className={`${authCardClass} max-w-[420px] border-blue-100`}>
        <div className="mb-8 text-center">
          <h2 className={authTitleClass}>Đăng nhập</h2>
          <p className={authSubtitleClass}>Khám phá những chuyến đi tuyệt vời cùng TraVi</p>
        </div>

        {registerMessage && <div className={authAlertSuccessClass}>{registerMessage}</div>}
        {submitError && <div className={authAlertErrorClass}>{submitError}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div className={authFormGroupClass}>
            <AuthTextField
              id="customer-email"
              name="email"
              label="Email"
              type="email"
              value={form.email}
              placeholder="khachhang@gmail.com"
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
              placeholder="Nhập mật khẩu"
              autoComplete="current-password"
              error={errors.matKhau}
              icon="lock"
              tone="blue"
              onChange={(value) => updateField('matKhau', value)}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className={`${authButtonBaseClass} cursor-pointer bg-blue-600 shadow-lg shadow-blue-200 hover:bg-blue-700`}
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
          <div className="mb-3">
            <Link to="/forgot-password" state={{ email: form.email.trim() }} className={authSmallLinkClass}>
              Quên mật khẩu?
            </Link>
          </div>

          Chưa có tài khoản?{' '}
          <Link
            to="/register"
            className="cursor-pointer font-bold text-blue-600 hover:text-blue-800"
          >
            Đăng ký ngay
          </Link>
        </div>

        <div className="flex justify-center gap-4 pt-6 mt-6 border-t border-gray-100">
          <Link
            to="/partner/login"
            className={`${authSmallLinkClass} text-gray-400 hover:text-emerald-600`}
          >
            Dành cho đối tác
          </Link>
          <span className="text-gray-200">|</span>
          <Link
            to="/admin/login"
            className={`${authSmallLinkClass} text-gray-400 hover:text-slate-800`}
          >
            Quản trị viên
          </Link>
        </div>
      </div>
    </div>
  );
}
