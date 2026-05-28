import type { LucideIcon } from 'lucide-react'
import type { PaginationParams, SortParams } from '../../types/common'
import type { Amenity, FilterOption, Image } from '../hotels/types'

export type RestaurantCatalog = {
  id: string
  name: string
  city: string
  district?: string
  address: string
  cuisine: string
  rating: number
  reviewCount: number
  thumbnailUrl?: string
  priceRange: string
  averagePrice: number
  availableSeats: number
  amenityHighlights: string[]
  popularity?: number
}

export type TableAvailability = {
  id: string
  name: string
  seats: number
  location?: string
  available: boolean
}

export type MenuItemStatus = 'DANG_BAN' | 'TAM_HET' | 'NGUNG_BAN'

export type MenuItem = {
  id: string
  name: string
  image: string
  description: string
  price: number
  category: string
  status: MenuItemStatus
  deleted: boolean
}

export type Menu = {
  id: string
  name: string
  description?: string
  items: MenuItem[]
}

export type RestaurantPracticalInfo = {
  openingHours: string
  dressCode: string
  contact: string
}

export type RestaurantDetail = {
  id: string
  name: string
  location: string
  city: string
  cuisine: string
  rating: number
  reviewCount: number
  description: string
  subtitle: string
  badges: string[]
  tablesRemaining: number
  allowPreOrder: boolean
  address: string
  latitude?: number
  longitude?: number
  images: Image[]
  amenities: Array<Amenity & { icon?: LucideIcon }>
  availableTables: TableAvailability[]
  menus: Menu[]
  practicalInfo: RestaurantPracticalInfo
}

export type RestaurantSearchParams = PaginationParams &
  SortParams & {
    city?: string
    keyword?: string
    cuisine?: string[]
    date?: string
    time?: string
    guests?: number
    minPrice?: number
    maxPrice?: number
    amenities?: string[]
    rating?: number
  }

export type RestaurantDetailParams = {
  date?: string
  time?: string
  guests?: number
}

export type RestaurantFilterOptions = {
  amenities: FilterOption[]
  types: FilterOption[]
  cuisines: FilterOption[]
}

