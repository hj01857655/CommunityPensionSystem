import request from '@/utils/request'
import type {
  Activity,
  ActivityRegister,
  ActivityListParams,
  RegisteredActivitiesParams,
} from '@/types/activity'
import type { ApiResponse, PageResponse } from '@/types/response'
import { ActivityRegisterType } from '@/types/enums'

/**
 * Activity API
 */
export const activityApi = {
  /**
   * Get Activity List
   */
  getActivityList: (params?: ActivityListParams) => {
    return request.get<ApiResponse<Activity[]>>('/api/activity/list', { params })
  },

  /**
   * Get Activity Detail
   */
  getActivityDetail: (activityId: number) => {
    return request.get<ApiResponse<Activity>>(`/api/activity/${activityId}`)
  },

  /**
   * Register for Activity
   */
  registerActivity: (
    activityId: number,
    elderId: number,
    registerType: ActivityRegisterType = ActivityRegisterType.Self
  ) => {
    return request.post<ApiResponse<null>>(
      `/api/activity/register/${activityId}`,
      null,
      { params: { elderId, registerType } }
    )
  },

  /**
   * Get Activity Registration Status
   */
  getActivityStatus: async (activityId: number, elderId?: number) => {
    try {
      const response = await request.get<ApiResponse<{
        status: number
        isRegistered: boolean
        registerId?: number
      }>>(
        `/api/activity/register/status/${activityId}`,
        { params: elderId ? { elderId } : undefined }
      )
      return response
    } catch (error: any) {
      // 404 means not registered
      if (error?.response?.status === 404) {
        return {
          code: 200,
          data: {
            status: -1,
            isRegistered: false,
          },
          message: '未报名',
        }
      }
      throw error
    }
  },

  /**
   * Get User Registered Activities
   */
  getUserRegisteredActivities: (params?: RegisteredActivitiesParams) => {
    return request.get<ApiResponse<PageResponse<ActivityRegister>>>(
      '/api/activity/register/user/list',
      {
        params: {
          pageNum: params?.pageNum || 1,
          pageSize: params?.pageSize || 10,
          ...params,
        },
      }
    )
  },

  /**
   * Cancel Activity Registration
   */
  cancelActivityRegistration: (registerId: number, elderId: number) => {
    return request.post<ApiResponse<null>>(
      `/api/activity/register/cancel/${registerId}`,
      null,
      { params: { elderId } }
    )
  },

  /**
   * Self Check-in to Activity
   * Combines two API calls: get register ID + check in
   */
  selfCheckIn: async (activityId: number, elderId: number, location?: string) => {
    // Step 1: Get register ID
    const registerIdRes = await request.get<ApiResponse<number>>(
      '/api/activity/check-in/register-id',
      { params: { activityId, elderId } }
    )

    if (registerIdRes.code !== 200 || !registerIdRes.data) {
      throw new Error('未找到报名记录')
    }

    // Step 2: Check in
    return request.post<ApiResponse<null>>('/api/activity/check-in', {
      registerId: registerIdRes.data,
      checkInUserId: elderId,
      isProxyCheckIn: 0,
      location,
    })
  },

  /**
   * Get Check-in Status
   */
  getCheckInStatus: async (activityId: number, elderId: number) => {
    const response = await request.get<ApiResponse<boolean>>(
      '/api/activity/check-in/check',
      { params: { activityId, elderId } }
    )

    return {
      ...response,
      data: {
        isCheckedIn: response.data,
        checkInTime: response.data ? new Date().toISOString() : undefined,
      },
    }
  },
}
