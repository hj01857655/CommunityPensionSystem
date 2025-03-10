import axios from '@/utils/axios'

// 获取工作人员列表
export const getStaffList = async (params) => {
  const response = await axios.get('/staff/list', {
    params: {
      page: params?.page ?? 1,
      size: params?.size ?? 10
      
    }
  })
  return response.data
}

// 添加工作人员
export const addStaff = async (data) => {
  const response = await axios.post('/staff/add', data)
  return response.data
}

// 更新工作人员信息
export const updateStaff = async (data) => {
  const response = await axios.put('/staff/update', data)
  return response.data
}

// 删除工作人员
export const deleteStaff = async (id) => {
  const response = await axios.delete(`/staff/delete/${id}`)
  return response.data
}

// 获取工作人员详情
export const getStaffDetail = async (id) => {
  const response = await axios.get(`/staff/detail/${id}`)
  return response.data
}

// 获取工作人员服务记录
export const getStaffServiceRecords = async (id) => {
  const response = await axios.get(`/staff/service-records/${id}`)
  return response.data
}