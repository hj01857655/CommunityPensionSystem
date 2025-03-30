import axios from '@/utils/axios';

/**
 * 服务评价管理相关接口
 */

/**
 * 分页获取评价列表
 * @param {Object} query - 查询参数
 * @param {number} [query.current=1] - 当前页码
 * @param {number} [query.size=10] - 每页显示条数
 * @param {string} [query.serviceName] - 服务名称（可选）
 * @param {number} [query.rating] - 评分（可选）
 * @param {string} [query.startTime] - 开始时间（可选）
 * @param {string} [query.endTime] - 结束时间（可选）
 * @returns {Promise<{code: number, data: {records: Array<{id: number, serviceName: string, elderName: string, rating: number, content: string, replyContent: string, createTime: string}>, total: number}, msg: string}>}
 */
export const getEvaluationList = query => {
  return axios.get('/api/service/evaluation/list', {
    params: {
      current: query.current || 1,
      size: query.size || 10,
      serviceName: query.serviceName || '',
      rating: query.rating,
      startTime: query.startTime || '',
      endTime: query.endTime || ''
    }
  });
};

/**
 * 回复评价
 * @param {Object} data - 回复数据
 * @param {number} data.id - 评价ID
 * @param {string} data.replyContent - 回复内容
 * @returns {Promise<{code: number, msg: string}>}
 */
export const replyEvaluation = data => {
  return axios.post('/api/service/evaluation/reply', data);
};

/**
 * 删除评价
 * @param {number} id - 评价ID
 * @returns {Promise<{code: number, msg: string}>}
 */
export const deleteEvaluation = id => {
  return axios.delete(`/api/service/evaluation/${id}`);
};

/**
 * 导出评价列表
 * @param {Object} query - 查询参数
 * @returns {Promise<Blob>}
 */
export const exportEvaluation = query => {
  return axios.get('/api/service/evaluation/export', {
    params: query,
    responseType: 'blob'
  });
};