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
      return 'Dữ liệu gửi lên chưa hợp lệ. Vui lòng kiểm tra lại các trường trong form.'
    case 401:
      return 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại bằng tài khoản đối tác.'
    case 403:
      return 'Tài khoản hiện tại không có quyền đối tác để thực hiện chức năng này. Vui lòng đăng nhập lại bằng tài khoản đối tác.'
    case 404:
      return 'Không tìm thấy dữ liệu cần thao tác. Vui lòng tải lại trang rồi thử lại.'
    case 409:
      return 'Dữ liệu bị trùng hoặc xung đột với hồ sơ đã tồn tại trong hệ thống.'
    case 413:
      return 'File tải lên quá lớn. Vui lòng chọn file nhỏ hơn.'
    case 415:
      return 'Định dạng file không được hỗ trợ.'
    case 500:
      return 'Backend đang gặp lỗi xử lý. Vui lòng kiểm tra log Spring Boot.'
    default:
      return fallbackMessage
  }
}

export function getApiErrorMessage(error: unknown, fallbackMessage: string) {
  if (axios.isAxiosError(error)) {
    if (!error.response) {
      return 'Không kết nối được backend. Vui lòng kiểm tra Spring Boot có đang chạy đúng cổng API hay chưa.'
    }

    const data = error.response.data

    if (error.response.status === 413) {
      return getStatusFallback(error.response.status, fallbackMessage)
    }

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
