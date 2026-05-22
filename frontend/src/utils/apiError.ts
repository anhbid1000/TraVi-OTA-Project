import axios from 'axios'

type ApiErrorPayload = {
  message?: string
  error?: string
  details?: string | string[] | Record<string, unknown>
  errors?: string[] | Record<string, unknown>
  fieldErrors?: Record<string, unknown>
  [key: string]: unknown
}

function stringifyErrorValue(value: unknown): string {
  if (!value) return ''

  if (typeof value === 'string') {
    return value
  }

  if (Array.isArray(value)) {
    return value.map(stringifyErrorValue).filter(Boolean).join(' ')
  }

  if (typeof value === 'object') {
    return Object.values(value as Record<string, unknown>)
      .map(stringifyErrorValue)
      .filter(Boolean)
      .join(' ')
  }

  return String(value)
}

function getStatusFallback(status: number | undefined, fallbackMessage: string) {
  switch (status) {
    case 400:
      return 'D? li?u g?i l�n chua h?p l?. Vui l�ng ki?m tra l?i c�c tru?ng trong form.'
    case 401:
      return 'Phi�n dang nh?p d� h?t h?n. Vui l�ng dang nh?p l?i b?ng t�i kho?n d?i t�c.'
    case 403:
      return 'T�i kho?n hi?n t?i kh�ng c� quy?n d?i t�c d? thao t�c ch?c nang n�y.'
    case 404:
      return 'Kh�ng t�m th?y d? li?u c?n thao t�c. Vui l�ng t?i l?i trang r?i th? l?i.'
    case 409:
      return 'D? li?u b? tr�ng ho?c xung d?t v?i h? so d� t?n t?i trong h? th?ng.'
    case 413:
      return 'File t?i l�n qu� l?n. Vui l�ng ch?n file nh? hon.'
    case 415:
      return '�?nh d?ng file kh�ng du?c h? tr?.'
    case 500:
      return 'Backend dang g?p l?i x? l�. Vui l�ng ki?m tra log Spring Boot.'
    default:
      return fallbackMessage
  }
}

export function getApiErrorMessage(error: unknown, fallbackMessage: string) {
  if (axios.isAxiosError(error)) {
    if (!error.response) {
      return 'Kh�ng k?t n?i du?c backend. Vui l�ng ki?m tra Spring Boot c� dang ch?y ? d�ng c?ng API hay chua.'
    }

    const data = error.response.data

    if (typeof data === 'string' && data.trim()) {
      return data
    }

    if (data && typeof data === 'object') {
      const payload = data as ApiErrorPayload

      return (
        stringifyErrorValue(payload.message) ||
        stringifyErrorValue(payload.error) ||
        stringifyErrorValue(payload.details) ||
        stringifyErrorValue(payload.errors) ||
        stringifyErrorValue(payload.fieldErrors) ||
        getStatusFallback(error.response.status, fallbackMessage)
      )
    }

    return getStatusFallback(error.response.status, error.message || fallbackMessage)
  }

  if (error instanceof Error) {
    return error.message
  }

  return fallbackMessage
}