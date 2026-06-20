import { useState, useEffect, useCallback } from 'react'
import { Badge, Popover, Tabs, Button, List, Empty, message } from 'antd'
import { BellOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useNotifications } from '@/hooks/useNotifications'
import { formatDateTime } from '@/utils/format'

/**
 * Notification Center Component
 * 
 * Features:
 * - Real-time notification display with custom hook
 * - Tab-based filtering (All / Unread)
 * - Mark as read functionality
 * - Navigate to detail page
 * - Auto-refresh capability
 * - Responsive design
 * - Elegant interaction patterns
 */
const NotificationCenter: React.FC = () => {
  const navigate = useNavigate()
  const [activeTab, setActiveTab] = useState('all')
  const [visible, setVisible] = useState(false)
  
  const {
    notifications,
    unreadCount,
    markAsRead,
    markAllAsRead,
    fetchNotifications,
  } = useNotifications()

  const unreadNotifications = notifications.filter((n) => !n.isRead)

  // Handle notification click
  const handleNotificationClick = useCallback(
    async (noticeId: number, isRead: boolean) => {
      if (!isRead) {
        await markAsRead(noticeId)
      }
      setVisible(false)
      navigate(`/front/notice/${noticeId}`)
    },
    [markAsRead, navigate]
  )

  // Handle mark all as read
  const handleMarkAllAsRead = useCallback(() => {
    markAllAsRead()
    message.success('全部标记为已读')
  }, [markAllAsRead])

  // Handle view all
  const handleViewAll = useCallback(() => {
    setVisible(false)
    navigate('/front/notice')
  }, [navigate])

  // Auto-refresh when popover opens
  useEffect(() => {
    if (visible) {
      fetchNotifications()
    }
  }, [visible, fetchNotifications])

  // Render notification list
  const renderNotificationList = useCallback(
    (data: typeof notifications) => (
      <List
        dataSource={data}
        locale={{ emptyText: <Empty description="暂无通知" image={Empty.PRESENTED_IMAGE_SIMPLE} /> }}
        renderItem={(item) => (
          <List.Item
            onClick={() => handleNotificationClick(item.noticeId, !!item.isRead)}
            style={{
              cursor: 'pointer',
              backgroundColor: item.isRead ? '#fff' : '#f0f9ff',
              transition: 'background-color 0.3s',
              padding: '12px 16px',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = '#f5f7fa'
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = item.isRead ? '#fff' : '#f0f9ff'
            }}
          >
            <List.Item.Meta
              title={
                <div style={{ fontWeight: item.isRead ? 400 : 600, color: '#303133' }}>
                  {item.title}
                </div>
              }
              description={
                <>
                  <div
                    style={{
                      fontSize: '13px',
                      color: '#606266',
                      marginBottom: '4px',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap',
                      maxWidth: '100%',
                    }}
                  >
                    {item.content}
                  </div>
                  <div style={{ fontSize: '12px', color: '#909399' }}>
                    {formatDateTime(item.publishTime)}
                  </div>
                </>
              }
            />
          </List.Item>
        )}
        style={{ maxHeight: '400px', overflow: 'auto' }}
      />
    ),
    [handleNotificationClick]
  )

  // Popover content
  const content = (
    <div style={{ width: 400 }}>
      {/* Header */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          padding: '12px 16px',
          borderBottom: '1px solid #EBEEF5',
          fontWeight: 500,
        }}
      >
        <span>通知中心</span>
        <Button type="link" size="small" onClick={handleMarkAllAsRead} disabled={unreadCount === 0}>
          全部已读
        </Button>
      </div>

      {/* Tabs */}
      <Tabs
        activeKey={activeTab}
        onChange={setActiveTab}
        items={[
          {
            key: 'all',
            label: `全部 (${notifications.length})`,
            children: renderNotificationList(notifications),
          },
          {
            key: 'unread',
            label: `未读 (${unreadCount})`,
            children: renderNotificationList(unreadNotifications),
          },
        ]}
        style={{ marginTop: 0 }}
        tabBarStyle={{ marginBottom: 0, paddingLeft: '16px', paddingRight: '16px' }}
      />

      {/* Footer */}
      <div
        style={{
          padding: '10px 16px',
          textAlign: 'center',
          borderTop: '1px solid #EBEEF5',
        }}
      >
        <Button type="link" onClick={handleViewAll}>
          查看全部通知
        </Button>
      </div>
    </div>
  )

  return (
    <Popover
      content={content}
      trigger="click"
      open={visible}
      onOpenChange={setVisible}
      placement="bottomRight"
      overlayInnerStyle={{ padding: 0 }}
    >
      <Badge count={unreadCount} offset={[-5, 5]} overflowCount={99}>
        <BellOutlined
          style={{
            fontSize: '20px',
            cursor: 'pointer',
            color: '#606266',
            transition: 'color 0.3s',
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.color = '#1890ff'
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.color = '#606266'
          }}
        />
      </Badge>
    </Popover>
  )
}

export default NotificationCenter
