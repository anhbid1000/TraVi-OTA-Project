import { API_BASE_URL, api } from './api'
import type {
  BusinessProfilePayload,
  BusinessProfileResponse,
  ComboPayload,
  ComboResponse,
  MenuItemPayload,
  MenuItemResponse,
  MenuPayload,
  MenuResponse,
  MenuItemStatus,
  RoomPayload,
  RoomResponse,
  PartnerDashboardOverviewResponse,
  PartnerBookingDetail,
  PartnerBookingPageResponse,
  PartnerBookingStatus,
  PartnerRestaurantDashboardOverviewResponse,
  RestaurantDashboardPeriod,
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

  getDashboardOverview(days = 30) {
    return api
      .get<PartnerDashboardOverviewResponse>('/v1/partner/dashboard/overview', {
        params: { days },
      })
      .then((response) => response.data)
  },

  getRestaurantDashboardOverview(period: RestaurantDashboardPeriod = 'TODAY') {
    return api
      .get<PartnerRestaurantDashboardOverviewResponse>('/v1/partner/dashboard/restaurant-overview', {
        params: { period },
      })
      .then((response) => response.data)
  },

  getPartnerBookings(params: {
    status?: PartnerBookingStatus[]
    fromDate?: string
    toDate?: string
    keyword?: string
    page?: number
    size?: number
    sort?: string
  }) {
    const query = new URLSearchParams()
    if (params.status && params.status.length > 0) {
      params.status.forEach((status) => query.append('status', status))
    }
    if (params.fromDate) query.set('fromDate', params.fromDate)
    if (params.toDate) query.set('toDate', params.toDate)
    if (params.keyword) query.set('keyword', params.keyword)
    if (typeof params.page === 'number') query.set('page', String(params.page))
    if (typeof params.size === 'number') query.set('size', String(params.size))
    if (params.sort) query.set('sort', params.sort)

    return api
      .get<PartnerBookingPageResponse>('/v1/partner/bookings', { params: query })
      .then((response) => response.data)
  },

  getPartnerBookingDetail(bookingId: string) {
    return api
      .get<PartnerBookingDetail>(`/v1/partner/bookings/${bookingId}`)
      .then((response) => response.data)
  },

  updatePartnerBookingStatus(
    bookingId: string,
    payload: { targetStatus: PartnerBookingStatus; reason?: string },
  ) {
    return api
      .patch<PartnerBookingDetail>(`/v1/partner/bookings/${bookingId}/status`, payload)
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

  getHotelAmenities() {
    return api
      .get<Array<{ id: string; tenTienIch: string; loaiTienIch: string; moTa: string }>>(
        '/v1/public/amenities/hotels'
      )
      .then((response) => response.data)
  },
  getRestaurantAmenities() {
    return api
      .get<{ amenities: Array<{ value: string; label: string; description?: string | null }> }>(
        '/v1/public/restaurants/filter-options/data'
      )
      .then((response) => response.data.amenities.map((item) => item.label))
  },

  getRooms(hotelId: string) {
    return api
      .get<RoomResponse[]>(`/v1/partner/hotels/${hotelId}/rooms`)
      .then((response) => response.data)
  },
  getHotelDetail(hotelId: string) {
    return api
      .get<{
        idTaiSan: string
        idHoSo: string
        ten: string
        moTa?: string
        giaCoBan: number
        hangSao?: number
        loaiKhachSan?: string
        gioNhanPhong?: string
        gioTraPhong?: string
        soTang?: number
        tongSoPhong?: number
        tienIch?: Array<{ id: string; tenTienIch: string; loaiTienIch?: string; moTa?: string }>
        danhSachAnh?: Array<{
          id: string
          doiTuongId: string
          duongDanUrl: string
          moTaAnh?: string
          laAnhDaiDien?: boolean
          ngayTaiLen?: string
        }>
      }>(`/v1/partner/hotels/${hotelId}`)
      .then((response) => response.data)
  },

  createRoom(hotelId: string, payload: RoomPayload) {
    return api
      .post<RoomResponse>(`/v1/partner/hotels/${hotelId}/rooms`, payload)
      .then((response) => response.data)
  },
  updateHotelAmenities(hotelId: string, amenityIds: string[]) {
    return api
      .patch(`/v1/partner/hotels/${hotelId}/amenities`, { amenityIds })
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
  getRestaurantDetail(restaurantId: string) {
    return api
      .get<{
        idTaiSan: string
        idHoSo: string
        ten: string
        moTa?: string
        giaCoBan: number
        sucChua?: number
        loaiAmThuc?: string
        gioMoCua?: string
        gioDongCua?: string
        coDatBanTruoc?: boolean
        coDatMonTruoc?: boolean
        danhSachAnh?: Array<{
          id: string
          doiTuongId: string
          duongDanUrl: string
          moTaAnh?: string
          laAnhDaiDien?: boolean
          ngayTaiLen?: string
        }>
        tienIch?: Array<{
          id: string
          tenTienIch: string
          loaiTienIch?: string
          moTa?: string
          coThuPhi?: boolean
          phiSuDung?: number
        }>
      }>(`/v1/partner/restaurants/${restaurantId}`)
      .then((response) => response.data)
  },
  updateRestaurantAmenities(
    restaurantId: string,
    tienIchNhaHang: Array<{ tenTienIch: string; loaiTienIch: string; moTa: string; coThuPhi: boolean; phiSuDung: number }>,
  ) {
    return api
      .patch(`/v1/partner/restaurants/${restaurantId}/amenities`, { tienIchNhaHang })
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


  getMenus(restaurantId: string) {
    return api
      .get<MenuResponse[]>(`/v1/partner/restaurants/${restaurantId}/menus`)
      .then((response) => response.data)
  },

  createMenu(restaurantId: string, payload: MenuPayload) {
    return api
      .post<MenuResponse>(`/v1/partner/restaurants/${restaurantId}/menus`, payload)
      .then((response) => response.data)
  },

  updateMenu(restaurantId: string, menuId: string, payload: MenuPayload) {
    return api
      .put<MenuResponse>(`/v1/partner/restaurants/${restaurantId}/menus/${menuId}`, payload)
      .then((response) => response.data)
  },

  deleteMenu(restaurantId: string, menuId: string) {
    return api.delete(`/v1/partner/restaurants/${restaurantId}/menus/${menuId}`)
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
