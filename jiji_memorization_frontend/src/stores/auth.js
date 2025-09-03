import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/utils/axios'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const currentUser = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  
  // 计算属性
  const isAuthenticated = computed(() => !!token.value && !!currentUser.value)
  
  // 设置token
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  // 设置用户信息
  const setUser = (user) => {
    currentUser.value = user
    localStorage.setItem('user', JSON.stringify(user))
  }
  
  // 登录
  const login = async (username, password) => {
    try {
      const response = await api.post('/api/auth/login', { username, password })
      if (response.data.status === 'success') {
        setToken(response.data.token)
        setUser(response.data.user)
        return { success: true }
      } else {
        return { success: false, message: response.data.message }
      }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || '登录失败' }
    }
  }
  
  // 注册
  const register = async (username, email, password) => {
    try {
      const response = await api.post('/api/auth/register', { username, email, password })
      if (response.data.status === 'success') {
        setToken(response.data.token)
        setUser(response.data.user)
        return { success: true }
      } else {
        return { success: false, message: response.data.message }
      }
    } catch (error) {
      return { success: false, message: error.response?.data?.message || '注册失败' }
    }
  }
  
  // 登出
  const logout = () => {
    token.value = ''
    currentUser.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
  
  // 检查自动登录
  const checkAutoLogin = async () => {
    if (token.value && currentUser.value) {
      try {
        const response = await api.post('/api/auth/validate', {}, {
          headers: { Authorization: `Bearer ${token.value}` }
        })
        
        if (response.data.status === 'success' && response.data.valid) {
          // Token有效，保持登录状态
          return true
        } else {
          // Token无效，清除登录状态
          logout()
          return false
        }
      } catch (error) {
        // 验证失败，清除登录状态
        logout()
        return false
      }
    }
    return false
  }
  
  // 检查Token是否过期
  const isTokenExpired = () => {
    if (!token.value) return true
    
    try {
      const payload = JSON.parse(atob(token.value.split('.')[1]))
      const currentTime = Date.now() / 1000
      return payload.exp < currentTime
    } catch (error) {
      return true
    }
  }
  
  return {
    token,
    currentUser,
    isAuthenticated,
    setToken,
    setUser,
    login,
    register,
    logout,
    checkAutoLogin,
    isTokenExpired
  }
})
