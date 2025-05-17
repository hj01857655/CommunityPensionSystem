import axios from '@/utils/axios';

/**
 * 服务预约相关接口
 */

/**
 * 获取服务项目列表
 * @param {Object} params - 查询参数
 * @param {number} [params.current=1] - 当前页码
 * @param {number} [params.size=10] - 每页条数
 * @param {string} [params.serviceName] - 服务名称
 * @param {number} [params.status] - 状态：0-正常 1-停用
 * @returns {Promise<{code: number, data: {records: Array<ServiceItemVO>, total: number}, msg: string}>}
 */
export const getServiceList = async (params) => {
  const res = await axios.get('/api/service/item/list', { params })
    .catch(error => {
      console.error('Error fetching service list:', error);
      return Promise.reject(error);
    });
  return res;
};

/**
 * 获取服务项目详情
 * @param {number} serviceId - 服务项目ID
 * @returns {Promise<{code: number, data: ServiceItemVO, msg: string}>}
 */
export const getServiceDetail =async (serviceId) => {
  return axios.get(`/api/service/item/${serviceId}`)
    .catch(error => {
      console.error('Error fetching service detail:', error);
      return Promise.reject(error);
    });
};

/**
 * 获取服务类别列表
 * @returns {Promise<{code: number, data: Array, msg: string}>}
 */
export const getServiceCategories = async() => {
  return axios.get('/api/service/categories')
    .catch(error => {
      console.error('Error fetching service categories:', error);
      return Promise.reject(error);
    });
};

/**
 * 创建服务预约
 * @param {Object} data - 预约信息
 * @param {number} data.serviceItemId - 服务项目ID
 * @param {number} data.userId - 预约用户ID
 * @param {string} data.scheduleTime - 预约时间（格式：yyyy-MM-ddTHH:mm:ss）
 * @param {string} data.applyReason - 申请原因（长度5-500字符）
 * @returns {Promise<{code: number, msg: string}>}
 */
export const createAppointment = async (data) => {
  return axios.post('/api/service/order', data)
    .catch(error => {
      console.error('Error creating appointment:', error);
      return Promise.reject(error);
    });
};

/**
 * 获取我的预约列表
 * @param {Object} params - 查询参数
 * @param {number} params.userId - 用户ID
 * @param {number} [params.pageNum=1] - 当前页码
 * @param {number} [params.pageSize=10] - 每页条数
 * @param {number} [params.status] - 预约状态
 * @param {string} [params.startTime] - 开始时间
 * @param {string} [params.endTime] - 结束时间
 * @returns {Promise<{code: number, data: Array, message: string}>}
 */
export const getMyAppointments = async (params) => {
  const { userId, ...queryParams } = params;
  return axios.get(`/api/service/order/user/${userId}`, { 
    params: {
      ...queryParams,
      pageNum: params.pageNum || 1,
      pageSize: params.pageSize || 10
    }
  }).catch(error => {
    return Promise.reject(error);
  });
};

/**
 * 取消预约
 * @param {number} orderId - 预约ID
 * @param {string} [reason] - 取消原因
 * @returns {Promise<{code: number, msg: string}>}
 */
export const cancelAppointment = async (orderId, reason) => {
  return axios.post(`/api/service/order/${orderId}/cancel`, null, { params: { reason } })
    .catch(error => {
      console.error('Error canceling appointment:', error);
      return Promise.reject(error);
    });
};

/**
 * 评价服务
 * @param {number} orderId - 预约ID
 * @param {Object} data - 评价数据
 * @param {number} data.rating - 评分（1-5）
 * @param {string} data.content - 评价内容
 * @returns {Promise<{code: number, msg: string}>}
 */
export const evaluateService = async (orderId, data) => {
  return axios.post(`/api/service/order/${orderId}/evaluate`, data)
    .catch(error => {
      console.error('Error evaluating service:', error);
      return Promise.reject(error);
    });
};