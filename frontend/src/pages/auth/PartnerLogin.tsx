import { useState, type FormEvent } from 'react'
import { Building2 } from 'lucide-react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import { tokenStorage } from '../../services/tokenStorage'
import type { LoginRequest } from '../../types/auth'
import { normalizeRole } from '../../routes/routeGuards'
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
  authIconBoxClass,
  authLayoutClass,
  authSubtitleClass,
  authTitleClass,
} from './authUi'

type LoginFormValues = Pick<LoginRequest, 'email' | 'matKhau'>

type LoginFormErrors = Partial<Record<keyof LoginFormValues, string>>

type LocationState = {
  email?: string
  registerMessage?: string
}

export function PartnerLogin() {
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

  const registerMessage = locationState?.registerMessage

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
      const role = normalizeRole(user?.role)

      if (role !== 'DOI_TAC') {
        await logout()
        setSubmitError('Tài khoản này không có quyền truy cập khu vực đối tác.')
        return
      }

      navigate('/partner', { replace: true })
    } catch (error) {
      setSubmitError(getApiErrorMessage(error, 'Email hoặc mật khẩu không đúng.'))
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
        loaiTaiKhoan: 'DOI_TAC',
      })

      const user = tokenStorage.getUserFromToken()
      const role = normalizeRole(user?.role)

      if (role !== 'DOI_TAC') {
        await logout()
        setSubmitError('Tài khoản này không có quyền truy cập khu vực đối tác.')
        return
      }

      navigate('/partner', { replace: true })
    } catch (error) {
      setSubmitError(getApiErrorMessage(error, 'Đăng nhập Google thất bại.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={`${authLayoutClass} bg-gradient-to-br from-emerald-50 via-white to-teal-50`}>
      <div className={`${authCardClass} max-w-[420px] border-emerald-100`}>
        <div className="mb-8 text-center">
          <div className={`${authIconBoxClass} bg-emerald-50 border-emerald-100 text-emerald-600`}>
            <Building2 size={30} strokeWidth={2.1} aria-hidden="true" />
          </div>

          <h2 className={authTitleClass}>Đối tác TraVi</h2>
          <p className={authSubtitleClass}>Dành cho khách sạn và nhà hàng</p>
        </div>

        {registerMessage && <div className={authAlertSuccessClass}>{registerMessage}</div>}
        {submitError && <div className={authAlertErrorClass}>{submitError}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div className={authFormGroupClass}>
            <AuthTextField
              id="partner-email"
              name="email"
              label="Email đăng nhập"
              type="email"
              value={form.email}
              placeholder="doitac@gmail.com"
              autoComplete="email"
              error={errors.email}
              icon="mail"
              tone="emerald"
              onChange={(value) => updateField('email', value)}
            />
          </div>

          <div className="mb-6">
            <AuthTextField
              id="partner-password"
              name="matKhau"
              label="Mật khẩu"
              type="password"
              value={form.matKhau}
              placeholder="Nhập mật khẩu"
              autoComplete="current-password"
              error={errors.matKhau}
              icon="lock"
              tone="emerald"
              onChange={(value) => updateField('matKhau', value)}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className={`${authButtonBaseClass} bg-emerald-600 shadow-lg shadow-emerald-200 hover:bg-emerald-700`}
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
            <Link
              to="/partner/forgot-password"
              state={{ email: form.email.trim() }}
              className="font-medium text-emerald-600 hover:text-emerald-800"
            >
              Quên mật khẩu?
            </Link>
          </div>

          Chưa phải đối tác?{' '}
          <Link
            to="/partner/register"
            className="font-bold text-emerald-600 hover:text-emerald-800"
          >
            Đăng ký ngay
          </Link>
        </div>

        <div className="mt-6 text-center">
          <Link
            to="/login"
            className="text-base font-medium text-gray-400 transition-colors hover:text-gray-600"
          >
            ← Quay lại trang khách hàng
          </Link>
        </div>
      </div>
    </div>
  )
}
