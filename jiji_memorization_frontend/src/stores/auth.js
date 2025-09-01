import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  
  // 计算属性
  const isAuthenticated = computed(() => !!token.value && !!user.value)
  const currentUser = computed(() => user.value)
  
  // 方法
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const setUser = (newUser) => {
    user.value = newUser
    localStorage.setItem('user', JSON.stringify(newUser))
  }
  
  const login = (newToken, newUser) => {
    setToken(newToken)
    setUser(newUser)
  }
  
  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
  
  const clearAuth = () => {
    logout()
  }
  
  // 检查token是否过期（这里可以添加JWT解析逻辑）
  const isTokenExpired = () => {
    if (!token.value) return true
    
    try {
      // 简单的token过期检查，实际项目中应该解析JWT
      // 这里假设token包含过期时间信息
      return false
    } catch (error) {
      console.error('Token validation error:', error)
      return true
    }
  }
  
  // 自动登录检查
  const checkAutoLogin = () => {
    if (token.value && user.value && !isTokenExpired()) {
      return true
    }
    
    // 如果token无效，清除认证信息
    if (isTokenExpired()) {
      clearAuth()
    }
    
    return false
  }
  
  return {
    // 状态
    token,
    user,
    
    // 计算属性
    isAuthenticated,
    currentUser,
    
    // 方法
    setToken,
    setUser,
    login,
    logout,
    clearAuth,
    isTokenExpired,
    checkAutoLogin
  }
})
