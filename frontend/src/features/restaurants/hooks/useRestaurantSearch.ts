import { useCallback, useEffect, useRef, useState } from 'react'
import type { PageResponse } from '../../../types/common'
import { sendAiUserEvent } from '../../ai/services/aiService'
import { tokenStorage } from '../../../services/tokenStorage'
import { getApiErrorMessage } from '../../../utils/apiError'
import { searchRestaurants } from '../services/restaurantService'
import type { RestaurantCatalog, RestaurantSearchParams } from '../types'

type UseRestaurantSearchResult = {
  data: PageResponse<RestaurantCatalog> | null
  loading: boolean
  error: string | null
  refetch: () => Promise<void>
}

type UseRestaurantSearchOptions = {
  enabled?: boolean
}

export function useRestaurantSearch(
  params: RestaurantSearchParams,
  options: UseRestaurantSearchOptions = {},
): UseRestaurantSearchResult {
  const enabled = options.enabled ?? true
  const [data, setData] = useState<PageResponse<RestaurantCatalog> | null>(null)
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
      const response = await searchRestaurants(paramsRef.current)
      if (requestId !== requestIdRef.current) {
        return
      }
      setData(response)
      if (tokenStorage.getAccessToken()) {
        void sendAiUserEvent({
          eventType: 'SEARCH',
          assetType: 'RESTAURANT',
          keyword: paramsRef.current.keyword || paramsRef.current.city || 'restaurant-search',
          city: paramsRef.current.city,
          source: 'RESTAURANT_CATALOG',
          metadata: JSON.stringify({
            guests: paramsRef.current.guests,
            date: paramsRef.current.date,
            time: paramsRef.current.time,
            resultCount: response.totalElements,
          }),
        }).catch(() => {
          // AI tracking la best-effort, khong chan luong tim kiem catalog.
        })
      }
    } catch (fetchError) {
      if (requestId !== requestIdRef.current) {
        return
      }
      if (fetchError instanceof Error) {
        console.error(fetchError)
      }
      setError(getApiErrorMessage(fetchError, 'Không thể tải danh sách nhà hàng lúc này. Vui lòng thử lại.'))
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
