import axios from '@/utils/axios';

/**
 * 获取服务项目列表
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} [params.serviceName] - 服务名称
 * @param {string} [params.status] - 状态：0-正常 1-停用
 * @returns {Promise<{code: number, data: {records: Array, total: number}, msg: string}>}
 */
export const getServiceItemList = (params) => {
  return axios.get('/api/service/item/list', { params });
};

/**
 * 获取服务项目详情
 * @param {number} serviceId - 服务项目ID
 * @returns {Promise<{code: number, data: Object, msg: string}>}
 */
export const getServiceItemDetail = (serviceId) => {
  return axios.get(`/api/service/item/${serviceId}`);
};

/**
 * 新增服务项目
 * @param {Object} data - 服务项目数据
 * @param {string} data.serviceName - 服务名称
 * @param {string} data.serviceDescription - 服务描述
 * @param {number} data.servicePrice - 服务价格
 * @param {number} data.serviceDuration - 服务时长
 * @param {string} data.status - 状态：0-正常 1-停用
 * @returns {Promise<{code: number, msg: string}>}
 */
export const createServiceItem = (data) => {
  return axios.post('/api/service/item', data);
};

/**
 * 修改服务项目
 * @param {Object} data - 服务项目数据
 * @param {number} data.serviceId - 服务项目ID
 * @param {string} data.serviceName - 服务名称
 * @param {string} data.serviceDescription - 服务描述
 * @param {number} data.servicePrice - 服务价格
 * @param {number} data.serviceDuration - 服务时长
 * @param {string} data.status - 状态：0-正常 1-停用
 * @returns {Promise<{code: number, msg: string}>}
 */
export const updateServiceItem = (data) => {
  return axios.put('/api/service/item', data);
};

/**
 * 删除服务项目
 * @param {string} serviceIds - 服务项目ID，多个以逗号分隔
 * @returns {Promise<{code: number, msg: string}>}
 */
export const deleteServiceItem = (serviceIds) => {
  return axios.delete(`/api/service/item/${serviceIds}`);
};

/**
 * 导出服务项目列表
 * @param {Object} params - 查询参数，与获取列表接口参数一致
 * @returns {Promise<Blob>} - 返回文件流
 */
export const exportServiceItem = (params) => {
  return axios.get('/api/service/item/export', { 
    params,
    responseType: 'blob'
  });
};

/**
 * 更新服务项目状态
 * @param {Object} data - 状态数据
 * @param {number} data.serviceId - 服务项目ID
 * @param {string} data.status - 状态：0-正常 1-停用
 * @returns {Promise<{code: number, msg: string}>}
 */
export const updateServiceItemStatus = (data) => {
  const { serviceId, status } = data;
  return axios.put(`/api/service/item/${status === '0' ? 'enable' : 'disable'}/${serviceId}`);
}; 