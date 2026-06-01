import { AxiosError } from 'axios'
import { api } from '../../../services/api'
import type { ApiErrorResponse, PageResponse } from '../../../types/common'
import { formatTimeValue, normalizeVietnameseText } from '../../../utils/display'
import { getDefaultHotelStay } from '../../../utils/date'
import type {
  Amenity,
  HotelCatalog,
  HotelFilterOptions,
  HotelDetail,
  HotelDetailParams,
  HotelPolicy,
  RoomAvailability,
  RoomCombinationOption,
  HotelSearchParams,
  Image,
} from '../types'

const DEFAULT_PAGE = 0
const DEFAULT_SIZE = 10
const MAX_SIZE = 50
type BackendHotelCatalogItem = {
  id: string
  tenKhachSan: string
  diaChi: string
  thanhPho: string
  hangSao: number
  thumbnailUrl?: string
  diemDanhGiaTrungBinh?: number
  soLuongDanhGia?: number
  giaThapNhat?: number
  soPhongConTrong?: number
  tienIchNoiBat?: Array<{ tenTienIch?: string; moTa?: string }>
}

type BackendHotelDetail = {
  id: string
  businessProfileId?: string
  tenKhachSan: string
  moTa?: string
  diaChi?: string
  thanhPho?: string
  quanHuyen?: string
  phuongXa?: string
  kinhDo?: number
  viDo?: number
  hangSao?: number
  gioNhanPhong?: string
  gioTraPhong?: string
  chinhSach?: {
    gioNhanPhong?: string
    gioTraPhong?: string
    chinhSachHuy?: string
    chinhSachHoanTien?: string
    quyDinhTreEm?: string
    quyDinhVatNuoi?: string
    ghiChuKhac?: string
  } | null
  diemDanhGiaTrungBinh?: number
  soLuongDanhGia?: number
  images?: Array<{ id: string; duongDanUrl?: string; moTaAnh?: string; laAnhDaiDien?: boolean }>
  amenities?: Array<{ id: string; tenTienIch?: string; moTa?: string }>
  availableRooms?: Array<{
    roomId: string
    tenPhong?: string
    loaiPhong?: string
    moTa?: string
    dienTich?: number
    soKhachToiDa?: number
    soGiuong?: number
    giaCoBan?: number
    soLuongConTrong?: number
    images?: Array<{ id: string; duongDanUrl?: string; moTaAnh?: string; laAnhDaiDien?: boolean }>
    amenities?: string[]
  }>
  roomCombinationOptions?: Array<{
    totalCapacity?: number
    totalRooms?: number
    totalPricePerNight?: number
    items?: Array<{
      roomId: string
      roomName?: string
      roomType?: string
      quantity?: number
      capacityPerRoom?: number
      pricePerRoom?: number
    }>
  }>
}

function clampSize(size?: number) {
  if (!size || size < 1) {
    return DEFAULT_SIZE
  }
  return Math.min(size, MAX_SIZE)
}

function normalizePage<T>(value: unknown): PageResponse<T> {
  const raw = (value ?? {}) as {
    content?: T[]
    items?: T[]
    page?: number
    number?: number
    size?: number
    totalElements?: number
    total?: number
    totalPages?: number
    hasNext?: boolean
    hasPrevious?: boolean
    first?: boolean
    last?: boolean
  }

  const content = raw.content ?? raw.items ?? []
  const page = raw.page ?? raw.number ?? DEFAULT_PAGE
  const size = raw.size ?? DEFAULT_SIZE
  const totalElements = raw.totalElements ?? raw.total ?? content.length
  const totalPages = raw.totalPages ?? Math.max(1, Math.ceil(totalElements / Math.max(1, size)))
  const hasNext = raw.hasNext ?? (typeof raw.last === 'boolean' ? !raw.last : page + 1 < totalPages)

  return {
    content,
    page,
    size,
    totalElements,
    totalPages,
    hasNext,
    hasPrevious: raw.hasPrevious ?? page > 0,
  }
}

function toApiError(error: unknown): Error {
  if (error instanceof AxiosError) {
    const payload = error.response?.data as ApiErrorResponse | string | undefined
    if (typeof payload === 'string' && payload.trim()) {
      return new Error(payload)
    }
    if (payload && typeof payload === 'object' && 'message' in payload && payload.message) {
      return new Error(payload.message)
    }
    return new Error(error.message)
  }
  if (error instanceof Error) {
    return error
  }
  return new Error('Lỗi không xác định')
}

function toCsv(values?: Array<string | number>) {
  if (!values || values.length === 0) {
    return undefined
  }
  return values.join(',')
}

