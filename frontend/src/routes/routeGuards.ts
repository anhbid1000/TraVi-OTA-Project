import type { ReactNode } from 'react';
import type { AuthUser } from '../types/auth';

export type RoleName = 'QUAN_TRI_VIEN' | 'DOI_TAC' | 'KHACH_HANG';

export type GuardRouteProps = {
  children?: ReactNode;
  redirectTo?: string;
};

export type RoleGuardRouteProps = GuardRouteProps & {
  unauthorizedRedirectTo?: string;
};

export type RoleRouteProps = RoleGuardRouteProps & {
  allowedRoles: readonly RoleName[];
};

export const normalizeRole = (role?: string) =>
  role
    ?.replace(/^ROLE[_-]/, '')
    .trim()
    .toUpperCase();

export const getUserRole = (user: AuthUser | null) => {
  if (!user) {
    return undefined
  }

  return typeof user.role === 'string' && user.role.trim()
    ? user.role
    : typeof user.vaiTro === 'string'
      ? user.vaiTro
      : undefined
}

export const hasAllowedRole = (user: AuthUser | null, allowedRoles: readonly RoleName[]) => {
  const currentRole = normalizeRole(getUserRole(user));

  if (!currentRole) {
    return false;
  }

  return allowedRoles.some((role) => normalizeRole(role) === currentRole);
};

export const getDefaultPathByRole = (role?: string) => {
  const normalizedRole = normalizeRole(role);

  switch (normalizedRole) {
    case 'QUAN_TRI_VIEN':
      return '/admin';
    case 'DOI_TAC':
      return '/partner';
    case 'KHACH_HANG':
      return '/';
    default:
      return '/';
  }
};
