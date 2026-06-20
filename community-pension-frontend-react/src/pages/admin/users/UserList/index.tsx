import { Card, Table, Typography } from 'antd'
import type { ColumnsType } from 'antd/es/table'

const { Title } = Typography

interface UserData {
  key: string
  name: string
  username: string
  role: string
  status: string
}

/**
 * Admin Portal User List Page
 */
const AdminUserList: React.FC = () => {
  const columns: ColumnsType<UserData> = [
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Username',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: 'Role',
      dataIndex: 'role',
      key: 'role',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
    },
  ]

  const data: UserData[] = []

  return (
    <div>
      <Title level={2}>User Management</Title>
      
      <Card style={{ marginTop: 24 }}>
        <Table columns={columns} dataSource={data} />
      </Card>
    </div>
  )
}

export default AdminUserList
