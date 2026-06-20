import request from '@/utils/request'
import type {
  HealthRecord,
  AddHealthRecordRequest,
  UpdateHealthRecordRequest,
} from '@/types/health'
import type { ApiResponse } from '@/types/response'
import { calculateBMI, formatDateTime } from '@/utils/format'

/**
 * Health API
 */
export const healthApi = {
  /**
   * Get Health Records
   */
  getHealthRecords: (elderId: number) => {
    return request.get<ApiResponse<HealthRecord[]>>('/api/health/record/getHealthRecords', {
      params: { elderId },
    })
  },

  /**
   * Add Health Record
   */
  addHealthRecord: (data: AddHealthRecordRequest) => {
    const recordData: AddHealthRecordRequest = {
      ...data,
      // Auto-calculate BMI if height and weight provided
      bmi: data.height && data.weight ? calculateBMI(data.weight, data.height) : data.bmi,
      // Auto-generate timestamp if not provided
      recordTime: data.recordTime || formatDateTime(new Date()),
      symptomsRecordTime: data.symptomsRecordTime || formatDateTime(new Date()),
    }

    return request.post<ApiResponse<HealthRecord>>(
      '/api/health/record/addHealthRecords',
      recordData
    )
  },

  /**
   * Update Health Record
   */
  updateHealthRecord: (data: UpdateHealthRecordRequest) => {
    const recordData: UpdateHealthRecordRequest = {
      ...data,
      // Auto-calculate BMI if height and weight provided
      bmi: data.height && data.weight ? calculateBMI(data.weight, data.height) : data.bmi,
    }

    return request.put<ApiResponse<HealthRecord>>(
      '/api/health/record/updateHealthRecords',
      recordData
    )
  },

  /**
   * Check if Health Record Exists
   */
  checkHealthRecordExists: async (elderId: number): Promise<boolean> => {
    try {
      const response = await healthApi.getHealthRecords(elderId)
      return response.code === 200 && response.data != null && response.data.length > 0
    } catch (error: any) {
      if (error?.response?.status === 404) {
        return false
      }
      throw error
    }
  },
}
