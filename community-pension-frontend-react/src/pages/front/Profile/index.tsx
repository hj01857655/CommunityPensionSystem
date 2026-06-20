import { useState, useEffect, useCallback } from 'react'
import {
  Card,
  Form,
  Input,
  Select,
  DatePicker,
  Button,
  Row,
  Col,
  Tabs,
  Upload,
  message,
  Modal,
  Tag,
  Space,
  Divider,
  Avatar,
} from 'antd'
import {
  UserOutlined,
  PlusOutlined,
  CloseOutlined,
  SaveOutlined,
  EditOutlined,
} from '@ant-design/icons'
import type { UploadProps } from 'antd'
import dayjs from 'dayjs'
import { useAuthStore } from '@/store/authStore'
import { userApi } from '@/api/user'
import { UserRole } from '@/types/enums'
import { getAvatarUrl } from '@/utils/avatarUtils'

const { TextArea } = Input
const { TabPane } = Tabs

interface ProfileFormData {
  userId: number
  name: string
  gender: string
  birthday: string
  idCard: string
  phone: string
  address: string
  email?: string
  emergencyContactName: string
  emergencyContactPhone: string
  avatar?: string
  remark?: string
  bindElderIds?: number[]
  bindKinIds?: number[]
  relationType?: string
}

interface FamilyMember {
  id: number
  userId: number
  name: string
  relationType: string
}

/**
 * Profile Page - User Personal Information Management
 * 
 * Complete feature-rich implementation with:
 * - Tab-based information display (Basic, Contact, Other)
 * - Edit mode toggle with save/cancel
 * - Avatar upload with preview
 * - Family member binding/unbinding (elder-kin relationship)
 * - Form validation
 * - Responsive layout
 */
