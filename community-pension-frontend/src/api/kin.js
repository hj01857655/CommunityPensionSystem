import axios from '@/utils/axios'

// 获取所有亲属信息
export function getAllKins() {
  return axios({
    url: '/api/kins',
    method: 'get'
  })
}

// 添加亲属信息
export function addKin(data) {
  return axios({
    url: '/api/kin/add',
    method: 'post',
    data
  })
}

// 更新亲属信息
export function updateKin(data) {
  return axios({
    url: '/kin/update',
    method: 'put',
    data
  })
}

// 删除亲属信息
export function deleteKin(id) {
  return axios({
    url: `/kin/delete/${id}`,
    method: 'delete'
  })
}

// 获取亲属详情
export function getKinDetail(id) {
  return axios({
    url: `/kin/detail/${id}`,
    method: 'get'
  })
}