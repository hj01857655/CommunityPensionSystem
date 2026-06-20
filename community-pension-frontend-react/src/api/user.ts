import request from '@/utils/request'
import type { User, UpdateUserRequest } from '@/types/user'
import type { ApiResponse } from '@/types/response'

/**
 * User API
 */
export const userApi = {
  /**
   * Update User Info
   */
  updateUserInfo: (data: UpdateUserRequest) => {
    return request.put<ApiResponse<null>>(`/api/system/user/${data.userId}`, data)
  },

  /**
   * Get unbound elders (for kin to bind)
   */
  getUnboundElders: () => {
    return request.get<ApiResponse<User[]>>('/api/user/elder/unbound')
  },

  /**
   * Get kin list by elder ID
   */
  getKinListByElderId: (elderId: number) => {
    return request.get<ApiResponse<User[]>>(`/api/system/user/kins/${elderId}`)
  },

  /**
   * Get elder list by kin ID
   */
  getElderListByKinId: (kinId: number) => {
    return request.get<ApiResponse<User[]>>(`/api/system/user/elders/${kinId}`)
  },

  /**
   * Bind elder and kin relation
   */
  bindElderKinRelation: (elderId: number, kinId: number, relationType: string) => {
    return request.post<ApiResponse<null>>('/api/user/elder-kin/bind', {
      elderId,
      kinId,
      relationType,
    })
  },

  /**
   * Unbind elder and kin relation
   */
  unbindElderKinRelation: (elderId: number, kinId: number) => {
    return request.post<ApiResponse<null>>('/api/user/elder-kin/unbind', {
      elderId,
      kinId,
    })
  },
}

