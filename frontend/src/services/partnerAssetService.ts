import { API_BASE_URL, api } from './api'
import type {
  BusinessProfilePayload,
  BusinessProfileResponse,
  ComboPayload,
  ComboResponse,
  MenuItemPayload,
  MenuItemResponse,
  MenuItemStatus,
  RoomPayload,
  RoomResponse,
  TablePayload,
  TableResponse,
} from '../types/asset'

export const partnerAssetService = {
  getBusinessProfiles() {
    return api
      .get<BusinessProfileResponse[]>('/v1/partner/business-profiles')
      .then((response) => response.data)
  },

  getBusinessProfileDetail(id: string) {
    return api
      .get<BusinessProfileResponse>(`/v1/partner/business-profiles/${id}`)
      .then((response) => response.data)
  },

createBusinessProfile(payload: BusinessProfilePayload) {
  return api
    .post<BusinessProfileResponse>('/v1/partner/business-profiles', payload)
    .then((response) => response.data)
},

  updateBusinessProfile(id: string, payload: BusinessProfilePayload) {
    return api
      .put<BusinessProfileResponse>(`/v1/partner/business-profiles/${id}`, payload)
      .then((response) => response.data)
  },

  uploadPartnerAsset(file: File) {
    const formData = new FormData()
    formData.append('file', file)

    return api
      .post<{ fileName: string; url: string; contentType: string; size: number }>(
        '/v1/partner/business-profiles/uploads',
        formData,
        { headers: { 'Content-Type': 'multipart/form-data' } },
      )
      .then((response) => ({
        ...response.data,
        url: response.data.url.startsWith('http')
          ? response.data.url
          : `${API_BASE_URL.replace(/\/api$/, '')}${response.data.url}`,
      }))
  },

  getRooms(hotelId: string) {
    return api
      .get<RoomResponse[]>(`/v1/partner/hotels/${hotelId}/rooms`)
      .then((response) => response.data)
  },

  createRoom(hotelId: string, payload: RoomPayload) {
    return api
      .post<RoomResponse>(`/v1/partner/hotels/${hotelId}/rooms`, payload)
      .then((response) => response.data)
  },

  updateRoom(hotelId: string, roomId: string, payload: RoomPayload) {
    return api
      .put<RoomResponse>(`/v1/partner/hotels/${hotelId}/rooms/${roomId}`, payload)
      .then((response) => response.data)
  },

  deleteRoom(hotelId: string, roomId: string) {
    return api.delete(`/v1/partner/hotels/${hotelId}/rooms/${roomId}`)
  },

  getTables(restaurantId: string) {
    return api
      .get<TableResponse[]>(`/v1/partner/restaurants/${restaurantId}/tables`)
      .then((response) => response.data)
  },

  createTable(restaurantId: string, payload: TablePayload) {
    return api
      .post<TableResponse>(`/v1/partner/restaurants/${restaurantId}/tables`, payload)
      .then((response) => response.data)
  },

  updateTable(restaurantId: string, tableId: string, payload: TablePayload) {
    return api
      .put<TableResponse>(`/v1/partner/restaurants/${restaurantId}/tables/${tableId}`, payload)
      .then((response) => response.data)
  },

  deleteTable(restaurantId: string, tableId: string) {
    return api.delete(`/v1/partner/restaurants/${restaurantId}/tables/${tableId}`)
  },

  getMenuItems(restaurantId: string) {
    return api
      .get<MenuItemResponse[]>(`/v1/partner/restaurants/${restaurantId}/menu-items`)
      .then((response) => response.data)
  },

  createMenuItem(restaurantId: string, payload: MenuItemPayload) {
    return api
      .post<MenuItemResponse>(`/v1/partner/restaurants/${restaurantId}/menu-items`, payload)
      .then((response) => response.data)
  },

  updateMenuItem(restaurantId: string, itemId: string, payload: MenuItemPayload) {
    return api
      .put<MenuItemResponse>(`/v1/partner/restaurants/${restaurantId}/menu-items/${itemId}`, payload)
      .then((response) => response.data)
  },

  deleteMenuItem(restaurantId: string, itemId: string) {
    return api.delete(`/v1/partner/restaurants/${restaurantId}/menu-items/${itemId}`)
  },

  updateMenuItemStatus(restaurantId: string, itemId: string, trangThai: MenuItemStatus) {
    return api
      .patch<MenuItemResponse>(`/v1/partner/restaurants/${restaurantId}/menu-items/${itemId}/status`, {
        trangThai,
      })
      .then((response) => response.data)
  },

  getCombos(restaurantId: string) {
    return api
      .get<ComboResponse[]>(`/v1/partner/restaurants/${restaurantId}/combos`)
      .then((response) => response.data)
  },

  createCombo(restaurantId: string, payload: ComboPayload) {
    return api
      .post<ComboResponse>(`/v1/partner/restaurants/${restaurantId}/combos`, payload)
      .then((response) => response.data)
  },

  updateCombo(restaurantId: string, comboId: string, payload: ComboPayload) {
    return api
      .put<ComboResponse>(`/v1/partner/restaurants/${restaurantId}/combos/${comboId}`, payload)
      .then((response) => response.data)
  },

  deleteCombo(restaurantId: string, comboId: string) {
    return api.delete(`/v1/partner/restaurants/${restaurantId}/combos/${comboId}`)
  },
}
