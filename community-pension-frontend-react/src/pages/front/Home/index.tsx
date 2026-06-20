import { useState, useEffect, useCallback } from 'react'
import {
  Card,
  Row,
  Col,
  Statistic,
  Typography,
  Button,
  Space,
  Tag,
  Empty,
  Skeleton,
  Modal,
  Form,
  Input,
  message,
  Tooltip,
  notification,
} from 'antd'
import {
  HeartOutlined,
  CalendarOutlined,
  CustomerServiceOutlined,
  NotificationOutlined,
  PhoneOutlined,
  ReloadOutlined,
  WarningOutlined,
  ClockCircleOutlined,
  EnvironmentOutlined,
  CloudOutlined,
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'
import { useUserStatistics } from '@/hooks/useUserStatistics'
import { healthApi } from '@/api/health'
import { serviceApi } from '@/api/service'
import { activityApi } from '@/api/activity'
import { noticeApi } from '@/api/notice'
import { emergencyApi } from '@/api/emergency'
import { formatDateTime, formatDate } from '@/utils/format'
import { ServiceOrderStatusMapper, ActivityStatusMapper } from '@/utils/statusMapper'
import type { HealthRecord } from '@/types/health'
import type { ServiceOrder } from '@/types/service'
import type { Activity } from '@/types/activity'
import type { Notice } from '@/types/notice'

const { Title, Paragraph, Text } = Typography

/**
 * Front Portal Home Page - Dashboard
 * 
 * Complete feature-rich implementation with:
 * - Real-time health monitoring with abnormal value detection
 * - Service appointments with upcoming reminders
 * - Recent activities with status and registration
 * - Notice board with quick view
 * - Weather information
 * - Emergency call dialog with location and message
 * - Auto-refresh every 5 minutes
 * - Elegant error handling and loading states
 */
const FrontHome: React.FC = () => {
  const navigate = useNavigate()
  const { userInfo } = useAuthStore()
  const { statistics, loading: statsLoading, refresh: refreshStats } = useUserStatistics()

  // State management
  const [healthData, setHealthData] = useState<HealthRecord | null>(null)
  const [healthLoading, setHealthLoading] = useState(false)
  const [services, setServices] = useState<ServiceOrder[]>([])
  const [servicesLoading, setServicesLoading] = useState(false)
  const [activities, setActivities] = useState<Activity[]>([])
  const [activitiesLoading, setActivitiesLoading] = useState(false)
  const [notices, setNotices] = useState<Notice[]>([])
  const [weatherData] = useState({
    temperature: 25,
    weather: '晴',
    humidity: 65,
  })
  const [emergencyDialogVisible, setEmergencyDialogVisible] = useState(false)
  const [emergencyLoading, setEmergencyLoading] = useState(false)
  const [emergencyForm] = Form.useForm()

  // Health value ranges and labels
  const healthRanges: Record<string, { min: number | string; max: number | string; unit: string }> = {
    bloodPressure: { min: '90/60', max: '140/90', unit: 'mmHg' },
    heartRate: { min: 60, max: 100, unit: '次/分' },
    bloodSugar: { min: 3.9, max: 6.1, unit: 'mmol/L' },
    temperature: { min: 36.3, max: 37.2, unit: '℃' },
    height: { min: 0, max: 0, unit: 'cm' },
    weight: { min: 0, max: 0, unit: 'kg' },
  }

  const healthLabels: Record<string, string> = {
    bloodPressure: '血压',
    heartRate: '心率',
    bloodSugar: '血糖',
    temperature: '体温',
    height: '身高',
    weight: '体重',
  }

  // Check if health value is abnormal
  const isAbnormalValue = (key: string, value: string | number): boolean => {
    if (!value || !healthRanges[key]) return false

    if (key === 'bloodPressure' && typeof value === 'string' && value.includes('/')) {
      const [systolic, diastolic] = value.split('/').map(v => parseInt(v.trim(), 10))
      const [minSys, minDia] = (healthRanges[key].min as string).split('/').map(v => parseInt(v.trim(), 10))
      const [maxSys, maxDia] = (healthRanges[key].max as string).split('/').map(v => parseInt(v.trim(), 10))
      return systolic < minSys || systolic > maxSys || diastolic < minDia || diastolic > maxDia
    }

    if (key === 'height' || key === 'weight') return false

    const numValue = parseFloat(String(value))
    const min = parseFloat(String(healthRanges[key].min))
    const max = parseFloat(String(healthRanges[key].max))
    return numValue < min || numValue > max
  }

  // Format health value display
  const formatHealthValue = (key: string, value: string | number): string => {
    if (value === null || value === undefined) return '暂无数据'
    return `${value} ${healthRanges[key]?.unit || ''}`
  }

  // Check if service is upcoming (within 24 hours)
  const isUpcomingService = (scheduleTime: string): boolean => {
    if (!scheduleTime) return false
    const now = new Date()
    const serviceTime = new Date(scheduleTime)
    const hoursDiff = (serviceTime.getTime() - now.getTime()) / (1000 * 60 * 60)
    return hoursDiff > 0 && hoursDiff <= 24
  }

  // Get service time display
  const getServiceTimeDisplay = (scheduleTime: string): string => {
    if (!scheduleTime) return '时间未定'
    const now = new Date()
    const serviceTime = new Date(scheduleTime)
    const hoursDiff = (serviceTime.getTime() - now.getTime()) / (1000 * 60 * 60)
    const daysDiff = Math.floor(hoursDiff / 24)

    if (hoursDiff < 0) {
      return `已过期 (${formatDate(scheduleTime)})`
    } else if (hoursDiff < 1) {
      return '即将开始 (不到1小时)'
    } else if (hoursDiff < 24) {
      return `即将开始 (${Math.floor(hoursDiff)}小时后)`
    } else {
      return `${daysDiff}天后 (${formatDate(scheduleTime)})`
    }
  }

  // Check if activity is coming soon (within 48 hours)
  const isActivityComingSoon = (startTime: string): boolean => {
    if (!startTime) return false
    const now = new Date()
    const activityTime = new Date(startTime)
    const hoursDiff = (activityTime.getTime() - now.getTime()) / (1000 * 60 * 60)
    return hoursDiff > 0 && hoursDiff <= 48
  }

  // Fetch health data
  const fetchHealthData = useCallback(async () => {
    if (!userInfo?.userId) return
    setHealthLoading(true)
    try {
      const res = await healthApi.getHealthRecords(userInfo.userId)
      if (res.code === 200 && res.data && res.data.length > 0) {
        setHealthData(res.data[0])
      } else {
        setHealthData(null)
      }
    } catch (error) {
      console.error('获取健康数据失败:', error)
      setHealthData(null)
    } finally {
      setHealthLoading(false)
    }
  }, [userInfo?.userId])

  // Fetch service appointments
  const fetchServices = useCallback(async () => {
    if (!userInfo?.userId) return
    setServicesLoading(true)
    try {
      const res = await serviceApi.getMyAppointments({
        userId: userInfo.userId,
        pageNum: 1,
        pageSize: 10,
      })
      if (res.code === 200 && res.data) {
        const filteredServices = (Array.isArray(res.data) ? res.data : [])
          .filter((service: ServiceOrder) => service.status !== 4 && service.status !== 5) // Exclude cancelled and rejected
          .sort((a: ServiceOrder, b: ServiceOrder) => new Date(a.scheduleTime).getTime() - new Date(b.scheduleTime).getTime())
          .slice(0, 3)
        setServices(filteredServices)
      } else {
        setServices([])
      }
    } catch (error) {
      console.error('获取服务预约失败:', error)
      setServices([])
    } finally {
      setServicesLoading(false)
    }
  }, [userInfo?.userId])

  // Fetch activities
  const fetchActivities = useCallback(async () => {
    setActivitiesLoading(true)
    try {
      const res = await activityApi.getActivityList({
        pageNum: 1,
        pageSize: 5,
      })
      if (res.code === 200 && res.data) {
        setActivities(Array.isArray(res.data) ? res.data.slice(0, 3) : [])
      } else {
        setActivities([])
      }
    } catch (error) {
      console.error('获取活动列表失败:', error)
      setActivities([])
    } finally {
      setActivitiesLoading(false)
    }
  }, [])

  // Fetch notices
  const fetchNotices = useCallback(async () => {
    try {
      const res = await noticeApi.getNoticeList({
        pageNum: 1,
        pageSize: 3,
      })
      if (res.code === 200 && res.data?.records) {
        setNotices(res.data.records)
      } else {
        setNotices([])
      }
    } catch (error) {
      console.error('获取通知列表失败:', error)
      setNotices([])
    }
  }, [])

  // Handle service cancellation
  const handleCancelService = async (serviceId: number) => {
    Modal.confirm({
      title: '确认取消',
      content: '确定要取消这个服务预约吗？',
      okText: '确认',
      cancelText: '取消',
      onOk: async () => {
        try {
          const res = await serviceApi.cancelAppointment(serviceId)
          if (res.code === 200) {
            message.success('取消成功')
            fetchServices()
          } else {
            message.error(res.message || '取消失败')
          }
        } catch (error) {
          console.error('取消服务失败:', error)
          message.error('取消服务失败')
        }
      },
    })
  }

  // Handle emergency call
  const handleEmergencyCall = async () => {
    try {
      const values = await emergencyForm.validateFields()
      setEmergencyLoading(true)

      const res = await emergencyApi.sendEmergencyCall({
        userId: userInfo?.userId!,
        location: values.location || '未提供位置信息',
        message: values.message || '紧急求助',
      })

      if (res.code === 200) {
        setEmergencyDialogVisible(false)
        emergencyForm.resetFields()
        
        notification.success({
          message: '紧急呼叫已发送',
          description: '工作人员将尽快与您联系，请保持电话畅通',
          duration: 5,
        })

        // Ask if want to dial emergency number
        Modal.confirm({
          title: '紧急呼叫',
          content: '是否同时拨打紧急电话？',
          okText: '拨打',
          cancelText: '取消',
          onOk: () => {
            window.location.href = 'tel:120'
          },
        })
      } else {
        message.error(res.message || '紧急呼叫发送失败')
      }
    } catch (error) {
      console.error('紧急呼叫失败:', error)
      message.error('紧急呼叫发送失败，请直接拨打紧急电话')
    } finally {
      setEmergencyLoading(false)
    }
  }

  // Refresh all data
  const refreshAllData = useCallback(async () => {
    message.loading({ content: '数据刷新中...', key: 'refresh', duration: 0 })
    try {
      await Promise.allSettled([
        fetchHealthData(),
        fetchServices(),
        fetchActivities(),
        fetchNotices(),
        refreshStats(),
      ])
      message.success({ content: '刷新成功', key: 'refresh', duration: 2 })
    } catch (error) {
      message.error({ content: '刷新失败', key: 'refresh', duration: 2 })
    }
  }, [fetchHealthData, fetchServices, fetchActivities, fetchNotices, refreshStats])

  // Initial data fetch
  useEffect(() => {
    fetchHealthData()
    fetchServices()
    fetchActivities()
    fetchNotices()
  }, [fetchHealthData, fetchServices, fetchActivities, fetchNotices])

  // Auto refresh every 5 minutes
  useEffect(() => {
    const intervalId = setInterval(() => {
      refreshAllData()
    }, 5 * 60 * 1000)

    return () => clearInterval(intervalId)
  }, [refreshAllData])

  return (
    <div style={{ padding: '24px', backgroundColor: '#f0f2f5', minHeight: 'calc(100vh - 64px - 70px)' }}>
      {/* Welcome Banner */}
      <Card
        style={{
          marginBottom: '24px',
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          border: 'none',
        }}
        bodyStyle={{ padding: '32px' }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <Title level={2} style={{ color: '#fff', marginBottom: '8px' }}>
              欢迎回来，{userInfo?.name || '访客'}！
            </Title>
            <Paragraph style={{ color: '#fff', fontSize: '16px', marginBottom: 0 }}>
              祝您身体健康，生活愉快
            </Paragraph>
          </div>
          <Button
            type="primary"
            icon={<ReloadOutlined />}
            size="large"
            onClick={refreshAllData}
            style={{ backgroundColor: 'rgba(255,255,255,0.2)', borderColor: 'transparent' }}
          >
            刷新数据
          </Button>
        </div>
      </Card>

      {/* Emergency Call Button - Float at top */}
      <div style={{ marginBottom: '24px', textAlign: 'right' }}>
        <Button
          type="primary"
          danger
          size="large"
          icon={<PhoneOutlined />}
          onClick={() => setEmergencyDialogVisible(true)}
          style={{ boxShadow: '0 4px 12px rgba(255, 77, 79, 0.4)' }}
        >
          紧急呼叫
        </Button>
      </div>

      {/* Quick Stats */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Skeleton loading={statsLoading} active paragraph={{ rows: 1 }}>
              <Statistic
                title="本月服务"
                value={statistics.monthlyServices}
                suffix="次"
                valueStyle={{ color: '#1890ff' }}
              />
            </Skeleton>
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Skeleton loading={statsLoading} active paragraph={{ rows: 1 }}>
              <Statistic
                title="健康记录"
                value={statistics.healthRecords}
                suffix="条"
                valueStyle={{ color: '#52c41a' }}
              />
            </Skeleton>
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Skeleton loading={statsLoading} active paragraph={{ rows: 1 }}>
              <Statistic
                title="活动参与"
                value={statistics.activityParticipations}
                suffix="次"
                valueStyle={{ color: '#faad14' }}
              />
            </Skeleton>
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Skeleton loading={statsLoading} active paragraph={{ rows: 1 }}>
              <Statistic
                title="未读通知"
                value={statistics.unreadNotices}
                suffix="条"
                valueStyle={{ color: '#f5222d' }}
              />
            </Skeleton>
          </Card>
        </Col>
      </Row>

      {/* Main Content Grid */}
      <Row gutter={[16, 16]}>
        {/* Health Monitoring Card */}
        <Col xs={24} md={8}>
          <Card
            title={
              <Space>
                <HeartOutlined style={{ color: '#52c41a' }} />
                <span>健康监测</span>
              </Space>
            }
            extra={
              <Button type="link" onClick={() => navigate('/front/health')}>
                查看更多
              </Button>
            }
            style={{ height: '100%' }}
          >
            <Skeleton loading={healthLoading} active paragraph={{ rows: 4 }}>
              {healthData ? (
                <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                  {Object.entries(healthLabels).map(([key, label]) => {
                    const value = healthData[key as keyof HealthRecord]
                    const isAbnormal = isAbnormalValue(key, value as string | number)
                    return (
                      <div key={key} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <Text strong>{label}:</Text>
                        <Space>
                          <Text style={{ color: isAbnormal ? '#ff4d4f' : '#000' }}>
                            {formatHealthValue(key, value as string | number)}
                          </Text>
                          {isAbnormal && (
                            <Tooltip title={`正常范围: ${healthRanges[key].min}-${healthRanges[key].max}${healthRanges[key].unit}`}>
                              <WarningOutlined style={{ color: '#ff4d4f' }} />
                            </Tooltip>
                          )}
                        </Space>
                      </div>
                    )
                  })}
                  <Text type="secondary" style={{ fontSize: '12px' }}>
                    更新时间: {formatDateTime(healthData.recordTime)}
                  </Text>
                </Space>
              ) : (
                <Empty description="暂无健康数据" />
              )}
            </Skeleton>
          </Card>
        </Col>

        {/* Service Appointments Card */}
        <Col xs={24} md={8}>
          <Card
            title={
              <Space>
                <CustomerServiceOutlined style={{ color: '#1890ff' }} />
                <span>服务预约</span>
              </Space>
            }
            extra={
              <Button type="link" onClick={() => navigate('/front/service')}>
                查看更多
              </Button>
            }
            style={{ height: '100%' }}
          >
            <Skeleton loading={servicesLoading} active paragraph={{ rows: 3 }}>
              {services.length > 0 ? (
                <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                  {services.map(service => (
                    <div
                      key={service.id || service.orderId}
                      style={{
                        padding: '12px',
                        border: '1px solid #f0f0f0',
                        borderRadius: '8px',
                        backgroundColor: isUpcomingService(service.scheduleTime) ? '#fff7e6' : '#fff',
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                        <Text strong>{service.serviceItemName}</Text>
                        <Tag color={ServiceOrderStatusMapper.toColor(service.status)}>
                          {ServiceOrderStatusMapper.toText(service.status)}
                        </Tag>
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '8px' }}>
                        <ClockCircleOutlined style={{ marginRight: '4px', color: isUpcomingService(service.scheduleTime) ? '#faad14' : '#666' }} />
                        <Text style={{ fontSize: '12px', color: isUpcomingService(service.scheduleTime) ? '#faad14' : '#666' }}>
                          {getServiceTimeDisplay(service.scheduleTime)}
                        </Text>
                      </div>
                      {ServiceOrderStatusMapper.canCancel(service.status) && (
                        <Button
                          type="link"
                          danger
                          size="small"
                          onClick={() => handleCancelService(service.id || service.orderId)}
                          style={{ padding: 0 }}
                        >
                          取消预约
                        </Button>
                      )}
                    </div>
                  ))}
                </Space>
              ) : (
                <Empty description="暂无预约服务" />
              )}
            </Skeleton>
          </Card>
        </Col>

        {/* Activities Card */}
        <Col xs={24} md={8}>
          <Card
            title={
              <Space>
                <CalendarOutlined style={{ color: '#faad14' }} />
                <span>社区活动</span>
              </Space>
            }
            extra={
              <Button type="link" onClick={() => navigate('/front/activity')}>
                查看更多
              </Button>
            }
            style={{ height: '100%' }}
          >
            <Skeleton loading={activitiesLoading} active paragraph={{ rows: 3 }}>
              {activities.length > 0 ? (
                <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                  {activities.map(activity => (
                    <div
                      key={activity.id}
                      style={{
                        padding: '12px',
                        border: '1px solid #f0f0f0',
                        borderRadius: '8px',
                        backgroundColor: isActivityComingSoon(activity.startTime) ? '#e6f7ff' : '#fff',
                        position: 'relative',
                      }}
                    >
                      {isActivityComingSoon(activity.startTime) && (
                        <div
                          style={{
                            position: 'absolute',
                            top: '8px',
                            right: '8px',
                            padding: '2px 8px',
                            backgroundColor: '#1890ff',
                            color: '#fff',
                            borderRadius: '4px',
                            fontSize: '11px',
                          }}
                        >
                          即将开始
                        </div>
                      )}
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px', paddingRight: ActivityStatusMapper.canRegister(activity.status) ? '70px' : '0' }}>
                        <Text strong>{activity.title}</Text>
                        <Tag color={ActivityStatusMapper.toColor(activity.status)}>
                          {ActivityStatusMapper.toText(activity.status)}
                        </Tag>
                      </div>
                      <div style={{ marginBottom: '4px' }}>
                        <ClockCircleOutlined style={{ marginRight: '4px', color: '#666' }} />
                        <Text style={{ fontSize: '12px', color: '#666' }}>
                          {formatDateTime(activity.startTime)}
                        </Text>
                      </div>
                      <div>
                        <EnvironmentOutlined style={{ marginRight: '4px', color: '#666' }} />
                        <Text style={{ fontSize: '12px', color: '#666' }}>
                          {activity.location || '地点待定'}
                        </Text>
                      </div>
                      {ActivityStatusMapper.canRegister(activity.status) && userInfo && (
                        <Button
                          type="link"
                          size="small"
                          onClick={() => navigate(`/front/activity/${activity.id}`)}
                          style={{ padding: 0, marginTop: '8px' }}
                        >
                          立即报名
                        </Button>
                      )}
                    </div>
                  ))}
                </Space>
              ) : (
                <Empty description="暂无活动" />
              )}
            </Skeleton>
          </Card>
        </Col>

        {/* Notices Card */}
        <Col xs={24} md={8}>
          <Card
            title={
              <Space>
                <NotificationOutlined style={{ color: '#f5222d' }} />
                <span>通知公告</span>
              </Space>
            }
            extra={
              <Button type="link" onClick={() => navigate('/front/notice')}>
                查看更多
              </Button>
            }
            style={{ height: '100%' }}
          >
            {notices.length > 0 ? (
              <Space direction="vertical" size="small" style={{ width: '100%' }}>
                {notices.map(notice => (
                  <div
                    key={notice.id}
                    style={{
                      padding: '12px',
                      border: '1px solid #f0f0f0',
                      borderRadius: '8px',
                      cursor: 'pointer',
                      transition: 'all 0.3s',
                    }}
                    onClick={() => navigate(`/front/notice/${notice.id}`)}
                    onMouseEnter={e => {
                      e.currentTarget.style.backgroundColor = '#fafafa'
                    }}
                    onMouseLeave={e => {
                      e.currentTarget.style.backgroundColor = '#fff'
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Tooltip title={notice.title}>
                        <Text
                          strong
                          ellipsis
                          style={{
                            flex: 1,
                            marginRight: '8px',
                            cursor: 'pointer',
                          }}
                        >
                          {notice.title}
                        </Text>
                      </Tooltip>
                      <Text type="secondary" style={{ fontSize: '12px', whiteSpace: 'nowrap' }}>
                        {formatDate(notice.publishTime)}
                      </Text>
                    </div>
                  </div>
                ))}
              </Space>
            ) : (
              <Empty description="暂无通知" />
            )}
          </Card>
        </Col>

        {/* Weather Card */}
        <Col xs={24} md={8}>
          <Card
            title={
              <Space>
                <CloudOutlined style={{ color: '#1890ff' }} />
                <span>当前天气</span>
              </Space>
            }
            style={{ height: '100%' }}
          >
            <Space direction="vertical" size="middle" style={{ width: '100%' }}>
              <div style={{ textAlign: 'center' }}>
                <Title level={1} style={{ marginBottom: 0, fontSize: '48px' }}>
                  {weatherData.temperature}°C
                </Title>
                <Text type="secondary">{weatherData.weather}</Text>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <Text>湿度</Text>
                <Text strong>{weatherData.humidity}%</Text>
              </div>
            </Space>
          </Card>
        </Col>

        {/* Emergency Contact Card */}
        <Col xs={24} md={8}>
          <Card
            title={
              <Space>
                <WarningOutlined style={{ color: '#ff4d4f' }} />
                <span>紧急求助</span>
              </Space>
            }
            style={{ height: '100%' }}
          >
            <Space direction="vertical" size="large" style={{ width: '100%', textAlign: 'center' }}>
              <Button
                type="primary"
                danger
                size="large"
                icon={<PhoneOutlined />}
                onClick={() => setEmergencyDialogVisible(true)}
                style={{ width: '100%' }}
              >
                紧急呼叫
              </Button>
              <div>
                <Paragraph style={{ marginBottom: '4px' }}>
                  紧急联系人：社区养老服务中心
                </Paragraph>
                <Paragraph style={{ marginBottom: 0 }}>
                  联系电话：120 / 110
                </Paragraph>
              </div>
            </Space>
          </Card>
        </Col>
      </Row>

      {/* Emergency Call Dialog */}
      <Modal
        title={
          <Space>
            <WarningOutlined style={{ color: '#ff4d4f' }} />
            <span>紧急呼叫</span>
          </Space>
        }
        open={emergencyDialogVisible}
        onCancel={() => !emergencyLoading && setEmergencyDialogVisible(false)}
        footer={null}
        closable={!emergencyLoading}
        maskClosable={!emergencyLoading}
        width={500}
      >
        <div style={{ padding: '12px 0' }}>
          <div
            style={{
              padding: '12px',
              backgroundColor: '#fff7e6',
              border: '1px solid #ffd591',
              borderRadius: '8px',
              marginBottom: '24px',
            }}
          >
            <Space>
              <WarningOutlined style={{ color: '#fa8c16' }} />
              <Text>您正在发起紧急呼叫，系统将通知管理人员并提供紧急救援。</Text>
            </Space>
          </div>

          <Form
            form={emergencyForm}
            layout="vertical"
          >
            <Form.Item
              label="您的位置"
              name="location"
              rules={[{ required: true, message: '请输入您的位置' }]}
            >
              <Input
                placeholder="请输入您的具体位置，例如：3号楼2单元501室"
                disabled={emergencyLoading}
              />
            </Form.Item>

            <Form.Item
              label="紧急信息"
              name="message"
              rules={[{ required: true, message: '请描述您的情况' }]}
            >
              <Input.TextArea
                rows={3}
                placeholder="请简要描述您的情况，例如：我感觉胸闷气短，需要紧急救援"
                disabled={emergencyLoading}
              />
            </Form.Item>
          </Form>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px', marginTop: '24px' }}>
            <Button
              onClick={() => setEmergencyDialogVisible(false)}
              disabled={emergencyLoading}
            >
              取消
            </Button>
            <Button
              type="primary"
              danger
              onClick={handleEmergencyCall}
              loading={emergencyLoading}
            >
              发送紧急呼叫
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  )
}

export default FrontHome
