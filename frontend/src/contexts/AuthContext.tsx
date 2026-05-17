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
import type { AuthUser, LoginRequest } from '../types/auth'

type AuthProviderProps = {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<AuthUser | null>(() => tokenStorage.getUserFromToken())
  const [accessToken, setAccessToken] = useState<string | null>(() => tokenStorage.getAccessToken())
  const [refreshToken, setRefreshToken] = useState<string | null>(() => tokenStorage.getRefreshToken())
  const [isLoading, setIsLoading] = useState(false)

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
    }

    const handleSessionRefresh = () => {
      syncSessionFromStorage()
    }

    window.addEventListener('auth:logout', handleForcedLogout)
    window.addEventListener('auth:refresh', handleSessionRefresh)

    return () => {
      window.removeEventListener('auth:logout', handleForcedLogout)
      window.removeEventListener('auth:refresh', handleSessionRefresh)
    }
  }, [clearSession, syncSessionFromStorage])

  useEffect(() => {
    if (!accessToken || !tokenStorage.isAccessTokenExpired()) {
      return
    }

    void refreshSession().catch(clearSession)
  }, [accessToken, clearSession, refreshSession])

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      accessToken,
      refreshToken,
      isAuthenticated: Boolean(accessToken && !tokenStorage.isAccessTokenExpired()),
      isLoading,
      login,
      logout,
      refreshSession,
    }),
    [accessToken, isLoading, login, logout, refreshSession, refreshToken, user],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
