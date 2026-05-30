import { AxiosError } from 'axios'
import { api } from '../../../services/api'
import type { ApiErrorResponse } from '../../../types/common'
import type {
  WeatherForecast,
  WeatherForecastParams,
  WeatherForecastRange,
  WeatherForecastRangeParams,
} from '../types'

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

function addDays(date: string, days: number) {
  const nextDate = new Date(`${date}T00:00:00`)
  nextDate.setDate(nextDate.getDate() + days)
  const year = nextDate.getFullYear()
  const month = String(nextDate.getMonth() + 1).padStart(2, '0')
  const day = String(nextDate.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

function getMockWeatherRange(params: WeatherForecastRangeParams): WeatherForecastRange {
  const startDate = params.startDate ?? new Date().toISOString().slice(0, 10)
  const endDate = params.endDate ?? startDate
  const start = new Date(`${startDate}T00:00:00`)
  const end = new Date(`${endDate}T00:00:00`)
  const days = Math.max(1, Math.round((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1)
  const forecasts = Array.from({ length: days }, (_, index) => getMockWeather({
    latitude: params.latitude,
    longitude: params.longitude,
    date: addDays(startDate, index),
  }))
  const rainyDays = forecasts.filter((forecast) => forecast.condition.toLowerCase().includes('mưa')).length
  const averageTemperature = forecasts.reduce((sum, forecast) => sum + forecast.temperatureC, 0) / forecasts.length

  return {
    forecasts,
    summary: {
      averageTemperature: Math.round(averageTemperature * 10) / 10,
      condition: rainyDays > 0 ? 'Có mưa rào' : 'Thời tiết ổn định',
      icon: rainyDays > 0 ? 'cloud-rain' : 'sun',
      message: rainyDays > 0
        ? `Lịch trình có ${rainyDays} ngày có khả năng mưa.`
        : 'Thời tiết nhìn chung ổn định cho lịch trình.',
    },
  }
}

type BackendWeatherForecast = Omit<WeatherForecast, 'temperatureC'> & {
  temperature?: number
  temperatureC?: number
}

type BackendWeatherForecastSummary = {
  averageTemperature?: number
  condition: string
  icon?: string
  message?: string
}

type BackendWeatherForecastRange = {
  forecasts: BackendWeatherForecast[]
  summary: BackendWeatherForecastSummary
}

function mapWeatherForecast(payload: BackendWeatherForecast): WeatherForecast {
  return {
    date: payload.date,
    temperatureC: payload.temperatureC ?? payload.temperature ?? 0,
    condition: payload.condition,
    icon: payload.icon,
    warningMessage: payload.warningMessage,
  }
}

function mapWeatherForecastRange(payload: BackendWeatherForecastRange): WeatherForecastRange {
  return {
    forecasts: payload.forecasts.map(mapWeatherForecast),
    summary: {
      averageTemperature: payload.summary.averageTemperature ?? 0,
      condition: payload.summary.condition,
      icon: payload.summary.icon,
      message: payload.summary.message,
    },
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
    const response = await api.get<BackendWeatherForecast>('/v1/public/weather/forecast', {
      params: {
        lat: params.latitude,
        lng: params.longitude,
        date: params.date,
      },
    })

    return mapWeatherForecast(response.data)
  } catch (error) {
    if (ENABLE_MOCK_FALLBACK) {
      return getMockWeather(params)
    }
    throw toApiError(error)
  }
}

export async function getWeatherForecastRange(params: WeatherForecastRangeParams): Promise<WeatherForecastRange> {
  if (
    typeof params.latitude !== 'number' ||
    typeof params.longitude !== 'number' ||
    !params.startDate ||
    !params.endDate
  ) {
    throw new Error('Latitude, longitude, startDate and endDate are required')
  }

  try {
    const response = await api.get<BackendWeatherForecastRange>('/v1/public/weather/forecast/range', {
      params: {
        lat: params.latitude,
        lng: params.longitude,
        startDate: params.startDate,
        endDate: params.endDate,
      },
    })

    return mapWeatherForecastRange(response.data)
  } catch (error) {
    if (ENABLE_MOCK_FALLBACK) {
      return getMockWeatherRange(params)
    }
    throw toApiError(error)
  }
}
