import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// 创建axios实例
const service: AxiosInstance = axios.create({
  // 开发环境使用Vite代理，baseURL留空
  // 生产环境通过环境变量配置
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 10000,
  withCredentials: false
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data
    
    // 如果code不是200，则判断为错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // 401: Token过期或未登录
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    // 返回data数据
    return res.data
  },
  (error) => {
    console.error('响应错误:', error)
    ElMessage.error(error.message || '网络错误，请稍后重试')
    return Promise.reject(error)
  }
)

export default service