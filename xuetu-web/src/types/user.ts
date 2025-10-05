/**
 * 用户相关类型定义
 */

export interface User {
  id: number
  username: string
  email: string
  phone: string
  nickname: string
  avatar: string
  status: number
  createdTime: string
  updatedTime: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  userInfo: User
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
  phone?: string
  nickname?: string
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
}