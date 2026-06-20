import { useState, useEffect, useCallback } from 'react'
import {
  Card,
  Tabs,
  Table,
  Input,
  Select,
  Button,
  Tag,
  Modal,
  Form,
  DatePicker,
  TimePicker,
  Space,
  Pagination,
  Empty,
  Descriptions,
  message,
} from 'antd'
import {
  SearchOutlined,
  CalendarOutlined,
  InfoCircleOutlined,
  ReloadOutlined,
} from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import dayjs, { Dayjs } from 'dayjs'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuthStore } from '@/store/authStore'
import { serviceApi } from '@/api/service'
import { formatDateTime } from '@/utils/format'
import { ServiceOrderStatusMapper } from '@/utils/statusMapper'
import type { ServiceItem, ServiceOrder } from '@/types/service'

const { TabPane } = Tabs
const { TextArea } = Input
const { RangePicker } = DatePicker

interface BookingFormData {
  appointmentDate: string | null
  appointmentTime: string | null
  remark: string
}

/**
 * Service Appointment Page
 * 
 * Complete feature-rich implementation with:
 * - Service list with search and category filter
 * - My appointments with status filter and date range
 * - Booking dialog with date/time picker
 * - Appointment detail modal
 * - Service detail modal
 * - Cancel appointment functionality
 * - Pagination
 * - Tab state persistence
 */
