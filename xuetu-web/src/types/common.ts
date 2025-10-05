/**
 * 通用类型定义
 */

// API响应格式
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页请求参数
export interface PageParam {
  current: number
  size: number
}

// 分页响应数据
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
}

// 选项类型
export interface Option {
  label: string
  value: string | number
}

// 文件上传响应
export interface UploadResponse {
  url: string
  name: string
  size: number
}