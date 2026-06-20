/**
 * Notice Types
 */

export interface Notice {
  id: number
  noticeId: number
  title: string
  content: string
  noticeType: string // 通知类型：系统通知、活动通知等
  publisherId?: number
  publisherName?: string
  publishTime: string
  isRead?: boolean
  targetUserType?: string // 目标用户类型
  priority?: number // 优先级
  createTime?: string
  updateTime?: string
}

export interface NoticeListParams {
  current?: number
  size?: number
  pageNum?: number
  pageSize?: number
  noticeType?: string
  isRead?: boolean
}
