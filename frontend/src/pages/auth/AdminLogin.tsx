import { useState, type FormEvent } from 'react'
import { ShieldCheck } from 'lucide-react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import { tokenStorage } from '../../services/tokenStorage'
import type { LoginRequest } from '../../types/auth'
import { normalizeRole } from '../../routes/routeGuards'
import { getApiErrorMessage } from '../../utils/apiError'
import { AuthTextField } from './AuthTextField'
import { isValidEmail } from './authValidation'
import {
  authAlertErrorClass,
  authButtonBaseClass,
  authCardClass,
  authFormGroupClass,
  authIconBoxClass,
  authLayoutClass,
  authSubtitleClass,
  authTitleClass,
} from './authUi'

type LoginFormValues = Pick<LoginRequest, 'email' | 'matKhau'>

type LoginFormErrors = Partial<Record<keyof LoginFormValues, string>>

export function AdminLogin() {
  const navigate = useNavigate()
  const { login, logout } = useAuth()

  const [form, setForm] = useState<LoginFormValues>({
    email: '',
    matKhau: '',
  })

  const [errors, setErrors] = useState<LoginFormErrors>({})
  const [submitError, setSubmitError] = useState('')
  const [loading, setLoading] = useState(false)

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

      if (role !== 'QUAN_TRI_VIEN') {
        await logout()
        setSubmitError('Tài khoản này không có quyền truy cập trang quản trị.')
        return
      }

      navigate('/admin', { replace: true })
    } catch (error) {
      setSubmitError(getApiErrorMessage(error, 'Tài khoản quản trị không hợp lệ.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={`${authLayoutClass} bg-gradient-to-br from-slate-100 via-gray-50 to-slate-200`}>
      <div className={`${authCardClass} max-w-[420px] border-slate-200`}>
        <div className="mb-8 text-center">
          <div className={`${authIconBoxClass} bg-slate-50 border-slate-100 text-slate-700`}>
            <ShieldCheck size={30} strokeWidth={2.1} aria-hidden="true" />
          </div>

          <h2 className={`${authTitleClass} text-slate-900`}>Quản trị viên</h2>
          <p className={authSubtitleClass}>Cổng truy cập hệ thống TraVi OTA</p>
        </div>

        {submitError && <div className={authAlertErrorClass}>{submitError}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div className={authFormGroupClass}>
            <AuthTextField
              id="admin-email"
              name="email"
              label="Email quản trị"
              type="email"
              value={form.email}
              placeholder="admin@gmail.com"
              autoComplete="email"
              error={errors.email}
              icon="mail"
              tone="slate"
              onChange={(value) => updateField('email', value)}
            />
          </div>

          <div className="mb-6">
            <AuthTextField
              id="admin-password"
              name="matKhau"
              label="Mật khẩu"
              type="password"
              value={form.matKhau}
              placeholder="Nhập mật khẩu"
              autoComplete="current-password"
              error={errors.matKhau}
              icon="lock"
              tone="slate"
              onChange={(value) => updateField('matKhau', value)}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className={`${authButtonBaseClass} bg-slate-800 hover:bg-slate-900 hover:shadow-lg hover:shadow-slate-800/30`}
          >
            {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
        </form>

        <div className="mt-6 text-center">
          <Link
            to="/login"
            className="text-base font-medium transition-colors text-slate-500 hover:text-slate-800"
          >
            ← Quay lại trang khách hàng
          </Link>
        </div>
      </div>
    </div>
  )
}
