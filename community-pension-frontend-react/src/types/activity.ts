/**
 * Activity Types
 */

export interface Activity {
  id: number
  activityId: number
  title: string
  activityName: string
  activityType: string
  description?: string
  location?: string
  startTime: string
  endTime: string
  maxParticipants?: number
  currentParticipants?: number
  status: number // 0-未开始 1-进行中 2-已结束 3-已取消
  organizer?: string
  contactPhone?: string
  imageUrl?: string
  createTime?: string
  updateTime?: string
}

export interface ActivityRegister {
  registerId: number
  activityId: number
  elderId: number
  elderName?: string
  registerTime: string
  registerType: number // 0-自主报名 1-代理报名
  status: number // 0-待审核 1-已通过 2-已拒绝 3-已取消
  isCheckedIn?: boolean
  checkInTime?: string
}

export interface ActivityCheckIn {
  checkInId: number
  registerId: number
  activityId: number
  elderId: number
  checkInTime: string
  checkInUserId: number
  isProxyCheckIn: number // 0-自助签到 1-代签
  location?: string
}

export interface ActivityListParams {
  status?: number
  activityType?: string
  pageNum?: number
  pageSize?: number
}

export interface RegisteredActivitiesParams {
  pageNum?: number
  pageSize?: number
  status?: number
}
