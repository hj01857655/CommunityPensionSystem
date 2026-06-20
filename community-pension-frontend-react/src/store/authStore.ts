import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { User } from '@/types/user'

/**
 * Auth Store State Interface
 */
interface AuthState {
  // Access Token (stored in memory only)
  accessToken: string | null
  // Refresh Token (stored in HttpOnly Cookie by backend)
  isAuthenticated: boolean
  userInfo: User | null
  
  // Actions
  setAccessToken: (token: string | null) => void
  setUserInfo: (user: User | null) => void
  login: (accessToken: string, userInfo: User) => void
  logout: () => void
  clearAuth: () => void
}

/**
 * Auth Store
 * 
 * Security Implementation:
 * - Access Token: Stored in memory (this store), NOT in localStorage/sessionStorage
 * - Refresh Token: Stored in HttpOnly Cookie (handled by backend), NOT accessible to JS
 * - Token Rotation: Implemented in axios interceptor
 * - Multi-Tenant Isolation: Portal-specific token validation
 */
export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      isAuthenticated: false,
      userInfo: null,

      setAccessToken: (token) => set({ 
        accessToken: token,
        isAuthenticated: !!token 
      }),

      setUserInfo: (user) => set({ userInfo: user }),

      login: (accessToken, userInfo) => set({
        accessToken,
        userInfo,
        isAuthenticated: true,
      }),

      logout: () => {
        set({
          accessToken: null,
          userInfo: null,
          isAuthenticated: false,
        })
      },

      clearAuth: () => {
        set({
          accessToken: null,
          userInfo: null,
          isAuthenticated: false,
        })
      },
    }),
    {
      name: 'auth-storage',
      // Only persist userInfo, NOT tokens (security best practice)
      partialize: (state) => ({
        userInfo: state.userInfo,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
)
