import { useEffect, useRef } from 'react'
import { LoaderCircle } from 'lucide-react'
import { authAlertErrorClass } from '../utils'

type GoogleCredentialResponse = {
  credential?: string
}

type GoogleIdentityApi = {
  initialize: (config: {
    client_id: string
    callback: (response: GoogleCredentialResponse) => void
  }) => void
  renderButton: (
    parent: HTMLElement,
    options: {
      text: 'continue_with'
      shape: 'rectangular'
      theme: 'outline'
      width: string
      locale: string
    },
  ) => void
}

type GoogleAuthButtonProps = {
  disabled?: boolean
  isLoading?: boolean
  onCredential: (idToken: string) => void | Promise<void>
  onError: (message: string) => void
}

const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID?.trim()

export function GoogleAuthButton({
  disabled = false,
  isLoading = false,
  onCredential,
  onError,
}: GoogleAuthButtonProps) {
  const buttonContainerRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    if (!googleClientId || !buttonContainerRef.current) {
      return
    }

    const renderGoogleButton = () => {
      const googleIdentity = (
        window as Window & {
          google?: {
            accounts?: {
              id?: GoogleIdentityApi
            }
          }
        }
      ).google?.accounts?.id
      if (!googleIdentity || !buttonContainerRef.current) {
        return false
      }

      buttonContainerRef.current.innerHTML = ''
      googleIdentity.initialize({
        client_id: googleClientId,
        callback: (response: GoogleCredentialResponse) => {
          if (!response.credential) {
            onError('Google không trả về định danh hợp lệ. Vui lòng thử lại.')
            return
          }

          void onCredential(response.credential)
        },
      })

      googleIdentity.renderButton(buttonContainerRef.current, {
        text: 'continue_with',
        shape: 'rectangular',
        theme: 'outline',
        width: '320',
        locale: 'vi',
      })

      return true
    }

    if (renderGoogleButton()) {
      return
    }

    const timerId = window.setInterval(() => {
      if (renderGoogleButton()) {
        window.clearInterval(timerId)
      }
    }, 300)

    return () => {
      window.clearInterval(timerId)
    }
  }, [onCredential, onError])

  const triggerGoogleLogin = () => {
    const renderedButton = buttonContainerRef.current?.querySelector<HTMLElement>('div[role="button"]')

    if (!renderedButton) {
      onError('Google chưa sẵn sàng. Vui lòng thử lại sau vài giây.')
      return
    }

    renderedButton.click()
  }

  if (!googleClientId) {
    return (
      <div className="mt-6">
        <div className={authAlertErrorClass}>
          Chưa cấu hình `VITE_GOOGLE_CLIENT_ID`, nên nút đăng nhập Google hiện chưa khả dụng.
        </div>
      </div>
    )
  }

  return (
    <div className="mt-7">
      <div className="relative mb-5 text-center">
        <span className="relative z-10 inline-block bg-white px-3 text-xs font-semibold tracking-[0.16em] text-gray-400 uppercase">
          Hoặc tiếp tục với
        </span>
        <div className="absolute inset-x-0 top-1/2 z-0 h-px -translate-y-1/2 bg-gray-100" />
      </div>

      <div ref={buttonContainerRef} className="sr-only" aria-hidden="true" />

      <button
        type="button"
        onClick={triggerGoogleLogin}
        disabled={disabled}
        className="inline-flex h-12 w-full cursor-pointer items-center justify-center gap-3 rounded-xl border border-[#d9dde4] bg-white px-4 text-sm font-semibold text-[#1f2937] transition-colors hover:bg-[#f8fafc] disabled:cursor-not-allowed disabled:opacity-70"
        aria-label="Tiếp tục với Google"
      >
        <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
          <path
            d="M21.35 11.1h-9.18v2.99h5.27c-.23 1.51-1.78 4.44-5.27 4.44-3.17 0-5.76-2.62-5.76-5.86s2.59-5.86 5.76-5.86c1.8 0 3 .77 3.69 1.43l2.52-2.45C16.77 4.31 14.69 3.5 12.17 3.5c-5.03 0-9.1 4.07-9.1 9.09s4.07 9.09 9.1 9.09c5.25 0 8.74-3.69 8.74-8.88 0-.6-.06-1.04-.16-1.7z"
            fill="#4285F4"
          />
          <path
            d="M6.15 14.28l-.68.52-2.4 1.87a9.06 9.06 0 0 0 8.1 5.01c2.52 0 4.6-.83 6.13-2.26l-2.93-2.26c-.78.54-1.8.92-3.2.92-3.37 0-4.98-2.28-5.02-3.8z"
            fill="#34A853"
          />
          <path
            d="M3.07 7.51A9.08 9.08 0 0 0 3.07 16.67l3.08-2.39a5.44 5.44 0 0 1 0-4.38z"
            fill="#FBBC05"
          />
          <path
            d="M12.17 6.71c1.95 0 3.28.85 4.03 1.55l2.94-2.87c-1.55-1.44-3.63-2.39-6.97-2.39a9.06 9.06 0 0 0-8.1 5.01l3.08 2.39c.74-2.23 2.84-3.69 5.02-3.69z"
            fill="#EA4335"
          />
        </svg>
        <span>Tiếp tục với Google</span>
      </button>


      {isLoading && (
        <div className="mt-3 flex items-center justify-center gap-2 text-sm font-medium text-gray-500">
          <LoaderCircle className="animate-spin" size={16} aria-hidden="true" />
          Đang xác thực với Google...
        </div>
      )}
    </div>
  )
}





