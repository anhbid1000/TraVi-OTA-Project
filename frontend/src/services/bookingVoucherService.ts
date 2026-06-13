import { api } from './api'
import { getApiErrorMessage } from '../utils/apiError'

export type VoucherCheckoutResponse = {
  bookingId: string
  voucherId: number
  maVoucher: string
  originalAmount: number
  discountAmount: number
  finalAmount: number
  isApplied: boolean
  message: string
}

export type VoucherPreviewResponse = {
  voucherId: number
  maVoucher: string
  discountAmount?: number
  discountPercentage?: number
  estimatedFinalAmount?: number
  isEligible: boolean
  ineligibilityReason?: string
}

export const bookingVoucherService = {
  async getAvailableVouchers(bookingId: string): Promise<VoucherPreviewResponse[]> {
    try {
      const { data } = await api.get(`/v1/user/bookings/${bookingId}/available-vouchers`)
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải danh sách voucher khả dụng.'))
    }
  },

  async previewVoucher(bookingId: string, maVoucher: string): Promise<VoucherPreviewResponse> {
    try {
      const { data } = await api.get(`/v1/user/bookings/${bookingId}/voucher-preview`, { params: { maVoucher } })
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể xem trước giảm giá voucher.'))
    }
  },

  async applyVoucher(bookingId: string, maVoucher: string): Promise<VoucherCheckoutResponse> {
    try {
      const { data } = await api.post(`/v1/user/bookings/${bookingId}/apply-voucher`, { maVoucher })
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể áp dụng voucher cho đơn đặt chỗ này.'))
    }
  },

  async removeVoucher(bookingId: string): Promise<void> {
    try {
      await api.delete(`/v1/user/bookings/${bookingId}/voucher`)
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể gỡ voucher khỏi đơn đặt chỗ.'))
    }
  },
}
