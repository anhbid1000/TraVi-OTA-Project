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

