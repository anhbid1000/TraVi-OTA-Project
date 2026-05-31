import { api } from './api';

export type UserProfile = {
  id: number
  username: string
  email: string
  hoTen: string
  soDienThoai: string
  vaiTro: string
}

export type CreateHotelBookingPayload = {
  hotelId: string
  tenNguoiDat: string
  sdtNguoiDat: string
  emailNguoiDat: string
  ghiChu?: string
  ngayCheckIn: string
  ngayCheckOut: string
  soKhach?: number
  gioNhanPhongDuKien?: string
  expectedTotalAmount?: number
  rooms: Array<{ roomId: string; soLuong: number }>
}

export type CreateRestaurantBookingPayload = {
  restaurantId: string
  tenNguoiDat: string
  sdtNguoiDat: string
  emailNguoiDat: string
  ghiChu?: string
  date: string
  time: string
  soNguoi: number
}

export type HotelBookingResponse = {
  id: string
  maDon: string
  hotelId: string
  tenKhachSan: string
  ngayCheckIn: string
  ngayCheckOut: string
  soDem: number
  soKhach: number
  gioNhanPhongDuKien?: string
  tongTienGoc: number
  tienKhuyenMai: number
  tongTienThanhToan: number
  trangThaiDon: string
  paymentExpiredAt: string
  paymentExpiresInSeconds?: number
  rooms: Array<{
    roomId: string
    tenPhong: string
    soLuong: number
    donGia: number
    soDem: number
    thanhTien: number
  }>
}

export type RestaurantBookingResponse = {
  id: string
  maDon: string
  restaurantId: string
  tenNhaHang: string
  ngayGioBatDau: string
  ngayGioKetThuc: string
  soNguoi: number
  tienCoc: number
  tongTienThanhToan: number
  trangThaiDon: string
  paymentExpiredAt: string
  paymentExpiresInSeconds?: number
  tables: Array<{
    tableId: string
    tenBan: string
    soChoNgoi: number
    viTriSanh?: string
  }>
}

export type BookingSearchResponse = {
  id: string
  maDon: string
  tenTaiSan: string
  anhTaiSan: string
  ngayTao: string
  tongTienThanhToan: number
  trangThai: string
  loaiTaiSan: 'HOTEL' | 'RESTAURANT'
  ngayBatDau?: string
  ngayKetThuc?: string
  tenNguoiDat: string
  sdtNguoiDat: string
  emailNguoiDat: string
  soKhach: number
  rooms?: Array<{
    tenPhong: string
    soLuong: number
    donGia: number
    thanhTien: number
  }>
  tables?: Array<{
    tenBan: string
    soChoNgoi: number
    viTri?: string
  }>
}

export const userService = {
  async getProfile(): Promise<UserProfile> {
    const response = await api.get('v1/user/me');
    return response.data
  },

  async createHotelBooking(payload: CreateHotelBookingPayload): Promise<HotelBookingResponse> {
    const response = await api.post('v1/user/bookings/hotel', payload)
    return response.data
  },

  async createRestaurantBooking(payload: CreateRestaurantBookingPayload): Promise<RestaurantBookingResponse> {
    const response = await api.post('v1/user/bookings/restaurant', payload)
    return response.data
  },

  async mockPayHotelBooking(bookingId: string): Promise<HotelBookingResponse> {
    const response = await api.post('v1/user/bookings/hotel/mock-pay', {
      bookingId,
      paymentRef: `mock_${Date.now()}`,
    })
    return response.data
  },

  async mockPayRestaurantBooking(bookingId: string): Promise<RestaurantBookingResponse> {
    const response = await api.post('v1/user/bookings/restaurant/mock-pay', {
      bookingId,
      paymentRef: `mock_${Date.now()}`,
    })
    return response.data
  },

  async getBookingHistory(type: 'all' | 'hotel' | 'restaurant' = 'all'): Promise<BookingSearchResponse[]> {
    const response = await api.get('v1/user/bookings/history', {
      params: { type }
    })
    return response.data
  },

  async lookupBookingPublicly(maDon: string, email?: string, phone?: string): Promise<BookingSearchResponse> {
    const response = await api.get('v1/public/bookings/search', {
      params: { maDon, email, phone }
    })
    return response.data
  },
}
