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
 * @param {number|string} id - 服务项目ID
 * @returns {Promise<{code: number, data: Object, msg: string}>}
 */
export const getDetail = (id) => {
  console.log('调用getDetail API，ID类型:', typeof id, '值:', id);
  
  // JavaScript中大整数可能会有精度问题，确保ID作为字符串传递
  const idStr = String(id);
  console.log('转换后的ID字符串:', idStr);
  
  return axios.get(`/api/service/item/${idStr}`);
};

/**
 * 新增服务项目
 * @param {Object} data - 服务项目数据
 * @param {string} data.serviceName - 服务名称
 * @param {string} data.description - 服务描述
 * @param {number} data.price - 服务价格
 * @param {number} data.duration - 服务时长
 * @param {string} data.status - 状态：0-正常 1-停用
 * @param {string} data.serviceType - 服务类型
 * @returns {Promise<{code: number, msg: string}>}
 */
export const create = (data) => {
  console.log('API create方法接收到的数据:', data);
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
 * @param {string} data.serviceType - 服务类型
 * @returns {Promise<{code: number, msg: string}>}
 */
export const update = (data) => {
  console.log('API update方法接收到的数据:', data);
  return axios.put('/api/service/item', data)
    .catch(error => {
      console.error('更新API错误详情:', error);
      if (error.response) {
        console.error('服务器响应状态:', error.response.status);
        console.error('服务器响应数据:', error.response.data);
      }
      throw error;
    });
};

/**
 * 删除服务项目
 * @param {number|string} id - 服务项目ID
 * @returns {Promise<{code: number, msg: string}>}
 */
export const remove = (id) => {
  console.log('API remove方法接收到的ID:', id, typeof id);
  
  if (!id) {
    return Promise.reject(new Error('服务项目ID不能为空'));
  }
  
  return axios.delete(`/api/service/item/${id}`)
    .then(response => {
      console.log('删除API成功响应:', response);
      console.log('删除API响应数据:', response.data);
      console.log('删除API响应状态:', response.status);
      console.log('删除API响应头:', response.headers);
      
      // 检查后端返回的数据中的 data 字段是否为 true
      if (response.data && response.data.code === 200) {
        if (response.data.data === true) {
          return response.data;
        } else {
          console.error('删除API返回false，可能表示删除失败:', response.data);
          return Promise.reject(new Error('删除失败: 后端操作未成功完成'));
        }
      }
      return response.data;
    })
    .catch(error => {
      console.error('删除API错误详情:', error);
      if (error.response) {
        console.error('服务器响应状态:', error.response.status);
        console.error('服务器响应数据:', error.response.data);
        console.error('服务器响应头:', error.response.headers);
      }
      throw error;
    });
};

/**
 * 批量删除服务项目
 * @param {string} ids - 服务项目ID，多个以逗号分隔
 * @returns {Promise<{code: number, msg: string}>}
 */
export const batchRemove = (ids) => {
  console.log('API batchRemove方法接收到的IDs:', ids);
  
  if (!ids) {
    return Promise.reject(new Error('服务项目ID不能为空'));
  }
  
  return axios.delete(`/api/service/item/batch/${ids}`)
    .then(response => {
      console.log('批量删除API成功响应:', response);
      console.log('批量删除API响应数据:', response.data);
      console.log('批量删除API响应状态:', response.status);
      console.log('批量删除API响应头:', response.headers);
      
      // 检查后端返回的数据中的 data 字段是否为 true
      if (response.data && response.data.code === 200) {
        if (response.data.data === true) {
          return response.data;
        } else {
          console.error('批量删除API返回false，可能表示删除失败:', response.data);
          return Promise.reject(new Error('批量删除失败: 后端操作未成功完成'));
        }
      }
      return response.data;
    })
    .catch(error => {
      console.error('批量删除API错误详情:', error);
      if (error.response) {
        console.error('服务器响应状态:', error.response.status);
        console.error('服务器响应数据:', error.response.data);
        console.error('服务器响应头:', error.response.headers);
      }
      throw error;
    });
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