<template>
  <div class="textbook-selector">
    <div class="selector-content">
      <!-- 头部装饰 -->
      <div class="header-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
      </div>
      
      <div class="selector-header">
        <div class="header-icon">📚</div>
        <h2>选择教材</h2>
        <p class="header-subtitle">选择适合你的学习教材</p>
        <button @click="$emit('close')" class="close-btn">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
        </button>
      </div>
      
      <!-- 年级选择 -->
      <div class="grade-section">
        <div class="section-title">
          <div class="title-icon">🎓</div>
          <h3>选择年级</h3>
        </div>
        <div class="grade-selector">
          <select v-model="selectedGradeId" @change="handleGradeChange" class="grade-select">
            <option value="">全部年级</option>
            <option v-for="grade in grades" :key="grade.id" :value="grade.id">
              {{ grade.gradeName }}
            </option>
          </select>
        </div>
      </div>
      
      <!-- 教材选择 -->
      <div class="textbook-section">
        <div class="section-title">
          <div class="title-icon">📖</div>
          <h3>选择教材</h3>
        </div>
        <div class="textbook-list">
          <div
            v-for="textbook in textbooks"
            :key="textbook.id"
            @click="selectTextbook(textbook)"
            :class="['textbook-card', { active: selectedTextbook?.id === textbook.id }]"
          >
            <div class="card-left">
              <div class="card-icon">📕</div>
            </div>
            <div class="card-content">
              <h4>{{ textbook.name }}</h4>
              <p class="card-grade">{{ textbook.grade }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 确认选择 -->
      <div class="confirm-section">
        <div v-if="selectedTextbook" class="selected-info">
          <div class="selected-icon">✅</div>
          <div class="selected-content">
            <p class="selected-label">已选择教材</p>
            <p class="selected-text">{{ selectedTextbook.grade }} - {{ selectedTextbook.name }}</p>
          </div>
        </div>
        <div v-else class="no-selection-info">
          <div class="no-selection-icon">👆</div>
          <p>请选择一本教材开始学习</p>
        </div>
        
        <div class="button-group">
          <button @click="$emit('close')" class="cancel-btn">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
            取消
          </button>
          <button 
            @click="confirmSelection" 
            class="confirm-btn"
            :disabled="!selectedTextbook"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="20,6 9,17 4,12"></polyline>
            </svg>
            确认选择
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import api from '@/utils/axios'

export default {
  name: 'TextbookSelector',
  emits: ['close', 'textbook-selected'],
  setup(props, { emit }) {
    // 年级列表（写死）
    const grades = ref([
      { id: '一年级', gradeName: '一年级' },
      { id: '二年级', gradeName: '二年级' },
      { id: '三年级', gradeName: '三年级' },
      { id: '四年级', gradeName: '四年级' },
      { id: '五年级', gradeName: '五年级' },
      { id: '六年级', gradeName: '六年级' },
      { id: '七年级', gradeName: '七年级' },
      { id: '八年级', gradeName: '八年级' },
      { id: '九年级', gradeName: '九年级' },
      { id: '高中', gradeName: '高中' }
    ])
    const textbooks = ref([])
    const selectedGrade = ref(null)
    const selectedGradeId = ref('')
    const selectedTextbook = ref(null)
    const loading = ref(false)
    
    // 获取教材列表
    const fetchTextbooks = async (gradeId) => {
      try {
        loading.value = true
        let response
        if (gradeId && gradeId !== '') {
          // 获取指定年级的教材
          response = await api.get(`/api/textbooks/grade/${encodeURIComponent(gradeId)}`)
        } else {
          // 获取所有教材
          response = await api.get('/api/textbooks/all')
        }
        textbooks.value = response.data
      } catch (error) {
        console.error('Failed to fetch textbooks:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 处理年级选择变化
    const handleGradeChange = () => {
      selectedTextbook.value = null
      if (selectedGradeId.value === '') {
        selectedGrade.value = null
      } else {
        const grade = grades.value.find(g => g.id === selectedGradeId.value)
        selectedGrade.value = grade
      }
      fetchTextbooks(selectedGradeId.value)
    }
    
    // 选择教材
    const selectTextbook = (textbook) => {
      selectedTextbook.value = textbook
    }
    
    // 确认选择
    const confirmSelection = () => {
      if (selectedTextbook.value) {
        // 使用教材本身的年级信息，而不是筛选的年级
        const grade = {
          id: selectedTextbook.value.grade,
          gradeName: selectedTextbook.value.grade
        }
        emit('textbook-selected', {
          grade: grade,
          textbook: selectedTextbook.value
        })
        emit('close')
      }
    }
    
    onMounted(() => {
      // 默认选择全部年级，获取所有教材
      fetchTextbooks('')
    })
    
    return {
      grades,
      textbooks,
      selectedGrade,
      selectedGradeId,
      selectedTextbook,
      loading,
      handleGradeChange,
      selectTextbook,
      confirmSelection
    }
  }
}
</script>

<style scoped>
.textbook-selector {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.selector-content {
  background: linear-gradient(145deg, #ffffff 0%, #f8f9ff 100%);
  border-radius: 24px;
  max-width: 700px;
  width: 90%;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  animation: slideUp 0.4s ease-out;
}

@keyframes slideUp {
  from { 
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to { 
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 头部装饰 */
.header-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 120px;
  overflow: hidden;
  pointer-events: none;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.1;
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 80px;
  height: 80px;
  top: -20px;
  right: -20px;
  animation-delay: 0s;
}

.circle-2 {
  width: 60px;
  height: 60px;
  top: 20px;
  left: -30px;
  animation-delay: 2s;
}

.circle-3 {
  width: 40px;
  height: 40px;
  top: 60px;
  right: 60px;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

/* 头部样式 */
.selector-header {
  position: relative;
  text-align: center;
  padding: 20px 30px 15px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  flex-shrink: 0;
}

.header-icon {
  font-size: 32px;
  margin-bottom: 8px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-10px); }
  60% { transform: translateY(-5px); }
}

.selector-header h2 {
  margin: 0 0 4px 0;
  font-size: 22px;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-subtitle {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
  font-weight: 400;
}

.close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  cursor: pointer;
  padding: 12px;
  border-radius: 50%;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

/* 年级选择区域 */
.grade-section {
  padding: 20px 30px;
  background: white;
  flex-shrink: 0;
}

.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.title-icon {
  font-size: 20px;
  margin-right: 8px;
}

.section-title h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.grade-select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e1e5e9;
  border-radius: 12px;
  background: white;
  color: #333;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.grade-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

/* 教材选择区域 */
.textbook-section {
  flex: 1;
  padding: 20px 30px;
  overflow-y: auto;
  background: #fafbff;
}

.textbook-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.textbook-card {
  background: white;
  border: 2px solid #e1e5e9;
  border-radius: 16px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  gap: 16px;
}

.textbook-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.textbook-card:hover {
  border-color: #667eea;
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.textbook-card:hover::before {
  transform: scaleX(1);
}

.textbook-card.active {
  border-color: #667eea;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.textbook-card.active::before {
  transform: scaleX(1);
  background: rgba(255, 255, 255, 0.3);
}

.card-left {
  flex-shrink: 0;
}

.card-icon {
  font-size: 28px;
  display: block;
}

.card-content {
  flex: 1;
  min-width: 0;
}

.card-content h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: inherit;
  line-height: 1.4;
  height: 2.8em; /* 固定为两行文字的高度 */
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-grade {
  margin: 0;
  font-size: 13px;
  opacity: 0.7;
  color: inherit;
}


/* 确认区域 */
.confirm-section {
  padding: 20px 30px;
  background: white;
  flex-shrink: 0;
  border-top: 1px solid #f0f0f0;
}

.selected-info,
.no-selection-info {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
  padding: 20px;
  border-radius: 16px;
  text-align: center;
}

.selected-info {
  background: linear-gradient(135deg, #e8f5e8 0%, #f0f8f0 100%);
  border: 2px solid #4caf50;
}

.no-selection-info {
  background: linear-gradient(135deg, #fff3e0 0%, #fef7e0 100%);
  border: 2px solid #ff9800;
}

.selected-icon,
.no-selection-icon {
  font-size: 24px;
  margin-right: 12px;
}

.selected-content p {
  margin: 4px 0;
}

.selected-label {
  font-weight: 600;
  color: #2e7d32;
  font-size: 16px;
}

.selected-text {
  color: #388e3c;
  font-size: 14px;
}

.no-selection-info p {
  margin: 0;
  color: #f57c00;
  font-weight: 500;
}

.button-group {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.cancel-btn,
.confirm-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 32px;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 140px;
  justify-content: center;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
  border: 2px solid #e0e0e0;
}

.cancel-btn:hover {
  background: #eeeeee;
  color: #333;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.confirm-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.confirm-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.confirm-btn:disabled {
  background: #e0e0e0;
  color: #999;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .selector-content {
    width: 95%;
    max-height: 90vh;
  }
  
  .textbook-list {
    grid-template-columns: 1fr;
  }
  
  .button-group {
    flex-direction: column;
    gap: 12px;
  }
  
  .cancel-btn,
  .confirm-btn {
    width: 100%;
  }
}
</style>
