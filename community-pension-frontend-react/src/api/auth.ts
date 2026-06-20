import request from '@/utils/request'
import type { User, LoginRequest, LoginResponse, UpdateUserRequest } from '@/types/user'
import type { ApiResponse } from '@/types/response'

/**
 * Auth API
 */
export const authApi = {
  /**
   * User Login (Front Portal)
   */
  login: (data: LoginRequest) => {
    return request.post<ApiResponse<LoginResponse>>('/api/auth/login', data)
  },

  /**
   * Admin Login (Admin Portal)
   */
  adminLogin: (data: LoginRequest) => {
    return request.post<ApiResponse<LoginResponse>>('/api/auth/adminLogin', data)
  },

  /**
   * User Logout
   */
  logout: () => {
    return request.post<ApiResponse<null>>('/api/auth/logout')
  },

  /**
   * Refresh Access Token
   */
  refresh: () => {
    return request.post<ApiResponse<{ accessToken: string }>>('/api/auth/refresh')
  },

  /**
   * Get Current User Info
   */
  getUserInfo: (userId: number) => {
    return request.get<ApiResponse<{ user: User }>>(`/api/system/user/${userId}`)
  },

  /**
   * Update Password
   */
  updatePassword: (data: {
    oldPassword: string
    newPassword: string
  }) => {
    return request.put<ApiResponse<null>>('/api/auth/password', data)
  },

  /**
   * Get Captcha
   */
  getCaptcha: () => {
    return request.get<ApiResponse<{ captchaId: string; captchaImage: string }>>('/api/auth/captcha')
  },
}

/**
 * User API
 */
export const userApi = {
  /**
   * Update User Info
   */
  updateUserInfo: (data: UpdateUserRequest) => {
    return request.put<ApiResponse<User>>(`/api/system/user/${data.userId}`, data)
  },

  /**
   * Get Unbound Elders
   */
  getUnboundElders: () => {
    return request.get<ApiResponse<User[]>>('/api/user/elder/unbound')
  },

  /**
   * Get Kin List by Elder ID
   */
  getKinListByElderId: (elderId: number) => {
    return request.get<ApiResponse<User[]>>(`/api/system/user/kins/${elderId}`)
  },

  /**
   * Get Kin IDs by Elder ID
   */
  getKinIdsByElderId: (elderId: number) => {
    return request.get<ApiResponse<number[]>>(`/api/user/kin-ids/${elderId}`)
  },

  /**
   * Bind Elder-Kin Relation
   */
  bindElderKinRelation: (data: { elderId: number; kinId: number; relationType: string }) => {
    return request.post<ApiResponse<null>>('/api/user/elder-kin/bind', data)
  },

  /**
   * Unbind Elder-Kin Relation
   */
  unbindElderKinRelation: (data: { elderId: number; kinId: number }) => {
    return request.post<ApiResponse<null>>('/api/user/elder-kin/unbind', data)
  },
}
