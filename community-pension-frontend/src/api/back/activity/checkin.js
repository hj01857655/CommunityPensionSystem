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
  return axios.get('/api/activity/check-in/list', { params })
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
  return axios.get(`/api/activity/check-in/register/${id}`)
}

/**
 * 签到
 * @param {Object} data - 签到数据
 * @param {string|number} data.registerId - 报名记录ID
 * @param {string|number} data.checkInUserId - 签到人ID
 * @param {number} data.isProxyCheckIn - 是否代签：0-本人签到，1-他人代签
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const create = (data) => {
  return axios.post('/api/activity/check-in', data)
}

/**
 * 批量签到
 * @param {Object} data - 批量签到数据
 * @param {Array<string|number>} data.registerIds - 报名记录ID列表
 * @param {string|number} data.checkInUserId - 签到人ID
 * @param {number} data.isProxyCheckIn - 是否代签：0-本人签到，1-他人代签
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const batchCreate = (data) => {
  return axios.post('/api/activity/check-in/batch', data.registerIds, {
    params: {
      checkInUserId: data.checkInUserId,
      isProxyCheckIn: data.isProxyCheckIn
    }
  })
}

/**
 * 签退
 * @param {string|number} registerId - 报名记录ID
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const signOut = (registerId) => {
  return axios.post(`/api/activity/check-in/signout/${registerId}`)
}

/**
 * 导出签到记录
 * @param {Object} params - 导出参数
 * @returns {Promise<Blob>} Excel文件
 */
export const exportList = (params) => {
  return axios.get('/api/activity/check-in/export', { 
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
 *     activityId: number,
 *     activityTitle: string,
 *     approvedCount: number,
 *     checkedInCount: number,
 *     notCheckedInCount: number,
 *     checkInRate: string
 *   },
 *   msg: string
 * }>}
 */
export const getStats = (activityId) => {
  return axios.get(`/api/activity/check-in/stats/${activityId}`)
}

/**
 * 检查老人是否已签到
 * @param {string|number} activityId - 活动ID
 * @param {string|number} elderId - 老人ID
 * @returns {Promise<{
 *   code: number,
 *   data: boolean,
 *   msg: string
 * }>}
 */
export const checkElderCheckedIn = (activityId, elderId) => {
  return axios.get('/api/activity/check-in/check', {
    params: {
      activityId,
      elderId
    }
  })
}

/**
 * 获取老人对特定活动的报名ID
 * @param {string|number} activityId - 活动ID
 * @param {string|number} elderId - 老人ID
 * @returns {Promise<{
 *   code: number,
 *   data: number,
 *   msg: string
 * }>}
 */
export const getRegisterIdByActivityAndElder = (activityId, elderId) => {
  return axios.get('/api/activity/check-in/register-id', {
    params: {
      activityId,
      elderId
    }
  })
} 