import type { AuthResponse, AuthUser } from '../types/auth'

const ACCESS_TOKEN_KEY = 'travi_access_token'
const REFRESH_TOKEN_KEY = 'travi_refresh_token'
const TOKEN_TYPE_KEY = 'travi_token_type'

const decodeBase64Url = (value: string) => {
  const base64 = value.replace(/-/g, '+').replace(/_/g, '/')
  const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=')
  return atob(padded)
}

export const tokenStorage = {
  getAccessToken() {
    return localStorage.getItem(ACCESS_TOKEN_KEY)
  },

  getRefreshToken() {
    return localStorage.getItem(REFRESH_TOKEN_KEY)
  },

  getTokenType() {
    return localStorage.getItem(TOKEN_TYPE_KEY) ?? 'Bearer'
  },

  setTokens(auth: AuthResponse) {
    localStorage.setItem(ACCESS_TOKEN_KEY, auth.token)
    localStorage.setItem(REFRESH_TOKEN_KEY, auth.refreshToken)
    localStorage.setItem(TOKEN_TYPE_KEY, auth.type || 'Bearer')
  },

  clearTokens() {
    localStorage.removeItem(ACCESS_TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    localStorage.removeItem(TOKEN_TYPE_KEY)
  },

  getUserFromToken(): AuthUser | null {
    const token = this.getAccessToken()

    if (!token) {
      return null
    }

    try {
      const [, payload] = token.split('.')
      if (!payload) {
        return null
      }

      return JSON.parse(decodeBase64Url(payload)) as AuthUser
    } catch {
      return null
    }
  },

  isAccessTokenExpired() {
    const user = this.getUserFromToken()

    if (!user?.exp) {
      return true
    }

    return user.exp * 1000 <= Date.now()
  },
}
