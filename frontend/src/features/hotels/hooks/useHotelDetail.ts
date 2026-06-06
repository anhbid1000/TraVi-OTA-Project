import { useCallback, useEffect, useRef, useState } from 'react'
import { getHotelDetail } from '../services/hotelService'
import type { HotelDetail, HotelDetailParams } from '../types'

type UseHotelDetailResult = {
  data: HotelDetail | null
  loading: boolean
  error: string | null
  refetch: () => Promise<void>
}

export function useHotelDetail(id: string | undefined, params: HotelDetailParams): UseHotelDetailResult {
  const [data, setData] = useState<HotelDetail | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const requestIdRef = useRef(0)
  const paramsRef = useRef(params)
  paramsRef.current = params

  // Use JSON.stringify as a stable dependency key — prevents infinite loops caused by object
  // reference inequality when the caller passes a structurally-identical params object.
  const paramsKey = JSON.stringify(params)

  const fetchData = useCallback(async () => {
    if (!id) {
      setData(null)
      setError('Thiếu mã khách sạn.')
      return
    }

    const requestId = requestIdRef.current + 1
    requestIdRef.current = requestId

    setLoading(true)
    setError(null)

    try {
      const response = await getHotelDetail(id, paramsRef.current)
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
      setError('Không thể tải chi tiết khách sạn lúc này. Vui lòng thử lại.')
    } finally {
      if (requestId === requestIdRef.current) {
        setLoading(false)
      }
    }
  }, [id])

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

