<template>
  <div id="app">
    <!-- 登录页面和后台管理页面不显示导航 -->
    <nav v-if="authStore.isAuthenticated && !isAdminPage" class="navbar">
      <div class="nav-container">
        <div class="nav-brand">
          <router-link to="/" class="brand-link">吉吉记单词</router-link>
        </div>
        
        <div class="nav-menu">
          <router-link to="/" class="nav-link">主页</router-link>
          <router-link to="/about" class="nav-link">关于</router-link>
        </div>
        
        <div class="nav-user">
          <span class="username">{{ authStore.currentUser?.username }}</span>
          <button @click="handleLogout" class="logout-btn">退出</button>
        </div>
      </div>
    </nav>
    
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth'
import { useRouter, useRoute } from 'vue-router'
import { onMounted, computed } from 'vue'

export default {
  name: 'App',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    const route = useRoute()
    
    // 判断当前是否是后台管理页面
    const isAdminPage = computed(() => {
      return route.path === '/admin'
    })
    
    // 组件挂载时检查认证状态
    onMounted(() => {
      authStore.checkAutoLogin()
    })
    
    const handleLogout = () => {
      authStore.logout()
      router.push('/login')
    }
    
    return {
      authStore,
      isAdminPage,
      handleLogout
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
    'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f5f5f5;
}

#app {
  min-height: 100vh;
}

.navbar {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.nav-brand .brand-link {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  text-decoration: none;
}

.nav-menu {
  display: flex;
  gap: 20px;
}

.nav-link {
  color: #666;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.nav-link:hover {
  color: #333;
  background-color: #f0f0f0;
}

.nav-link.router-link-active {
  color: #667eea;
  background-color: #f0f4ff;
}

.nav-user {
  display: flex;
  align-items: center;
  gap: 16px;
}

.username {
  color: #333;
  font-weight: 500;
}

.logout-btn {
  background: #ff4757;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.logout-btn:hover {
  background: #ff3742;
}

.main-content {
  flex: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-container {
    padding: 0 16px;
  }
  
  .nav-menu {
    gap: 12px;
  }
  
  .nav-link {
    padding: 6px 12px;
    font-size: 14px;
  }
  
  .nav-user {
    gap: 12px;
  }
  
  .username {
    font-size: 14px;
  }
  
  .logout-btn {
    padding: 6px 12px;
    font-size: 12px;
  }
}
</style>

