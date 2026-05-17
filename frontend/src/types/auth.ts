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

export type AccountType = 'DOI_TAC' | 'KHACH_HANG'

export type RegisterRequest = {
  username: string
  email: string
  hoTen: string
  soDienThoai: string
  matKhau: string
  loaiTaiKhoan: AccountType
}

export type VerifyEmailRequest = {
  email: string
  confirmOTP: string
}

export type ResendOtpRequest = {
  email: string
}
