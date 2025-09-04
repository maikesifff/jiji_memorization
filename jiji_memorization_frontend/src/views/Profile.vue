<template>
  <div class="profile-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>个人资料</h1>
      <p>管理您的个人信息和设置</p>
    </div>

    <!-- 主要内容区域 -->
    <div class="profile-content">
      <!-- 左侧：头像和基本信息 -->
      <div class="profile-left">
        <div class="avatar-section">
          <div class="avatar-container">
            <img 
              :src="getUserAvatar()" 
              :alt="user?.username || '用户头像'"
              @error="handleAvatarError"
              class="profile-avatar"
            />
            <div class="avatar-overlay" @click="uploadAvatar">
              <i class="upload-icon">📷</i>
              <span>更换头像</span>
            </div>
          </div>
          <div class="avatar-info">
            <h3>{{ user?.nickname || user?.username || '用户名' }}</h3>
            <p>{{ user?.email || '邮箱' }}</p>
            <p class="join-date">加入时间：{{ formatDate(user?.createdAt) }}</p>
          </div>
        </div>

        <!-- 统计信息 -->
        <div class="stats-section">
          <h4>学习统计</h4>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-number">{{ stats.totalWords || 0 }}</div>
              <div class="stat-label">已学单词</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.correctAnswers || 0 }}</div>
              <div class="stat-label">正确答题</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.studyDays || 0 }}</div>
              <div class="stat-label">学习天数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ stats.currentStreak || 0 }}</div>
              <div class="stat-label">连续学习</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：详细信息表单 -->
      <div class="profile-right">
        <div class="form-section">
          <h4>基本信息</h4>
          <form @submit.prevent="updateProfile" class="profile-form">
            <div class="form-group">
              <label for="username">用户名</label>
              <div class="readonly-field">
                {{ user?.username || '未知' }}
                <span class="readonly-note">（用户名不可修改）</span>
              </div>
            </div>

            <div class="form-group">
              <label for="email">邮箱</label>
              <input 
                type="email" 
                id="email"
                v-model="formData.email"
                :disabled="!isEditing"
                class="form-input"
              />
            </div>

            <div class="form-group">
              <label for="nickname">昵称</label>
              <input 
                type="text" 
                id="nickname"
                v-model="formData.nickname"
                :disabled="!isEditing"
                class="form-input"
                placeholder="请输入昵称"
              />
            </div>

            <div class="form-group">
              <label for="status">账户状态</label>
              <div class="status-display">
                <span :class="['status-badge', user?.status?.toLowerCase()]">
                  {{ getStatusText(user?.status) }}
                </span>
              </div>
            </div>

            <div class="form-actions">
              <button 
                v-if="!isEditing" 
                type="button" 
                @click="startEditing"
                class="btn btn-primary"
              >
                编辑资料
              </button>
              <template v-else>
                <button 
                  type="submit" 
                  class="btn btn-success"
                  :disabled="isUpdating"
                >
                  {{ isUpdating ? '保存中...' : '保存更改' }}
                </button>
                <button 
                  type="button" 
                  @click="cancelEditing"
                  class="btn btn-secondary"
                >
                  取消
                </button>
              </template>
            </div>
          </form>
        </div>

        <!-- 密码修改 -->
        <div class="form-section">
          <h4>修改密码</h4>
          <form @submit.prevent="changePassword" class="password-form">
            <div class="form-group">
              <label for="currentPassword">当前密码</label>
              <input 
                type="password" 
                id="currentPassword"
                v-model="passwordData.currentPassword"
                class="form-input"
                placeholder="请输入当前密码"
              />
            </div>

            <div class="form-group">
              <label for="newPassword">新密码</label>
              <input 
                type="password" 
                id="newPassword"
                v-model="passwordData.newPassword"
                class="form-input"
                placeholder="请输入新密码"
              />
            </div>

            <div class="form-group">
              <label for="confirmPassword">确认新密码</label>
              <input 
                type="password" 
                id="confirmPassword"
                v-model="passwordData.confirmPassword"
                class="form-input"
                placeholder="请再次输入新密码"
              />
            </div>

            <div class="form-actions">
              <button 
                type="submit" 
                class="btn btn-warning"
                :disabled="isChangingPassword"
              >
                {{ isChangingPassword ? '修改中...' : '修改密码' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 隐藏的文件输入 -->
    <input 
      type="file" 
      ref="avatarInput"
      @change="handleAvatarUpload"
      accept="image/*"
      style="display: none"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import api from '@/utils/axios'

const authStore = useAuthStore()
const router = useRouter()

// 响应式数据
const user = computed(() => authStore.currentUser)
const isEditing = ref(false)
const isUpdating = ref(false)
const isChangingPassword = ref(false)

// 表单数据
const formData = reactive({
  email: '',
  nickname: ''
})

// 密码修改数据
const passwordData = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 统计信息
const stats = reactive({
  totalWords: 0,
  correctAnswers: 0,
  studyDays: 0,
  currentStreak: 0
})

// 文件输入引用
const avatarInput = ref(null)

// 获取用户头像
const getUserAvatar = () => {
  if (!user.value) return getDefaultAvatar('User')
  
  if (user.value.avatar && user.value.avatar.trim()) {
    if (user.value.avatar.startsWith('http://') || user.value.avatar.startsWith('https://')) {
      return user.value.avatar
    }
    
    if (user.value.avatar.startsWith('avatar_')) {
      return `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/uploads/avatars/${user.value.avatar}`
    }
    
    if (user.value.avatar.startsWith('/')) {
      return `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}${user.value.avatar}`
    }
    
    return `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/uploads/avatars/${user.value.avatar}`
  }
  
  return getDefaultAvatar(user.value.username || 'User')
}

// 生成默认头像
const getDefaultAvatar = (username) => {
  const firstLetter = username.charAt(0).toUpperCase()
  const colors = [
    '#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7',
    '#DDA0DD', '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E9'
  ]
  const colorIndex = username.charCodeAt(0) % colors.length
  const backgroundColor = colors[colorIndex]
  
  const svg = `
    <svg width="120" height="120" viewBox="0 0 120 120" xmlns="http://www.w3.org/2000/svg">
      <rect width="120" height="120" fill="${backgroundColor}" rx="60"/>
      <text x="60" y="70" font-family="Arial, sans-serif" font-size="48" font-weight="bold" text-anchor="middle" fill="white">${firstLetter}</text>
    </svg>
  `
  
  return `data:image/svg+xml;base64,${btoa(svg)}`
}

// 头像加载失败处理
const handleAvatarError = (event) => {
  if (user.value) {
    event.target.src = getDefaultAvatar(user.value.username || 'User')
  }
}

// 上传头像
const uploadAvatar = () => {
  avatarInput.value?.click()
}

// 处理头像上传
const handleAvatarUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件（支持格式：PNG、JPG、JPEG、GIF、BMP、WEBP）')
    return
  }
  
  if (file.size > 2 * 1024 * 1024) {
    alert('图片大小不能超过2MB')
    return
  }
  
  try {
    const base64Data = await fileToBase64(file)
    const fileExtension = file.name.split('.').pop().toLowerCase()
    const timestamp = Date.now()
    const newFileName = `avatar_${user.value.id}_${timestamp}.${fileExtension}`
    
    const uploadResponse = await api.post('/api/users/upload-avatar', {
      userId: user.value.id,
      fileName: newFileName,
      fileData: base64Data,
      fileType: file.type,
      fileSize: file.size
    })
    
    if (uploadResponse.data.status === 'success') {
      authStore.currentUser.avatar = uploadResponse.data.avatarPath
      // 更新localStorage中的用户信息
      authStore.setUser(authStore.currentUser)
      alert('头像上传成功！')
    } else {
      alert('头像上传失败: ' + uploadResponse.data.message)
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    
    // 尝试从错误响应中获取后端返回的错误信息
    let errorMessage = '头像上传失败，请稍后重试'
    if (error.response && error.response.data && error.response.data.message) {
      errorMessage = error.response.data.message
    } else if (error.message) {
      errorMessage = error.message
    }
    
    alert(errorMessage)
  }
  
  event.target.value = ''
}

// 文件转Base64
const fileToBase64 = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => {
      const base64 = reader.result.split(',')[1]
      resolve(base64)
    }
    reader.onerror = reject
    reader.readAsDataURL(file)
  })
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'ACTIVE': '正常',
    'INACTIVE': '未激活',
    'SUSPENDED': '已暂停',
    'DELETED': '已删除'
  }
  return statusMap[status] || '未知'
}

