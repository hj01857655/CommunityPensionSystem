import request from '@/utils/request'
import type {
  EmergencyCall,
  SendEmergencyCallRequest,
  EmergencyCallHistoryParams,
} from '@/types/emergency'
import type { ApiResponse } from '@/types/response'

/**
 * Emergency API
 */
export const emergencyApi = {
  /**
   * Send Emergency Call
   */
  sendEmergencyCall: (data: SendEmergencyCallRequest) => {
    return request.post<ApiResponse<EmergencyCall>>('/api/emergency/call', data)
  },

  /**
   * Get Emergency Call History
   */
  getEmergencyCallHistory: (params?: EmergencyCallHistoryParams) => {
    return request.get<ApiResponse<EmergencyCall[]>>('/api/emergency/history', { params })
  },

  /**
   * Cancel Emergency Call
   */
  cancelEmergencyCall: (callId: string) => {
    return request.put<ApiResponse<null>>(`/api/emergency/cancel/${callId}`)
  },
}
