import { Layout, Menu, Dropdown, Avatar, Button } from 'antd'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import {
  UserOutlined,
  LogoutOutlined,
  SettingOutlined,
  HomeOutlined,
  HeartOutlined,
  CalendarOutlined,
  CustomerServiceOutlined,
} from '@ant-design/icons'
import { useAuthStore } from '@/store/authStore'
import { useThemeStore } from '@/store/themeStore'
import { authApi } from '@/api'
import NotificationCenter from '@/components/common/NotificationCenter'
import type { MenuProps } from 'antd'

const { Header, Content, Footer } = Layout

/**
 * Front Portal Layout
 * 
 * For elder and kin roles
 * Features: Simple navigation, large fonts, high contrast (accessibility)
 */
const FrontLayout: React.FC = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const { userInfo, logout } = useAuthStore()
  const { toggleTheme, isDark } = useThemeStore()

  // Handle logout
  const handleLogout = async () => {
    try {
      await authApi.logout()
      logout()
      navigate('/front/login')
    } catch (error) {
      console.error('Logout failed:', error)
      logout()
      navigate('/front/login')
    }
  }

  // User dropdown menu
  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人资料',
      onClick: () => navigate('/front/profile'),
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '设置',
      onClick: () => navigate('/front/settings'),
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ]

  // Main navigation menu
  const navMenuItems: MenuProps['items'] = [
    {
      key: '/front',
      icon: <HomeOutlined />,
      label: '首页',
    },
    {
      key: '/front/service',
      icon: <CustomerServiceOutlined />,
      label: '服务预约',
    },
    {
      key: '/front/health',
      icon: <HeartOutlined />,
      label: '健康中心',
    },
    {
      key: '/front/activity',
      icon: <CalendarOutlined />,
      label: '社区活动',
    },
  ]

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          padding: '0 24px',
          background: isDark ? '#001529' : '#fff',
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        }}
      >
        {/* Logo */}
        <div
          style={{
            fontSize: '24px',
            fontWeight: 'bold',
            color: isDark ? '#fff' : '#1890ff',
            cursor: 'pointer',
          }}
          onClick={() => navigate('/front')}
        >
          社区养老系统
        </div>

        {/* Navigation Menu */}
        <Menu
          mode="horizontal"
          selectedKeys={[location.pathname]}
          items={navMenuItems}
          onClick={({ key }) => navigate(key)}
          style={{ flex: 1, minWidth: 0, marginLeft: '24px', border: 'none' }}
        />

        {/* User Actions */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '24px' }}>
          {/* Notification Center */}
          <NotificationCenter />

          {/* Theme Toggle */}
          <Button onClick={toggleTheme} type="text" style={{ fontSize: '20px' }}>
            {isDark ? '🌞' : '🌙'}
          </Button>

          {/* User Dropdown */}
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
                cursor: 'pointer',
              }}
            >
              <Avatar src={userInfo?.avatar} icon={<UserOutlined />} size={40} />
              <span style={{ color: isDark ? '#fff' : '#000', fontWeight: 500 }}>
                {userInfo?.name}
              </span>
            </div>
          </Dropdown>
        </div>
      </Header>

      <Content
        style={{
          padding: '24px',
          background: isDark ? '#141414' : '#f0f2f5',
          minHeight: 'calc(100vh - 134px)',
        }}
      >
        <div
          style={{
            background: isDark ? '#1f1f1f' : '#fff',
            padding: '24px',
            borderRadius: '8px',
            minHeight: '100%',
          }}
        >
          <Outlet />
        </div>
      </Content>

      <Footer
        style={{
          textAlign: 'center',
          background: isDark ? '#001529' : '#fff',
          color: isDark ? '#fff' : '#000',
        }}
      >
        社区养老系统 ©{new Date().getFullYear()} - 联系我们：1234567890
      </Footer>
    </Layout>
  )
}

export default FrontLayout
