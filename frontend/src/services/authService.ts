import { api } from './api'
import { tokenStorage } from './tokenStorage'
import type {
  AuthResponse,
  LoginRequest,
  RefreshTokenRequest,
  RegisterRequest,
  VerifyEmailRequest,
} from '../types/auth'

const AUTH_ENDPOINT = '/v1/auth'

export const authService = {
  async login(payload: LoginRequest) {
    const { data } = await api.post<AuthResponse>(`${AUTH_ENDPOINT}/login`, payload)
    tokenStorage.setTokens(data)
    return data
  },

  async logout(): Promise<void> {
    const refreshToken = tokenStorage.getRefreshToken()
    const payload: RefreshTokenRequest | undefined = refreshToken ? { refreshToken } : undefined

    try {
      await api.post(`${AUTH_ENDPOINT}/logout`, payload)
    } finally {
      tokenStorage.clearTokens()
    }
  },

  async refreshToken() {
    const refreshToken = tokenStorage.getRefreshToken()

    if (!refreshToken) {
      throw new Error('Missing refresh token')
    }

    const { data } = await api.post<AuthResponse>(`${AUTH_ENDPOINT}/refresh`, {
      refreshToken,
    })
    tokenStorage.setTokens(data)
    return data
  },

  async register(payload: RegisterRequest) {
    const { data } = await api.post<string>(`${AUTH_ENDPOINT}/register`, payload)
    return data
  },

  async verifyEmail(payload: VerifyEmailRequest) {
    const { data } = await api.put<string>(`${AUTH_ENDPOINT}/verify-email`, payload)
    return data
  },
}
