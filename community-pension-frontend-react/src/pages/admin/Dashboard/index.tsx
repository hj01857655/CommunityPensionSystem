import { Card, Row, Col, Statistic, Typography } from 'antd'
import { UserOutlined, TeamOutlined, RiseOutlined } from '@ant-design/icons'

const { Title } = Typography

/**
 * Admin Portal Dashboard Page
 */
const AdminDashboard: React.FC = () => {
  return (
    <div>
      <Title level={2}>Dashboard</Title>
      
      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="Total Users"
              value={1128}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="Active Staff"
              value={48}
              prefix={<TeamOutlined />}
            />
          </Card>
        </Col>
        
        <Col xs={24} sm={12} lg={8}>
          <Card>
            <Statistic
              title="Growth Rate"
              value={12.5}
              prefix={<RiseOutlined />}
              suffix="%"
            />
          </Card>
        </Col>
      </Row>

      <Card title="Recent Activities" style={{ marginTop: 24 }}>
        Dashboard content coming soon...
      </Card>
    </div>
  )
}

export default AdminDashboard
