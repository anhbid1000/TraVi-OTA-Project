import { api } from '../../../services/api';
import { getApiErrorMessage } from '../../../utils/apiError';
import type { LoaiDichVu } from '../types/review';

export type BookingStatusV2 = 'DA_HOAN_THANH' | 'CHO_THANH_TOAN' | 'DA_THANH_TOAN' | 'DA_HUY' | 'CHO_XAC_NHAN' | string;

export interface BookingHistoryItemV2 {
  id: string;
  serviceName: string;
  serviceType: LoaiDichVu;
  bookingId?: string;
  reservationId?: string;
  dateLabel: string;
  totalPrice: number;
  status: BookingStatusV2;
  reviewed?: boolean;
  thumbnailUrl?: string;
}

type BackendBookingLike = Record<string, unknown>;

function readString(item: BackendBookingLike, keys: string[], fallback = '') {
  for (const key of keys) {
    const value = item[key];
    if (typeof value === 'string' && value.trim()) return value;
  }
  return fallback;
}

function readNumber(item: BackendBookingLike, keys: string[], fallback = 0) {
  for (const key of keys) {
    const value = item[key];
    if (typeof value === 'number') return value;
    if (typeof value === 'string' && !Number.isNaN(Number(value))) return Number(value);
  }
  return fallback;
}

function readBoolean(item: BackendBookingLike, keys: string[], fallback = false) {
  for (const key of keys) {
    const value = item[key];
    if (typeof value === 'boolean') return value;
  }
  return fallback;
}

function normalizeBooking(item: BackendBookingLike): BookingHistoryItemV2 {
  const serviceType = readString(item, ['loaiDichVu', 'serviceType', 'type'], 'KHACH_SAN') === 'NHA_HANG' ? 'NHA_HANG' : 'KHACH_SAN';
  const id = readString(item, ['id', 'bookingId', 'reservationId', 'maDon'], '');
  return {
    id,
    serviceName: readString(item, ['tenCoSo', 'serviceName', 'hotelName', 'restaurantName', 'tenKhachSan', 'tenNhaHang'], 'Dịch vụ TraVi'),
    serviceType,
    bookingId: serviceType === 'KHACH_SAN' ? readString(item, ['bookingId', 'id'], id) : undefined,
    reservationId: serviceType === 'NHA_HANG' ? readString(item, ['reservationId', 'id'], id) : undefined,
    dateLabel: readString(item, ['dateLabel', 'ngayTao', 'createdAt', 'ngayCheckIn', 'ngayGioBatDau'], 'Đang cập nhật'),
    totalPrice: readNumber(item, ['tongTienThanhToan', 'totalPrice', 'price'], 0),
    status: readString(item, ['trangThaiDon', 'status'], 'CHO_THANH_TOAN'),
    reviewed: readBoolean(item, ['reviewed', 'daDanhGia'], false),
    thumbnailUrl: readString(item, ['thumbnailUrl', 'imageUrl', 'anhDaiDien'], ''),
  };
}

export const bookingServiceV2 = {
  async getCustomerBookings(): Promise<BookingHistoryItemV2[]> {
    try {
      const { data } = await api.get('/v1/user/bookings');
      const rawItems = Array.isArray(data) ? data : (data?.content ?? data?.items ?? []);
      return rawItems.map((item: BackendBookingLike) => normalizeBooking(item));
    } catch (error) {
      throw new Error(getApiErrorMessage(error, 'Không thể tải danh sách chuyến đi.'));
    }
  },
};
