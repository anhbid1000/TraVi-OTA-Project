export type WeatherForecast = {
  date: string
  temperatureC: number
  condition: string
  icon?: string
  warningMessage?: string
}

export type WeatherForecastParams = {
  latitude?: number
  longitude?: number
  date?: string
}

export type WeatherForecastSummary = {
  averageTemperature: number
  condition: string
  icon?: string
  message?: string
}

export type WeatherForecastRange = {
  forecasts: WeatherForecast[]
  summary: WeatherForecastSummary
}

export type WeatherForecastRangeParams = {
  latitude?: number
  longitude?: number
  startDate?: string
  endDate?: string
}