// 开始编辑
const startEditing = () => {
  isEditing.value = true
  formData.email = user.value.email || ''
  formData.nickname = user.value.nickname || ''
}

// 取消编辑
const cancelEditing = () => {
  isEditing.value = false
  // 恢复到当前用户的数据
  formData.email = user.value.email || ''
  formData.nickname = user.value.nickname || ''
}

// 更新资料
const updateProfile = async () => {
  if (!formData.email.trim()) {
    alert('邮箱不能为空')
    return
  }
  
  isUpdating.value = true
  
  try {
    const response = await api.put(`/api/users/${user.value.id}`, {
      email: formData.email,
      nickname: formData.nickname
    })
    
    if (response.data.status === 'success') {
      // 更新本地用户信息
      authStore.currentUser.email = formData.email
      authStore.currentUser.nickname = formData.nickname
      // 更新localStorage中的用户信息
      authStore.setUser(authStore.currentUser)
      
      isEditing.value = false
      alert('资料更新成功！')
    } else {
      alert('资料更新失败: ' + response.data.message)
    }
  } catch (error) {
    console.error('资料更新失败:', error)
    
    // 尝试从错误响应中获取后端返回的错误信息
    let errorMessage = '资料更新失败，请稍后重试'
    if (error.response && error.response.data && error.response.data.message) {
      errorMessage = error.response.data.message
    } else if (error.message) {
      errorMessage = error.message
    }
    
    alert(errorMessage)
  } finally {
    isUpdating.value = false
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordData.currentPassword.trim()) {
    alert('请输入当前密码')
    return
  }
  
  if (!passwordData.newPassword.trim()) {
    alert('请输入新密码')
    return
  }
  
  if (passwordData.newPassword.length < 6) {
    alert('新密码长度不能少于6位')
    return
  }
  
  if (passwordData.newPassword !== passwordData.confirmPassword) {
    alert('两次输入的新密码不一致')
    return
  }
  
  isChangingPassword.value = true
  
  try {
    const response = await api.put(`/api/users/${user.value.id}/password`, {
      currentPassword: passwordData.currentPassword,
      newPassword: passwordData.newPassword
    })
    
    if (response.data.status === 'success') {
      alert('密码修改成功！')
      // 清空密码表单
      passwordData.currentPassword = ''
      passwordData.newPassword = ''
      passwordData.confirmPassword = ''
    } else {
      alert('密码修改失败: ' + response.data.message)
    }
  } catch (error) {
    console.error('密码修改失败:', error)
    
    // 尝试从错误响应中获取后端返回的错误信息
    let errorMessage = '密码修改失败，请稍后重试'
    if (error.response && error.response.data && error.response.data.message) {
      errorMessage = error.response.data.message
    } else if (error.message) {
      errorMessage = error.message
    }
    
    alert(errorMessage)
  } finally {
    isChangingPassword.value = false
  }
}

