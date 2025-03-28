import axios from '@/utils/axios';

/**
 * 获取工单列表
 * @param {Object} params - 查询参数
 * @param {string} [params.userId] - 用户ID
 * @param {string} [params.serviceItemId] - 服务项目ID
 * @param {string} [params.status] - 状态：0-待审核 1-已派单 2-服务中 3-已完成
 * @param {string} [params.startTime] - 开始时间
 * @param {string} [params.endTime] - 结束时间
 * @returns {Promise<{code: number, data: {records: Array, total: number}, msg: string}>}
 */
export const getServiceOrderList = (params) => {
  return axios.get('/api/service/order/list', { params });
};

/**
 * 获取工单详情
 * @param {number} id - 工单ID
 * @returns {Promise<{code: number, data: Object, msg: string}>}
 */
export const getServiceOrderDetail = (id) => {
  return axios.get(`/api/service/order/${id}`);
};

/**
 * 新增工单
 * @param {Object} data - 工单数据
 * @param {number} data.userId - 用户ID
 * @param {number} data.serviceItemId - 服务项目ID
 * @param {string} data.applyReason - 申请原因
 * @param {string} data.scheduleTime - 预约时间
 * @returns {Promise<{code: number, msg: string}>}
 */
export const createServiceOrder = (data) => {
  return axios.post('/api/service/order', data);
};

/**
 * 审核工单
 * @param {Object} data - 审核数据
 * @param {number} data.id - 工单ID
 * @param {number} data.status - 状态：1-通过 2-拒绝
 * @param {string} [data.reviewRemark] - 审核备注
 * @returns {Promise<{code: number, msg: string}>}
 */
export const reviewServiceOrder = (data) => {
  const { id, status, reviewRemark } = data;
  return axios.put(`/api/service/order/${id}/review`, {
    status,
    reviewRemark
  });
};

/**
 * 派单
 * @param {number} id - 工单ID
 * @returns {Promise<{code: number, msg: string}>}
 */
export const assignServiceOrder = (id) => {
  return axios.put(`/api/service/order/${id}/assign`);
};

/**
 * 开始服务
 * @param {number} id - 工单ID
 * @returns {Promise<{code: number, msg: string}>}
 */
export const startServiceOrder = (id) => {
  return axios.put(`/api/service/order/${id}/start`);
};

/**
 * 完成服务
 * @param {Object} data - 完成数据
 * @param {number} data.id - 工单ID
 * @param {number} data.actualDuration - 实际服务时长
 * @returns {Promise<{code: number, msg: string}>}
 */
export const completeServiceOrder = (data) => {
  const { id, actualDuration } = data;
  return axios.put(`/api/service/order/${id}/complete`, {
    actualDuration
  });
};

/**
 * 导出工单
 * @param {Object} params - 查询参数，与获取列表接口参数一致
 * @returns {Promise<Blob>} - 返回文件流
 */
export const exportServiceOrder = (params) => {
  return axios.get('/api/service/order/export', {
    params,
    responseType: 'blob'
  });
}; 