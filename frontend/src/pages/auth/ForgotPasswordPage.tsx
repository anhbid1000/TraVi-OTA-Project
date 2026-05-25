import { useEffect, useMemo, useState, type FormEvent } from 'react'
import { LoaderCircle } from 'lucide-react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { authService } from '../../services/authService'
import { getApiErrorMessage } from '../../utils/apiError'
import { AuthTextField, LoginTemplate } from '../../features/auth/components'
import {
  authAlertErrorClass,
  authAlertSuccessClass,
  authButtonBaseClass,
  authFormGroupClass,
  authLastFormGroupClass,
  authSmallLinkClass,
  isValidEmail,
  validatePassword,
} from '../../features/auth/utils'

type AuthTone = 'blue' | 'emerald'
type ForgotPasswordStep = 'request' | 'verify' | 'reset'

type ForgotPasswordLocationState = {
  email?: string
}

type ForgotPasswordPageProps = {
  loginPath: string
  tone: AuthTone
  title: string
  subtitle: string
}

type ForgotPasswordForm = {
  email: string
  confirmOTP: string
  matKhauMoi: string
  xacNhanMatKhau: string
}

type ForgotPasswordErrors = Partial<Record<keyof ForgotPasswordForm, string>>

const RESEND_COOLDOWN_SECONDS = 60

const toneClasses: Record<AuthTone, { button: string }> = {
  blue: {
    button: 'bg-blue-500 shadow-lg shadow-blue-200 hover:bg-blue-600',
  },
  emerald: {
    button: 'bg-emerald-600 shadow-lg shadow-emerald-200 hover:bg-emerald-700',
  },
};