function buildSearchParams(params: HotelSearchParams) {
  return {
    city: params.city,
    keyword: params.keyword,
    checkIn: params.checkIn,
    checkOut: params.checkOut,
    guests: params.guests,
    minPrice: params.minPrice,
    maxPrice: params.maxPrice,
    stars: toCsv(params.stars),
    amenities: toCsv(params.amenities),
    sort: params.sort,
    page: Math.max(DEFAULT_PAGE, params.page ?? DEFAULT_PAGE),
    size: clampSize(params.size),
  }
}

function hasHotelSearchCriteria(params: HotelSearchParams) {
  return Boolean(
    params.city?.trim() ||
    params.keyword?.trim() ||
    params.checkIn ||
    params.checkOut ||
    params.minPrice ||
    params.maxPrice ||
    (params.stars && params.stars.length > 0) ||
    (params.amenities && params.amenities.length > 0) ||
    params.rating,
  )
}

function pageFromItems<T>(items: T[], params: HotelSearchParams): PageResponse<T> {
  const page = Math.max(DEFAULT_PAGE, params.page ?? DEFAULT_PAGE)
  const size = clampSize(params.size)
  const hasNext = items.length >= size && size < MAX_SIZE

  return {
    content: items,
    page,
    size,
    totalElements: items.length,
    totalPages: Math.max(1, Math.ceil(items.length / Math.max(1, size))),
    hasNext,
    hasPrevious: page > 0,
  }
}

function mapHotelCatalogItem(item: BackendHotelCatalogItem): HotelCatalog {
  return {
    id: item.id,
    name: normalizeVietnameseText(item.tenKhachSan, { titleCase: true }),
    city: normalizeVietnameseText(item.thanhPho, { titleCase: true }),
    district: undefined,
    address: normalizeVietnameseText(item.diaChi, { titleCase: true }),
    stars: item.hangSao ?? 0,
    rating: item.diemDanhGiaTrungBinh ?? 0,
    reviewCount: item.soLuongDanhGia ?? 0,
    thumbnailUrl: item.thumbnailUrl,
    minPrice: item.giaThapNhat ?? 0,
    availableRooms: item.soPhongConTrong ?? 0,
    amenityHighlights: (item.tienIchNoiBat ?? [])
      .map((amenity) => normalizeVietnameseText(amenity.tenTienIch || amenity.moTa || '', { titleCase: true }))
      .filter(Boolean),
  }
}

function mapImage(item: { id: string; duongDanUrl?: string; moTaAnh?: string; laAnhDaiDien?: boolean }): Image {
  return {
    id: item.id,
    url: item.duongDanUrl || '',
    alt: normalizeVietnameseText(item.moTaAnh),
    isPrimary: item.laAnhDaiDien,
  }
}

function mapAmenity(item: { id: string; tenTienIch?: string; moTa?: string }): Amenity {
  return {
    id: item.id,
    name: normalizeVietnameseText(item.tenTienIch, { titleCase: true }) || 'Tiện ích',
    description: normalizeVietnameseText(item.moTa),
  }
}

function mapRoom(item: NonNullable<BackendHotelDetail['availableRooms']>[number]): RoomAvailability {
  const coverImage = item.images?.find((image) => image.laAnhDaiDien) ?? item.images?.[0]
  return {
    id: item.roomId,
    name: normalizeVietnameseText(item.tenPhong, { titleCase: true }) || 'Phòng',
    image: coverImage?.duongDanUrl || '',
    areaM2: item.dienTich ?? 0,
    maxGuests: item.soKhachToiDa ?? 0,
    bedText: item.soGiuong ? `${item.soGiuong} giường` : 'Đang cập nhật',
    description: normalizeVietnameseText(item.moTa || item.loaiPhong) || 'Đang cập nhật',
    highlights: (item.amenities ?? []).map((amenity) => normalizeVietnameseText(amenity, { titleCase: true })),
    pricePerNight: item.giaCoBan ?? 0,
    availableQuantity: item.soLuongConTrong ?? 0,
  }
}

function mapPolicies(payload: BackendHotelDetail['chinhSach']): HotelPolicy[] {
  if (!payload) {
    return []
  }

  const candidates: HotelPolicy[] = [
    { title: 'Giờ nhận phòng', description: payload.gioNhanPhong ? formatTimeValue(payload.gioNhanPhong) : 'Đang cập nhật' },
    { title: 'Giờ trả phòng', description: payload.gioTraPhong ? formatTimeValue(payload.gioTraPhong) : 'Đang cập nhật' },
    { title: 'Chính sách hủy', description: normalizeVietnameseText(payload.chinhSachHuy) || 'Đang cập nhật' },
    { title: 'Chính sách hoàn tiền', description: normalizeVietnameseText(payload.chinhSachHoanTien) || 'Đang cập nhật' },
    { title: 'Quy định trẻ em', description: normalizeVietnameseText(payload.quyDinhTreEm) || 'Đang cập nhật' },
    { title: 'Quy định vật nuôi', description: normalizeVietnameseText(payload.quyDinhVatNuoi) || 'Đang cập nhật' },
    { title: 'Ghi chú khác', description: normalizeVietnameseText(payload.ghiChuKhac) || 'Đang cập nhật' },
  ]

  return candidates.filter((item) => item.description && item.description !== 'Đang cập nhật')
}

