export type AuthUser = {
  username?: string
  email?: string
  role?: string
  exp?: number
  iat?: number
  [key: string]: unknown
}

export type LoginRequest = {
  email: string
  matKhau: string
  nhoMatKhau?: boolean
}

export type AuthResponse = {
  token: string
  refreshToken: string
  type: string
  message: string
}

export type RefreshTokenRequest = {
  refreshToken: string
}

export type RegisterRequest = {
  email: string
  matKhau: string
  [key: string]: unknown
}

export type VerifyEmailRequest = {
  email: string
  confirmOTP: string
}
