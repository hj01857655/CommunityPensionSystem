/**
 * User Types
 */

export type UserRole = 'admin' | 'staff' | 'elder' | 'kin'

export interface User {
  userId: number
  username: string
  name: string
  gender?: string
  phone: string
  email?: string
  address?: string
  avatar?: string
  birthday?: string
  idCard?: string
  emergencyContactName?: string
  emergencyContactPhone?: string
  healthCondition?: string
  allergy?: string
  medicalHistory?: string
  roleId: number
  roleIdList?: number[]
  roleNames?: string[]
  remark?: string // 备注
  bindElderIds?: number[] // 绑定的老人ID列表(for kin)
  bindKinIds?: number[] // 绑定的家属ID列表(for elder)
  relationType?: string // 关系类型
  createTime?: string
  updateTime?: string
}

export interface LoginRequest {
  username: string
  password: string
  roleId?: number
  portal?: 'front' | 'admin'
}

export interface LoginResponse {
  accessToken: string
  /** 旧 Vue 兼容字段；React 端 refresh token 走 HttpOnly cookie，不依赖此值 */
  refreshToken?: string
  /** 后端登录响应中的用户信息字段名为 user */
  user: User
}

export interface UpdateUserRequest {
  userId: number
  name?: string
  gender?: string
  phone?: string
  email?: string
  address?: string
  avatar?: string
  birthday?: string
  idCard?: string
  emergencyContactName?: string
  emergencyContactPhone?: string
  healthCondition?: string
  allergy?: string
  medicalHistory?: string
}

export interface ElderKinRelation {
  elderId: number
  kinId: number
  relationType: string
  createTime?: string
}
