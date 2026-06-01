import { api } from './api'
import { getApiErrorMessage } from '../utils/apiError'

export type LoyaltySummary = {
  customerId: number
  currentPoints: number
  totalSpending: number
  currentTier: string
  nextTier?: string | null
  requiredSpendingForNext?: number | null
  remainingSpending?: number | null
  progressPercent?: number | null
}

export type LoyaltyProgress = {
  currentTier: string
  totalSpending: number
  nextTier?: string | null
  requiredSpending?: number | null
  remainingSpending?: number | null
  progressPercent?: number | null
}

export type PointHistory = {
  id: number
  customerId: number
  soDiemThayDoi: number
  loaiGiaoDichDiem: string
  diemTruocGiaoDich: number
  diemSauGiaoDich: number
  bookingId?: number | null
  voucherId?: number | null
  ghiChu?: string | null
  createdAt: string
}

export type WalletVoucher = {
  id: number
  voucherId: number
  customerId: string
  maVoucher: string
  maVoucherCaNhan?: string | null
  tenUuDai?: string | null
  trangThai: string
  sourceType?: string | null
  issuedAt?: string | null
  reserveExpiresAt?: string | null
  usedAt?: string | null
  expiredAt?: string | null
  bookingId?: number | null
  isReserveExpired?: boolean | null
}

export type ExchangeableVoucher = {
  id: number
  tenUuDai: string
  moTa?: string | null
  maVoucher: string
  mucGiam: number
  loaiGiamGia: string
  giaTriGiamToiDa?: number | null
  diemCanDoi?: number | null
  choPhepDoiBangDiem?: boolean | null
  ngayKetThuc?: string | null
}

export type ExchangeVoucherResponse = {
  customerVoucherId: number
  voucherId: number
  maVoucher: string
  maVoucherCaNhan: string
  diemCanDoi: number
  diemSauGiaoDich: number
  message: string
}

export const loyaltyService = {
  async getSummary(): Promise<LoyaltySummary> {
    try {
      const { data } = await api.get('/v1/user/loyalty/summary')
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải tổng quan loyalty.'))
    }
  },

  async getProgress(): Promise<LoyaltyProgress> {
    try {
      const { data } = await api.get('/v1/user/loyalty/progress')
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải tiến độ hạng thành viên.'))
    }
  },

  async getHistory(limit = 20): Promise<PointHistory[]> {
    try {
      const { data } = await api.get('/v1/user/loyalty/history', { params: { limit } })
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải lịch sử điểm.'))
    }
  },

  async getWalletVouchers(): Promise<WalletVoucher[]> {
    try {
      const { data } = await api.get('/v1/user/loyalty/wallet/vouchers')
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải ví voucher.'))
    }
  },

  async getExchangeableVouchers(): Promise<ExchangeableVoucher[]> {
    try {
      const { data } = await api.get('/v1/user/loyalty/exchangeable-vouchers')
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải danh sách voucher có thể đổi.'))
    }
  },

  async exchangeVoucher(voucherId: number): Promise<ExchangeVoucherResponse> {
    try {
      const { data } = await api.post('/v1/user/loyalty/exchange', { voucherId })
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Đổi voucher thất bại.'))
    }
  },
}
