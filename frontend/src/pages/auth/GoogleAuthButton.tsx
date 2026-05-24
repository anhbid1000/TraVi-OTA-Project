import { useEffect, useRef } from 'react'
import { LoaderCircle } from 'lucide-react'
import { authAlertErrorClass } from './authUi'

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
      shape: 'pill'
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
    if (!googleClientId || disabled || !buttonContainerRef.current) {
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
        shape: 'pill',
        theme: 'outline',
        width: '355',
        locale: 'en',
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
  }, [disabled, onCredential, onError])

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
    <div className="mt-6">
      <div className="relative mb-4 text-center">
        <span className="relative z-10 inline-block bg-white px-3 text-sm font-medium text-gray-400">
          Hoặc tiếp tục với
        </span>
        <div className="absolute inset-x-0 top-1/2 z-0 h-px -translate-y-1/2 bg-gray-100" />
      </div>

      <div className={disabled ? 'pointer-events-none opacity-70' : undefined}>
        <div ref={buttonContainerRef} className="flex justify-center" />
      </div>

      {isLoading && (
        <div className="mt-3 flex items-center justify-center gap-2 text-sm font-medium text-gray-500">
          <LoaderCircle className="animate-spin" size={16} aria-hidden="true" />
          Đang xác thực với Google...
        </div>
      )}
    </div>
  )
}



