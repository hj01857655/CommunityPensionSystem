import axios from '@/utils/axios'

// 获取字典类型列表
export const getDictTypeList = (query) => {
  return axios.get('/api/system/dict/type/list', { params: query })
}

// 获取字典类型详细
export const getDictType = (dictId) => {
  return axios.get('/api/system/dict/type/' + dictId)
}

// 新增字典类型
export const addDictType = (data) => {
  return axios.post('/api/system/dict/type', data)
}

// 修改字典类型
export const updateDictType = (data) => {
  return axios.put('/api/system/dict/type', data)
}

// 删除字典类型
export const deleteDictType = (dictId) => {
  return axios.delete('/api/system/dict/type/' + dictId)
}

// 修改字典类型状态
export const changeDictTypeStatus = (data) => {
  return axios.put('/api/system/dict/type/changeStatus', data)
}

// 获取字典数据列表
export const getDictDataList = (query) => {
  return axios.get('/api/system/dict/data/list', { params: query })
}

// 获取字典数据详细
export const getDictData = (dictCode) => {
  return axios.get('/api/system/dict/data/' + dictCode)
}

// 新增字典数据
export const addDictData = (data) => {
  return axios.post('/api/system/dict/data', data)
}

// 修改字典数据
export const updateDictData = (data) => {
  return axios.put('/api/system/dict/data', data)
}

// 删除字典数据
export const deleteDictData = (dictCode) => {
  return axios.delete('/api/system/dict/data/' + dictCode)
}

// 修改字典数据状态
export const changeDictDataStatus = (data) => {
  return axios.put('/api/system/dict/data/changeStatus', data)
}

// 刷新字典缓存
export function refreshCache() {
  return axios.delete('/api/system/dict/type/refreshCache')
} 