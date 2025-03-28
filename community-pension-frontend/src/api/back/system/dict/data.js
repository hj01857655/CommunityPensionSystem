import axios from '@/utils/axios'

/**
 * 根据字典类型查询字典数据
 * @param {string} dictType - 字典类型
 * @returns {Promise<{code: number, data: Array, msg: string}>}
 */
export const getDictDataByType = (dictType) => {
  return axios.get(`/api/system/dict/data/type/${dictType}`);
};

/**
 * 获取字典数据列表
 * @param {Object} params - 查询参数
 * @param {string} [params.dictType] - 字典类型
 * @param {string} [params.dictLabel] - 字典标签
 * @param {string} [params.status] - 状态：0-正常 1-停用
 * @returns {Promise<{code: number, data: Array, msg: string}>}
 */
export const getDictDataList = (params) => {
  return axios.get('/api/system/dict/data/list', { params });
};

/**
 * 获取字典数据详情
 * @param {number} dictCode - 字典编码
 * @returns {Promise<{code: number, data: Object, msg: string}>}
 */
export const getDictDataDetail = (dictCode) => {
  return axios.get(`/api/system/dict/data/${dictCode}`);
};

/**
 * 新增字典数据
 * @param {Object} data - 字典数据
 * @param {string} data.dictType - 字典类型
 * @param {string} data.dictLabel - 字典标签
 * @param {string} data.dictValue - 字典键值
 * @param {string} data.status - 状态：0-正常 1-停用
 * @param {number} [data.dictSort] - 字典排序
 * @param {string} [data.remark] - 备注
 * @returns {Promise<{code: number, msg: string}>}
 */
export const createDictData = (data) => {
  return axios.post('/api/system/dict/data', data);
};

/**
 * 修改字典数据
 * @param {Object} data - 字典数据
 * @param {number} data.dictCode - 字典编码
 * @param {string} data.dictType - 字典类型
 * @param {string} data.dictLabel - 字典标签
 * @param {string} data.dictValue - 字典键值
 * @param {string} data.status - 状态：0-正常 1-停用
 * @param {number} [data.dictSort] - 字典排序
 * @param {string} [data.remark] - 备注
 * @returns {Promise<{code: number, msg: string}>}
 */
export const updateDictData = (data) => {
  return axios.put('/api/system/dict/data', data);
};

/**
 * 删除字典数据
 * @param {string} dictCodes - 字典编码，多个以逗号分隔
 * @returns {Promise<{code: number, msg: string}>}
 */
export const deleteDictData = (dictCodes) => {
  return axios.delete(`/api/system/dict/data/${dictCodes}`);
};

/**
 * 导出字典数据
 * @param {Object} params - 查询参数
 * @param {string} [params.dictType] - 字典类型
 * @param {string} [params.dictLabel] - 字典标签
 * @param {string} [params.status] - 状态：0-正常 1-停用
 * @returns {Promise<Blob>} - 返回文件流
 */
export const exportDictData = (params) => {
  return axios.get('/api/system/dict/data/export', {
    params,
    responseType: 'blob'
  });
}; 