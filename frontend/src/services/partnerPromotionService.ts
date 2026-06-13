import { api } from './api'
import { getApiErrorMessage } from '../utils/apiError'

export type PromotionResponse = {
  id: number
  tenUuDai: string
  moTa?: string | null
  businessProfileId: string
  mucGiam: number
  loaiGiamGia: string
  giaTriGiamToiDa?: number | null
  ngayBatDau: string
  ngayKetThuc: string
  trangThaiUuDai: string
  loaiUuDai: string
  maVoucher?: string | null
  soLuongPhatHanh?: number | null
  soLuongDaDung?: number | null
  donHangToiThieu?: number | null
  usageLimitPerUser?: number | null
  diemCanDoi?: number | null
  choPhepDoiBangDiem?: boolean | null
  phamViApDung?: string | null
  targetType?: string | null
  targetId?: number | null
  createdAt?: string | null
  updatedAt?: string | null
}

export type PromotionAnalyticsSummary = {
  campaignId: number
  usageCount: number
  bookingCount: number
  generatedRevenue: number
  discountCost: number
  conversionRate: number
}

export type PromotionAnalyticsDaily = {
  ngay: string
  usageCount: number
  bookingCount: number
  generatedRevenue: number
  discountCost: number
  conversionRate: number
}

export type CreateDirectPromotionPayload = {
  tenUuDai: string
  moTa?: string
  businessProfileId: string
  mucGiam: number
  loaiGiamGia: string
  giaTriGiamToiDa?: number
  ngayBatDau: string
  ngayKetThuc: string
  targetType?: string
  targetId?: number
}

export type CreateVoucherPayload = CreateDirectPromotionPayload & {
  maVoucher: string
  soLuongPhatHanh: number
  donHangToiThieu?: number
  usageLimitPerUser?: number
  diemCanDoi?: number
  choPhepDoiBangDiem?: boolean
  phamViApDung: string
}

export type UpdatePromotionPayload = CreateDirectPromotionPayload & {
  soLuongPhatHanh?: number
  donHangToiThieu?: number
  usageLimitPerUser?: number
  diemCanDoi?: number
  choPhepDoiBangDiem?: boolean
  phamViApDung?: string
}

export const partnerPromotionService = {
  async getPromotions(): Promise<PromotionResponse[]> {
    try {
      const { data } = await api.get('/v1/partner/promotions')
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải danh sách chiến dịch.'))
    }
  },

  async createDirectPromotion(payload: CreateDirectPromotionPayload): Promise<PromotionResponse> {
    try {
      const { data } = await api.post('/v1/partner/promotions/direct', payload)
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tạo ưu đãi trực tiếp.'))
    }
  },

  async createVoucher(payload: CreateVoucherPayload): Promise<PromotionResponse> {
    try {
      const { data } = await api.post('/v1/partner/promotions/voucher', payload)
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tạo voucher campaign.'))
    }
  },

  async updatePromotion(id: number, payload: UpdatePromotionPayload): Promise<PromotionResponse> {
    try {
      const { data } = await api.put(`/v1/partner/promotions/${id}`, payload)
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể cập nhật chiến dịch.'))
    }
  },

  async pausePromotion(id: number): Promise<PromotionResponse> {
    try {
      const { data } = await api.patch(`/v1/partner/promotions/${id}/pause`)
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tạm dừng chiến dịch.'))
    }
  },

  async resumePromotion(id: number): Promise<PromotionResponse> {
    try {
      const { data } = await api.patch(`/v1/partner/promotions/${id}/resume`)
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể kích hoạt lại chiến dịch.'))
    }
  },

  async getPromotionAnalytics(id: number): Promise<PromotionAnalyticsSummary> {
    try {
      const { data } = await api.get(`/v1/partner/promotions/${id}/analytics`)
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải analytics tổng quan.'))
    }
  },

  async getPromotionAnalyticsDaily(id: number, fromDate?: string, toDate?: string): Promise<PromotionAnalyticsDaily[]> {
    try {
      const { data } = await api.get(`/v1/partner/promotions/${id}/analytics/daily`, {
        params: { fromDate, toDate },
      })
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải analytics theo ngày.'))
    }
  },
}
