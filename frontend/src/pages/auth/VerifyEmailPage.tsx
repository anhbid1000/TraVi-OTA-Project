import { useEffect, useState, type FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { BadgeCheck } from 'lucide-react'
import { cn } from '../../utils/cn'
import { authService } from '../../services/authService'
import { getApiErrorMessage } from '../../utils/apiError'
import { AuthTextField } from '../../features/auth/components'
import {
  authAlertErrorClass,
  authAlertSuccessClass,
  authButtonBaseClass,
  authCardClass,
  authFormGroupClass,
  authIconBoxClass,
  authLayoutClass,
  authSmallLinkClass,
  authSubtitleClass,
  authTitleClass,
  isValidEmail,
} from '../../features/auth/utils'

type VerifyEmailLocationState = {
  email?: string
  loginPath?: string
  registerMessage?: string
  from?: {
    pathname?: string
  }
}

type VerifyEmailForm = {
  email: string
  confirmOTP: string
}

type VerifyEmailErrors = Partial<Record<keyof VerifyEmailForm, string>>

const RESEND_COOLDOWN_SECONDS = 60

export function VerifyEmailPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const locationState = location.state as VerifyEmailLocationState | null

  const [form, setForm] = useState<VerifyEmailForm>({
    email: locationState?.email ?? '',
    confirmOTP: '',
  })
  const [errors, setErrors] = useState<VerifyEmailErrors>({})
  const [submitError, setSubmitError] = useState('')
  const [submitSuccess, setSubmitSuccess] = useState('')
  const [loading, setLoading] = useState(false)
  const [resendLoading, setResendLoading] = useState(false)
  const [resendCooldown, setResendCooldown] = useState(0)

  const loginPath = locationState?.loginPath ?? '/login'

  useEffect(() => {
    if (resendCooldown <= 0) {
      return
    }

    const timerId = window.setInterval(() => {
      setResendCooldown((current) => Math.max(current - 1, 0))
    }, 1000)

    return () => window.clearInterval(timerId)
  }, [resendCooldown])

  const updateField = (field: keyof VerifyEmailForm, value: string) => {
    setForm((current) => ({
      ...current,
      [field]: value,
    }))

    setErrors((current) => ({
      ...current,
      [field]: undefined,
    }))

    setSubmitError('')
    setSubmitSuccess('')
  }

  const validateEmail = () => {
    const email = form.email.trim()

    if (!email) {
      setErrors((current) => ({ ...current, email: 'Vui lòng nhập email.' }))
      return false
    }

    if (!isValidEmail(email)) {
      setErrors((current) => ({ ...current, email: 'Email không hợp lệ.' }))
      return false
    }

    setErrors((current) => ({ ...current, email: undefined }))
    return true
  }

  const validateForm = () => {
    const nextErrors: VerifyEmailErrors = {}

    if (!form.email.trim()) {
      nextErrors.email = 'Vui lòng nhập email.'
    } else if (!isValidEmail(form.email.trim())) {
      nextErrors.email = 'Email không hợp lệ.'
    }

    if (!form.confirmOTP.trim()) {
      nextErrors.confirmOTP = 'Vui lòng nhập mã OTP.'
    } else if (!/^\d{6}$/.test(form.confirmOTP.trim())) {
      nextErrors.confirmOTP = 'Mã OTP phải gồm 6 chữ số.'
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
    setSubmitSuccess('')

    try {
      const message = await authService.verifyEmail({
        email: form.email.trim(),
        confirmOTP: form.confirmOTP.trim(),
      })

      navigate(loginPath, {
        replace: true,
        state: {
          email: form.email.trim(),
          from: locationState?.from,
          registerMessage:
            typeof message === 'string'
              ? message
              : 'Xác minh tài khoản thành công. Bạn có thể đăng nhập.',
        },
      })
    } catch (error) {
      setSubmitError(getApiErrorMessage(error, 'Xác minh OTP thất bại. Vui lòng kiểm tra lại mã.'))
    } finally {
      setLoading(false)
    }
  }

  const handleResendOtp = async () => {
    if (resendCooldown > 0) {
      return
    }

    if (!validateEmail()) {
      return
    }

    setResendLoading(true)
    setSubmitError('')
    setSubmitSuccess('')

    try {
      const message = await authService.resendOtp({
        email: form.email.trim(),
      })

      setSubmitSuccess(typeof message === 'string' ? message : 'Mã OTP mới đã được gửi tới email của bạn.')
      setResendCooldown(RESEND_COOLDOWN_SECONDS)
    } catch (error) {
      setSubmitError(getApiErrorMessage(error, 'Không gửi lại được mã OTP. Vui lòng thử lại.'))
    } finally {
      setResendLoading(false)
    }
  }

  return (
    <div className={cn(authLayoutClass, 'bg-linear-to-br from-blue-50 via-white to-indigo-50')}>
      <div className={cn(authCardClass, 'max-w-105 border-blue-100')}>
        <div className="mb-8 text-center">
          <div className={cn(authIconBoxClass, 'bg-blue-50 border-blue-100 text-blue-600')}>
            <BadgeCheck size={30} strokeWidth={2.1} aria-hidden="true" />
          </div>

          <h2 className={authTitleClass}>Xác minh email</h2>
          <p className={authSubtitleClass}>Nhập mã OTP được gửi tới email đăng ký của bạn</p>
        </div>

        {locationState?.registerMessage && (
          <div className={authAlertSuccessClass}>{locationState.registerMessage}</div>
        )}
        {submitSuccess && <div className={authAlertSuccessClass}>{submitSuccess}</div>}
        {submitError && <div className={authAlertErrorClass}>{submitError}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div className={authFormGroupClass}>
            <AuthTextField
              id="verify-email"
              name="email"
              label="Email"
              type="email"
              value={form.email}
              placeholder="email@example.com"
              autoComplete="email"
              error={errors.email}
              icon="mail"
              tone="blue"
              onChange={(value) => updateField('email', value)}
            />
          </div>

          <div className="mb-6">
            <AuthTextField
              id="verify-otp"
              name="confirmOTP"
              label="Mã OTP"
              value={form.confirmOTP}
              placeholder="123456"
              autoComplete="one-time-code"
              error={errors.confirmOTP}
              icon="key"
              tone="blue"
              onChange={(value) => updateField('confirmOTP', value)}
            />
          </div>

          <button
            type="submit"
            disabled={loading || resendLoading}
            className={cn(
              authButtonBaseClass,
              'cursor-pointer bg-blue-600 shadow-lg shadow-blue-200 hover:bg-blue-700',
            )}
          >
            {loading ? 'Đang xác minh...' : 'Xác minh tài khoản'}
          </button>

          <button
            type="button"
            disabled={loading || resendLoading || resendCooldown > 0}
            onClick={handleResendOtp}
            className="cursor-pointer mt-3 w-full rounded-lg border border-blue-100 bg-white px-4 py-3 text-sm font-semibold text-blue-600 transition hover:border-blue-200 hover:bg-blue-50 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {resendLoading
              ? 'Đang gửi lại mã...'
              : resendCooldown > 0
                ? `Gửi lại sau ${resendCooldown}s`
                : 'Gửi lại mã OTP'}
          </button>
        </form>

        <div className="mt-6 text-center">
          <Link
            to={loginPath}
            state={{ email: form.email.trim(), from: locationState?.from }}
            className={cn(authSmallLinkClass, 'text-gray-400 hover:text-blue-600')}
          >
            Quay lại đăng nhập
          </Link>
        </div>
      </div>
    </div>
  );
}
