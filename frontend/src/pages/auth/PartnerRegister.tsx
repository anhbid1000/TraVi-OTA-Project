import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { cn } from '../../utils/cn'
import { useAuth } from '../../hooks/useAuth'
import { authService } from '../../services/authService'
import { tokenStorage } from '../../services/tokenStorage'
import { partnerAssetService } from '../../services/partnerAssetService'
import type { RegisterRequest } from '../../types/auth'
import { getUserRole, normalizeRole } from '../../routes/routeGuards'
import { getApiErrorMessage } from '../../utils/apiError'
import { AuthTextField, GoogleAuthButton, LoginTemplate } from '../../features/auth/components'
import {
  authAlertErrorClass,
  authButtonBaseClass,
  authFooterClass,
  authFormGroupClass,
  authLastFormGroupClass,
  isValidEmail,
  isValidPhoneNumber,
  validatePassword,
} from '../../features/auth/utils'

type PartnerRegisterFormValues = {
  username: string
  hoTen: string
  email: string
  soDienThoai: string
  matKhau: string
  confirmPassword: string
}

type PartnerRegisterFormErrors = Partial<Record<keyof PartnerRegisterFormValues, string>>

const initialForm: PartnerRegisterFormValues = {
  username: '',
  hoTen: '',
  email: '',
  soDienThoai: '',
  matKhau: '',
  confirmPassword: '',
}

