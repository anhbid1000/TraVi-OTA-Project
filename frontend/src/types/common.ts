export type PageResponse<T> = {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  hasNext: boolean
  hasPrevious: boolean
}

export type PaginationParams = {
  page?: number
  size?: number
}

export type SortParams = {
  sort?: string
}

export type ApiErrorResponse = {
  status?: number
  error?: string
  message: string
  timestamp?: string
  path?: string
}

