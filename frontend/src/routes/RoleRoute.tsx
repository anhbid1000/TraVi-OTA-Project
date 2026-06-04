import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { hasAllowedRole, type RoleRouteProps } from './routeGuards'

export function RoleRoute({
  allowedRoles,
  children,
  redirectTo = '/login',
  unauthorizedRedirectTo = '/403',
}: RoleRouteProps) {
  const { user, isAuthenticated, isLoading } = useAuth()
  const location = useLocation()

  if (isLoading) {
    return null
  }

  if (!isAuthenticated) {
    return <Navigate to={redirectTo} replace state={{ from: location }} />
  }

  if (!hasAllowedRole(user, allowedRoles)) {
    return <Navigate to={unauthorizedRedirectTo} replace />
  }

  return children ?? <Outlet />
}
