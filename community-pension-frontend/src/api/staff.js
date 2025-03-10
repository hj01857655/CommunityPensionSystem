import axios from '@/utils/axios'

// 获取工作人员列表
export const getStaffList = async (params) => {
  const response = await axios.get('/staff/list', {
    params: {
      page: params?.page ?? 1,
      size: params?.size ?? 10
      
    }
  })
}

// 添加工作人员
export function addStaff(data) {
  return request({
    url: '/staff/add',
    method: 'post',
    data
  })
}

// 更新工作人员信息
export function updateStaff(data) {
  return request({
    url: '/staff/update',
    method: 'put',
    data
  })
}

// 删除工作人员
export function deleteStaff(id) {
  return request({
    url: `/staff/delete/${id}`,
    method: 'delete'
  })
}

// 获取工作人员详情
export function getStaffDetail(id) {
  return request({
    url: `/staff/detail/${id}`,
    method: 'get'
  })
}

// 获取工作人员服务记录
export function getStaffServiceRecords(id) {
  return request({
    url: `/staff/service-records/${id}`,
    method: 'get'
  })
}