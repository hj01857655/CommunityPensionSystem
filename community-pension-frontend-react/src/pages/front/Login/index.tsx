import { Form, Input, Button, Card, message, Select, Checkbox } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { useAuthStore } from '@/store/authStore'
import { authApi } from '@/api'
import type { User } from '@/types/user'

const ROLE_OPTIONS = [
  { label: '老人', value: 1 },
  { label: '老人家属', value: 2 },
]

/**
 * Front Portal Login Page
 * 
 * Improvements over Vue version:
 * - Removed unnecessary setTimeout delays
 * - Simplified state management
 * - Better type safety with enums
 * - Cleaner form validation
 */
const FrontLogin: React.FC = () => {
  const navigate = useNavigate()
  const { login } = useAuthStore()
  const [form] = Form.useForm()
  const [loading, setLoading] = useState(false)
  const [rememberMe, setRememberMe] = useState(false)

  const handleLogin = async (values: { username: string; password: string; roleId: number }) => {
    setLoading(true)
    try {
      const response = await authApi.login({
        username: values.username,
        password: values.password,
        roleId: values.roleId,
        portal: 'front',
      })

      if (response.code === 200 && response.data) {
        // Store auth info
        login(response.data.accessToken, response.data.user as User)

        // Remember username if checked
        if (rememberMe) {
          localStorage.setItem('rememberedUsername', values.username)
          localStorage.setItem('rememberedRoleId', String(values.roleId))
        } else {
          localStorage.removeItem('rememberedUsername')
          localStorage.removeItem('rememberedRoleId')
        }

        message.success('登录成功')
        navigate('/front')
      }
    } catch (error: any) {
      message.error(error?.message || '登录失败，请检查用户名和密码')
    } finally {
      setLoading(false)
    }
  }

  const handleForgotPassword = () => {
    message.warning('请联系管理员重置密码')
  }

  // Load remembered credentials on mount
  useState(() => {
    const rememberedUsername = localStorage.getItem('rememberedUsername')
    const rememberedRoleId = localStorage.getItem('rememberedRoleId')
    
    if (rememberedUsername) {
      form.setFieldsValue({ 
        username: rememberedUsername,
        roleId: rememberedRoleId ? Number(rememberedRoleId) : 1,
      })
      setRememberMe(true)
    }
  })

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      }}
    >
      <Card
        title="社区养老系统登录"
        style={{ width: 420, boxShadow: '0 8px 24px rgba(0,0,0,0.15)' }}
        headStyle={{ textAlign: 'center', fontSize: '24px', fontWeight: 600 }}
      >
        <Form
          form={form}
          onFinish={handleLogin}
          layout="vertical"
          size="large"
          initialValues={{ roleId: 1 }}
        >
          <Form.Item
            name="roleId"
            label="角色"
            rules={[{ required: true, message: '请选择角色' }]}
          >
            <Select placeholder="请选择角色" options={ROLE_OPTIONS} />
          </Form.Item>

          <Form.Item
            name="username"
            label="用户名"
            rules={[
              { required: true, message: '请输入用户名/手机号/身份证号' },
              { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符' },
            ]}
          >
            <Input prefix={<UserOutlined />} placeholder="请输入用户名/手机号/身份证号" />
          </Form.Item>

          <Form.Item
            name="password"
            label="密码"
            rules={[
              { required: true, message: '请输入密码' },
              { min: 6, max: 20, message: '密码长度在 6 到 20 个字符' },
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="请输入密码" />
          </Form.Item>

          <Form.Item>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Checkbox checked={rememberMe} onChange={(e) => setRememberMe(e.target.checked)}>
                记住密码
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
      </Card>
    </div>
  )
}

export default FrontLogin
