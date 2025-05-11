import instance from '@/utils/axios'

/**
 * 获取通知公告列表
 * @param {Object} params - 查询参数
 * @param {number} params.current - 当前页码
 * @param {number} params.size - 每页条数
 * @returns {Promise} - 返回Promise对象
 */
export function getNoticeList(params) {
    return instance.get('/api/notice/list', {
    params
  })
}

/**
 * 获取通知公告详情
 * @param {number} id - 通知公告ID
 * @returns {Promise} - 返回Promise对象
 */
export function getNoticeDetail(id) {
    return instance.get(`/api/notifications/${id}`)
}

// 标记通知为已读
export const markNoticeAsRead = async (id) => {
  try {
    const response = await instance.post(`/api/notice/mark-read/${id}`)
    return response.data
  } catch (error) {
    console.error('标记通知已读失败:', error)
    return { code: 500, message: '标记通知已读失败' }
  }
}