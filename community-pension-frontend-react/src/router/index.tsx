import { createBrowserRouter, Navigate } from 'react-router-dom'
import { lazy, Suspense } from 'react'
import { Spin } from 'antd'

// Layouts
import FrontLayout from '@/layouts/FrontLayout'
import AdminLayout from '@/layouts/AdminLayout'

// Auth Guard
import AuthGuard from '@/components/common/AuthGuard'

/**
 * Loading Component
 */
const PageLoading = () => (
  <div style={{ 
    display: 'flex', 
    justifyContent: 'center', 
    alignItems: 'center', 
    height: '100vh' 
  }}>
    <Spin size="large" />
  </div>
)

/**
 * Lazy Load Wrapper
 */
const lazyLoad = (Component: React.LazyExoticComponent<React.FC>) => (
  <Suspense fallback={<PageLoading />}>
    <Component />
  </Suspense>
)

// ==================== Auth Pages ====================
const FrontLogin = lazy(() => import('@/pages/front/Login'))
const AdminLogin = lazy(() => import('@/pages/admin/Login'))

// ==================== Front Portal Pages ====================
const FrontHome = lazy(() => import('@/pages/front/Home'))
const FrontProfile = lazy(() => import('@/pages/front/Profile'))

// ==================== Admin Portal Pages ====================
const AdminDashboard = lazy(() => import('@/pages/admin/Dashboard'))
const AdminUserList = lazy(() => import('@/pages/admin/users/UserList'))

/**
 * Router Configuration
 * 
 * Structure:
 * - /front: User-facing portal (elder, kin roles)
 * - /admin: Admin portal (admin, staff roles)
 * - Protected routes use AuthGuard
 * - Multi-tenant token isolation
 */
const router = createBrowserRouter([
  // Root redirect
  {
    path: '/',
    element: <Navigate to="/front" replace />,
  },

  // ==================== Front Portal ====================
  {
    path: '/front/login',
    element: lazyLoad(FrontLogin),
  },
  {
    path: '/front',
    element: (
      <AuthGuard allowedRoles={['elder', 'kin']}>
        <FrontLayout />
      </AuthGuard>
    ),
    children: [
      {
        index: true,
        element: lazyLoad(FrontHome),
      },
      {
        path: 'profile',
        element: lazyLoad(FrontProfile),
      },
    ],
  },

  // ==================== Admin Portal ====================
  {
    path: '/admin/login',
    element: lazyLoad(AdminLogin),
  },
  {
    path: '/admin',
    element: (
      <AuthGuard allowedRoles={['admin', 'staff']}>
        <AdminLayout />
      </AuthGuard>
    ),
    children: [
      {
        index: true,
        element: <Navigate to="/admin/dashboard" replace />,
      },
      {
        path: 'dashboard',
        element: lazyLoad(AdminDashboard),
      },
      {
        path: 'users',
        element: lazyLoad(AdminUserList),
      },
    ],
  },

  // ==================== 404 ====================
  {
    path: '*',
    element: <div>404 Not Found</div>,
  },
])

export default router
