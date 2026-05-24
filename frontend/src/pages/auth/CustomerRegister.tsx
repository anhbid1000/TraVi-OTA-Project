import { useState, type FormEvent } from 'react'
import { UserPlus } from 'lucide-react'
import { Link, useNavigate } from 'react-router-dom'
import { authService } from '../../services/authService'
import type { RegisterRequest } from '../../types/auth'
import { getApiErrorMessage } from '../../utils/apiError'
import { AuthTextField } from './AuthTextField'
import { isValidEmail, isValidPhoneNumber, validatePassword } from './authValidation'
import {
  authAlertErrorClass,
  authButtonBaseClass,
  authCardClass,
  authFooterClass,
  authFormGroupClass,
  authIconBoxClass,
  authLastFormGroupClass,
  authLayoutClass,
  authSmallLinkClass,
  authSubtitleClass,
  authTitleClass,
} from './authUi'

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

  return (
    <div className={`${authLayoutClass} bg-gradient-to-br from-blue-50 via-white to-indigo-50`}>
      <div className={`${authCardClass} border-blue-100`}>
        <div className="mb-8 text-center">
          <div className={`${authIconBoxClass} bg-blue-50 border-blue-100 text-blue-600`}>
            <UserPlus size={30} strokeWidth={2.1} aria-hidden="true" />
          </div>

          <h2 className={authTitleClass}>Đăng ký tài khoản</h2>
          <p className={authSubtitleClass}>
            Tạo tài khoản khách hàng để đặt phòng và sử dụng dịch vụ TraVi
          </p>
        </div>

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
              onChange={(value) => updateField('confirmPassword', value)}
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className={`${authButtonBaseClass} cursor-pointer bg-blue-600 shadow-lg shadow-blue-200 hover:bg-blue-700`}
          >
            {loading ? 'Đang đăng ký...' : 'Đăng ký'}
          </button>
        </form>

        <div className={authFooterClass}>
          Đã có tài khoản?{' '}
          <Link to="/login" className="font-bold text-blue-600 hover:text-blue-800">
            Đăng nhập
          </Link>
        </div>

        <div className="pt-6 mt-6 text-center border-t border-gray-100">
          <Link
            to="/partner/register"
            className={`${authSmallLinkClass} text-gray-400 hover:text-emerald-600`}
          >
            Đăng ký tài khoản đối tác
          </Link>
        </div>
      </div>
    </div>
  )
}
