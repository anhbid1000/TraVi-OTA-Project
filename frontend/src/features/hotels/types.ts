import type { LucideIcon } from 'lucide-react'
import type { PaginationParams, SortParams } from '../../types/common'

export type Image = {
  id: string
  url: string
  alt?: string
  isPrimary?: boolean
  displayOrder?: number
}

export type Amenity = {
  id: string
  name: string
  icon?: LucideIcon
  description?: string
}

export type FilterOption = {
  id: string
  name: string
  description?: string
}

export type HotelFilterOptions = {
  amenities: FilterOption[]
  types: FilterOption[]
  cuisines: FilterOption[]
}

export type HotelCatalog = {
  id: string
  name: string
  city: string
  district?: string
  address: string
  stars: number
  rating: number
  reviewCount: number
  thumbnailUrl?: string
  minPrice: number
  availableRooms: number
  amenityHighlights: string[]
  type?: string
  popularity?: number
}

export type RoomAvailability = {
  id: string
  name: string
  image: string
  areaM2: number
  maxGuests: number
  bedText: string
  description: string
  highlights: string[]
  pricePerNight: number
  availableQuantity: number
}

export type RoomComboItem = {
  roomId: string
  roomName: string
  roomType: string
  quantity: number
  capacityPerRoom: number
  pricePerRoom: number
}

export type RoomCombinationOption = {
  totalCapacity: number
  totalRooms: number
  totalPricePerNight: number
  image?: string
  items: RoomComboItem[]
}

export type HotelPolicy = {
  title: string
  description: string
}

export type HotelDetail = {
  id: string
  name: string
  location: string
  subtitle: string
  stars: number
  rating: number
  reviewCount: number
  description: string
  address: string
  latitude?: number
  longitude?: number
  images: Image[]
  amenities: Amenity[]
  policies: HotelPolicy[]
  rooms: RoomAvailability[]
  roomCombinationOptions?: RoomCombinationOption[]
}

export type HotelSearchParams = PaginationParams &
  SortParams & {
    city?: string
    keyword?: string
    checkIn?: string
    checkOut?: string
    guests?: number
    minPrice?: number
    maxPrice?: number
    stars?: number[]
    amenities?: string[]
    rating?: number
  }

export type HotelDetailParams = {
  checkIn?: string
  checkOut?: string
  guests?: number
}