const Service: React.FC = () => {
  const [form] = Form.useForm()
  const navigate = useNavigate()
  const location = useLocation()
  const { userInfo } = useAuthStore()

  // Get initial tab from URL query or localStorage
  const getInitialTab = () => {
    const params = new URLSearchParams(location.search)
    const tabParam = params.get('tab')
    if (tabParam === 'list' || tabParam === 'my') return tabParam
    return localStorage.getItem('service-active-tab') || 'list'
  }

  // State
  const [activeTab, setActiveTab] = useState(getInitialTab())

  // Service list state
  const [services, setServices] = useState<ServiceItem[]>([])
  const [servicesLoading, setServicesLoading] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const [categoryFilter, setCategoryFilter] = useState<string | undefined>()
  const [categoryOptions, setCategoryOptions] = useState<string[]>([])
  const [currentPage, setCurrentPage] = useState(1)
  const [pageSize] = useState(10)
  const [totalServices, setTotalServices] = useState(0)

  // My appointments state
  const [appointments, setAppointments] = useState<ServiceOrder[]>([])
  const [appointmentsLoading, setAppointmentsLoading] = useState(false)
  const [statusFilter, setStatusFilter] = useState<number | undefined>()
  const [dateRange, setDateRange] = useState<[Dayjs, Dayjs] | null>(null)
  const [appointmentPage, setAppointmentPage] = useState(1)
  const [appointmentPageSize] = useState(10)
  const [totalAppointments, setTotalAppointments] = useState(0)

  // Booking dialog state
  const [bookingVisible, setBookingVisible] = useState(false)
  const [currentService, setCurrentService] = useState<ServiceItem | null>(null)
  const [submitting, setSubmitting] = useState(false)

  // Detail modals state
  const [serviceDetailVisible, setServiceDetailVisible] = useState(false)
  const [appointmentDetailVisible, setAppointmentDetailVisible] = useState(false)
  const [currentAppointment, setCurrentAppointment] = useState<ServiceOrder | null>(null)

  // Status options
  const statusOptions = [
    { value: 0, label: '待审核' },
    { value: 1, label: '已派单' },
    { value: 2, label: '服务中' },
    { value: 3, label: '已完成' },
    { value: 4, label: '已取消' },
    { value: 5, label: '已拒绝' },
  ]

  // Filter services by search query and category
  const filteredServices = services.filter((service) => {
    const matchesSearch =
      !searchQuery ||
      service.serviceName.toLowerCase().includes(searchQuery.toLowerCase())
    const matchesCategory =
      !categoryFilter || service.serviceTypeName === categoryFilter
    return matchesSearch && matchesCategory
  })

  // Fetch service list
  const fetchServices = useCallback(async () => {
    setServicesLoading(true)
    try {
      const res = await serviceApi.getServiceList({
        pageNum: currentPage,
        pageSize,
        serviceName: searchQuery || undefined,
      })
      if (res.code === 200 && res.data) {
        const serviceList = res.data.records || res.data || []
        setServices(serviceList)
        setTotalServices(res.data.total || serviceList.length)

        // Extract unique categories
        const categories = Array.from(
          new Set(
            serviceList
              .map((s: ServiceItem) => s.serviceTypeName)
              .filter(Boolean)
          )
        )
        setCategoryOptions(categories as string[])
      }
    } catch (error) {
      console.error('获取服务列表失败:', error)
      message.error('获取服务列表失败')
    } finally {
      setServicesLoading(false)
    }
  }, [currentPage, pageSize, searchQuery])

  // Fetch my appointments
  const fetchAppointments = useCallback(async () => {
    if (!userInfo?.userId) return

    setAppointmentsLoading(true)
    try {
      const res = await serviceApi.getMyAppointments({
        userId: userInfo.userId,
        pageNum: appointmentPage,
        pageSize: appointmentPageSize,
        status: statusFilter,
        startTime: dateRange?.[0]?.format('YYYY-MM-DD'),
        endTime: dateRange?.[1]?.format('YYYY-MM-DD'),
      })
      if (res.code === 200 && res.data) {
        const appointmentList = Array.isArray(res.data) ? res.data : res.data.records || []
        setAppointments(appointmentList)
        setTotalAppointments(
          Array.isArray(res.data) ? appointmentList.length : res.data.total || 0
        )
      }
    } catch (error) {
      console.error('获取预约列表失败:', error)
      message.error('获取预约列表失败')
    } finally {
      setAppointmentsLoading(false)
    }
  }, [userInfo?.userId, appointmentPage, appointmentPageSize, statusFilter, dateRange])

  // Handle tab change
  const handleTabChange = (key: string) => {
    setActiveTab(key)
    localStorage.setItem('service-active-tab', key)
    
    // Update URL
    navigate(`?tab=${key}`, { replace: true })

    if (key === 'list') {
      fetchServices()
    } else if (key === 'my') {
      fetchAppointments()
    }
  }

  // Open booking dialog
  const openBookingDialog = (service: ServiceItem) => {
    setCurrentService(service)
    form.resetFields()
    setBookingVisible(true)
  }

  // Submit booking
  const handleSubmitBooking = async () => {
    try {
      await form.validateFields()
      const values = form.getFieldsValue() as BookingFormData

      if (!currentService || !values.appointmentDate || !values.appointmentTime) {
        message.error('请填写完整的预约信息')
        return
      }

      // Combine date and time
      const scheduleTime = `${values.appointmentDate} ${values.appointmentTime}:00`

      // Validate future time (at least 1 hour ahead)
      const scheduleDateTime = dayjs(scheduleTime)
      const now = dayjs()
      const oneHourLater = now.add(1, 'hour')

      if (scheduleDateTime.isBefore(now)) {
        message.error('预约时间必须大于当前时间')
        return
      }

      if (scheduleDateTime.isBefore(oneHourLater)) {
        message.error('预约时间必须至少提前1小时')
        return
      }

      setSubmitting(true)
      const res = await serviceApi.createAppointment({
        serviceItemId: currentService.id,
        userId: userInfo?.userId!,
        scheduleTime,
        applyReason: values.remark,
      })

      if (res.code === 200) {
        message.success('预约提交成功')
        setBookingVisible(false)
        if (activeTab === 'my') {
          fetchAppointments()
        }
      } else {
        message.error(res.message || '预约提交失败')
      }
    } catch (error: any) {
      console.error('预约提交失败:', error)
      if (error.errorFields) {
        // Form validation error
        return
      }
      message.error(error.message || '预约提交失败')
    } finally {
      setSubmitting(false)
    }
  }

  // Cancel appointment
  const handleCancelAppointment = (appointment: ServiceOrder) => {
    Modal.confirm({
      title: '确认取消',
      content: '确认要取消该预约吗？',
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          const res = await serviceApi.cancelAppointment(appointment.id || appointment.orderId)
          if (res.code === 200) {
            message.success('预约取消成功')
            fetchAppointments()
          } else {
            message.error(res.message || '取消失败')
          }
        } catch (error) {
          console.error('取消预约失败:', error)
          message.error('取消预约失败')
        }
      },
    })
  }

  // Disable past dates
  const disabledDate = (current: Dayjs) => {
    return current && current.isBefore(dayjs(), 'day')
  }

  // Disable past times and non-working hours
  const disabledTime = (date: Dayjs | null) => {
    if (!date) return {}

    const now = dayjs()
    const isToday = date.isSame(now, 'day')

    return {
      disabledHours: () => {
        const hours: number[] = []
        // Disable past hours if today
        if (isToday) {
          for (let i = 0; i <= now.hour(); i++) {
            hours.push(i)
          }
        }
        // Disable non-working hours (before 8 AM and after 6 PM)
        for (let i = 0; i < 8; i++) hours.push(i)
        for (let i = 18; i < 24; i++) hours.push(i)
        return hours
      },
      disabledMinutes: (selectedHour: number) => {
        if (isToday && selectedHour === now.hour()) {
          const minutes: number[] = []
          for (let i = 0; i <= now.minute(); i++) {
            minutes.push(i)
          }
          return minutes
        }
        return []
      },
    }
  }

  // Service list columns
  const serviceColumns: ColumnsType<ServiceItem> = [
    {
      title: '服务名称',
      dataIndex: 'serviceName',
      key: 'serviceName',
      width: 200,
    },
    {
      title: '服务类别',
      dataIndex: 'serviceTypeName',
      key: 'serviceTypeName',
      width: 150,
      render: (text) => <Tag color="blue">{text}</Tag>,
    },
    {
      title: '价格',
      dataIndex: 'price',
      key: 'price',
      width: 120,
      render: (price) => <span className="text-primary font-semibold">{price} 元</span>,
    },
    {
      title: '时长',
      dataIndex: 'duration',
      key: 'duration',
      width: 120,
      render: (duration) => <span>{duration} 分钟</span>,
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button
            type="primary"
            size="small"
            icon={<CalendarOutlined />}
            onClick={() => openBookingDialog(record)}
          >
            预约
          </Button>
          <Button
            size="small"
            icon={<InfoCircleOutlined />}
            onClick={() => {
              setCurrentService(record)
              setServiceDetailVisible(true)
            }}
          >
            详情
          </Button>
        </Space>
      ),
    },
  ]

  // Appointment list columns
  const appointmentColumns: ColumnsType<ServiceOrder> = [
    {
      title: '服务名称',
      dataIndex: 'serviceItemName',
      key: 'serviceItemName',
      width: 150,
    },
    {
      title: '服务类别',
      dataIndex: 'serviceTypeName',
      key: 'serviceTypeName',
      width: 120,
      render: (text) => <Tag color="cyan">{text || '未分类'}</Tag>,
    },
    {
      title: '价格/时长',
      key: 'info',
      width: 120,
      render: (_, record) => (
        <div>
          <div>{record.serviceFee || 0} 元</div>
          <div className="text-gray-500 text-sm">{record.serviceDuration || 0} 分钟</div>
        </div>
      ),
    },
    {
      title: '预约时间',
      dataIndex: 'scheduleTime',
      key: 'scheduleTime',
      width: 180,
      render: (text) => formatDateTime(text),
    },
    {
      title: '预约备注',
      dataIndex: 'applyReason',
      key: 'applyReason',
      width: 150,
      ellipsis: true,
      render: (text) => text || '无',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status) => (
        <Tag color={ServiceOrderStatusMapper.toColor(status)}>
          {ServiceOrderStatusMapper.toText(status)}
        </Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            size="small"
            onClick={() => {
              setCurrentAppointment(record)
              setAppointmentDetailVisible(true)
            }}
          >
            详情
          </Button>
          {ServiceOrderStatusMapper.canCancel(record.status) && (
            <Button
              type="link"
              danger
              size="small"
              onClick={() => handleCancelAppointment(record)}
            >
              取消
            </Button>
          )}
        </Space>
      ),
    },
  ]

  // Initialize
  useEffect(() => {
    if (activeTab === 'list') {
      fetchServices()
    } else if (activeTab === 'my') {
      fetchAppointments()
    }
  }, [activeTab, fetchServices, fetchAppointments])

  return (
    <div className="p-6 bg-gray-50 min-h-[calc(100vh-64px-70px)]">
      <Card title="服务预约" className="shadow-md">
        <Tabs activeKey={activeTab} onChange={handleTabChange}>
          {/* Service List Tab */}
          <TabPane tab="服务列表" key="list">
            {/* Filters */}
            <div className="mb-4 flex gap-4">
              <Input
                placeholder="搜索服务名称"
                prefix={<SearchOutlined />}
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onPressEnter={fetchServices}
                style={{ width: 300 }}
                allowClear
              />
              <Select
                placeholder="服务类别"
                value={categoryFilter}
                onChange={setCategoryFilter}
                style={{ width: 200 }}
                allowClear
              >
                {categoryOptions.map((cat) => (
                  <Select.Option key={cat} value={cat}>
                    {cat}
                  </Select.Option>
                ))}
              </Select>
              <Button type="primary" onClick={fetchServices}>
                搜索
              </Button>
            </div>

            {/* Service Table */}
            <Table
              columns={serviceColumns}
              dataSource={filteredServices}
              loading={servicesLoading}
              rowKey={(record) => record.id}
              pagination={false}
              locale={{
                emptyText: (
                  <Empty description="暂无服务数据" image={Empty.PRESENTED_IMAGE_SIMPLE} />
                ),
              }}
            />

            {/* Pagination */}
            {totalServices > pageSize && (
              <div className="mt-4 flex justify-end">
                <Pagination
                  current={currentPage}
                  pageSize={pageSize}
                  total={totalServices}
                  onChange={setCurrentPage}
                  showTotal={(total) => `共 ${total} 条`}
                />
              </div>
            )}
          </TabPane>

          {/* My Appointments Tab */}
          <TabPane tab="我的预约" key="my">
            {/* Filters */}
            <div className="mb-4 flex gap-4">
              <Select
                placeholder="预约状态"
                value={statusFilter}
                onChange={setStatusFilter}
                style={{ width: 150 }}
                allowClear
              >
                {statusOptions.map((opt) => (
                  <Select.Option key={opt.value} value={opt.value}>
                    {opt.label}
                  </Select.Option>
                ))}
              </Select>
              <RangePicker
                value={dateRange}
                onChange={(dates) => setDateRange(dates as [Dayjs, Dayjs] | null)}
                format="YYYY-MM-DD"
                placeholder={['开始日期', '结束日期']}
              />
              <Button
                type="primary"
                icon={<ReloadOutlined />}
                onClick={fetchAppointments}
              >
                刷新
              </Button>
            </div>

            {/* Appointment Table */}
            <Table
              columns={appointmentColumns}
              dataSource={appointments}
              loading={appointmentsLoading}
              rowKey={(record) => record.id || record.orderId}
              pagination={false}
              locale={{
                emptyText: (
                  <Empty description="暂无预约数据" image={Empty.PRESENTED_IMAGE_SIMPLE} />
                ),
              }}
            />

            {/* Pagination */}
            {totalAppointments > appointmentPageSize && (
              <div className="mt-4 flex justify-end">
                <Pagination
                  current={appointmentPage}
                  pageSize={appointmentPageSize}
                  total={totalAppointments}
                  onChange={setAppointmentPage}
                  showTotal={(total) => `共 ${total} 条`}
                />
              </div>
            )}
          </TabPane>
        </Tabs>
      </Card>

      {/* Booking Modal */}
      <Modal
        title="服务预约"
        open={bookingVisible}
        onCancel={() => setBookingVisible(false)}
        onOk={handleSubmitBooking}
        confirmLoading={submitting}
        width={500}
      >
        <Form form={form} layout="vertical" className="mt-4">
          <Form.Item label="服务名称">
            <span>{currentService?.serviceName}</span>
          </Form.Item>
          <Form.Item label="服务类别">
            <span>{currentService?.serviceTypeName}</span>
          </Form.Item>
          <Form.Item label="价格">
            <span>{currentService?.price} 元</span>
          </Form.Item>
          <Form.Item label="时长">
            <span>{currentService?.duration} 分钟</span>
          </Form.Item>
          <Form.Item
            label="预约日期"
            name="appointmentDate"
            rules={[{ required: true, message: '请选择预约日期' }]}
          >
            <DatePicker
              style={{ width: '100%' }}
              format="YYYY-MM-DD"
              disabledDate={disabledDate}
            />
          </Form.Item>
          <Form.Item
            label="预约时间"
            name="appointmentTime"
            rules={[{ required: true, message: '请选择预约时间' }]}
          >
            <TimePicker
              style={{ width: '100%' }}
              format="HH:mm"
              disabledTime={() => {
                const date = form.getFieldValue('appointmentDate')
                return disabledTime(date)
              }}
            />
          </Form.Item>
          <Form.Item
            label="申请原因"
            name="remark"
            rules={[
              { required: true, message: '请输入申请原因' },
              { min: 5, max: 500, message: '申请原因长度必须在5-500个字符之间' },
            ]}
          >
            <TextArea rows={3} placeholder="请输入申请原因" maxLength={500} showCount />
          </Form.Item>
        </Form>
      </Modal>

      {/* Service Detail Modal */}
      <Modal
        title="服务详情"
        open={serviceDetailVisible}
        onCancel={() => setServiceDetailVisible(false)}
        footer={null}
        width={500}
      >
        {currentService && (
          <Descriptions column={1} bordered>
            <Descriptions.Item label="服务名称">{currentService.serviceName}</Descriptions.Item>
            <Descriptions.Item label="服务类别">{currentService.serviceTypeName}</Descriptions.Item>
            <Descriptions.Item label="价格">{currentService.price} 元</Descriptions.Item>
            <Descriptions.Item label="时长">{currentService.duration} 分钟</Descriptions.Item>
            <Descriptions.Item label="服务描述">
              {currentService.description || '暂无描述'}
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>

      {/* Appointment Detail Modal */}
      <Modal
        title="预约详情"
        open={appointmentDetailVisible}
        onCancel={() => setAppointmentDetailVisible(false)}
        footer={null}
        width={600}
      >
        {currentAppointment && (
          <Descriptions column={2} bordered>
            <Descriptions.Item label="服务名称">
              {currentAppointment.serviceItemName}
            </Descriptions.Item>
            <Descriptions.Item label="服务类别">
              {currentAppointment.serviceTypeName || '未分类'}
            </Descriptions.Item>
            <Descriptions.Item label="价格">
              {currentAppointment.serviceFee || 0} 元
            </Descriptions.Item>
            <Descriptions.Item label="时长">
              {currentAppointment.serviceDuration || 0} 分钟
            </Descriptions.Item>
            <Descriptions.Item label="预约时间">
              {formatDateTime(currentAppointment.scheduleTime)}
            </Descriptions.Item>
            <Descriptions.Item label="预约状态">
              <Tag color={ServiceOrderStatusMapper.toColor(currentAppointment.status)}>
                {ServiceOrderStatusMapper.toText(currentAppointment.status)}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="申请原因" span={2}>
              {currentAppointment.applyReason || '无'}
            </Descriptions.Item>
            {currentAppointment.status >= 1 && (
              <Descriptions.Item label="服务人员">
                {currentAppointment.staffName || '暂未指派'}
              </Descriptions.Item>
            )}
            {currentAppointment.statusUpdateTime && (
              <Descriptions.Item label="状态更新时间">
                {formatDateTime(currentAppointment.statusUpdateTime)}
              </Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Modal>
    </div>
  )
}

export default Service
