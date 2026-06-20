import { Navigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'
import { UserRole } from '@/types/enums'

/**
 * Auth Guard Props
 */
interface AuthGuardProps {
  children: React.ReactNode
  allowedRoles?: string[]
}

/**
 * Auth Guard Component
 * 
 * Features:
 * - Protected route authentication
 * - Role-based access control (RBAC)
 * - Multi-tenant portal isolation
 * - Automatic redirect to login
 */
const AuthGuard: React.FC<AuthGuardProps> = ({ children, allowedRoles }) => {
  const { isAuthenticated, userInfo } = useAuthStore()
  const location = useLocation()

  // Check authentication
  if (!isAuthenticated || !userInfo) {
    // Determine which login page to redirect to based on current path
    const loginPath = location.pathname.startsWith('/admin') 
      ? '/admin/login' 
      : '/front/login'
    
    return <Navigate to={loginPath} state={{ from: location }} replace />
  }

  // Check role-based access
  if (allowedRoles && allowedRoles.length > 0) {
    const userRoleId = userInfo.roleId
    const roleMap: Record<number, string> = {
      1: UserRole.Elder,
      2: UserRole.Kin,
      3: UserRole.Staff,
      4: UserRole.Admin,
    }
    
    const userRole = roleMap[userRoleId]
    
    if (!userRole || !allowedRoles.includes(userRole)) {
      // Redirect to appropriate portal based on user role
      const redirectPath = [3, 4].includes(userRoleId) ? '/admin' : '/front'
      return <Navigate to={redirectPath} replace />
    }
  }

  return <>{children}</>
}

export default AuthGuard
