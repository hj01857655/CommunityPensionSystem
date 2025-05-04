import request from '@/utils/request'

// 绑定老人和家属关系
export function bindRelation(data) {
  return request({
      url: '/api/system/user/bind-relation',
    method: 'post',
      params: {
          elderId: data.elderId,
          kinId: data.kinId,
          relationType: data.relationType
      }
  })
}

// 解绑老人和家属关系
export function unbindRelation(elderId, kinId) {
  return request({
      url: '/api/system/user/unbind-relation',
      method: 'post',
    params: { elderId, kinId }
  })
}

// 获取老人的家属列表
export function getKinsByElderId(elderId) {
  return request({
      url: `/api/system/user/kins/${elderId}`,
    method: 'get'
  })
}

// 获取家属的老人列表
export function getEldersByKinId(kinId) {
  return request({
      url: `/api/system/user/elders/${kinId}`,
    method: 'get'
  })
}

// 注意：这些方法现在返回更丰富的信息，包含用户ID、用户名、姓名和关系类型

// 获取老人和家属的关系详情
export function getRelationDetail(elderId, kinId) {
  return request({
      url: '/api/system/user/relation-detail',
    method: 'get',
    params: { elderId, kinId }
  })
}

// 获取未绑定家属的老人列表
export function getUnboundElders() {
  return request({
      url: '/api/system/user/unbound/elders',
    method: 'get'
  })
}

// 获取未绑定老人的家属列表
export function getUnboundKins() {
  return request({
      url: '/api/system/user/unbound/kins',
    method: 'get'
  })
}
