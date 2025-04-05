import axios from '@/utils/axios'

/**
 * 获取报名记录列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页条数
 * @param {string|number} params.activityId - 活动ID
 * @param {string} params.status - 报名状态
 * @param {string} params.name - 报名者姓名
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
 *       applyTime: string,
 *       auditTime: string,
 *       remark: string
 *     }>,
 *     total: number
 *   },
 *   msg: string
 * }>}
 */
export const getList = (params) => {
  return axios.get('/api/activity/register/list', { params })
}

/**
 * 获取报名记录详情
 * @param {string|number} id - 报名记录ID
 * @returns {Promise<{
 *   code: number,
 *   data: {
 *     id: number,
 *     activityId: number,
 *     participantId: number,
 *     status: number,
 *     applyTime: string,
 *     auditTime: string,
 *     remark: string
 *   },
 *   msg: string
 * }>}
 */
export const getDetail = (id) => {
  return axios.get(`/api/activity/register/${id}`)
}

/**
 * 审核报名申请
 * @param {string|number} id - 报名记录ID
 * @param {Object} data - 审核数据
 * @param {number} data.status - 审核状态
 * @param {string} data.remark - 审核备注
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const audit = (id, data) => {
  return axios.put(`/api/activity/register/${id}/audit`, data)
}

/**
 * 批量审核报名申请
 * @param {Object} data - 批量审核数据
 * @param {Array<string|number>} data.ids - 报名记录ID列表
 * @param {number} data.status - 审核状态
 * @param {string} data.remark - 审核备注
 * @returns {Promise<{
 *   code: number,
 *   msg: string
 * }>}
 */
export const batchAudit = (data) => {
  return axios.put('/api/activity/register/batch-audit', data)
}

/**
 * 导出报名记录
 * @param {Object} params - 导出参数
 * @returns {Promise<Blob>} Excel文件
 */
export const exportList = (params) => {
  return axios.get('/api/activity/register/export', { 
    params,
    responseType: 'blob'
  })
}

/**
 * 获取报名统计
 * @param {string|number} activityId - 活动ID
 * @returns {Promise<{
 *   code: number,
 *   data: {
 *     total: number,
 *     approved: number,
 *     rejected: number,
 *     pending: number
 *   },
 *   msg: string
 * }>}
 */
export const getStats = (activityId) => {
  return axios.get(`/api/activity/register/${activityId}/stats`)
}

/**
 * 获取活动报名者列表
 * @param {string|number} activityId - 活动ID
 * @returns {Promise<{
 *   code: number,
 *   data: Array<{
 *     id: number,
 *     name: string,
 *     phone: string,
 *     status: number
 *   }>,
 *   msg: string
 * }>}
 */
export const getParticipants = (activityId) => {
  return axios.get(`/api/activity/register/${activityId}/participants`)
} 