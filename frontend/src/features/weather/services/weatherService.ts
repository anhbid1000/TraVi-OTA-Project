import { AxiosError } from 'axios'
import { api } from '../../../services/api'
import type { ApiErrorResponse } from '../../../types/common'
import type { WeatherForecast, WeatherForecastParams } from '../types'

const ENABLE_MOCK_FALLBACK = import.meta.env.VITE_ENABLE_CATALOG_MOCK_FALLBACK !== 'false'

function toApiError(error: unknown): Error {
  if (error instanceof AxiosError) {
    const payload = error.response?.data as ApiErrorResponse | undefined
    return new Error(payload?.message ?? error.message)
  }
  if (error instanceof Error) {
    return error
  }
  return new Error('Unknown error')
}

function getMockWeather(params: WeatherForecastParams): WeatherForecast {
  const date = params.date ?? new Date().toISOString().slice(0, 10)
  const latitude = params.latitude ?? 0
  const longitude = params.longitude ?? 0
  const seed = Math.abs(Math.round(latitude * 10 + longitude * 10 + Number(date.slice(-2)))) % 3

  if (seed === 0) {
    return {
      date,
      temperatureC: 24,
      condition: 'Mưa nhẹ',
      warningMessage: 'Ưu tiên hoạt động trong nhà trong ngày này.',
    }
  }

  if (seed === 1) {
    return {
      date,
      temperatureC: 27,
      condition: 'Nhiều mây',
      warningMessage: 'Thời tiết ổn định, mang theo áo khoác mỏng.',
    }
  }

  return {
    date,
    temperatureC: 31,
    condition: 'Nắng đẹp',
    warningMessage: 'Thích hợp cho hoạt động ngoài trời.',
  }
}

export async function getWeatherForecast(params: WeatherForecastParams): Promise<WeatherForecast> {
  if (
    typeof params.latitude !== 'number' ||
    typeof params.longitude !== 'number' ||
    !params.date
  ) {
    throw new Error('Latitude, longitude and date are required')
  }

  try {
    const response = await api.get<WeatherForecast>('/v1/public/weather/forecast', {
      params: {
        lat: params.latitude,
        lng: params.longitude,
        date: params.date,
      },
    })

    return response.data
  } catch (error) {
    if (ENABLE_MOCK_FALLBACK) {
      return getMockWeather(params)
    }
    throw toApiError(error)
  }
}


