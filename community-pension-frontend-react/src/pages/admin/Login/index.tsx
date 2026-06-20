import { Form, Input, Button, Card, message, Select, Checkbox } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { useAuthStore } from '@/store/authStore'
import { authApi } from '@/api'
import type { User } from '@/types/user'

const ROLE_OPTIONS = [
  { label: '社区工作人员', value: 3 },
  { label: '管理员', value: 4 },
]

/**
 * Admin Portal Login Page
 * 
 * Improvements over Vue version:
 * - Removed complex role mapping logic
 * - Simplified token management (handled by axios interceptor)
 * - Better error handling
 * - Cleaner code structure
 */
const AdminLogin: React.FC = () => {
  const navigate = useNavigate()
  const { login } = useAuthStore()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [rememberMe, setRememberMe] = useState(true)

  const handleLogin = async (values: { username: string; password: string; roleId: number }) => {
    setLoading(true)
    try {
      const response = await authApi.adminLogin({
        username: values.username,
        password: values.password,
        roleId: values.roleId,
        portal: 'admin',
      })

      if (response.code === 200 && response.data) {
        // Store auth info
        login(response.data.accessToken, response.data.user as User)

        // Remember credentials if checked
        if (rememberMe) {
          sessionStorage.setItem('rememberedUsername', values.username)
          sessionStorage.setItem('rememberedRoleId', String(values.roleId))
        } else {
          sessionStorage.removeItem('rememberedUsername')
          sessionStorage.removeItem('rememberedRoleId')
        }

        message.success('登录成功')
        navigate('/admin')
      }
    } catch (error: any) {
      message.error(error?.message || '登录失败，请检查用户名和密码')
    } finally {
      setLoading(false)
    }
  }

  const handleForgotPassword = () => {
    message.info('请联系系统管理员重置密码')
  }

  // Load remembered credentials on mount
  useState(() => {
    const rememberedUsername = sessionStorage.getItem('rememberedUsername')
    const rememberedRoleId = sessionStorage.getItem('rememberedRoleId')
    
    if (rememberedUsername) {
      form.setFieldsValue({ 
        username: rememberedUsername,
        roleId: rememberedRoleId ? Number(rememberedRoleId) : 4,
      })
    }
  })

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #1e3c72 0%, #2a5298 100%)',
      }}
    >
      <Card
        title={
          <div style={{ textAlign: 'center' }}>
            <div style={{ fontSize: '28px', fontWeight: 600, marginBottom: '8px' }}>
              社区养老系统
            </div>
            <div style={{ fontSize: '16px', fontWeight: 400, color: '#666' }}>
              管理后台
            </div>
          </div>
        }
        style={{ width: 450, boxShadow: '0 8px 24px rgba(0,0,0,0.15)' }}
        headStyle={{ borderBottom: 'none', paddingBottom: 0 }}
      >
        <div style={{ textAlign: 'center', fontSize: '22px', fontWeight: 600, marginBottom: '24px' }}>
          后台管理登录
        </div>

        <Form
          form={form}
          onFinish={handleLogin}
          layout="vertical"
          size="large"
          initialValues={{ roleId: 4 }}
        >
          <Form.Item
            name="username"
            label="用户名"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="请输入用户名" />
          </Form.Item>

          <Form.Item
            name="password"
            label="密码"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="请输入密码" />
          </Form.Item>

          <Form.Item
            name="roleId"
            label="角色"
            rules={[{ required: true, message: '请选择角色' }]}
          >
            <Select placeholder="请选择角色" options={ROLE_OPTIONS} />
          </Form.Item>

          <Form.Item>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Checkbox checked={rememberMe} onChange={(e) => setRememberMe(e.target.checked)}>
                记住我
              </Checkbox>
              <a onClick={handleForgotPassword} style={{ color: '#1890ff' }}>
                忘记密码？
              </a>
            </div>
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading} block>
              {loading ? '登录中...' : '登录'}
            </Button>
          </Form.Item>
        </Form>

        <div style={{ marginTop: '20px', textAlign: 'center', color: '#666', fontSize: '14px' }}>
          © {new Date().getFullYear()} 社区养老系统 - 版权所有
        </div>
      </Card>
    </div>
  )
}

export default AdminLogin
