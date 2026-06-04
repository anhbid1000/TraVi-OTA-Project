import { createContext } from 'react'
import type { AuthResponse, AuthUser, GoogleAuthRequest, LoginRequest } from '../types/auth'

export type AuthContextValue = {
  user: AuthUser | null
  accessToken: string | null
  refreshToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (payload: LoginRequest) => Promise<AuthResponse>
  loginWithGoogle: (payload: GoogleAuthRequest) => Promise<AuthResponse>
  logout: () => Promise<void>
  refreshSession: () => Promise<AuthResponse>
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined)
