import { useCallback, useEffect, useRef, useState } from 'react'
import { getWeatherForecast } from '../services/weatherService'
import type { WeatherForecast, WeatherForecastParams } from '../types'

type UseWeatherForecastResult = {
  data: WeatherForecast | null
  loading: boolean
  error: string | null
  refetch: () => Promise<void>
}

export function useWeatherForecast(params: WeatherForecastParams): UseWeatherForecastResult {
  const [data, setData] = useState<WeatherForecast | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const requestIdRef = useRef(0)
  const paramsRef = useRef(params)
  paramsRef.current = params

  // Use JSON.stringify as a stable dependency key — prevents infinite loops caused by object
  // reference inequality when the caller passes a structurally-identical params object.
  const paramsKey = JSON.stringify(params)

  const fetchData = useCallback(async () => {
    const { latitude, longitude, date } = paramsRef.current
    if (typeof latitude !== 'number' || typeof longitude !== 'number' || !date) {
      setData(null)
      setError(null)
      return
    }

    const requestId = requestIdRef.current + 1
    requestIdRef.current = requestId

    setLoading(true)
    setError(null)

    try {
      const response = await getWeatherForecast(paramsRef.current)
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

