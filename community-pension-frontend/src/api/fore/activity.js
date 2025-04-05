import axios from '@/utils/axios'

/**
 * 活动管理相关接口
 */

/**
 * 获取活动列表
 * @param {Object} [params] - 查询参数
 * @param {number} [params.status] - 活动状态（可选）
 * @returns {Promise<{code: number, data: Array<Object>, msg: string}>}
 */
export const getActivityList = (params = {}) => {
  return axios.get('/api/activity/list', { params })
}

/**
 * 活动报名
 * @param {number} activityId - 活动ID
 * @param {Object} [elderData={}] - 老人相关数据，必须包含elderId
 * @returns {Promise<{code: number, msg: string}>}
 */
export const registerActivity = (activityId, elderData = {}) => {
  // 检查和处理参数
  console.log('API调用: 报名活动', { activityId, elderData });
  
  // 提取elderId参数
  const elderId = elderData.elderId;
  
  if (!elderId) {
    console.error('报名活动参数错误: 缺少elderId', elderData);
    return Promise.reject({ message: '报名活动需要提供老人ID' });
  }
  
  // 使用URL参数发送elderId，这样后端能够正确接收
  return axios.post(`/api/activity/register/${activityId}`, null, {
    params: { elderId }
  });
}

/**
 * 获取活动详情
 * @param {number} activityId - 活动ID
 * @returns {Promise<{code: number, data: Object, msg: string}>}
 */
export const getActivityDetail = (activityId) => {
  return axios.get(`/api/activity/${activityId}`)
}

/**
 * 获取用户活动报名状态
 * @param {number} activityId - 活动ID
 * @returns {Promise<{code: number, data: {status: number}, msg: string}>}
 */
export const getActivityStatus = (activityId) => {
  return axios.get(`/api/activity/status/${activityId}`)
}

/**
 * 获取用户已报名的活动列表
 * @param {Object} [params] - 查询参数
 * @param {number} [params.pageNum=1] - 页码
 * @param {number} [params.pageSize=10] - 每页大小
 * @returns {Promise<{code: number, data: {records: Array<Object>, total: number}, msg: string}>}
 */
export const getUserRegisteredActivities = (params = {}) => {
  return axios.get('/api/activity/register/user/list', { 
    params: {
      pageNum: params.pageNum || 1,
      pageSize: params.pageSize || 10,
      ...params
    }
  })
}

/**
 * 取消活动报名
 * @param {number} registerId - 报名记录ID
 * @param {number} elderId - 老人ID
 * @returns {Promise<{code: number, msg: string}>}
 */
export const cancelActivityRegistration = (registerId, elderId) => {
  return axios.post(`/api/activity/register/cancel/${registerId}`, null, {
    params: { elderId }
  })
} 