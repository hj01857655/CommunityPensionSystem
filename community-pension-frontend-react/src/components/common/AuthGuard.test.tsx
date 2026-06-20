import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import AuthGuard from './AuthGuard'
import { useAuthStore } from '@/store/authStore'

vi.mock('@/store/authStore')

describe('AuthGuard', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('redirects to login when not authenticated', () => {
    vi.mocked(useAuthStore).mockReturnValue({
      isAuthenticated: false,
      userInfo: null,
      accessToken: null,
      setAccessToken: vi.fn(),
      setUserInfo: vi.fn(),
      login: vi.fn(),
      logout: vi.fn(),
      clearAuth: vi.fn(),
    })

    render(
      <BrowserRouter>
        <AuthGuard>
          <div>Protected Content</div>
        </AuthGuard>
      </BrowserRouter>
    )

    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument()
  })

  it('renders children when authenticated with correct role', () => {
    vi.mocked(useAuthStore).mockReturnValue({
      isAuthenticated: true,
      userInfo: { id: '1', username: 'admin', name: 'Admin', role: 'admin' },
      accessToken: 'token',
      setAccessToken: vi.fn(),
      setUserInfo: vi.fn(),
      login: vi.fn(),
      logout: vi.fn(),
      clearAuth: vi.fn(),
    })

    render(
      <BrowserRouter>
        <AuthGuard allowedRoles={['admin']}>
          <div>Protected Content</div>
        </AuthGuard>
      </BrowserRouter>
    )

    expect(screen.getByText('Protected Content')).toBeInTheDocument()
  })
})
