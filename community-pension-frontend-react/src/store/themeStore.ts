import { create } from 'zustand'
import { persist } from 'zustand/middleware'

/**
 * Theme Mode Types
 */
export type ThemeMode = 'light' | 'dark' | 'auto'

/**
 * Theme Store State Interface
 */
interface ThemeState {
  mode: ThemeMode
  isDark: boolean
  
  // Actions
  setMode: (mode: ThemeMode) => void
  toggleTheme: () => void
  initTheme: () => void
}

/**
 * Theme Store
 * 
 * Supports:
 * - Light/Dark mode
 * - Auto mode (follows system preference)
 * - Persistent storage
 */
export const useThemeStore = create<ThemeState>()(
  persist(
    (set, get) => ({
      mode: 'light',
      isDark: false,

      setMode: (mode) => {
        const isDark = mode === 'dark' || 
          (mode === 'auto' && window.matchMedia('(prefers-color-scheme: dark)').matches)
        
        set({ mode, isDark })
        
        // Update document class for CSS variables
        if (isDark) {
          document.documentElement.classList.add('dark')
        } else {
          document.documentElement.classList.remove('dark')
        }
      },

      toggleTheme: () => {
        const { mode } = get()
        const newMode = mode === 'light' ? 'dark' : 'light'
        get().setMode(newMode)
      },

      initTheme: () => {
        const { mode } = get()
        get().setMode(mode)
        
        // Listen to system theme changes when in auto mode
        if (mode === 'auto') {
          const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
          mediaQuery.addEventListener('change', (e) => {
            const { mode: currentMode } = get()
            if (currentMode === 'auto') {
              set({ isDark: e.matches })
              if (e.matches) {
                document.documentElement.classList.add('dark')
              } else {
                document.documentElement.classList.remove('dark')
              }
            }
          })
        }
      },
    }),
    {
      name: 'theme-storage',
    }
  )
)
