import { useCallback, useEffect, useRef, useState } from 'react'
import { getWeatherForecastRange } from '../services/weatherService'
import type { WeatherForecastRange, WeatherForecastRangeParams } from '../types'

type UseWeatherForecastRangeResult = {
  data: WeatherForecastRange | null
  loading: boolean
  error: string | null
  refetch: () => Promise<void>
}

export function useWeatherForecastRange(params: WeatherForecastRangeParams): UseWeatherForecastRangeResult {
  const [data, setData] = useState<WeatherForecastRange | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const requestIdRef = useRef(0)
  const paramsRef = useRef(params)
  paramsRef.current = params

  const paramsKey = JSON.stringify(params)

  const fetchData = useCallback(async () => {
    const { latitude, longitude, startDate, endDate } = paramsRef.current
    if (typeof latitude !== 'number' || typeof longitude !== 'number' || !startDate || !endDate) {
      setData(null)
      setError(null)
      return
    }

    const requestId = requestIdRef.current + 1
    requestIdRef.current = requestId

    setLoading(true)
    setError(null)

    try {
      const response = await getWeatherForecastRange(paramsRef.current)
      if (requestId !== requestIdRef.current) {
        return
      }
      setData(response)
    } catch (fetchError) {
      if (requestId !== requestIdRef.current) {
        return
      }
      setError(fetchError instanceof Error ? fetchError.message : 'Khong the tai du bao thoi tiet.')
      setData(null)
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
