const VIETNAM_TIME_ZONE = 'Asia/Ho_Chi_Minh'

export function pointTypeLabel(type: string) {
  const map: Record<string, string> = {
    TICH_DIEM: 'Tích điểm từ đặt chỗ',
    DOI_VOUCHER: 'Đổi voucher',
    HOAN_DIEM: 'Hoàn điểm',
    DIEU_CHINH_ADMIN: 'Điều chỉnh điểm',
    MILESTONE_REWARD: 'Thưởng mốc',
    COMPENSATION: 'Bồi thường',
    EARNED_FROM_BOOKING: 'Tích điểm từ đặt chỗ',
    EXCHANGE_VOUCHER: 'Đổi voucher',
    MILESTONE_BONUS: 'Thưởng mốc',
  }
  return map[type] ?? type
}

export function voucherStatusLabel(status: string) {
  const map: Record<string, string> = {
    CHUA_DUNG: 'Có thể dùng',
    RESERVED: 'Đang giữ',
    DA_DUNG: 'Đã dùng',
    EXPIRED: 'Hết hạn',
    CANCELLED: 'Đã hủy',
  }
  return map[status] ?? status
}

function parseBackendDateTime(value?: string | null) {
  if (!value) return null
  const hasExplicitTimezone = /(?:Z|[+-]\d{2}:?\d{2})$/i.test(value)
  const date = new Date(hasExplicitTimezone ? value : `${value}Z`)
  return Number.isNaN(date.getTime()) ? null : date
}

export function formatVietnamDateTime(value?: string | null) {
  const date = parseBackendDateTime(value)
  if (!date) return '--'
  return date.toLocaleString('vi-VN', {
    timeZone: VIETNAM_TIME_ZONE,
    hour12: false,
  })
}
