import axios, {
  AxiosError,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
} from 'axios'
import type { AuthResponse } from '../types/auth'
import { tokenStorage } from './tokenStorage'

type RetriableRequestConfig = InternalAxiosRequestConfig & {
  _retry?: boolean
}

const rawBaseUrl =
  import.meta.env.VITE_API_BASE_URL ??
  import.meta.env.VITE_API_URL ??
  'http://localhost:8080/api'

const normalizedBaseUrl = rawBaseUrl.replace(/\/$/, '')
export const API_BASE_URL = normalizedBaseUrl.endsWith('/api')
  ? normalizedBaseUrl
  : `${normalizedBaseUrl}/api`

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

let refreshPromise: Promise<AuthResponse> | null = null

const refreshAccessToken = async () => {
  const refreshToken = tokenStorage.getRefreshToken()

  if (!refreshToken) {
    throw new Error('Missing refresh token')
  }

  if (!refreshPromise) {
    refreshPromise = axios
      .post<AuthResponse>(`${API_BASE_URL}/v1/auth/refresh`, { refreshToken })
      .then((response) => {
        tokenStorage.setTokens(response.data)
        window.dispatchEvent(new Event('auth:refresh'))
        return response.data
      })
      .finally(() => {
        refreshPromise = null
      })
  }

  return refreshPromise
}

api.interceptors.request.use((config) => {
  const token = tokenStorage.getAccessToken()

  if (token) {
    config.headers.Authorization = `${tokenStorage.getTokenType()} ${token}`
  }

  return config
})

api.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as RetriableRequestConfig | undefined
    const status = error.response?.status
    const requestUrl = originalRequest?.url ?? ''
    const isAuthRequest = requestUrl.includes('/auth/login') || requestUrl.includes('/auth/refresh')

    if (status !== 401 || !originalRequest || originalRequest._retry || isAuthRequest) {
      return Promise.reject(error)
    }

    originalRequest._retry = true

    try {
      const auth = await refreshAccessToken()
      originalRequest.headers.Authorization = `${auth.type || 'Bearer'} ${auth.token}`
      return api(originalRequest)
    } catch (refreshError) {
      tokenStorage.clearTokens()
      window.dispatchEvent(new Event('auth:logout'))
      return Promise.reject(refreshError)
    }
  },
)
