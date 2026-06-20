import request from '@/utils/request'
import type {
  ServiceItem,
  ServiceCategory,
  ServiceOrder,
  ServiceListParams,
  CreateServiceOrderRequest,
  MyAppointmentsParams,
} from '@/types/service'
import type { ApiResponse, PageResponse } from '@/types/response'

/**
 * Service API
 */
export const serviceApi = {
  /**
   * Get Service List
   */
  getServiceList: (params?: ServiceListParams) => {
    return request.get<ApiResponse<PageResponse<ServiceItem>>>('/api/service/item/list', {
      params,
    })
  },

  /**
   * Get Service Detail
   */
  getServiceDetail: (serviceId: number) => {
    return request.get<ApiResponse<ServiceItem>>(`/api/service/item/${serviceId}`)
  },

  /**
   * Get Service Categories
   */
  getServiceCategories: () => {
    return request.get<ApiResponse<ServiceCategory[]>>('/api/service/categories')
  },

  /**
   * Create Service Appointment
   */
  createAppointment: (data: CreateServiceOrderRequest) => {
    return request.post<ApiResponse<null>>('/api/service/order', data)
  },

  /**
   * Get My Appointments
   */
  getMyAppointments: (params: MyAppointmentsParams) => {
    const { userId, ...queryParams } = params
    return request.get<ApiResponse<ServiceOrder[]>>(`/api/service/order/user/${userId}`, {
      params: {
        ...queryParams,
        pageNum: params.pageNum || 1,
        pageSize: params.pageSize || 10,
      },
    })
  },

  /**
   * Cancel Appointment
   */
  cancelAppointment: (orderId: number, reason?: string) => {
    return request.post<ApiResponse<null>>(
      `/api/service/order/${orderId}/cancel`,
      null,
      { params: reason ? { reason } : undefined }
    )
  },
}
