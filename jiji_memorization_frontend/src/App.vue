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
          <div class="user-info">
            <div class="user-avatar">
              <img 
                :src="getUserAvatar()" 
                :alt="authStore.currentUser?.username" 
                @error="handleAvatarError"
                ref="avatarImg"
              />
            </div>
            <span class="username">{{ authStore.currentUser?.nickname || authStore.currentUser?.username }}</span>
          </div>
          <div class="user-menu" :class="{ 'active': showUserMenu }">
            <button @click="toggleUserMenu" class="menu-trigger">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="1"></circle>
                <circle cx="19" cy="12" r="1"></circle>
                <circle cx="5" cy="12" r="1"></circle>
              </svg>
            </button>
            <div v-if="showUserMenu" class="menu-dropdown">
              <div class="menu-item" @click="goToProfile">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                  <circle cx="12" cy="7" r="4"></circle>
                </svg>
                个人资料
              </div>
              <div class="menu-item" @click="goToSettings">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="3"></circle>
                  <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1 1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path>
                </svg>
                设置
              </div>
              <div class="menu-divider"></div>
              <div class="menu-item logout-item" @click="handleLogout">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                  <polyline points="16,17 21,12 16,7"></polyline>
                  <line x1="21" y1="12" x2="9" y2="12"></line>
                </svg>
                退出登录
              </div>
            </div>
            
            <!-- 隐藏的文件输入 -->
            <input 
              ref="avatarInput" 
              type="file" 
              accept="image/*" 
              style="display: none" 
              @change="handleAvatarUpload"
            />
          </div>
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
import { onMounted, computed, ref, onUnmounted } from 'vue'
import api from '@/utils/axios'

