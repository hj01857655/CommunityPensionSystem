import axios from '@/utils/axios'

// 获取所有亲属信息
export const getAllKins = async () => {
  const response = await axios.get('/api/kins')
  return response.data
}

// 添加亲属信息
export const addKin = async (data) => {
  const response = await axios.post('/api/kin/add', data)
  return response.data
}

// 更新亲属信息
export const updateKin = async (data) => {
  const response = await axios.put('/kin/update', data)
  return response.data
}

// 删除亲属信息
export const deleteKin = async (id) => {
  const response = await axios.delete(`/kin/delete/${id}`)
  return response.data
}

// 获取亲属详情
export const getKinDetail = async (id) => {
  const response = await axios.get(`/kin/detail/${id}`)
  return response.data
}