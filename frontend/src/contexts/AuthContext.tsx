import {
  useCallback,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { authService } from '../services/authService'
import { tokenStorage } from '../services/tokenStorage'
import { AuthContext, type AuthContextValue } from './authContext'
import type { AuthUser, GoogleAuthRequest, LoginRequest } from '../types/auth'

type AuthProviderProps = {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<AuthUser | null>(() => tokenStorage.getUserFromToken())
  const [accessToken, setAccessToken] = useState<string | null>(() => tokenStorage.getAccessToken())
  const [refreshToken, setRefreshToken] = useState<string | null>(() => tokenStorage.getRefreshToken())
  const [isLoading, setIsLoading] = useState(true)

  const syncSessionFromStorage = useCallback(() => {
    setUser(tokenStorage.getUserFromToken())
    setAccessToken(tokenStorage.getAccessToken())
    setRefreshToken(tokenStorage.getRefreshToken())
  }, [])

  const clearSession = useCallback(() => {
    tokenStorage.clearTokens()
    setUser(null)
    setAccessToken(null)
    setRefreshToken(null)
  }, [])

  const login = useCallback(
    async (payload: LoginRequest) => {
      setIsLoading(true)

      try {
        const auth = await authService.login(payload)
        syncSessionFromStorage()
        return auth
      } finally {
        setIsLoading(false)
      }
    },
    [syncSessionFromStorage],
  )

  const loginWithGoogle = useCallback(
    async (payload: GoogleAuthRequest) => {
      setIsLoading(true)

      try {
        const auth = await authService.loginWithGoogle(payload)
        syncSessionFromStorage()
        return auth
      } finally {
        setIsLoading(false)
      }
    },
    [syncSessionFromStorage],
  )

  const logout = useCallback(async () => {
    setIsLoading(true)

    try {
      await authService.logout()
    } finally {
      clearSession()
      setIsLoading(false)
    }
  }, [clearSession])

  const refreshSession = useCallback(async () => {
    const auth = await authService.refreshToken()
    syncSessionFromStorage()
    return auth
  }, [syncSessionFromStorage])

  useEffect(() => {
    const handleForcedLogout = () => {
      clearSession()
      setIsLoading(false)
    }

    const handleSessionRefresh = () => {
      syncSessionFromStorage()
      setIsLoading(false)
    }

    window.addEventListener('auth:logout', handleForcedLogout)
    window.addEventListener('auth:refresh', handleSessionRefresh)

    return () => {
      window.removeEventListener('auth:logout', handleForcedLogout)
      window.removeEventListener('auth:refresh', handleSessionRefresh)
    }
  }, [clearSession, syncSessionFromStorage])

  // Bootstrap phiên đăng nhập khi app mount:
  // - Nếu có access token còn hạn -> dùng luôn
  // - Nếu access token hết hạn nhưng còn refresh token -> refresh trước khi render protected routes
  // - Nếu không có token -> clear session
  useEffect(() => {
    const bootstrapAuth = async () => {
      const currentAccessToken = tokenStorage.getAccessToken()
      const currentRefreshToken = tokenStorage.getRefreshToken()

      if (!currentAccessToken) {
        clearSession()
        setIsLoading(false)
        return
      }

      if (!tokenStorage.isAccessTokenExpired()) {
        syncSessionFromStorage()
        setIsLoading(false)
        return
      }

      if (!currentRefreshToken) {
        clearSession()
        setIsLoading(false)
        return
      }

      try {
        await refreshSession()
      } catch {
        clearSession()
      } finally {
        setIsLoading(false)
      }
    }

    void bootstrapAuth()
  }, [clearSession, refreshSession, syncSessionFromStorage])

  // Runtime refresh nếu access token bị hết hạn trong khi đang sử dụng app
  useEffect(() => {
    if (!accessToken || !tokenStorage.isAccessTokenExpired() || !refreshToken) {
      return
    }

    void refreshSession().catch(() => {
      clearSession()
    })
  }, [accessToken, clearSession, refreshSession, refreshToken])

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      accessToken,
      refreshToken,
      isAuthenticated: Boolean(accessToken && !tokenStorage.isAccessTokenExpired()),
      isLoading,
      login,
      loginWithGoogle,
      logout,
      refreshSession,
    }),
    [accessToken, isLoading, login, loginWithGoogle, logout, refreshSession, refreshToken, user],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
