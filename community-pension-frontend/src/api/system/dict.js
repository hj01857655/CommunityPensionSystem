import request from '@/utils/request'

/**
 * 获取字典数据列表
 * @param {string} dictType 字典类型
 * @returns {Promise} 返回字典数据列表
 */
export function getDictDataByType(dictType) {
  return request({
    url: '/system/dict/data/type/' + dictType,
    method: 'get'
  })
}

/**
 * 获取字典数据列表
 * @returns {Promise} 返回字典数据列表
 */
export function listDictData(query) {
  return request({
    url: '/system/dict/data/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询字典数据详细
 * @param {number} dictCode 字典编码
 * @returns {Promise} 返回字典数据详情
 */
export function getDictData(dictCode) {
  return request({
    url: '/system/dict/data/' + dictCode,
    method: 'get'
  })
}

/**
 * 新增字典数据
 * @param {Object} data 字典数据
 * @returns {Promise} 返回操作结果
 */
export function addDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'post',
    data: data
  })
}

/**
 * 修改字典数据
 * @param {Object} data 字典数据
 * @returns {Promise} 返回操作结果
 */
export function updateDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'put',
    data: data
  })
}

/**
 * 删除字典数据
 * @param {number} dictCode 字典编码
 * @returns {Promise} 返回操作结果
 */
export function delDictData(dictCode) {
  return request({
    url: '/system/dict/data/' + dictCode,
    method: 'delete'
  })
} 