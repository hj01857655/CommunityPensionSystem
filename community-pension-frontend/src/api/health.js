import axios from '@/utils/axios'

// 获取健康数据
export const getHealthData = (roleId) => {
  return axios.get('/api/getHealth', { params: { roleId } })
}

// 更新健康数据
export const updateHealthData = (data) => {
  return axios.post('/api/updateHealth', data)
}