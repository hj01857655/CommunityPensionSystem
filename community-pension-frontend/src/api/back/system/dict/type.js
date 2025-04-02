import axios from '@/utils/axios';

/**
 * 获取字典类型列表
 * @param {Object} params - 查询参数
 * @param {string} [params.dictName] - 字典名称
 * @param {string} [params.dictType] - 字典类型
 * @param {string} [params.status] - 状态：0-正常 1-停用
 * @returns {Promise<{code: number, data: Array, msg: string}>}
 */
export const getDictTypeList = (params) => {
  return axios.get('/api/system/dict/type/list', { params });
};

/**
 * 获取字典类型详情
 * @param {number} dictId - 字典ID
 * @returns {Promise<{code: number, data: Object, msg: string}>}
 */
export const getDictTypeDetail = (dictId) => {
  return axios.get(`/api/system/dict/type/${dictId}`);
};

/**
 * 新增字典类型
 * @param {Object} data - 字典类型数据
 * @param {string} data.dictName - 字典名称
 * @param {string} data.dictType - 字典类型
 * @param {string} data.status - 状态：0-正常 1-停用
 * @param {string} data.remark - 备注
 * @returns {Promise<{code: number, msg: string}>}
 */
export const createDictType = (data) => {
  return axios.post('/api/system/dict/type', data);
};

/**
 * 修改字典类型
 * @param {Object} data - 字典类型数据
 * @param {number} data.dictId - 字典ID
 * @param {string} data.dictName - 字典名称
 * @param {string} data.dictType - 字典类型
 * @param {string} data.status - 状态：0-正常 1-停用
 * @param {string} data.remark - 备注
 * @returns {Promise<{code: number, msg: string}>}
 */
export const updateDictType = (data) => {
  return axios.put('/api/system/dict/type', data);
};

/**
 * 删除字典类型
 * @param {string} dictIds - 字典ID，多个以逗号分隔
 * @returns {Promise<{code: number, msg: string}>}
 */
export const deleteDictType = (dictIds) => {
  return axios.delete(`/api/system/dict/type/${dictIds}`);
};

/**
 * 刷新字典缓存
 * @returns {Promise<{code: number, msg: string}>}
 */
export const refreshDictCache = () => {
  return axios.delete('/api/system/dict/type/refreshCache');
};

