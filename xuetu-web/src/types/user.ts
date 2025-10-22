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
  gender?: number // 0-未设置 1-男 2-女
  birthday?: string
  bio?: string // 个人简介
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
  confirmPassword: string
  email: string
  phone?: string
  nickname?: string
}

export interface ChangePasswordRequest {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

export interface UpdateProfileRequest {
  nickname?: string
  email?: string
  phone?: string
  gender?: number
  birthday?: string
  bio?: string
}