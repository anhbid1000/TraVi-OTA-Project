import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { cn } from '../../utils/cn'
import { useAuth } from '../../hooks/useAuth'
import { authService } from '../../services/authService'
import { tokenStorage } from '../../services/tokenStorage'
import type { RegisterRequest } from '../../types/auth'
import { getDefaultPathByRole } from '../../routes/routeGuards'
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

type CustomerRegisterFormValues = {
  username: string
  hoTen: string
  email: string
  soDienThoai: string
  matKhau: string
  confirmPassword: string
}

type CustomerRegisterFormErrors = Partial<Record<keyof CustomerRegisterFormValues, string>>

const initialForm: CustomerRegisterFormValues = {
  username: '',
  hoTen: '',
  email: '',
  soDienThoai: '',
  matKhau: '',
  confirmPassword: '',
}

export function CustomerRegister() {
  const navigate = useNavigate()
  const { loginWithGoogle } = useAuth()

  const [form, setForm] = useState<CustomerRegisterFormValues>(initialForm)
  const [errors, setErrors] = useState<CustomerRegisterFormErrors>({})
  const [submitError, setSubmitError] = useState('')
  const [loading, setLoading] = useState(false)

  const updateField = (field: keyof CustomerRegisterFormValues, value: string) => {
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
    const nextErrors: CustomerRegisterFormErrors = {}

    if (!form.username.trim()) {
      nextErrors.username = 'Vui lòng nhập tên đăng nhập.'
    } else if (form.username.trim().length < 4) {
      nextErrors.username = 'Tên đăng nhập phải có ít nhất 4 ký tự.'
    } else if (!/^[a-zA-Z0-9_.-]+$/.test(form.username.trim())) {
      nextErrors.username = 'Tên đăng nhập chỉ gồm chữ, số, dấu gạch dưới hoặc dấu chấm.'
    }

    if (!form.hoTen.trim()) {
      nextErrors.hoTen = 'Vui lòng nhập họ và tên.'
    } else if (form.hoTen.trim().length < 2) {
      nextErrors.hoTen = 'Họ tên phải có ít nhất 2 ký tự.'
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
      loaiTaiKhoan: 'KHACH_HANG',
    }

    try {
      const result = await authService.register(payload)

      const successMessage =
        typeof result === 'string'
          ? result
          : 'Đăng ký thành công. Vui lòng kiểm tra email để xác thực tài khoản.'

      navigate('/verify-email', {
        replace: true,
        state: {
          email: form.email.trim(),
          loginPath: '/login',
          registerMessage: successMessage,
        },
      })
    } catch (error) {
      setSubmitError(
        getApiErrorMessage(error, 'Đăng ký thất bại. Vui lòng kiểm tra lại thông tin.'),
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
        loaiTaiKhoan: 'KHACH_HANG',
      })

      const user = tokenStorage.getUserFromToken()
      const defaultPath = getDefaultPathByRole(user?.role)

      navigate(defaultPath, { replace: true })
    } catch (error) {
      setSubmitError(getApiErrorMessage(error, 'Đăng ký Google thất bại.'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <LoginTemplate
      activeRole="KHACH_HANG"
      title="Đăng ký tài khoản"
      subtitle="Tạo tài khoản khách hàng để đặt phòng và sử dụng dịch vụ TraVi"
    >
        {submitError && <div className={authAlertErrorClass}>{submitError}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div className={authFormGroupClass}>
            <AuthTextField
              id="customer-register-username"
              name="username"
              label="Tên đăng nhập"
              value={form.username}
              placeholder="nguyenvana"
              autoComplete="username"
              error={errors.username}
              icon="user"
              tone="blue"
              onChange={(value) => updateField('username', value)}
            />
          </div>

          <div className={authFormGroupClass}>
            <AuthTextField
              id="customer-register-fullname"
              name="hoTen"
              label="Họ và tên"
              value={form.hoTen}
              placeholder="Nguyễn Văn A"
              autoComplete="name"
              error={errors.hoTen}
              icon="user"
              tone="blue"
              onChange={(value) => updateField('hoTen', value)}
            />
          </div>

          <div className={authFormGroupClass}>
            <AuthTextField
              id="customer-register-email"
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

          <div className={authFormGroupClass}>
            <AuthTextField
              id="customer-register-phone"
              name="soDienThoai"
              label="Số điện thoại"
              type="tel"
              value={form.soDienThoai}
              placeholder="0912345678"
              autoComplete="tel"
              error={errors.soDienThoai}
              icon="phone"
              tone="blue"
              onChange={(value) => updateField('soDienThoai', value)}
            />
          </div>

          <div className={authFormGroupClass}>
            <AuthTextField
              id="customer-register-password"
              name="matKhau"
              label="Mật khẩu"
              type="password"
              value={form.matKhau}
              placeholder="Password@123"
              autoComplete="new-password"
              error={errors.matKhau}
              icon="lock"
              tone="blue"
              showPasswordToggle
              onChange={(value) => updateField('matKhau', value)}
            />
          </div>

          <div className={authLastFormGroupClass}>
            <AuthTextField
              id="customer-register-confirm-password"
              name="confirmPassword"
              label="Nhập lại mật khẩu"
              type="password"
              value={form.confirmPassword}
              placeholder="Nhập lại mật khẩu"
              autoComplete="new-password"
              error={errors.confirmPassword}
              icon="lock"
              tone="blue"
              showPasswordToggle
              onChange={(value) => updateField('confirmPassword', value)}
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
            {loading ? 'Đang đăng ký...' : 'Đăng ký'}
          </button>

          <GoogleAuthButton
            disabled={loading}
            isLoading={loading}
            onCredential={handleGoogleRegister}
            onError={setSubmitError}
          />
        </form>

        <div className={authFooterClass}>
          Đã có tài khoản?{' '}
          <Link to="/login" className="font-bold text-blue-500 hover:text-blue-600">
            Đăng nhập
          </Link>
        </div>
    </LoginTemplate>
  )
}
