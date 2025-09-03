<template>
  <div class="admin">
    <!-- 登录验证 -->
    <div v-if="!isAuthenticated" class="login-overlay">
      <div class="login-modal">
        <div class="login-header">
          <h2>后台管理登录</h2>
          <p>请输入管理员账号密码</p>
        </div>
        
        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label>用户名</label>
            <input 
              v-model="loginForm.username" 
              type="text" 
              required
              placeholder="请输入用户名"
            />
          </div>
          
          <div class="form-group">
            <label>密码</label>
            <input 
              v-model="loginForm.password" 
              type="password" 
              required
              placeholder="请输入密码"
            />
          </div>
          
          <div v-if="loginError" class="error-message">
            {{ loginError }}
          </div>
          
          <button type="submit" class="login-btn">登录</button>
        </form>
      </div>
    </div>
    
    <!-- 主管理界面 -->
    <div v-else class="admin-main">
      <!-- 侧边栏导航 -->
      <div class="sidebar">
        <div class="sidebar-header">
          <h2>吉吉记单词</h2>
          <p>后台管理</p>
        </div>
        
        <nav class="sidebar-nav">
          <div 
            v-for="menu in menus" 
            :key="menu.key"
            @click="selectMenu(menu)"
            :class="['nav-item', { active: currentMenu?.key === menu.key }]"
          >
            <span class="nav-icon">{{ menu.icon }}</span>
            <span class="nav-text">{{ menu.name }}</span>
          </div>
        </nav>
        
        <div class="sidebar-footer">
          <button @click="goBack" class="back-btn">
            ← 返回前台
          </button>
          <button @click="handleLogout" class="logout-btn">
            退出登录
          </button>
        </div>
      </div>
      
      <!-- 主内容区域 -->
      <div class="main-content">
        <div class="content-header">
          <h1>{{ currentMenu?.name || '后台管理' }}</h1>
          <div class="user-info">
            <span>管理员：{{ loginForm.username }}</span>
          </div>
        </div>
        
        <div class="content-body">
          <!-- 统计概览 -->
          <Dashboard v-if="currentMenu?.key === 'dashboard'" />
          
          <!-- 教材管理 -->
          <TextbookManager v-if="currentMenu?.key === 'textbooks'" />
          
          <!-- 单元管理 -->
          <UnitManager v-if="currentMenu?.key === 'units'" />
          
          <!-- 单词管理 -->
          <WordManager v-if="currentMenu?.key === 'words'" />
          
          <!-- 单元单词关联管理 -->
          <UnitWordManager v-if="currentMenu?.key === 'unitWords'" />
          
          <!-- 释义管理 -->
          <MeaningManager v-if="currentMenu?.key === 'meanings'" />
          
          <!-- 短语管理 -->
          <PhraseManager v-if="currentMenu?.key === 'phrases'" />
          
          <!-- 例句管理 -->
          <SentenceManager v-if="currentMenu?.key === 'sentences'" />
          
          <!-- 用户管理 -->
          <UserManager v-if="currentMenu?.key === 'users'" />
          
          <!-- 学习记录管理 -->
          <ErrorRecordManager v-if="currentMenu?.key === 'errorRecords'" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import Dashboard from '@/components/admin/Dashboard.vue'
import TextbookManager from '@/components/admin/TextbookManager.vue'
import UnitManager from '@/components/admin/UnitManager.vue'
import WordManager from '@/components/admin/WordManager.vue'
import UserManager from '@/components/admin/UserManager.vue'
import ErrorRecordManager from '@/components/admin/ErrorRecordManager.vue'
import UnitWordManager from '@/components/admin/UnitWordManager.vue'
import MeaningManager from '@/components/admin/MeaningManager.vue'
import PhraseManager from '@/components/admin/PhraseManager.vue'
import SentenceManager from '@/components/admin/SentenceManager.vue'

export default {
  name: 'Admin',
  components: {
    Dashboard,
    TextbookManager,
    UnitManager,
    WordManager,
    UserManager,
    ErrorRecordManager,
    UnitWordManager,
    MeaningManager,
    PhraseManager,
    SentenceManager
  },
  setup() {
    const router = useRouter()
    const currentMenu = ref(null)
    const isAuthenticated = ref(false)
    const loginError = ref('')
    
    const loginForm = ref({
      username: '',
      password: ''
    })
    
    const menus = [
      { key: 'dashboard', name: '统计概览', icon: '📊' },
      { key: 'textbooks', name: '教材管理', icon: '📚' },
      { key: 'units', name: '单元管理', icon: '📖' },
      { key: 'words', name: '单词管理', icon: '🔤' },
      { key: 'unitWords', name: '单元单词关联', icon: '🔗' },
      { key: 'meanings', name: '释义管理', icon: '📝' },
      { key: 'phrases', name: '短语管理', icon: '📖' },
      { key: 'sentences', name: '例句管理', icon: '💬' },
      { key: 'users', name: '用户管理', icon: '👥' },
      { key: 'errorRecords', name: '学习记录', icon: '📋' }
    ]
    
    const selectMenu = (menu) => {
      currentMenu.value = menu
    }
    
    const goBack = () => {
      router.push('/')
    }
    
    const handleLogout = () => {
      isAuthenticated.value = false
      loginForm.value.username = ''
      loginForm.value.password = ''
      loginError.value = ''
    }
    
    const handleLogin = () => {
      if (loginForm.value.username === 'admin' && loginForm.value.password === 'admin') {
        isAuthenticated.value = true
        loginError.value = ''
        // 默认显示统计概览
        currentMenu.value = menus[0]
      } else {
        loginError.value = '用户名或密码错误'
      }
    }
    
    onMounted(() => {
      // 检查是否已经登录
      if (isAuthenticated.value) {
        currentMenu.value = menus[0]
      }
    })
    
    return {
      currentMenu,
      menus,
      isAuthenticated,
      loginForm,
      loginError,
      selectMenu,
      goBack,
      handleLogout,
      handleLogin
    }
  }
}
</script>

<style scoped>
.admin {
  height: 100vh;
  background: #f5f7fa;
}

.login-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.login-modal {
  background: white;
  border-radius: 12px;
  padding: 40px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 24px;
}

.login-header p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.form-group input {
  padding: 12px;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.error-message {
  color: #dc3545;
  font-size: 14px;
  text-align: center;
}

.login-btn {
  padding: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
}

.admin-main {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 280px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
}

.sidebar-header {
  padding: 30px 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-header h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: 600;
}

.sidebar-header p {
  margin: 0;
  opacity: 0.8;
  font-size: 14px;
}

.sidebar-nav {
  flex: 1;
  padding: 20px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.1);
  border-left-color: rgba(255, 255, 255, 0.5);
}

.nav-item.active {
  background: rgba(255, 255, 255, 0.2);
  border-left-color: white;
}

.nav-icon {
  font-size: 20px;
  margin-right: 15px;
  width: 24px;
  text-align: center;
}

.nav-text {
  font-size: 16px;
  font-weight: 500;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.back-btn,
.logout-btn {
  width: 100%;
  padding: 12px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover,
.logout-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.logout-btn {
  background: rgba(220, 53, 69, 0.8);
  border-color: rgba(220, 53, 69, 0.8);
}

.logout-btn:hover {
  background: rgba(220, 53, 69, 1);
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-header {
  background: white;
  padding: 20px 30px;
  border-bottom: 1px solid #e1e5e9;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.content-header h1 {
  margin: 0;
  color: #333;
  font-size: 28px;
  font-weight: 600;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info span {
  color: #666;
  font-size: 14px;
}

.content-body {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}
</style>