const Profile: React.FC = () => {
  const [form] = Form.useForm()
  const { userInfo } = useAuthStore()
  
  const [activeTab, setActiveTab] = useState('basic')
  const [isEditMode, setIsEditMode] = useState(false)
  const [loading, setLoading] = useState(false)
  const [avatarUrl, setAvatarUrl] = useState<string>('')
  const [originalData, setOriginalData] = useState<ProfileFormData | null>(null)
  
  // Family members lists
  const [kinList, setKinList] = useState<FamilyMember[]>([])
  const [elderList, setElderList] = useState<FamilyMember[]>([])
  const [kinListLoading, setKinListLoading] = useState(false)
  const [elderListLoading, setElderListLoading] = useState(false)

  // Check user role
  const isElder = userInfo?.roleId === Number(UserRole.Elder)
  const isKin = userInfo?.roleId === Number(UserRole.Kin)

  // Fetch kin list (for elder users)
  const fetchKinList = useCallback(async () => {
    if (!isElder || !userInfo?.userId) return
    
    setKinListLoading(true)
    try {
      const res = await userApi.getKinListByElderId(userInfo.userId)
      if (res.code === 200 && res.data) {
        const kinData = Array.isArray(res.data) ? res.data : []
        setKinList(kinData.map((kin: any) => ({
          id: kin.id || kin.userId,
          userId: kin.userId || kin.id,
          name: kin.name || '未知家属',
          relationType: kin.relationType || '未知关系',
        })))
      }
    } catch (error) {
      console.error('获取家属列表失败:', error)
      message.error('获取家属列表失败')
    } finally {
      setKinListLoading(false)
    }
  }, [isElder, userInfo?.userId])

  // Fetch elder list (for kin users)
  const fetchElderList = useCallback(async () => {
    if (!isKin || !userInfo?.userId) return
    
    setElderListLoading(true)
    try {
      const res = await userApi.getElderListByKinId(userInfo.userId)
      if (res.code === 200 && res.data) {
        const elderData = Array.isArray(res.data) ? res.data : []
        setElderList(elderData.map((elder: any) => ({
          id: elder.id || elder.userId,
          userId: elder.userId || elder.id,
          name: elder.name || '未知老人',
          relationType: elder.relationType || '未知关系',
        })))
      }
    } catch (error) {
      console.error('获取老人列表失败:', error)
      message.error('获取老人列表失败')
    } finally {
      setElderListLoading(false)
    }
  }, [isKin, userInfo?.userId])

  // Initialize form data
  const initFormData = useCallback(async () => {
    if (!userInfo) {
      message.error('获取用户信息失败，请重新登录')
      return
    }

    const formData: ProfileFormData = {
      userId: userInfo.userId,
      name: userInfo.name || '',
      gender: userInfo.gender || '',
      birthday: userInfo.birthday || '',
      idCard: userInfo.idCard || '',
      phone: userInfo.phone || '',
      address: userInfo.address || '',
      email: userInfo.email || '',
      emergencyContactName: userInfo.emergencyContactName || '',
      emergencyContactPhone: userInfo.emergencyContactPhone || '',
      avatar: userInfo.avatar || '',
      remark: userInfo.remark || '',
      bindElderIds: userInfo.bindElderIds || [],
      bindKinIds: userInfo.bindKinIds || [],
      relationType: userInfo.relationType || '',
    }

    // Set form values
    form.setFieldsValue({
      ...formData,
      birthday: formData.birthday ? dayjs(formData.birthday) : null,
    })

    // Set avatar
    if (formData.avatar) {
      setAvatarUrl(getAvatarUrl(formData.avatar))
    }

    // Save original data
    setOriginalData(formData)

    // Fetch family member lists
    if (isElder) {
      await fetchKinList()
    } else if (isKin) {
      await fetchElderList()
    }
  }, [userInfo, form, isElder, isKin, fetchKinList, fetchElderList])

  // Toggle edit mode
  const toggleEditMode = () => {
    if (isEditMode && originalData) {
      // Cancel editing - restore original data
      form.setFieldsValue({
        ...originalData,
        birthday: originalData.birthday ? dayjs(originalData.birthday) : null,
      })
      if (originalData.avatar) {
        setAvatarUrl(getAvatarUrl(originalData.avatar))
      }
    }
    setIsEditMode(!isEditMode)
  }

  // Save profile
  const saveProfile = async () => {
    try {
      await form.validateFields()
      
      Modal.confirm({
        title: '确认保存',
        content: '确认保存修改的信息吗？',
        okText: '确定',
        cancelText: '取消',
        onOk: async () => {
          setLoading(true)
          try {
            const values = form.getFieldsValue()
            const formData = {
              userId: userInfo?.userId!,
              name: values.name,
              gender: values.gender,
              birthday: values.birthday ? values.birthday.format('YYYY-MM-DD') : undefined,
              idCard: values.idCard,
              phone: values.phone,
              address: values.address,
              email: values.email,
              emergencyContactName: values.emergencyContactName,
              emergencyContactPhone: values.emergencyContactPhone,
              avatar: values.avatar,
            }

            const res = await userApi.updateUserInfo(formData)
            if (res.code === 200) {
              message.success('保存成功')
              setIsEditMode(false)
              
              // Update original data
              const newOriginalData = {
                ...formData,
                userId: userInfo?.userId!,
                bindElderIds: originalData?.bindElderIds || [],
                bindKinIds: originalData?.bindKinIds || [],
                relationType: originalData?.relationType || '',
              }
              setOriginalData(newOriginalData)
              
              // Re-fetch user info to sync
              await initFormData()
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

  // Avatar upload configuration
  const uploadProps: UploadProps = {
    name: 'file',
    action: '/api/user/avatar',
    headers: {
      Authorization: `Bearer ${localStorage.getItem('user-access-token')}`,
    },
    showUploadList: false,
    beforeUpload: (file) => {
      const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif'
      if (!isJpgOrPng) {
        message.error('只能上传 JPG/PNG/GIF 格式的图片!')
        return false
      }
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isLt2M) {
        message.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    },
    onChange: (info) => {
      if (info.file.status === 'uploading') {
        setLoading(true)
        return
      }
      if (info.file.status === 'done') {
        setLoading(false)
        if (info.file.response && info.file.response.code === 200) {
          const newAvatarUrl = info.file.response.data
          setAvatarUrl(getAvatarUrl(newAvatarUrl))
          form.setFieldValue('avatar', newAvatarUrl)
          
          // Update localStorage userInfo
          const storedUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
          storedUserInfo.avatar = newAvatarUrl
          localStorage.setItem('userInfo', JSON.stringify(storedUserInfo))
          
          message.success('头像上传成功')
        } else {
          message.error('头像上传失败')
        }
      }
      if (info.file.status === 'error') {
        setLoading(false)
        message.error('头像上传失败')
      }
    },
  }

  // Handle unbind kin (for elder users)
  const handleUnbindKin = async (kinId: number) => {
    Modal.confirm({
      title: '确认解绑',
      content: '确定要解绑该家属吗？',
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          const res = await userApi.unbindElderKinRelation(userInfo?.userId!, kinId)
          if (res.code === 200) {
            message.success('解绑成功')
            await fetchKinList()
          } else {
            message.error(res.message || '解绑失败')
          }
        } catch (error) {
          console.error('解绑失败:', error)
          message.error('解绑失败')
        }
      },
    })
  }

  // Handle unbind elder (for kin users)
  const handleUnbindElder = async (elderId: number) => {
    Modal.confirm({
      title: '确认解绑',
      content: '确定要解绑该老人吗？',
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        try {
          const res = await userApi.unbindElderKinRelation(elderId, userInfo?.userId!)
          if (res.code === 200) {
            message.success('解绑成功')
            await fetchElderList()
          } else {
            message.error(res.message || '解绑失败')
          }
        } catch (error) {
          console.error('解绑失败:', error)
          message.error('解绑失败')
        }
      },
    })
  }

  // Initialize on mount
  useEffect(() => {
    initFormData()
  }, [initFormData])

  return (
    <div className="p-6 bg-gray-50 min-h-[calc(100vh-64px-70px)]">
      <Card 
        title="个人信息" 
        className="shadow-md"
        extra={
          <Space>
            {!isEditMode && (
              <Button type="primary" icon={<EditOutlined />} onClick={toggleEditMode}>
                编辑
              </Button>
            )}
            {isEditMode && (
              <>
                <Button onClick={toggleEditMode}>取消</Button>
                <Button 
                  type="primary" 
                  icon={<SaveOutlined />} 
                  onClick={saveProfile}
                  loading={loading}
                >
                  保存
                </Button>
              </>
            )}
          </Space>
        }
      >
        <Form
          form={form}
          layout="vertical"
          disabled={!isEditMode}
        >
          <Tabs activeKey={activeTab} onChange={setActiveTab} type="card">
            {/* Basic Information Tab */}
            <TabPane tab="基本信息" key="basic">
              <Row gutter={20}>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="姓名"
                    name="name"
                    rules={[{ required: true, message: '请输入姓名' }]}
                  >
                    <Input placeholder="请输入姓名" />
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="性别"
                    name="gender"
                    rules={[{ required: true, message: '请选择性别' }]}
                  >
                    <Select placeholder="请选择性别">
                      <Select.Option value="男">男</Select.Option>
                      <Select.Option value="女">女</Select.Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={20}>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="出生日期"
                    name="birthday"
                    rules={[{ required: true, message: '请选择出生日期' }]}
                  >
                    <DatePicker 
                      style={{ width: '100%' }} 
                      placeholder="请选择出生日期"
                      format="YYYY-MM-DD"
                    />
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="身份证号"
                    name="idCard"
                    rules={[
                      { required: true, message: '请输入身份证号' },
                      { 
                        pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, 
                        message: '请输入有效的身份证号' 
                      }
                    ]}
                  >
                    <Input placeholder="请输入身份证号" />
                  </Form.Item>
                </Col>
              </Row>
            </TabPane>

            {/* Contact Information Tab */}
            <TabPane tab="联系信息" key="contact">
              <Row gutter={20}>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="联系电话"
                    name="phone"
                    rules={[
                      { required: true, message: '请输入联系电话' },
                      { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号' }
                    ]}
                  >
                    <Input placeholder="请输入联系电话" />
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="地址"
                    name="address"
                    rules={[{ required: true, message: '请输入地址' }]}
                  >
                    <Input placeholder="请输入地址" />
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={20}>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="紧急联系人"
                    name="emergencyContactName"
                  >
                    <Input placeholder="请输入紧急联系人姓名" />
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12}>
                  <Form.Item
                    label="紧急联系人电话"
                    name="emergencyContactPhone"
                    rules={[
                      { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号' }
                    ]}
                  >
                    <Input placeholder="请输入紧急联系人电话" />
                  </Form.Item>
                </Col>
              </Row>
            </TabPane>

            {/* Other Information Tab */}
            <TabPane tab="其他信息" key="other">
              <Row gutter={20}>
                <Col xs={24} sm={12}>
                  <Form.Item label="头像" name="avatar">
                    <Upload {...uploadProps} disabled={!isEditMode}>
                      <div 
                        style={{
                          width: 178,
                          height: 178,
                          border: '1px dashed #d9d9d9',
                          borderRadius: 8,
                          cursor: isEditMode ? 'pointer' : 'not-allowed',
                          display: 'flex',
                          justifyContent: 'center',
                          alignItems: 'center',
                          overflow: 'hidden',
                        }}
                      >
                        {avatarUrl ? (
                          <Avatar 
                            src={avatarUrl} 
                            size={178} 
                            icon={<UserOutlined />}
                            style={{ width: '100%', height: '100%' }}
                          />
                        ) : (
                          <div style={{ textAlign: 'center' }}>
                            <PlusOutlined style={{ fontSize: 32, color: '#999' }} />
                            <div style={{ marginTop: 8, color: '#999' }}>上传头像</div>
                          </div>
                        )}
                      </div>
                    </Upload>
                    <div style={{ marginTop: 8, fontSize: 12, color: '#999' }}>
                      支持 JPG/PNG/GIF 格式，大小不超过 2MB
                    </div>
                  </Form.Item>
                </Col>
                <Col xs={24} sm={12}>
                  <Form.Item label="个人简介" name="remark">
                    <TextArea 
                      rows={6} 
                      placeholder="请输入个人简介"
                      maxLength={200}
                      showCount
                    />
                  </Form.Item>
                </Col>
              </Row>

              {/* Family Binding Information (Elder Role) */}
              {isElder && (
                <>
                  <Divider orientation={"left" as any}>已绑定家属信息</Divider>
                  <Row gutter={20}>
                    <Col xs={24} sm={12}>
                      <Form.Item label="已绑定家属">
                        {kinListLoading ? (
                          <div className="text-gray-400 text-center py-2 bg-gray-50 rounded">
                            加载中...
                          </div>
                        ) : kinList.length > 0 ? (
                          <Space wrap>
                            {kinList.map(kin => (
                              <Tag 
                                key={kin.id}
                                closable={isEditMode}
                                onClose={() => handleUnbindKin(kin.id)}
                                closeIcon={<CloseOutlined />}
                              >
                                {kin.name}
                              </Tag>
                            ))}
                          </Space>
                        ) : (
                          <div className="text-gray-400 text-center py-2 bg-gray-50 rounded">
                            暂未绑定家属
                          </div>
                        )}
                      </Form.Item>
                    </Col>
                    <Col xs={24} sm={12}>
                      <Form.Item label="与家属关系">
                        <Input 
                          value={kinList.length > 0 ? kinList[0].relationType : ''}
                          disabled 
                        />
                      </Form.Item>
                    </Col>
                  </Row>
                </>
              )}

              {/* Family Binding Information (Kin Role) */}
              {isKin && (
                <>
                  <Divider orientation={"left" as any}>已绑定老人信息</Divider>
                  <Row gutter={20}>
                    <Col xs={24} sm={12}>
                      <Form.Item label="已绑定老人">
                        {elderListLoading ? (
                          <div className="text-gray-400 text-center py-2 bg-gray-50 rounded">
                            加载中...
                          </div>
                        ) : elderList.length > 0 ? (
                          <Space wrap>
                            {elderList.map(elder => (
                              <Tag 
                                key={elder.id}
                                closable={isEditMode}
                                onClose={() => handleUnbindElder(elder.id)}
                                closeIcon={<CloseOutlined />}
                              >
                                {elder.name}
                              </Tag>
                            ))}
                          </Space>
                        ) : (
                          <div className="text-gray-400 text-center py-2 bg-gray-50 rounded">
                            暂未绑定老人
                          </div>
                        )}
                      </Form.Item>
                    </Col>
                    <Col xs={24} sm={12}>
                      <Form.Item label="与老人关系">
                        <Input 
                          value={elderList.length > 0 ? elderList[0].relationType : ''}
                          disabled 
                        />
                      </Form.Item>
                    </Col>
                  </Row>
                </>
              )}
            </TabPane>
          </Tabs>
        </Form>
      </Card>
    </div>
  )
}

export default Profile
