import { RoleRoute } from './RoleRoute'
import type { RoleGuardRouteProps } from './routeGuards'

const ADMIN_ROLES = ['QUAN_TRI_VIEN'] as const

export function AdminRoute({
  children,
  redirectTo = '/login',
  unauthorizedRedirectTo = '/403',
}: RoleGuardRouteProps) {
  return (
    <RoleRoute
      allowedRoles={ADMIN_ROLES}
      redirectTo={redirectTo}
      unauthorizedRedirectTo={unauthorizedRedirectTo}
    >
      {children}
    </RoleRoute>
  )
}
