import axios from '@/utils/axios';

/**
 * 发送紧急呼叫
 * @param {Object} data - 紧急呼叫数据
 * @param {number} data.userId - 用户ID (必须是数字类型)
 * @param {string} data.userName - 用户姓名
 * @returns {Promise} 请求结果
 */
export const sendEmergencyCall = (data) => {
  return axios.post('/api/emergency/call', data)
    .catch(error => {
      console.error('发送紧急呼叫失败:', error);
      return Promise.reject(error);
    });
};

/**
 * 获取紧急呼叫历史记录
 * @param {Object} params - 查询参数
 * @returns {Promise} 请求结果
 */
export const getEmergencyCallHistory = (params) => {
  return axios.get('/api/emergency/history', { params })
    .catch(error => {
      console.error('获取紧急呼叫历史记录失败:', error);
      return Promise.reject(error);
    });
};

/**
 * 取消紧急呼叫
 * @param {string} callId - 呼叫ID
 * @returns {Promise} 请求结果
 */
export const cancelEmergencyCall = (callId) => {
  return axios.put(`/api/emergency/cancel/${callId}`)
    .catch(error => {
      console.error('取消紧急呼叫失败:', error);
      return Promise.reject(error);
    });
};
