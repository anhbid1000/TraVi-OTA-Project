import { useCallback, useEffect, useRef, useState } from 'react'
import type { PageResponse } from '../../../types/common'
import { getApiErrorMessage } from '../../../utils/apiError'
import { searchRestaurants } from '../services/restaurantService'
import type { RestaurantCatalog, RestaurantSearchParams } from '../types'

type UseRestaurantSearchResult = {
  data: PageResponse<RestaurantCatalog> | null
  loading: boolean
  error: string | null
  refetch: () => Promise<void>
}

export function useRestaurantSearch(params: RestaurantSearchParams): UseRestaurantSearchResult {
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
  }, [])

  useEffect(() => {
    void fetchData()
  }, [fetchData, paramsKey])

  return {
    data,
    loading,
    error,
    refetch: fetchData,
  }
}

