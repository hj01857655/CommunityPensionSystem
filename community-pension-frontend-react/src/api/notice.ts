import request from '@/utils/request'
import type { Notice, NoticeListParams } from '@/types/notice'
import type { ApiResponse, PageResponse } from '@/types/response'

/**
 * Notice API
 */
export const noticeApi = {
  /**
   * Get Notice List
   */
  getNoticeList: (params?: NoticeListParams) => {
    return request.get<ApiResponse<PageResponse<Notice>>>('/api/notice/list', { params })
  },

  /**
   * Get Notice Detail
   */
  getNoticeDetail: (noticeId: number) => {
    return request.get<ApiResponse<Notice>>(`/api/notice/${noticeId}`)
  },

  /**
   * Mark Notice as Read
   */
  markNoticeAsRead: (noticeId: number) => {
    return request.post<ApiResponse<null>>(`/api/notice/mark-read/${noticeId}`)
  },
}
