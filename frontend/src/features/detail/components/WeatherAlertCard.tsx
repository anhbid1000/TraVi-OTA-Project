import { Cloud, CloudRain, Sun } from 'lucide-react'
import type { LucideIcon } from 'lucide-react'
import { useMemo } from 'react'
import { useWeatherForecast } from '../../weather/hooks/useWeatherForecast'
import { normalizeVietnameseText } from '../../../utils/display'

type WeatherAlertCardProps = {
  latitude?: number
  longitude?: number
  date?: string
  locationName?: string
}

type WeatherData = {
  icon: LucideIcon
  badge: string
  temperatureC: number
  message: string
}

function toWeatherData(condition: string, temperatureC: number, message: string): WeatherData {
  const normalized = condition.toLowerCase()
  if (normalized.includes('mua') || normalized.includes('rain')) {
    return {
      icon: CloudRain,
      badge: condition,
      temperatureC,
      message,
    }
  }

  if (normalized.includes('may') || normalized.includes('cloud')) {
    return {
      icon: Cloud,
      badge: condition,
      temperatureC,
      message,
    }
  }

  return {
    icon: Sun,
    badge: condition,
    temperatureC,
    message,
  }
}

export function WeatherAlertCard({ latitude, longitude, date, locationName }: WeatherAlertCardProps) {
  const weatherParams = useMemo(
    () => ({ latitude, longitude, date }),
    [latitude, longitude, date],
  )
  const { data, error } = useWeatherForecast(weatherParams)

  if (!date || typeof latitude !== 'number' || typeof longitude !== 'number' || error || !data) {
    return null
  }

  const weather = toWeatherData(
    normalizeVietnameseText(data.condition),
    data.temperatureC,
    normalizeVietnameseText(data.warningMessage) || 'Thời tiết ổn định.',
  )

  const Icon = weather.icon

  return (
    <article className="flex items-center gap-3 rounded-2xl border border-outline-variant/30 bg-white p-4 shadow-sm">
      <div className="rounded-xl bg-mint-green p-2 text-primary">
        <Icon size={20} />
      </div>
      <div>
        <span className="mb-1 inline-block rounded bg-error-container px-2 py-0.5 text-[10px] font-bold uppercase text-on-error-container">
          {weather.badge}
        </span>
        <p className="text-sm font-semibold text-primary">
          {weather.temperatureC}°C {locationName ? `tại ${locationName}` : ''}
        </p>
        <p className="text-xs text-on-surface-variant">{weather.message}</p>
      </div>
    </article>
  )
}
