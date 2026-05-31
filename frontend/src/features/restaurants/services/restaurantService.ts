import { AxiosError } from 'axios'
import { api } from '../../../services/api'
import type { ApiErrorResponse, PageResponse } from '../../../types/common'
import {
  formatPriceRange,
  formatTimeValue,
  normalizeVietnameseText,
} from '../../../utils/display'
import type {
  RestaurantCatalog,
  RestaurantDetail,
  RestaurantDetailParams,
  RestaurantFilterOptions,
  RestaurantSearchParams,
} from '../types'

const DEFAULT_PAGE = 0
const DEFAULT_SIZE = 10
const MAX_SIZE = 50
type BackendRestaurantCatalogItem = {
  id: string
  tenNhaHang: string
  diaChi: string
  thanhPho: string
  loaiAmThuc: string
  thumbnailUrl?: string
  diemDanhGiaTrungBinh?: number
  soLuongDanhGia?: number
  khoangGia?: string
  soGheConTrong?: number
  tienIchNoiBat?: Array<{ tenTienIch?: string; moTa?: string }>
}

type BackendRestaurantDetail = {
  id: string
  businessProfileId?: string
  tenNhaHang: string
  moTa?: string
  diaChi?: string
  thanhPho?: string
  quanHuyen?: string
  phuongXa?: string
  kinhDo?: number
  viDo?: number
  loaiAmThuc?: string
  gioMoCua?: string
  gioDongCua?: string
  coDatMonTruoc?: boolean
  diemDanhGiaTrungBinh?: number
  soLuongDanhGia?: number
  images?: Array<{ id: string; duongDanUrl?: string; moTaAnh?: string; laAnhDaiDien?: boolean }>
  amenities?: Array<{ id: string; tenTienIch?: string; moTa?: string }>
  availableTables?: Array<{ tableId: string; tenBan?: string; soGhe?: number; viTri?: string; available?: boolean }>
  menus?: Array<{
    id: string
    tenThucDon?: string
    moTa?: string
    items?: Array<{
      id: string
      tenMon?: string
      moTa?: string
      gia?: number
      danhMucMon?: string
      anhMonUrl?: string
      trangThai?: string
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
  }

  const content = raw.content ?? raw.items ?? []
  const page = raw.page ?? raw.number ?? DEFAULT_PAGE
  const size = raw.size ?? DEFAULT_SIZE
  const totalElements = raw.totalElements ?? raw.total ?? content.length
  const totalPages = raw.totalPages ?? Math.max(1, Math.ceil(totalElements / Math.max(1, size)))

  return {
    content,
    page,
    size,
    totalElements,
    totalPages,
    hasNext: raw.hasNext ?? page + 1 < totalPages,
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

function buildSearchParams(params: RestaurantSearchParams) {
  return {
    city: params.city,
    keyword: params.keyword,
    cuisine: toCsv(params.cuisine),
    date: params.date,
    time: params.time,
    guests: params.guests,
    minPrice: params.minPrice,
    maxPrice: params.maxPrice,
    amenities: toCsv(params.amenities),
    rating: params.rating,
    sort: params.sort,
    page: Math.max(DEFAULT_PAGE, params.page ?? DEFAULT_PAGE),
    size: clampSize(params.size),
  }
}

function mapRestaurantCatalogItem(item: BackendRestaurantCatalogItem): RestaurantCatalog {
  return {
    id: item.id,
    name: normalizeVietnameseText(item.tenNhaHang, { titleCase: true }),
    city: normalizeVietnameseText(item.thanhPho, { titleCase: true }),
    district: undefined,
    address: normalizeVietnameseText(item.diaChi, { titleCase: true }),
    cuisine: normalizeVietnameseText(item.loaiAmThuc, { titleCase: true }),
    rating: item.diemDanhGiaTrungBinh ?? 0,
    reviewCount: item.soLuongDanhGia ?? 0,
    thumbnailUrl: item.thumbnailUrl,
    priceRange: formatPriceRange(item.khoangGia),
    averagePrice: 0,
    availableSeats: item.soGheConTrong ?? 0,
    amenityHighlights: (item.tienIchNoiBat ?? [])
      .map((amenity) => normalizeVietnameseText(amenity.tenTienIch || amenity.moTa || '', { titleCase: true }))
      .filter(Boolean),
  }
}

function toOpeningHours(open?: string, close?: string) {
  if (open && close) {
    return `${formatTimeValue(open)} - ${formatTimeValue(close)}`
  }
  return 'Đang cập nhật'
}

function mapRestaurantDetail(payload: BackendRestaurantDetail): RestaurantDetail {
  const locationParts = [payload.phuongXa, payload.quanHuyen, payload.thanhPho]
    .filter(Boolean)
    .map((part) => normalizeVietnameseText(part, { titleCase: true }))
  const location = locationParts.join(', ') || normalizeVietnameseText(payload.diaChi, { titleCase: true }) || 'Việt Nam'
  const description = normalizeVietnameseText(payload.moTa) || 'Đang cập nhật mô tả'

  return {
    id: payload.id,
    businessProfileId: payload.businessProfileId || payload.id,
    name: normalizeVietnameseText(payload.tenNhaHang, { titleCase: true }),
    location,
    city: normalizeVietnameseText(payload.thanhPho, { titleCase: true }) || 'Việt Nam',
    cuisine: normalizeVietnameseText(payload.loaiAmThuc, { titleCase: true }) || 'Đang cập nhật',
    rating: payload.diemDanhGiaTrungBinh ?? 0,
    reviewCount: payload.soLuongDanhGia ?? 0,
    description,
    subtitle: description,
    badges: [],
    tablesRemaining: (payload.availableTables ?? []).filter((table) => table.available).length,
    allowPreOrder: Boolean(payload.coDatMonTruoc),
    address: normalizeVietnameseText(payload.diaChi, { titleCase: true }) || location,
    latitude: payload.viDo,
    longitude: payload.kinhDo,
    images: (payload.images ?? []).map((image) => ({
      id: image.id,
      url: image.duongDanUrl || '',
      alt: normalizeVietnameseText(image.moTaAnh),
      isPrimary: image.laAnhDaiDien,
    })),
    amenities: (payload.amenities ?? []).map((amenity) => ({
      id: amenity.id,
      name: normalizeVietnameseText(amenity.tenTienIch, { titleCase: true }) || 'Tiện ích',
      description: normalizeVietnameseText(amenity.moTa),
    })),
    availableTables: (payload.availableTables ?? []).map((table) => ({
      id: table.tableId,
      name: normalizeVietnameseText(table.tenBan, { titleCase: true }) || 'Bàn',
      seats: table.soGhe ?? 0,
      location: normalizeVietnameseText(table.viTri, { titleCase: true }),
      available: Boolean(table.available),
    })),
    menus: (payload.menus ?? []).map((menu) => ({
      id: menu.id,
      name: normalizeVietnameseText(menu.tenThucDon, { titleCase: true }) || 'Thực đơn',
      description: normalizeVietnameseText(menu.moTa),
      items: (menu.items ?? []).map((item) => ({
        id: item.id,
        name: normalizeVietnameseText(item.tenMon, { titleCase: true }) || 'Món ăn',
        image: item.anhMonUrl || '',
        description: normalizeVietnameseText(item.moTa),
        price: item.gia ?? 0,
        category: normalizeVietnameseText(item.danhMucMon, { titleCase: true }) || 'Khác',
        status: (item.trangThai === 'TAM_HET' || item.trangThai === 'NGUNG_BAN' ? item.trangThai : 'DANG_BAN') as 'DANG_BAN' | 'TAM_HET' | 'NGUNG_BAN',
        deleted: false,
      })),
    })),
    practicalInfo: {
      openingHours: toOpeningHours(payload.gioMoCua, payload.gioDongCua),
      dressCode: 'Đang cập nhật',
      contact: 'Đang cập nhật',
    },
  }
}

export async function searchRestaurants(
  params: RestaurantSearchParams,
): Promise<PageResponse<RestaurantCatalog>> {
  const response = await api.get('/v1/public/restaurants/search', {
    params: buildSearchParams(params),
  })

  try {
    const normalized = normalizePage<BackendRestaurantCatalogItem>(response.data)
    return {
      ...normalized,
      content: normalized.content.map(mapRestaurantCatalogItem),
    }
  } catch (error) {
    throw toApiError(error)
  }
}

export async function getRestaurantDetail(
  id: string,
  params: RestaurantDetailParams,
): Promise<RestaurantDetail> {
  if (!id) {
    throw new Error('Thiếu mã nhà hàng')
  }

  const response = await api.get<BackendRestaurantDetail>(`/v1/public/restaurants/${id}`, {
    params,
  })
  return mapRestaurantDetail(response.data)
}

export async function getFeaturedRestaurants(
  params: RestaurantSearchParams,
): Promise<RestaurantCatalog[]> {
  const response = await api.get<BackendRestaurantCatalogItem[]>('/v1/public/restaurants/featured', {
    params: {
      date: params.date,
      time: params.time,
      guests: params.guests,
      size: clampSize(params.size ?? 3),
    },
  })

  return response.data.map(mapRestaurantCatalogItem)
}

export async function getRestaurantFilterOptions(): Promise<RestaurantFilterOptions> {
  const response = await api.get<RestaurantFilterOptions>('/v1/public/restaurants/filter-options/data')
  return response.data
}


