import { useEffect } from 'react'
import { RouterProvider } from 'react-router-dom'
import { ConfigProvider, App as AntdApp, theme } from 'antd'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import zhCN from 'antd/locale/zh_CN'
import enUS from 'antd/locale/en_US'
import 'dayjs/locale/zh-cn'
import router from './router'
import { useThemeStore } from './store/themeStore'
import { lightTheme, darkTheme } from './config/antd-theme'
import './i18n'
import './styles/global.css'

// React Query Client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
})

/**
 * Root App Component
 * 
 * Integrations:
 * - Ant Design ConfigProvider (theme, locale)
 * - React Router 7
 * - React Query (TanStack Query)
 * - Zustand State Management
 * - i18next Internationalization
 */
function App() {
  const { isDark, initTheme } = useThemeStore()
  const locale = localStorage.getItem('language') === 'en-US' ? enUS : zhCN

  // Initialize theme on mount
  useEffect(() => {
    initTheme()
  }, [initTheme])

  return (
    <QueryClientProvider client={queryClient}>
      <ConfigProvider
        locale={locale}
        theme={{
          ...isDark ? darkTheme : lightTheme,
          algorithm: isDark ? theme.darkAlgorithm : theme.defaultAlgorithm,
          cssVar: { key: 'app' }, // Enable CSS Variables for dynamic theme switching
        }}
      >
        <AntdApp>
          <RouterProvider router={router} />
        </AntdApp>
      </ConfigProvider>
    </QueryClientProvider>
  )
}

export default App