export function ForgotPasswordPage({ loginPath, tone, title, subtitle }: ForgotPasswordPageProps) {
  const navigate = useNavigate()
  const location = useLocation()
  const locationState = location.state as ForgotPasswordLocationState | null
  const styles = toneClasses[tone]
  const activeRole = tone === 'blue' ? 'KHACH_HANG' : 'DOI_TAC'

  const [step, setStep] = useState<ForgotPasswordStep>('request')
  const [form, setForm] = useState<ForgotPasswordForm>({
    email: locationState?.email ?? '',
    confirmOTP: '',
    matKhauMoi: '',
    xacNhanMatKhau: '',
  })
  const [errors, setErrors] = useState<ForgotPasswordErrors>({})
  const [submitError, setSubmitError] = useState('')
  const [submitSuccess, setSubmitSuccess] = useState('')
  const [loading, setLoading] = useState(false)
  const [resendLoading, setResendLoading] = useState(false)
  const [resendCooldown, setResendCooldown] = useState(0)

  useEffect(() => {
    if (resendCooldown <= 0) {
      return
    }

    const timerId = window.setInterval(() => {
      setResendCooldown((current) => Math.max(current - 1, 0))
    }, 1000)

    return () => window.clearInterval(timerId)
  }, [resendCooldown])

  const submitLabel = useMemo(() => {
    switch (step) {
      case 'request':
        return loading ? 'Đang gửi mã...' : 'Gửi mã xác thực'
      case 'verify':
        return loading ? 'Đang xác thực...' : 'Xác thực mã OTP'
      case 'reset':
        return loading ? 'Đang đặt lại mật khẩu...' : 'Đặt lại mật khẩu'
      default:
        return 'Tiếp tục'
    }
  }, [loading, step])

  const updateField = (field: keyof ForgotPasswordForm, value: string) => {
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

  const validateCurrentStep = () => {
    const nextErrors: ForgotPasswordErrors = {}

    if (!form.email.trim()) {
      nextErrors.email = 'Vui lòng nhập email.'
    } else if (!isValidEmail(form.email.trim())) {
      nextErrors.email = 'Email không hợp lệ.'
    }

    if (step === 'verify') {
      if (!form.confirmOTP.trim()) {
        nextErrors.confirmOTP = 'Vui lòng nhập mã OTP.'
      } else if (!/^\d{6}$/.test(form.confirmOTP.trim())) {
        nextErrors.confirmOTP = 'Mã OTP phải gồm 6 chữ số.'
      }
    }

    if (step === 'reset') {
      const passwordError = validatePassword(form.matKhauMoi)
      if (passwordError) {
        nextErrors.matKhauMoi = passwordError
      }

      if (!form.xacNhanMatKhau) {
        nextErrors.xacNhanMatKhau = 'Vui lòng nhập lại mật khẩu mới.'
      } else if (form.xacNhanMatKhau !== form.matKhauMoi) {
        nextErrors.xacNhanMatKhau = 'Mật khẩu xác nhận không khớp.'
      }
    }

    setErrors(nextErrors)
    return Object.keys(nextErrors).length === 0
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()

    if (!validateCurrentStep()) {
      return
    }

    setLoading(true)
    setSubmitError('')
    setSubmitSuccess('')

    try {
      if (step === 'request') {
        const message = await authService.requestPasswordResetOtp({
          email: form.email.trim(),
        })

        setSubmitSuccess(
          typeof message === 'string'
            ? message
            : 'Mã OTP đặt lại mật khẩu đã được gửi tới email của bạn.',
        )
        setStep('verify')
        setResendCooldown(RESEND_COOLDOWN_SECONDS)
        return
      }

      if (step === 'verify') {
        const message = await authService.verifyPasswordResetOtp({
          email: form.email.trim(),
          confirmOTP: form.confirmOTP.trim(),
        })

        setSubmitSuccess(
          typeof message === 'string'
            ? message
            : 'OTP hợp lệ. Vui lòng nhập mật khẩu mới của bạn.',
        )
        setStep('reset')
        return
      }

      const message = await authService.resetPassword({
        email: form.email.trim(),
        matKhauMoi: form.matKhauMoi,
        xacNhanMatKhau: form.xacNhanMatKhau,
      })

      navigate(loginPath, {
        replace: true,
        state: {
          registerMessage:
            typeof message === 'string'
              ? message
              : 'Đặt lại mật khẩu thành công. Bạn có thể đăng nhập lại.',
        },
      })
    } catch (error) {
      setSubmitError(getApiErrorMessage(error, 'Không thể tiếp tục khôi phục mật khẩu.'))
    } finally {
      setLoading(false)
    }
  }

  const handleResendOtp = async () => {
    if (resendCooldown > 0 || step !== 'verify') {
      return
    }

    if (!form.email.trim() || !isValidEmail(form.email.trim())) {
      setErrors((current) => ({
        ...current,
        email: 'Vui lòng nhập email hợp lệ để nhận lại mã OTP.',
      }))
      return
    }

    setResendLoading(true)
    setSubmitError('')
    setSubmitSuccess('')

    try {
      const message = await authService.requestPasswordResetOtp({
        email: form.email.trim(),
      })

      setSubmitSuccess(
        typeof message === 'string' ? message : 'Mã OTP mới đã được gửi tới email của bạn.',
      )
      setResendCooldown(RESEND_COOLDOWN_SECONDS)
    } catch (error) {
      setSubmitError(getApiErrorMessage(error, 'Không thể gửi lại mã OTP.'))
    } finally {
      setResendLoading(false)
    }
  }

  return (
    <LoginTemplate
      activeRole={activeRole}
      title={title}
      subtitle={subtitle}
    >
      {submitSuccess && <div className={authAlertSuccessClass}>{submitSuccess}</div>}
      {submitError && <div className={authAlertErrorClass}>{submitError}</div>}

      <form onSubmit={handleSubmit} noValidate>
          <div className={authFormGroupClass}>
            <AuthTextField
              id="forgot-password-email"
              name="email"
              label="Email"
              type="email"
              value={form.email}
              placeholder="email@example.com"
              autoComplete="email"
              error={errors.email}
              icon="mail"
              tone={tone}
              onChange={(value) => updateField('email', value)}
            />
          </div>

          {step === 'verify' && (
            <div className={authFormGroupClass}>
              <AuthTextField
                id="forgot-password-otp"
                name="confirmOTP"
                label="Mã OTP"
                value={form.confirmOTP}
                placeholder="123456"
                autoComplete="one-time-code"
                error={errors.confirmOTP}
                icon="key"
                tone={tone}
                onChange={(value) => updateField('confirmOTP', value)}
              />
            </div>
          )}

          {step === 'reset' && (
            <>
              <div className={authFormGroupClass}>
                <AuthTextField
                  id="forgot-password-new-password"
                  name="matKhauMoi"
                  label="Mật khẩu mới"
                  type="password"
                  value={form.matKhauMoi}
                  placeholder="Password@123"
                  autoComplete="new-password"
                  error={errors.matKhauMoi}
                  icon="lock"
                  tone={tone}
                  showPasswordToggle
                  onChange={(value) => updateField('matKhauMoi', value)}
                />
              </div>

              <div className={authLastFormGroupClass}>
                <AuthTextField
                  id="forgot-password-confirm-password"
                  name="xacNhanMatKhau"
                  label="Xác nhận mật khẩu mới"
                  type="password"
                  value={form.xacNhanMatKhau}
                  placeholder="Nhập lại mật khẩu mới"
                  autoComplete="new-password"
                  error={errors.xacNhanMatKhau}
                  icon="lock"
                  tone={tone}
                  showPasswordToggle
                  onChange={(value) => updateField('xacNhanMatKhau', value)}
                />
              </div>
            </>
          )}

          <button
            type="submit"
            disabled={loading || resendLoading}
            className={`${authButtonBaseClass} ${styles.button}`}
          >
            {loading ? (
              <span className="inline-flex items-center justify-center gap-2">
                <LoaderCircle className="animate-spin" size={18} aria-hidden="true" />
                {submitLabel}
              </span>
            ) : (
              submitLabel
            )}
          </button>

          {step === 'verify' && (
            <button
              type="button"
              disabled={loading || resendLoading || resendCooldown > 0}
              onClick={handleResendOtp}
              className="mt-3 w-full rounded-xl border border-gray-100 bg-white px-4 py-3 text-sm font-semibold text-gray-600 transition hover:border-gray-200 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {resendLoading
                ? 'Đang gửi lại mã...'
                : resendCooldown > 0
                  ? `Gửi lại sau ${resendCooldown}s`
                  : 'Gửi lại mã OTP'}
            </button>
          )}
      </form>

      <div className="mt-6 text-center">
        <Link to={loginPath} state={{ email: form.email.trim() }} className={authSmallLinkClass}>
          Quay lại đăng nhập
        </Link>
      </div>
    </LoginTemplate>
  )
}



