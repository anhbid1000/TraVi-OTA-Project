import { api } from './api'
import { tokenStorage } from './tokenStorage'
import type {
  AuthResponse,
  ForgotPasswordRequest,
  GoogleAuthRequest,
  LoginRequest,
  RefreshTokenRequest,
  RegisterRequest,
  ResetPasswordRequest,
  ResendOtpRequest,
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

  async loginWithGoogle(payload: GoogleAuthRequest) {
    const { data } = await api.post<AuthResponse>(`${AUTH_ENDPOINT}/google`, payload)
    tokenStorage.setTokens(data)
    return data
  },

  async verifyEmail(payload: VerifyEmailRequest) {
    const { data } = await api.put<string>(`${AUTH_ENDPOINT}/verify-email`, payload)
    return data
  },

  async resendOtp(payload: ResendOtpRequest) {
    const { data } = await api.post<string>(`${AUTH_ENDPOINT}/resend-otp`, payload)
    return data
  },

  async requestPasswordResetOtp(payload: ForgotPasswordRequest) {
    const { data } = await api.post<string>(`${AUTH_ENDPOINT}/forgot-password/request-otp`, payload)
    return data
  },

  async verifyPasswordResetOtp(payload: VerifyEmailRequest) {
    const { data } = await api.post<string>(`${AUTH_ENDPOINT}/forgot-password/verify-otp`, payload)
    return data
  },

  async resetPassword(payload: ResetPasswordRequest) {
    const { data } = await api.post<string>(`${AUTH_ENDPOINT}/forgot-password/reset`, payload)
    return data
  },
}
