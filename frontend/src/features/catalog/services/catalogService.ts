import { AxiosError } from 'axios'
import { api } from '../../../services/api'
import type { ApiErrorResponse } from '../../../types/common'

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

export async function getCitySuggestions(): Promise<string[]> {
  try {
    const response = await api.get<string[]>('/v1/public/locations/cities')
    return (response.data ?? []).filter((city) => typeof city === 'string' && city.trim().length > 0)
  } catch (error) {
    throw toApiError(error)
  }
}


