import axios from '@/utils/axios';

/**
 * 获取服务项目列表
 * @param {Object} params - 查询参数
 * @param {number} params.current - 当前页码
 * @param {number} params.size - 每页数量
 * @param {string} [params.serviceName] - 服务名称
 * @param {string} [params.status] - 状态：0-正常 1-停用
 * @returns {Promise<{code: number, data: {records: Array, total: number}, msg: string}>}
 */
export const getList = (params) => {
  return axios.get('/api/service/item/list', { params });
};

/**
 * 获取服务项目详情
 * @param {number} id - 服务项目ID
 * @returns {Promise<{code: number, data: Object, msg: string}>}
 */
export const getDetail = (id) => {
  return axios.get(`/api/service/item/${id}`);
};

/**
 * 新增服务项目
 * @param {Object} data - 服务项目数据
 * @param {string} data.serviceName - 服务名称
 * @param {string} data.description - 服务描述
 * @param {number} data.price - 服务价格
 * @param {number} data.duration - 服务时长
 * @param {string} data.status - 状态：0-正常 1-停用
 * @returns {Promise<{code: number, msg: string}>}
 */
export const create = (data) => {
  return axios.post('/api/service/item', data);
};

/**
 * 修改服务项目
 * @param {Object} data - 服务项目数据
 * @param {number} data.serviceId - 服务项目ID
 * @param {string} data.serviceName - 服务名称
 * @param {string} data.description - 服务描述
 * @param {number} data.price - 服务价格
 * @param {number} data.duration - 服务时长
 * @param {string} data.status - 状态：0-正常 1-停用
 * @returns {Promise<{code: number, msg: string}>}
 */
export const update = (data) => {
  return axios.put('/api/service/item', data);
};

/**
 * 删除服务项目
 * @param {number} id - 服务项目ID
 * @returns {Promise<{code: number, msg: string}>}
 */
export const remove = (id) => {
  return axios.delete(`/api/service/item/${id}`);
};

/**
 * 批量删除服务项目
 * @param {string} ids - 服务项目ID，多个以逗号分隔
 * @returns {Promise<{code: number, msg: string}>}
 */
export const batchRemove = (ids) => {
  return axios.delete(`/api/service/item/batch/${ids}`);
};

/**
 * 导出服务项目列表
 * @param {Object} params - 查询参数，与获取列表接口参数一致
 * @returns {Promise<Blob>} - 返回文件流
 */
export const exportList = (params) => {
  return axios.get('/api/service/item/export', { 
    params,
    responseType: 'blob'
  });
};

/**
 * 更新服务项目状态
 * @param {Object} data - 状态数据
 * @param {number} data.id - 服务项目ID
 * @param {string} data.status - 状态：0-正常 1-停用
 * @returns {Promise<{code: number, msg: string}>}
 */
export const updateStatus = (data) => {
  const { id, status } = data;
  return axios.put(`/api/service/item/${status}/${id}`);
}; 