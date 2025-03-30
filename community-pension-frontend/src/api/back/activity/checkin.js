import axios from '@/utils/axios'

/**
 * 获取签到记录列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页条数
 * @param {string|number} params.activityId - 活动ID
 * @param {string} params.status - 签到状态
 * @param {string} params.name - 参与者姓名
 * @param {string} params.phone - 联系电话
 * @param {string} params.startTime - 开始时间
 * @param {string} params.endTime - 结束时间
 * @returns {Promise<{
 *   code: number,
 *   data: {
 *     records: Array<{
 *       id: number,
 *       activityId: number,
 *       participantId: number,
 *       status: number,
 *       checkinTime: string,
 *       remark: string
 *     }>,
 *     total: number
 *   },
 *   msg: string
 * }>}
 */
export const getList = (params) => {
  return axios.get('/api/activity/checkin/list', { params })
}

/**
 * 获取签到记录详情
 * @param {string|number} id - 签到记录ID
 * @returns {Promise<{
 *   code: number,
 *   data: {
 *     id: number,
 *     activityId: number,
 *     participantId: number,
 *     status: number,
 *     checkinTime: string,
 *     remark: string
 *   },
 *   msg: string
 * }>}
 */
export const getDetail = (id) => {
  return axios.get(`/api/activity/checkin/${id}`)
}

/**
 * 签到
 * @param {Object} data - 签到数据
 * @param {string|number} data.activityId - 活动ID
 * @param {string|number} data.participantId - 参与者ID
 * @param {string} data.checkinTime - 签到时间
 * @param {string} data.remark - 签到备注
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const create = (data) => {
  return axios.post('/api/activity/checkin', data)
}

/**
 * 批量签到
 * @param {Object} data - 批量签到数据
 * @param {string|number} data.activityId - 活动ID
 * @param {Array<string|number>} data.participantIds - 参与者ID列表
 * @param {string} data.checkinTime - 签到时间
 * @param {string} data.remark - 签到备注
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const batchCreate = (data) => {
  return axios.post('/api/activity/checkin/batch', data)
}

/**
 * 取消签到
 * @param {string|number} id - 签到记录ID
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const cancel = (id) => {
  return axios.put(`/api/activity/checkin/${id}/cancel`)
}

/**
 * 导出签到记录
 * @param {Object} params - 导出参数
 * @returns {Promise<Blob>} Excel文件
 */
export const exportList = (params) => {
  return axios.get('/api/activity/checkin/export', { 
    params,
    responseType: 'blob'
  })
}

/**
 * 获取签到统计
 * @param {string|number} activityId - 活动ID
 * @returns {Promise<{
 *   code: number,
 *   data: {
 *     total: number,
 *     checkedIn: number,
 *     notCheckedIn: number
 *   },
 *   msg: string
 * }>}
 */
export const getStats = (activityId) => {
  return axios.get(`/api/activity/checkin/${activityId}/stats`)
}

/**
 * 获取活动签到记录
 * @param {string|number} activityId - 活动ID
 * @returns {Promise<{
 *   code: number,
 *   data: Array<{
 *     id: number,
 *     participantId: number,
 *     name: string,
 *     phone: string,
 *     status: number,
 *     checkinTime: string
 *   }>,
 *   msg: string
 * }>}
 */
export const getCheckins = (activityId) => {
  return axios.get(`/api/activity/checkin/${activityId}/checkins`)
} 