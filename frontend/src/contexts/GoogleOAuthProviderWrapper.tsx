import { useEffect, type ReactNode } from 'react'

type GoogleOAuthProviderWrapperProps = {
  children: ReactNode
}

const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID?.trim()

export function GoogleOAuthProviderWrapper({ children }: GoogleOAuthProviderWrapperProps) {
  useEffect(() => {
    if (!googleClientId) {
      return
    }

    const existingScript = document.querySelector<HTMLScriptElement>('script[data-google-gsi="true"]')
    if (existingScript) {
      return
    }

    const script = document.createElement('script')
    script.src = 'https://accounts.google.com/gsi/client'
    script.async = true
    script.defer = true
    script.dataset.googleGsi = 'true'
    document.head.appendChild(script)
  }, [])

  if (!googleClientId) {
    return <>{children}</>
  }

  return <>{children}</>
}


