import axios from '@/utils/axios';

/**
 * 服务评价管理相关接口 (ServiceReviewController)
 */

/**
 * 分页获取评价列表
 * @param {Object} query - 查询参数
 * @param {number} [query.serviceId] - 服务ID
 * @param {number} [query.status] - 评价状态
 * @param {number} [query.current=1] - 当前页码 (对应后端 pageNum)
 * @param {number} [query.size=10] - 每页显示条数 (对应后端 pageSize)
 * @returns {Promise<{code: number, data: {records: Array, total: number}, msg: string}>}
 */
export const getServiceReviewList = (query) => {
  const params = {
    pageNum: query.current || 1,
    pageSize: query.size || 10
  };
  if (query.serviceId !== null && query.serviceId !== undefined) {
    params.serviceId = query.serviceId;
  }
  if (query.status !== null && query.status !== undefined && query.status !== '') {
    params.status = query.status;
  }
  if (query.userName) params.userName = query.userName;
  if (query.serviceName) params.serviceName = query.serviceName;
  if (query.score) params.score = query.score;
  if (query.beginTime) params.beginTime = query.beginTime;
  if (query.endTime) params.endTime = query.endTime;
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
 * 新增评价 (对应 addServiceReview)
 * @param {Object} data - 评价数据 (应符合 ServiceReviewDTO 结构)
 * @param {number} data.serviceAppointmentId - 服务预约ID
 * @param {number} data.elderId - 老人ID
 * @param {number} data.rating - 评分
 * @param {string} data.content - 内容
 * @returns {Promise<{code: number, data: number, msg: string}>}
 */
export const createServiceReview = (data) => {
  // 后端需要 ServiceReviewDTO
  return axios.post('/api/service/review', data);
};

/**
 * 回复评价 (对应 replyServiceReview)
 * @param {Object} data - 回复数据 (应符合 ServiceReviewReplyDTO 结构)
 * @param {number} data.id - 评价ID
 * @param {string} data.replyContent - 回复内容
 * @returns {Promise<{code: number, msg: string}>}
 */
export const replyServiceReview = (data) => {
  // 后端需要 ServiceReviewReplyDTO 作为 RequestBody
  return axios.post('/api/service/review/reply', data);
};

/**
 * 审核评价 (对应 auditServiceReview)
 * @param {Object} data - 审核数据 (应符合 ServiceReviewAuditDTO 结构)
 * @param {number} data.id - 评价ID
 * @param {number} data.status - 审核状态
 * @param {string} [data.auditReason] - 审核理由
 * @returns {Promise<{code: number, msg: string}>}
 */
export const auditServiceReview = (data) => {
  // 后端需要 ServiceReviewAuditDTO 作为 RequestBody
  return axios.post('/api/service/review/audit', data);
};

/**
 * 删除评价 (对应 deleteServiceReview)
 * @param {number} id - 评价ID
 * @returns {Promise<{code: number, msg: string}>}
 */
export const deleteServiceReview = (id) => {
  return axios.delete(`/api/service/review/${id}`);
};


// --- 以下是 review.js 原有但后端 ServiceReviewController 中不直接对应或参数不符的函数，需要确认是否保留或修改 ---

/**
 * 获取服务项目平均评分 (后端无此接口 /api/service/review/average/{serviceId})
 * @param {number} serviceId - 服务项目ID
 * @returns {Promise<{code: number, data: number, msg: string}>}
 */
// export const getServiceAverageRating = (serviceId) => {
//   return axios.get(`/api/service/review/average/${serviceId}`);
// };

/**
 * 检查用户是否已评价 (对应 checkReviewExists)
 * @param {Object} params - 查询参数
 * @param {number} params.serviceAppointmentId - 服务预约ID (后端需要 serviceAppointmentId)
 * @param {number} params.elderId - 老人ID (后端需要 elderId)
 * @returns {Promise<{code: number, data: boolean, msg: string}>}
 */
export const checkServiceReview = (params) => {
  // 确认后端参数名为 serviceAppointmentId 和 elderId
  return axios.get('/api/service/review/check', { params });
};

/**
 * 导出评价数据 (后端无此接口)
 * @param {Object} params - 查询参数
 * @returns {Promise<Blob>} - 返回文件流
 */
// export const exportServiceReview = (params) => {
//   return axios.get('/api/service/review/export', {
//     params,
//     responseType: 'blob'
//   });
// }; 