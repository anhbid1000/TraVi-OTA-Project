import { useCallback, useEffect, useRef, useState } from 'react'
import type { PageResponse } from '../../../types/common'
import { getApiErrorMessage } from '../../../utils/apiError'
import { searchHotels } from '../services/hotelService'
import type { HotelCatalog, HotelSearchParams } from '../types'

type UseHotelSearchResult = {
  data: PageResponse<HotelCatalog> | null
  loading: boolean
  error: string | null
  refetch: () => Promise<void>
}

type UseHotelSearchOptions = {
  enabled?: boolean
}

export function useHotelSearch(params: HotelSearchParams, options: UseHotelSearchOptions = {}): UseHotelSearchResult {
  const enabled = options.enabled ?? true
  const [data, setData] = useState<PageResponse<HotelCatalog> | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const requestIdRef = useRef(0)
  const paramsRef = useRef(params)
  paramsRef.current = params

  // Use JSON.stringify as a stable dependency key — prevents infinite loops caused by object
  // reference inequality when the caller passes a structurally-identical params object.
  const paramsKey = JSON.stringify(params)

  const fetchData = useCallback(async () => {
    if (!enabled) {
      return
    }

    const requestId = requestIdRef.current + 1
    requestIdRef.current = requestId

    setLoading(true)
    setError(null)

    try {
      const response = await searchHotels(paramsRef.current)
      if (requestId !== requestIdRef.current) {
        return
      }
      setData(response)
    } catch (fetchError) {
      if (requestId !== requestIdRef.current) {
        return
      }
      if (fetchError instanceof Error) {
        console.error(fetchError)
      }
      setError(getApiErrorMessage(fetchError, 'Không thể tải danh sách khách sạn lúc này. Vui lòng thử lại.'))
    } finally {
      if (requestId === requestIdRef.current) {
        setLoading(false)
      }
    }
  }, [enabled])

  useEffect(() => {
    if (!enabled) {
      return
    }

    void fetchData()
  }, [enabled, fetchData, paramsKey])

  return {
    data,
    loading,
    error,
    refetch: fetchData,
  }
}
