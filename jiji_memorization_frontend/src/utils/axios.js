import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

// 从环境变量获取API地址，支持IP修改
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// 创建axios实例
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response) {
      // 服务器返回错误状态码
      switch (error.response.status) {
        case 401:
          // 未授权，清除认证信息并跳转到登录页
          const authStore = useAuthStore()
          authStore.clearAuth()
          window.location.href = '/login'
          break
        case 403:
          // 禁止访问
          console.error('Access forbidden')
          break
        case 404:
          // 资源不存在
          console.error('Resource not found')
          break
        case 500:
          // 服务器内部错误
          console.error('Internal server error')
          break
        default:
          console.error('Request failed with status:', error.response.status)
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      console.error('No response received:', error.request)
    } else {
      // 请求配置出错
      console.error('Request error:', error.message)
    }
    
    return Promise.reject(error)
  }
)

export default api
