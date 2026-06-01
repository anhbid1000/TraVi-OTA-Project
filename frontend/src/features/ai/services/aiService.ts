import { api } from '../../../services/api'
import type {
  AiRecommendationFeedbackRequest,
  AiRecommendationType,
  AiUserEventRequest,
  DanhMucSoThichResponse,
  ItineraryRequest,
  ItineraryResponse,
  SoThichResponse,
  ThongBaoNguCanhResponse,
  UpdatePreferencesRequest,
  UserAiRecommendationResponse,
} from '../types'

export type RecommendationParams = {
  type?: AiRecommendationType
  city?: string
  limit?: number
}

export async function getUserRecommendations(params: RecommendationParams = {}) {
  const response = await api.get<UserAiRecommendationResponse>('/v1/user/recommendations', {
    params,
  })
  return response.data
}

export async function sendRecommendationFeedback(payload: AiRecommendationFeedbackRequest) {
  const response = await api.post<string>('/v1/user/recommendations/feedback', payload)
  return response.data
}

export async function sendAiUserEvent(payload: AiUserEventRequest) {
  const response = await api.post<string>('/v1/user/ai-events', payload)
  return response.data
}

export async function getCategories() {
  const response = await api.get<DanhMucSoThichResponse[]>('/v1/user/preferences/categories')
  return response.data
}

export async function getUserPreferences() {
  const response = await api.get<SoThichResponse[]>('/v1/user/preferences')
  return response.data
}

export async function updateUserPreferences(payload: UpdatePreferencesRequest) {
  const response = await api.put<SoThichResponse[]>('/v1/user/preferences', payload)
  return response.data
}

export async function generateItinerary(payload: ItineraryRequest) {
  const response = await api.post<ItineraryResponse>('/v1/user/itinerary-planner', payload)
  return response.data
}

export async function getSmartNotifications() {
  const response = await api.get<ThongBaoNguCanhResponse[]>('/v1/user/notifications/smart')
  return response.data
}
