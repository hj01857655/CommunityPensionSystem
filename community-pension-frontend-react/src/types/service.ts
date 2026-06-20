/**
 * Service Types
 */

export interface ServiceItem {
  id: number // serviceItemId的别名
  serviceItemId: number
  serviceName: string
  categoryId?: number
  categoryName?: string
  serviceType?: number // 服务类型ID
  serviceTypeName?: string // 服务类型名称
  description?: string
  price?: number
  unit?: string // 服务单位：次、小时等
  duration?: number // 服务时长(分钟)
  status: number // 0-正常 1-停用
  imageUrl?: string
  providerId?: number
  providerName?: string
  createTime?: string
  updateTime?: string
}

export interface ServiceCategory {
  categoryId: number
  categoryName: string
  description?: string
  sort?: number
}

export interface ServiceOrder {
  id: number
  orderId: number
  orderNo?: string
  serviceItemId: number
  serviceItemName?: string
  serviceTypeName?: string // 服务类型名称
  serviceFee?: number // 服务费用
  serviceDuration?: number // 服务时长
  userId: number
  userName?: string
  scheduleTime: string
  applyReason: string
  status: number // 0-待审核 1-已接单 2-服务中 3-已完成 4-已取消 5-已拒绝
  statusText?: string
  staffId?: number
  staffName?: string
  actualStartTime?: string
  actualEndTime?: string
  feedback?: string
  rating?: number
  cancelReason?: string
  statusUpdateTime?: string // 状态更新时间
  createTime?: string
  updateTime?: string
}

export interface ServiceListParams {
  current?: number
  pageNum?: number // 页码
  size?: number
  pageSize?: number // 每页大小
  serviceName?: string
  status?: number
  categoryId?: number
}

export interface CreateServiceOrderRequest {
  serviceItemId: number
  userId: number
  scheduleTime: string
  applyReason: string
}

export interface MyAppointmentsParams {
  userId: number
  pageNum?: number
  pageSize?: number
  status?: number
  startTime?: string
  endTime?: string
}
