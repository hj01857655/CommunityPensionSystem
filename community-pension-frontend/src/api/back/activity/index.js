import axios from '@/utils/axios'

/**
 * 获取活动列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页条数
 * @param {string} params.title - 活动标题
 * @param {number} params.status - 活动状态
 * @param {string} params.startTime - 开始时间
 * @param {string} params.endTime - 结束时间
 * @returns {Promise<{
 *   code: number,
 *   data: {
 *     records: Array<{
 *       id: number,
 *       title: string,
 *       content: string,
 *       status: number,
 *       startTime: string,
 *       endTime: string,
 *       createTime: string
 *     }>,
 *     total: number
 *   },
 *   msg: string
 * }>}
 */
export const getList = (params) => {
  return axios.get('/api/activity/list', { params })
}

/**
 * 获取活动详情
 * @param {string|number} id - 活动ID
 * @returns {Promise<{
 *   code: number,
 *   data: {
 *     id: number,
 *     title: string,
 *     content: string,
 *     status: number,
 *     startTime: string,
 *     endTime: string,
 *     createTime: string
 *   },
 *   msg: string
 * }>}
 */
export const getDetail = (id) => {
  return axios.get(`/api/activity/${id}`)
}

/**
 * 创建活动
 * @param {Object} data - 活动数据
 * @param {string} data.title - 活动标题
 * @param {number} data.type - 活动类型
 * @param {string} data.content - 活动内容
 * @param {string} data.startTime - 开始时间
 * @param {string} data.endTime - 结束时间
 * @param {number} data.maxParticipants - 最大参与人数
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const create = (data) => {
  return axios.post('/api/activity', data)
}

/**
 * 更新活动
 * @param {string|number} id - 活动ID
 * @param {Object} data - 活动数据
 * @param {string} data.title - 活动标题
 * @param {number} data.type - 活动类型
 * @param {string} data.content - 活动内容
 * @param {string} data.startTime - 开始时间
 * @param {string} data.endTime - 结束时间
 * @param {number} data.maxParticipants - 最大参与人数
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const update = (id, data) => {
  return axios.put(`/api/activity/${id}`, data)
}

/**
 * 删除活动
 * @param {string|number} id - 活动ID
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const deleteActivity = (id) => {
  return axios.delete(`/api/activity/${id}`)
}

/**
 * 更新活动状态
 * @param {string|number} id - 活动ID
 * @param {number} status - 活动状态
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const updateStatus = (id, status) => {
  return axios.put(`/api/activity/${id}/status`, { status })
}

/**
 * 获取活动统计
 * @param {string|number} id - 活动ID
 * @returns {Promise<{
 *   code: number,
 *   data: {
 *     totalParticipants: number,
 *     checkedInCount: number,
 *     pendingCount: number
 *   },
 *   msg: string
 * }>}
 */
export const getStats = (id) => {
  return axios.get(`/api/activity/${id}/stats`)
}

/**
 * 导出活动列表
 * @param {Object} params - 导出参数
 * @returns {Promise<Blob>} Excel文件
 */
export const exportList = (params) => {
  return axios.get('/api/activity/export', { 
    params,
    responseType: 'blob'
  })
} 