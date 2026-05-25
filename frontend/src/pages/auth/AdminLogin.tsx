import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import { tokenStorage } from '../../services/tokenStorage'
import type { LoginRequest } from '../../types/auth'
import { normalizeRole } from '../../routes/routeGuards'
import { getApiErrorMessage } from '../../utils/apiError'
import { AuthTextField, LoginTemplate } from '../../features/auth/components'
import {
  authAlertErrorClass,
  authButtonBaseClass,
  authFormGroupClass,
  isValidEmail,
} from '../../features/auth/utils'

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
    <LoginTemplate
      activeRole="QUAN_TRI_VIEN"
      title="Đăng nhập quản trị"
      subtitle="Chỉ dành cho tài khoản quản trị viên hệ thống"
    >
      {submitError && <div className={authAlertErrorClass}>{submitError}</div>}

      <form onSubmit={handleSubmit} noValidate>
        <div className={authFormGroupClass}>
          <AuthTextField
            id="admin-email"
            name="email"
            label="Email quản trị"
            type="email"
            value={form.email}
            placeholder="name@example.com"
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
            placeholder="••••••••"
            autoComplete="current-password"
            error={errors.matKhau}
            icon="lock"
            tone="slate"
            showPasswordToggle
            onChange={(value) => updateField('matKhau', value)}
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className={`${authButtonBaseClass} bg-[#334155] shadow-[0_12px_30px_rgba(51,65,85,0.18)] hover:bg-[#0f172a]`}
        >
          {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
        </button>
      </form>
    </LoginTemplate>
  )
}
