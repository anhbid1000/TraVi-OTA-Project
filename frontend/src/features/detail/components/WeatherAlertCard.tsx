import { Cloud, CloudLightning, CloudRain, Sun } from 'lucide-react'
import type { LucideIcon } from 'lucide-react'
import { useMemo } from 'react'
import { useWeatherForecastRange } from '../../weather/hooks/useWeatherForecastRange'
import { normalizeVietnameseText } from '../../../utils/display'

type WeatherAlertCardProps = {
  latitude?: number
  longitude?: number
  date?: string
  endDate?: string
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
  if (normalized.includes('dong') || normalized.includes('lightning') || normalized.includes('storm')) {
    return {
      icon: CloudLightning,
      badge: condition,
      temperatureC,
      message,
    }
  }

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

function formatWeatherDate(date: string) {
  const parsedDate = new Date(`${date}T00:00:00`)
  if (Number.isNaN(parsedDate.getTime())) {
    return date
  }
  return parsedDate.toLocaleDateString('vi-VN', {
    weekday: 'short',
    day: '2-digit',
    month: '2-digit',
  })
}

export function WeatherAlertCard({ latitude, longitude, date, endDate, locationName }: WeatherAlertCardProps) {
  const weatherParams = useMemo(
    () => ({ latitude, longitude, startDate: date, endDate: endDate || date }),
    [latitude, longitude, date, endDate],
  )
  const { data, error } = useWeatherForecastRange(weatherParams)

  if (!date || typeof latitude !== 'number' || typeof longitude !== 'number' || error || !data) {
    return null
  }

  const summary = toWeatherData(
    normalizeVietnameseText(data.summary.condition),
    data.summary.averageTemperature,
    normalizeVietnameseText(data.summary.message) || 'Thời tiết ổn định.',
  )

  const SummaryIcon = summary.icon

  return (
    <article className="space-y-3 rounded-2xl border border-outline-variant/30 bg-white p-4 shadow-sm">
      <div className="flex items-center gap-3">
        <div className="rounded-xl bg-mint-green p-2 text-primary">
          <SummaryIcon size={20} />
        </div>
        <div>
          <p className="text-sm font-semibold text-primary">
            Dự báo thời tiết {locationName ? `tại ${locationName}` : ''}
          </p>
          <p className="text-xs text-on-surface-variant">
            Trung bình {summary.temperatureC}°C trong lịch trình
          </p>
        </div>
      </div>

      <div className="grid gap-2">
        {data.forecasts.map((forecast) => {
          const weather = toWeatherData(
            normalizeVietnameseText(forecast.condition),
            forecast.temperatureC,
            normalizeVietnameseText(forecast.warningMessage) || 'Thời tiết ổn định.',
          )
          const Icon = weather.icon

          return (
            <div key={forecast.date} className="flex items-start gap-3 rounded-lg bg-surface-container/60 px-3 py-2">
              <Icon className="mt-0.5 shrink-0 text-primary" size={18} />
              <div className="min-w-0 flex-1">
                <div className="flex flex-wrap items-center gap-2">
                  <span className="text-xs font-semibold text-on-surface">{formatWeatherDate(forecast.date)}</span>
                  <span className="rounded bg-error-container px-2 py-0.5 text-[10px] font-bold uppercase text-on-error-container">
                    {weather.badge}
                  </span>
                  <span className="text-xs font-semibold text-primary">{weather.temperatureC}°C</span>
                </div>
                <p className="mt-1 text-xs text-on-surface-variant">{weather.message}</p>
              </div>
            </div>
          )
        })}
      </div>

      <div className="border-t border-outline-variant/30 pt-3">
        <p className="text-xs font-semibold uppercase text-primary">Tổng quát</p>
        <p className="mt-1 text-xs text-on-surface-variant">{summary.message}</p>
      </div>
    </article>
  )
}
