import axios from '@/utils/axios';

/**
 * 获取评价列表
 * @param {Object} params - 查询参数
 * @param {number} [params.serviceItemId] - 服务项目ID
 * @param {number} [params.orderId] - 工单ID
 * @param {number} [params.userId] - 用户ID
 * @param {number} [params.rating] - 评分：1-5星
 * @returns {Promise<{code: number, data: {records: Array, total: number}, msg: string}>}
 */
export const getServiceReviewList = (params) => {
  return axios.get('/api/service/review/list', { params });
};

/**
 * 获取评价详情
 * @param {number} id - 评价ID
 * @returns {Promise<{code: number, data: Object, msg: string}>}
 */
export const getServiceReviewDetail = (id) => {
  return axios.get(`/api/service/review/${id}`);
};

/**
 * 新增评价
 * @param {Object} data - 评价数据
 * @param {number} data.orderId - 工单ID
 * @param {number} data.userId - 用户ID
 * @param {number} data.rating - 评分：1-5星
 * @param {string} data.content - 评价内容
 * @returns {Promise<{code: number, msg: string}>}
 */
export const createServiceReview = (data) => {
  return axios.post('/api/service/review', data);
};

/**
 * 回复评价
 * @param {Object} data - 回复数据
 * @param {number} data.id - 评价ID
 * @param {string} data.replyContent - 回复内容
 * @returns {Promise<{code: number, msg: string}>}
 */
export const replyServiceReview = (data) => {
  const { id, replyContent } = data;
  return axios.put(`/api/service/review/${id}/reply`, {
    replyContent
  });
};

/**
 * 获取服务项目平均评分
 * @param {number} serviceId - 服务项目ID
 * @returns {Promise<{code: number, data: number, msg: string}>}
 */
export const getServiceAverageRating = (serviceId) => {
  return axios.get(`/api/service/review/average/${serviceId}`);
};

/**
 * 检查用户是否已评价
 * @param {Object} params - 查询参数
 * @param {number} params.orderId - 工单ID
 * @param {number} params.userId - 用户ID
 * @returns {Promise<{code: number, data: boolean, msg: string}>}
 */
export const checkServiceReview = (params) => {
  return axios.get('/api/service/review/check', { params });
};

/**
 * 导出评价数据
 * @param {Object} params - 查询参数，与获取列表接口参数一致
 * @returns {Promise<Blob>} - 返回文件流
 */
export const exportServiceReview = (params) => {
  return axios.get('/api/service/review/export', {
    params,
    responseType: 'blob'
  });
}; 