// 加载用户统计信息
const loadUserStats = async () => {
  try {
    const response = await api.get(`/api/users/${user.value.id}/stats`)
    if (response.data.status === 'success') {
      Object.assign(stats, response.data.data)
    }
  } catch (error) {
    console.error('加载统计信息失败:', error)
    // 如果API不存在或失败，保持默认值0
  }
}

// 组件挂载时加载数据
onMounted(() => {
  if (!user.value) {
    router.push('/login')
    return
  }
  
  // 初始化表单数据
  formData.email = user.value.email || ''
  formData.nickname = user.value.nickname || ''
  
  loadUserStats()
})
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 2rem;
}

.page-header {
  text-align: center;
  margin-bottom: 3rem;
  color: white;
}

.page-header h1 {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
  font-weight: 700;
}

.page-header p {
  font-size: 1.1rem;
  opacity: 0.9;
}

.profile-content {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.profile-left {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.avatar-section {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.avatar-container {
  position: relative;
  display: inline-block;
  margin-bottom: 1.5rem;
}

.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #f0f0f0;
  transition: all 0.3s ease;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  cursor: pointer;
  color: white;
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.upload-icon {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.avatar-info h3 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  color: #333;
}

.avatar-info p {
  color: #666;
  margin-bottom: 0.25rem;
}

.join-date {
  font-size: 0.9rem;
  color: #999;
}

.stats-section {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.stats-section h4 {
  font-size: 1.3rem;
  margin-bottom: 1.5rem;
  color: #333;
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.stat-item {
  text-align: center;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 12px;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 0.9rem;
  color: #666;
}

.profile-right {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.form-section {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.form-section h4 {
  font-size: 1.3rem;
  margin-bottom: 1.5rem;
  color: #333;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 0.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #333;
}

.form-input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.form-input:disabled {
  background-color: #f5f5f5;
  color: #666;
}

.readonly-field {
  padding: 0.75rem 1rem;
  background-color: #f8f9fa;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 1rem;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.readonly-note {
  font-size: 0.8rem;
  color: #999;
  font-style: italic;
}

.status-display {
  padding: 0.75rem 1rem;
  background: #f8f9fa;
  border-radius: 10px;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 600;
}

.status-badge.active {
  background: #d4edda;
  color: #155724;
}

.status-badge.inactive {
  background: #fff3cd;
  color: #856404;
}

.status-badge.suspended {
  background: #f8d7da;
  color: #721c24;
}

.status-badge.deleted {
  background: #d1ecf1;
  color: #0c5460;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-block;
  text-align: center;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-success {
  background: linear-gradient(135deg, #56ab2f 0%, #a8e6cf 100%);
  color: white;
}

.btn-success:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(86, 171, 47, 0.4);
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background: #5a6268;
  transform: translateY(-2px);
}

.btn-warning {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.btn-warning:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(240, 147, 251, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-content {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .profile-container {
    padding: 1rem;
  }
}
</style>
