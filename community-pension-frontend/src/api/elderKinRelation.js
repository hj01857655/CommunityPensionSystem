import request from '@/utils/request'

// 绑定老人和家属关系
export function bindRelation(data) {
  return request({
    url: '/api/elder-kin/bind',
    method: 'post',
    data
  })
}

// 解绑老人和家属关系
export function unbindRelation(elderId, kinId) {
  return request({
    url: '/api/elder-kin/unbind',
    method: 'delete',
    params: { elderId, kinId }
  })
}

// 获取老人的家属列表
export function getKinsByElderId(elderId) {
  return request({
    url: `/api/elder-kin/kins/${elderId}`,
    method: 'get'
  })
}

// 获取家属的老人列表
export function getEldersByKinId(kinId) {
  return request({
    url: `/api/elder-kin/elders/${kinId}`,
    method: 'get'
  })
}

// 获取老人和家属的关系详情
export function getRelationDetail(elderId, kinId) {
  return request({
    url: '/api/elder-kin/relation',
    method: 'get',
    params: { elderId, kinId }
  })
}

// 获取未绑定家属的老人列表
export function getUnboundElders() {
  return request({
    url: '/api/elder-kin/unbound-elders',
    method: 'get'
  })
}

// 获取未绑定老人的家属列表
export function getUnboundKins() {
  return request({
    url: '/api/elder-kin/unbound-kins',
    method: 'get'
  })
}
