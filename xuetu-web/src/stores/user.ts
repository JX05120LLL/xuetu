import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, LoginRequest, RegisterRequest } from '@/types/user'
import { login, register, logout as logoutApi, getUserInfo as getUserInfoApi } from '@/api/user'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>('')
  const userInfo = ref<User | null>(null)

  // 计算属性
  const isLogin = computed(() => !!token.value && !!userInfo.value)
  const userId = computed(() => userInfo.value?.id || 0)

  // 登录
  async function userLogin(data: LoginRequest) {
    try {
      const res = await login(data)
      token.value = res.token
      userInfo.value = res.userInfo
      
      // 存储到本地
      localStorage.setItem('token', res.token)
      localStorage.setItem('userInfo', JSON.stringify(res.userInfo))
      
      ElMessage.success('登录成功')
      return true
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  }

  // 注册
  async function userRegister(data: RegisterRequest) {
    try {
      await register(data)
      ElMessage.success('注册成功，请登录')
      return true
    } catch (error) {
      console.error('注册失败:', error)
      return false
    }
  }

  // 登出
  async function userLogout() {
    try {
      await logoutApi()
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      // 清除状态
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      
      ElMessage.success('已退出登录')
      router.push('/login')
    }
  }

  // 获取用户信息
  async function getUserInfo() {
    if (!userId.value) {
      // 尝试从本地存储恢复
      const localUserInfo = localStorage.getItem('userInfo')
      if (localUserInfo) {
        userInfo.value = JSON.parse(localUserInfo)
      }
    }
    
    if (userId.value) {
      try {
        const info = await getUserInfoApi(userId.value)
        userInfo.value = info
        localStorage.setItem('userInfo', JSON.stringify(info))
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    }
  }

  return {
    token,
    userInfo,
    isLogin,
    userId,
    login: userLogin,
    register: userRegister,
    logout: userLogout,
    getUserInfo
  }
})