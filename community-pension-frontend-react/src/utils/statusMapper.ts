/**
 * Status Mapper Utility
 * 
 * Centralizes all status mapping logic to ensure consistency with backend
 * Backend references:
 * - ServiceOrderStatus.java (服务订单状态)
 * - dict_data.activity_status (活动状态)
 */

import { ServiceOrderStatus, ActivityStatus } from '@/types/enums'

/**
 * Service Order Status Mapper
 * Backend: ServiceOrderStatus.java
 * 0-待审核(PENDING) 1-已派单(ASSIGNED) 2-服务中(IN_PROGRESS) 3-已完成(COMPLETED) 4-已取消(CANCELLED) 5-已拒绝(REJECTED)
 */
export const ServiceOrderStatusMapper = {
  // Status to text mapping
  toText: (status: number): string => {
    const map: Record<number, string> = {
      [ServiceOrderStatus.Pending]: '待审核',
      [ServiceOrderStatus.Assigned]: '已派单',
      [ServiceOrderStatus.InProgress]: '服务中',
      [ServiceOrderStatus.Completed]: '已完成',
      [ServiceOrderStatus.Cancelled]: '已取消',
      [ServiceOrderStatus.Rejected]: '已拒绝',
    }
    return map[status] || '未知状态'
  },

  // Status to Ant Design Tag color
  toColor: (status: number): string => {
    const map: Record<number, string> = {
      [ServiceOrderStatus.Pending]: 'default',
      [ServiceOrderStatus.Assigned]: 'success',
      [ServiceOrderStatus.InProgress]: 'processing',
      [ServiceOrderStatus.Completed]: 'success',
      [ServiceOrderStatus.Cancelled]: 'error',
      [ServiceOrderStatus.Rejected]: 'error',
    }
    return map[status] || 'default'
  },

  // Check if order can be cancelled by user
  canCancel: (status: number): boolean => {
    return status === ServiceOrderStatus.Pending || status === ServiceOrderStatus.Assigned
  },

  // Check if order is in active state
  isActive: (status: number): boolean => {
    return (
      status === ServiceOrderStatus.Pending ||
      status === ServiceOrderStatus.Assigned ||
      status === ServiceOrderStatus.InProgress
    )
  },

  // Check if order is completed or closed
  isClosed: (status: number): boolean => {
    return (
      status === ServiceOrderStatus.Completed ||
      status === ServiceOrderStatus.Cancelled ||
      status === ServiceOrderStatus.Rejected
    )
  },
}

/**
 * Activity Status Mapper
 * Backend: dict_data.activity_status
 * 0-筹备中(Preparing) 1-报名中(Registering) 2-进行中(InProgress) 3-已结束(Ended) 4-已取消(Cancelled)
 */
export const ActivityStatusMapper = {
  // Status to text mapping
  toText: (status: number): string => {
    const map: Record<number, string> = {
      [ActivityStatus.Preparing]: '筹备中',
      [ActivityStatus.Registering]: '报名中',
      [ActivityStatus.InProgress]: '进行中',
      [ActivityStatus.Ended]: '已结束',
      [ActivityStatus.Cancelled]: '已取消',
    }
    return map[status] || '未知状态'
  },

  // Status to Ant Design Tag color
  toColor: (status: number): string => {
    const map: Record<number, string> = {
      [ActivityStatus.Preparing]: 'default',
      [ActivityStatus.Registering]: 'processing',
      [ActivityStatus.InProgress]: 'success',
      [ActivityStatus.Ended]: 'default',
      [ActivityStatus.Cancelled]: 'error',
    }
    return map[status] || 'default'
  },

  // Check if activity accepts registration
  canRegister: (status: number): boolean => {
    return status === ActivityStatus.Preparing || status === ActivityStatus.Registering
  },

  // Check if activity is ongoing
  isOngoing: (status: number): boolean => {
    return status === ActivityStatus.InProgress
  },

  // Check if activity can be checked in
  canCheckIn: (status: number): boolean => {
    return status === ActivityStatus.InProgress
  },

  // Check if activity is closed
  isClosed: (status: number): boolean => {
    return status === ActivityStatus.Ended || status === ActivityStatus.Cancelled
  },
}

/**
 * Helper function to get status text from either string or number
 */
export const getServiceStatusText = (status: number | string): string => {
  if (typeof status === 'number') {
    return ServiceOrderStatusMapper.toText(status)
  }
  return status
}

/**
 * Helper function to get activity status text from either string or number
 */
export const getActivityStatusText = (status: number | string): string => {
  if (typeof status === 'number') {
    return ActivityStatusMapper.toText(status)
  }
  return status
}
