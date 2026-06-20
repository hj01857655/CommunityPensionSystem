/**
 * Emergency Types
 */

export interface EmergencyCall {
  callId: string
  userId: number
  userName: string
  userPhone?: string
  location?: string
  callTime: string
  status: EmergencyCallStatus
  responseTime?: string
  handlerId?: number
  handlerName?: string
  resolveTime?: string
  notes?: string
  createTime?: string
  updateTime?: string
}

export enum EmergencyCallStatus {
  Pending = 0,
  Processing = 1,
  Resolved = 2,
  Cancelled = 3,
}

export interface SendEmergencyCallRequest {
  userId: number
  userName?: string
  userPhone?: string
  location?: string
  message?: string
}

export interface EmergencyCallHistoryParams {
  userId?: number
  status?: EmergencyCallStatus
  startTime?: string
  endTime?: string
  pageNum?: number
  pageSize?: number
}