function mapRoomComboOption(
  item: NonNullable<BackendHotelDetail['roomCombinationOptions']>[number],
  availableRooms: RoomAvailability[]
): RoomCombinationOption {
  let highestPriceRoomImage = ''
  let maxPrice = -1

  const items = (item.items ?? []).map((x) => {
    if ((x.pricePerRoom ?? 0) > maxPrice) {
      maxPrice = x.pricePerRoom ?? 0
      const room = availableRooms.find((r) => r.id === x.roomId)
      if (room && room.image) {
        highestPriceRoomImage = room.image
      }
    }

    return {
      roomId: x.roomId,
      roomName: normalizeVietnameseText(x.roomName, { titleCase: true }) || 'Phòng',
      roomType: normalizeVietnameseText(x.roomType, { titleCase: true }) || 'Unknown',
      quantity: x.quantity ?? 0,
      capacityPerRoom: x.capacityPerRoom ?? 0,
      pricePerRoom: x.pricePerRoom ?? 0,
    }
  })

  return {
    totalCapacity: item.totalCapacity ?? 0,
    totalRooms: item.totalRooms ?? 0,
    totalPricePerNight: item.totalPricePerNight ?? 0,
    image: highestPriceRoomImage,
    items,
  }
}

function mapHotelDetail(payload: BackendHotelDetail): HotelDetail {
  const locationParts = [payload.phuongXa, payload.quanHuyen, payload.thanhPho]
    .filter(Boolean)
    .map((part) => normalizeVietnameseText(part, { titleCase: true }))
  const location = locationParts.join(', ') || normalizeVietnameseText(payload.diaChi, { titleCase: true }) || 'Việt Nam'
  const description = normalizeVietnameseText(payload.moTa) || 'Đang cập nhật mô tả'

  const rooms = (payload.availableRooms ?? []).map(mapRoom)

  return {
    id: payload.id,
    businessProfileId: payload.businessProfileId || payload.id,
    name: normalizeVietnameseText(payload.tenKhachSan, { titleCase: true }),
    location,
    subtitle: description,
    stars: payload.hangSao ?? 0,
    rating: payload.diemDanhGiaTrungBinh ?? 0,
    reviewCount: payload.soLuongDanhGia ?? 0,
    description,
    address: normalizeVietnameseText(payload.diaChi, { titleCase: true }) || location,
    latitude: payload.viDo,
    longitude: payload.kinhDo,
    images: (payload.images ?? []).map(mapImage),
    amenities: (payload.amenities ?? []).map(mapAmenity),
    policies: mapPolicies(payload.chinhSach),
    rooms,
    roomCombinationOptions: (payload.roomCombinationOptions ?? []).map((combo) => mapRoomComboOption(combo, rooms)),
  }
}

export async function searchHotels(params: HotelSearchParams): Promise<PageResponse<HotelCatalog>> {
  if (!hasHotelSearchCriteria(params)) {
    const defaultStay = getDefaultHotelStay()
    const featuredHotels = await getFeaturedHotels({
      checkIn: defaultStay.checkIn,
      checkOut: defaultStay.checkOut,
      guests: params.guests ?? defaultStay.guests,
      size: params.size,
    })
    return pageFromItems(featuredHotels, params)
  }

  const response = await api.get('/v1/public/hotels/search', {
    params: buildSearchParams(params),
  })

  try {
    const normalized = normalizePage<BackendHotelCatalogItem>(response.data)
    return {
      ...normalized,
      content: normalized.content.map(mapHotelCatalogItem),
    }
  } catch (error) {
    throw toApiError(error)
  }
}

export async function getHotelDetail(id: string, params: HotelDetailParams): Promise<HotelDetail> {
  if (!id) {
    throw new Error('Thiếu mã khách sạn')
  }

  const response = await api.get<BackendHotelDetail>(`/v1/public/hotels/${id}`, {
    params,
  })
  return mapHotelDetail(response.data)
}

export async function getFeaturedHotels(params: HotelSearchParams): Promise<HotelCatalog[]> {
  const response = await api.get<BackendHotelCatalogItem[]>('/v1/public/hotels/featured', {
    params: {
      checkIn: params.checkIn,
      checkOut: params.checkOut,
      guests: params.guests,
      size: clampSize(params.size ?? 4),
    },
  })

  return response.data.map(mapHotelCatalogItem)
}

export async function getHotelFilterOptions(): Promise<HotelFilterOptions> {
  const response = await api.get<HotelFilterOptions>('/v1/public/hotels/filter-options/data')
  return response.data
}
