import axios from 'axios'

type ApiErrorPayload = {
  message?: string
  error?: string
  details?: string
  [key: string]: unknown
}

export function getApiErrorMessage(error: unknown, fallbackMessage: string) {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data

    if (typeof data === 'string') {
      return data
    }

    if (data && typeof data === 'object') {
      const payload = data as ApiErrorPayload
      return payload.message || payload.error || payload.details || fallbackMessage
    }

    if (error.response?.status === 401) {
      return 'Email hoặc mật khẩu không đúng.'
    }

    return error.message || fallbackMessage
  }

  if (error instanceof Error) {
    return error.message
  }

  return fallbackMessage
}