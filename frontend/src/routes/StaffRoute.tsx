import { RoleRoute } from './RoleRoute'
import type { RoleGuardRouteProps } from './routeGuards'

const STAFF_ROLES = ['DOI_TAC'] as const

export function StaffRoute({
  children,
  redirectTo = '/login',
  unauthorizedRedirectTo = '/403',
}: RoleGuardRouteProps) {
  return (
    <RoleRoute
      allowedRoles={STAFF_ROLES}
      redirectTo={redirectTo}
      unauthorizedRedirectTo={unauthorizedRedirectTo}
    >
      {children}
    </RoleRoute>
  )
}
