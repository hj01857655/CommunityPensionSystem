/**
 * API Response Types
 */

export interface ApiResponse<T = any> {
  code: number
  data: T
  message: string
}

export interface PageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface PageRequest {
  current?: number
  size?: number
  [key: string]: any
}