export function PartnerRegister() {
  const navigate = useNavigate()
  const { loginWithGoogle, logout } = useAuth()

  const [form, setForm] = useState<PartnerRegisterFormValues>(initialForm)
  const [errors, setErrors] = useState<PartnerRegisterFormErrors>({})
  const [submitError, setSubmitError] = useState('')
  const [loading, setLoading] = useState(false)

  const updateField = (field: keyof PartnerRegisterFormValues, value: string) => {
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
    const nextErrors: PartnerRegisterFormErrors = {}

    if (!form.username.trim()) {
      nextErrors.username = 'Vui lòng nhập tên đăng nhập.'
    } else if (form.username.trim().length < 4) {
      nextErrors.username = 'Tên đăng nhập phải có ít nhất 4 ký tự.'
    } else if (!/^[a-zA-Z0-9_.-]+$/.test(form.username.trim())) {
      nextErrors.username = 'Tên đăng nhập chỉ gồm chữ, số, dấu gạch dưới hoặc dấu chấm.'
    }

    if (!form.hoTen.trim()) {
      nextErrors.hoTen = 'Vui lòng nhập tên người đại diện.'
    } else if (form.hoTen.trim().length < 2) {
      nextErrors.hoTen = 'Tên người đại diện phải có ít nhất 2 ký tự.'
    }

    if (!form.email.trim()) {
      nextErrors.email = 'Vui lòng nhập email.'
    } else if (!isValidEmail(form.email.trim())) {
      nextErrors.email = 'Email không hợp lệ.'
    }

    if (!form.soDienThoai.trim()) {
      nextErrors.soDienThoai = 'Vui lòng nhập số điện thoại.'
    } else if (!isValidPhoneNumber(form.soDienThoai.trim())) {
      nextErrors.soDienThoai = 'Số điện thoại không đúng định dạng.'
    }

    const passwordError = validatePassword(form.matKhau)
    if (passwordError) {
      nextErrors.matKhau = passwordError
    }

    if (!form.confirmPassword) {
      nextErrors.confirmPassword = 'Vui lòng nhập lại mật khẩu.'
    } else if (form.confirmPassword !== form.matKhau) {
      nextErrors.confirmPassword = 'Mật khẩu nhập lại không khớp.'
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

    const payload: RegisterRequest = {
      username: form.username.trim(),
      hoTen: form.hoTen.trim(),
      email: form.email.trim(),
      soDienThoai: form.soDienThoai.trim(),
      matKhau: form.matKhau,
      loaiTaiKhoan: 'DOI_TAC',
    }

    try {
      const result = await authService.register(payload)

      const successMessage =
        typeof result === 'string'
          ? result
          : 'Đăng ký đối tác thành công. Vui lòng kiểm tra email để xác thực tài khoản.'

      navigate('/verify-email', {
        replace: true,
        state: {
          email: form.email.trim(),
          loginPath: '/partner/login',
          registerMessage: successMessage,
        },
      })
    } catch (error) {
      setSubmitError(
        getApiErrorMessage(error, 'Đăng ký đối tác thất bại. Vui lòng kiểm tra lại thông tin.'),
      )
    } finally {
      setLoading(false)
    }
  }

  const handleGoogleRegister = async (idToken: string) => {
    setLoading(true)
    setSubmitError('')

    try {
      await loginWithGoogle({
        idToken,
        loaiTaiKhoan: 'DOI_TAC',
      })

      const user = tokenStorage.getUserFromToken()
      const role = normalizeRole(getUserRole(user))

      if (role !== 'DOI_TAC') {
        await logout()
        setSubmitError('Tài khoản này không có quyền truy cập khu vực đối tác.')
        return
      }

      const profiles = await partnerAssetService.getBusinessProfiles().catch(() => [])
      const targetPath = profiles.length > 0 ? '/partner/dashboard' : '/partner/business-profile'
      navigate(targetPath, { replace: true })
    } catch (error) {
      setSubmitError(getApiErrorMessage(error, 'Đăng ký Google thất bại.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <LoginTemplate
      activeRole="DOI_TAC"
      title="Đăng ký đối tác"
      subtitle="Dành cho khách sạn, nhà hàng và đơn vị cung cấp dịch vụ du lịch"
    >
        {submitError && <div className={authAlertErrorClass}>{submitError}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div className={authFormGroupClass}>
            <AuthTextField
              id="partner-register-username"
              name="username"
              label="Tên đăng nhập"
              value={form.username}
              placeholder="doitac_travi"
              autoComplete="username"
              error={errors.username}
              icon="user"
              tone="emerald"
              onChange={(value) => updateField('username', value)}
            />
          </div>

          <div className={authFormGroupClass}>
            <AuthTextField
              id="partner-register-representative"
              name="hoTen"
              label="Tên người đại diện"
              value={form.hoTen}
              placeholder="Nguyễn Văn B"
              autoComplete="name"
              error={errors.hoTen}
              icon="user"
              tone="emerald"
              onChange={(value) => updateField('hoTen', value)}
            />
          </div>

          <div className={authFormGroupClass}>
            <AuthTextField
              id="partner-register-email"
              name="email"
              label="Email đối tác"
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

          <div className={authFormGroupClass}>
            <AuthTextField
              id="partner-register-phone"
              name="soDienThoai"
              label="Số điện thoại liên hệ"
              type="tel"
              value={form.soDienThoai}
              placeholder="0912345678"
              autoComplete="tel"
              error={errors.soDienThoai}
              icon="phone"
              tone="emerald"
              onChange={(value) => updateField('soDienThoai', value)}
            />
          </div>

          <div className={authFormGroupClass}>
            <AuthTextField
              id="partner-register-password"
              name="matKhau"
              label="Mật khẩu"
              type="password"
              value={form.matKhau}
              placeholder="Password@123"
              autoComplete="new-password"
              error={errors.matKhau}
              icon="lock"
              tone="emerald"
              showPasswordToggle
              onChange={(value) => updateField('matKhau', value)}
            />
          </div>

          <div className={authLastFormGroupClass}>
            <AuthTextField
              id="partner-register-confirm-password"
              name="confirmPassword"
              label="Nhập lại mật khẩu"
              type="password"
              value={form.confirmPassword}
              placeholder="Nhập lại mật khẩu"
              autoComplete="new-password"
              error={errors.confirmPassword}
              icon="lock"
              tone="emerald"
              showPasswordToggle
              onChange={(value) => updateField('confirmPassword', value)}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className={cn(
              authButtonBaseClass,
              'bg-emerald-600 shadow-lg shadow-emerald-200 hover:bg-emerald-700',
            )}
          >
            {loading ? 'Đang đăng ký...' : 'Đăng ký đối tác'}
          </button>

          <GoogleAuthButton
            disabled={loading}
            isLoading={loading}
            onCredential={handleGoogleRegister}
            onError={setSubmitError}
          />
        </form>

        <div className={authFooterClass}>
          Đã có tài khoản đối tác?{' '}
          <Link
            to="/partner/login"
            className="font-bold text-emerald-600 hover:text-emerald-800"
          >
            Đăng nhập
          </Link>
        </div>
    </LoginTemplate>
  )
}
