import { useState, useEffect, useCallback, useRef } from 'react'
import {
  Card,
  Tabs,
  Table,
  Tag,
  Button,
  Form,
  Input,
  InputNumber,
  Row,
  Col,
  Divider,
  message,
  Empty,
  Modal,
  Pagination,
  Space,
  Spin,
} from 'antd'
import {
  ReloadOutlined,
  EditOutlined,
  SaveOutlined,
  CopyOutlined,
  DownloadOutlined,
} from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import { useAuthStore } from '@/store/authStore'
import { healthApi } from '@/api/health'
import { formatDateTime } from '@/utils/format'
import { calculateBMI } from '@/utils/format'
import HealthTrendChart from './components/HealthTrendChart'
import type { HealthRecord } from '@/types/health'

const { TabPane } = Tabs
const { TextArea } = Input

/**
 * Health Center Page
 * 
 * Complete feature-rich implementation with:
 * - Health monitoring records with trend chart
 * - Health profile with edit mode
 * - BMI calculation
 * - Abnormal value detection
 * - Export to PDF
 * - Copy to clipboard
 * - Pagination
 * - Auto-refresh
 */
const Health: React.FC = () => {
  const [form] = Form.useForm()
  const { userInfo } = useAuthStore()
  const healthInfoRef = useRef<HTMLDivElement>(null)

  // State
  const [activeTab, setActiveTab] = useState(localStorage.getItem('health-active-tab') || 'monitor')
  const [isEditMode, setIsEditMode] = useState(false)
  const [loading, setLoading] = useState(false)
  const [copyLoading, setCopyLoading] = useState(false)
  const [exportLoading, setExportLoading] = useState(false)

  // Health profile state
  const [healthData, setHealthData] = useState<HealthRecord | null>(null)

  // Health monitoring state
  const [monitorList, setMonitorList] = useState<HealthRecord[]>([])
  const [monitorLoading, setMonitorLoading] = useState(false)
  const [monitorTotal, setMonitorTotal] = useState(0)
  const [currentPage, setCurrentPage] = useState(1)
  const [pageSize] = useState(10)

  // Monitoring type mapping
  const monitoringTypeMap: Record<string, string> = {
    '1': '血压',
    '2': '血糖',
    '3': '体温',
    '4': '心率',
    '5': '血氧',
    '6': '体重',
  }

  // Table columns for health monitoring
  const monitorColumns: ColumnsType<HealthRecord> = [
    {
      title: '时间',
      dataIndex: 'monitoringTime',
      key: 'monitoringTime',
      width: 180,
      render: (text: string) => formatDateTime(text),
    },
    {
      title: '类型',
      dataIndex: 'monitoringType',
      key: 'monitoringType',
      width: 100,
      render: (text: string) => monitoringTypeMap[text] || '其他',
    },
    {
      title: '数值',
      dataIndex: 'monitoringValue',
      key: 'monitoringValue',
      width: 100,
    },
    {
      title: '单位',
      dataIndex: 'monitoringUnit',
      key: 'monitoringUnit',
      width: 80,
    },
    {
      title: '状态',
      key: 'status',
      width: 100,
      render: (_: any, record: HealthRecord) => {
        // Check if any vital sign is abnormal
        const isAbnormal = 
          (record.bloodPressure && (
            parseInt(record.bloodPressure.split('/')[0]) > 140 ||
            parseInt(record.bloodPressure.split('/')[1]) > 90
          )) ||
          (record.heartRate && (record.heartRate < 60 || record.heartRate > 100)) ||
          (record.bloodSugar && (record.bloodSugar < 3.9 || record.bloodSugar > 6.1)) ||
          (record.temperature && (record.temperature < 36 || record.temperature > 37.3))
        
        return (
          <Tag color={isAbnormal ? 'error' : 'success'}>
            {isAbnormal ? '异常' : '正常'}
          </Tag>
        )
      },
    },
    {
      title: '异常说明',
      dataIndex: 'abnormalDescription',
      key: 'abnormalDescription',
      render: (text: string) => text || '-',
    },
  ]

  // Check if value is abnormal
  const isAbnormalValue = (field: string, value: any): boolean => {
    if (!value) return false

    switch (field) {
      case 'bloodPressure':
        if (typeof value === 'string' && value.includes('/')) {
          const [sys, dia] = value.split('/').map(Number)
          return sys > 140 || dia > 90 || sys < 90 || dia < 60
        }
        return false
      case 'bloodSugar':
        return value < 3.9 || value > 7.8
      case 'temperature':
        return value < 36.0 || value > 37.2
      case 'heartRate':
        return value < 60 || value > 100
      default:
        return false
    }
  }

  // Fetch health monitoring list
  const fetchMonitorList = useCallback(async () => {
    if (!userInfo?.userId) return

    setMonitorLoading(true)
    try {
      const res = await healthApi.getHealthRecords(userInfo.userId)
      if (res.code === 200 && res.data) {
        const records = Array.isArray(res.data) ? res.data : []
        // Simple pagination on frontend
        const startIndex = (currentPage - 1) * pageSize
        const endIndex = startIndex + pageSize
        setMonitorList(records.slice(startIndex, endIndex))
        setMonitorTotal(records.length)
      }
    } catch (error) {
      console.error('获取健康监测数据失败:', error)
      message.error('获取健康监测数据失败')
    } finally {
      setMonitorLoading(false)
    }
  }, [userInfo?.userId, currentPage, pageSize])

  // Fetch health profile
  const fetchHealthProfile = useCallback(async () => {
    if (!userInfo?.userId) return

    setLoading(true)
    try {
      const res = await healthApi.getHealthRecords(userInfo.userId)
      if (res.code === 200 && res.data && res.data.length > 0) {
        const record = res.data[0]
        setHealthData(record)
        
        // Set form values
        form.setFieldsValue({
          ...record,
          medicalHistory: record.medicalHistory || '无',
          allergy: record.allergy || '无',
          symptoms: record.symptoms || '无',
          medication: record.medication || '无',
        })
      } else {
        // Create new health record if not exists
        await createHealthRecord()
      }
    } catch (error) {
      console.error('获取健康档案失败:', error)
      message.error('获取健康档案失败')
    } finally {
      setLoading(false)
    }
  }, [userInfo?.userId, form])

  // Create health record
  const createHealthRecord = async () => {
    if (!userInfo?.userId) return

    try {
      const height = 170
      const weight = 65
      const bmi = calculateBMI(height, weight)

      const newRecord = {
        elderId: userInfo.userId,
        height,
        weight,
        bmi,
        bloodPressure: '120/80',
        heartRate: 75,
        bloodSugar: 5.6,
        temperature: 36.5,
        medicalHistory: '无',
        allergy: '无',
        symptoms: '无',
        medication: '无',
      }

      const res = await healthApi.addHealthRecord(newRecord)
      if (res.code === 200 && res.data) {
        message.success('健康档案创建成功')
        setHealthData(res.data)
        form.setFieldsValue(res.data)
      }
    } catch (error) {
      console.error('创建健康档案失败:', error)
      message.error('创建健康档案失败')
    }
  }

  // Toggle edit mode
  const toggleEditMode = () => {
    if (isEditMode && healthData) {
      // Cancel editing - restore original data
      form.setFieldsValue(healthData)
    }
    setIsEditMode(!isEditMode)
  }

  // Save health profile
  const saveHealthProfile = async () => {
    try {
      await form.validateFields()
      
      Modal.confirm({
        title: '确认保存',
        content: '确认保存修改的健康档案信息吗？',
        okText: '确定',
        cancelText: '取消',
        onOk: async () => {
          setLoading(true)
          try {
            const values = form.getFieldsValue()
            
            // Calculate BMI if height and weight exist
            let bmi = healthData?.bmi
            if (values.height && values.weight) {
              bmi = calculateBMI(values.height, values.weight)
            }

            const formData = {
              ...healthData,
              ...values,
              bmi,
              userId: userInfo?.userId,
            }

            const res = await healthApi.updateHealthRecord(formData)
            if (res.code === 200) {
              message.success('保存成功')
              setHealthData(res.data)
              setIsEditMode(false)
              await fetchHealthProfile()
            } else {
              message.error(res.message || '保存失败')
            }
          } catch (error) {
            console.error('保存失败:', error)
            message.error('保存失败')
          } finally {
            setLoading(false)
          }
        },
      })
    } catch (error) {
      console.error('表单验证失败:', error)
    }
  }

  // Copy health info to clipboard
  const copyHealthInfo = async () => {
    if (!healthData) return

    setCopyLoading(true)
    try {
      const bmi = healthData.bmi || calculateBMI(healthData.height!, healthData.weight!)
      const info = [
        '【基础指标】',
        `身高: ${healthData.height || '未填写'} cm`,
        `体重: ${healthData.weight || '未填写'} kg`,
        `BMI: ${bmi || '未填写'}`,
        '【生命体征】',
        `血压: ${healthData.bloodPressure || '未填写'} mmHg`,
        `心率: ${healthData.heartRate || '未填写'} 次/分`,
        `血糖: ${healthData.bloodSugar || '未填写'} mmol/L`,
        `体温: ${healthData.temperature || '未填写'} ℃`,
        '【健康状况】',
        `既往病史: ${healthData.medicalHistory || '无'}`,
        `过敏史: ${healthData.allergy || '无'}`,
        `当前症状: ${healthData.symptoms || '无'}`,
        `用药情况: ${healthData.medication || '无'}`,
        `最后更新时间: ${formatDateTime(healthData.recordTime)}`,
      ].join('\n')

      await navigator.clipboard.writeText(info)
      message.success('健康档案已复制到剪贴板')
    } catch (error) {
      console.error('复制失败:', error)
      message.error('复制失败，请重试')
    } finally {
      setCopyLoading(false)
    }
  }

  // Export health info to PDF
  const exportHealthInfoPDF = async () => {
    if (!healthInfoRef.current) return

    setExportLoading(true)
    message.info('正在生成PDF，请稍候...')

    try {
      const html2canvas = (await import('html2canvas')).default
      const jsPDF = (await import('jspdf')).default

      const canvas = await html2canvas(healthInfoRef.current, { scale: 2 })
      const imgData = canvas.toDataURL('image/png')
      const pdf = new jsPDF('p', 'mm', 'a4')
      const pageWidth = pdf.internal.pageSize.getWidth()
      const imgWidth = pageWidth - 20
      const imgHeight = (canvas.height * imgWidth) / canvas.width

      pdf.addImage(imgData, 'PNG', 10, 10, imgWidth, imgHeight)
      pdf.save('健康档案.pdf')
      message.success('健康档案已导出为PDF')
    } catch (error) {
      console.error('导出PDF失败:', error)
      message.error('导出PDF失败，请重试')
    } finally {
      setExportLoading(false)
    }
  }

  // Handle tab change
  const handleTabChange = (key: string) => {
    setActiveTab(key)
    localStorage.setItem('health-active-tab', key)
    if (key === 'monitor') {
      fetchMonitorList()
    } else if (key === 'profile') {
      fetchHealthProfile()
    }
  }

  // Initialize
  useEffect(() => {
    if (activeTab === 'monitor') {
      fetchMonitorList()
    } else if (activeTab === 'profile') {
      fetchHealthProfile()
    }
  }, [activeTab, fetchMonitorList, fetchHealthProfile])

  return (
    <div className="p-6 bg-gray-50 min-h-[calc(100vh-64px-70px)]">
      <Card title="健康中心" className="shadow-md">
        <Tabs activeKey={activeTab} onChange={handleTabChange}>
          {/* Health Monitoring Tab */}
          <TabPane tab="健康监测记录" key="monitor">
            {/* Trend Chart */}
            <HealthTrendChart data={monitorList} loading={monitorLoading} />

            {/* Refresh Button */}
            <div className="mb-4 flex justify-end">
              <Button
                type="primary"
                icon={<ReloadOutlined />}
                onClick={fetchMonitorList}
                loading={monitorLoading}
              >
                刷新
              </Button>
            </div>

            {/* Monitoring Table */}
            <Table
              columns={monitorColumns}
              dataSource={monitorList}
              loading={monitorLoading}
              rowKey={(record) => record.recordId}
              pagination={false}
              locale={{
                emptyText: (
                  <Empty
                    description="暂无健康监测数据"
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                  />
                ),
              }}
              rowClassName={(record) => {
                const isAbnormal = 
                  (record.bloodPressure && (
                    parseInt(record.bloodPressure.split('/')[0]) > 140 ||
                    parseInt(record.bloodPressure.split('/')[1]) > 90
                  )) ||
                  (record.heartRate && (record.heartRate < 60 || record.heartRate > 100)) ||
                  (record.bloodSugar && (record.bloodSugar < 3.9 || record.bloodSugar > 6.1)) ||
                  (record.temperature && (record.temperature < 36 || record.temperature > 37.3))
                return isAbnormal ? 'bg-red-50' : ''
              }}
            />

            {/* Pagination */}
            {monitorTotal > pageSize && (
              <div className="mt-4 flex justify-end">
                <Pagination
                  current={currentPage}
                  pageSize={pageSize}
                  total={monitorTotal}
                  onChange={setCurrentPage}
                  showTotal={(total) => `共 ${total} 条`}
                />
              </div>
            )}
          </TabPane>

          {/* Health Profile Tab */}
          <TabPane tab="健康档案" key="profile">
            <Spin spinning={loading}>
              {isEditMode ? (
                // Edit Mode
                <Form form={form} layout="vertical">
                  <Divider orientation={"left" as any}>基础指标</Divider>
                  <Row gutter={20}>
                    <Col xs={24} sm={12}>
                      <Form.Item
                        label="身高"
                        name="height"
                        rules={[
                          { required: true, message: '请输入身高' },
                          { type: 'number', min: 100, max: 250, message: '身高应在100-250cm之间' },
                        ]}
                      >
                        <InputNumber
                          style={{ width: '100%' }}
                          min={100}
                          max={250}
                          addonAfter="cm"
                        />
                      </Form.Item>
                    </Col>
                    <Col xs={24} sm={12}>
                      <Form.Item
                        label="体重"
                        name="weight"
                        rules={[
                          { required: true, message: '请输入体重' },
                          { type: 'number', min: 30, max: 200, message: '体重应在30-200kg之间' },
                        ]}
                      >
                        <InputNumber
                          style={{ width: '100%' }}
                          min={30}
                          max={200}
                          addonAfter="kg"
                        />
                      </Form.Item>
                    </Col>
                  </Row>

                  <Divider orientation={"left" as any}>生命体征</Divider>
                  <Row gutter={20}>
                    <Col xs={24} sm={12}>
                      <Form.Item
                        label="血压"
                        name="bloodPressure"
                        rules={[
                          { required: true, message: '请输入血压' },
                          { pattern: /^\d{2,3}\/\d{2,3}$/, message: '请输入正确格式，如：120/80' },
                        ]}
                      >
                        <Input placeholder="如：120/80" addonAfter="mmHg" />
                      </Form.Item>
                    </Col>
                    <Col xs={24} sm={12}>
                      <Form.Item
                        label="心率"
                        name="heartRate"
                        rules={[
                          { required: true, message: '请输入心率' },
                          { type: 'number', min: 40, max: 200, message: '心率应在40-200次/分之间' },
                        ]}
                      >
                        <InputNumber
                          style={{ width: '100%' }}
                          min={40}
                          max={200}
                          addonAfter="次/分"
                        />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Row gutter={20}>
                    <Col xs={24} sm={12}>
                      <Form.Item
                        label="血糖"
                        name="bloodSugar"
                        rules={[
                          { required: true, message: '请输入血糖' },
                          { type: 'number', min: 2, max: 30, message: '血糖值应在2-30mmol/L之间' },
                        ]}
                      >
                        <InputNumber
                          style={{ width: '100%' }}
                          min={2}
                          max={30}
                          step={0.1}
                          precision={1}
                          addonAfter="mmol/L"
                        />
                      </Form.Item>
                    </Col>
                    <Col xs={24} sm={12}>
                      <Form.Item
                        label="体温"
                        name="temperature"
                        rules={[
                          { required: true, message: '请输入体温' },
                          { type: 'number', min: 35, max: 42, message: '体温应在35-42℃之间' },
                        ]}
                      >
                        <InputNumber
                          style={{ width: '100%' }}
                          min={35}
                          max={42}
                          step={0.1}
                          precision={1}
                          addonAfter="℃"
                        />
                      </Form.Item>
                    </Col>
                  </Row>

                  <Divider orientation={"left" as any}>健康状况</Divider>
                  <Form.Item label="既往病史" name="medicalHistory">
                    <TextArea rows={2} placeholder="请输入既往病史" />
                  </Form.Item>
                  <Form.Item label="过敏史" name="allergy">
                    <TextArea rows={2} placeholder="请输入过敏史" />
                  </Form.Item>
                  <Form.Item label="当前症状" name="symptoms">
                    <TextArea rows={2} placeholder="请输入当前症状" />
                  </Form.Item>
                  <Form.Item label="用药情况" name="medication">
                    <TextArea rows={2} placeholder="请输入用药情况" />
                  </Form.Item>

                  <Form.Item>
                    <Space>
                      <Button
                        type="primary"
                        icon={<SaveOutlined />}
                        onClick={saveHealthProfile}
                        loading={loading}
                      >
                        保存
                      </Button>
                      <Button onClick={() => form.resetFields()}>重置</Button>
                      <Button onClick={toggleEditMode}>取消</Button>
                    </Space>
                  </Form.Item>
                </Form>
              ) : (
                // View Mode
                <div ref={healthInfoRef}>
                  {healthData ? (
                    <>
                      <Divider orientation={"left" as any}>基础指标</Divider>
                      <Row gutter={20}>
                        <Col xs={24} sm={12}>
                          <p>
                            <strong>身高:</strong> {healthData.height || '未填写'} cm
                          </p>
                          <p>
                            <strong>体重:</strong> {healthData.weight || '未填写'} kg
                          </p>
                        </Col>
                        <Col xs={24} sm={12}>
                          <p>
                            <strong>BMI:</strong>{' '}
                            {healthData.bmi ||
                              calculateBMI(healthData.height!, healthData.weight!) ||
                              '未填写'}
                          </p>
                        </Col>
                      </Row>

                      <Divider orientation={"left" as any}>生命体征</Divider>
                      <Row gutter={20}>
                        <Col xs={24} sm={12}>
                          <p>
                            <strong>血压:</strong>{' '}
                            {isAbnormalValue('bloodPressure', healthData.bloodPressure) ? (
                              <Tag color="error">{healthData.bloodPressure}</Tag>
                            ) : (
                              healthData.bloodPressure || '未填写'
                            )}{' '}
                            mmHg
                          </p>
                          <p>
                            <strong>心率:</strong>{' '}
                            {isAbnormalValue('heartRate', healthData.heartRate) ? (
                              <Tag color="error">{healthData.heartRate}</Tag>
                            ) : (
                              healthData.heartRate || '未填写'
                            )}{' '}
                            次/分
                          </p>
                        </Col>
                        <Col xs={24} sm={12}>
                          <p>
                            <strong>血糖:</strong>{' '}
                            {isAbnormalValue('bloodSugar', healthData.bloodSugar) ? (
                              <Tag color="error">{healthData.bloodSugar}</Tag>
                            ) : (
                              healthData.bloodSugar || '未填写'
                            )}{' '}
                            mmol/L
                          </p>
                          <p>
                            <strong>体温:</strong>{' '}
                            {isAbnormalValue('temperature', healthData.temperature) ? (
                              <Tag color="error">{healthData.temperature}</Tag>
                            ) : (
                              healthData.temperature || '未填写'
                            )}{' '}
                            ℃
                          </p>
                        </Col>
                      </Row>

                      <Divider orientation={"left" as any}>健康状况</Divider>
                      <Row gutter={20}>
                        <Col xs={24} sm={12}>
                          <p>
                            <strong>既往病史:</strong> {healthData.medicalHistory || '无'}
                          </p>
                          <p>
                            <strong>过敏史:</strong> {healthData.allergy || '无'}
                          </p>
                        </Col>
                        <Col xs={24} sm={12}>
                          <p>
                            <strong>当前症状:</strong> {healthData.symptoms || '无'}
                          </p>
                          <p>
                            <strong>用药情况:</strong> {healthData.medication || '无'}
                          </p>
                        </Col>
                      </Row>

                      <div className="mt-4 flex gap-3 items-center">
                        <Button
                          size="small"
                          icon={<CopyOutlined />}
                          onClick={copyHealthInfo}
                          loading={copyLoading}
                        >
                          复制全部
                        </Button>
                        <Button
                          size="small"
                          icon={<DownloadOutlined />}
                          onClick={exportHealthInfoPDF}
                          loading={exportLoading}
                        >
                          导出PDF
                        </Button>
                        <span className="text-gray-400 text-sm ml-2">
                          最后更新时间：{formatDateTime(healthData.recordTime)}
                        </span>
                      </div>
                    </>
                  ) : (
                    <Empty
                      description="暂无健康档案数据"
                      image={Empty.PRESENTED_IMAGE_SIMPLE}
                    >
                      <Button type="primary" onClick={fetchHealthProfile}>
                        创建健康档案
                      </Button>
                    </Empty>
                  )}
                </div>
              )}

              {!isEditMode && (
                <div className="mt-4 flex gap-3">
                  <Button
                    type="primary"
                    icon={<EditOutlined />}
                    onClick={toggleEditMode}
                    disabled={!healthData}
                  >
                    编辑
                  </Button>
                  <Button icon={<ReloadOutlined />} onClick={fetchHealthProfile}>
                    刷新
                  </Button>
                </div>
              )}
            </Spin>
          </TabPane>
        </Tabs>
      </Card>
    </div>
  )
}

export default Health