export default {
  name: 'App',
  setup() {
    const authStore = useAuthStore()
    const router = useRouter()
    const route = useRoute()
    
    // 用户菜单状态
    const showUserMenu = ref(false)
    
    // 判断当前是否是后台管理页面
    const isAdminPage = computed(() => {
      return route.path === '/admin'
    })
    
    // 组件挂载时检查认证状态
    onMounted(() => {
      authStore.checkAutoLogin()
      // 点击外部关闭菜单
      document.addEventListener('click', handleClickOutside)
    })
    
    // 组件卸载时移除事件监听
    onUnmounted(() => {
      document.removeEventListener('click', handleClickOutside)
    })
    
    // 获取用户头像
    const getUserAvatar = () => {
      const user = authStore.currentUser
      if (!user) return getDefaultAvatar('User')
      
      // 如果用户有自定义头像路径
      if (user.avatar && user.avatar.trim()) {
        // 检查是否是完整的URL
        if (user.avatar.startsWith('http://') || user.avatar.startsWith('https://')) {
          return user.avatar
        }
        
        // 检查是否是服务器上的头像文件（以avatar_开头）
        if (user.avatar.startsWith('avatar_')) {
          return `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/uploads/avatars/${user.avatar}`
        }
        
        // 检查是否是相对路径，需要添加服务器基础URL
        if (user.avatar.startsWith('/')) {
          return `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}${user.avatar}`
        }
        
        // 如果是其他文件名，假设存储在uploads/avatars目录
        return `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/uploads/avatars/${user.avatar}`
      }
      
      // 没有自定义头像时，生成默认头像
      return getDefaultAvatar(user.username || 'User')
    }
    
    // 生成默认头像
    const getDefaultAvatar = (username) => {
      const firstLetter = username.charAt(0).toUpperCase()
      
      // 生成基于用户名的头像颜色
      const colors = [
        '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7',
        '#DDA0DD', '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E9'
      ]
      const colorIndex = username.charCodeAt(0) % colors.length
      const backgroundColor = colors[colorIndex]
      
      // 创建SVG头像
      const svg = `
        <svg width="32" height="32" viewBox="0 0 32 32" xmlns="http://www.w3.org/2000/svg">
          <rect width="32" height="32" fill="${backgroundColor}" rx="16"/>
          <text x="16" y="20" font-family="Arial, sans-serif" font-size="14" font-weight="bold" text-anchor="middle" fill="white">${firstLetter}</text>
        </svg>
      `
      
      return `data:image/svg+xml;base64,${btoa(svg)}`
    }
    
    // 切换用户菜单
    const toggleUserMenu = (event) => {
      event.stopPropagation()
      showUserMenu.value = !showUserMenu.value
    }
    
    // 点击外部关闭菜单
    const handleClickOutside = (event) => {
      if (!event.target.closest('.user-menu')) {
        showUserMenu.value = false
      }
    }
    
    // 跳转到个人资料
    const goToProfile = () => {
      showUserMenu.value = false
      router.push('/profile')
    }
    
    // 跳转到设置
    const goToSettings = () => {
      showUserMenu.value = false
      // 这里可以跳转到设置页面
      console.log('跳转到设置')
    }
    
    // 处理头像加载错误
    const handleAvatarError = (event) => {
      console.warn('头像加载失败，使用默认头像')
      const user = authStore.currentUser
      if (user) {
        event.target.src = getDefaultAvatar(user.username || 'User')
      }
    }
    
    // 上传头像
    const uploadAvatar = async () => {
      showUserMenu.value = false
      
      // 首先尝试使用File System Access API（现代浏览器支持）
      if (window.showOpenFilePicker) {
        try {
          const [fileHandle] = await window.showOpenFilePicker({
            types: [{
              description: '图片文件',
              accept: {
                'image/*': ['.png', '.jpg', '.jpeg', '.gif', '.bmp', '.webp']
              }
            }]
          })
          const file = await fileHandle.getFile()
          
          // 验证文件类型
          if (!file.type.startsWith('image/')) {
            alert('请选择图片文件')
            return
          }
          
          // 验证文件大小（2MB限制）
          if (file.size > 2 * 1024 * 1024) {
            alert('图片大小不能超过2MB')
            return
          }
          
          // 使用File System Access API获取的文件
          await handleFileUpload(file, fileHandle.name)
          
        } catch (error) {
          if (error.name === 'AbortError') {
            // 用户取消了选择
            return
          }
          console.log('File System Access API失败，回退到传统方法:', error)
          // 回退到传统的文件选择方法
          const avatarInput = document.querySelector('input[type="file"]')
          if (avatarInput) {
            avatarInput.click()
          }
        }
      } else {
        // 浏览器不支持File System Access API，使用传统方法
        const avatarInput = document.querySelector('input[type="file"]')
        if (avatarInput) {
          avatarInput.click()
        }
      }
    }
    
    // 处理文件上传的通用方法
    const handleFileUpload = async (file, fileName = null) => {
      try {
        // 验证文件类型（只允许图片）
        if (!file.type.startsWith('image/')) {
          alert('请选择图片文件（支持格式：PNG、JPG、JPEG、GIF、BMP、WEBP）')
          return
        }
        
        // 验证文件大小（2MB限制）
        if (file.size > 2 * 1024 * 1024) {
          alert('图片大小不能超过2MB')
          return
        }
        
        // 将文件转换为Base64
        const base64Data = await fileToBase64(file)
        
        // 获取文件扩展名
        const fileExtension = file.name.split('.').pop().toLowerCase()
        
        // 生成唯一的文件名（用户ID + 时间戳 + 扩展名）
        const timestamp = Date.now()
        const newFileName = `avatar_${authStore.currentUser.id}_${timestamp}.${fileExtension}`
        
        console.log('准备上传头像文件:', newFileName)
        console.log('文件大小:', file.size, 'bytes')
        console.log('文件类型:', file.type)
        
        // 上传到服务器
        const uploadResponse = await api.post('/api/users/upload-avatar', {
          userId: authStore.currentUser.id,
          fileName: newFileName,
          fileData: base64Data,
          fileType: file.type,
          fileSize: file.size
        })
        
        if (uploadResponse.data.status === 'success') {
          // 更新本地用户信息
          authStore.currentUser.avatar = uploadResponse.data.avatarPath
          // 更新localStorage中的用户信息
          authStore.setUser(authStore.currentUser)
          alert('头像上传成功！')
        } else {
          alert('头像上传失败: ' + uploadResponse.data.message)
        }
      } catch (error) {
        console.error('头像上传失败:', error)
        alert('头像上传失败，请稍后重试')
      }
    }
    
    // 将文件转换为Base64
    const fileToBase64 = (file) => {
      return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onload = () => {
          // 移除data:image/...;base64,前缀，只保留Base64数据
          const base64 = reader.result.split(',')[1]
          resolve(base64)
        }
        reader.onerror = reject
        reader.readAsDataURL(file)
      })
    }
    
    // 处理头像上传（传统文件选择）
    const handleAvatarUpload = async (event) => {
      const file = event.target.files[0]
      if (!file) return
      
      // 使用通用的文件上传处理方法
      await handleFileUpload(file, event.target.value)
      
      // 清空文件输入
      event.target.value = ''
    }
    
    const handleLogout = () => {
      showUserMenu.value = false
      authStore.logout()
      router.push('/login')
    }
    
    return {
      authStore,
      isAdminPage,
      showUserMenu,
      getUserAvatar,
      toggleUserMenu,
      goToProfile,
      goToSettings,
      handleAvatarError,
      uploadAvatar,
      handleAvatarUpload,
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
  position: relative;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #e0e0e0;
  transition: border-color 0.3s ease;
}

.user-avatar:hover {
  border-color: #667eea;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.username {
  color: #333;
  font-weight: 500;
  font-size: 14px;
}

.user-menu {
  position: relative;
}

.menu-trigger {
  background: none;
  border: none;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  color: #666;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-trigger:hover {
  background-color: #f0f0f0;
  color: #333;
}

.menu-trigger svg {
  transition: transform 0.3s ease;
}

.user-menu.active .menu-trigger svg {
  transform: rotate(90deg);
}

.menu-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  min-width: 160px;
  z-index: 1000;
  margin-top: 4px;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  font-size: 14px;
  color: #333;
}

.menu-item:hover {
  background-color: #f8f9fa;
}

.menu-item svg {
  color: #666;
  flex-shrink: 0;
}

.menu-divider {
  height: 1px;
  background-color: #e0e0e0;
  margin: 4px 0;
}

.logout-item {
  color: #ff4757;
}

.logout-item:hover {
  background-color: #fff5f5;
}

.logout-item svg {
  color: #ff4757;
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
    gap: 8px;
  }
  
  .user-info {
    gap: 6px;
  }
  
  .user-avatar {
    width: 28px;
    height: 28px;
  }
  
  .username {
    font-size: 13px;
  }
  
  .menu-trigger {
    padding: 6px;
  }
  
  .menu-dropdown {
    min-width: 140px;
  }
  
  .menu-item {
    padding: 10px 12px;
    font-size: 13px;
  }
}
</style>

