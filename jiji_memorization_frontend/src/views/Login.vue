<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>吉吉记单词</h1>
        <p>请登录您的账户</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">用户名或邮箱</label>
          <input
            id="username"
            v-model="loginForm.username"
            type="text"
            placeholder="请输入用户名或邮箱"
            required
            :disabled="loading"
          />
        </div>
        
        <div class="form-group">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            required
            :disabled="loading"
          />
        </div>
        
        <div class="form-actions">
          <button 
            type="submit" 
            class="login-btn"
            :disabled="loading"
          >
            <span v-if="loading">登录中...</span>
            <span v-else>登录</span>
          </button>
        </div>
        
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
      </form>
      
      <div class="login-footer">
        <p>还没有账户？ <a href="#" @click.prevent="goToRegister">立即注册</a></p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/utils/axios'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    const loading = ref(false)
    const errorMessage = ref('')
    
    const loginForm = reactive({
      username: '',
      password: ''
    })
    
    const handleLogin = async () => {
      if (!loginForm.username || !loginForm.password) {
        errorMessage.value = '请填写完整的登录信息'
        return
      }
      
      loading.value = true
      errorMessage.value = ''
      
      try {
        const response = await api.post('/api/auth/login', {
          username: loginForm.username,
          password: loginForm.password
        })
        
        if (response.data.status === 'success') {
          // 保存token和用户信息
          authStore.setToken(response.data.token)
          authStore.setUser(response.data.user)
          
          // 跳转到主页
          router.push('/')
        } else {
          errorMessage.value = response.data.message || '登录失败'
        }
      } catch (error) {
        console.error('Login error:', error)
        if (error.response?.data?.message) {
          errorMessage.value = error.response.data.message
        } else {
          errorMessage.value = '登录失败，请检查网络连接'
        }
      } finally {
        loading.value = false
      }
    }
    
    const goToRegister = () => {
      router.push('/register')
    }
    
    return {
      loginForm,
      loading,
      errorMessage,
      handleLogin,
      goToRegister
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 400px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  color: #333;
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
}

.login-header p {
  color: #666;
  margin: 0;
  font-size: 16px;
}

.login-form {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.form-group input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.form-actions {
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.error-message {
  background-color: #fee;
  color: #c33;
  padding: 12px;
  border-radius: 6px;
  font-size: 14px;
  text-align: center;
  margin-bottom: 20px;
}

.login-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e1e5e9;
}

.login-footer p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.login-footer a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.login-footer a:hover {
  text-decoration: underline;
}
</style>
