export type AiRecommendationType = 'ALL' | 'HOTEL' | 'RESTAURANT'

export type AiRecommendationItem = {
  id: string
  type: 'HOTEL' | 'RESTAURANT' | 'PLACE'
  name: string
  city?: string | null
  district?: string | null
  basePrice?: number | null
  score: number
  reasons: string[]
}

export type UserAiRecommendationResponse = {
  profileSummary: string
  profileSignals: string[]
  recommendations: AiRecommendationItem[]
}

export type AiRecommendationFeedbackRequest = {
  idTaiSan: string
  viewed?: boolean
  clicked?: boolean
  booked?: boolean
  feedbackRating?: number
  feedbackNote?: string
}

export type AiUserEventRequest = {
  eventType: 'SEARCH' | 'VIEW' | 'CLICK' | 'BOOK' | 'REVIEW' | 'PROMOTION_VIEW'
  assetType?: 'HOTEL' | 'RESTAURANT' | 'PLACE'
  assetId?: string
  keyword?: string
  city?: string
  source?: string
  metadata?: string
}

export type SoThichResponse = {
  id: string
  tenSoThich: string
}

export type DanhMucSoThichResponse = {
  id: string
  tenDanhMuc: string
  moTa: string
  danhSachSoThich: SoThichResponse[]
}

export type UpdatePreferencesRequest = {
  soThichIds: string[]
}

export type ItineraryRequest = {
  city: string
  durationDays: number
  groupSize: number
  budgetLevel: string
}

export type Activity = {
  timeWindow: string
  description: string
  recommendedAssetId: string | null
  assetType: string
  assetName: string
}

export type DailyPlan = {
  day: number
  activities: Activity[]
}

export type ItineraryResponse = {
  city: string
  durationDays: number
  budgetLevel: string
  dailyPlans: DailyPlan[]
}

export type ThongBaoNguCanhResponse = {
  idThongBao: string
  loaiNguCanh: string
  noiDung: string
  mucDo: string
  thoiGianHieuLuc: string
  type: string
}
